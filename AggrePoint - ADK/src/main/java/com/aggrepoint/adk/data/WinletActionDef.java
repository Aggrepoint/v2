package com.aggrepoint.adk.data;

import com.icebean.core.adb.AdbException;

/**
 * Action定义
 * 
 * @author YJM
 */
public class WinletActionDef extends WinletMethodDef {
	public WinletActionDef() throws AdbException {
		super();
	}

	public void print(String tab, java.io.PrintWriter pw) {
		printStartTag(tab, "path", pw, "action");
		printSubElements(tab, pw);
		printEndTag(tab, pw, "action");
	}
}
