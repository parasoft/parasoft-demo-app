package com.parasoft.demoapp.util;

public class SqlStringEscapeUtil {

    public static final char escapeChar = '/';

    public static String escapeLikeString(String str){
        str = str.replaceAll("/", "//");
        str = str.replaceAll("%", "/%");
        str = str.replaceAll("_", "/_");
        return str;
    }
}
