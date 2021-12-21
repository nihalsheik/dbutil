package com.nihalsoft.java.dbutil.common;

import java.sql.Connection;

public class ConnectionHolder {

    private Connection connection;
    private long ts;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
        this.ts = System.currentTimeMillis();
    }

    public Connection getConnection() {
        return connection;
    }

    public long getTs() {
        return ts;
    }

}
