package com.aggrepoint.adk.ui.data;


public class EditSubSel extends Edit {
	public String m_strParent;

	public EditSubSel() {
		m_strType = "subsel";
		m_strParent = "";
	}
	
	public String getParent() {
		return m_strParent;
	}
}
