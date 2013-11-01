package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.icebean.core.adb.AdbException;

/**
 * URI定义
 * 
 * @author YJM
 */
public class URIDef extends BaseModuleDef {
	/** 包含的子路径 */
	public Vector<URIDef> m_vecSubUris;
	/** 包含的Winlet － 只有在顶级URI中才能包含 */
	public Vector<WinletDef> m_vecWins;

	public URIDef() throws AdbException {
		m_vecSubUris = new Vector<URIDef>();
		m_vecWins = null;
	}

	public Vector<WinletDef> getWins() {
		if (m_vecWins == null)
			m_vecWins = new Vector<WinletDef>();
		return m_vecWins;
	}

	public void setWins(Vector<WinletDef> c) {
	}

	/**
	 * 根据路径查找相应的URI
	 */
	public URIDef findUri(String uri) {
		StringTokenizer st = new StringTokenizer(uri, "/");
		URIDef tempUri;

		URIDef uriDef = this;
		while (st.hasMoreTokens()) {
			String path = st.nextToken();
			boolean bMatched = false;
			for (Enumeration<URIDef> enm = uriDef.m_vecSubUris.elements(); enm
					.hasMoreElements();) {
				tempUri = enm.nextElement();
				if (tempUri.m_strPath.equals(path)) {
					bMatched = true;
					uriDef = tempUri;
					break;
				}
			}

			if (!bMatched) {
				if (uriDef.m_bExpandMatch)
					return uriDef;

				// 没有找到路径对应的URI定义
				return null;
			}
		}

		return uriDef;
	}

	/**
	 * 根据路径查找相应的WinletViewDef或ActionDef
	 */
	public BaseModuleDef findWin(String uri) {
		StringTokenizer st = new StringTokenizer(uri, "/");
		if (!st.hasMoreTokens()) // 必须有一级路径
			return null;

		String path = st.nextToken();
		if (st.hasMoreTokens()) // 不能有多于二级路径
			return null;

		for (WinletDef win : m_vecWins)
			if (win.m_strPath.equals(path))
				return win;

		return null;
	}

	public void print(String tab, java.io.PrintWriter pw) {
		printStartTag(tab, "path", pw, "uri");
		printSubElements(tab, pw);
		for (Enumeration<URIDef> enm = m_vecSubUris.elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw);
		for (Enumeration<WinletDef> enm = getWins().elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw);
		printEndTag(tab, pw, "uri");
	}
}
