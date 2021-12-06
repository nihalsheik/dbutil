package com.nihalsoft.java.dbutil.result;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.BeanProcessor;

import com.nihalsoft.java.dbutil.common.Column;
import com.nihalsoft.java.dbutil.common.DataMap;

public class BeanProcessorEx extends BeanProcessor {

    @Override
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        DataMap dm = new DataMap();
        for (int i = 0; i < props.length; i++) {
            Method getter = props[i].getReadMethod();
            Column e = getter.getAnnotation(Column.class);
            String key = (e != null && !e.name().equals("")) ? e.name() : props[i].getName();
            dm.put(key, i);
        }

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            columnToProperty[col] = dm.getInt(columnName);
        }

        return columnToProperty;
    }
}
