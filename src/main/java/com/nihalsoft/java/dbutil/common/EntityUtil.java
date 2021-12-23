package com.nihalsoft.java.dbutil.common;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.nihalsoft.java.dbutil.annotation.Column;
import com.nihalsoft.java.dbutil.annotation.Id;
import com.nihalsoft.java.dbutil.annotation.Table;

public class EntityUtil {

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static String getTableName(Class<?> clazz) throws Exception {
        Table tbl = clazz.getAnnotation(Table.class);
        if (tbl == null) {
            throw new SQLException("Invalid table name or empty");
        }
        return tbl.name();
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public static EntityDescriptor getDescriptor(Class<?> clazz) throws Exception {

        Table tbl = clazz.getAnnotation(Table.class);

        AtomicReference<ColumnInfo> idColumn = new AtomicReference<ColumnInfo>();

        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

        EntityUtil._getInfo(clazz, null, (ci) -> {
            if (ci.isIdColumn()) {
                idColumn.set(ci);
            }
            columns.add(ci);
        });

        return new EntityDescriptor(tbl.name(), columns, idColumn.get());
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param entity
     * @param filter
     * @return
     * @throws Exception
     */
    public static EntityDescriptor getDescriptor(Object entity, Predicate<ColumnInfo> filter) throws Exception {

        AtomicReference<ColumnInfo> idColumn = new AtomicReference<ColumnInfo>();

        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

        EntityUtil._getInfo(entity.getClass(), entity, (ci) -> {

            try {
                if (filter == null || filter.test(ci)) {
                    columns.add(ci);
                }
                if (ci.isIdColumn()) {
                    idColumn.set(ci);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        Table tbl = entity.getClass().getAnnotation(Table.class);

        return new EntityDescriptor(tbl.name(), columns, idColumn.get());
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param clazz
     * @param object
     * @param consumer
     * @throws Exception
     */
    private static void _getInfo(Class<?> clazz, Object object, Consumer<ColumnInfo> consumer) throws Exception {

        BeanInfo bi = Introspector.getBeanInfo(clazz);

        for (PropertyDescriptor property : bi.getPropertyDescriptors()) {

            if (property.getName().equals("class")) {
                continue;
            }

            Method getter = property.getReadMethod();
            Column e = getter.getAnnotation(Column.class);
            String name = (e != null && !e.name().equals("")) ? e.name() : property.getName();
            Id idCol = getter.getAnnotation(Id.class);
            Object value = null;
            if (object != null) {
                value = getter.invoke(object);
            }
            ColumnInfo colInfo = new ColumnInfo(name, value, e != null && e.insertable(), idCol != null);

            consumer.accept(colInfo);
        }

    }
}
