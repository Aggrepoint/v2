package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.AdbAdapter;

public class ApBranchGroupUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// markup
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayMarkup(ApBranchGroup group) {
		return EnumMarkup.fromId(group.m_iMarkupType).name();
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// branch
	//
	// //////////////////////////////////////////////////////////////////////

	public static SelectOption getBranchList(ApBranchGroup group,
			AdbAdapter adapter, boolean forAdmin) throws Exception {
		SelectOption option = new BasicSelectOption(SelectOption.TYPE_LABEL,
				"branch", "", null, null, null);

		ApBranch br = new ApBranch();
		br.m_lSiteID = group.m_lSiteID;
		br.m_iOfficialFlag = group.m_iOfficialFlag;
		for (ApBranch b : adapter.retrieveMulti(br, "loadNotGroupBySite", null))
			option.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Long
					.toString(b.m_lBranchID), b.m_strBranchName + " ["
					+ EnumMarkup.fromId(b.m_iMarkupType).name() + "]", null,
					null, null));

		return option;
	}
}
