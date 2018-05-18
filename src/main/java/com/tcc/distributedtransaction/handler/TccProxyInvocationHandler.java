package com.tcc.distributedtransaction.handler;

import com.tcc.distributedtransaction.annotation.client.Confirm;
import com.tcc.distributedtransaction.model.TccExecutor;
import com.tcc.distributedtransaction.singleton.TccAttributeSingleton;
import com.tcc.distributedtransaction.util.TccConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Setter
@Getter
@Slf4j
public class TccProxyInvocationHandler implements InvocationHandler {

    private Object feignClientBean;

    private Class feignClientInterface;

    public TccProxyInvocationHandler(Object feignClientBean, Class feignClientInterface) {
        this.feignClientBean = feignClientBean;
        this.feignClientBean = feignClientInterface;
    }


    @Override
    public Object invoke(Object proxyObject, Method method, Object[] parameters) throws Throwable {
        String tryKey = getTryKey(method);
        if (StringUtils.isNotEmpty(tryKey)) {
            String executorKey = feignClientInterface.getName() + "." + method.getName();
            TccExecutor tccExecutor = TccAttributeSingleton.getProcessor().getCheckExecutor(executorKey);
            if (tccExecutor != null) {
                tccExecutor.execute(parameters);
            }
        }
        return method.invoke(feignClientBean, parameters);
    }

    private String getTryKey(Method method) {
        Confirm confirm = method.getAnnotation(Confirm.class);
        if (confirm != null) {
            String tryKey = confirm.preCheck();
            if (StringUtils.isEmpty(tryKey)) {
                tryKey = this.feignClientInterface.getName() + TccConstants.PERIOD +
                        "try" + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
            }
            return tryKey;
        } else {
            log.warn("Method [{}] don't have Confirm annotation.", method.getName());
        }
        return null;
    }

}
