package com.aggrepoint.adk.taglib.html;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TextTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	Vector<String> keys = new Vector<String>();

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		return SKIP_BODY;
	}
}
