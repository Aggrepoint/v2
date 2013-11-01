package com.aggrepoint.adk;

import com.aggrepoint.adk.data.WinletViewDef;

public class WinletAccess {
	public static String getState(Winlet winlet, WinletViewDef def) {
		return winlet.getState(def);
	}

	public static void resetState(Winlet winlet, WinletViewDef def) {
		winlet.resetState(def);
	}
}
