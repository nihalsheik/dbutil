package com.nihalsoft.java.dbutil.result;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.BeanProcessor;

import com.nihalsoft.java.dbutil.common.Column;

public class BeanProcessorEx extends BeanProcessor {

    @Override
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            for (int i = 0; i < props.length; i++) {
                Method getter = props[i].getReadMethod();
                Column e = getter.getAnnotation(Column.class);
                String key = getter.getName();
                if (e != null && !e.name().equals("")) {
                    key = e.name();
                }
                if (columnName.equalsIgnoreCase(key)) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }
}
