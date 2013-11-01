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
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.RuleConst;

/**
 * 输出页面名称
 * 
 * @author YJM
 */
public class PageNameTag extends TagSupport implements ReqAttrConst, RuleConst {
	static final long serialVersionUID = 0;

	String m_strPage;

	int m_iLevel;

	boolean m_bShowMenu;

	public PageNameTag() {
		m_strPage = null;
		m_iLevel = -1;
		m_bShowMenu = true;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setMenu(String menu) {
		if (menu == null)
			return;

		if (menu.equalsIgnoreCase("n") || menu.equalsIgnoreCase("no")
				|| menu.equalsIgnoreCase("0"))
			m_bShowMenu = false;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			ApBPage page = Utils
					.getPage(this, pageContext, m_strPage, m_iLevel);

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			if (page != null) {
				ApBPagePsnName name = null;

				if (reqInfo.htPrivateNames != null
						&& reqInfo.htPrivateNames.size() != 0) // 从私有个性化名称中查找
					name = reqInfo.htPrivateNames.get(new Long(page.m_lPageID));
				else if (page.m_vecPsnNames != null) { // 从统一个性化名称中查找
					for (Enumeration<ApBPagePsnName> enm = page.m_vecPsnNames
							.elements(); enm.hasMoreElements();) {
						name = enm.nextElement();
						if (reqInfo.psnEngine.eveluate(name.m_strAccessRule))
							break;
						name = null;
					}
				}

				String strName = name == null ? page.m_strPageName
						: name.m_strPageName;

				if (reqInfo.viewMode == EnumViewMode.EDIT && m_bShowMenu) {
					// 编辑模式下，判断Context菜单的类型
					String menuName = null;

					if (reqInfo.psnEngine.eveluate(SU_ROOT)
							|| reqInfo.psnEngine
									.eveluate(reqInfo.branch.m_strManageRule)) { // 分支管理员
						menuName = "page_2";
					} else {
						if (page.ownerIs(reqInfo.psnEngine.getUserProfile()
								.getProperty(IUserProfile.PROPERTY_ID))) { // 私有页面拥有者
							menuName = "page_3";
						} else {
							boolean bIsClbPsn = reqInfo.psnEngine
									.eveluate(page.m_strClbPsnRule);
							boolean bIsPvtPsn = reqInfo.psnEngine
									.eveluate(page.m_strPvtPsnRule);

							if (bIsClbPsn && bIsPvtPsn) // 符合协作和私有个性化规则
								menuName = "page_4";
							else if (bIsClbPsn) // 仅符合协作个性化规则
								menuName = "page_5";
							else if (bIsPvtPsn) // 仅符合私有个性化规则
								menuName = "page_6";
						}
					}

					if (menuName != null) {
						out.print("<span class=\"ae_page menuid#");
						out.print(menuName);
						out.print("# pid#");
						out.print(page.m_lPageID);
						out.print("# pname#");
						out.print(page.m_strPageName);
						out.print("#\">");
					}
					out.print(strName);
					if (menuName != null)
						out.print("</span>");
				} else
					out.print(strName);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
