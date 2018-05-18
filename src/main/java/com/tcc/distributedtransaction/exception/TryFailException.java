package com.tcc.distributedtransaction.exception;

import lombok.Getter;

public class TryFailException extends Exception {

    @Getter
    private Object response;

    public TryFailException(Object response) {
        this.response = response;
    }

    public TryFailException(Object response, Throwable throwable) {
        super(throwable);
        this.response = response;
    }

    public TryFailException() {
    }

    public TryFailException(String message) {
        super(message);
    }

    public TryFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public TryFailException(Throwable cause) {
        super(cause);
    }

    public TryFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
