package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.UrlConst;

/**
 * 
 * 
 * @author YJM
 */

public class HtmlTag extends BodyTagSupport implements ReqAttrConst, UrlConst {
	static final long serialVersionUID = 0;

	String m_strAttr;

	public HtmlTag() {
		m_strAttr = null;
	}

	public void setAttr(String attr) {
		m_strAttr = attr;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" ");
			if (m_strAttr != null) {
				out.print(" ");
				out.print(m_strAttr);
			}
			out.print(">");

			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.print("</html>");

			return EVAL_PAGE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}
}
