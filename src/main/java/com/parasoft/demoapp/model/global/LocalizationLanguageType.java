package com.parasoft.demoapp.model.global;

/**
 * Need add enums when localizing PDA to other languages.
 */
public enum LocalizationLanguageType {

    EN(""), ZH("_zh_CN");

    private String propertiesFileSuffix;

    LocalizationLanguageType(String type) {
        this.propertiesFileSuffix = type;
    }

    public String getPropertiesFileSuffix() {
        return propertiesFileSuffix;
    }
}
