package com.aggrepoint.adk;

import java.util.Enumeration;

/**
 * 属性对象
 * 
 * 可以通过getProperty()的方式获取其属性
 * 
 * @author Owner
 * 
 */
public interface IPropertyObject {
	/** 获取可访问的属性的名称 */
	public Enumeration<String> getPublicPropertyNames();

	/** 获取属性 */
	public String getProperty(String property);

	/** 判断是否存在属性 */
	public boolean hasProperty(String property);
}
