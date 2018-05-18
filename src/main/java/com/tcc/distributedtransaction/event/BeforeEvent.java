package com.tcc.distributedtransaction.event;

import java.util.Map;

public interface BeforeEvent {

    Object[] execute(Object[] parameters, Map<String, Object> attributes );
}
