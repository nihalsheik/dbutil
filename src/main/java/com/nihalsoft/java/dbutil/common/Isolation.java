package com.nihalsoft.java.dbutil.common;

public enum Isolation {
    
    TRANSACTION_NONE(0),

    TRANSACTION_READ_COMMITTED(1),

    TRANSACTION_READ_UNCOMMITTED(2),

    TRANSACTION_REPEATABLE_READ(4),

    TRANSACTION_SERIALIZABLE(8);

    private int value = 0;

    Isolation(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
