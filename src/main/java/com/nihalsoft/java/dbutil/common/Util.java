package com.nihalsoft.java.dbutil.common;

import java.sql.SQLException;

public class Util {

    public static void throwIf(boolean condition, String message) throws Exception {
        if (condition) {
            throw new Exception(message);
        }
    }
    
    public static void throwSqlException(boolean condition, String message) throws SQLException {
        if (condition) {
            throw new SQLException(message);
        }
    }

}
