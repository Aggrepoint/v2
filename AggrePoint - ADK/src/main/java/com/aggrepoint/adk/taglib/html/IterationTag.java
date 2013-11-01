package com.aggrepoint.adk.taglib.html;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 
 */
public class IterationTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String name;

	private Iterator<?> iterator;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
	}

	public void setGroup(Collection<?> members) {
		if (members != null && members.size() > 0)
			iterator = members.iterator();
	}

	public int doStartTag() {
		if (iterator == null)
			return SKIP_BODY;
		if (iterator.hasNext()) {
			pageContext.setAttribute(name, iterator.next());
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}

	public int doAfterBody() throws JspException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();
		if (iterator.hasNext()) {
			pageContext.setAttribute(name, iterator.next());
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}
}
