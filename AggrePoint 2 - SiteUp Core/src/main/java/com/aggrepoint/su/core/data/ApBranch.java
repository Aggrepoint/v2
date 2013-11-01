package com.aggrepoint.su.core.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;

/**
 * 
 * 同时提供已加载站点分支的Cache功能
 * 
 * @author YJM
 */
public class ApBranch extends ADB {
	/** 静态个性化 */
	public static final int PSN_TYPE_STATIC = 0;

	/** 动态个性化 */
	public static final int PSN_TYPE_DYNAMIC = 1;

	/** 组合分支，用来将多个分支组合在一起 */
	public static final int PSN_TYPE_GROUP = 2;

	public long m_lBranchID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;

	public long m_lSiteID;

	public int m_iPsnType;

	public String m_strBranchName;

	public String m_strBranchDesc;

	public String m_strAccessRule;

	public String m_strManageRule;

	public String m_strClbPsnRule;

	public String m_strPvtPsnRule;

	public String m_strRootPath;

	public String m_strHomePath;

	public String m_strLoginPath;

	public String m_strNoAccessPath;

	public String m_strUUID;

	/** 管理模板编号。若指定了该模版，则在维护栏目内容时会使用该模版。该模版必须是HTML模版 */
	public long m_lAdminTmplID;

	/** 对于非组合分支，为所用的标记类型；对于组合分支，0表示不在会话中保存分支选择结果，1表示在会话中保存分支选择结果 */
	public int m_iMarkupType;

	/** 首页 */
	public ApBPage m_rootPage;

	/** 包含的全局页面 */
	ADBList<ApBPage> m_adblPages;

	public Vector<ApBPage> m_vecPages;

	public Vector<ApPathMap> m_vecMaps;

	/** 包含的全局页面的个性化页面名称（非组合分支） */
	ADBList<ApBPagePsnName> m_adblPsnNames;

	public Vector<ApBPagePsnName> m_vecPsnNames;

	/** 包含的全局页面中的全局栏位布局设置（非组合分支） */
	ADBList<ApBPageLayout> m_adblLayouts;

	public Vector<ApBPageLayout> m_vecLayouts;

	/** 包含的全局页面中的全局个性化模板设置（非组合分支） */
	ADBList<ApBPagePsnTmpl> m_adblPsnTmpls;

	public Vector<ApBPagePsnTmpl> m_vecPsnTmpls;

	/** 包含的全局页面中的全局内容（非组合分支） */
	ADBList<ApBPageContent> m_adblContents;

	public Vector<ApBPageContent> m_vecContents;

	/** 包含的子分支（组合分支） */
	ADBList<ApBranchGroup> m_adblBranchGroups;

	public Vector<ApBranchGroup> m_vecBranchGroups;

	/** 包含的应用资源 */
	ADBList<ApRes> m_adblAppReses;

	public Vector<ApRes> m_vecAppReses;

	/** 包含的支持路径扩展匹配的页面 */
	ADBList<ApBPage> m_adblExpandMatchPages;

	public Vector<ApBPage> m_vecExpandMatchPages;

	/** 站点分支Cache */
	static Hashtable<String, ApBranch> m_htSiteBranches = new Hashtable<String, ApBranch>();

	/** 分支Cache，用于页面找不到时查对所属分支 */
	static ADBList<ApBranch> m_adbBranches;
	static String ADB_BRANCHES_LOCK = new String();

	/** 定义了路径映射的分支的Cache */
	static ADBList<ApBranch> m_adbBranchHasMap;
	static String ADB_BRANCH_HAS_MAP_LOCK = new String();

	/** 组合分支 */
	static ADBList<ApBranch> m_adbGroupBranch;
	static String ADB_GROUP_BRANCH_LOCK = new String();

	public ApBranch() throws AdbException {
		m_lBranchID = m_lSiteID = -1;
		m_strBranchName = m_strBranchDesc = m_strRootPath = m_strHomePath = m_strLoginPath = m_strNoAccessPath = m_strUUID = "";
		m_strAccessRule = "T";
		m_strManageRule = m_strClbPsnRule = m_strPvtPsnRule = "F";
		m_rootPage = null;
		m_iPsnType = 1;
		m_lAdminTmplID = 0;
	}

