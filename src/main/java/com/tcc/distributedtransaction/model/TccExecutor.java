package com.tcc.distributedtransaction.model;

import com.tcc.distributedtransaction.event.AfterEvent;
import com.tcc.distributedtransaction.event.BeforeEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.HashMap;

@Slf4j
@Getter
public abstract class TccExecutor {

    protected Object bean;

    protected Method method;

    protected AfterEvent afterEvent;

    protected BeforeEvent beforeEvent;

    protected DefaultListableBeanFactory defaultListableBeanFactory;

    public TccExecutor(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public void init(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    public abstract void execute(Object[] parameters) throws Exception;

    //TODO: 需要实现before处理的参数传递，要不然太不方便
    protected Object[] before(Object[] parameters) {
        Object[] actualParameters = parameters;
        if (beforeEvent != null) {
            actualParameters = beforeEvent.execute(parameters, new HashMap<>());
        }
        return actualParameters;
    }

    //TODO: 需要实现after处理的参数传递，要不然太不方便
    protected Boolean after(Object response) {
        Boolean hasAfterHandler = false;
        if (afterEvent != null) {
            hasAfterHandler = true;
            afterEvent.execute(response, new HashMap<>());
        }
        return hasAfterHandler;
    }

    protected void setAfterEvent(String afterHandlerName) {
        if (StringUtils.isNotEmpty(afterHandlerName)) {
            Object afterHandlerBean = this.defaultListableBeanFactory.getBean(afterHandlerName);
            if (afterHandlerBean != null && afterHandlerBean instanceof AfterEvent) {
                afterEvent = (AfterEvent) afterHandlerBean;
            }
        }
    }

    protected void setBeforeEvent(String beforeHandlerName) {
        if (StringUtils.isNotEmpty(beforeHandlerName)) {
            Object beforeHandlerBean = this.defaultListableBeanFactory.getBean(beforeHandlerName);
            if (beforeHandlerBean != null && beforeHandlerBean instanceof BeforeEvent) {
                beforeEvent = (BeforeEvent) beforeHandlerBean;
            }
        }
    }

}
