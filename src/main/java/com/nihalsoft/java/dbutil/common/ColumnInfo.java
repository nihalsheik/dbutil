package com.nihalsoft.java.dbutil.common;

public class ColumnInfo {

    private String name = "";
    private boolean insertable;
    private boolean idColumn;

    public ColumnInfo(String name, boolean insertable, boolean idColumn) {
        this.name = name;
        this.insertable = insertable;
        this.idColumn = idColumn;
    }

    public boolean isIdColumn() {
        return idColumn;
    }

    public String getName() {
        return name;
    }

    public boolean isInsertable() {
        return !idColumn || insertable;
    }

}
