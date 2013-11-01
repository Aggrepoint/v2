package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class ListAddTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { new VariableInfo(data
				.getAttributeString("list"), "java.util.Vector", true,
				VariableInfo.AT_END) };
	}
}
