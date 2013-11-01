package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.LangTextProcessor;

public class TextBodyTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();

		try {
			LangTextProcessor.parseToWriter(getPreviousOut(), body.getString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		return SKIP_BODY;
	}
}
