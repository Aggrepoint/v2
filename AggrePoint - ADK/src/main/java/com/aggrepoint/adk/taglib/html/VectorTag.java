package com.aggrepoint.adk.taglib.html;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 
 */
public class VectorTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String name;

	private Vector<?> vector;

	Enumeration<?> enm;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
	}

	public void setVector(Vector<?> vec) {
		vector = vec;
		if (vector != null)
			enm = vector.elements();
	}

	public int doStartTag() {
		if (vector == null)
			return SKIP_BODY;
		if (!enm.hasMoreElements())
			return SKIP_BODY;
		pageContext.setAttribute(name, enm.nextElement());
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
		if (enm.hasMoreElements()) {
			pageContext.setAttribute(name, enm.nextElement());
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}
}
