package com.aggrepoint.su.core.data;

import java.util.Collection;
import java.util.Vector;

import com.aggrepoint.adk.FileParameter;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 
 * @author YJM
 */
public class ApSite extends ADB {
	public long m_lSiteID;
	public String m_strSiteName;
	public String m_strSiteDesc;
	public long m_lSiteLogoID;
	public String m_strManageRule;
	public String m_strPublishBranchDir;
	public String m_strStaticBranchUrl;
	public String m_strPublishChannelDir;
	public String m_strStaticChannelUrl;
	public String m_strPublishResDir;
	public String m_strStaticResUrl;
	public Vector<ApBranch> m_vecBranches;
	public String m_strUUID;

	public FileParameter m_logoFile;

	/** 以下属性用于从XML文件中导入 */
	public String m_strLogoContentType;
	public String m_strLogoName;
	public String m_strLogoFile;
	public Vector<ApLayout> m_vecLayouts;
	public Vector<ApTemplate> m_vecTemplates;
	public Vector<ApFrame> m_vecFrames;
	public Vector<ApApp> m_vecApps;
	public Vector<ApContCat> m_vecContCats;
	public Vector<ApChannel> m_vecChannels;
	public Vector<ApResDir> m_vecDirs;
	public Vector<ApBranchGroup> m_vecBranchGroups;
	public ApResDir m_dirTemplate;
	public ApResDir m_dirFrame;
	public ApResDir m_dirCPage;
	public ApResDir m_dirContent;
	public ApResDir m_dirApp;

	public ApSite() throws AdbException {
		m_lSiteID = -1;
		m_lSiteLogoID = 0;
		m_strSiteName = m_strSiteDesc = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strUUID = "";
		m_strManageRule = "T";
	}

	public long getId() {
		return m_lSiteID;
	}

	public String getName() {
		return m_strSiteName;
	}

	static String fixPath(String str, boolean fixSep) {
		if (str == null)
			return str;
		str = str.trim();
		if (str.equals(""))
			return null;
		if (str.endsWith("/") || str.endsWith("\\"))
			str = str.substring(0, str.length() - 1);
		if (fixSep)
			str = str.replaceAll("\\\\", "/");
		return str;
	}

	public String getPublishBranchDir() {
		return m_strPublishBranchDir;
	}

	public void setPublishBranchDir(String str) {
		m_strPublishBranchDir = fixPath(str, false);
	}

	public String getStaticBranchUrl() {
		return m_strStaticBranchUrl;
	}

	public void setStaticBranchUrl(String str) {
		m_strStaticBranchUrl = fixPath(str, true);
	}

	public String getPublishChannelDir() {
		return m_strPublishChannelDir;
	}

	public void setPublishChannelDir(String str) {
		m_strPublishChannelDir = fixPath(str, false);
	}

	public String getStaticChannelUrl() {
		return m_strStaticChannelUrl;
	}

	public void setStaticChannelUrl(String str) {
		m_strStaticChannelUrl = fixPath(str, true);
	}

	public String getPublishResDir() {
		return m_strPublishResDir;
	}

	public void setPublishResDir(String str) {
		m_strPublishResDir = fixPath(str, false);
	}

	public String getStaticResUrl() {
		return m_strStaticResUrl;
	}

	public void setStaticResUrl(String str) {
		m_strStaticResUrl = fixPath(str, true);
	}

	public Vector<ApBranch> getBranches() {
		if (m_vecBranches == null)
			m_vecBranches = new Vector<ApBranch>();
		return m_vecBranches;
	}

	public void setBranches(Vector<ApBranch> col) {
	}

	public Vector<ApLayout> getLayouts() {
		if (m_vecLayouts == null)
			m_vecLayouts = new Vector<ApLayout>();
		return m_vecLayouts;
	}

	public void setLayouts(Vector<ApLayout> col) {
	}

	public Vector<ApTemplate> getTemplates() {
		if (m_vecTemplates == null)
			m_vecTemplates = new Vector<ApTemplate>();
		return m_vecTemplates;
	}

	public void setTemplates(Vector<ApTemplate> col) {
	}

	public Vector<ApFrame> getFrames() {
		if (m_vecFrames == null)
			m_vecFrames = new Vector<ApFrame>();
		return m_vecFrames;
	}

	public void setFrames(Vector<ApFrame> col) {
	}

	public Vector<ApApp> getApps() {
		if (m_vecApps == null)
			m_vecApps = new Vector<ApApp>();
		return m_vecApps;
	}

	public void setApps(Vector<ApApp> col) {
	}

	public Vector<ApContCat> getContCats() {
		if (m_vecContCats == null)
			m_vecContCats = new Vector<ApContCat>();
		return m_vecContCats;
	}

	public void setContCats(Vector<ApContCat> col) {
	}

	public Collection<ApChannel> getChannels() {
		if (m_vecChannels == null)
			m_vecChannels = new Vector<ApChannel>();
		return m_vecChannels;
	}

	public void setChannels(Collection<ApChannel> col) {
	}

	public Collection<ApResDir> getDirs() {
		if (m_vecDirs == null)
			m_vecDirs = new Vector<ApResDir>();
		return m_vecDirs;
	}

	public void setDirs(Collection<ApResDir> col) {
	}

	public Collection<ApBranchGroup> getBranchgroups() {
		if (m_vecBranchGroups == null)
			m_vecBranchGroups = new Vector<ApBranchGroup>();
		return m_vecBranchGroups;
	}

	public void setBranchgroups(Collection<ApBranchGroup> col) {
	}

	public static ApSite load(DbAdapter adapter, long siteid) throws Exception {
		ApSite site = new ApSite();
		site.m_lSiteID = siteid;
		return adapter.retrieve(site, "loadWithCache");
	}

	public void initSysResDir(AdbAdapter adapter) throws Exception {
		ApResDir root = ApResDir.createResDir(adapter, m_lSiteID, null, "sys",
				false);
		m_dirTemplate = ApResDir.createResDir(adapter, m_lSiteID, root, "tmpl",
				false);
		m_dirFrame = ApResDir.createResDir(adapter, m_lSiteID, root, "frame",
				false);
		m_dirCPage = ApResDir.createResDir(adapter, m_lSiteID, root, "cpage",
				false);
		m_dirContent = ApResDir.createResDir(adapter, m_lSiteID, root, "cont",
				false);
		m_dirApp = ApResDir
				.createResDir(adapter, m_lSiteID, root, "app", false);
	}
}
