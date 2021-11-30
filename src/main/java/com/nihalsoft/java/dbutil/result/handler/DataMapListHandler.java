package com.nihalsoft.java.dbutil.result.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.nihalsoft.java.dbutil.common.DataMap;

public class DataMapListHandler implements ResultSetHandler<List<DataMap>> {

    @Override
    public List<DataMap> handle(ResultSet rs) throws SQLException {
        List<DataMap> rows = new ArrayList<DataMap>();
        if (!rs.next()) {
            return rows;
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        String[] colNames = new String[cols];
        
        DataMap dm = new DataMap();
        for (int i = 1; i <= cols; i++) {
            String columnName = rsmd.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(i);
            }
            colNames[i - 1] = columnName;
            dm.put(columnName, rs.getObject(i));
        }
        rows.add(dm);

        while (rs.next()) {
            dm = new DataMap();
            for (int i = 1; i <= cols; i++) {
                dm.put(colNames[i - 1], rs.getObject(i));
            }
            rows.add(dm);
        }
        return rows;
    }

}
