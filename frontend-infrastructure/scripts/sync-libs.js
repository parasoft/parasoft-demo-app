const { readFile, writeFile, mkdir } = require('node:fs/promises');
const path = require('node:path');

const infrastructure = path.resolve(__dirname, '..');
const defaultTarget = path.resolve(infrastructure, '../src/main/resources/static/lib');
const textExtensions = new Set(['.js', '.css', '.map', '.svg', '.txt']);

function normalize(data, filename) {
  // Git's autocrlf changes checkout text on Windows. Fonts and images must
  // always retain their exact bytes, even when they contain CR/LF sequences.
  // npm packages also ship text license notices without a filename extension.
  return (textExtensions.has(path.extname(filename)) || /^LICEN[CS]E$/i.test(path.basename(filename)))
    ? Buffer.from(data.toString('utf8').replace(/\r\n/g, '\n'))
    : data;
}

function inside(root, relative) {
  if (typeof relative !== 'string' || !relative || path.isAbsolute(relative)) {
    throw new Error(`Invalid relative asset path: ${relative}`);
  }
  const resolved = path.resolve(root, relative);
  const fromRoot = path.relative(root, resolved);
  if (!fromRoot || fromRoot === '..' || fromRoot.startsWith(`..${path.sep}`) || path.isAbsolute(fromRoot)) {
    throw new Error(`Asset path escapes its managed directory: ${relative}`);
  }
  return resolved;
}

async function optionalFile(filename) {
  try {
    return await readFile(filename);
  } catch (error) {
    if (error.code === 'ENOENT') return null;
    throw error;
  }
}

async function requiredFile(filename) {
  const data = await optionalFile(filename);
  if (data === null) {
    throw new Error(`Missing source file: ${filename}. Run npm ci --ignore-scripts in frontend-infrastructure.`);
  }
  return data;
}

async function syncLibraries({
  infrastructureDir = infrastructure,
  targetDir = defaultTarget,
  manifest,
  mapping,
  check = false,
  log = console.log
} = {}) {
  manifest ??= JSON.parse(await requiredFile(path.join(infrastructureDir, 'package.json')));
  mapping ??= JSON.parse(await requiredFile(path.join(infrastructureDir, 'scripts/lib-files.json')));
  const prepared = [];
  const packages = new Set();
  const destinations = new Set();

  function prepare(destination, data, source) {
    const filename = inside(targetDir, destination);
    if (destinations.has(filename)) throw new Error(`Duplicate asset destination: ${destination}`);
    destinations.add(filename);
    // Published source maps may use extensionless aliases (Swiper's "0").
    // Classify text by the upstream filename so autocrlf still works for these aliases.
    prepared.push({ filename, destination, source, data: normalize(data, source) });
  }

  // Resolve every npm package source before touching the served libraries.
  // A late missing file must not leave a partly synchronized tree.
  for (const entry of mapping) {
    const packageDir = inside(path.join(infrastructureDir, 'node_modules'), entry.package);
    if (!packages.has(entry.package)) {
      const expected = manifest.dependencies?.[entry.package];
      if (!expected || !/^\d+\.\d+\.\d+(?:-[\w.-]+)?$/.test(expected)) {
        throw new Error(`Missing exact dependency version for ${entry.package}.`);
      }
      const installed = JSON.parse(await requiredFile(path.join(packageDir, 'package.json')));
      if (installed.version !== expected) {
        throw new Error(`Package ${entry.package}: expected ${expected}, installed ${installed.version}. Run npm ci --ignore-scripts.`);
      }
      packages.add(entry.package);
    }
    const data = await requiredFile(inside(packageDir, entry.source));
    prepare(entry.destination, data, entry.source);
  }

  const changed = [];
  for (const entry of prepared) {
    const current = await optionalFile(entry.filename);
    if (current === null || !normalize(current, entry.source).equals(entry.data)) changed.push(entry);
  }
  if (check && changed.length) {
    throw new Error(`Library drift in ${changed.length} file(s):\n${changed.map(entry => `  ${entry.destination}`).join('\n')}\nRun npm run sync-libs in frontend-infrastructure.`);
  }
  if (!check) {
    for (const entry of changed) {
      await mkdir(path.dirname(entry.filename), { recursive: true });
      await writeFile(entry.filename, entry.data);
    }
    for (const entry of changed) log(`Updated ${entry.destination}`);
  }
  log(`${check ? 'Checked' : 'Synchronized'} ${prepared.length} library files; ${changed.length} changed.`);
  return { total: prepared.length, changed: changed.length };
}

module.exports = { syncLibraries };

if (require.main === module) {
  const args = process.argv.slice(2);
  if (args.some(arg => arg !== '--check')) {
    console.error(`Unknown option: ${args.find(arg => arg !== '--check')}. Usage: node scripts/sync-libs.js [--check]`);
    process.exitCode = 1;
  } else {
    syncLibraries({ check: args.includes('--check') }).catch(error => {
      console.error(`Library synchronization failed: ${error.message}`);
      process.exitCode = 1;
    });
  }
}
