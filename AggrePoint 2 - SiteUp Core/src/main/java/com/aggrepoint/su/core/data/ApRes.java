package com.aggrepoint.su.core.data;

import java.awt.Image;
import java.io.File;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.common.ImageUtils;

/**
 * 
 * @author YJM
 */
public class ApRes extends ADB {
	public static final int TYPE_IMAGE = 1;
	public static final int TYPE_FLASH = 100;
	public static final int TYPE_CSS = 200;
	public static final int TYPE_JS = 300;

	public long m_lResID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lResDirID;
	public String m_strFileName;
	public String m_strContentType;
	public String m_strFile;
	public int m_iFileType;
	public String m_strSmallIcon;
	public String m_strLargeIcon;
	public String m_strAttribute;
	public int m_iWidth;
	public int m_iHeight;
	public long m_lSize;
	public int m_iOrder;

	public ApRes() throws AdbException {
		m_lResID = m_lResDirID = 0;
		m_strFileName = m_strContentType = m_strFile = m_strAttribute = "";
		m_iOrder = m_iOfficialFlag = 0;
	}

	public String getFileName() {
		return m_strFileName;
	}

	public void setFileName(String fileName) {
		m_strFileName = fileName;
	}

	public void createWithIcons(String tempDir, AdbAdapter adapter)
			throws Exception {
		if (m_strContentType.startsWith("image/")) {
			Image img = javax.imageio.ImageIO.read(new File(m_strFile));
			m_strSmallIcon = tempDir + File.separator + "smallicon.jpg";
			ImageUtils.shrinkImage(img, m_strSmallIcon, 100, 100);
			m_strLargeIcon = tempDir + File.separator + "largeicon.jpg";
			ImageUtils.shrinkImage(img, m_strLargeIcon, 300, 300);
			m_iWidth = img.getWidth(null);
			m_iHeight = img.getHeight(null);
			m_iFileType = TYPE_IMAGE;
		} else if (m_strContentType
				.equalsIgnoreCase("application/x-shockwave-flash")) {
			m_iFileType = TYPE_FLASH;
		} else if (m_strContentType.equalsIgnoreCase("text/css")) {
			m_iFileType = TYPE_CSS;
		} else if (m_strFileName.endsWith(".js"))
			m_iFileType = TYPE_JS;

		adapter.create(this);
	}
}
