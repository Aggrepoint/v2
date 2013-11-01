package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * 
 */
public class IncludeTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		if (data.getAttributeString("var") != null)
			return new VariableInfo[] { new VariableInfo(data
					.getAttributeString("var"), "java.lang.String", true,
					VariableInfo.AT_END) };

		return new VariableInfo[] { new VariableInfo(data
				.getAttributeString("vars"), "java.util.Vector", true,
				VariableInfo.AT_END) };
	}
}
