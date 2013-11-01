package com.aggrepoint.su.core.data;

import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;

public class ApBPageContentUI {
	// ///////////////////////////////////////////////////////////////////////
	//
	// type
	//
	// //////////////////////////////////////////////////////////////////////
	public static String getDisplayType(ApBPageContent cont) {
		if (cont.isWindow())
			return "窗口";
		if (cont.isPageContent())
			return "页面内容";
		return "公用内容";
	}

	static SelectOption INHERITABLE = new BasicSelectOption(
			SelectOption.TYPE_LABEL, "inheritable", "", null, null, null);
	static {
		INHERITABLE.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "0",
				"不继承", null, null, null));
		INHERITABLE.addSub(new BasicSelectOption(SelectOption.TYPE_OPTION, "1",
				"继承", null, null, null));
	}

	public static SelectOption getInheList(ApBPageContent cont, boolean nameOnly) {
		return INHERITABLE;
	}

	public static String getInhe(ApBPageContent cont) {
		return cont.m_bInheritable ? "1" : "0";
	}

	public static void setInhe(ApBPageContent cont, String inhe) {
		cont.m_bInheritable = inhe.equalsIgnoreCase("1");
	}
}
