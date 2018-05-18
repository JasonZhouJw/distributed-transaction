package com.tcc.distributedtransaction.singleton;

import com.tcc.distributedtransaction.annotation.client.Cancel;
import com.tcc.distributedtransaction.annotation.client.PreCheck;
import com.tcc.distributedtransaction.model.CancelExecutor;
import com.tcc.distributedtransaction.model.TccExecutor;
import com.tcc.distributedtransaction.model.TryExecutor;
import com.tcc.distributedtransaction.util.TccConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TccAttributeSingleton {

    private static TccAttributeSingleton processor = ProcessorInner.TCC_ATTRIBUTE_SINGLETON;

    /**
     * key: PreCheck.value(), default: className.methodName
     */
    private Map<String, TccExecutor> checkExecutorMap = new HashMap<>();

    /**
     * key: Cancel.value(), default: className.methodName
     */
    private Map<String, TccExecutor> cancelExecutorMap = new HashMap<>();

    private TccAttributeSingleton() {

    }

    public static TccAttributeSingleton getProcessor() {
        return processor;
    }

    public TccExecutor getCheckExecutor(String executorKey) {
        return checkExecutorMap.get(executorKey);
    }

    public TccExecutor getCancelExecutor(String executorKey) {
        return cancelExecutorMap.get(executorKey);
    }


    public void addExecutor(PreCheck preCheck, String beanName, Object bean, Method method, DefaultListableBeanFactory defaultListableBeanFactory) {
        String executorName = getExecutorName(preCheck.value(), beanName, method);
        TccExecutor executor = this.checkExecutorMap.get(executorName);
        if (executor != null) {
            log.warn("Executor [{}] already existing ", executorName);
        } else {
            TryExecutor tryExecutor = new TryExecutor(bean, method, preCheck);
            tryExecutor.init(defaultListableBeanFactory);
            this.checkExecutorMap.put(executorName, tryExecutor);
        }
    }

    public void addExecutor(Cancel cancel, String beanName, Object bean, Method method, DefaultListableBeanFactory defaultListableBeanFactory) {
        String executorName = getExecutorName(cancel.value(), beanName, method);
        TccExecutor executor = this.cancelExecutorMap.get(executorName);
        if (executor != null) {
            log.warn("Executor [{}] already existing ", executorName);
        } else {
            CancelExecutor cancelExecutor = new CancelExecutor(bean, method, cancel);
            cancelExecutor.init(defaultListableBeanFactory);
            this.cancelExecutorMap.put(executorName, cancelExecutor);
        }
    }

    private static class ProcessorInner {
        static final TccAttributeSingleton TCC_ATTRIBUTE_SINGLETON = new TccAttributeSingleton();
    }

    private String getExecutorName(String tccKey, String beanName, Method method) {
        return StringUtils.isNotEmpty(tccKey) ? tccKey : beanName + TccConstants.PERIOD + method.getName();
    }
}
