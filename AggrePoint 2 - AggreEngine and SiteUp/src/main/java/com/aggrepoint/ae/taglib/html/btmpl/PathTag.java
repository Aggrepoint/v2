package com.aggrepoint.ae.taglib.html.btmpl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApPathMap;

/**
 * 
 * 
 * @author YJM
 */

/*
 * <ae:path level="0" name="page" gap="&nbsp;&nbsp;"><a href="<ae:url
 * page="page"/>"><ae:name page="page"/></a></ae:path>
 */

public class PathTag extends BodyTagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strName;

	String m_strGap;

	ApPathMap m_map;

	Enumeration<ApBPage> m_enum;

	ApBPage m_page;

	public PathTag() {
		m_iLevel = 0;
		m_page = null;
		m_strName = null;
	}

	public void setLevel(int level) {
		if (level >= 0)
			m_iLevel = level;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public void setGap(String gap) {
		m_strGap = gap;
	}

	public ApBPage getPage() {
		return m_page;
	}

	Object nextElement() {
		while (m_enum.hasMoreElements()) {
			m_page = m_enum.nextElement();

			// { 对映射的处理
			if (!m_page.m_bInheritUseMap && !m_page.m_bUseMap)
				return m_page;
			if (m_map == null
					|| ApPathMap.executePattern(m_map.getFromLinkPattern(),
							m_page.m_strFullPath) == null)
				continue;
			// }

			return m_page;
		}

		m_page = null;
		return null;
	}

	public int doStartTag() throws JspException {
		try {
			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);
			m_map = reqInfo.pathMap;
			ApBPage currentInTree = reqInfo.currentPageInTree;

			Vector<ApBPage> vecPages = new Vector<ApBPage>();
			if (currentInTree != null) {
				vecPages.add(currentInTree);
				while (currentInTree.m_parent != null
						&& currentInTree.m_parent != currentInTree) {
					currentInTree = currentInTree.m_parent;
					vecPages.add(currentInTree);
				}
			} else { // 当前页面可能是私有页面
				long lID = reqInfo.page.m_lPageID;
				ApBPage tempPage;
				boolean bFound = false;
				do {
					for (Enumeration<ApBPage> enm = reqInfo.vecPrivatePages
							.elements(); enm.hasMoreElements();) {
						tempPage = enm.nextElement();

						if (tempPage.m_lPageID == lID) {
							vecPages.add(tempPage);
							lID = tempPage.m_lParentID;
							bFound = true;
							break;
						}
					}
				} while (bFound);

				if (vecPages.size() == 0) // 在私有页面中没有找到当前页面
					return SKIP_BODY;

				tempPage = reqInfo.branch.m_rootPage.findPage(lID);
				if (tempPage == null) // 没有在共有页面中没有找到最上层私有页面的父页面
					return SKIP_BODY;

				vecPages.add(tempPage);
				while (tempPage.m_parent != null
						&& tempPage.m_parent != tempPage) {
					tempPage = tempPage.m_parent;
					vecPages.add(tempPage);
				}
			}

			// 将vecPages的顺序调转过来
			Vector<ApBPage> vec = new Vector<ApBPage>();
			for (int i = vecPages.size() - 1 - m_iLevel; i >= 0; i--)
				vec.add(vecPages.elementAt(i));

			m_enum = vec.elements();

			Object next = nextElement();
			if (next == null)
				return SKIP_BODY;

			if (m_strName == null)
				pageContext.setAttribute(m_strName, next);
			return EVAL_BODY_BUFFERED;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();

		Object next = nextElement();
		if (next == null)
			return SKIP_BODY;

		try {
			getPreviousOut().write(m_strGap);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		if (m_strName == null)
			pageContext.setAttribute(m_strName, next);
		return EVAL_BODY_BUFFERED;
	}
}
