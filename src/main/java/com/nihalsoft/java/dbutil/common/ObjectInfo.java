package com.nihalsoft.java.dbutil.common;

import java.lang.reflect.Method;

public class ObjectInfo {

    private String name = "";
    private Object value = "";
    private String type = "";
    private Method getter;

    public ObjectInfo(String name, Object value, String type, Method getter) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Method getGetter() {
        return getter;
    }

}
