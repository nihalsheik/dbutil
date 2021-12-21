package com.nihalsoft.java.dbutil.common;

@FunctionalInterface
public interface TxConsumer {

    public abstract void exec() throws Exception;
}
