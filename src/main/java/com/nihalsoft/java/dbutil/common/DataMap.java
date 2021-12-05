package com.nihalsoft.java.dbutil.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataMap implements Map<String, Object> {

    Map<String, Object> source;

    public DataMap() {
        super();
        source = new HashMap<String, Object>();
    }

    public static DataMap create() {
        return new DataMap();
    }

    @Override
    public DataMap put(String key, Object value) {
        source.put(key, value);
        return this;
    }

    public String getString(String key) {
        try {
            return source.get(key).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public Long getLong(String key) {
        try {
            return Long.valueOf(source.get(key).toString());
        } catch (Exception ex) {
            return 0L;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.valueOf(source.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public float getFloat(String key) {
        try {
            return Float.valueOf(source.get(key).toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public Object get(String key) {
        return source.get(key);
    }

    public Object get(String key, Object defaultValue) {
        if (source.containsKey(key)) {
            return source.get(key);
        } else {
            return defaultValue;
        }
    }

    public void remove(String... keys) {
        for (String key : keys) {
            source.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) source.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        source.entrySet().forEach(a -> {
            sb.append(a.getKey()).append("=").append(a.getValue()).append(" ");
        });
        return sb.toString();
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return source.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return source.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return source.get(key);
    }

    @Override
    public Object remove(Object key) {
        return source.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        source.putAll(m);
    }

    @Override
    public void clear() {
        source.clear();
    }

    @Override
    public Set<String> keySet() {
        return source.keySet();
    }

    @Override
    public Collection<Object> values() {
        return source.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return source.entrySet();
    }

    public Map<String, Object> toMap() {
        return source;
    }
}
