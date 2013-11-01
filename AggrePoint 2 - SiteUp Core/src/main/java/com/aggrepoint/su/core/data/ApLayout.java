package com.aggrepoint.su.core.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.FileParameter;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.common.StringUtils;

/**
 * 栏位布局
 * 
 * @author YJM
 */
public class ApLayout extends ADB {
	/** 编号 */
	public long m_lLayoutID;

	public int m_iOfficialFlag;

	public boolean m_bPubFlag;

	public long m_lSiteID;

	/** 名称 */
	public String m_strLayoutName;

	/** 内容 */
	public String m_strContent;

	public int m_iZoneCount;

	/** 预览图片编号 */
	public long m_lPreviewImgID;

	public String m_strUUID;

	/** Cache */
	static Hashtable<Integer, ADBList<ApLayout>> m_htLists = new Hashtable<Integer, ADBList<ApLayout>>();

	/** Cache */
	static Hashtable<Integer, Hashtable<Long, ApLayout>> m_htLayouts = new Hashtable<Integer, Hashtable<Long, ApLayout>>();

	public FileParameter m_previewImage;

	/** 以下属性用于从XML文件中导入 */
	public long m_lImportID;

	public String m_strLogoContentType;

	public String m_strLogoName;

	public String m_strLogoFile;

	public ApLayout() throws AdbException {
		m_strLayoutName = m_strContent = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strUUID = "";
	}

	public long getId() {
		return m_lLayoutID;
	}

	public long getPreviewImgId() {
		return m_lPreviewImgID;
	}

	public String getName() {
		return m_strLayoutName;
	}

	public String getContent() {
		return m_strContent;
	}

	public void setContent(String str) {
		m_strContent = str;
		m_iZoneCount = StringUtils.countMatches(str, "$ZONE$");
	}

	public int getZoneCount() {
		return m_iZoneCount;
	}

	public static Vector<ApLayout> getLayouts(AdbAdapter adapter,
			int officialFlag, boolean forceCheck) throws Exception {
		boolean bReloaded = false;

		ADBList<ApLayout> list = m_htLists.get(officialFlag);
		if (list == null) {
			ApLayout layout = new ApLayout();
			layout.m_iOfficialFlag = officialFlag;
			list = adapter.retrieveMultiDbl(layout, "loadAll", "default");
			m_htLists.put(officialFlag, list);
			bReloaded = true;
		} else
			bReloaded = !list.sync(adapter, forceCheck);

		if (bReloaded) {
			Hashtable<Long, ApLayout> htLayouts = new Hashtable<Long, ApLayout>();
			m_htLayouts.put(officialFlag, htLayouts);

			ApLayout layout;
			for (Enumeration<ApLayout> enm = list.m_vecObjects.elements(); enm
					.hasMoreElements();) {
				layout = enm.nextElement();
				if (layout.m_iOfficialFlag == officialFlag)
					htLayouts.put(new Long(layout.m_lLayoutID), layout);
			}
		}

		return list.m_vecObjects;
	}

	public static ApLayout getLayout(AdbAdapter adapter, long layoutID,
			int officialFlag, boolean forceCheck) throws Exception {
		getLayouts(adapter, officialFlag, forceCheck);
		return m_htLayouts.get(officialFlag).get(new Long(layoutID));
	}

	/**
	 * 根据id查找UUID
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ApLayout findUuidById(AdbAdapter adapter, long id)
			throws Exception {
		ApLayout layout = new ApLayout();

		layout.m_lLayoutID = id;
		layout = adapter.retrieve(layout, "loadUUIDWithCache");
		if (layout == null)
			throw new Exception("Layout " + id + " not found!");
		return layout;
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
		ApLayout layout = new ApLayout();

		layout.m_strUUID = uuid;
		layout = adapter.retrieve(layout, "loadIDWithCache");
		if (layout == null)
			throw new Exception("Layout " + uuid + " not found!");
		return layout.m_lLayoutID;
	}
}
