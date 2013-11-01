package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;

/**
 * IF
 * 
 * @author YJM
 */
public class IfTag extends TagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strType;

	String m_strPage;

	String m_strThen;

	String m_strElse;

	int m_iLevel;

	public IfTag() {
		m_strType = m_strThen = m_strElse = "";
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setType(String type) {
		m_strType = type;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setThen(String then) {
		m_strThen = then;
	}

	public void setElse(String str) {
		m_strElse = str;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);
			ApBPage currentPage = reqInfo.currentPageInTree;

			ApBPage page = Utils.getPage(this, pageContext, m_strPage,
					m_iLevel);

			if (m_strType.equals("current")) { // 判断指定的页是否当前页
				if (currentPage == page)
					out.print(m_strThen);
				else
					out.print(m_strElse);
			} else if (m_strType.equals("focus")) { // 判断指定的页是否当前页或当前页的直接上级页面
				while (currentPage != null
						&& currentPage.m_parent != currentPage
						&& currentPage != page)
					currentPage = currentPage.m_parent;
				if (currentPage == page)
					out.print(m_strThen);
				else
					out.print(m_strElse);
			} else if (m_strType.equals("first")) { // 用于在tree标记中，判断当前页面是否第一个页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (tt.isFirst())
						out.print(m_strThen);
					else
						out.print(m_strElse);
			} else if (m_strType.equals("last")) { // 用于在tree标记中，判断当前页面是否还有后续页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (!tt.hasNext())
						out.print(m_strThen);
					else
						out.print(m_strElse);
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
