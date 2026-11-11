const assert = require('node:assert/strict');
const { readFile, readdir, stat } = require('node:fs/promises');
const path = require('node:path');
const { test } = require('node:test');

const staticDir = path.resolve(__dirname, '../../src/main/resources/static');
const libraryDir = path.join(staticDir, 'lib');

async function files(directory) {
  const result = [];
  for (const entry of await readdir(directory, { withFileTypes: true })) {
    const file = path.join(directory, entry.name);
    if (entry.isDirectory()) result.push(...await files(file));
    else result.push(file);
  }
  return result;
}

function localReference(source, reference, sourceRoot = '') {
  reference = reference.trim().replace(/^['"]|['"]$/g, '');
  if (!reference || reference.startsWith('#') || reference.startsWith('data:')) return null;
  const base = new URL(path.relative(staticDir, source).split(path.sep).join('/'), 'https://resources.invalid/');
  const root = sourceRoot ? new URL(`${sourceRoot.replace(/\/$/, '')}/`, base) : base;
  const url = new URL(reference, root);
  if (url.origin !== base.origin) return null;
  const file = path.resolve(staticDir, `.${decodeURIComponent(url.pathname)}`);
  assert.ok(file.startsWith(`${staticDir}${path.sep}`), `Reference escapes static resources: ${url}`);
  return file;
}

test('vendor CSS, source maps, and non-embedded sources resolve to shipped files', async () => {
  const shipped = await files(libraryDir);
  const textSources = new Set(shipped.filter(file => /\.(css|js)$/.test(file)));
  const missing = new Set();
  async function check(source, reference, sourceRoot) {
    const target = localReference(source, reference, sourceRoot);
    if (!target) return null;
    try {
      if ((await stat(target)).isFile()) return target;
    } catch (error) {
      if (error.code !== 'ENOENT' && error.code !== 'ENOTDIR') throw error;
    }
    missing.add(`${path.relative(staticDir, source)} -> ${path.relative(staticDir, target)}`);
  }
  for (const source of shipped) {
    if (source.endsWith('.map')) {
      const map = JSON.parse(await readFile(source, 'utf8'));
      for (const [index, original] of (map.sources || []).entries()) {
        // A map without embedded source text still causes a browser to request the original file.
        if (map.sourcesContent?.[index] == null) {
          const target = await check(source, original, map.sourceRoot);
          // Originals can have extensionless URLs and can reference another source map.
          if (target && /\.(css|js)$/.test(map.file || '')) textSources.add(target);
        }
      }
    }
  }
  for (const source of textSources) {
    const content = await readFile(source, 'utf8');
    if (source.endsWith('.css')) {
      for (const match of content.matchAll(/url\(\s*([^)]+?)\s*\)/g)) await check(source, match[1]);
    }
    for (const match of content.matchAll(/[#@]\s*sourceMappingURL\s*=\s*([^\s*]+)/g)) await check(source, match[1]);
  }
  assert.deepEqual([...missing].sort(), [], 'Referenced vendor resources must be packaged too');
});

test('the browser-default favicon uses the application branding', async () => {
  let favicon;
  await assert.doesNotReject(async () => {
    favicon = await readFile(path.join(staticDir, 'favicon.ico'));
  }, 'Browsers must find the default /favicon.ico resource');
  assert.deepEqual(favicon, await readFile(path.join(staticDir, 'common/images/favicon.ico')));
});
