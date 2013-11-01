/*
 * 创建日期 2005-12-11
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.aggrepoint.su.core.admin;

import java.util.Enumeration;

import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApCPage;
import com.aggrepoint.su.core.data.ApChannel;
import com.aggrepoint.su.core.data.ApContCat;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * @author administrator
 * 
 *         更改所生成类型注释的模板为 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class UUIDInit {
	public static void main(String[] args) {
		try {
			DbAdapter adapter = new DbAdapter(DBConnManager.getConnection(),
					DBConnManager.getConnectionSyntax());

			ApContent sc = new ApContent();
			for (Enumeration<ApContent> enm = adapter.retrieveMulti(sc)
					.elements(); enm.hasMoreElements();) {
				sc = enm.nextElement();
				if (sc.m_strUUID == null || sc.m_strUUID.equals("")) {
					sc.m_strUUID = UUIDGen.get();
					adapter.update(sc);
					System.out.println("site content " + sc.m_strName);
				}
			}

			ApTemplate st = new ApTemplate();
			for (Enumeration<ApTemplate> enm = adapter.retrieveMulti(st)
					.elements(); enm.hasMoreElements();) {
				st = enm.nextElement();
				if (st.m_strUUID == null || st.m_strUUID.equals("")) {
					st.m_strUUID = UUIDGen.get();
					adapter.update(st);
					System.out.println("site template " + st.m_strTmplName);
				}
			}

			ApLayout sl = new ApLayout();
			for (Enumeration<ApLayout> enm = adapter.retrieveMulti(sl)
					.elements(); enm.hasMoreElements();) {
				sl = enm.nextElement();
				if (sl.m_strUUID == null || sl.m_strUUID.equals("")) {
					sl.m_strUUID = UUIDGen.get();
					adapter.update(sl);
					System.out.println("site layout " + sl.m_strLayoutName);
				}
			}

			ApFrame sf = new ApFrame();
			for (Enumeration<ApFrame> enm = adapter.retrieveMulti(sf)
					.elements(); enm.hasMoreElements();) {
				sf = enm.nextElement();
				if (sf.m_strUUID == null || sf.m_strUUID.equals("")) {
					sf.m_strUUID = UUIDGen.get();
					adapter.update(sf);
					System.out.println("site frame " + sf.m_strFrameName);
				}
			}

			ApApp app = new ApApp();
			for (Enumeration<ApApp> enm = adapter.retrieveMulti(app).elements(); enm
					.hasMoreElements();) {
				app = enm.nextElement();
				if (app.m_strUUID == null || app.m_strUUID.equals("")) {
					app.m_strUUID = UUIDGen.get();
					adapter.update(app);
					System.out.println("app " + app.m_strAppName);
				}
			}

			ApWindow win = new ApWindow();
			for (Enumeration<ApWindow> enm = adapter.retrieveMulti(win)
					.elements(); enm.hasMoreElements();) {
				win = enm.nextElement();
				if (win.m_strUUID == null || win.m_strUUID.equals("")) {
					win.m_strUUID = UUIDGen.get();
					adapter.update(win);
					System.out.println("win " + win.m_strName);
				}
			}

			ApSite site = new ApSite();
			for (Enumeration<ApSite> enm = adapter.retrieveMulti(site)
					.elements(); enm.hasMoreElements();) {
				site = enm.nextElement();
				if (site.m_strUUID == null || site.m_strUUID.equals("")) {
					site.m_strUUID = UUIDGen.get();
					adapter.update(site);
					System.out.println("site " + site.m_strSiteName);
				}
			}

			ApBranch branch = new ApBranch();
			for (Enumeration<ApBranch> enm = adapter.retrieveMulti(branch)
					.elements(); enm.hasMoreElements();) {
				branch = enm.nextElement();
				if (branch.m_strUUID == null || branch.m_strUUID.equals("")) {
					branch.m_strUUID = UUIDGen.get();
					adapter.update(branch);
					System.out.println("branch " + branch.m_strBranchName);
				}
			}

			ApBPage bpage = new ApBPage();
			for (Enumeration<ApBPage> enm = adapter.retrieveMulti(bpage)
					.elements(); enm.hasMoreElements();) {
				bpage = enm.nextElement();
				if (bpage.m_strUUID == null || bpage.m_strUUID.equals("")) {
					bpage.m_strUUID = UUIDGen.get();
					adapter.update(bpage);
					System.out.println("bpage " + bpage.m_strPageName);
				}
			}

			ApChannel channel = new ApChannel();
			for (Enumeration<ApChannel> enm = adapter.retrieveMulti(channel)
					.elements(); enm.hasMoreElements();) {
				channel = enm.nextElement();
				if (channel.m_strUUID == null || channel.m_strUUID.equals("")) {
					channel.m_strUUID = UUIDGen.get();
					adapter.update(channel);
					System.out.println("channel " + channel.m_strChannelName);
				}
			}

			ApCPage cpage = new ApCPage();
			for (Enumeration<ApCPage> enm = adapter.retrieveMulti(cpage)
					.elements(); enm.hasMoreElements();) {
				cpage = enm.nextElement();
				if (cpage.m_strUUID == null || cpage.m_strUUID.equals("")) {
					cpage.m_strUUID = UUIDGen.get();
					adapter.update(cpage);
					System.out.println("bpage " + cpage.m_strTitle);
				}
			}

			ApContCat contcat = new ApContCat();
			for (Enumeration<ApContCat> enm = adapter.retrieveMulti(contcat)
					.elements(); enm.hasMoreElements();) {
				contcat = enm.nextElement();
				if (contcat.m_strUUID == null || contcat.m_strUUID.equals("")) {
					contcat.m_strUUID = UUIDGen.get();
					adapter.update(contcat);
					System.out.println("contcat " + contcat.m_strContCatName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
