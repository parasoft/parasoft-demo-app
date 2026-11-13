const assert = require('node:assert/strict');
const { readFile } = require('node:fs/promises');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');

async function localization(language, defaults = [JSON.parse]) {
  let configure;
  let configured;
  let preferredLanguage;
  let loaderFactory;
  const angular = {
    element: () => ({ val: () => '' }),
    extend: Object.assign,
    fromJson: value => typeof value === 'string' ? JSON.parse(value) : value,
    module: () => ({ factory: (name, factory) => { loaderFactory = factory; } })
  };
  const context = vm.createContext({ angular, navigator: { language } });
  const common = await readFile(path.resolve(__dirname, '../../src/main/resources/static/common/js/common.js'), 'utf8');
  vm.runInContext(common, context);
  context.setLocale({ config: callback => { configure = callback; } });
  const httpProvider = { defaults: { transformResponse: defaults } };
  configure({
    useSanitizeValueStrategy() {},
    useUrlLoader: (url, options) => { configured = { url, options }; },
    preferredLanguage: languageKey => { preferredLanguage = languageKey; }
  }, httpProvider);

  return {
    httpProvider,
    async load(variant, responseBody, rejection) {
      const upstream = await readFile(path.resolve(__dirname, '../node_modules/angular-translate-loader-url', variant), 'utf8');
      vm.runInContext(upstream, context);
      const loader = loaderFactory({ reject: key => Promise.reject(key) }, async request => {
        if (rejection) throw rejection;
        assert.equal(request.url, '/localize');
        const transforms = [].concat(request.transformResponse ?? httpProvider.defaults.transformResponse);
        const data = transforms.reduce((value, transform) => transform(value), responseBody[request.params.lang]);
        return { data };
      });
      return loader({ url: configured.url, key: preferredLanguage, ...configured.options });
    }
  };
}

const responses = {
  EN: '{"status":1,"message":"success","data":"{\\"WELCOME\\":\\"Welcome\\"}"}',
  ZH: '{"status":1,"message":"success","data":"{\\"WELCOME\\":\\"欢迎\\"}"}'
};

for (const variant of ['angular-translate-loader-url.js', 'angular-translate-loader-url.min.js']) {
  test(`${variant} loads the English translation map using application configuration`, async () => {
    const app = await localization('en-US');
    assert.deepEqual(await app.load(variant, responses), { WELCOME: 'Welcome' });
  });

  test(`${variant} loads the Chinese translation map using application configuration`, async () => {
    const app = await localization('zh-CN');
    assert.deepEqual(await app.load(variant, responses), { WELCOME: '欢迎' });
  });

  test(`${variant} retains the upstream rejection behavior on HTTP failure`, async () => {
    const app = await localization('en-US');
    await assert.rejects(app.load(variant, responses, new Error('HTTP 503')), reason => reason === 'EN');
  });
}

test('localization configuration leaves unrelated HTTP response transformations unchanged', async () => {
  const defaults = [JSON.parse];
  await localization('en-US', defaults);
  const ordinaryResponse = defaults.reduce((value, transform) => transform(value), '{"status":1,"data":{"items":[1]}}');
  assert.deepEqual(ordinaryResponse, { status: 1, data: { items: [1] } });
});

test('localization also preserves a default response transform provided as a function', async () => {
  const app = await localization('en-US', JSON.parse);
  assert.deepEqual(await app.load('angular-translate-loader-url.min.js', responses), { WELCOME: 'Welcome' });
});
