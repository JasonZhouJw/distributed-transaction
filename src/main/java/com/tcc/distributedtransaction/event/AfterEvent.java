package com.tcc.distributedtransaction.event;

import java.util.Map;

public interface AfterEvent<T> {

    void execute(T response, Map<String, Object> attributes);
}
