package com.nihalsoft.java.dbutil.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Session {

    private static final Logger log = Logger.getLogger(Session.class.getName());;

    private String sessionId;

    private TransactionManager txManager;

    private Isolation isolation = Isolation.TRANSACTION_NONE;

    private Propagation propagation = Propagation.PROPAGATION_REQUIRED;

    public Session(TransactionManager txManager) {
        this.txManager = txManager;
        this.sessionId = this.createSessionId();
    }

    public Session propagation(Propagation propagation) {
        this.propagation = propagation;
        return this;
    }

    public Session isolation(Isolation isolation) {
        this.isolation = isolation;
        return this;
    }

    public void begin(TxConsumer txc) throws SQLException {
        ActiveConnection actCon = null;
        Connection conn = null;
        boolean owner = true;
        try {

            actCon = this.getConnection();

            owner = actCon.ownerOf(sessionId);

            Util.throwIf(actCon == null, "Error while creating connection");

            conn = actCon.getConnection();

            if (this.isolation.value() > 0) {
                conn.setTransactionIsolation(isolation.value());
            }

            conn.setAutoCommit(false);

            txc.exec();

            if (owner) {
                log.info("Commit - sessionId  " + this.sessionId);
                txManager.commit(conn);
            }

        } catch (Exception ex) {
            log.warning(ex.getMessage());
            txManager.rollback(conn);
        } finally {
            if (owner) {
                txManager.close(conn);
            }
        }
    }

    private String createSessionId() {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        StackTraceElement ste = stes[4];
        return Thread.currentThread().getId() + "-" + ste.getClassName() + "-" + ste.getMethodName() + "-"
                + ste.getLineNumber();
    }

    private ActiveConnection getConnection() throws SQLException {

        ActiveConnection actCon = null;

        switch (this.propagation) {
        case PROPAGATION_REQUIRED:
            actCon = txManager.createConnectionIfNecessary(sessionId);
            break;

        case PROPAGATION_REQUIRES_NEW:
            actCon = txManager.createConnection(sessionId);
            break;

        case PROPAGATION_MANDATORY:
            actCon = txManager.getActiveConnection();
            if (actCon == null) {
                throw new SQLException("There is no active transaction");
            }
            break;

        case PROPAGATION_NEVER:
            actCon = txManager.getActiveConnection();
            if (actCon != null) {
                throw new SQLException("There is active transaction");
            }
            break;
        default:
            break;
        }
        return actCon;
    }

}
