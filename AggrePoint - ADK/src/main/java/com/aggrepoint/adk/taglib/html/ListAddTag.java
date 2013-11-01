package com.aggrepoint.adk.taglib.html;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * @author YJM
 */
public class ListAddTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	String list;

	String scope;

	Object value;

	public void setList(String list) {
		this.list = list;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		Vector<Object> vec = null;

		if (scope == null || scope.equalsIgnoreCase("page"))
			vec = (Vector<Object>) pageContext.getAttribute(list);
		else if (scope.equalsIgnoreCase("request"))
			vec = (Vector<Object>) pageContext.getRequest().getAttribute(list);
		else if (scope.equalsIgnoreCase("application"))
			vec = (Vector<Object>) pageContext.getServletContext()
					.getAttribute(list);
		else
			vec = (Vector<Object>) pageContext.getAttribute(list);

		if (vec == null) {
			vec = new Vector<Object>();
			if (scope == null || scope.equalsIgnoreCase("page"))
				pageContext.setAttribute(list, vec);
			else if (scope.equalsIgnoreCase("request"))
				pageContext.getRequest().setAttribute(list, vec);
			else if (scope.equalsIgnoreCase("application"))
				pageContext.getServletContext().setAttribute(list, vec);
			else
				pageContext.setAttribute(list, vec);
		}
		vec.add(value);
		return (SKIP_BODY);
	}
}
