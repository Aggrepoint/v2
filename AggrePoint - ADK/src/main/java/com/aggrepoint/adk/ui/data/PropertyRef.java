package com.aggrepoint.adk.ui.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import com.aggrepoint.adk.ui.SelectOption;

public class PropertyRef extends Property {
	private Property m_ref;

	public PropertyRef() {
	}

	public int getTypeCode() {
		return m_ref.getTypeCode();
	}

	public Class<?> getType() {
		return m_ref.getType();
	}

	public boolean hasDisplay() {
		return m_ref.hasDisplay();
	}

	public Object getDisplay(Object adb) throws Exception {
		return m_ref.getDisplay(adb);
	}

	public Object get(Object adb) throws Exception {
		return m_ref.get(adb);
	}

	public void set(Object adb, Object val) throws Exception {
		m_ref.set(adb, val);
	}

	public Object getListId(Object adb) throws Exception {
		return m_ref.getListId(adb);
	}

	public Collection<? extends SelectOption> getList(Object adb)
			throws Exception {
		return m_ref.getList(adb);
	}

	public void setRef(Property ref) {
		m_ref = ref;
		for (Edit edit : m_vecEdits)
			edit.m_property = ref;
	}

	public Property getRef() {
		return m_ref;
	}

	public String getId() {
		String str = super.getId();
		if (str == null || str.equals(""))
			str = m_ref.getId();
		return str;
	}

	public String getSortAsc() {
		String str = super.getSortAsc();
		if (str == null)
			str = m_ref.getSortAsc();
		return str;
	}

	public String getSortDesc() {
		String str = super.getSortDesc();
		if (str == null)
			str = m_ref.getSortDesc();
		return str;
	}

	public boolean isAjaxValidate() {
		if (m_strAjaxValidate != null && !m_strAjaxValidate.equals(""))
			return m_strAjaxValidate.equalsIgnoreCase("yes");
		return m_ref.isAjaxValidate();
	}

	public String getMandatory() {
		return m_ref.getMandatory();
	}

	public String getLabel(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		String label = super.getLabel(markup, flags, flags1, flags2, flags3);
		if (label == null && m_ref != null)
			label = m_ref.getLabel(markup, flags, flags1, flags2, flags3);
		return label;
	}

	public String getRemarks(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		String remarks = super
				.getRemarks(markup, flags, flags1, flags2, flags3);
		if (remarks == null && m_ref != null)
			remarks = m_ref.getRemarks(markup, flags, flags1, flags2, flags3);
		return remarks;
	}

	public String getHelp(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		String help = super.getHelp(markup, flags, flags1, flags2, flags3);
		if (help == null && m_ref != null)
			help = m_ref.getHelp(markup, flags, flags1, flags2, flags3);
		return help;
	}

	public Vector<Decoractor> getDecoractors(String markup,
			HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		Vector<Decoractor> decos = super.getDecoractors(markup, flags, flags1,
				flags2, flags3);
		if ((decos == null || decos.size() == 0) && m_ref != null)
			decos = m_ref.getDecoractors(markup, flags, flags1, flags2, flags3);
		return decos;
	}

	public String getDisplayAttribute(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3, String type) {
		String attr = super.getDisplayAttribute(markup, flags, flags1, flags2,
				flags3, type);
		if (attr == null && m_ref != null)
			attr = m_ref.getDisplayAttribute(markup, flags, flags1, flags2,
					flags3, type);
		return attr;
	}

	public Edit getEdit(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Edit edit = super.getEdit(markup, flags, flags1, flags2, flags3);
		if (edit == null && m_ref != null)
			edit = m_ref.getEdit(markup, flags, flags1, flags2, flags3);
		return edit;
	}

	public Vector<Validator> getValidators(String markup,
			HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		Vector<Validator> v = super.getValidators(markup, flags, flags1,
				flags2, flags3);
		if (v == null || v.size() == 0)
			v = m_ref.getValidators(markup, flags, flags1, flags2, flags3);
		return v;
	}
}
