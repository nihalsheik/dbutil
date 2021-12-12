package com.nihalsoft.java.dbutil.common;

import java.util.List;

public class EntityDescriptor {

    private String tableName = "";

    private List<ColumnInfo> columns;

    private ColumnInfo idColumn = null;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setIdColumn(ColumnInfo idColumn) {
        this.idColumn = idColumn;
    }

    public ColumnInfo getIdColumn() {
        return idColumn;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }
}
