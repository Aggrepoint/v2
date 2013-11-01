package com.aggrepoint.adk;

import java.util.Hashtable;

import com.aggrepoint.adk.ui.PropertyInstance;

public class ViewForm {
	String m_strName;
	Hashtable<String, PropertyInstance> m_htProperties;

	public ViewForm(String name) {
		m_strName = name;
	}

	public void addProperty(PropertyInstance pi) {
		if (m_htProperties == null)
			m_htProperties = new Hashtable<String, PropertyInstance>();
		m_htProperties.put(pi.getId(), pi);
	}

	public PropertyInstance getProperty(String id) {
		if (m_htProperties == null)
			return null;
		return m_htProperties.get(id);
	}
}
