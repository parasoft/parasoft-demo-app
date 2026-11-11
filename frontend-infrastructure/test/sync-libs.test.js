const assert = require('node:assert/strict');
const { createHash } = require('node:crypto');
const { mkdtemp, mkdir, readFile, writeFile, readdir, rm, stat } = require('node:fs/promises');
const { tmpdir } = require('node:os');
const path = require('node:path');
const { spawnSync } = require('node:child_process');
const vm = require('node:vm');
const { test } = require('node:test');

const script = path.resolve(__dirname, '../scripts/sync-libs.js');
const digest = data => createHash('sha256').update(data).digest('hex');

async function fixture(t) {
  const root = await mkdtemp(path.join(tmpdir(), 'pda-libs-'));
  t.after(() => rm(root, { recursive: true, force: true }));
  const infrastructureDir = path.join(root, 'infrastructure');
  const targetDir = path.join(root, 'static', 'lib');
  const packageDir = path.join(infrastructureDir, 'node_modules', 'example');
  await mkdir(packageDir, { recursive: true });
  await mkdir(targetDir, { recursive: true });
  await writeFile(path.join(packageDir, 'package.json'), '{"version":"1.2.3"}');
  await writeFile(path.join(packageDir, 'example.js'), 'window.example = true;\n');
  const manifest = { dependencies: { example: '1.2.3' }, upstreamAssets: [] };
  const mapping = [{ package: 'example', source: 'example.js', destination: 'example.js' }];
  const options = { infrastructureDir, targetDir, manifest, mapping, log() {} };
  return { ...options, options, packageDir };
}

async function synchronize(options) {
  const { syncLibraries } = require(script);
  return syncLibraries(options);
}

test('copies mapped files and leaves application-owned assets untouched', async t => {
  const f = await fixture(t);
  await writeFile(path.join(f.targetDir, 'swagger-bg.svg'), '<svg/>');
  const result = await synchronize(f.options);
  assert.equal(await readFile(path.join(f.targetDir, 'example.js'), 'utf8'), 'window.example = true;\n');
  assert.equal(await readFile(path.join(f.targetDir, 'swagger-bg.svg'), 'utf8'), '<svg/>');
  assert.equal(result.changed, 1);
  const first = await stat(path.join(f.targetDir, 'example.js'));
  assert.equal((await synchronize(f.options)).changed, 0);
  assert.equal((await stat(path.join(f.targetDir, 'example.js'))).mtimeMs, first.mtimeMs);
});

test('check detects missing and modified files without writing', async t => {
  const f = await fixture(t);
  await assert.rejects(synchronize({ ...f.options, check: true }), /drift.*example.js/is);
  assert.deepEqual(await readdir(f.targetDir), []);
  await writeFile(path.join(f.targetDir, 'example.js'), 'changed');
  await assert.rejects(synchronize({ ...f.options, check: true }), /drift.*example.js/is);
  assert.equal(await readFile(path.join(f.targetDir, 'example.js'), 'utf8'), 'changed');
});

test('normalizes text line endings without rewriting equivalent checkout files', async t => {
  const f = await fixture(t);
  await writeFile(path.join(f.targetDir, 'example.js'), 'window.example = true;\r\n');
  assert.equal((await synchronize({ ...f.options, check: true })).changed, 0);
  assert.equal(await readFile(path.join(f.targetDir, 'example.js'), 'utf8'), 'window.example = true;\r\n');
});

test('preflights every package source before writing any target', async t => {
  const f = await fixture(t);
  f.mapping.push({ package: 'example', source: 'missing.css', destination: 'missing.css' });
  await assert.rejects(synchronize(f.options), /missing.css/);
  assert.deepEqual(await readdir(f.targetDir), []);
});

test('rejects an installed package with a different version', async t => {
  const f = await fixture(t);
  await writeFile(path.join(f.packageDir, 'package.json'), '{"version":"9.9.9"}');
  await assert.rejects(synchronize(f.options), /example.*1.2.3.*9.9.9/s);
  assert.deepEqual(await readdir(f.targetDir), []);
});

function upstream(f) {
  // Literal bytes include CR/LF to catch accidental text normalization of fonts.
  const bytes = Buffer.from([0, 13, 10, 255, 17]);
  f.manifest.upstreamAssets.push({
    name: 'Example font', version: '1.000', license: 'OFL-1.1',
    url: 'https://example.invalid/immutable/font.ttf',
    sha256: digest(bytes), destination: 'fonts/font.ttf'
  });
  return bytes;
}

test('downloads verified binary bytes and reuses the verified cache offline', async t => {
  const f = await fixture(t);
  const bytes = upstream(f);
  await synchronize({ ...f.options, fetcher: async () => new Response(bytes) });
  assert.deepEqual(await readFile(path.join(f.targetDir, 'fonts/font.ttf')), bytes);
  assert.equal((await synchronize({ ...f.options, check: true, fetcher: async () => {
    throw new Error('network must not be used for cached artifacts');
  } })).changed, 0);
  await writeFile(path.join(f.targetDir, 'fonts/font.ttf'), Buffer.from([0, 10, 255, 17]));
  await assert.rejects(synchronize({ ...f.options, check: true }), /drift.*font.ttf/is);
});

