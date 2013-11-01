package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.JspException;

/**
 * 
 */
public class CfgNodeTag extends CfgNodesTag {
	private static final long serialVersionUID = 1L;

	public CfgNodeTag() {
		flags = FLAG_ALL;
	}

	public int doStartTag() {
		return super.doStartTag();
	}

	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
}
