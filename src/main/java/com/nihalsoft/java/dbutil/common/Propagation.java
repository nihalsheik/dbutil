package com.nihalsoft.java.dbutil.common;

public enum Propagation {

    PROPAGATION_REQUIRED(0),

    PROPAGATION_MANDATORY(1),

    PROPAGATION_REQUIRES_NEW(2),

//    PROPAGATION_NOT_SUPPORTED(3),

    PROPAGATION_NEVER(4);

    private int value = 0;

    Propagation(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
