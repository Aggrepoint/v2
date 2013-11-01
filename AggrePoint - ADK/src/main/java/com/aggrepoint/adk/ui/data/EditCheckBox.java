package com.aggrepoint.adk.ui.data;


public class EditCheckBox extends Edit {
	public String m_strOn;
	public String m_strOff;

	public EditCheckBox() {
		m_strType = "checkbox";
		m_strOn = m_strOff = "";
	}

	public Object format(Object obj) {
		if (obj instanceof Boolean)
			return obj;
		if (obj instanceof Long)
			return ((Long) obj).longValue() != 0;
		if (obj instanceof Integer)
			return ((Integer) obj).intValue() != 0;
		if (obj instanceof Short)
			return ((Short) obj).shortValue() != 0;
		return obj;
	}
}
