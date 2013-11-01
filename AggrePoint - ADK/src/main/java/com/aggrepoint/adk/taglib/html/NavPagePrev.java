package com.aggrepoint.adk.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

public class NavPagePrev extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		NavPage sel = (NavPage) TagSupport.findAncestorWithClass(this,
				NavPage.class);
		BodyContent body = getBodyContent();
		if (sel.m_iCurr < sel.m_iSt) {
			try {
				body.writeOut(getPreviousOut());
			} catch (IOException e) {
				throw new JspTagException(e.getMessage());
			}
		}

		body.clearBody();
		return SKIP_BODY;
	}
}
