package com.aggrepoint.su.data;

import java.io.File;
import java.util.Vector;

import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.aggrepoint.adk.ui.ValidateResult;

public class ResUploadUI {
	static SelectOption CLEANEXISTING = new BasicSelectOption(
			SelectOption.TYPE_LABEL, "reimb", "", null, null, null);
	static {
		CLEANEXISTING.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION,
				"1", "[@CN:清除@EN:Yes]", null, null, null));
		CLEANEXISTING.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION,
				"0", "[@CN:不清除@EN:No]", null, null, null));
	}

	public static String getCleanExisting(ResUpload item) {
		return item.m_bCleanExisting ? "1" : "0";
	}

	public static void setCleanExisting(ResUpload item, String str) {
		item.m_bCleanExisting = str != null && str.equals("1");
	}

	public static SelectOption getCleanExistingList(ResUpload item,
			boolean nameOnly) throws Exception {
		return CLEANEXISTING;
	}

	public static ValidateResult checkPath(ResUpload item, Vector<String> args) {
		File file = new File(item.m_strPath);
		if (file.exists() && file.isDirectory())
			return ValidateResult.PASS;

		return ValidateResult.FAILED;
	}
}
