package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class CfgValTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo info1 = new VariableInfo(data.getAttributeString("var"),
				"java.lang.String", true, VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
