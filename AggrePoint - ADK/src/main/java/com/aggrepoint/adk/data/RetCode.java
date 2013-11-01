package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.Vector;

import org.w3c.dom.Document;

import com.aggrepoint.adk.EnumPresentMethod;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IPsnEngine;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbData;
import com.icebean.core.adb.AdbException;
import com.icebean.core.xml.MatchElement;

/**
 * 返回码定义
 * 
 * @author YJM
 */
public class RetCode extends ADB {
	/** 返回码编号 */
	public int m_iID;

	/** 对应的异常。若该属性不为空字符串，则表示该响应码是异常响应码定义，m_iID属性忽略 */
	public String m_strThrow;

	/** 级别 */
	public int m_iLevel;

	/** 系统信息 */
	public String m_strLogMessage;

	/** 用户信息 */
	public String m_strUserMessage;

	/** 展示方法 */
	public EnumPresentMethod m_pMethod;

	/**
	 * 是否允许Cache。该属性会影响在HTTP响应头中是否会放入禁止Cache相关的头，
	 * 而且会被Ajax版的WindowAction方法用于判断执行动作后是否需要重新加载窗口内容
	 */
	public boolean m_bCache;

	/** URL */
	public String m_strUrl;

	/** 下一个状态 */
	public String m_strState;

	/** 新的窗口模式，用于切换窗口大小状态 */
	public EnumWinMode m_winMode;

	/** 窗口的标题 */
	public String m_strWinTitle;

	/** 需要同时更新的窗口。用于实现Ajax，指明执行了一个操作后会受到影响的其他窗口 */
	public String m_strUpdate;

	public String m_strEnsureVisible;

	/** 是否要显示在对话框中 */
	public boolean m_bDialog;

	/** 个性化规则 */
	public String m_strPsnRule;

	/** 指定使用的Logger。值为空表示根据响应码级别来取得Logger，为null表示不记日志，其他表示使用指定的Logger */
	public String m_strLogger;

	/** 个性化选择 */
	public Vector<RetCode> m_vecPsnVars;

	/** 异常类 */
	Class<?> m_thrownClass;

	public Document m_docSelf;

	MatchElement m_matchElement;

	public RetCode() throws AdbException {
		m_iID = 0;
		m_iLevel = 0;
		m_pMethod = EnumPresentMethod.NOTDEFINED;
		m_strLogMessage = m_strUserMessage = m_strUrl = m_strState = m_strPsnRule = m_strThrow = m_strLogger = m_strWinTitle = m_strUpdate = m_strEnsureVisible = "";
		m_winMode = EnumWinMode.NOCHANGE;
		m_bCache = false;
	}

	public String getId() {
		return Integer.toString(m_iID);
	}

	public void setId(String str) {
		try {
			m_iID = Integer.parseInt(str);
		} catch (Exception e) {
			m_iID = 0;
		}
	}

	public String getLevel() {
		return Integer.toString(m_iLevel);
	}

	public void setLevel(String str) {
		try {
			m_iLevel = Integer.parseInt(str);
		} catch (Exception e) {
			m_iLevel = 0;
		}
	}

	public String getLogMessage() {
		return m_strLogMessage;
	}

	public void setLogMessage(String str) {
		if (str == null || str.equals(""))
			return;
		m_strLogMessage = str;
	}

	public String getUserMessage() {
		return m_strUserMessage;
	}

	public void setUserMessage(String str) {
		if (str == null || str.equals(""))
			return;
		m_strUserMessage = str;
	}

	public void setMethod(String str) {
		if (str.equals("")) {
			if (m_pMethod == EnumPresentMethod.NOTDEFINED)
				m_pMethod = EnumPresentMethod.NOACTION;
		} else
			m_pMethod = EnumPresentMethod.fromKey(str);
	}

	public String getMethod() {
		return m_pMethod.getKey();
	}

	public String getUrl() {
		return m_strUrl;
	}

	public void setUrl(String str) {
		if (str == null || str.equals(""))
			return;
		m_strUrl = str;
		if (m_pMethod == EnumPresentMethod.NOTDEFINED
				|| m_pMethod == EnumPresentMethod.NOACTION)
			m_pMethod = EnumPresentMethod.FORWARD;
	}

	public String getLogger() {
		return m_strLogger;
	}

	public void setLogger(String str) {
		if (str == null || str.equals(""))
			return;
		if (str.equalsIgnoreCase("null"))
			m_strLogger = null;
		else
			m_strLogger = str;
	}

	public String getState() {
		return m_strState;
	}

	public void setState(String str) {
		if (str == null || str.equals(""))
			return;
		m_strState = str;
	}

	public String getWinMode() {
		return m_winMode.getName();
	}

	public void setWinMode(String str) {
		m_winMode = EnumWinMode.fromName(str);
	}

	public String getWinTitle() {
		return m_strWinTitle;
	}

	public void setWinTitle(String str) {
		if (str == null || str.equals(""))
			return;
		m_strWinTitle = str;
	}

