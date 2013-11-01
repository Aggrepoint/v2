package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.ui.ValidateResult;
import com.aggrepoint.adk.ui.ValidateResultType;
import com.icebean.core.adb.AdbAdapter;

public class LogoUI {
	/** 信息Bundle */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	// ///////////////////////////////////////////////////////////////////////
	//
	// logoFile
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayLogoFile(long logId) throws Exception {
		if (logId > 0)
			return "<img src='/ap2/res/img?id=" + logId + "'/>";
		return "";
	}

	public static ValidateResult checkLogoFile(FileParameter logoFile,
			int imgType, AdbAdapter adapter, long[] logoid) throws Exception {
		if (logoFile == null)
			return ValidateResult.PASS;

		if (logoFile.m_lSize <= 0)
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					m_msg.getMessage("logotoolarge", ""));

		if (!logoFile.m_strContentType.startsWith("image"))
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					m_msg.getMessage("logoformatwrong", ""));

		if (logoFile.m_strContentType.indexOf("jpeg") < 0
				&& logoFile.m_strContentType.indexOf("gif") < 0)
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					m_msg.getMessage("logoformatwrong", ""));

		ApImage image = new ApImage();
		// Not deleting existing image from DB because of if something
		// goes wrong and user don't save the changes then the user will
		// still be associating with existing image

		image.m_iImageType = imgType;
		image.m_strImageName = logoFile.getFileName();
		image.m_strContentType = logoFile.m_strContentType;
		image.m_strImage = logoFile.m_strFullPath;
		adapter.create(image);

		logoid[0] = image.m_lImageID;
		return ValidateResult.PASS;
	}

}
