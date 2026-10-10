const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');

const source = fs.readFileSync(path.join(__dirname,
    '../../main/resources/static/common/js/demoAdmin.js'), 'utf8');

function loadOptions(preferences) {
    const controllers = {};
    const module = { controller(name, callback) { controllers[name] = callback; } };
    const context = vm.createContext({
        angular: {
            module: () => module,
            element: () => ({ next: () => ({ text: () => 'Outdoor' }) })
        },
        setLocale() {}, initHeaderController() {}, initProductBuildInfo() {},
        initAuthorizationHeader() {}, initToastr() {}
    });
    vm.runInContext(source, context);
    const options = {};
    const data = { restEndPoints: [], demoBugs: [], activeMqConfig: {},
        kafkaConfig: {}, rabbitMqConfig: {}, ...preferences };
    const http = request => {
        assert.equal(request.url, '/v1/demoAdmin/currentPreferences');
        return { then(callback) {
            callback({ data: { status: 1, data } });
            return { catch() {} };
        } };
    };
    controllers.optionsForm.call(options, {}, {}, http, () => {});
    return options;
}

test('restores saved newOrdersInitiallyProcessed', () => {
    const options = loadOptions({ newOrdersInitiallyProcessed: true });
    assert.equal(options.newOrdersInitiallyProcessed, true);
});

test('keeps newOrdersInitiallyProcessed unchecked when absent or disabled', () => {
    for (const preferences of [{}, { newOrdersInitiallyProcessed: false },
        { newOrdersInitiallyProcessed: null }]) {
        assert.equal(loadOptions(preferences).newOrdersInitiallyProcessed, false);
    }
});
