package com.aggrepoint.su.core.data;

import java.util.Hashtable;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.StringUtils;

/**
 * 内容
 * 
 * @author YJM
 */
public class ApContent extends ADB {
	public long m_lContentID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;

	public long m_lSiteID;

	public long m_lContCatID;

	public long m_lPageID;

	public String m_strName;

	public int m_iOrder;

	public String m_strContent;

	public String m_strDesc;

	public boolean m_bIsPkg;

	public String m_strPkgName;

	public String m_strPkgContentType;

	public String m_strPackage;

	public String m_strAccessRule;

	public long m_lResDirID;

	public int m_iZoneCount;

	public String m_strUUID;

	/** 以下属性用于从XML文件中导入 */
	public long m_lImportID;

	public String m_strIsDynamic;

	public String m_strLogoContentType;

	public String m_strLogoName;

	public String m_strLogoFile;

	public ApResDir m_dir;

	/** 内容 Cache */
	static Hashtable<String, ApContent> m_htContents = new Hashtable<String, ApContent>();

	public ApContent() throws AdbException {
		m_strName = m_strContent = m_strDesc = m_strPkgName = m_strPkgContentType = m_strPackage = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strIsDynamic = m_strUUID = "";
		m_strAccessRule = "T";
		m_bIsPkg = false;
	}

	public String getName() {
		return m_strName == null ? "" : m_strName;
	}

	public void setName(String str) {
		m_strName = str == null ? "" : str;
	}

	public String getDesc() {
		return m_strDesc == null ? "" : m_strDesc;
	}

	public void setDesc(String str) {
		m_strDesc = str == null ? "" : str;
	}

	public String getPkgName() {
		return m_strPkgName == null ? "" : m_strPkgName;
	}

	public void setPkgName(String str) {
		m_strPkgName = str == null ? "" : str;
	}

	public String getPkgContType() {
		return m_strPkgContentType == null ? "" : m_strPkgContentType;
	}

	public void setPkgContType(String str) {
		m_strPkgContentType = str == null ? "" : str;
	}

	public ApResDir getDir() throws Exception {
		if (m_dir == null)
			m_dir = new ApResDir();
		return m_dir;
	}

	public void setDir(ApResDir dir) {
		m_dir = dir;
	}

	public long getId() {
		return m_lContentID;
	}

	public String getAccessRule() {
		return m_strAccessRule;
	}

	public String getContent() {
		return m_strContent;
	}

	public void setContent(String str) {
		m_strContent = str;
		if (m_strContent.startsWith("<p>")
				&& m_strContent.endsWith("</p>")
				&& m_strContent.indexOf("</p>") == m_strContent
						.lastIndexOf("</p>"))
			m_strContent = m_strContent.substring(3, m_strContent.length() - 4);
		m_iZoneCount = StringUtils.countMatches(str, "$ZONE$");
	}

	public int getZoneCount() {
		return m_iZoneCount;
	}

	/**
	 * 从Cache中获取内容
	 */
	public static ApContent getFromCache(AdbAdapter adapter, long contentID,
			boolean forceCheck) throws Exception {
		ApContent content = null;
		String key = Long.toString(contentID);

		content = m_htContents.get(key);

		if (content != null) {
			if (!adapter.syncCheck(content, forceCheck, false)) {
				// 不同步时不直接在原对象上进行同步，以防影响已经获取该对象的线程的处理
				m_htContents.remove(key);
				content = null;
			}
		}

		if (content == null) { // 需要加载
			content = new ApContent();
			content.m_lContentID = contentID;

			if (adapter.retrieve(content) == null)
				return null;

			m_htContents.put(key, content);
		}

		return content;
	}

	public boolean isPageContent() {
		return m_lPageID > 0;
	}

	/**
	 * 根据id查找UUID
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ApContent findUuidById(AdbAdapter adapter, long id)
			throws Exception {
		ApContent content = new ApContent();

		content.m_lContentID = id;
		content = adapter.retrieve(content, "loadUUIDWithCache");
		if (content == null)
			throw new Exception("Content " + id + " not found!");
		return content;
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
		ApContent content = new ApContent();

		content.m_strUUID = uuid;
		content = adapter.retrieve(content, "loadIDWithCache");
		if (content == null)
			throw new Exception("Content " + uuid + " not found!");
		return content.m_lContentID;
	}

	public static ApContent newContent(DbAdapter adapter, long siteId,
			long catId, long pageId) throws Exception {
		ApContent content = new ApContent();

		content.m_lSiteID = siteId;
		content.m_lContCatID = catId;
		content.m_lPageID = pageId;
		content.m_strUUID = UUIDGen.get();
		ApResDir dir = new ApResDir();
		dir.m_lSiteID = siteId;
		dir.m_strFullPath = "/sys/cont/";
		adapter.retrieve(dir, "loadByPath");

		ApResDir resDir = ApResDir.createResDir(adapter, siteId, dir, "", true);

		content.m_lResDirID = resDir.m_lResDirID;
		adapter.create(content);

		resDir.setDir(dir, Long.toString(content.m_lContentID));
		adapter.update(resDir, "updateDir");

		return content;

	}
}
