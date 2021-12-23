package com.nihalsoft.java.dbutil.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class TransactionManager {

    private Logger log = Logger.getLogger("TransactionManager");

    private DataSource ds;

    private Map<String, ActiveConnection> conHolders = new HashMap<String, ActiveConnection>();

    public TransactionManager(DataSource ds) {
        this.ds = ds;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @return
     * @throws SQLException
     */
    public ActiveConnection createConnectionIfNecessary(String key) throws SQLException {
        log.info("createConnectionIfNecessary");
        ActiveConnection ch = this.getActiveConnection();
        if (ch == null) {
            log.info("There is no active connection, creating new one");
            ch = this.createConnection(key);
        }
        return ch;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @return
     * @throws SQLException
     */
    public ActiveConnection createConnection(String key) throws SQLException {
        log.info("Creating Connection : Key " + key);
        Connection conn = this.ds.getConnection();
        if (conn == null) {
            throw new SQLException("Error while creating connection");
        }

        ActiveConnection ch = new ActiveConnection(key, conn);
        conHolders.put(key, ch);
        return ch;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @return
     */
    public ActiveConnection getActiveConnection() {
        log.info("getActiveConnection");

        long tid = Thread.currentThread().getId();
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int len = stes.length;

        String key = "";
        for (int i = 4; i < len; i++) {
            key = this._generateKey(tid, stes[i]);
            log.info(String.format("%1$" + i + "s", "-->") + " Searching : " + key);
            if (conHolders.containsKey(key)) {
                log.info("Connection available with key : ---------> " + key);
                return conHolders.get(key);
            }
        }

        return null;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param tid
     * @param ste
     * @return
     */
    private String _generateKey(long tid, StackTraceElement ste) {
        return tid + "-" + ste.getClassName() + "-" + ste.getMethodName() + "-" + ste.getLineNumber();
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param conn
     */
    public void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
        }
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param conn
     */
    public void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
        }
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @param conn
     */
    public void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }

}
