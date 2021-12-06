package com.nihalsoft.java.dbutil.common;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class BeanInfo {

    private String tableName = "";

    private List<ColumnInfo> columns;

    private ColumnInfo idColumn;

    private List<String> columnNames;
    private List<Object> values;

    public BeanInfo(Object object) throws Exception {
        columns = new ArrayList<ColumnInfo>();
        for (PropertyDescriptor property : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            if (!property.getName().equals("class")) {
                columns.add(new ColumnInfo(property, object));
            }
        }
        this.init(object.getClass());
    }

    public BeanInfo(Class<?> clazz) throws Exception {
        columns = new ArrayList<ColumnInfo>();
        for (PropertyDescriptor property : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            if (!property.getName().equals("class")) {
                columns.add(new ColumnInfo(property, clazz));
            }
        }
        this.init(clazz);

    }

    private void init(Class<?> clazz) {
        values = new ArrayList<Object>();
        Table tbl = clazz.getAnnotation(Table.class);
        this.tableName = tbl.name();
        for (ColumnInfo ci : columns) {
            if (ci.isIdColumn()) {
                this.idColumn = ci;
            }
//            columnNames.add(ci.getName());
//            values.add(ci.getValue());
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
//
//    public List<String> getColumnNames() {
//        return columnNames;
//    }
//
//    public List<Object> getValues() {
//        return values;
//    }

}
