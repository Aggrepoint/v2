package com.aggrepoint.adk.ui.data;


public class EditTextArea extends Edit {
	public int m_iRows;
	public int m_iCols;

	public EditTextArea() {
		m_strType = "textarea";
		m_iRows = 10;
		m_iCols = 10;
	}

	public int getRows() {
		return m_iRows;
	}

	public int getCols() {
		return m_iCols;
	}
}
