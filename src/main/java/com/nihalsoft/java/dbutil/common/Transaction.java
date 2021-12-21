package com.nihalsoft.java.dbutil.common;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

    private TransactionManager tm;

    private int isolation = -1;

    public Transaction(TransactionManager tm) {
        this.tm = tm;
    }

    public Transaction withIsolation(int isolation) {
        this.isolation = isolation;
        return this;
    }

    public void exec(TxConsumer txc) throws SQLException {
        Connection conn = null;
        try {
            
            conn = tm.createConnection();

            if (this.isolation != -1) {
                conn.setTransactionIsolation(isolation);
            }

            conn.setAutoCommit(false);
            
            txc.exec();
            
            System.out.println("Commit...");
            tm.commit(conn);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            tm.rollback(conn);
        } finally {
            tm.close(conn);
        }
    }
    
}
