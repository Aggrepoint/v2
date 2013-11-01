package com.aggrepoint.adk.ui.data;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.ui.BasicSelectOption;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.common.TypeCast;

/**
 * 5 types of option list, try in following order: <br>
 * <br>
 * 1) If the property has wrapper and the wrapper has the getXXXList() method
 * defined for the property, then get the list from property.<br>
 * 2) Based on list property, list should refers a method in the ADB which can
 * returns a list of options. This method should has no parameter, or has one
 * DbAdapter parameter.<br>
 * <br>
 * 3) Based on factory property. Factory property should refers to a class with
 * a specified method name in the form "class.name". This method optionally have
 * a DbAdapter parameter and should return a list of options.<br>
 * <br>
 * 4) Based on enum property. Enum should refers to a enumeration class which
 * implements the SelectOption interface.<br>
 * <br>
 * 5) Based on property type. Property type should be Enum that implements
 * SelectOption interface.
 * 
 * @author Jim
 * 
 */
public class EditSelect extends Edit implements IAdkConst, UIConst {
	public String m_strList;
	public String m_strFactory;
	public String m_strEnum;
	public String m_strExclude;
	private Class<? extends SelectOption> m_enumClass;
	private Method m_factoryMethod;
	private boolean m_bFactoryNeedsDbAdapter;

	public EditSelect() {
		m_strType = "select";
		m_strEnum = m_strList = m_strFactory = "";
	}

	public void setEnum(String e) throws ClassNotFoundException {
		if (e != null && !e.equals("")) {
			m_strEnum = e;
			m_enumClass = com.icebean.core.common.TypeCast.cast(Class
					.forName(m_strEnum));
		}
	}

	public String getEnum() {
		return m_strEnum;
	}

	public void setFactory(String f) throws ClassNotFoundException,
			NoSuchMethodException {
		if (f != null && !f.equals("")) {
			m_strFactory = f;
			String name, method;
			int idx = f.lastIndexOf(".");
			if (idx <= 0) {
				name = f;
				method = SELECT_OPTION_FACTORY_METHOD;
			} else {
				name = f.substring(0, idx);
				method = f.substring(idx + 1);
			}
			Class<?> c = com.icebean.core.common.TypeCast.cast(Class
					.forName(name));
			try {
				m_factoryMethod = c.getMethod(method);
				m_bFactoryNeedsDbAdapter = false;
			} catch (NoSuchMethodException e) {
				m_factoryMethod = c.getMethod(method, AdbAdapter.class);
				m_bFactoryNeedsDbAdapter = true;
			}
		}
	}

	public String getFactory() {
		return m_strFactory;
	}

	private DbAdapter getDbAdapter() throws Exception {
		return new DbAdapter(((HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST)).getReuseDbConn());
	}

	private Collection<? extends SelectOption> getOptionsBeforeExclude(ADB adb)
			throws Exception {
		if (adb != null && m_property.m_wrapperGetList != null)
			return TypeCast.cast(m_property.getList(adb));

		if (adb != null && m_strList != null && !m_strList.equals("")) {
			try {
				return TypeCast.cast(adb.getClass().getMethod(m_strList)
						.invoke(adb));
			} catch (NoSuchMethodException e) {
				return TypeCast.cast(adb.getClass().getMethod(m_strList,
						AdbAdapter.class).invoke(adb, getDbAdapter()));
			}
		}

		if (m_factoryMethod != null) {
			if (m_bFactoryNeedsDbAdapter) {
				return TypeCast.cast(m_factoryMethod.invoke(null,
						getDbAdapter()));
			} else {
				return TypeCast.cast(m_factoryMethod.invoke(null));
			}
		}

		if (m_enumClass != null) {
			Vector<SelectOption> vec = new Vector<SelectOption>();
			for (SelectOption option : m_enumClass.getEnumConstants())
				vec.add(option);
			return vec;
		}

		if (m_property.getType().isEnum()) {
			Vector<SelectOption> vec = new Vector<SelectOption>();
			SelectOption[] col = TypeCast.cast(m_property.getType()
					.getEnumConstants());
			for (SelectOption option : col)
				vec.add(option);
			return vec;
		}

		return null;
	}

	public Collection<? extends SelectOption> getOptions(ADB adb)
			throws Exception {
		Collection<? extends SelectOption> col = getOptionsBeforeExclude(adb);
		if (col == null || m_strExclude == null || m_strExclude.equals(""))
			return col;

		HashSet<String> tokens = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(m_strExclude, " ,");
		while (st.hasMoreTokens())
			tokens.add(st.nextToken());

		Vector<SelectOption> newcol = new Vector<SelectOption>();
		for (SelectOption so : col) {
			if (!tokens.contains(so.getId()))
				newcol.add(so);
		}

		return newcol;
	}

	public String getJsonOptions(ADB adb) throws Exception {
		return BasicSelectOption.toJson(getOptions(adb));
	}

	public Collection<? extends SelectOption> getOptions() throws Exception {
		return getOptions(null);
	}

	public Collection<? extends SelectOption> getFlatOptions(ADB adb)
			throws Exception {
		return BasicSelectOption.flatten(getOptions(adb), null);
	}

	public Collection<? extends SelectOption> getFlatOptions() throws Exception {
		return getFlatOptions(null);
	}

	public String getJsonOptions() throws Exception {
		return BasicSelectOption.toJson(getOptions());
	}

	public void populate(ADB adb, String val) throws Exception {
		if (m_strList != null && !m_strList.equals("")) {
			m_property.set(adb, val);
			return;
		}

		if (m_property.getType().isEnum()) {
			SelectOption[] col = TypeCast.cast(m_property.getType()
					.getEnumConstants());
			for (SelectOption option : col)
				if (option.getId().equals(val)) {
					m_property.set(adb, option);
					return;
				}
		}

		m_property.set(adb, val);
	}
}
