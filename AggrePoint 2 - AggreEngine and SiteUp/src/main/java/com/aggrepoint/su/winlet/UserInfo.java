package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApSite;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 当前用户信息
 * 
 * @author YJM
 */
public class UserInfo extends WinletBase {
	private static final long serialVersionUID = 1L;

	public String m_strSiteName;

	public int showInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (req.getUserProfile().isAnonymous())
			return 10;

		long siteId = getSiteId(req);
		if (m_strSiteName == null) {
			if (siteId <= 0)
				m_strSiteName = "";
			else {
				DbAdapter adapter = new DbAdapter(req.getDBConn());
				ApSite site = new ApSite();
				site.m_lSiteID = siteId;
				adapter.retrieve(site);
				m_strSiteName = site.m_strSiteName;
			}
		}

		return 0;
	}

	/**
	 * 退出登录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int logout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		clearUserProfile(resp);
		return 0;
	}
}
