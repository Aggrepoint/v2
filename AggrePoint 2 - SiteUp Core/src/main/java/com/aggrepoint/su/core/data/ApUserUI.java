package com.aggrepoint.su.core.data;

import java.util.Hashtable;
import java.util.StringTokenizer;

import com.icebean.core.adb.db.DbAdapter;

public class ApUserUI extends LogoUI {
	static Hashtable<String, String> RIGHTS = new Hashtable<String, String>();

	static {
		RIGHTS.put("root", "超级管理");

		RIGHTS.put("site", "站点管理");
		RIGHTS.put("branch", "分支管理");
		RIGHTS.put("app", "应用管理");
		RIGHTS.put("tmpl", "模板管理");
		RIGHTS.put("frame", "窗框管理");
		RIGHTS.put("cont", "内容管理");
		RIGHTS.put("layout", "布局管理");
		RIGHTS.put("res", "资源管理");
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// rights
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayRights(ApUser app) {
		if (app.m_strRights == null)
			return "";

		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(app.m_strRights, ", ");
		boolean bFirst = true;
		while (st.hasMoreTokens()) {
			String right = RIGHTS.get(st.nextToken());
			if (right != null) {
				if (bFirst)
					bFirst = false;
				else
					sb.append(", ");
				sb.append(right);
			}
		}
		return sb.toString();
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// sites
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplaySites(ApUser app, DbAdapter adapter)
			throws Exception {
		if (app.m_strSites == null)
			return "";

		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(app.m_strSites, ", ");
		ApSite site = new ApSite();
		boolean bFirst = true;
		while (st.hasMoreTokens()) {
			site.m_lSiteID = Long.parseLong(st.nextToken());
			if (adapter.retrieve(site, "loadWithCache") != null) {
				if (bFirst)
					bFirst = false;
				else
					sb.append(", ");

				sb.append(site.m_strSiteName);
			}
		}

		return sb.toString();
	}
}
