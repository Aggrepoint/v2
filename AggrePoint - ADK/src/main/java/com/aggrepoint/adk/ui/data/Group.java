package com.aggrepoint.adk.ui.data;

import java.util.HashSet;
import java.util.Vector;

import com.icebean.core.adb.ADB;

public class Group extends ADB {
	public String m_strId;
	public Vector<PropertyRef> m_vecPropertyRefs;
	public HashSet<Flag> m_hsFlags;
	private HashSet<String> m_hsStrFlags;

	public Group() {
		m_strId = "";
		m_vecPropertyRefs = new Vector<PropertyRef>();
		m_hsFlags = new HashSet<Flag>();
	}

	public HashSet<String> getFlags() {
		if (m_hsStrFlags == null) {
			m_hsStrFlags = new HashSet<String>();
			for (Flag flag : m_hsFlags)
				m_hsStrFlags.add(flag.m_strValue);
		}

		return m_hsStrFlags;
	}
}
