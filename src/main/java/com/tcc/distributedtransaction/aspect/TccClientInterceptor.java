package com.tcc.distributedtransaction.aspect;

import com.tcc.distributedtransaction.annotation.TccClient;
import com.tcc.distributedtransaction.exception.CancelFailException;
import com.tcc.distributedtransaction.exception.TccException;
import com.tcc.distributedtransaction.exception.TryFailException;
import com.tcc.distributedtransaction.model.TccExecutor;
import com.tcc.distributedtransaction.singleton.TccAttributeSingleton;
import com.tcc.distributedtransaction.util.TccConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Aspect
@Component
public class TccClientInterceptor {

    private final Map<String, ExecutorService> executorServiceMap = new HashMap<>();

    @SuppressWarnings("unused")
    @Pointcut("@annotation(com.tcc.distributedtransaction.annotation.TccClient)")
    public void tccClient() {

    }

    @Around("@annotation(tccClient)")
    public Object around(ProceedingJoinPoint pjp, TccClient tccClient) throws Throwable {
        try {
            //TODO: 需要考虑在before和after处理器中添加参数
            return pjp.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof TryFailException) {
                throw throwable;
            } else if (throwable instanceof TccException) {
                log.info("start to rollback");

                if (tccClient.sequential()) {
                    this.checkSequential(tccClient.check(), pjp.getArgs());
                } else {
                    String executorKey = pjp.getSignature().getDeclaringTypeName() + TccConstants.PERIOD + pjp.getSignature().getName();
                    ExecutorService executorService = this.getExecutorService(executorKey);
                    this.cancelMultiple(executorService, tccClient.check(), pjp.getArgs());
                }

                log.info("{} rollback success");
            }
            throw throwable;
        }
    }


    private ExecutorService getExecutorService(String key) {
        ExecutorService executorService = this.executorServiceMap.get(key);
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
            this.executorServiceMap.put(key, executorService);
        }
        return executorService;
    }

    private void checkSequential(String[] checkKeys, Object[] parameters) throws Exception {
        for (String checkKey : checkKeys) {
            TccExecutor tccExecutor = TccAttributeSingleton.getProcessor().getCancelExecutor(checkKey);
            if (tccExecutor != null) {
                tccExecutor.execute(parameters);
            }
        }
    }

    private void cancelMultiple(ExecutorService executorService, String[] checkKeys, Object[] parameters) throws Exception {
        List<Exception> partExceptionList = new ArrayList<>();
        List<Callable<Exception>> checkTaskList = new ArrayList<>();
        for (String checkKey : checkKeys) {
            checkTaskList.add(() -> {
                Exception resultException = null;
                TccExecutor tccExecutor = TccAttributeSingleton.getProcessor().getCancelExecutor(checkKey);
                if (tccExecutor == null) {
                    return resultException;
                }
                try {
                    tccExecutor.execute(parameters);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    resultException = e;
                }
                return resultException;
            });
        }
        try {
            executorService.invokeAll(checkTaskList).forEach(responseVoFuture -> {
                try {
                    if (responseVoFuture.isDone() && responseVoFuture.get() != null) {
                        partExceptionList.add(responseVoFuture.get());
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                } catch (ExecutionException e) {
                    log.error(e.getMessage(), e);
                }
            });
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        if (CollectionUtils.isNotEmpty(partExceptionList)) {
            throw new CancelFailException(partExceptionList);
        }
    }
}
