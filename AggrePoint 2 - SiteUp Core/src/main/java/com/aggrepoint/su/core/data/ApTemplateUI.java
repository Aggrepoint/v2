package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.ui.SelectOption;

public class ApTemplateUI extends LogoUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// markup
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayMarkup(ApTemplate tmpl) {
		return ApBranchUI.MARKUPS.getSubName(Integer
				.toString(tmpl.m_iMarkupType));
	}

	public static SelectOption getMarkupList(ApTemplate tmpl, boolean nameOnly) {
		return ApBranchUI.MARKUPS;
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// content
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getContent(ApTemplate tmpl) {
		return tmpl.m_strContent;
	}

	public static void setContent(ApTemplate tmpl, String value) {
		tmpl.setContent(value);
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// preview image
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayPreviewImg(ApTemplate tmpl) throws Exception {
		return getDisplayLogoFile(tmpl.m_lPreviewImgID);
	}
}
