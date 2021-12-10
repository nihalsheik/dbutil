package com.nihalsoft.java.dbutil.common;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class BeanInfo {

    private String tableName = "";

    private List<ColumnInfo> columns;

    private ColumnInfo idColumn;

    public BeanInfo(Object object) throws Exception {
        this.init(null, object);
    }

    public BeanInfo(Class<?> clazz) throws Exception {
        this.init(clazz, null);
    }

    private void init(Class<?> clazz, Object object) throws IntrospectionException {

        columns = new ArrayList<ColumnInfo>();
        for (PropertyDescriptor property : Introspector.getBeanInfo(clazz != null ? clazz : object.getClass())
                .getPropertyDescriptors()) {
            if (!property.getName().equals("class")) {
                if (clazz != null) {
                    columns.add(new ColumnInfo(property, clazz));
                } else {
                    columns.add(new ColumnInfo(property, object));
                }
            }
        }

        if (clazz == null) {
            clazz = object.getClass();
        }

        Table tbl = clazz.getAnnotation(Table.class);
        this.tableName = tbl.name();
        for (ColumnInfo ci : columns) {
            if (ci.isIdColumn()) {
                this.idColumn = ci;
            }
        }
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public ColumnInfo getIdColumn() {
        return idColumn;
    }

}