	public Vector<ApBranchGroup> getBranchGroups() {
		return m_vecBranchGroups;
	}

	public long getBranchId() {
		return m_lBranchID;
	}

	public String getBranchName() {
		return m_strBranchName;
	}

	public Vector<ApBPage> getPages() {
		if (m_vecPages == null)
			m_vecPages = new Vector<ApBPage>();
		return m_vecPages;
	}

	public void setPages(Vector<ApBPage> pages) {
	}

	public Vector<ApPathMap> getMaps() {
		if (m_vecMaps == null)
			m_vecMaps = new Vector<ApPathMap>();
		return m_vecMaps;
	}

	public void setMaps(Vector<ApPathMap> maps) {
	}

	public Vector<ApBPagePsnName> getPsnNames() {
		if (m_vecPsnNames == null)
			m_vecPsnNames = new Vector<ApBPagePsnName>();
		return m_vecPsnNames;
	}

	public void setPsnNames(Vector<ApBPagePsnName> names) {
	}

	public Vector<ApBPageLayout> getLayouts() {
		if (m_vecLayouts == null)
			m_vecLayouts = new Vector<ApBPageLayout>();
		return m_vecLayouts;
	}

	public void setLayouts(Vector<ApBPageLayout> layouts) {
	}

	public Vector<ApBPagePsnTmpl> getPsnTmpls() {
		if (m_vecPsnTmpls == null)
			m_vecPsnTmpls = new Vector<ApBPagePsnTmpl>();
		return m_vecPsnTmpls;
	}

	public void setPsnTmpls(Vector<ApBPagePsnTmpl> psnTmpls) {
	}

	public Vector<ApBPageContent> getContents() {
		if (m_vecContents == null)
			m_vecContents = new Vector<ApBPageContent>();
		return m_vecContents;
	}

	public void setContents(Vector<ApBPageContent> contents) {
	}

	/**
	 * 递归地将页面中可以继承的布局或内容继承给子页面
	 * 
	 * @param page
	 */
	static void cascadeInheritableContent(ApBPage page) {
		Vector<ApBPageLayout> vec1 = null;
		Vector<ApBPageContent> vec2 = null;

		for (Enumeration<ApBPageLayout> enm = page.getLayouts().elements(); enm
				.hasMoreElements();) {
			ApBPageLayout layout = enm.nextElement();
			if (layout.m_bInheritable) {
				if (vec1 == null)
					vec1 = new Vector<ApBPageLayout>();
				vec1.add(layout);
			}
		}

		for (Enumeration<ApBPageContent> enm = page.getContents().elements(); enm
				.hasMoreElements();) {
			ApBPageContent content = enm.nextElement();
			if (content.m_bInheritable) {
				if (vec2 == null)
					vec2 = new Vector<ApBPageContent>();
				vec2.add(content);
			}
		}

		for (Enumeration<ApBPage> enm = page.getSubPages().elements(); enm
				.hasMoreElements();) {
			ApBPage subPage = enm.nextElement();
			if (vec1 != null)
				subPage.getLayouts().addAll(vec1);
			if (vec2 != null)
				subPage.mergeContents(vec2);
			cascadeInheritableContent(subPage);
		}
	}

