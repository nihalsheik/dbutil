package com.nihalsoft.java.dbutil.result.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import com.nihalsoft.java.dbutil.common.DataMap;

public class DataMapHandler implements ResultSetHandler<DataMap> {

    @Override
    public DataMap handle(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        DataMap dm = new DataMap();
        for (int i = 1; i <= cols; i++) {
            String columnName = rsmd.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(i);
            }
            System.out.println(columnName);
            dm.put(columnName, rs.getObject(i));
        }
        return dm;
    }

}
