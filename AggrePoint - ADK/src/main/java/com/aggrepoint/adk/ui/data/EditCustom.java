package com.aggrepoint.adk.ui.data;

import java.util.regex.Pattern;

public class EditCustom extends Edit {
	static Pattern P_TEXTAREA = Pattern
			.compile("^\\s*<\\s*(textarea|TEXTAREA)\\s*");

	public String m_strContent;
	Boolean m_bIsTextArea;

	public EditCustom() {
		m_strType = "custom";
		m_strContent = "";
	}

	public boolean isTextArea() {
		if (m_bIsTextArea == null) {
			synchronized (P_TEXTAREA) {
				m_bIsTextArea = m_strContent != null
						&& P_TEXTAREA.matcher(m_strContent).find();
			}
		}

		return m_bIsTextArea;
	}

	public String getContent(Object value) {
		if (value == null)
			value = "";

		return m_strContent.replace("$VALUE$", value.toString());
	}
}
