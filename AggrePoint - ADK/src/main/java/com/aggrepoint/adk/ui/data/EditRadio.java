package com.aggrepoint.adk.ui.data;


public class EditRadio extends EditSelect {
	boolean m_bBreak;

	public EditRadio() {
		m_strType = "radio";
	}

	public String getBreak() {
		return m_bBreak ? "yes" : "no";
	}

	public void setBreak(String str) {
		m_bBreak = str != null && str.equalsIgnoreCase("yes");
	}

	public boolean isBreak() {
		return m_bBreak;
	}
}
