package com.aggrepoint.adk;

import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.data.WinletDef;
import com.aggrepoint.adk.data.WinletViewDef;

public class ViewInstance {
	WinInstance m_wis;
	String m_strViewId;
	String m_strViewPath;
	Winlet m_winlet;
	WinletDef m_winletDef;
	WinletViewDef m_viewDef;
	ViewInstance m_parent;
	Vector<ViewInstance> m_vecSubViews;
	Hashtable<String, String> m_htParams;

	public ViewInstance(WinInstance wi, String viewid, Winlet winlet,
			WinletDef winletDef, WinletViewDef viewDef,
			Hashtable<String, String> htParams) {
		m_wis = wi;
		m_strViewId = viewid;
		m_strViewPath = viewid;
		m_winlet = winlet;
		m_winletDef = winletDef;
		m_viewDef = viewDef;
		m_htParams = htParams;
		m_parent = null;
		m_vecSubViews = null;
	}

	public void setParams(Hashtable<String, String> params) {
		m_htParams = params;
	}

	public Hashtable<String, String> getParams() {
		return m_htParams;
	}

	public String getParam(String name) {
		String str = null;
		if (m_htParams != null)
			str = m_htParams.get(name);
		if (str != null)
			return str;
		if (m_parent == null)
			return null;
		return m_parent.getParam(name);
	}

	public String getId() {
		return m_strViewId;
	}

	public WinletViewDef getViewDef() {
		return m_viewDef;
	}

	public Winlet getWinlet() {
		return m_winlet;
	}

	public void clearSub() {
		m_vecSubViews = null;
	}

	public ViewInstance addSub(IServerContext context, IModuleRequest req,
			Winlet winlet, String viewname, Hashtable<String, String> htParams)
			throws Exception {
		WinletDef def;

		if (winlet == null) {
			def = m_winletDef;
			winlet = m_winlet;
		} else {
			def = m_wis.getEmbeddedDef(winlet.getClass().getName());
		}

		for (WinletViewDef view : def.m_vecViews)
			if (view.m_strPath.equals(viewname)) {
				if (m_vecSubViews == null)
					m_vecSubViews = new Vector<ViewInstance>();
				ViewInstance inst = new ViewInstance(m_wis, m_strViewId + "_"
						+ Integer.toString(m_vecSubViews.size() + 1), winlet,
						def, view, htParams);
				inst.m_parent = this;
				m_vecSubViews.add(inst);
				return inst;
			}

		throw new Exception("Unable to find view \"" + viewname
				+ "\" in winlet " + winlet.getClass().getName());
	}

	ViewInstance find(String viewid) {
		if (m_strViewId.equals(viewid))
			return this;
		if (m_vecSubViews != null && viewid.startsWith(m_strViewId + "_")) {
			for (ViewInstance view : m_vecSubViews) {
				if (viewid.equals(view.m_strViewId))
					return view;

				if (viewid.startsWith(view.m_strViewId + "_"))
					return view.find(viewid);
			}
		}
		return null;
	}

	void getUpdateViews(Vector<ViewToUpdate> updates,
			Vector<String> vecUpdates, String defaultClzName) {
		for (ViewToUpdate t : updates) {
			if (t.clzName == null) {
				if (m_winletDef.m_strModuleClass.equals(defaultClzName)
						&& t.viewName.equals(m_viewDef.m_strPath)) {
					if (t.ensureVisible)
						vecUpdates.add("!" + m_strViewId);
					else
						vecUpdates.add(m_strViewId);
					return;
				}
			} else {
				if ((m_winletDef.m_strModuleClass.equals(t.clzName) || m_winletDef.m_strModuleClass
						.endsWith("." + t.clzName))
						&& t.viewName.equals(m_viewDef.m_strPath)) {
					if (t.ensureVisible)
						vecUpdates.add("!" + m_strViewId);
					else
						vecUpdates.add(m_strViewId);
					return;
				}
			}
		}

		if (m_vecSubViews != null)
			for (ViewInstance vi : m_vecSubViews)
				vi.getUpdateViews(updates, vecUpdates, defaultClzName);
	}

	public String translateUpdateViews(IModuleRequest req, String from) {
		if (from == null || from.equals("")) {
			return "";
		}

		boolean bUpdateWholeWin = false;
		Vector<ViewToUpdate> updates = ViewToUpdate.parse(from);
		Vector<String> vecUpdates = new Vector<String>();
		Vector<ViewToUpdate> toRemove = new Vector<ViewToUpdate>();
		for (ViewToUpdate update : updates) {
			if (update.clzName == null) {
				if (update.viewName.equals("window")) {
					bUpdateWholeWin = true;
					toRemove.add(update);
				} else if (update.viewName.equals("parent")) {
					if (m_parent != null)
						update.viewName = m_parent.m_strViewId;
					else {
						bUpdateWholeWin = true;
						toRemove.add(update);
					}
				} else if (update.viewName.equals("area"))
					return "area";
				else if (update.viewName.equals("page"))
					return "page";
			}
		}

		updates.removeAll(toRemove);

		// { Match all other windows within same page
		for (WinInstance wi : WinletHelper.getWinInstancesInPage(req)) {
			if (wi == m_wis)
				continue;

			wi.m_view.getUpdateViews(updates, vecUpdates,
					m_winletDef.m_strModuleClass);
		}
		// }

		if (!bUpdateWholeWin)
			m_wis.m_view.getUpdateViews(updates, vecUpdates,
					m_winletDef.m_strModuleClass);
		else
			vecUpdates.add(m_wis.m_strIId);

		StringBuffer sb = new StringBuffer();
		boolean bFirst = true;
		for (String str : vecUpdates) {
			if (bFirst)
				bFirst = false;
			else
				sb.append(",");
			sb.append(str);
		}

		return sb.toString();
	}

	public Winlet getEmbedded(Class<?> c) throws Exception {
		if (m_vecSubViews == null)
			return null;

		for (ViewInstance sub : m_vecSubViews) {
			if (sub.m_winlet.getClass().equals(c))
				return sub.m_winlet;
		}
		Winlet winlet = null;
		for (ViewInstance sub : m_vecSubViews) {
			winlet = sub.getEmbedded(c);
			if (winlet != null)
				return winlet;
		}
		return null;
	}

	public Winlet getEmbedded(String c) throws Exception {
		if (m_vecSubViews == null)
			return null;

		for (ViewInstance sub : m_vecSubViews) {
			if (sub.m_winlet.getClass().getName().equals(c))
				return sub.m_winlet;
		}
		Winlet winlet = null;
		for (ViewInstance sub : m_vecSubViews) {
			winlet = sub.getEmbedded(c);
			if (winlet != null)
				return winlet;
		}
		return null;
	}
}
