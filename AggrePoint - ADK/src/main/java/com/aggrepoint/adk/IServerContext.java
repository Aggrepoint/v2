package com.aggrepoint.adk;


/**
 * 模块所运行的服务器环境
 * 
 * @author YJM
 */
public interface IServerContext {
	/** 获取服务器名称 */
	public String getServerName();

	/** 获取全局参数配置 */
	public String getParam(String name);

	/** 获取临时路径，以/结尾 */
	public String getTempDir();

	/** 获取应用根目录，以/结尾 */
	public String getRootDir();

	/**
	 * 获取资源被映射的根路径：只包括ContextRoot，以/开头不以/结尾
	 */
	public String getResourceRootPath();
}
