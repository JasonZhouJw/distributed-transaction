package com.tcc.distributedtransaction.model;

import com.tcc.distributedtransaction.annotation.client.PreCheck;
import com.tcc.distributedtransaction.domain.TryResult;
import com.tcc.distributedtransaction.domain.TryResultCode;
import com.tcc.distributedtransaction.exception.TryFailException;
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
public class TryExecutor extends TccExecutor {

    private PreCheck preCheck;

    public TryExecutor(Object bean, Method method, PreCheck preCheck) {
        super(bean, method);
        this.preCheck = preCheck;
    }


    @Override
    public void init(DefaultListableBeanFactory defaultListableBeanFactory) {
        super.init(defaultListableBeanFactory);
        this.setAfterEvent(preCheck.after());
        this.setBeforeEvent(preCheck.before());
    }

    public void execute(Object[] parameters) throws TryFailException {
        try {
            Object[] actualParameter = this.before(parameters);
            log.info("Start to Try [{}], Parameters are [{}] ", this.method.getName(), JsonUtils.toJson(actualParameter));
            Class returnType = this.getMethod().getReturnType();
            if (returnType == null) {
                this.getMethod().invoke(this.getBean(), actualParameter);
                log.info("End to Try [{}], the method is void ", this.method.getName(), JsonUtils.toJson(actualParameter));
                this.after(null);
            } else {
                Object response = this.getMethod().invoke(this.getBean(), actualParameter);
                Boolean hasAfterHandler = this.after(response);
                log.info("End to Try [{}], the response : [{}]", this.method.getName(), JsonUtils.toJson(response));
                if (!hasAfterHandler && response instanceof TryResult && !TryResultCode.isSuccess(((TryResult) response).getCode())) {
                    throw new TryFailException(response);
                }
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (TryFailException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TryFailException(e);
        }
    }
}
