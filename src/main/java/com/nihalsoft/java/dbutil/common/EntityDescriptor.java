package com.nihalsoft.java.dbutil.common;

import java.util.List;

public class EntityDescriptor {

    private String tableName = "";

    private List<ColumnInfo> columns;

    private ColumnInfo idColumn = null;

    public EntityDescriptor(String tableName, List<ColumnInfo> columns, ColumnInfo idColumn) {
        this.tableName = tableName;
        this.columns = columns;
        this.idColumn = idColumn;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public boolean hasId() {
        return this.idColumn != null;
    }

    public ColumnInfo getIdColumn() {
        return idColumn;
    }

}
