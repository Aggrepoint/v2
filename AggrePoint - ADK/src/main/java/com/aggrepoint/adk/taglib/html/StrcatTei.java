package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class StrcatTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo info1 = new VariableInfo(data.getAttributeString("var"),
				"java.lang.StringBuffer", true, VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
