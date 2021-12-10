package com.nihalsoft.java.dbutil.common;

import java.util.HashMap;

public class DataMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public DataMap() {
        super();
    }

    public static DataMap create() {
        return new DataMap();
    }

    public DataMap add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public String getString(String key) {
        try {
            return this.get(key).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public Long getLong(String key) {
        try {
            return Long.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0L;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public float getFloat(String key) {
        try {
            return Float.valueOf(this.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public Object get(String key, Object defaultValue) {
        if (this.containsKey(key)) {
            return this.get(key);
        } else {
            return defaultValue;
        }
    }

    public void remove(String... keys) {
        for (String key : keys) {
            this.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) this.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.entrySet().forEach(a -> {
            sb.append(a.getKey()).append("=").append(a.getValue()).append(" ");
        });
        return sb.toString();
    }
}