test('rejects unverified downloads before any target writes', async t => {
  const f = await fixture(t);
  upstream(f);
  await assert.rejects(synchronize({ ...f.options, fetcher: async () => new Response('wrong') }), /SHA-256.*Example font/is);
  assert.deepEqual(await readdir(f.targetDir), []);
});

test('reports failed upstream HTTP requests before target writes', async t => {
  const f = await fixture(t);
  upstream(f);
  await assert.rejects(synchronize({ ...f.options, fetcher: async () => new Response('missing', { status: 404 }) }), /404.*Example font/is);
  assert.deepEqual(await readdir(f.targetDir), []);
});

test('check neither creates a download cache nor replaces a corrupt cache', async t => {
  const f = await fixture(t);
  const bytes = upstream(f);
  await writeFile(path.join(f.targetDir, 'example.js'), 'window.example = true;\n');
  await mkdir(path.join(f.targetDir, 'fonts'));
  await writeFile(path.join(f.targetDir, 'fonts/font.ttf'), bytes);
  const cacheDir = path.join(f.infrastructureDir, '.cache');
  await synchronize({ ...f.options, check: true, fetcher: async () => new Response(bytes) });
  await assert.rejects(stat(cacheDir), { code: 'ENOENT' });
  await synchronize({ ...f.options, fetcher: async () => new Response(bytes) });
  const cached = (await readdir(cacheDir))[0];
  await writeFile(path.join(cacheDir, cached), 'corrupt');
  await assert.rejects(synchronize({ ...f.options, check: true }), /SHA-256.*Example font/is);
  assert.equal(await readFile(path.join(cacheDir, cached), 'utf8'), 'corrupt');
});

test('rejects mappings that escape the managed directories', async t => {
  const f = await fixture(t);
  f.mapping[0].destination = '../outside.js';
  await assert.rejects(synchronize(f.options), /path|outside|escape/i);
  assert.deepEqual(await readdir(f.targetDir), []);
});

test('applies a preserved override only to the original upstream content', async t => {
  const f = await fixture(t);
  await mkdir(path.join(f.infrastructureDir, 'preserved'));
  await writeFile(path.join(f.infrastructureDir, 'preserved/example.js.json'), JSON.stringify('window.example =\n true;\n'));
  f.mapping[0].preservedOverride = { file: 'preserved/example.js.json', upstreamSha256: digest('window.example = true;\n') };
  await synchronize(f.options);
  assert.equal(await readFile(path.join(f.targetDir, 'example.js'), 'utf8'), 'window.example =\n true;\n');
  await writeFile(path.join(f.packageDir, 'example.js'), 'window.example = false;\n');
  await assert.rejects(synchronize(f.options), /override|SHA-256/i);
  assert.equal(await readFile(path.join(f.targetDir, 'example.js'), 'utf8'), 'window.example =\n true;\n');
});

test('CLI rejects unrecognized options', () => {
  const result = spawnSync(process.execPath, [script, '--unknown'], { encoding: 'utf8' });
  assert.equal(result.status, 1);
  assert.match(result.stderr, /unknown.*--unknown/i);
});

test('preserved URL loader unwraps the application translation response', async () => {
  const code = JSON.parse(await readFile(path.resolve(__dirname, '../preserved/angular-translate-loader-url.min.js.json'), 'utf8'));
  let factory;
  vm.runInNewContext(code, {
    angular: {
      extend: Object.assign,
      module: () => ({ factory: (name, implementation) => { factory = implementation; } })
    },
    $: { parseJSON: JSON.parse }
  });
  const loader = factory({ reject: key => Promise.reject(key) }, async () => ({ data: { data: '{"WELCOME":"Welcome"}' } }));
  assert.deepEqual(await loader({ url: '/translations', key: 'en' }), { WELCOME: 'Welcome' });
});

test('every shipped library file has a source or is the application-owned Swagger background', async () => {
  const infrastructureDir = path.resolve(__dirname, '..');
  const manifest = JSON.parse(await readFile(path.join(infrastructureDir, 'package.json'), 'utf8'));
  const mapping = JSON.parse(await readFile(path.join(infrastructureDir, 'scripts/lib-files.json'), 'utf8'));
  const targetDir = path.resolve(infrastructureDir, '../src/main/resources/static/lib');
  async function files(directory, prefix = '') {
    const result = [];
    for (const entry of await readdir(directory, { withFileTypes: true })) {
      const relative = prefix + entry.name;
      if (entry.isDirectory()) result.push(...await files(path.join(directory, entry.name), `${relative}/`));
      else result.push(relative);
    }
    return result;
  }
  const destinations = [...mapping, ...manifest.upstreamAssets].map(entry => entry.destination);
  assert.equal(new Set(destinations).size, destinations.length, 'each destination must have one source');
  assert.deepEqual((await files(targetDir)).sort(), [...destinations, 'swaggerui/images/swagger-bg.svg'].sort());
});
