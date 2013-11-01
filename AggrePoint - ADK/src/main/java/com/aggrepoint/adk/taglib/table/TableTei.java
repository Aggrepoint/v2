package com.aggrepoint.adk.taglib.table;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class TableTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo[] info = { new VariableInfo("row",
				TableRowInfo.class.getName(), true, VariableInfo.NESTED) };
		return info;
	}
}
