package com.tcc.distributedtransaction.exception;

import java.util.ArrayList;
import java.util.List;

public class CancelFailException extends Exception {

    private List<Exception> cancelExceptionList = new ArrayList<>();

    public CancelFailException() {
    }

    public CancelFailException(List<Exception> cancelExceptionList) {
        this.cancelExceptionList = cancelExceptionList;
    }

    public CancelFailException(String message) {
        super(message);
    }

    public CancelFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelFailException(Throwable cause) {
        super(cause);
    }

    public CancelFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public void addException(Exception e) {
        cancelExceptionList.add(e);
    }
}
