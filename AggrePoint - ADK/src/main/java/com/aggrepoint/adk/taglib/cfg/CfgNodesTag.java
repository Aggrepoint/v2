package com.aggrepoint.adk.taglib.cfg;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.taglib.html.ELFunction;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.xml.MatchElement;

/**
 * 
 */
public class CfgNodesTag extends BodyTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;
	protected static final String FLAG_ALL = "ALL_FLAGS";

	String var;

	MatchElement node;

	String path;

	String flags;

	CfgNodes nodes;

	public void setVar(String var) {
		this.var = var;
	}

	public void setNode(MatchElement node) {
		this.node = node;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public int doStartTag() {
		pageContext.setAttribute(var, null);

		MatchElement elm = null;
		if (node == null)
			elm = ((RetCode) ThreadContext.getAttribute(THREAD_ATTR_RETCODE))
					.getMatchElement();
		else
			elm = node;

		if (elm == null)
			return SKIP_BODY;

		try {
			nodes = new CfgNodes(elm, path);
		} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}

		MatchElement n = nodes.getNextNode(MarkupTag.getMarkupName(pageContext
				.getRequest()));
		while (n != null) {
			String flag = ELFunction.cfgval(n, "flag");
			if (flag == null || flag.trim().equals(""))
				break;
			if (flags != null
					&& (FLAG_ALL.equals(flags) || flags.indexOf(flag) != -1))
				break;
			n = nodes.getNextNode(MarkupTag.getMarkupName(pageContext
					.getRequest()));
		}
		if (n == null)
			return SKIP_BODY;

		pageContext.setAttribute(var, n);
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();
		MatchElement n = nodes.getNextNode(MarkupTag.getMarkupName(pageContext
				.getRequest()));
		while (n != null) {
			String flag = ELFunction.cfgval(n, "flag");
			if (flag == null || flag.trim().equals(""))
				break;
			if (flags != null
					&& (FLAG_ALL.equals(flags) || flags.indexOf(flag) != -1))
				break;
			n = nodes.getNextNode(MarkupTag.getMarkupName(pageContext
					.getRequest()));
		}
		if (n == null)
			return SKIP_BODY;

		pageContext.setAttribute(var, n);
		return EVAL_BODY_BUFFERED;
	}
}
