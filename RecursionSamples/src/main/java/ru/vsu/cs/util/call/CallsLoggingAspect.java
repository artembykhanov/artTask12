package ru.vsu.cs.util.call;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Класс, реализующий аспект AspectJ
 */
@Aspect
public class CallsLoggingAspect {

    /**
     * Метод, который вызывается вокруг методов помеченных CallLogging аннотацией
     * (требуется правильная сборка проекта с помощью Maven с дополнительными плагинами)
     */
    @Around(value = "execution(* *(..)) && @annotation(CallsLogging)")
    public Object aroundCallMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        CallsLoggingUtils.enter(joinPoint.getSignature().getName(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            CallsLoggingUtils.exit(result);
            return result;
        } catch (Throwable th) {
            CallsLoggingUtils.exit(th);
            throw th;
        }
    }
}
