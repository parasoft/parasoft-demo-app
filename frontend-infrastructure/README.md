# Frontend dependency infrastructure

This directory records and reproduces the third-party browser files shipped in
`src/main/resources/static/lib/`. The application continues to use those checked-in
files. Node and npm are needed only to maintain them, not to build or run the Java
application.

## Setup and synchronization

Use Node.js 22 or later and npm. From the repository root:

```sh
cd frontend-infrastructure
npm ci --ignore-scripts
npm run check-libs
```

`npm ci` installs the exact dependency tree in `package-lock.json`.
`--ignore-scripts` skips dependency lifecycle scripts; this workflow only needs
published distribution files, including for packages with optional native helpers.
The npm scripts in this project are invoked explicitly.

To restore missing or changed library files:

```sh
npm run sync-libs
npm run check-libs
npm test
```

`check-libs` exits with a nonzero status and lists files that differ. It writes
neither the library tree nor the download cache. The first check may use the
network for source-pinned assets. `sync-libs` verifies every source before writing
the library tree and only writes files whose contents differ. It does not delete
files outside its explicit mapping. Text comparisons account for LF/CRLF checkout
line endings; fonts and images are compared byte-for-byte.

The commands resolve paths relative to the script. For example, this also works
from the repository root:

```sh
node frontend-infrastructure/scripts/sync-libs.js --check
```

## Versions and file mapping

All direct npm versions are exact in `package.json`; the lockfile pins the
transitive dependencies and npm distribution integrity hashes. The explicit source
and destination allowlist is `scripts/lib-files.json`. It covers 49 npm-backed
files; two more files are declared in `package.json` under `upstreamAssets`.

| Library | Package | Version |
|---|---|---|
| AngularJS | `angular` | 1.8.0 |
| AngularJS cookies | `angular-cookies` | 1.8.2 |
| Angular Translate | `angular-translate` | 2.18.3 |
| Angular Translate URL loader | `angular-translate-loader-url` | 2.18.3 |
| Bootstrap | `bootstrap` | 3.3.7 |
| Font Awesome | `font-awesome` | 4.7.0 |
| jQuery | `jquery` | 3.3.1 |
| Moment.js | `moment` | 2.24.0 |
| SockJS | `sockjs-client` | 1.5.0 |
| STOMP | `stomp-websocket` | 2.3.4-next |
| Swagger UI | `swagger-ui-dist` | 3.28.0 |
| Swiper | `swiper` | 5.4.5 |
| Toastr | `toastr` | 2.1.4 |

These versions intentionally reproduce the current application. This workflow
records dependencies; it does not upgrade them or resolve their existing
vulnerabilities. npm may report deprecations or vulnerabilities during installation.

### Special handling

- AngularJS core and cookies retain their existing different versions.
- jQuery's distribution file is copied to the existing `jquery-3.3.1.min.js` URL.
- Font Awesome's font files remain in `lib/fonts/`, matching its CSS references.
- STOMP's existing file comes from `stomp-websocket@2.3.4-next`; the similarly named
  `stompjs` package contains different code.
- Both URL-loader files come directly from npm. The application-owned `setLocale`
  configuration in `static/common/js/common.js` adapts `/localize` responses through
  the loader's `$http.transformResponse` option. It retains Angular's default JSON
  parsing and then uses `angular.fromJson(body.data)` to unwrap the serialized
  translation map. This transformation applies only to localization requests and
  preserves the backend response contract; no vendor override is needed.
- `swaggerui/images/swagger-bg.svg` is application branding rather than an upstream
  Swagger UI distribution file. Synchronization leaves it in place.

### Source-pinned assets

The original SerializeJSON version is not published on npm, and the historical
Orbitron font differs from modern npm font packages. Their original versions,
licenses, immutable URLs, destination paths, and SHA-256 values are recorded under
`upstreamAssets` in `package.json`:

