package com.aggrepoint.adk;

import org.apache.log4j.Priority;

import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.LoggerDef;
import com.aggrepoint.adk.data.RetCode;

/**
 * @author YJM
 */
public interface ILogger {
	/** 初始化 */
	public void init(LoggerDef def, IServerContext context)
			throws WebBaseException;

	/**
	 * 
	 * @param moduleDef
	 * @param module
	 * @param req
	 * @param resp
	 */
	public void log(BaseModuleDef moduleDef, IModule module, RetCode code,
			IModuleRequest req, IModuleResponse resp, boolean skipExecution);

	public void contextLog(Priority priority, IModuleRequest req,
			String message, Throwable exp);
}
