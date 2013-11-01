package com.aggrepoint.adk.ui.data;

import com.aggrepoint.adk.IModuleRequest;
import com.icebean.core.adb.ADB;

public class EditNumber extends EditText {
	public String m_strFormat;
	public boolean m_bIgnoreError;

	public EditNumber() {
		m_strType = "number";
	}

	public String getFormat() {
		return m_strFormat;
	}

	public String getIgnoreError() {
		return m_bIgnoreError ? "yes" : "no";
	}

	public void setIgnoreError(String str) {
		m_bIgnoreError = str != null && str.equalsIgnoreCase("yes");
	}

	/**
	 * 应用格式
	 */
	public Object format(Object obj) {
		if (m_strFormat == null || m_strFormat.equals(""))
			return obj;

		if (obj instanceof Short || obj instanceof Integer
				|| obj instanceof Long)
			return getNumberFormat(m_strFormat).format(
					((Number) obj).longValue());

		if (obj instanceof Number)
			return getNumberFormat(m_strFormat).format(
					((Number) obj).doubleValue());

		return obj;
	}

	public void populate(ADB adb, String val) throws Exception {
		try {
			m_property.set(adb, getNumberFormat(m_strFormat).parse(val));
		} catch (Exception e) {
			if (!m_bIgnoreError)
				throw e;
			else
				m_property.set(adb, 0);
		}
	}

	public String getValue(ADB adb, IModuleRequest req) {
		try {
			if (m_bIgnoreError)
				return m_property.get(adb).toString();
		} catch (Exception e) {
		}

		return super.getValue(adb, req);
	}
}
