package com.aggrepoint.adk;


/**
 * 常量定义
 * 
 * @author YJM
 */
public interface IAdkConst {
	/** 系统响应码：初始化失败 */
	public static final int ERROR_INIT_FAILED = 9001;
	/** 系统响应码：找不到要访问的资源 */
	public static final int ERROR_NOT_FOUND = 9002;
	/** 系统响应码：预处理失败 */
	public static final int ERROR_PRE_EXECUTE = 9003;
	/** 系统响应码：展现失败 */
	public static final int ERROR_PRESENT = 9004;
	/** 系统响应码：匿名用户不能访问 */
	public static final int ERROR_NOT_AUTHENTIC = 8001;
	/** 系统响应码：无权访问 */
	public static final int ERROR_NOT_AUTHORIZED = 8002;

	/** 进行展示之前，将响应码放入线程环境时使用的键值 */
	public static final String THREAD_ATTR_RETCODE = "com.aggrepoint.adk.retcode";
	/** 进行展示之前，将模块放入线程环境时用的键值 */
	public static final String THREAD_ATTR_MODULE = "com.aggrepoint.adk.module";
	/** 进行展示之前，将请求对象放入线程环境时使用的键值 */
	public static final String THREAD_ATTR_REQUEST = "com.aggrepoint.adk.request";
	/** 进行展示之前，将异常放入线程环境时使用的键值 */
	public static final String THREAD_ATTR_EXCEPTION = "com.aggrepoint.adk.exception";

	public static final Class<?>[] MODULE_METHOD_PARAM_TYPE = {
			IModuleRequest.class, IModuleResponse.class };
}
