package com.tcc.distributedtransaction.exception;

public class TccException extends Exception {
    public TccException() {
        super();
    }

    public TccException(String s) {
        super(s);
    }

    public TccException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TccException(Throwable throwable) {
        super(throwable);
    }

    protected TccException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
