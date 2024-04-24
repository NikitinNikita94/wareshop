package team.mediasoft.wareshop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Slf4j
@Aspect
public class TransactionExecutionAspect implements TransactionSynchronization {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void registerTransactionExecution() {
        TransactionSynchronizationManager.registerSynchronization(this);
    }

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public void beforeCompletion() {
        startTime.set(System.nanoTime());
    }

    @Override
    public void afterCommit() {
        log.info("Transaction time: {} nano-second", System.nanoTime() - startTime.get());
    }
}
