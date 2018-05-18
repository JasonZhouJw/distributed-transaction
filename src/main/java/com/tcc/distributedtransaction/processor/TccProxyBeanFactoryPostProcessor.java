package com.tcc.distributedtransaction.processor;

import com.tcc.distributedtransaction.annotation.client.Cancel;
import com.tcc.distributedtransaction.annotation.client.PreCheck;
import com.tcc.distributedtransaction.proxy.TccProxyFactoryBean;
import com.tcc.distributedtransaction.singleton.TccAttributeSingleton;
import com.tcc.distributedtransaction.util.TccUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
public class TccProxyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private DefaultListableBeanFactory defaultListableBeanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if (this.defaultListableBeanFactory == null) {
            this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        }
        Map<String, Object> feignClientMap = defaultListableBeanFactory.getBeansWithAnnotation(FeignClient.class);
        if (feignClientMap != null) {
            feignClientMap.forEach((beanName, bean) -> {
                Class feignClientInterface;
                try {
                    feignClientInterface = this.getClass().getClassLoader().loadClass(beanName);
                } catch (ClassNotFoundException e) {
                    log.warn("Bean [{}] is not feign client.", beanName);
                    return;
                }

                this.registerTccBean(bean, feignClientInterface, this.defaultListableBeanFactory);

                Method[] methods = feignClientInterface.getMethods();
                if (methods != null && methods.length > 0) {
                    for (Method method : methods) {
                        PreCheck preCheck = method.getAnnotation(PreCheck.class);
                        if (preCheck != null) {
                            TccAttributeSingleton.getProcessor().addExecutor(preCheck, beanName, bean, method, defaultListableBeanFactory);
                            continue;
                        }
                        Cancel cancel = method.getAnnotation(Cancel.class);
                        if (cancel != null) {
                            TccAttributeSingleton.getProcessor().addExecutor(cancel, beanName, bean, method, defaultListableBeanFactory);
                        }
                    }
                }
            });
        }

    }


    private void registerTccBean(Object bean, Class clazz, DefaultListableBeanFactory defaultListableBeanFactory) {
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(TccProxyFactoryBean.class);
        definition.addPropertyValue("feignClientBean", bean);
        definition.addPropertyValue("feignClientInterface", clazz);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        beanDefinition.setPrimary(isPrimary());

        defaultListableBeanFactory.registerBeanDefinition(TccUtils.getBeanName(clazz), beanDefinition);
    }

    private Boolean isPrimary() {
        return false;
    }

}
