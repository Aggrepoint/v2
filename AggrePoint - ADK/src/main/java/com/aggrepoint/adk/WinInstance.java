package com.aggrepoint.adk;

import java.util.Hashtable;

import com.aggrepoint.adk.data.WinletDef;

public class WinInstance {
	String m_strIId;
	ViewInstance m_view;
	static Hashtable<String, WinletDef> m_htEmbeddedDef;

	public WinInstance(String iid, WinletDef def, Winlet winlet) {
		m_strIId = iid;
		m_view = new ViewInstance(this, iid, winlet, def, def.m_view, null);
	}

	public ViewInstance findView(String vid) {
		return m_view.find(vid);
	}

	WinletDef getEmbeddedDef(String clzName) throws Exception {
		if (m_htEmbeddedDef == null)
			m_htEmbeddedDef = new Hashtable<String, WinletDef>();
		WinletDef def = m_htEmbeddedDef.get(clzName);
		if (def == null) {
			def = WinletHelper
					.getWinletDef(m_view.m_winlet.getClass(), clzName);
			// associate the def with the icebean.xml context to inheriate
			// retcode, parameter etc. definition
			def.m_parent = m_view.m_winletDef.m_parent;
			m_htEmbeddedDef.put(clzName, def);
		}
		return def;
	}
}
