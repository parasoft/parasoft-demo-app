const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const path = require('node:path');
const { test } = require('node:test');
const { JSDOM } = require('jsdom');

const libraryDir = path.resolve(__dirname, '../../src/main/resources/static/lib');
const jquery = readFileSync(path.join(libraryDir, 'jquery/jquery-3.3.1.min.js'), 'utf8');
const plugin = readFileSync(path.join(libraryDir, 'jquery/jquery.serializejson.min.js'), 'utf8');

// These fixtures cover the field shapes used by the application's seven call sites.
// A real DOM and the shipped jQuery preserve successful-control and checkbox rules.
const forms = [
  {
    name: 'category search with multiple locations and Chinese text',
    html: '<input name="searchString" value="太空 背包"><input name="categoryId" value="2">' +
      '<select name="sort"><option selected value="name">Alphabetical</option></select>' +
      '<input type="checkbox" name="regions[]" value="WEST" checked>' +
      '<input type="checkbox" name="regions[]" value="EAST" checked>' +
      '<input type="checkbox" name="regions[]" value="NORTH">',
    expected: { searchString: '太空 背包', categoryId: '2', sort: 'name', regions: ['WEST', 'EAST'] }
  },
  {
    name: 'general preferences retain string booleans and selected bug arrays',
    html: '<input name="webServiceMode" value="REST_API"><input name="industryType" value="AEROSPACE">' +
      '<input type="checkbox" name="advertisingEnabled" value="true" checked>' +
      '<input type="checkbox" name="newOrdersInitiallyProcessed" value="true">' +
      '<input type="checkbox" name="demoBugs[]" value="BUG_ONE" checked>' +
      '<input type="checkbox" name="demoBugs[]" value="BUG_TWO" checked>' +
      '<input name="ignored" value="disabled" disabled><input value="no name">',
    expected: { webServiceMode: 'REST_API', industryType: 'AEROSPACE', advertisingEnabled: 'true', demoBugs: ['BUG_ONE', 'BUG_TWO'] }
  },
  {
    name: 'category create and update retain empty and multiline values',
    html: '<input name="name" value="飞行器"><input type="hidden" name="imagePath" value="">' +
      '<textarea name="description">Line one\n第二行</textarea><input type="file" name="image">',
    expected: { name: '飞行器', imagePath: '', description: 'Line one\r\n第二行' }
  },
  {
    name: 'item fields remain strings for the existing application number conversion',
    html: '<input name="name" value="001"><input name="region" value="WEST">' +
      '<input type="number" name="inStock" value="0"><input name="categoryId" value="2">',
    expected: { name: '001', region: 'WEST', inStock: '0', categoryId: '2' }
  },
  {
    name: 'labels preserve language and nested overrides',
    html: '<input name="languageType" value="ZH"><input name="labelsOverrided" value="false">' +
      '<input type="hidden" name="labelPairs[PROJECT_NAME]" value="太空 &amp; 航空">' +
      '<input type="hidden" name="labelPairs[PURCHASER_TITLE]" value="">',
    expected: { languageType: 'ZH', labelsOverrided: 'false', labelPairs: { PROJECT_NAME: '太空 & 航空', PURCHASER_TITLE: '' } }
  },
  ...['APPROVED', 'DENIED'].map(status => ({
    name: `approver response ${status} preserves comments`,
    html: `<select name="status"><option selected value="${status}">${status}</option></select>` +
      '<textarea name="comments">English 中文 0 false</textarea>',
    expected: { status, comments: 'English 中文 0 false' }
  }))
];

for (const form of forms) {
  test(`SerializeJSON: ${form.name}`, () => {
    const dom = new JSDOM(`<form>${form.html}</form>`, { runScripts: 'outside-only' });
    try {
      dom.window.eval(jquery);
      dom.window.eval(plugin);
      // Cross-realm objects must be converted before strict structural comparison.
      const actual = JSON.parse(JSON.stringify(dom.window.jQuery('form').serializeJSON()));
      assert.deepEqual(actual, form.expected);
    } finally {
      dom.window.close();
    }
  });
}
