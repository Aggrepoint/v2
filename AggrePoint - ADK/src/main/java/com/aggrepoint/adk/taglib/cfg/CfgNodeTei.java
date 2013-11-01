package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class CfgNodeTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo info1 = new VariableInfo(data.getAttributeString("var"),
				"com.icebean.core.xml.MatchElement", true, VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
