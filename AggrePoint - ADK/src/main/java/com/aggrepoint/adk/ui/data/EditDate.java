package com.aggrepoint.adk.ui.data;

import java.util.Date;

import com.icebean.core.adb.ADB;

public class EditDate extends EditText {
	public String m_strFormat;

	public EditDate() {
		m_strType = "date";
		m_strFormat = "yyyy-MM-dd";
	}

	public String getFormat() {
		return m_strFormat;
	}

	/**
	 * 应用格式
	 */
	public Object format(Object obj) {
		if (obj == null)
			return obj;

		if (m_strFormat != null && !m_strFormat.equals("")
				&& Date.class.isAssignableFrom(obj.getClass()))
			return getSimpleDateFormat(m_strFormat).format((Date) obj);

		return obj;
	}

	public void populate(ADB adb, String val) throws Exception {
		if (val == null || val.trim().equals(""))
			m_property.set(adb, null);
		else
			m_property.set(adb, getSimpleDateFormat(m_strFormat).parse(val));
	}
}
