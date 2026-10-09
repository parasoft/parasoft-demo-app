const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');

const source = fs.readFileSync(path.join(__dirname,
    '../../main/resources/static/common/js/demoAdmin.js'), 'utf8');

function loadAdmin() {
    // Register controllers without running them; the mapping itself is the real admin code.
    const module = { controller() {} };
    const context = vm.createContext({
        angular: {
            module: () => module,
            element: () => ({ next: () => ({ text: () => 'Outdoor' }) })
        },
        setLocale() {},
        initHeaderController() {},
        initProductBuildInfo() {},
        initAuthorizationHeader() {},
        initToastr() {}
    });
    vm.runInContext(source, context);
    return context;
}

test('restores the immediate processing checkbox from saved preferences', () => {
    const admin = loadAdmin();
    const checked = admin.handleDemoBugsFromServer([
        { demoBugsType: 'PROCESS_ORDERS_IMMEDIATELY' }
    ]);
    assert.equal(checked.process_orders_immediately, true);
    assert.equal(checked.unkonwn, undefined);
});

test('keeps immediate processing unchecked when absent or reset', () => {
    const admin = loadAdmin();
    for (const bugs of [null, [], [{ demoBugsType: 'INCORRECT_LOCATION_FOR_APPROVED_ORDERS' }]]) {
        assert.equal(Boolean(admin.handleDemoBugsFromServer(bugs).process_orders_immediately), false);
    }
});

test('restores immediate processing alongside existing demo bugs', () => {
    const admin = loadAdmin();
    const checked = admin.handleDemoBugsFromServer([
        { demoBugsType: 'REVERSE_ORDER_OF_ORDERS' },
        { demoBugsType: 'PROCESS_ORDERS_IMMEDIATELY' }
    ]);
    assert.equal(checked.reverse_order_of_orders, true);
    assert.equal(checked.process_orders_immediately, true);
});
