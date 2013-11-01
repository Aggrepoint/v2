package com.aggrepoint.adk.data;

import com.aggrepoint.adk.DynamicClassLoader;
import com.aggrepoint.adk.ILogger;
import com.aggrepoint.adk.IServerContext;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 日志记录器定义
 * 
 * @author YJM
 */
public class LoggerDef extends ADB {
	public String m_strID;
	public String m_strModule;
	public boolean m_bDefault;
	ILogger m_module;

	public LoggerDef() throws AdbException {
		m_strID = m_strModule = "";
		m_bDefault = false;
		m_module = null;
	}

	public void setDefault(String def) {
		m_bDefault = def.equalsIgnoreCase("y");
	}

	public String getDefault() {
		return m_bDefault ? "y" : "n";
	}

	/**
	 * 获取模块实例
	 */
	public ILogger getLoggerInstance(boolean reload, IServerContext context)
			throws Exception {
		try {
			if (m_module == null || reload) {
				Class<?> c = new DynamicClassLoader(getClass().getClassLoader())
						.loadClass(m_strModule);

				// {构建并初始化Module实例
				m_module = (ILogger) c.newInstance();
				m_module.init(this, context);
				// }
			}
		} catch (Exception e) {
			m_module = null;
			throw e;
		}

		return m_module;
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<logger id=\"");
		pw.print(m_strID);
		pw.print("\" module=\"");
		pw.print(m_strModule);
		pw.print("\" default=\"");
		pw.print(getDefault());
		pw.println("\"/>");
	}
}
