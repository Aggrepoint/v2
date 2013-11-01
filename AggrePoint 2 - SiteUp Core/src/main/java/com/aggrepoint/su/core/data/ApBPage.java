package com.aggrepoint.su.core.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbData;
import com.icebean.core.adb.AdbException;
import com.icebean.core.common.HTTPUtils;

/**
 * 站点页面
 * 
 * @author YJM
 */
public class ApBPage extends ADB implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 页面 */
	public static final int TYPE_PAGE = 10;

	/** 链接 */
	public static final int TYPE_LINK = 20;

	/** 页面编号 */
	public long m_lPageID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;

	/** 文档类型 */
	public int m_iPageType;

	/** 页面名称 */
	public String m_strPageName;

	/** 站点编号 */
	public long m_lSiteID;

	/** 站点分支编号 */
	public long m_lBranchID;

	/** 模板编号 */
	public long m_lTemplateID;

	/** 使用的模板的参数 */
	public String m_strTmplParams;

	/** 是否从父页面继承模板设置 */
	public boolean m_bInheritTmpl;

	/** 父页面编号 */
	public long m_lParentID;

	/** 顺序 */
	public int m_iOrder;

	/** 页面打开方式 */
	public int m_iOpenMode;

	/** 页面路径 */
	public String m_strPathName;

	/** 拥有人编号 */
	public String m_strOwnerID;

	/** 个性化访问规则 */
	public String m_strAccessRule;

	/** 协作个性化规则 */
	public String m_strClbPsnRule;

	/** 允许在该页面下建立私有化个性页面的用户规则 */
	public String m_strPvtPsnRule;

	/** 是否直接跳到子页面 */
	public boolean m_bSkipToSub;

	public boolean m_bHide;

	/** 选择页面时是否重置页面中所有窗口 */
	public boolean m_bResetWin;

	/** 是否启用映射。启用映射的页面，只有当前访问URL符合站点上定义的映射规则，而且页面的URL符合对应的反向映射时，才会在导航结构中出现 */
	public boolean m_bUseMap;

	public int m_iChildCount;

	/**
	 * 是否从父栏目继承了启用映射的标志。若父栏目启用了映射，子栏目也必须启用。m_bUseMap或m_bInheritUseMap为true时就启用映射
	 */
	public boolean m_bInheritUseMap;

	/** 是否扩展匹配 */
	public boolean m_bExpandMatch;

	/** 栏位布局设置 */
	public Vector<ApBPageLayout> m_vecLayouts;

	/** 个性化栏目名称 */
	public Vector<ApBPagePsnName> m_vecPsnNames;

	/** 个性化模板设置 */
	public Vector<ApBPagePsnTmpl> m_vecPsnTmpls;

	/** 包含的内容 */
	public Vector<ApBPageContent> m_vecContents;

	/** 子页面 */
	public Vector<ApBPage> m_vecSubPages;

	/** 上级页面 */
	public ApBPage m_parent;

	/** 完整的页面路径，包括站点根路径 */
	public String m_strFullPath;

	/** 直接页面路径，与FullPath的区别在于不包括站点根路径，用于与http server配合缩短URL */
	public String m_strDirectPath;

	/** 所处的层次 */
	public int m_iLevel;

	/** 分支下页面数目 */
	public int m_iCount;

	public String m_strUUID;

	/** 栏目中的标记类型 */
	public int m_iMarkupType;

	/** 模版UUID，用于从XML导入 */
	public String m_strTemplateUUID;

	/**
	 * 构造函数
	 */
	public ApBPage() throws AdbException {
		m_strPageName = m_strPathName = m_strTmplParams = m_strFullPath = m_strDirectPath = "";
		m_strAccessRule = "T";
		m_strOwnerID = m_strUUID = "";
		m_strClbPsnRule = m_strPvtPsnRule = "F";
		m_vecContents = null;
		m_vecSubPages = null;
		m_parent = null;
		m_bInheritTmpl = m_bSkipToSub = m_bResetWin = false;
	}

	public int getUseMap() {
		if (m_bUseMap)
			return 1;
		if (m_bExpandMatch)
			return 2;
		return 0;
	}

	public void setUseMap(int i) {
		if (i == 1) {
			m_bUseMap = true;
			m_bExpandMatch = false;
		} else if (i == 2) {
			m_bUseMap = false;
			m_bExpandMatch = true;
		} else {
			m_bUseMap = false;
			m_bExpandMatch = false;
		}
	}

	public String getPageName() {
		return m_strPageName == null ? "" : m_strPageName;
	}

	public void setPageName(String str) {
		m_strPageName = str == null ? "" : str;
	}

	public long getId() {
		return m_lPageID;
	}

	public long getParentId() {
		return m_lParentID;
	}

	public String getName() {
		return m_strPageName;
	}

	public String getPath() {
		return m_strPathName;
	}

	public String getFullPath() {
		return m_strFullPath;
	}

	public int getSkip() {
		return m_bSkipToSub ? 1 : 0;
	}

	public void setSkip(int i) {
		m_bSkipToSub = i == 0 ? false : true;
	}

	public int getChildCount() {
		return m_iChildCount;
	}

	public boolean ownerIs(String userId) {
		if (m_strOwnerID == null || m_strOwnerID.equals(""))
			return false;
		return m_strOwnerID.equals(userId);
	}

	/**
	 * AdbAdapter加载内容时使用
	 */
	public Vector<ApBPageContent> getContents() {
		if (m_vecContents == null)
			m_vecContents = new Vector<ApBPageContent>();

		return m_vecContents;
	}

	/**
	 * 供AdbAdapter使用。该方法不会被调用
	 */
	public void setContents(Vector<ApBPageContent> col) {
	}

	public Vector<ApBPageLayout> getLayouts() {
		if (m_vecLayouts == null)
			m_vecLayouts = new Vector<ApBPageLayout>();

		return m_vecLayouts;
	}

	public ApBPageLayout getLayoutForArea(String area) {
		if (m_vecLayouts == null)
			return null;

		for (ApBPageLayout l : m_vecLayouts)
			if (l.getAreaName().equals(area))
				return l;

		return null;
	}

	public void setLayouts(Vector<ApBPageLayout> vec) {
	}

	public Vector<ApBPagePsnName> getPsnNames() {
		if (m_vecPsnNames == null)
			m_vecPsnNames = new Vector<ApBPagePsnName>();

		return m_vecPsnNames;
	}

	public void setPsnNames(Vector<ApBPagePsnName> vec) {
	}

	public Vector<ApBPagePsnTmpl> getPsnTmpls() {
		if (m_vecPsnTmpls == null)
			m_vecPsnTmpls = new Vector<ApBPagePsnTmpl>();

		return m_vecPsnTmpls;
	}

	public void setPsnTmpls(Vector<ApBPagePsnTmpl> col) {
	}

	/**
	 * AdbAdapter加载子页面时使用
	 */
	public Vector<ApBPage> getSubPages() {
		if (m_vecSubPages == null)
			m_vecSubPages = new Vector<ApBPage>();

		return m_vecSubPages;
	}

	/**
	 * 供AdbAdapter使用。该方法不会被调用
	 */
	public void setSubPages(Vector<ApBPage> col) {
	}

	/**
	 * 对象加载成功后作为Trigger被调用
	 */
	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) {
		try {
			AdbData<ApBPage> data = adapter.getData(this);
			if (data.m_objMaster != null && data.m_objMaster instanceof ApBPage) {
				m_parent = data.m_objMaster;
				m_iLevel = m_parent.m_iLevel + 1;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 */
	public boolean contains(ApBPage page) {
		if (m_lPageID == page.m_lPageID)
			return true;

		if (m_vecSubPages != null)
			for (Enumeration<ApBPage> enm = m_vecSubPages.elements(); enm
					.hasMoreElements();)
				if (enm.nextElement().contains(page))
					return true;

		return false;
	}

	/**
	 * 
	 */
	public ApBPage findPage(long pageID) {
		if (m_lPageID == pageID)
			return this;

		ApBPage pageFound;

		if (m_vecSubPages != null)
			for (Enumeration<ApBPage> enm = m_vecSubPages.elements(); enm
					.hasMoreElements();) {
				pageFound = enm.nextElement().findPage(pageID);
				if (pageFound != null)
					return pageFound;
			}

		return null;
	}

	static String[] CASCADE_LOAD_METHOD = { "loadForCascadeTemplate",
			"loadForCascadePath", "loadForCascadeTemplateAndPath" };

	static String[] CASCADE_UPDATE_METHOD = { "updateTemplate", "updatePath",
			"updateTemplateAndPath" };

	/**
	 * 将模板设置或/和路径传递给所有子页面
	 */
	public void cascadeTemplateAndPath(AdbAdapter adapter, boolean forTemplate,
			boolean forPath, boolean forMap) throws Exception {
		if (!forTemplate && !forPath && !forMap)
			return;

		if (forTemplate)
			adapter.setFlag(this, "template");
		if (forPath)
			adapter.setFlag(this, "path");
		if (forMap)
			adapter.setFlag(this, "map");

		long parentID = m_lParentID;

		m_lParentID = m_lPageID;
		Vector<ApBPage> vecSubPages = null;
		try {
			vecSubPages = adapter.retrieveMulti(this,
					"loadForCascadeTemplateAndPathAndMap", null);
		} finally {
			m_lParentID = parentID;
		}

		for (Enumeration<ApBPage> enm = vecSubPages.elements(); enm
				.hasMoreElements();) {
			ApBPage page = enm.nextElement();
			if (forTemplate && page.m_bInheritTmpl) {
				page.m_lTemplateID = m_lTemplateID;
				adapter.setFlag(page, "template");
			}
			if (forPath) {
				page.m_strFullPath = m_strFullPath + page.m_strPathName + "/";
				page.m_strDirectPath = m_strDirectPath + page.m_strPathName
						+ "/";
				adapter.setFlag(page, "path");
			}
			if (forMap) {
				page.m_bInheritUseMap = m_bUseMap || m_bInheritUseMap;
				adapter.setFlag(page, "map");
			}

			if (forTemplate && page.m_bInheritTmpl || forPath || forMap)
				adapter.update(page, "updateTemplateAndPathAndMap");

			page.cascadeTemplateAndPath(adapter, forTemplate, forPath, forMap);
		}
	}

	/**
	 * 移动页面
	 */
	public void move(AdbAdapter adapter, int step) throws Exception {
		if (step == 0)
			return;

		Vector<ApBPage> vecPages = null;
		vecPages = adapter.retrieveMulti(this, "loadForMove", "default");
		if (vecPages.size() <= 1)
			return;

		// {找出当前页面所在位置
		int idx = 0;
		ApBPage page = null;
		for (Enumeration<ApBPage> enm = vecPages.elements(); enm
				.hasMoreElements(); idx++) {
			page = enm.nextElement();
			if (page.m_lPageID == m_lPageID)
				break;
		}
		// }

		// {计算要移去的位置
		int toIdx = idx + step;
		if (toIdx < 0)
			toIdx = 0;
		else if (toIdx >= vecPages.size())
			toIdx = vecPages.size() - 1;

		if (idx == toIdx)
			return;
		// }

		// {移动
		vecPages.removeElementAt(idx);
		vecPages.insertElementAt(page, toIdx);
		// }

		// 保存
		idx = 0;
		for (Enumeration<ApBPage> enm = vecPages.elements(); enm
				.hasMoreElements(); idx++) {
			page = enm.nextElement();
			page.m_iOrder = idx;
			adapter.update(page, "updateOrder");
		}
	}

	/**
	 * 将内容按归属关系组织
	 */
	public void sortContents() {
		Hashtable<Long, ApBPageContent> ht = new Hashtable<Long, ApBPageContent>();

		ApBPageContent[] conts = getContents().toArray(
				new ApBPageContent[getContents().size()]);

		for (int i = 0; i < conts.length; i++)
			ht.put(conts[i].m_lPageContID, conts[i]);

		for (int i = 0; i < conts.length; i++)
			if (conts[i].m_lContainContID > 0) {
				ApBPageContent parent = ht.get(conts[i].m_lContainContID);
				if (parent != null) {
					getContents().remove(conts[i]);
					parent.getChilds().add(conts[i]);
				}
			}
	}

	/**
	 * 合并内容 前提条件：本页面中的内容和要合并的内容各自都是按栏位、区位、顺序进行排列的
	 * 
	 * @param vecCont
	 */
	public void mergeContents(Vector<ApBPageContent> vecCont) {
		Vector<ApBPageContent> vecContents = new Vector<ApBPageContent>();
		Enumeration<ApBPageContent> enumThis = getContents().elements();
		Enumeration<ApBPageContent> enumPage = vecCont.elements();
		ApBPageContent contThis = null;
		if (enumThis.hasMoreElements())
			contThis = enumThis.nextElement();
		ApBPageContent contPage = null;
		if (enumPage.hasMoreElements())
			contPage = enumPage.nextElement();
		while (contThis != null || contPage != null) {
			if (contThis == null) {
				vecContents.add(contPage);
				if (enumPage.hasMoreElements())
					contPage = enumPage.nextElement();
				else
					contPage = null;
				continue;
			}
			if (contPage == null) {
				vecContents.add(contThis);
				if (enumThis.hasMoreElements())
					contThis = enumThis.nextElement();
				else
					contThis = null;
				continue;
			}

			// 按栏位、区域、顺序进行排序
			int i = contThis.m_strAreaName.compareTo(contPage.m_strAreaName)
					* -100;
			if (contThis.m_iZoneID < contPage.m_iZoneID)
				i = i + 10;
			else if (contThis.m_iZoneID > contPage.m_iZoneID)
				i = i - 10;
			if (contThis.m_iOrder < contPage.m_iOrder)
				i = i + 1;
			else if (contThis.m_iOrder > contPage.m_iOrder)
				i = i - 1;

			if (i >= 0) {
				vecContents.add(contThis);
				if (enumThis.hasMoreElements())
					contThis = enumThis.nextElement();
				else
					contThis = null;
				continue;
			} else {
				vecContents.add(contPage);
				if (enumPage.hasMoreElements())
					contPage = enumPage.nextElement();
				else
					contPage = null;
				continue;
			}
		}
		m_vecContents = vecContents;
	}

	/**
	 * 将全局的内容合并到本页面中 前提：本页面中只包含当前用户私有的内容
	 * 
	 * @param page
	 */
	public void mergePublicAssoc(ApBPage page) {
		if (page == null)
			return;

		// 合并栏位布局设置
		getLayouts().addAll(page.getLayouts());

		// 合并个性化栏目名称
		getPsnNames().addAll(page.getPsnNames());

		// 合并个性化模板设置
		getPsnTmpls().addAll(page.getPsnTmpls());

		// 合并内容
		sortContents();
		mergeContents(page.getContents());
	}

	public void publish(AdbAdapter adapter, String urlRoot, String rootDir,
			int[] counts, boolean recursive) throws Exception {
		String dir = rootDir + m_strFullPath;
		new File(dir).mkdirs();

		FileOutputStream fos = new FileOutputStream(dir + "index.html");
		try {
			HTTPUtils.httpToOutputStream(fos, urlRoot + m_strFullPath
					+ "?_v=pub");
			if (counts != null)
				counts[0]++;

			if (!recursive)
				return;

			ApBPage sub = new ApBPage();
			sub.m_lParentID = m_lPageID;
			sub.m_iOfficialFlag = m_iOfficialFlag;
			for (ApBPage s : adapter.retrieveMulti(sub, "loadByParentNoSub",
					null))
				s.publish(adapter, urlRoot, rootDir, counts, recursive);
		} finally {
			fos.close();
		}
	}

	public ValidateResult checkPath(AdbAdapter adapter, Vector<String> args)
			throws Exception {
		if (m_lParentID == 0 || m_lParentID == m_lPageID)
			return ValidateResult.PASS;

		ApBPage page = new ApBPage();
		page.m_iOfficialFlag = m_iOfficialFlag;
		page.m_strPathName = m_strPathName;
		page.m_lParentID = m_lParentID;
		if (adapter.retrieve(page, "loadByPath") == null
				|| page.m_lPageID == m_lPageID)
			return ValidateResult.PASS;

		return ValidateResult.FAILED;
	}
}
