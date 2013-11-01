package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;

public class ApLayoutUI extends LogoUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// preview image
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayPreviewImg(ApLayout layout) throws Exception {
		return getDisplayLogoFile(layout.m_lPreviewImgID);
	}

	public static ValidateResult checkPreviewImg(ApLayout layout,
			AdbAdapter adapter, Vector<String> args) throws Exception {
		long[] logoid = new long[1];
		ValidateResult res = checkLogoFile(layout.m_previewImage,
				ApImage.IMGTYPE_LAYOUT, adapter, logoid);
		if (res == ValidateResult.PASS && logoid[0] > 0)
			layout.m_lPreviewImgID = logoid[0];
		return res;
	}
}
