package com.aggrepoint.adk.taglib.html;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.IAdkConst;

/**
 * 
 * @author YJM
 */
public class ContTmplTag extends BodyTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String var;

	public void setVar(String var) {
		this.var = var;
	}

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(baos);
			body.writeOut(out);
			out.flush();
			pageContext.setAttribute(var, new ContTmpl(baos.toString()));
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		return (SKIP_BODY);
	}
}
