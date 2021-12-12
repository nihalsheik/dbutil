package com.nihalsoft.java.dbutil.common;

public class Entity {

    protected DataMap prop;

    public Entity() {
        prop = new DataMap();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) prop.get(key);
    }

    public <T> void set(String key, T value) {
        prop.put(key, value);
    }

    @Override
    public String toString() {
        return prop.toString();
    }

    public DataMap getProperties() {
        return prop;
    }

}
