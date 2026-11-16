const assert = require('node:assert/strict');
const { mkdtemp, mkdir, readFile, writeFile, readdir, rm, stat } = require('node:fs/promises');
const { tmpdir } = require('node:os');
const path = require('node:path');
const { spawnSync } = require('node:child_process');
const { test } = require('node:test');

const script = path.resolve(__dirname, '../scripts/sync-libs.js');

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
  const manifest = { dependencies: { example: '1.2.3' } };
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

test('recognizes text by its upstream filename when a source map uses an extensionless alias', async t => {
  const f = await fixture(t);
  f.mapping[0].destination = '0';
  await synchronize(f.options);
  await writeFile(path.join(f.targetDir, '0'), 'window.example = true;\r\n');
  assert.equal((await synchronize({ ...f.options, check: true })).changed, 0);
  assert.equal(await readFile(path.join(f.targetDir, '0'), 'utf8'), 'window.example = true;\r\n');
});

test('mapped npm fonts preserve binary CR/LF bytes and detect binary drift', async t => {
  const f = await fixture(t);
  const bytes = Buffer.from([119, 79, 70, 50, 0, 13, 10, 255]);
  await writeFile(path.join(f.packageDir, 'font.woff2'), bytes);
  f.mapping.push({ package: 'example', source: 'font.woff2', destination: 'fonts/font.woff2' });
  await synchronize(f.options);
  assert.deepEqual(await readFile(path.join(f.targetDir, 'fonts/font.woff2')), bytes);
  await writeFile(path.join(f.targetDir, 'fonts/font.woff2'), bytes.subarray(0, 6));
  await assert.rejects(synchronize({ ...f.options, check: true }), /drift.*font.woff2/is);
});

test('license notices tolerate checkout line endings including extensionless upstream LICENSE files', async t => {
  const f = await fixture(t);
  for (const source of ['LICENSE', 'LICENSE.txt']) {
    await writeFile(path.join(f.packageDir, source), 'Permission granted.\n');
    f.mapping.push({ package: 'example', source, destination: `notices/${source}.txt` });
  }
  await synchronize(f.options);
  for (const source of ['LICENSE', 'LICENSE.txt']) {
    await writeFile(path.join(f.targetDir, `notices/${source}.txt`), 'Permission granted.\r\n');
  }
  assert.equal((await synchronize({ ...f.options, check: true })).changed, 0);
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

test('rejects mappings that escape the managed directories', async t => {
  const f = await fixture(t);
  f.mapping[0].destination = '../outside.js';
  await assert.rejects(synchronize(f.options), /path|outside|escape/i);
  assert.deepEqual(await readdir(f.targetDir), []);
});

test('CLI rejects unrecognized options', () => {
  const result = spawnSync(process.execPath, [script, '--unknown'], { encoding: 'utf8' });
  assert.equal(result.status, 1);
  assert.match(result.stderr, /unknown.*--unknown/i);
});

test('every shipped library file has a source or is the application-owned Swagger background', async () => {
  const infrastructureDir = path.resolve(__dirname, '..');
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
  const destinations = mapping.map(entry => entry.destination);
  assert.equal(new Set(destinations).size, destinations.length, 'each destination must have one source');
  assert.deepEqual((await files(targetDir)).sort(), [...destinations, 'swaggerui/images/swagger-bg.svg'].sort());
});
