package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.AdbAdapter;

public class ApBPageUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// type
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayType(ApBPage page) {
		switch (page.m_iPageType) {
		case ApBPage.TYPE_LINK:
			return "[@CN:链接@EN:Link]";
		case ApBPage.TYPE_PAGE:
			return "[@CN:页面@EN:Page]";
		}
		return "";
	}

	static SelectOption TYPES = new BasicSelectOption(SelectOption.TYPE_LABEL,
			"type", "", null, null, null);
	static {
		TYPES.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(ApBPage.TYPE_PAGE), "[@CN:页面@EN:Page]", null, null,
				null));
		TYPES.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, Integer
				.toString(ApBPage.TYPE_LINK), "[@CN:链接@EN:Link]", null, null,
				null));
	}

	public static SelectOption getTypeList(ApBPage page, boolean nameOnly) {
		return TYPES;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// template
	//
	// //////////////////////////////////////////////////////////////////////

	public static String getDisplayTmpl(ApBPage page, AdbAdapter adapter)
			throws Exception {
		ApTemplate tmpl = ApTemplate.getTemplate(adapter, page.m_lTemplateID,
				false);
		if (tmpl == null)
			return "";

		if (page.m_bInheritTmpl)
			return "继承上级 (" + tmpl.m_strTmplName + ")";

		return tmpl.m_strTmplName;
	}

	public static SelectOption getTmplList(ApBPage page, AdbAdapter adapter,
			boolean nameOnly) throws Exception {
		SelectOption option = new BasicSelectOption(SelectOption.TYPE_LABEL,
				"template", "", null, null, null);
		ApTemplate template = new ApTemplate();
		template.m_iOfficialFlag = page.m_iOfficialFlag;
		template.m_lSiteID = page.m_lSiteID;
		template.m_iMarkupType = page.m_iMarkupType;
		option.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0",
				"[@CN:继承上级@EN:Inherit]", null, null, null));

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

	public static String getTmpl(ApBPage page) {
		return Long.toString(page.m_lTemplateID);
	}

	public static void setTmpl(ApBPage page, String val) {
		long l = Long.parseLong(val);

		// 需要加入检查
		if (l <= 0) {
			page.m_lTemplateID = 0;
			page.m_bInheritTmpl = true;
		} else
			page.m_lTemplateID = l;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// flags
	//
	// //////////////////////////////////////////////////////////////////////

	public static String getDisplaySkipToSub(ApBPage page) {
		return page.m_bSkipToSub ? "是" : "否";
	}

	public static String getDisplayHide(ApBPage page) {
		return page.m_bHide ? "是" : "否";
	}

	public static String getDisplayResetWin(ApBPage page) {
		return page.m_bResetWin ? "是" : "否";
	}

	// ///////////////////////////////////////////////////////////////////////
	// UseMap
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayUseMap(ApBPage page) {
		if (page.m_bUseMap)
			return "启用映射";
		if (page.m_bExpandMatch)
			return "匹配扩展";
		return "无";
	}

	static SelectOption USEMAP = new BasicSelectOption(SelectOption.TYPE_LABEL,
			"usemap", "", null, null, null);
	static {
		USEMAP.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0", "无",
				null, null, null));
		USEMAP.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "1",
				"启用映射", null, null, null));
		USEMAP.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "2",
				"匹配扩展", null, null, null));
	}

	public static SelectOption getUseMapList(ApBPage page, boolean nameOnly) {
		return USEMAP;
	}
}
