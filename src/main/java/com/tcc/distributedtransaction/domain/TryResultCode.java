package com.tcc.distributedtransaction.domain;

import lombok.Getter;

@Getter
public enum TryResultCode {

    SUCCESS("000000", "SUCCESS");

    private String code;

    private String message;

    TryResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Boolean isSuccess(String code) {
        return SUCCESS.code.equals(code);
    }
}
