package com.aggrepoint.ae.taglib.html.btmpl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;

/**
 * WHEN
 * 
 * @author YJM
 */
public class WhenTag extends BodyTagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strType;

	String m_strPage;

	int m_iLevel;

	public WhenTag() {
		m_strType = "";
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setType(String type) {
		m_strType = type.toLowerCase();
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public int doStartTag() throws JspException {
		boolean bResult = false;

		try {
			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			ApBPage currentPage = reqInfo.currentPageInTree;
			ApBPage page = Utils
					.getPage(this, pageContext, m_strPage, m_iLevel);

			if (m_strType.equals("current") || m_strType.equals("not_current")) { // 判断指定的页是否当前页
				if (currentPage == page) {
					if (m_strType.equals("current"))
						bResult = true;
				} else {
					if (m_strType.equals("not_current"))
						bResult = true;
				}
			} else if (m_strType.equals("focus")
					|| m_strType.equals("not_focus")) { // 判断指定的页是否当前页或当前页的直接上级页面
				while (currentPage != null
						&& currentPage.m_parent != currentPage
						&& currentPage != page)
					currentPage = currentPage.m_parent;
				if (currentPage == page) {
					if (m_strType.equals("focus"))
						bResult = true;
				} else {
					if (m_strType.equals("not_focus"))
						bResult = true;
				}
			} else if (m_strType.equals("first")
					|| m_strType.equals("not_first")) { // 用于在tree标记中，判断当前页面是否第一个页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null) {
					if (tt.isFirst()) {
						if (m_strType.equals("first"))
							bResult = true;
					} else {
						if (m_strType.equals("not_first"))
							bResult = true;
					}
				}
			} else if (m_strType.equals("last") || m_strType.equals("not_last")) { // 用于在tree标记中，判断当前页面是否还有后续页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null) {
					if (!tt.hasNext()) {
						if (m_strType.equals("last"))
							bResult = true;
					} else {
						if (m_strType.equals("not_last"))
							bResult = true;
					}
				}
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}

		if (bResult)
			return EVAL_BODY_BUFFERED;
		else
			return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();
		return SKIP_BODY;
	}
}
