package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 图片
 * 
 * @author YJM
 */
public class ApImage extends ADB {
	public static final int IMGTYPE_SITE = 15;
	public static final int IMGTYPE_TEMPLATE = 11;
	public static final int IMGTYPE_LAYOUT = 16;
	public static final int IMGTYPE_FRAME = 17;
	public static final int IMGTYPE_APPLICATION = 13;
	public static final int IMGTYPE_WINDOW = 14;
	public static final int IMGTYPE_CONTENT = 18;

	public long m_lImageID;
	public int m_iImageType;
	/** 图片内容类型 */
	public String m_strContentType;
	/** 图片文件名称 */
	public String m_strImageName;
	/** 图片文件 */
	public String m_strImage;
	/** 图片宽度 */
	public int m_iWidth;
	/** 图片高度 */
	public int m_iHeight;
	/** 图片用户类型 */
	public int m_iUserType;

	public ApImage() throws AdbException {
		m_lImageID = 0;
		m_strContentType = m_strImage = m_strImageName = "";
		m_iWidth = m_iHeight = m_iUserType = 0;
	}
}
