package com.aggrepoint.adk;

import java.util.StringTokenizer;
import java.util.Vector;

public class ViewToUpdate {
	public boolean ensureVisible;
	public String clzName;
	public String viewName;

	private ViewToUpdate(String token) {
		if (token.startsWith("!")) {
			ensureVisible = true;
			token = token.substring(1);
		} else
			ensureVisible = false;
		int idx = token.lastIndexOf(".");
		if (idx == -1) {
			clzName = null;
			viewName = token;
		} else {
			clzName = token.substring(0, idx);
			viewName = token.substring(idx + 1);
		}
	}

	public static Vector<ViewToUpdate> parse(String str) {
		if (str == null || str.equals(""))
			return null;

		StringTokenizer st = new StringTokenizer(str, ", ");
		Vector<ViewToUpdate> vec = new Vector<ViewToUpdate>();

		while (st.hasMoreTokens())
			vec.add(new ViewToUpdate(st.nextToken()));

		return vec;
	}
}
