package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.tagext.*;

/**
 * @author YJM
 */
public class EveluateTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");

		VariableInfo info1 =
			new VariableInfo(
				str,
				"java.lang.Boolean",
				true,
				VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
