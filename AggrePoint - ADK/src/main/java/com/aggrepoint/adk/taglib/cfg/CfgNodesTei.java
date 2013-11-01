package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class CfgNodesTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo info1 = new VariableInfo(data.getAttributeString("var"),
				"com.icebean.core.xml.MatchElement", true, VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
