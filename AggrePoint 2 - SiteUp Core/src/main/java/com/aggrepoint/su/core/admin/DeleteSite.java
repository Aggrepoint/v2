package com.aggrepoint.su.core.admin;

import com.aggrepoint.su.core.data.ApSite;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 该程序用于删除站点
 * 
 * @author YJM
 */
public class DeleteSite {
	static DbAdapter m_adapter;

	public static void main(String[] args) {
		long lSiteID;

		try {
			lSiteID = Long.parseLong(args[0]);
		} catch (Exception e) {
			System.out.println("Usage: DeleteSite siteid");
			return;
		}

		try {
			ApSite site = new ApSite();
			m_adapter = new DbAdapter(DBConnManager.getConnection(),
					DBConnManager.getConnectionSyntax());

			site.m_lSiteID = lSiteID;
			if (m_adapter.retrieve(site) == null)
				System.out.println("Site \"" + lSiteID + "\" not found.");
			else {
				System.out.println("Deleting site " + site.m_strSiteName);
				m_adapter.proc(site, "delete");
				System.out.println("Done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}