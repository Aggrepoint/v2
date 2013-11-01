package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.JspException;

import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.taglib.html.ELFunction;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.xml.MatchElement;

/**
 * 
 */
public class CfgValTag extends CfgNodesTag {
	private static final long serialVersionUID = 1L;

	String attr;

	String def;

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public void setDefault(String def) {
		this.def = def;
	}

	public int doStartTag() {
		pageContext.setAttribute(var, def);

		MatchElement elm = null;
		if (node == null)
			elm = ((RetCode) ThreadContext.getAttribute(THREAD_ATTR_RETCODE))
					.getMatchElement();
		else
			elm = node;

		if (elm == null)
			return SKIP_BODY;

		try {
			MatchElement n = new CfgNodes(elm, path).getNextNode(MarkupTag
					.getMarkupName(pageContext.getRequest()));
			if (n == null)
				return SKIP_BODY;

			String val = ELFunction.cfgval(n, attr);
			if (val != null && !val.equals(""))
				pageContext.setAttribute(var, val);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}

	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
}
