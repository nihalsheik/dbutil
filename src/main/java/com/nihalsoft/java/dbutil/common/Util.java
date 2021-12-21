package com.nihalsoft.java.dbutil.common;

public class Util {

    public static void throwIf(boolean condition, String message) throws Exception {
        if (condition) {
            throw new Exception(message);
        }
    }

}
