package com.aggrepoint.ae.taglib.html.btmpl;

import java.util.Enumeration;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;

/**
 * 
 * @author YJM
 * 
 * <ae:eval type="focus" page="page0" name="bbbb"/> <% if (bbbb.booleanValue()) {
 * %>aaa<% } else { %>bbb<% } %>
 */
public class EveluateTag extends TagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strType;

	String m_strPage;

	String m_strName;

	int m_iLevel;

	public EveluateTag() {
		m_strType = "";
		m_strName = null;
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setType(String type) {
		m_strType = type;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);
			ApBPage currentPage = reqInfo.currentPageInTree;

			boolean bResult = false;

			ApBPage page = Utils.getPage(this, pageContext, m_strPage,
					m_iLevel);

			if (m_strType.equals("hassub")) { // 判断指定的页是否包含子页面
				if (page.getSubPages().size() > 0)
					bResult = true;

				if (!bResult) { // 在私有页面中判断
					ApBPage tempPage;

					if (reqInfo.vecPrivatePages != null)
						for (Enumeration<ApBPage> enm = reqInfo.vecPrivatePages
								.elements(); enm.hasMoreElements();) {
							tempPage = enm.nextElement();
							if (tempPage.m_lParentID == page.m_lPageID) {
								bResult = true;
								break;
							}
						}
				}
			} else if (m_strType.equals("current")) { // 判断指定的页是否当前页
				if (currentPage == page)
					bResult = true;
			} else if (m_strType.equals("focus")) { // 判断指定的页是否当前页或当前页的直接上级页面
				while (currentPage != null
						&& currentPage.m_parent != currentPage
						&& currentPage != page)
					currentPage = currentPage.m_parent;
				if (currentPage == page)
					bResult = true;
			} else if (m_strType.equals("first")) { // 用于在tree标记中，判断当前页面是否第一个页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (tt.isFirst())
						bResult = true;
			} else if (m_strType.equals("last")) { // 用于在tree标记中，判断当前页面是否还有后续页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (!tt.hasNext())
						bResult = true;
			}

			pageContext.setAttribute(m_strName, new Boolean(bResult));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
