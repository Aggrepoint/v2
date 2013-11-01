package com.aggrepoint.adk.plugin;

import org.apache.log4j.Priority;

import com.aggrepoint.adk.ILogger;
import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.WebBaseException;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.LoggerDef;
import com.aggrepoint.adk.data.RetCode;

/**
 * 不输出任何东西的Logger
 * 
 * @author administrator
 */
public class NullLogger implements ILogger {
	public void init(LoggerDef def, IServerContext context)
			throws WebBaseException {
	}

	public void log(BaseModuleDef moduleDef, IModule module, RetCode code,
			IModuleRequest req, IModuleResponse resp, boolean skipExecution) {
	}

	public void contextLog(Priority priority, IModuleRequest req,
			String message, Throwable exp) {
	}
}
