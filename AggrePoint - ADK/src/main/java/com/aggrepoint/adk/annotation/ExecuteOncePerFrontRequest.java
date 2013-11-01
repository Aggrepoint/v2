package com.aggrepoint.adk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明方法在一次前端请求过程中只需要被执行一次<br>
 * 实现逻辑在WinletModule.execute()和EventHandler.execute()中
 * 
 * @author Owner
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteOncePerFrontRequest {
	ExecuteOnceScope scope() default ExecuteOnceScope.INSTANCE;
}
