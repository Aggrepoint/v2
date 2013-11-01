package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.tagext.*;

/**
 * @author YJM
 */
public class PathTei extends TagExtraInfo {
	public static int m_iIdx = 0;

	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		if (str == null || str.equals("")) {
			str = "AE_ANONYMOUS_PATH_PAGE_" + Integer.toString(m_iIdx++);
		}

		VariableInfo info1 =
			new VariableInfo(
				str,
				"com.aggrepoint.su.core.data.ApBPage",
				true,
				VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