	/**
	 * 将m_vecPages的页面以及内容根据其关系组织成为树形结构，将其根赋予m_rootPage
	 */
	void buildPageTree() {
		ApBPage page;
		ApBPage lastPage = null;

		// 为了避免在计算过程中m_rootPage被访问，待计算完毕后再更改m_rootPage
		ApBPage root = null;

		Enumeration<ApBPagePsnName> enumNames = getPsnNames().elements();
		ApBPagePsnName name = null;
		if (enumNames.hasMoreElements())
			name = enumNames.nextElement();

		Enumeration<ApBPageLayout> enumLayouts = getLayouts().elements();
		ApBPageLayout layout = null;
		if (enumLayouts.hasMoreElements())
			layout = enumLayouts.nextElement();

		Enumeration<ApBPagePsnTmpl> enumPsnTmpls = getPsnTmpls().elements();
		ApBPagePsnTmpl psnTmpl = null;
		if (enumPsnTmpls.hasMoreElements())
			psnTmpl = enumPsnTmpls.nextElement();

		Enumeration<ApBPageContent> enumContents = getContents().elements();
		ApBPageContent content = null;
		if (enumContents.hasMoreElements())
			content = enumContents.nextElement();

		// 将页面组织成为树需要进行多次循环才能完成，每次循环都能将一些叶子挂到树上

		// 当轮循环中未能挂到树上的叶子
		Vector<ApBPage> vecOrphans;
		// 是否第一轮循环
		boolean bFirstRound = true;
		// 当轮循环需要处理的叶子
		Vector<ApBPage> vecPages = m_vecPages;
		// 当轮循环需要处理的叶子的个数
		int pageCount;

		do {
			// 记下叶子数，用于判断当轮循环过后是否能够把一些叶子挂到树上，如果不能则结束处理，否则会进入死循环
			pageCount = vecPages.size();
			vecOrphans = new Vector<ApBPage>();

			for (Enumeration<ApBPage> enm = vecPages.elements(); enm
					.hasMoreElements();) {
				page = enm.nextElement();
				page.getSubPages().clear();

				if (bFirstRound) { // 只有在第一轮循环时才需要进行这些处理
					// {匹配全局个性化名称
					page.getPsnNames().clear();
					while (name != null) {
						if (name.m_lPageID > page.m_lPageID)
							break;
						if (name.m_lPageID == page.m_lPageID)
							page.getPsnNames().add(name);
						if (enumNames.hasMoreElements())
							name = enumNames.nextElement();
						else
							name = null;
					}
					// }

					// {匹配全局布局
					page.getLayouts().clear();
					while (layout != null) {
						if (layout.m_lPageID > page.m_lPageID)
							break;
						if (layout.m_lPageID == page.m_lPageID)
							page.getLayouts().add(layout);
						if (enumLayouts.hasMoreElements())
							layout = enumLayouts.nextElement();
						else
							layout = null;
					}
					// }

					// {匹配全局个性化模板
					page.getPsnTmpls().clear();
					while (psnTmpl != null) {
						if (psnTmpl.m_lPageID > page.m_lPageID)
							break;
						if (psnTmpl.m_lPageID == page.m_lPageID)
							page.getPsnTmpls().add(psnTmpl);
						if (enumPsnTmpls.hasMoreElements())
							psnTmpl = enumPsnTmpls.nextElement();
						else
							psnTmpl = null;
					}
					// }

					// {匹配全局内容
					page.getContents().clear();
					while (content != null) {
						if (content.m_lPageID > page.m_lPageID)
							break;
						if (content.m_lPageID == page.m_lPageID)
							page.getContents().add(content);
						if (enumContents.hasMoreElements())
							content = enumContents.nextElement();
						else
							content = null;
					}
					page.sortContents();
					// }

					if (root == null) {
						if (page.m_lPageID != 0) { // 错误！！不应该出现这样的情况
						}

						root = page;
						root.m_parent = root;
						root.m_iLevel = 0;
						lastPage = page;
						continue;
					}
				}

				if (lastPage == null || page.m_lParentID != lastPage.m_lPageID) // 重新查找当前页面的根页面
					lastPage = root.findPage(page.m_lParentID);

				if (lastPage == null) // 父页面暂未在树中
					vecOrphans.add(page);
				else {
					// {将页面按顺序添加到父页面中
					boolean bAdded = false;
					int posi = 0;
					for (Enumeration<ApBPage> enum1 = lastPage.getSubPages()
							.elements(); enum1.hasMoreElements(); posi++) {
						ApBPage tempPage = enum1.nextElement();
						if (page.m_iOrder < tempPage.m_iOrder) {
							lastPage.getSubPages().insertElementAt(page, posi);
							bAdded = true;
							break;
						}
					}
					if (!bAdded)
						lastPage.getSubPages().add(page);
					// }

					page.m_parent = lastPage;
					page.m_iLevel = lastPage.m_iLevel + 1;
				}
			}

			bFirstRound = false;
			vecPages = vecOrphans;
		} while (vecPages.size() > 0 && pageCount > vecPages.size());

		// 处理继承的布局和内容
		if (root != null)
			cascadeInheritableContent(root);

		m_rootPage = root;
	}

