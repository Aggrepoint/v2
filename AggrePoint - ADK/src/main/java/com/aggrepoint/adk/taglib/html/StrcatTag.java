package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 */
public class StrcatTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	String var;

	String sep;

	String add;

	public void setVar(String var) {
		this.var = var;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}

	public void setStr(String add) {
		this.add = add;
	}

	public int doStartTag() {
		StringBuffer sb;
		Object curr = pageContext.getAttribute(var);
		if (curr == null) {
			sb = new StringBuffer();
			pageContext.setAttribute(var, sb);
		} else if (curr instanceof StringBuffer)
			sb = (StringBuffer) curr;
		else {
			sb = new StringBuffer();
			sb.append(curr.toString());
			pageContext.setAttribute(var, sb);
		}

		if (sep != null && !sep.equals("")) {
			if (sb.length() > 0)
				sb.append(sep);
		}

		sb.append(add);
		return SKIP_BODY;
	}

	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
}
