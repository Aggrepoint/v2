package com.aggrepoint.adk.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 
 */
public class ArrayTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String name;

	private Object[] array;

	int idx;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
	}

	public void setArray(Object[] arr) {
		array = arr;
		idx = 0;
	}

	public int doStartTag() {
		if (array == null)
			return SKIP_BODY;
		if (array.length == 0)
			return SKIP_BODY;
		pageContext.setAttribute(name, array[idx++]);
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
		if (idx < array.length) {
			pageContext.setAttribute(name, array[idx++]);
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}
}
