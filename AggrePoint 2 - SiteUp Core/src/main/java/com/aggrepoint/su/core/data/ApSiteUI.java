package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;

public class ApSiteUI extends LogoUI {
	public static String getDisplayLogoFile(ApSite site) throws Exception {
		return getDisplayLogoFile(site.m_lSiteLogoID);
	}

	public static ValidateResult checkLogoFile(ApSite site, AdbAdapter adapter,
			Vector<String> args) throws Exception {
		long[] logoid = new long[1];
		ValidateResult res = checkLogoFile(site.m_logoFile,
				ApImage.IMGTYPE_SITE, adapter, logoid);
		if (res == ValidateResult.PASS && logoid[0] > 0)
			site.m_lSiteLogoID = logoid[0];
		return res;
	}
}