	/**
	 * 获取站点分支及其内全局页面定义
	 */
	public static ApBranch getSiteBranchWithGlobalAssocs(AdbAdapter adapter,
			long branchID, boolean useCache, boolean forceCheck)
			throws Exception {
		ApBranch branch = null;
		String key = Long.toString(branchID);

		if (useCache) {
			branch = m_htSiteBranches.get(key);

			if (branch != null) {
				boolean reload = !adapter.syncCheck(branch, forceCheck, true);

				if (branch.m_iPsnType == PSN_TYPE_GROUP) {
					if (!reload)
						reload = !branch.m_adblBranchGroups.syncCheck(adapter,
								forceCheck);
				} else {
					if (!reload)
						reload = !branch.m_adblPages.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblPsnNames.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblLayouts.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblPsnTmpls.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblContents.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblAppReses.syncCheck(adapter,
								forceCheck);
					if (!reload)
						reload = !branch.m_adblExpandMatchPages.syncCheck(
								adapter, forceCheck);
				}

				if (reload)
					branch = null;
			}
		}

		if (branch == null) { // 需要加载
			branch = new ApBranch();
			branch.m_lBranchID = branchID;

			if (adapter.retrieve(branch) == null)
				return null;

			if (branch.m_iPsnType == PSN_TYPE_GROUP) {
				// {加载被组合的分支
				ApBranchGroup group = new ApBranchGroup();
				group.m_lGroupBranchID = branchID;
				branch.m_adblBranchGroups = adapter.retrieveMultiDbl(group,
						"loadByBranch", "default");
				branch.m_vecBranchGroups = branch.m_adblBranchGroups.m_vecObjects;
				// }
			} else {
				// {加载全局页面
				ApBPage bpage = new ApBPage();
				bpage.m_lBranchID = branchID;
				branch.m_adblPages = adapter.retrieveMultiDbl(bpage,
						"loadGlobalForBranch", "default");
				branch.m_vecPages = branch.m_adblPages.m_vecObjects;
				// }

				// {加载全局个性化名称
				ApBPagePsnName psnName = new ApBPagePsnName();
				psnName.m_lBranchID = branchID;
				branch.m_adblPsnNames = adapter.retrieveMultiDbl(psnName,
						"loadGlobalForBranch", "default");
				branch.m_vecPsnNames = branch.m_adblPsnNames.m_vecObjects;
				// }

				// {加载全局页面布局
				ApBPageLayout layout = new ApBPageLayout();
				layout.m_lBranchID = branchID;
				branch.m_adblLayouts = adapter.retrieveMultiDbl(layout,
						"loadGlobalForBranch", "default");
				branch.m_vecLayouts = branch.m_adblLayouts.m_vecObjects;
				// }

				// {加载全局个性化模板
				ApBPagePsnTmpl psnTmpl = new ApBPagePsnTmpl();
				psnTmpl.m_lBranchID = branchID;
				branch.m_adblPsnTmpls = adapter.retrieveMultiDbl(psnTmpl,
						"loadGlobalForBranch", "default");
				branch.m_vecPsnTmpls = branch.m_adblPsnTmpls.m_vecObjects;
				// }

				// {加载全局内容
				ApBPageContent content = new ApBPageContent();
				content.m_lBranchID = branchID;
				branch.m_adblContents = adapter.retrieveMultiDbl(content,
						"loadGlobalForBranch", "default");
				branch.m_vecContents = branch.m_adblContents.m_vecObjects;
				// }

				// {加载应用资源
				ApRes res = new ApRes();
				res.m_lResID = branchID;
				branch.m_adblAppReses = adapter.retrieveMultiDbl(res,
						"loadAppResByBranch", null);
				branch.m_vecAppReses = branch.m_adblAppReses.m_vecObjects;
				// }

				// {加载支持路径扩展匹配的页面
				// 重新生成对象，因为原对象需要用于sync
				bpage = new ApBPage();
				bpage.m_lBranchID = branchID;
				branch.m_adblExpandMatchPages = adapter.retrieveMultiDbl(bpage,
						"loadExpandMatchForBranch", "default");
				branch.m_vecExpandMatchPages = branch.m_adblExpandMatchPages.m_vecObjects;
				// }

				branch.buildPageTree();
			}

			if (useCache)
				m_htSiteBranches.put(key, branch);
		}

		return branch;
	}

