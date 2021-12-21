package com.nihalsoft.java.dbutil.common;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.nihalsoft.java.dbutil.annotation.Column;
import com.nihalsoft.java.dbutil.annotation.Id;
import com.nihalsoft.java.dbutil.annotation.Table;

public class EntityDescriptor {

    private String tableName = "";

    private List<ColumnInfo> columns;

    private String idColumn = null;

    public EntityDescriptor(Class<?> clazz) throws Exception {

        Table tbl = clazz.getAnnotation(Table.class);
        this.tableName = tbl.name();

        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

        BeanInfo bi = Introspector.getBeanInfo(clazz);

        for (PropertyDescriptor property : bi.getPropertyDescriptors()) {

            if (property.getName().equals("class")) {
                continue;
            }

            Method getter = property.getReadMethod();
            Column e = getter.getAnnotation(Column.class);
            String name = (e != null && !e.name().equals("")) ? e.name() : property.getName();
            Id idCol = getter.getAnnotation(Id.class);
            ColumnInfo colInfo = new ColumnInfo(name, e != null && e.insertable(), idCol != null);
            if (idCol != null) {
                this.idColumn = name;
            }

            columns.add(colInfo);
        }

        this.columns = columns;
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

    public String getIdColumn() {
        return idColumn;
    }

    public DataMap loadValues(Object entity, Predicate<Entry<ColumnInfo, Object>> filter) throws Exception {
        DataMap dm = new DataMap();

        int i = 0;
        for (PropertyDescriptor property : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
            if (!property.getName().equals("class")) {
                Method getter = property.getReadMethod();
                Column e = getter.getAnnotation(Column.class);
                String name = (e != null && !e.name().equals("")) ? e.name() : property.getName();
                Object value = getter.invoke(entity);
                Entry<ColumnInfo, Object> entry = new SimpleEntry<ColumnInfo, Object>(this.columns.get(i), value);
                if (filter.test(entry)) {
                    dm.put(name, getter.invoke(entity));
                }
                i++;
            }
        }

        return dm;
    }
}
