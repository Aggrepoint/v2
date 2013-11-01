package com.aggrepoint.adk.ui.annotation;

public @interface UIProperty {
	String id();

	String name();

	String lsid() default "";
}
