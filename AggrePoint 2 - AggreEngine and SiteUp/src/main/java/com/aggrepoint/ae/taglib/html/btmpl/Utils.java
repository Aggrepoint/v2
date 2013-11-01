package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.su.core.data.ApBPage;

/**
 * @author YJM
 */
public class Utils implements ReqAttrConst {
	/**
	 * 找到与当前标记相关的页面
	 * 
	 * @param tag
	 * @param context
	 * @param strPage
	 * @param iLevel
	 * @return
	 */
	public static ApBPage getPage(TagSupport tag, PageContext context,
			String strPage, int iLevel) {
		ApBPage page = null;

		RequestInfo reqInfo = (RequestInfo) context.getRequest().getAttribute(
				ATTR_REQUEST_INFO);

		if (strPage != null) {
			if (strPage.equals("AE_ROOT"))
				page = reqInfo.branch.m_rootPage;
			else
				page = (ApBPage) context.getAttribute(strPage);
		} else if (iLevel >= 0) {
			ApBPage currentInTree = reqInfo.currentPageInTree;
			if (iLevel <= currentInTree.m_iLevel) {
				page = currentInTree;
				while (iLevel < page.m_iLevel)
					page = page.m_parent;
			}
		} else {
			TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(tag,
					TreeTag.class);
			if (tt != null)
				page = tt.getPage();
			else {
				PathTag pt = (PathTag) TagSupport.findAncestorWithClass(tag,
						PathTag.class);
				if (pt != null)
					page = pt.getPage();
			}
		}

		if (page == null)
			page = reqInfo.page;

		return page;
	}
}
