package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

public class TextKeyTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		TextTag text = (TextTag) TagSupport.findAncestorWithClass(this,
				TextTag.class);
		BodyContent body = getBodyContent();
		text.keys.add(body.getString());
		body.clearBody();
		return SKIP_BODY;
	}
}
