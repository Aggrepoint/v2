package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class ContTmplTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { new VariableInfo(data
				.getAttributeString("var"), "com.aggrepoint.adk.taglib.html.ContTmpl", true,
				VariableInfo.AT_END) };
	}
}
