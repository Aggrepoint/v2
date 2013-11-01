package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 返回码级别定义
 * 
 * @author YJM
 */
public class RetCodeLevelDef extends ADB {
	public static final int DB_ROLLBACK = 0;
	public static final int DB_COMMIT = 1;
	public static final int DB_NO_ACTION = 2;

	public String m_strID;
	public String m_strDesc;
	public int m_iDBAction;
	public boolean m_bDefault;
	public Vector<RetCodeLevelLoggerDef> m_vecLoggers;

	public void setDefault(String def) {
		m_bDefault = def.equalsIgnoreCase("y");
	}

	public String getDefault() {
		return m_bDefault ? "y" : "n";
	}

	public void setCommitDB(String str) {
		if (str.equalsIgnoreCase("c"))
			m_iDBAction = DB_COMMIT;
		else if (str.equalsIgnoreCase("n"))
			m_iDBAction = DB_NO_ACTION;
		else
			m_iDBAction = DB_ROLLBACK;
	}

	public String getCommitDB() {
		switch (m_iDBAction) {
		case DB_COMMIT:
			return "c";
		case DB_NO_ACTION:
			return "n";
		default:
			return "r";
		}
	}

	public Vector<RetCodeLevelLoggerDef> getLoggers() {
		if (m_vecLoggers == null)
			m_vecLoggers = new Vector<RetCodeLevelLoggerDef>();
		return m_vecLoggers;
	}

	public void setLoggers(Vector<RetCodeLevelLoggerDef> vec) {
	}

	public RetCodeLevelDef() throws AdbException {
		m_strID = "";
		m_strDesc = "";
		m_iDBAction = 0;
		m_bDefault = false;
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<level id=\"");
		pw.print(m_strID);
		pw.print("\" desc=\"");
		pw.print(getDefault());
		pw.print("\" db=\"");
		pw.print(getDefault());
		pw.print("\" default=\"");
		pw.print(getDefault());
		pw.println("\">");
		for (Enumeration<RetCodeLevelLoggerDef> enm = getLoggers().elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw);
		pw.print(tab);
		pw.println("</level>");
	}
}
