package com.javandroid.hotelbookingsystem.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //Log execution time of all Service methods
    @Around("execution(* com.javandroid.hotelbookingsystem.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;

        logger.info("Method {} executed in {} ms", joinPoint.getSignature(), timeTaken);
        return result;
    }

    //Log when any method in Service layer is called
    @Before("execution(* com.javandroid.hotelbookingsystem.service.*.*(..))")
    public void logBeforeServiceMethod() {
        logger.info("A service method was called.");
    }

    //Log when any Repository method is called
    @Before("execution(* com.javandroid.hotelbookingsystem.repository.*.*(..))")
    public void logBeforeRepositoryMethod() {
        logger.info("A repository method was called.");
    }

    //Log exceptions in Service methods
    @AfterThrowing(pointcut = "execution(* com.javandroid.hotelbookingsystem.service.*.*(..))", throwing = "ex")
    public void logServiceException(Exception ex) {
        logger.error("An error occurred in Service Layer: {}", ex.getMessage());
    }
}
