package com.aggrepoint.adk.ui.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.IModuleRequest;
import com.icebean.core.adb.ADB;

public class Edit extends Matchable {
	/** 所有Edit公用的数字格式化类 */
	protected static Hashtable<String, DecimalFormat> m_htNumFormat = new Hashtable<String, DecimalFormat>();
	/** 所有Edit公用的日期格式化类 */
	protected static Hashtable<String, SimpleDateFormat> m_htDateFormat = new Hashtable<String, SimpleDateFormat>();

	public String m_strType;
	public Property m_property;
	public Vector<Error> m_vecErrors;

	public Edit() {
		m_strType = "";
		m_vecErrors = new Vector<Error>();
	}

	public String getType() {
		return m_strType;
	}

	/**
	 * 获取数字格式化类
	 * 
	 * @param format
	 * @return
	 */
	public static DecimalFormat getNumberFormat(String format) {
		synchronized (m_htNumFormat) {
			DecimalFormat fmt = m_htNumFormat.get(format);
			if (fmt != null)
				return fmt;

			fmt = new DecimalFormat(format);
			m_htNumFormat.put(format, fmt);
			return fmt;
		}

	}

	/**
	 * 获取日期格式化类
	 * 
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String format) {
		synchronized (m_htDateFormat) {
			SimpleDateFormat fmt = m_htDateFormat.get(format);
			if (fmt != null)
				return fmt;

			fmt = new SimpleDateFormat(format);
			m_htDateFormat.put(format, fmt);
			return fmt;
		}
	}

	/**
	 * 对值进行格式化
	 * 
	 * @param obj
	 * @return
	 */
	public Object format(Object obj) {
		return obj;
	}

	public String getError(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Error error = Matchable.match(m_vecErrors, markup, flags, flags1,
				flags2, flags3);
		if (error == null)
			return null;
		return error.m_strError;
	}

	/**
	 * 从请求中取值
	 * 
	 * @param adb
	 * @param req
	 * @throws Exception
	 */
	public void populate(ADB adb, IModuleRequest req) throws Exception {
		populate(adb, req.getParameter(m_property.getId()));
	}

	/**
	 * 设置值
	 * 
	 * @param adb
	 * @param req
	 * @throws Exception
	 */
	public void populate(ADB adb, String value) throws Exception {
		m_property.set(adb, value);
	}

	public String getValue(ADB adb, IModuleRequest req) {
		return req.getParameter(m_property.getId());
	}
}
