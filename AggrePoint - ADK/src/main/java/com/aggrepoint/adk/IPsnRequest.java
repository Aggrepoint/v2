/*
 */
package com.aggrepoint.adk;

/**
 * @author administrator
 */
public interface IPsnRequest {
	/** 必须提供的属性：客户端地址 */
	public static final String IP = "ip";
	/** 必须提供的属性：LS */
	public static final String LOCALESET = "localeset";

	/** 获取属性 */
	public String getProperty(String property);
	/** 判断是否存在属性 */
	public boolean hasProperty(String property);
}
