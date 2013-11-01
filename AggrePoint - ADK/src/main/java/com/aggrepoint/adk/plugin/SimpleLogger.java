package com.aggrepoint.adk.plugin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import com.aggrepoint.adk.ILogger;
import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.WebBaseException;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.LogParam;
import com.aggrepoint.adk.data.LoggerDef;
import com.aggrepoint.adk.data.RetCode;
import com.icebean.core.common.Log4jIniter;

/**
 * 简单的日志
 * 
 * @author YJM
 */
public class SimpleLogger implements ILogger {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static DecimalFormat timeFormat = new DecimalFormat("0000000");
	static DecimalFormat codeFormat = new DecimalFormat("00000");
	String m_strServerName;
	protected Category m_contextLog;

	/** 初始化 */
	public void init(LoggerDef def, IServerContext context)
			throws WebBaseException {
		m_strServerName = context.getServerName();
		m_contextLog = Log4jIniter.getCategory(context.getClass());
	}

	public void log(BaseModuleDef moduleDef, IModule module, RetCode code,
			IModuleRequest req, IModuleResponse resp, boolean skipExecution) {
		Category log = Log4jIniter.getCategory(module
				.getImplementationObjectClass(), getClass());

		String user = "";
		try {
			IUserProfile userProfile = req.getUserProfile();
			if (userProfile.isAnonymous())
				user = "(anonymouse)";
			else
				user = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		} catch (Exception ee) {
		}

		Throwable t = resp.getRetThrow();

		StringBuffer sb = new StringBuffer();
		sb.append("| ");
		sb.append(m_strServerName);
		sb.append(" | ");
		sb.append(sdf.format(new java.util.Date()));
		sb.append(" | ");
		sb.append(timeFormat.format(System.currentTimeMillis()
				- req.getCreateTime()));
		sb.append(" | ");
		sb.append(codeFormat.format(resp.getRetCode()));
		sb.append(" | ");
		sb.append(req.getRemoteAddr());
		sb.append(" | ");
		sb.append(user);
		sb.append(" | ");
		sb.append(req.getControlPath());
		sb.append(req.getRequestPath());
		sb.append(" | ");
		sb.append(resp.getLogMessage() == null ? "" : resp.getLogMessage());
		sb.append(" | ");

		LogParam[] logParams = moduleDef.getLogParams();
		if (logParams != null)
			for (int i = 0; i < logParams.length; i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(logParams[i].m_strName);
				sb.append("=");
				sb.append(req.getParameter(logParams[i].m_strName, ""));
			}
		sb.append(" | ");
		sb.append(t == null ? "" : t.toString());
		sb.append(" | ");

		String str = req.getHeader("Referer");
		sb.append(str == null ? "" : str);
		sb.append(" | ");

		Cookie[] ck = ((HttpServletRequest) req.getRequestObject())
				.getCookies();
		if (ck != null)
			for (int i = 0; i < ck.length; i++) {
				sb.append(ck[i].getName());
				sb.append("=");
				sb.append(ck[i].getValue());
				sb.append(";");
			}
		else
			sb.append(" ");

		sb.append(" | ");

		str = req.getHeader("User-Agent");
		sb.append(str == null ? "" : str);

		log.info(sb.toString());
		if (t instanceof Exception)
			t.printStackTrace();
	}

	public void contextLog(Priority priority, IModuleRequest req,
			String message, Throwable exp) {
		String user = "";
		try {
			IUserProfile userProfile = req.getUserProfile();
			if (userProfile.isAnonymous())
				user = "(anonymouse)";
			else
				user = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		} catch (Exception ee) {
		}

		StringBuffer sb = new StringBuffer();
		sb.append(message);
		sb.append(" | ");
		sb.append(m_strServerName);
		sb.append(" | ");
		sb.append(sdf.format(new java.util.Date()));
		sb.append(" | ");
		sb.append(timeFormat.format(System.currentTimeMillis()
				- req.getCreateTime()));
		sb.append(" | ");
		sb.append(req.getRemoteAddr());
		sb.append(" | ");
		sb.append(user);
		sb.append(" | ");
		sb.append(req.getControlPath());
		sb.append(req.getRequestPath());
		sb.append(" | ");

		String str = req.getHeader("Referer");
		sb.append(str == null ? "" : str);
		sb.append(" | ");

		Cookie[] ck = ((HttpServletRequest) req.getRequestObject())
				.getCookies();
		if (ck != null)
			for (int i = 0; i < ck.length; i++) {
				sb.append(ck[i].getName());
				sb.append("=");
				sb.append(ck[i].getValue());
				sb.append(";");
			}
		else
			sb.append(" ");

		sb.append(" | ");

		str = req.getHeader("User-Agent");
		sb.append(str == null ? "" : str);

		if (exp != null)
			m_contextLog.log(priority, sb.toString(), exp);
		else
			m_contextLog.log(priority, sb.toString());
	}
}
