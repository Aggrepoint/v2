package com.aggrepoint.adk.ui.data;


public class EditText extends Edit {
	public int m_iWidth;
	public int m_iMaxLength;
	public String m_strFormat;

	public EditText() {
		m_strType = "text";
		m_iWidth = 10;
		m_iMaxLength = 10;
	}

	public int getWidth() {
		return m_iWidth;
	}

	public int getMaxLength() {
		return m_iMaxLength;
	}

	public String getFormat() {
		return m_strFormat;
	}
}
