package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;

public class ApFrameUI extends LogoUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// preview image
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayPreviewImg(ApFrame frame) throws Exception {
		return getDisplayLogoFile(frame.m_lPreviewImgID);
	}

	public static ValidateResult checkPreviewImg(ApFrame frame,
			AdbAdapter adapter, Vector<String> args) throws Exception {
		long[] logoid = new long[1];
		ValidateResult res = checkLogoFile(frame.m_previewImage,
				ApImage.IMGTYPE_FRAME, adapter, logoid);
		if (res == ValidateResult.PASS && logoid[0] > 0)
			frame.m_lPreviewImgID = logoid[0];
		return res;
	}
}
