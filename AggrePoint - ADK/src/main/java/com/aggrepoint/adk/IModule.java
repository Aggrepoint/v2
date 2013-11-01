package com.aggrepoint.adk;

import com.aggrepoint.adk.data.BaseModuleDef;

/**
 * 业务逻辑
 * 
 * @author YJM
 */
public interface IModule {
	/** 初始化 */
	public void init(BaseModuleDef def, IServerContext context)
			throws WebBaseException;

	/** 执行 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Throwable;

	/** 获取实现业务逻辑的对象的class，用于在记录日志时找到正确的配置文件 */
	public Class<?> getImplementationObjectClass();

	/** 获取实现业务逻辑的对象 */
	public Object getImplementationObject();
}
