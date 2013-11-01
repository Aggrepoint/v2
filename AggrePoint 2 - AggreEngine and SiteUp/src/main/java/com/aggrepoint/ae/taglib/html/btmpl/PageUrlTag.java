package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;

/**
 * 构造页面的URL
 * 
 * @author YJM
 */
public class PageUrlTag extends TagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strPage;

	int m_iLevel;

	public PageUrlTag() {
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			ApBPage page = Utils.getPage(this, pageContext, m_strPage,
					m_iLevel);

			if (page != null) {
				out.print(reqInfo.urlConstructor.siteBPage(page));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
