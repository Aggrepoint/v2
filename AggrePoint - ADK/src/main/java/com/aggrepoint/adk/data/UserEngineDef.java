package com.aggrepoint.adk.data;

import com.aggrepoint.adk.IUserEngine;
import com.aggrepoint.adk.plugin.SimpleUserEngine;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 用户身份引擎定义
 * 
 * @author YJM
 */
public class UserEngineDef extends ADB {
	public String m_strID;
	public String m_strModule;
	public boolean m_bDefault;
	IUserEngine m_engine;

	public UserEngineDef() throws AdbException {
		m_strID = "";
		m_strModule = SimpleUserEngine.class.getName();
		m_bDefault = false;
	}

	public String getDefault() {
		return m_bDefault ? "y" : "n";
	}

	public void setDefault(String str) {
		if (str != null)
			m_bDefault = str.equalsIgnoreCase("y");
	}

	public IUserEngine getEngineInstance() throws Exception {
		if (m_engine == null)
			m_engine = (IUserEngine) Class.forName(m_strModule).newInstance();
		return m_engine;
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<user id=\"");
		pw.print(m_strID);
		pw.print("\" module=\"");
		pw.print(m_strModule);
		pw.print("\" default=\"");
		pw.print(getDefault());
		pw.println("\"/>");
	}
}
