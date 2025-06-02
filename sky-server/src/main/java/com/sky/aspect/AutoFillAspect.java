package com.sky.aspect;

import com.sky.anno.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFillAspect {
     @Before("@annotation(com.sky.anno.AutoFill)")
     public void autoFill(JoinPoint  joinPoint) {
          log.info("开始进行数据填充");
          //获取目标方法上的注解，并拿到注解里的值
          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          OperationType operationType = signature.getMethod().getAnnotation(AutoFill.class).value();
          //获取当前方法参数
          Object[] args = joinPoint.getArgs();
           if (args == null || args.length == 0) {
               return;
          }
           Object object = args[0];
           try {
                //判断操作类型，如果 为insert，则进行填充四个 字段
                 if (operationType == OperationType.INSERT) {
                    //为插入操作的字段赋值
                    //setCreateTime
                     Method setCreateTime = object.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                      setCreateTime.invoke(object, LocalDateTime.now());
                     //setUpdateTime
                     Method setUpdateTime = object.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                     setUpdateTime.invoke(object, LocalDateTime.now());
                     //setCreateUser
                     Method setCreateUser = object.getClass().getDeclaredMethod("setCreateUser",  Long.class);
                     setCreateUser.invoke(object, BaseContext.getCurrentId());
                    //setUpdateUser
                     Method setUpdateUser = object.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                     setUpdateUser.invoke(object, BaseContext.getCurrentId());
                 } else if (operationType == OperationType.UPDATE) {
                    //为更新操作的字段赋值
                    //setUpdateTime
                     Method setUpdateTime = object.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                     setUpdateTime.invoke(object, LocalDateTime.now());
                    //setUpdateUser
                     Method setUpdateUser = object.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                     setUpdateUser.invoke(object, BaseContext.getCurrentId());
                }
           } catch (Exception e) {
               throw new RuntimeException(e);
           }

     }
}
