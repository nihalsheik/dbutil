package com.nihalsoft.java.dbutil.common;

import java.sql.Connection;

public class ActiveConnection {

    private String sessionId;
    
    private Connection connection;
    
    private long ts;

    public ActiveConnection(String sessionId, Connection connection) {
        this.sessionId = sessionId;
        this.connection = connection;
        this.ts = System.currentTimeMillis();
    }

    public String getSessionId() {
        return sessionId;
    }

    public Connection getConnection() {
        return connection;
    }

    public long getTs() {
        return ts;
    }

    public boolean ownerOf(String sessionId) {
        return this.sessionId.equals(sessionId);
    }
}
