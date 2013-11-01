package com.aggrepoint.adk.taglib.html;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;

/**
 * 用于在Window页面中构造窗口Action的URL
 * 
 * @author YJM
 */
public class StackTraceTag extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	Throwable t;

	public void setException(Throwable t) {
		this.t = t;
	}

	public int doStartTag() throws JspException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			t.printStackTrace(ps);
			ps.flush();
			pageContext.getOut().print(baos.toString());
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}

		return SKIP_BODY;
	}
}
