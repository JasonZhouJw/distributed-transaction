package com.tcc.distributedtransaction.proxy;

import com.tcc.distributedtransaction.handler.TccProxyInvocationHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.lang.reflect.Proxy;

@Setter
@Getter
public class TccProxyFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Object feignClientBean;

    private Class feignClientInterface;

    private ApplicationContext applicationContext;

    @Nullable
    @Override
    public Object getObject() throws Exception {
        TccProxyInvocationHandler tccProxyInvocationHandler = new TccProxyInvocationHandler(feignClientBean, feignClientInterface);
        return Proxy.newProxyInstance(feignClientInterface.getClassLoader(), new Class[]{feignClientInterface}, tccProxyInvocationHandler);
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return feignClientInterface;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //NONE
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
