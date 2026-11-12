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

`check-libs` exits with a nonzero status and lists files that differ without
writing any files. Both commands work offline after `npm ci` has installed the
locked packages. `sync-libs` validates every source before writing the library
tree and only writes files whose contents differ. It does not delete
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
and destination allowlist is `scripts/lib-files.json`. It covers 63 npm-backed
files, including the SerializeJSON and Orbitron license notices. All managed
third-party files come from npm packages.

| Library | Package | Version |
|---|---|---|
| AngularJS | `angular` | 1.8.0 |
| AngularJS cookies | `angular-cookies` | 1.8.2 |
| Angular Translate | `angular-translate` | 2.18.3 |
| Angular Translate URL loader | `angular-translate-loader-url` | 2.18.3 |
| Bootstrap | `bootstrap` | 3.3.7 |
| Font Awesome | `font-awesome` | 4.7.0 |
| jQuery | `jquery` | 3.3.1 |
| SerializeJSON | `jquery-serializejson` | 3.2.1 |
| Orbitron (400 and 700 normal) | `@fontsource/orbitron` | 5.3.0 |
| Moment.js | `moment` | 2.24.0 |
| SockJS | `sockjs-client` | 1.5.0 |
| STOMP | `stomp-websocket` | 2.3.4-next |
| Swagger UI | `swagger-ui-dist` | 3.28.0 |
| Swiper | `swiper` | 5.4.5 |
| Toastr | `toastr` | 2.1.4 |

These exact versions describe the application's shipped dependencies. This
workflow does not resolve all existing vulnerabilities. npm may report
deprecations or vulnerabilities during installation. `jsdom` is a pinned
development dependency used only for form tests.

### Special handling

- AngularJS core and cookies retain their existing different versions.
- jQuery's distribution file is copied to the existing `jquery-3.3.1.min.js` URL.
- Font Awesome's font files remain in `lib/fonts/`, matching its CSS references.
- SerializeJSON's npm distribution keeps the existing
  `jquery/jquery.serializejson.min.js` URL. Its MIT notice is shipped alongside it.
- Orbitron's regular 400 and bold 700 normal WOFF2 fonts, with WOFF fallbacks,
  come from the same `@fontsource/orbitron` release. Aerospace's application-owned
  CSS keeps the `Orbitron Regular` and `Orbitron Bold` aliases, but references only
  these local files. Local installed fonts are not selected ahead of the pinned files.
  `700` identifies bold; `normal` identifies the upright style. The OFL-1.1 notice
  is shipped as `fonts/Orbitron-LICENSE.txt`. The npm package version is distinct
  from the upstream font revision recorded in the package's metadata.
- Source maps are shipped with their distributions. Angular Cookies' map needs
  the original `angular-cookies.js` because it does not embed its source text.
- Swiper 5.4.5's minified map names its original source `0`. The upstream
  `js/swiper.js` is copied to `lib/swiper/0` to honor that URL without changing
  vendor contents. Its own `swiper.js.map` is included too. Text normalization
  uses the upstream filename, including for this extensionless alias.
- STOMP's existing file comes from `stomp-websocket@2.3.4-next`; the similarly named
  `stompjs` package contains different code.
- Both URL-loader files come directly from npm. The application-owned `setLocale`
  configuration in `static/common/js/common.js` adapts `/localize` responses through
  the loader's `$http.transformResponse` option. It retains Angular's default JSON
  parsing and then uses `angular.fromJson(body.data)` to unwrap the serialized
  translation map. This transformation applies only to localization requests and
  preserves the backend response contract; no vendor override is needed.
- `swaggerui/images/swagger-bg.svg` is a project-maintained, edited copy of the
  Swagger logo used by `common/css/pda-swagger-ui.css`. It is outside the npm file
  mapping, so synchronization leaves the existing logo in place.
- `static/favicon.ico` is an application-owned copy of
  `static/common/images/favicon.ico` for browsers that request the default icon
  URL. Keep both copies identical when updating the branding.

`npm test` checks local references in vendor CSS and source maps, including
original sources that are not embedded in a map and their own map references.
It also checks the default favicon against the application icon. These checks
complement dependency integrity checks; they do not replace browser checks of
application behavior, console errors, and resource delivery after an upgrade.
Form tests use the shipped jQuery and SerializeJSON in jsdom to check nested
labels, Chinese text, empty values, checkbox arrays, and approval responses.

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
4. Keep the bundled license notices with their distributions. For Orbitron,
   update both weights together and retain correct font extensions and CSS
   format declarations; never rename WOFF/WOFF2 bytes to a TTF filename.
5. Run `npm run sync-libs`, `npm run check-libs`, and `npm test`. Review the Git diff
   and exercise login, purchaser and approver pages, ordering, Demo Administration,
   translations, messaging, and Swagger UI. Update this version table.
6. Commit `package.json`, `package-lock.json`, mapping/formatting changes, this
   documentation, and the resulting files under `src/main/resources/static/lib/`.
   Keep `node_modules/` untracked.
