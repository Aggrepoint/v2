package com.aggrepoint.su.core.data;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Category;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.Log4jIniter;

/**
 * 
 * @author YJM
 */
public class ApResDir extends ADB {
	Category m_logger = Log4jIniter.getCategory();

	public long m_lResDirID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public boolean m_bSysFlag;
	public boolean m_bContFlag;
	public boolean m_bChildFlag;
	public long m_lSiteID;
	public long m_lParentDirID;
	public String m_strDirName;
	public String m_strFullPath;
	public String m_strUUID;

	public Vector<ApRes> m_vecReses;
	public Vector<ApResDir> m_vecChildDirs;

	public ApResDir() throws AdbException {
		m_strDirName = m_strFullPath = "";
	}

	public long getId() {
		return m_lResDirID;
	}

	public String getName() {
		return m_strDirName == null ? "" : m_strDirName;
	}

	public void setName(String str) {
		m_strDirName = str;
	}

	public boolean isHasChild() {
		return m_bChildFlag;
	}

	public String getFullPath() {
		return m_strFullPath;
	}

	public Collection<ApRes> getReses() {
		if (m_vecReses == null)
			m_vecReses = new Vector<ApRes>();
		return m_vecReses;
	}

	public void setReses(Collection<ApRes> col) {
	}

	public Collection<ApResDir> getChildDirs() {
		if (m_vecChildDirs == null)
			m_vecChildDirs = new Vector<ApResDir>();
		return m_vecChildDirs;
	}

	public void setChildDirs(Collection<ApResDir> col) {
	}

	public static ApResDir load(DbAdapter adapter, long dirid, int official)
			throws Exception {
		ApResDir dir = new ApResDir();
		dir.m_lResDirID = dirid;
		dir.m_iOfficialFlag = official;
		return adapter.retrieve(dir, "loadWithCache");
	}

	public ValidateResult checkDir(AdbAdapter adapter, Vector<String> args)
			throws Exception {
		ApResDir dir = new ApResDir();
		dir.m_iOfficialFlag = m_iOfficialFlag;
		dir.m_strDirName = m_strDirName;
		dir.m_lParentDirID = m_lParentDirID;
		if (dir.m_lParentDirID == 0) {
			dir.m_lSiteID = m_lSiteID;
			if (adapter.retrieve(dir, "loadRootByName") != null)
				return ValidateResult.FAILED;
		} else {
			if (adapter.retrieve(dir, "loadByName") != null)
				return ValidateResult.FAILED;
		}
		return ValidateResult.PASS;
	}

	public void setDir(ApResDir parent, String dir) {
		m_strDirName = dir;
		m_strFullPath = parent == null ? "/" + dir + "/" : parent.m_strFullPath
				+ dir + "/";
	}

	public static ApResDir createResDir(AdbAdapter adapter, long siteId,
			ApResDir parent, String dir, boolean contFlag) throws Exception {
		ApResDir resDir = new ApResDir();
		resDir.m_bSysFlag = true;
		resDir.m_lSiteID = siteId;
		resDir.m_strDirName = dir;
		resDir.m_strFullPath = parent == null ? "/" + dir + "/"
				: parent.m_strFullPath + dir + "/";
		resDir.m_strUUID = UUIDGen.get();
		resDir.m_lParentDirID = parent == null ? 0 : parent.m_lResDirID;
		resDir.m_bContFlag = contFlag;
		adapter.create(resDir);
		resDir.setCommonData(ICommDataKey.SEQUENCE, "NO");
		resDir.m_iOfficialFlag = 1;
		adapter.create(resDir);

		if (parent != null && !parent.m_bChildFlag) {
			parent.m_bChildFlag = true;
			adapter.update(parent, "updateAllChildFlag");
		}

		return resDir;
	}

	public void publish(AdbAdapter adapter, String rootDir, int[] counts)
			throws Exception {
		String dir = rootDir + m_strFullPath;

		m_logger.debug("creating directory \"" + dir + "\"");

		new File(dir).mkdirs();
		if (counts != null && counts.length > 0)
			counts[0]++;

		m_logger.debug("done.");

		ApRes res = new ApRes();
		res.m_lResDirID = m_lResDirID;
		res.m_iOfficialFlag = m_iOfficialFlag;
		for (ApRes r : adapter.retrieveMulti(res, "loadByDir", null)) {
			r.m_strFile = dir + r.m_strFileName;
			m_logger.debug("creating file \"" + r.m_strFile + "\"...");
			adapter.retrieve(r, "loadFile");
			m_logger.debug("done");

			if (counts != null && counts.length > 1)
				counts[1]++;
		}

		ApResDir sub = new ApResDir();
		sub.m_lParentDirID = m_lResDirID;
		sub.m_iOfficialFlag = m_iOfficialFlag;
		sub.m_lSiteID = m_lSiteID;
		for (ApResDir s : adapter.retrieveMulti(sub, "loadByDir", null))
			s.publish(adapter, rootDir, counts);
	}
}
