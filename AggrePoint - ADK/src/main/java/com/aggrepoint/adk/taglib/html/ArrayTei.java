package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.*;

/**
 * 
 */
public class ArrayTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo info1 =
			new VariableInfo(
				data.getAttributeString("name"),
				data.getAttributeString("type"),
				true,
				VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
