package com.aggrepoint.ae.taglib.html.btmpl;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;

/**
 * 
 * @author YJM
 */
/*
 * <ae:psn name="bbbb">=(user.isanonymous, F)</ae:psn> <% if
 * (bbbb.booleanValue()) { %>aaa<% } else { %>bbb<% } %>
 */
public class PsnRuleTag extends BodyTagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strName;

	public PsnRuleTag() {
		m_strName = null;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		StringWriter sw = new StringWriter();

		boolean bResult = false;

		try {
			body.writeOut(sw);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();

		String rule = sw.toString();

		RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
				.getAttribute(ATTR_REQUEST_INFO);

		bResult = reqInfo.psnEngine.eveluate(rule);

		pageContext.setAttribute(m_strName, new Boolean(bResult));

		return SKIP_BODY;
	}
}
