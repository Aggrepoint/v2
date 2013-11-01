package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.aggrepoint.adk.IPsnEngine;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbData;
import com.icebean.core.adb.AdbException;
import com.icebean.core.common.StringUtils;
import com.icebean.core.msg.MessageBoundle;

/**
 * 文本信息定义
 * 
 * @author YJM
 */
public class Message extends ADB {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();
	/** 信息 */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	/** 文本名称 */
	public String m_strName;
	/** 文本值 */
	public String m_strText;
	/** 是否已经过初始处理 */
	boolean m_bPrepared;
	/** 个性化规则 */
	public String m_strPsnRule;
	/** 个性化选择 */
	public Vector<Message> m_vecPsnVars;

	public Message() throws AdbException {
		m_strName = m_strText = m_strPsnRule = "";
		m_bPrepared = false;
	}

	public Vector<Message> getPsnVars() {
		if (m_vecPsnVars == null)
			m_vecPsnVars = new Vector<Message>();
		return m_vecPsnVars;
	}

	public void setPsnVars(Vector<Message> col) {
	}

	/**
	 * 准备...
	 */
	void prepare() {
		if (m_bPrepared)
			return;

		m_bPrepared = true;

		int iParamStart, iParamEnd;
		String strName = m_strName;
		String strText = m_strText;

		strName = MessageBoundle.reform(strName);

		iParamStart = strName.indexOf('(');
		iParamEnd = strName.indexOf(')');

		if (iParamStart == -1 && iParamEnd == -1) // 不带参数的信息定义
			return;

		if (iParamEnd < iParamStart) { // 只有开始括号没有结束括号
			m_log.error(m_msg.constructMessage("noEndBracket", m_strName));
			return;
		}

		if (iParamEnd != (strName.length() - 1)) { // 结束括号不在最后
			m_log
					.error(m_msg.constructMessage("endBracketNotAtEnd",
							m_strName));
			return;
		}

		String messageID = strName.substring(0, iParamStart).trim();
		String strParams = strName.substring(iParamStart + 1, iParamEnd).trim();
		if (strParams.equals("")) { // 括号内没有参数
			m_log.error(m_msg.constructMessage("noParameter", m_strName));
			return;
		}

		if (strParams.charAt(0) == ','
				|| strParams.charAt(strParams.length() - 1) == ',') {
			// 逗号前、后没有参数名称
			m_log.error(m_msg.constructMessage("parameterError", m_strName));
			return;
		}

		StringTokenizer st = new StringTokenizer(strParams, " ,");
		String strParam;
		int iCount = 0;

		while (st.hasMoreTokens()) {
			strParam = st.nextToken();

			if (m_strText.indexOf("(" + strParam + ")") == -1) { // 没有引用参数
				m_log.error(m_msg.constructMessage("parameterNotReferenced",
						m_strText, strParam));
				return;
			}

			strText = StringUtils.replaceString(strText, "(" + strParam + ")",
					"(" + iCount + ")");
			iCount++;
		}

		m_strName = messageID;
		m_strText = strText;
	}

	/**
	 * 对象加载前作为Trigger被调用，用于个性化的选项从基本选项上复制基本属性
	 */
	public void setBaseProperties(AdbAdapter adapter, Integer methodType,
			String methodId) {
		try {
			AdbData<Message> data = adapter.getData(this);
			if (data.m_objMaster != null && data.m_objMaster instanceof Message) {
				Message param = data.m_objMaster;

				m_strName = param.m_strName;
				m_strText = param.m_strText;
			}
		} catch (Exception e) {
		}
	}

	public String getName() {
		if (!m_bPrepared)
			prepare();

		return m_strName;
	}

	public String constructMessage(String[] inPlaceString) {
		if (!m_bPrepared)
			prepare();

		return MessageBoundle.constructMessageStatic(m_strText, inPlaceString);
	}

	/**
	 * 获取最合适的个性化定义
	 */
	public Message getPsnMessage(IPsnEngine engine) {
		if (m_vecPsnVars != null)
			for (Enumeration<Message> enm = m_vecPsnVars.elements(); enm
					.hasMoreElements();) {
				Message message = enm.nextElement();
				if (engine.eveluate(message.m_strPsnRule))
					return message;
			}

		return this;
	}

	public void print(String tab, java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("<");
		pw.print(tag);
		pw.print(" name=\"");
		pw.print(m_strName);
		pw.print("\" text=\"");
		pw.print(m_strText);
		pw.print("\" rule=\"");
		pw.print(m_strPsnRule);
		pw.println("\">");
		for (Enumeration<Message> enm = getPsnVars().elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "psn_msg");
		pw.print(tab);
		pw.print("</");
		pw.print(tag);
		pw.println(">");
	}
}
