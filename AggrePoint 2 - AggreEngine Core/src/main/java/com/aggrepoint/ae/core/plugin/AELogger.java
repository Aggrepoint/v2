package com.aggrepoint.ae.core.plugin;

import org.apache.log4j.Category;

import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.data.WinletActionDef;
import com.aggrepoint.adk.data.WinletStateDef;
import com.aggrepoint.adk.plugin.WinletLogger;
import com.aggrepoint.ae.core.Tools;
import com.icebean.core.common.Log4jIniter;

public class AELogger extends WinletLogger {
	public void log(BaseModuleDef moduleDef, IModule module, RetCode code,
			IModuleRequest req, IModuleResponse resp, boolean skipExecution) {
		if (moduleDef instanceof WinletStateDef
				|| moduleDef instanceof WinletActionDef) {
			super.log(moduleDef, module, code, req, resp, skipExecution);
			return;
		}

		Category log = Log4jIniter.getCategory(
				module.getImplementationObjectClass(), getClass());

		StringBuffer sb = new StringBuffer();
		sb.append("| ");
		sb.append(m_strServerName);
		sb.append(" | ");
		sb.append(reqFormat.format(req.getRequestID()));
		sb.append(" | ");
		sb.append(timeFormat.format(System.currentTimeMillis()
				- req.getCreateTime()));
		sb.append(" | ");
		sb.append(codeFormat.format(resp.getRetCode()));
		sb.append(" | ");
		sb.append(req.getRemoteAddr());
		sb.append(" | ");
		sb.append(Tools.getRemoteAddr(req));
		addUser(sb, req);
		sb.append(" | ");
		sb.append(req.getControlPath());
		sb.append(req.getRequestPath());
		addMethodAndPresent(sb, moduleDef, module, code, resp, skipExecution);
		sb.append(" | ");
		sb.append(resp.getLogMessage() == null ? "" : resp.getLogMessage());
		addParams(sb, moduleDef, req);
		sb.append(" | ");
		Throwable t = resp.getRetThrow();
		sb.append(t == null ? "" : t.toString());
		sb.append(" | ");
		String str = req.getHeader("Referer");
		sb.append(str == null ? "" : str);
		addCookies(sb, req);
		sb.append(" | ");
		str = req.getHeader("User-Agent");
		sb.append(str == null ? "" : str);

		log.info(sb.toString());
		if (t instanceof Exception)
			t.printStackTrace();
	}
}
