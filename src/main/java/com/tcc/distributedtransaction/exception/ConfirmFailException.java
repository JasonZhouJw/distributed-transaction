package com.tcc.distributedtransaction.exception;

public class ConfirmFailException extends Exception {
    public ConfirmFailException() {
    }

    public ConfirmFailException(String message) {
        super(message);
    }

    public ConfirmFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfirmFailException(Throwable cause) {
        super(cause);
    }

    public ConfirmFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
