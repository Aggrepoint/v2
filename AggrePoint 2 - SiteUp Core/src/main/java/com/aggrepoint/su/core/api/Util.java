/*
 * Yang Jiang Ming
 * 
 * 创建日期 2005-8-9
 */
package com.aggrepoint.su.core.api;

import java.util.Enumeration;
import java.util.Vector;

import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBPageLayout;
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.ApBPagePsnTmpl;
import com.aggrepoint.su.core.data.ApBranch;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;

/**
 * @author YJM
 */
public class Util {
	/**
	 * 判断指定路径对应的分支是否存在
	 * 
	 * @param adapter
	 * @param rootPath
	 * @return
	 * @throws Exception
	 */
	public static boolean branchExists(AdbAdapter adapter, String rootPath)
			throws Exception {
		ApBranch branch = new ApBranch();
		branch.m_strRootPath = rootPath;
		return adapter.retrieve(branch, "rootPathExists") != null;
	}

	/**
	 * 复制页面及附带的子页面和内容
	 * 
	 * @param adapter
	 * @param page
	 * @param newBranchID
	 * @throws Exception
	 */
	static void copyPage(AdbAdapter adapter, ApBPage page, long newBranchID)
			throws Exception {
		adapter.retrieve(page, "loadAssoc");

		long pageID = page.m_lPageID;
		long branchID = page.m_lBranchID;

		page.m_lBranchID = newBranchID;
		if (page.m_lPageID == page.m_lParentID)
			adapter.create(page, "createRoot");
		else
			adapter.create(page);

		for (Enumeration<ApBPagePsnName> enm = page.getPsnNames().elements(); enm
				.hasMoreElements();) {
			ApBPagePsnName psnName = enm.nextElement();
			psnName.m_lBranchID = newBranchID;
			psnName.m_lPageID = page.m_lPageID;
			adapter.create(psnName);
		}

		for (Enumeration<ApBPageLayout> enm = page.getLayouts().elements(); enm
				.hasMoreElements();) {
			ApBPageLayout layout = enm.nextElement();
			layout.m_lBranchID = newBranchID;
			layout.m_lPageID = page.m_lPageID;
			adapter.create(layout);
		}

		for (Enumeration<ApBPagePsnTmpl> enm = page.getPsnTmpls().elements(); enm
				.hasMoreElements();) {
			ApBPagePsnTmpl tmpl = enm.nextElement();
			tmpl.m_lBranchID = newBranchID;
			tmpl.m_lPageID = page.m_lPageID;
			adapter.create(tmpl);
		}

		for (Enumeration<ApBPageContent> enm = page.getContents().elements(); enm
				.hasMoreElements();) {
			ApBPageContent cont = enm.nextElement();
			cont.m_lBranchID = newBranchID;
			cont.m_lPageID = page.m_lPageID;
			adapter.create(cont);
		}

		ApBPage sub = new ApBPage();
		sub.m_lParentID = pageID;
		sub.m_lBranchID = branchID;
		Vector<ApBPage> vecSubs = adapter.retrieveMulti(sub,
				"loadByParentNoSub", "default");
		for (Enumeration<ApBPage> enm = vecSubs.elements(); enm
				.hasMoreElements();) {
			sub = enm.nextElement();
			sub.m_lParentID = page.m_lPageID;
			copyPage(adapter, sub, newBranchID);
		}
	}

	/**
	 * 
	 * @param adapter
	 * @param fromPath
	 * @param toPath
	 * @return -1 - 目标路径已经存在 -2 - 源Branch不存在
	 * @throws Exception
	 */
	public static long copyBranch(AdbAdapter adapter, String fromPath,
			String toPath) throws Exception {
		ApBranch branch = new ApBranch();
		branch.m_strRootPath = toPath;
		if (adapter.retrieve(branch, "rootPathExists") != null)
			return -1;

		long lBranchID;

		branch.m_strRootPath = fromPath;
		if (adapter.retrieve(branch, "loadByRootPath") == null)
			return -2;

		lBranchID = branch.m_lBranchID;

		branch.m_strRootPath = toPath;
		adapter.create(branch);

		ApBPage page = new ApBPage();
		page.m_lBranchID = lBranchID;
		if (adapter.retrieve(page, "loadRoot") != null) {
			page.m_strPathName = toPath;
			page.m_strFullPath = "/" + toPath + "/";
			page.m_strDirectPath = "/";
			copyPage(adapter, page, branch.m_lBranchID);
			page.cascadeTemplateAndPath(adapter, false, true, false);
		}

		return branch.m_lBranchID;
	}

	/**
	 * 根据BranchID删除分支
	 * 
	 * @param adapter
	 * @param branchID
	 * @throws Exception
	 */
	public static void deleteBranch(DbAdapter adapter, long branchID)
			throws Exception {
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = branchID;
		adapter.proc(branch, "delete");
	}

	/**
	 * 删除站点中除了指定分支之外的所有分支
	 * 
	 * @param adapter
	 * @param siteid
	 * @param execptBranchID
	 * @throws Exception
	 */
	public static void deleteBranches(DbAdapter adapter, long siteid,
			String[] exceptPaths) throws Exception {
		ApBranch branch = new ApBranch();
		branch.m_lSiteID = siteid;

		if (exceptPaths == null)
			exceptPaths = new String[0];

		Vector<Long> vecIDs = new Vector<Long>();
		for (Enumeration<ApBranch> enm = adapter.retrieveMulti(branch,
				"loadIDAndRootPathBySite", null).elements(); enm
				.hasMoreElements();) {
			branch = enm.nextElement();
			int i = exceptPaths.length - 1;
			for (; i >= 0; i--)
				if (exceptPaths[i].equals(branch.m_strRootPath))
					break;
			if (i >= 0)
				continue;
			vecIDs.add(new Long(branch.m_lBranchID));
		}

		for (Enumeration<Long> enm = vecIDs.elements(); enm.hasMoreElements();)
			deleteBranch(adapter, enm.nextElement().longValue());
	}
}
