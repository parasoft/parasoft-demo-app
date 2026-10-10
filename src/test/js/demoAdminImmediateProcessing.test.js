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

test('restores standalone initial status independently of demo bugs', () => {
    const options = loadOptions({ newOrdersInitiallyProcessed: true,
        demoBugs: [{ demoBugsType: 'REVERSE_ORDER_OF_ORDERS' }] });
    assert.equal(options.newOrdersInitiallyProcessed, true);
    assert.equal(options.demoBugs.reverse_order_of_orders, true);
});

test('keeps initial status unchecked for an absent or disabled preference', () => {
    for (const preferences of [{}, { newOrdersInitiallyProcessed: false },
        { newOrdersInitiallyProcessed: null }]) {
        assert.equal(loadOptions(preferences).newOrdersInitiallyProcessed, false);
    }
});
