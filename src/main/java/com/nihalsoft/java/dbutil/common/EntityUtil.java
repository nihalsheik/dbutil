package com.nihalsoft.java.dbutil.common;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.nihalsoft.java.dbutil.annotation.Column;
import com.nihalsoft.java.dbutil.annotation.Id;
import com.nihalsoft.java.dbutil.annotation.Table;

public class EntityUtil {

    public EntityUtil() {

    }

    public static String getTableName(Class<?> clazz) throws Exception {
        Table tbl = clazz.getAnnotation(Table.class);
        return tbl != null ? tbl.name() : "";
    }

    public static EntityDescriptor getEntityDescriptor(Object object) throws Exception {
        return EntityUtil.init(null, object);
    }

    public static EntityDescriptor getEntityDescriptor(Class<?> clazz) throws Exception {
        return EntityUtil.init(clazz, null);
    }

    private static EntityDescriptor init(Class<?> clazz, Object object) throws Exception {

        if (clazz == null) {
            clazz = object.getClass();
        }

        EntityDescriptor ei = new EntityDescriptor();
        Table tbl = clazz.getAnnotation(Table.class);
        ei.setTableName(tbl.name());

        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

        DataMap dm = null;
        BeanInfo bi;

        if (object != null && object.getClass().getSuperclass() == Entity.class) {
            Entity e = (Entity) object;
            dm = e.getProperties();
            bi = Introspector.getBeanInfo(clazz, Entity.class);
        } else {
            bi = Introspector.getBeanInfo(clazz);
        }

        for (PropertyDescriptor property : bi.getPropertyDescriptors()) {
            if (property.getName().equals("class")) {
                continue;
            }

            Method getter = property.getReadMethod();
            Column e = getter.getAnnotation(Column.class);
            String name = (e != null && !e.name().equals("")) ? e.name() : property.getName();
            Id idCol = getter.getAnnotation(Id.class);
            
            if (dm != null) {
                if (dm.containsKey(name)) {
                    columns.add(new ColumnInfo(name, dm.get(name), e));
                }
            } else {
                columns.add(new ColumnInfo(name, object != null ? getter.invoke(object) : null, e));
            }
        }

        ei.setColumns(columns);
        for (ColumnInfo ci : columns) {
            if (ci.isIdColumn()) {
                ei.setIdColumn(ci);
            }
        }

        return ei;
    }

}
