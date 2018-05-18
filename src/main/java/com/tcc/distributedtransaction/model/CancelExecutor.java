package com.tcc.distributedtransaction.model;

import com.tcc.distributedtransaction.annotation.client.Cancel;
import com.tcc.distributedtransaction.exception.CancelFailException;
import com.tcc.distributedtransaction.util.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Getter
@Setter
public class CancelExecutor extends TccExecutor {

    private Cancel cancel;

    public CancelExecutor(Object bean, Method method, Cancel cancel) {
        super(bean, method);
        this.cancel = cancel;
    }

    @Override
    public void init(DefaultListableBeanFactory defaultListableBeanFactory) {
        super.init(defaultListableBeanFactory);
        this.setAfterEvent(cancel.after());
        this.setBeforeEvent(cancel.before());
    }

    public void execute(Object[] parameters) throws CancelFailException {
        try {
            log.info("Start to Cancel [{}], Parameters are [{}] ", this.method.getName(), JsonUtils.toJson(parameters));
            this.getMethod().invoke(this.getBean(), parameters);
            log.info("End to Cancel [{}], the cancel method should not have return value ", this.method.getName(), JsonUtils.toJson(parameters));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CancelFailException(e);
        }
    }
}
