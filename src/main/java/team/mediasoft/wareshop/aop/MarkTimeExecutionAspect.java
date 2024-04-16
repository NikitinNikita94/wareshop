package team.mediasoft.wareshop.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class MarkTimeExecutionAspect {

    @Around("@annotation(team.mediasoft.wareshop.aop.MarkTime)")
    public Object markTimeExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        log.info("Метод с названием={}, отработал {} ms", joinPoint.getSignature().getName(), stopWatch.getTime());
        return result;
    }

}
