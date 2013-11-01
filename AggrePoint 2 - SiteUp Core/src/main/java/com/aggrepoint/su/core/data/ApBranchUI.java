package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.AdbAdapter;

public class ApBranchUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// type
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayType(ApBranch branch) {
		switch (branch.m_iPsnType) {
		case 2:
			return "[@CN:组合@EN:Group]";
		case 1:
			return "[@CN:动态@EN:Dynamic] "
					+ EnumMarkup.fromId(branch.m_iMarkupType).name();
		case 0:
			return "[@CN:静态@EN:Static] "
					+ EnumMarkup.fromId(branch.m_iMarkupType).name();
		}
		return "";
	}

	static SelectOption TYPES = new BasicSelectOption(SelectOption.TYPE_LABEL,
			"type", "", null, null, null);
	static {
		TYPES.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0",
				"[@CN:静态@EN:Static]", null, null, null));
		TYPES.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "1",
				"[@CN:动态@EN:Dynamic]", null, null, null));
		TYPES.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "2",
				"[@CN:组合@EN:Group]", null, null, null));
	}

	public static SelectOption getTypeList(ApBranch branch, boolean nameOnly) {
		return TYPES;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// markup
	//
	// //////////////////////////////////////////////////////////////////////

	static BasicSelectOption MARKUPS = new BasicSelectOption(
			SelectOption.TYPE_LABEL, "type", "", null, null, null);
	static {
		MARKUPS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(EnumMarkup.HTML.getId()), EnumMarkup.HTML.name(),
				null, null, null));
		MARKUPS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(EnumMarkup.XHTML.getId()), EnumMarkup.XHTML.name(),
				null, null, null));
		MARKUPS.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(EnumMarkup.WML.getId()), EnumMarkup.WML.name(), null,
				null, null));
	}

	static SelectOption SAVEINSESSION = new BasicSelectOption(
			SelectOption.TYPE_LABEL, "type", "", null, null, null);
	static {
		SAVEINSESSION
				.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0",
						"[@CN:不保存在会话中@EN:Don't save in session]", null, null,
						null));
		SAVEINSESSION.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION,
				"1", "[@CN:保存在会话中@EN:Save in session]", null, null, null));
	}

	public static SelectOption getMarkupList(ApBranch branch, boolean nameOnly) {
		if (branch.m_iPsnType == 2)
			return SAVEINSESSION;
		return MARKUPS;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// template
	//
	// //////////////////////////////////////////////////////////////////////

	public static String getDisplayTmpl(long id, AdbAdapter adapter)
			throws Exception {
		ApTemplate tmpl = ApTemplate.getTemplate(adapter, id, false);
		if (tmpl == null)
			return "";

		return tmpl.m_strTmplName;
	}

	public static String getDisplayAdminTmpl(ApBranch branch, AdbAdapter adapter)
			throws Exception {
		return getDisplayTmpl(branch.m_lAdminTmplID, adapter);
	}

	public static String getDisplayDefTmpl(ApBranch branch, AdbAdapter adapter)
			throws Exception {
		return getDisplayTmpl(branch.m_rootPage.m_lTemplateID, adapter);
	}

	public static SelectOption getTmplList(ApBranch branch, AdbAdapter adapter,
			boolean forAdmin) throws Exception {
		SelectOption option = new BasicSelectOption(SelectOption.TYPE_LABEL,
				"template", "", null, null, null);
		ApTemplate template = new ApTemplate();
		template.m_iOfficialFlag = branch.m_iOfficialFlag;
		template.m_lSiteID = branch.m_lSiteID;
		if (forAdmin) {
			template.m_iMarkupType = EnumMarkup.HTML.getId();
			option.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0",
					"[@CN:不指定@EN:Not Use]", null, null, null));
		} else
			template.m_iMarkupType = branch.m_iMarkupType;

		for (ApTemplate tmpl : adapter.retrieveMulti(template,
				"loadSiteSiteForSel", "order"))
			option.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Long
					.toString(tmpl.m_lTemplateID), "[@CN:本站：@EN:Local: ]"
					+ tmpl.m_strTmplName, null, null, null));

		template.m_lSiteID = 100;

		for (ApTemplate tmpl : adapter.retrieveMulti(template,
				"loadSiteSiteForSel", "order"))
			option.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Long
					.toString(tmpl.m_lTemplateID), "[@CN:全局：@EN:Global: ]"
					+ tmpl.m_strTmplName, null, null, null));

		return option;
	}

	public static SelectOption getAdminTmplList(ApBranch branch,
			AdbAdapter adapter, boolean nameOnly) throws Exception {
		return getTmplList(branch, adapter, true);

	}

	public static SelectOption getDefTmplList(ApBranch branch,
			AdbAdapter adapter, boolean nameOnly) throws Exception {
		return getTmplList(branch, adapter, false);

	}

	public static String getDefTmpl(ApBranch branch) {
		return Long.toString(branch.m_rootPage.m_lTemplateID);
	}

	public static void setDefTmpl(ApBranch branch, String val) {
		// 需要加入检查
		branch.m_rootPage.m_lTemplateID = Long.parseLong(val);
	}
}
