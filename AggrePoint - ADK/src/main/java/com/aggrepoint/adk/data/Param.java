package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.Vector;

import com.aggrepoint.adk.IPsnEngine;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbData;
import com.icebean.core.adb.AdbException;

/**
 * 模块中的参数定义
 * 
 * @author YJM
 */
public class Param extends ADB {
	/** 参数名称 */
	public String m_strName;
	/** 参数值 */
	public String m_strValue;
	/** 个性化规则 */
	public String m_strPsnRule;
	/** 个性化选择 */
	public Vector<Param> m_vecPsnVars;

	public Param() throws AdbException {
		m_strName = m_strValue = m_strPsnRule = "";
	}

	public Vector<Param> getPsnVars() {
		if (m_vecPsnVars == null)
			m_vecPsnVars = new Vector<Param>();
		return m_vecPsnVars;
	}

	public void setPsnVars(Vector<Param> vec) {
	}

	/**
	 * 对象加载前作为Trigger被调用，用于个性化的选项从基本选项上复制基本属性
	 */
	public void setBaseProperties(AdbAdapter adapter, Integer methodType,
			String methodId) {
		try {
			AdbData<Param> data = adapter.getData(this);
			if (data.m_objMaster != null && data.m_objMaster instanceof Param) {
				Param param = data.m_objMaster;

				m_strName = param.m_strName;
				m_strValue = param.m_strValue;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取最合适的个性化定义
	 */
	public Param getPsnParam(IPsnEngine engine) {
		if (m_vecPsnVars != null)
			for (Enumeration<Param> enm = m_vecPsnVars.elements(); enm
					.hasMoreElements();) {
				Param param = enm.nextElement();
				if (param.m_strPsnRule == null || param.m_strPsnRule.equals(""))
					return param;
				if (engine.eveluate(param.m_strPsnRule))
					return param;
			}

		return this;
	}

	public void print(String tab, java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("<");
		pw.print(tag);
		pw.print(" name=\"");
		pw.print(m_strName);
		pw.print("\" value=\"");
		pw.print(m_strValue);
		if (!m_strPsnRule.equals("")) {
			pw.print("\" rule=\"");
			pw.print(m_strPsnRule);
		}
		pw.println("\">");
		for (Enumeration<Param> enm = getPsnVars().elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "psn_param");
		pw.print(tab);
		pw.print("</");
		pw.print(tag);
		pw.println(">");
	}
}
