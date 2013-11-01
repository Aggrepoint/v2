package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;

public class ApAppUI extends LogoUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// status
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayStatus(ApApp app) {
		switch (app.m_iStatusID) {
		case ApApp.STATUS_NORMAL:
			return "[@CN:正常@EN:Normal]";
		case ApApp.STATUS_TEST:
			return "[@CN:测试@EN:Test]";
		case ApApp.STATUS_STOP:
			return "[@CN:停止@EN:Stop]";
		}
		return "";
	}

	static SelectOption STATUS = new BasicSelectOption(SelectOption.TYPE_LABEL,
			"status", "", null, null, null);
	static {
		STATUS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(ApApp.STATUS_NORMAL), "[@CN:正常@EN:Normal]", null,
				null, null));
		STATUS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(ApApp.STATUS_TEST), "[@CN:测试@EN:Test]", null, null,
				null));
		STATUS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(ApApp.STATUS_STOP), "[@CN:停止@EN:Stop]", null, null,
				null));
	}

	public static SelectOption getTypeList(ApApp app, boolean nameOnly) {
		return STATUS;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// preview image
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayPreviewImg(ApApp app) throws Exception {
		return getDisplayLogoFile(app.m_lLogoImageID);
	}

	public static ValidateResult checkPreviewImg(ApApp app, AdbAdapter adapter,
			Vector<String> args) throws Exception {
		long[] logoid = new long[1];
		ValidateResult res = checkLogoFile(app.m_previewImage,
				ApImage.IMGTYPE_APPLICATION, adapter, logoid);
		if (res == ValidateResult.PASS && logoid[0] > 0)
			app.m_lLogoImageID = logoid[0];
		return res;
	}
}
