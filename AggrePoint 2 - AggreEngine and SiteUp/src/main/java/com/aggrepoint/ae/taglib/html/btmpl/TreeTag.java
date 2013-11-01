package com.aggrepoint.ae.taglib.html.btmpl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApPathMap;
import com.aggrepoint.su.core.data.RuleConst;

/**
 * 
 * 
 * @author YJM
 */

/*
 * <ae:tree level="0" name="page0"> <ae:name page="page0"/> <ae:url
 * page="page0"/> <ae:tree root="page0" name="page1"> <ae:name page="page1"/>
 * </ae:tree> </ae:tree>
 */

public class TreeTag extends BodyTagSupport implements ReqAttrConst, RuleConst {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strRoot;

	String m_strName;

	Enumeration<ApBPage> m_enum;

	Enumeration<ApBPage> m_enumPrivate;

	ApBPage m_page;

	ApBPage m_pagePreFetch;

	String m_strGap;

	/** 是否第一个页面 */
	boolean m_bFirst;

	RequestInfo reqInfo;

	public TreeTag() {
		m_iLevel = -1;
		m_strRoot = null;
		m_strName = null;
		m_page = m_pagePreFetch = null;

	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setRoot(String root) {
		m_strRoot = root;
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

	/**
	 * 是否还有下一个页面
	 * 
	 * @return
	 */
	public boolean hasNext() {
		ApBPage page = m_page;
		m_pagePreFetch = nextElement();
		m_page = page;
		return m_pagePreFetch != null ? true : false;
	}

	/**
	 * 是否第一个页面
	 * 
	 * @return
	 */
	public boolean isFirst() {
		return m_bFirst;
	}

	ApBPage nextElement() {
		if (m_pagePreFetch != null) {
			m_page = m_pagePreFetch;
			m_pagePreFetch = null;
			return m_page;
		}

		while (m_enum.hasMoreElements()) {
			m_page = m_enum.nextElement();

			// { 对映射的处理
			if (m_page.m_bInheritUseMap || m_page.m_bUseMap) {
				if (reqInfo.pathMap == null
						|| ApPathMap.executePattern(
								reqInfo.pathMap.getFromLinkPattern(),
								m_page.m_strFullPath) == null)
					continue;
			}
			// }

			if (reqInfo.bIsRoot)
				return m_page;

			if (m_page.m_bHide)
				continue;
			if (m_page.ownerIs(reqInfo.strUserID))
				return m_page;
			if (reqInfo.psnEngine.eveluate(m_page.m_strAccessRule))
				return m_page;
		}
		while (m_enumPrivate != null && m_enumPrivate.hasMoreElements()) {
			m_page = m_enumPrivate.nextElement();
			if (reqInfo.bIsRoot)
				return m_page;
			if (m_page.m_bHide)
				continue;
			if (m_page.ownerIs(reqInfo.strUserID))
				return m_page;
			if (reqInfo.psnEngine.eveluate(m_page.m_strAccessRule))
				return m_page;
		}
		m_page = null;
		return null;
	}

	public int doStartTag() throws JspException {
		try {
			reqInfo = (RequestInfo) pageContext.getRequest().getAttribute(
					ATTR_REQUEST_INFO);

			ApBPage page = null;
			ApBPage currentInTree = reqInfo.currentPageInTree;

			if (m_strRoot != null) // 直接指定了要展现的根栏目
				page = (ApBPage) pageContext.getAttribute(m_strRoot);
			else if (m_iLevel > -1) { // 指定了要展现的级别
				if (m_iLevel <= currentInTree.m_iLevel) {
					page = currentInTree;
					while (m_iLevel < page.m_iLevel)
						page = page.m_parent;
				}
			} else { // 没有指定根栏目
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					page = tt.getPage();
				else
					page = reqInfo.branch.m_rootPage;
			}

			if (page == null)
				return SKIP_BODY;

			m_enum = page.getSubPages().elements();

			// {找出私有子页面
			m_enumPrivate = null;
			if (reqInfo.vecPrivatePages != null) {
				Vector<ApBPage> vec = new Vector<ApBPage>();
				ApBPage tempPage;

				for (Enumeration<ApBPage> enm = reqInfo.vecPrivatePages
						.elements(); enm.hasMoreElements();) {
					tempPage = enm.nextElement();
					if (tempPage.m_lParentID == page.m_lPageID) {
						tempPage.m_iLevel = page.m_iLevel + 1;
						tempPage.m_parent = page;
						vec.add(tempPage);
					}
				}

				m_enumPrivate = vec.elements();
			}
			// }

			Object next = nextElement();
			if (next == null)
				return SKIP_BODY;

			m_bFirst = true;

			if (m_strName != null)
				pageContext.setAttribute(m_strName, next);
			return EVAL_BODY_BUFFERED;
		} catch (Exception e) {
			e.printStackTrace();
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

		m_bFirst = false;

		try {
			if (m_strGap != null)
				body.write(m_strGap);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		if (m_strName != null)
			pageContext.setAttribute(m_strName, next);
		return EVAL_BODY_BUFFERED;
	}
}
