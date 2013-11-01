package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author YJM
 */
public class TreeTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		if (str == null || str.equals(""))
			return new VariableInfo[0];

		VariableInfo info1 = new VariableInfo(str,
				"com.aggrepoint.su.core.data.ApBPage", true,
				VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
