package com.nihalsoft.java.dbutil.common;

public class ColumnInfo {

    private String name = "";
    private boolean insertable;
    private boolean idColumn;
    private Object value = null;

    public ColumnInfo(String name, Object value, boolean insertable, boolean idColumn) {
        this.name = name;
        this.value = value;
        this.insertable = insertable;
        this.idColumn = idColumn;
    }

    public boolean isIdColumn() {
        return idColumn;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean isInsertable() {
        return !idColumn || insertable;
    }

}
