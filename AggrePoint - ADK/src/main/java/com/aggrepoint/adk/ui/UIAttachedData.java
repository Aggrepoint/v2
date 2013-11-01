package com.aggrepoint.adk.ui;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.ui.data.Property;
import com.aggrepoint.adk.ui.data.UIConfig;

/**
 * UI Adapter附加在ADB上的数据
 * 
 * @author Owner
 * 
 */
public class UIAttachedData {
	/** 错误信息 */
	private Hashtable<Property, Vector<String>> m_htErrorMsgs;
	/** 导致错误的值 */
	private Hashtable<Property, String> m_htErrorValues;
	/** 标记 */
	private HashSet<String> m_hsFlags;
	/** 对象配置 */
	public UIConfig m_config;

	public UIAttachedData(UIConfig config) {
		m_config = config;
	}

	public Vector<String> getErrorMsgs(Property property) {
		if (m_htErrorMsgs == null)
			m_htErrorMsgs = new Hashtable<Property, Vector<String>>();
		Vector<String> err = m_htErrorMsgs.get(property);
		if (err == null) {
			err = new Vector<String>();
			m_htErrorMsgs.put(property, err);
		}
		return err;
	}

	public void clearErrorMsgs(Property property) {
		if (m_htErrorMsgs == null)
			return;
		m_htErrorMsgs.remove(property);
	}

	public String getErrorValue(Property property) {
		if (m_htErrorValues == null)
			return null;
		return m_htErrorValues.get(property);
	}

	public void setErrorValue(Property property, String val) {
		if (m_htErrorValues == null)
			if (val == null)
				return;
			else
				m_htErrorValues = new Hashtable<Property, String>();
		if (val == null)
			m_htErrorValues.remove(property);
		else
			m_htErrorValues.put(property, val);
	}

	public UIAttachedData clearErrors() {
		m_htErrorMsgs = null;
		m_htErrorValues = null;
		return this;
	}

	public HashSet<String> getFlags() {
		if (m_hsFlags == null)
			m_hsFlags = new HashSet<String>();
		return m_hsFlags;
	}

	public void clearFlags() {
		m_hsFlags = null;
	}
}