	public String getUpdate() {
		return m_strUpdate;
	}

	public void setUpdate(String str) {
		if (str == null || str.equals(""))
			return;
		m_strUpdate = str;
	}

	public String getEnsureVisible() {
		return m_strEnsureVisible;
	}

	public void setEnsureVisible(String str) {
		if (str == null || str.equals(""))
			return;
		m_strEnsureVisible = str;
	}

	public Vector<RetCode> getPsnVars() {
		if (m_vecPsnVars == null)
			m_vecPsnVars = new Vector<RetCode>();
		return m_vecPsnVars;
	}

	public void setPsnVars(Vector<RetCode> vec) {
	}

	public String getCache() {
		return m_bCache ? "y" : "n";
	}

	public void setCache(String str) {
		if (str != null
				&& (str.equalsIgnoreCase("y") || str.equalsIgnoreCase("yes")))
			m_bCache = true;
		else
			m_bCache = false;
	}

	public String getDialog() {
		return m_bDialog ? "y" : "n";
	}

	public void setDialog(String str) {
		if (str != null
				&& (str.equalsIgnoreCase("y") || str.equalsIgnoreCase("yes")))
			m_bDialog = true;
		else
			m_bDialog = false;
	}

	/**
	 * 对象加载前作为Trigger被调用，用于个性化的选项从基本选项上复制基本属性
	 */
	public void setBaseProperties(AdbAdapter adapter, Integer methodType,
			String methodId) {
		try {
			AdbData<RetCode> data = adapter.getData(this);
			if (data.m_objMaster != null && data.m_objMaster instanceof RetCode) {
				RetCode retCode = data.m_objMaster;
				m_iID = retCode.m_iID;
				m_strThrow = retCode.m_strThrow;
				m_iLevel = retCode.m_iLevel;
				m_strLogMessage = retCode.m_strLogMessage;
				m_strUserMessage = retCode.m_strUserMessage;
				m_pMethod = retCode.m_pMethod;
				m_strUrl = retCode.m_strUrl;
				m_winMode = retCode.m_winMode;
				m_strUpdate = retCode.m_strUpdate;
				m_strEnsureVisible = retCode.m_strEnsureVisible;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取最合适的个性化定义
	 */
	public RetCode getPsnRetCode(IPsnEngine engine) {
		if (m_vecPsnVars != null)
			for (Enumeration<RetCode> enm = m_vecPsnVars.elements(); enm
					.hasMoreElements();) {
				RetCode retCode = enm.nextElement();
				if (engine.eveluate(retCode.m_strPsnRule))
					return retCode;
			}

		return this;
	}

	public Class<?> getThrowClass() {
		if (m_thrownClass == null)
			if (!m_strThrow.equals(""))
				try {
					m_thrownClass = Class.forName(m_strThrow);
				} catch (Exception e) {
					e.printStackTrace();
				}

		return m_thrownClass;
	}

	public MatchElement getMatchElement() {
		if (m_matchElement == null)
			m_matchElement = MatchElement.parse(m_docSelf.getDocumentElement());
		return m_matchElement;
	}

	public void print(String tab, java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("<");
		pw.print(tag);
		pw.print(" id=\"");
		pw.print(getId());
		if (!m_strThrow.equals("")) {
			pw.print("\" throw=\"");
			pw.print(m_strThrow);
		}
		pw.print("\" level=\"");
		pw.print(m_iLevel);
		if (!getLogMessage().equals("")) {
			pw.print("\" lmsg=\"");
			pw.print(getLogMessage());
		}
		if (!getUserMessage().equals("")) {
			pw.print("\" umsg=\"");
			pw.print(getUserMessage());
		}
		if (!getMethod().equals("")) {
			pw.print("\" method=\"");
			pw.print(getMethod());
		}
		if (!getUrl().equals("")) {
			pw.print("\" url=\"");
			pw.print(getUrl());
		}
		if (!getState().equals("")) {
			pw.print("\" state=\"");
			pw.print(getState());
		}
		if (!m_strThrow.equals("")) {
			pw.print("\" rule=\"");
			pw.print(m_strPsnRule);
		}
		if (!m_strLogger.equals("")) {
			pw.print("\" logger=\"");
			pw.print(m_strLogger);
		}
		if (m_winMode != EnumWinMode.NOCHANGE) {
			pw.print("\" winmode=\"");
			pw.print(m_winMode.getName());
		}
		if (!m_strUpdate.equals("")) {
			pw.print("\" update=\"");
			pw.print(m_strUpdate);
		}
		if (!m_strEnsureVisible.equals("")) {
			pw.print("\" ensurevisible=\"");
			pw.print(m_strEnsureVisible);
		}
		if (getCache().equals("y"))
			pw.print("\" cache=\"y");
		pw.println("\">");
		for (Enumeration<RetCode> enm = getPsnVars().elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "psn_retcode");
		pw.print(tab);
		pw.print("</");
		pw.print(tag);
		pw.println(">");
	}
}
