package com.aggrepoint.ae.taglib.html.btmpl;

import java.util.Enumeration;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.RuleConst;

/**
 * 计算在指定栏目下包含多少个子栏目
 * 
 * @author YJM
 */
/*
 * <ae:subcount level="0"/>
 * 
 * <ae:tree level="0" name="page0"> <ae:tree root="page0" name="page1">
 * <ae:subcount page="page1"/> </ae:tree> </ae:tree>
 * 
 * <ae:subcount level="0" name="ccc"/> <%= ccc * 3 %>
 */
public class SubCountTag extends TagSupport implements ReqAttrConst, RuleConst {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strPage;

	String m_strName;

	public SubCountTag() {
		m_strPage = null;
		m_strName = null;
		m_iLevel = -1;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			int count = 0;

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			boolean bIsRoot = reqInfo.viewMode == EnumViewMode.EDIT
					&& reqInfo.psnEngine.eveluate(SU_ROOT);

			ApBPage page = null;
			ApBPage currentInTree = reqInfo.currentPageInTree;

			if (m_strPage != null) // 直接指定了要展现的根栏目
				page = (ApBPage) pageContext.getAttribute(m_strPage);
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

			if (page != null) {
				for (Enumeration<ApBPage> enm = page.getSubPages().elements(); enm
						.hasMoreElements();) {
					ApBPage m_page = enm.nextElement();
					if (bIsRoot) {
						count++;
						continue;
					}

					if (m_page.m_bHide)
						continue;
					if (m_page.ownerIs(reqInfo.userProfile
							.getProperty(IUserProfile.PROPERTY_ID))) {
						count++;
						continue;
					}
					if (reqInfo.psnEngine.eveluate(m_page.m_strAccessRule)) {
						count++;
						continue;
					}
				}
			}

			// {找出私有子页面
			if (reqInfo.vecPrivatePages != null) {
				ApBPage tempPage;

				for (Enumeration<ApBPage> enm = reqInfo.vecPrivatePages
						.elements(); enm.hasMoreElements();) {
					tempPage = enm.nextElement();
					if (tempPage.m_lParentID == page.m_lPageID) {
						tempPage.m_iLevel = page.m_iLevel + 1;
						tempPage.m_parent = page;
						count++;
					}
				}
			}
			// }

			if (m_strName == null)
				out.print(count);
			else
				pageContext.setAttribute(m_strName, new Integer(count));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
