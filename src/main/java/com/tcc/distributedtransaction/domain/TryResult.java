package com.tcc.distributedtransaction.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TryResult<T> {

    private String code;

    private String message;

    private Object[] arguments;

    private String method;

    private T data;
}