| Asset | Version | Immutable upstream source | License |
|---|---|---|---|
| SerializeJSON | 2.3.1 | [jquery.serializeJSON at 8a92d110](https://github.com/marioizquierdo/jquery.serializeJSON/blob/8a92d110e087305b4ec30b0bfdd92c871efa31f9/jquery.serializejson.min.js) | MIT option from the original dual license |
| Orbitron Regular | 1.000 | [Google Fonts at 90abd17b](https://github.com/google/fonts/blob/90abd17b4f97671435798b6147b698aa9087612f/ofl/orbitron/Orbitron-Regular.ttf) | SIL Open Font License 1.1 |

Downloads use HTTPS, have a 30-second timeout, and must match the declared SHA-256
before any served file is written. A successful synchronization stores verified
downloads in the ignored `.cache/` directory, keyed by hash. Subsequent runs can
reuse them offline. A checksum mismatch fails the command; remove the affected
cache file and verify the declared source before retrying. Never replace a hash
merely to make an unexpected download pass.

## Upgrading a dependency

1. Choose and review the upstream release, license, security changes, and browser
   compatibility. Change the direct version in `package.json` to an exact version.
2. Regenerate the lockfile and install the new locked tree:

   ```sh
   npm install --package-lock-only --ignore-scripts
   npm ci --ignore-scripts
   ```

3. Review `scripts/lib-files.json` against the new distribution. Keep existing
   destination names and URLs unless an application change is explicitly intended.
   Include matching CSS, fonts, images, and source maps. For the URL loader, verify
   that its `$http` options continue to support the application's request-scoped
   translation-response transform. Keep the npm distribution files unmodified.
4. For a source-pinned asset, update its original version, full upstream commit
   URL, license reference, and SHA-256 together. Compute the checksum from the
   reviewed upstream bytes; for example, `Get-FileHash -Algorithm SHA256 <file>`
   in PowerShell or `sha256sum <file>` on Linux. npm's lockfile covers npm packages;
   these source pins are maintained explicitly in `upstreamAssets`.
5. Run `npm run sync-libs`, `npm run check-libs`, and `npm test`. Review the Git diff
   and exercise login, purchaser and approver pages, ordering, Demo Administration,
   translations, messaging, and Swagger UI. Update this version table.
6. Commit `package.json`, `package-lock.json`, mapping/formatting changes, this
   documentation, and the resulting files under `src/main/resources/static/lib/`.
   Keep `node_modules/` and `.cache/` untracked.

## Future SBOM and Dependency-Track integration

The existing shared Jenkins infrastructure used by JavaScript Coverage generates
Java and npm CycloneDX inventories separately, merges them, applies metadata
corrections, validates the result, and optionally uploads to Dependency-Track.
PDA can reuse that flow when CI integration is added.

The shared npm scanner can consume this `package.json` and lockfile. Source-pinned
downloads are not npm dependencies, so their components must be added explicitly
from `upstreamAssets`, using the shared processor's `sbom-injection-extra.json`
support or its custom scanner hook. Generate those records from this metadata so
versions and hashes have one source of truth. Record their actual upstream
identities rather than invented local npm package identities.

The URL loader can use its unmodified upstream npm identity. Its response adapter
is ordinary application code in `common.js`.

`sbom-updater` only enriches components that already exist. Its
`license-mapping.json` fills licenses; `sbom-injection.json` supplies missing
references of type `other` and uses exact PURL matches. Neither creates missing
components or changes their identifiers. Use exact mappings for special versions
such as STOMP's prerelease. Adding a source URL and hash establishes provenance;
vulnerability matching still requires identifiers recognized by the analyzers.

The extra-component merge copies only `components`. Use the custom scanner hook
or a normal input SBOM if dependency edges are required. When merging an inventory
of the built WAR with the frontend inventory, account for assets discovered in
both: the shared processor deduplicates by `bom-ref`, not package identity.

This directory does not generate or upload an SBOM. Pipeline configuration and
Dependency-Track integration remain separate work.
