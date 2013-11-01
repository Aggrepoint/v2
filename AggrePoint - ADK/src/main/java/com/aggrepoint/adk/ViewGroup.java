package com.aggrepoint.adk;

import java.util.Collection;

public class ViewGroup {
	public static class View {
		String id;
		String name;

		public View(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	String m_strId;
	Collection<View> m_views;
	String m_strSelected;

	public ViewGroup(String id, Collection<View> views) {
		m_strId = id;
		m_views = views;
		if (m_views != null)
			for (View view : m_views) {
				m_strSelected = view.id;
				return;
			}
	}

	public ViewGroup(String id, Collection<View> views, String selected) {
		m_strId = id;
		m_views = views;
		m_strSelected = selected;
	}

	public String getId() {
		return m_strId;
	}

	public void reset() {
		if (m_views == null)
			m_strSelected = null;
		else
			for (View view : m_views) {
				m_strSelected = view.id;
				return;
			}
	}

	public Collection<View> getViews() {
		return m_views;
	}

	public String getSelectedId() {
		return m_strSelected;
	}

	public boolean isSelected(String id) {
		return m_strSelected != null && id != null && m_strSelected.equals(id);
	}

	public void select(String id) {
		for (View view : m_views)
			if (view.id.equals(id)) {
				m_strSelected = id;
				return;
			}
	}

	public View getSelected() {
		for (View view : m_views)
			if (view.id.equals(m_strSelected))
				return view;
		return null;
	}
}