	/**
	 * 
	 * 
	 * @param adapter
	 * @return
	 * @throws Exception
	 */
	public static Vector<ApBranch> getAllBranch(AdbAdapter adapter)
			throws Exception {
		synchronized (ADB_BRANCHES_LOCK) {
			boolean reConstruct = false;

			if (m_adbBranches == null) {
				m_adbBranches = adapter.retrieveMultiDbl(new ApBranch(),
						"loadAllPath");
				reConstruct = true;
			} else {
				if (!adapter.syncList(m_adbBranches, false))
					reConstruct = true;
			}

			if (reConstruct) {
				ApBranch branch;
				for (Enumeration<ApBranch> enu = m_adbBranches.m_vecObjects
						.elements(); enu.hasMoreElements();) {
					branch = enu.nextElement();
					branch.m_strRootPath = "/" + branch.m_strRootPath + "/";
				}
			}
			return m_adbBranches.m_vecObjects;
		}
	}

	/**
	 * 获取定义了映射的分支的根路径列表，跟路径以'/'开头
	 * 
	 * @param adapter
	 * @return
	 * @throws Exception
	 */
	public static Vector<ApBranch> getRootHasMap(AdbAdapter adapter)
			throws Exception {
		synchronized (ADB_BRANCH_HAS_MAP_LOCK) {
			boolean reConstruct = false;

			if (m_adbBranchHasMap == null) {
				m_adbBranchHasMap = adapter.retrieveMultiDbl(new ApBranch(),
						"loadHasMap");
				reConstruct = true;
			} else {
				if (!adapter.syncList(m_adbBranchHasMap, false))
					reConstruct = true;
			}

			if (reConstruct) {
				ApBranch branch;
				for (Enumeration<ApBranch> enu = m_adbBranchHasMap.m_vecObjects
						.elements(); enu.hasMoreElements();) {
					branch = enu.nextElement();
					branch.m_strRootPath = "/" + branch.m_strRootPath;
				}
			}
			return m_adbBranchHasMap.m_vecObjects;
		}
	}

	/**
	 * 获取所有组合分支列表，跟路径以'/'开头，以'/'结尾
	 * 
	 * @param adapter
	 * @return
	 * @throws Exception
	 */
	public static Vector<ApBranch> getGroupBranch(AdbAdapter adapter)
			throws Exception {
		synchronized (ADB_GROUP_BRANCH_LOCK) {
			boolean reConstruct = false;

			if (m_adbGroupBranch == null) {
				m_adbGroupBranch = adapter.retrieveMultiDbl(new ApBranch(),
						"loadAllGroup");
				reConstruct = true;
			} else {
				if (!adapter.syncList(m_adbGroupBranch, false))
					reConstruct = true;
			}

			if (reConstruct) {
				for (ApBranch branch : m_adbGroupBranch.m_vecObjects) {
					// 在根路径前后加上'/'
					branch.m_strRootPath = "/" + branch.m_strRootPath + "/";
				}
			}
			return m_adbGroupBranch.m_vecObjects;
		}
	}

	/**
	 * 根据UUID查找ID
	 * 
	 * @param adapter
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static long findIdByUuid(AdbAdapter adapter, String uuid)
			throws Exception {
		ApBranch branch = new ApBranch();

		branch.m_strUUID = uuid;
		branch = adapter.retrieve(branch, "loadIDWithCache");
		if (branch == null)
			throw new Exception("Branch " + uuid + " not found!");
		return branch.m_lBranchID;
	}
}
