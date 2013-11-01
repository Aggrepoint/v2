package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class UIPropTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] {
				new VariableInfo(data.getAttributeString("var"),
						"com.aggrepoint.adk.ui.PropertyInstance", true,
						VariableInfo.NESTED),
				new VariableInfo("propidx", "java.lang.Integer", true,
						VariableInfo.NESTED),
				new VariableInfo("propcount", "java.lang.Integer", true,
						VariableInfo.NESTED),
				new VariableInfo("propfirst", "java.lang.Boolean", true,
						VariableInfo.NESTED),
				new VariableInfo("proplast", "java.lang.Boolean", true,
						VariableInfo.NESTED) };
	}
}
