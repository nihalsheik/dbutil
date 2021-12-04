package com.nihalsoft.java.dbutil.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class ColumnInfo {

    private String name = "";
    private Object value = "";
    private ColumnType type = ColumnType.DEFAULT;

    public ColumnInfo(PropertyDescriptor property, Object object) {
        name = property.getName();
        Method getter = property.getReadMethod();
        Column e = getter.getAnnotation(Column.class);
        if (e != null) {
            if (!e.name().equals("")) {
                name = e.name();
            }
            type = e.type();
        }
        try {
            value = getter.invoke(object);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public ColumnType getType() {
        return type;
    }

}
