package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;

public class ApWindowUI extends LogoUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// preview image
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayPreviewImg(ApWindow win) throws Exception {
		return getDisplayLogoFile(win.m_lPreviewImageID);
	}

	public static ValidateResult checkPreviewImg(ApWindow win,
			AdbAdapter adapter, Vector<String> args) throws Exception {
		long[] logoid = new long[1];
		ValidateResult res = checkLogoFile(win.m_previewImage,
				ApImage.IMGTYPE_WINDOW, adapter, logoid);
		if (res == ValidateResult.PASS && logoid[0] > 0)
			win.m_lPreviewImageID = logoid[0];
		return res;
	}
}
