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

    private Map<String, ConnectionHolder> conHolders = new HashMap<String, ConnectionHolder>();

    public TransactionManager(DataSource ds) {
        this.ds = ds;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @return
     * @throws SQLException
     */
    public Connection createConnection() throws SQLException {

        Connection conn = this.ds.getConnection();
        if (conn == null) {
            throw new SQLException("Error while creating connection");
        }

        StackTraceElement[] stes = Thread.currentThread().getStackTrace();

        String key = this._generateKey(Thread.currentThread().getId(), stes[3]);

        log.info("Creating Connection : Key " + key);

        conHolders.put(key, new ConnectionHolder(conn));
        return conn;
    }

    /**
     * --------------------------------------------------------------------------------
     * 
     * @return
     */
    public Connection getConnection() {

        long tid = Thread.currentThread().getId();
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int len = stes.length;

        String key = "";
        for (int i = 4; i < len; i++) {
            key = this._generateKey(tid, stes[i]);
            System.out.println("------------------------> Searching : " + key);
            if (conHolders.containsKey(key)) {
                System.out.println("Connection available with key : ---------> " + key);
                return conHolders.get(key).getConnection();
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
        return tid + "/" + ste.getClassName() + "/" + ste.getMethodName() + "/" + ste.getLineNumber();
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
