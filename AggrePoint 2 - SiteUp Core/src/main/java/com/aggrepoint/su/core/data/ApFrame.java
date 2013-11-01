package com.aggrepoint.su.core.data;

import java.util.Hashtable;

import com.aggrepoint.adk.FileParameter;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;

/**
 * Frame
 * 
 * @author YJM
 */
public class ApFrame extends ADB {
	public long m_lFrameID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lSiteID;
	public long m_lDefResDirID;
	public long m_lResDirID;
	public int m_iOrder;
	public String m_strContNormal;
	public String m_strContMax;
	public String m_strContMin;
	public String m_strFrameName;
	/** 预览图片编号 */
	public long m_lPreviewImgID;
	public String m_strUUID;

	public FileParameter m_previewImage;

	/** 以下属性用于从XML文件中导入 */
	public long m_lImportID;

	public String m_strLogoContentType;

	public String m_strLogoName;

	public String m_strLogoFile;

	public ApResDir m_dir;

	/** FrameCache */
	static Hashtable<String, ApFrame> m_htFrames = new Hashtable<String, ApFrame>();

	public ApFrame() throws AdbException {
		m_strFrameName = m_strContNormal = m_strContMax = m_strContMin = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strUUID = "";
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
		return m_lFrameID;
	}

	public String getName() {
		return m_strFrameName;
	}

	public long getPreviewImgId() {
		return m_lPreviewImgID;
	}

	/**
	 * 从Cache中获取Frame
	 */
	public static ApFrame getFrame(AdbAdapter adapter, long frameID,
			boolean forceCheck) throws Exception {
		ApFrame frame = null;
		String key = Long.toString(frameID);

		frame = m_htFrames.get(key);

		if (frame != null) {
			if (!adapter.syncCheck(frame, forceCheck, false)) {
				// 不同步时不直接在原对象上进行同步，以防影响已经获取该对象的线程的处理
				m_htFrames.remove(key);
				frame = null;
			}
		}

		if (frame == null) { // 需要加载
			frame = new ApFrame();
			frame.m_lFrameID = frameID;

			if (adapter.retrieve(frame, "loadDetail") == null)
				return null;

			m_htFrames.put(key, frame);
		}

		return frame;
	}

	/**
	 * 根据id查找UUID
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ApFrame findUuidById(AdbAdapter adapter, long id)
			throws Exception {
		ApFrame frame = new ApFrame();

		frame.m_lFrameID = id;
		frame = adapter.retrieve(frame, "loadUUIDWithCache");
		if (frame == null)
			throw new Exception("Frame " + id + " not found!");
		return frame;
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
		ApFrame frame = new ApFrame();

		frame.m_strUUID = uuid;
		frame = adapter.retrieve(frame, "loadIDWithCache");
		if (frame == null)
			throw new Exception("Frame " + uuid + " not found!");
		return frame.m_lFrameID;
	}
}
