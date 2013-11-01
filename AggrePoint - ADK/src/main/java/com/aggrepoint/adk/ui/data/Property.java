package com.aggrepoint.adk.ui.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.aggrepoint.adk.ui.HideException;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.beanutil.PropertyTypeCode;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;

public class Property extends Matchable implements IAdkConst {
	public String m_strId;
	public String m_strName;
	public String m_strWrapper;
	public String m_strWrapName;
	public String m_strGet;
	public String m_strSet;
	public String m_strSortAsc;
	public String m_strSortDesc;
	public boolean m_bMandatory;
	public String m_strAjaxValidate;
	public Vector<Label> m_vecLabels;
	public Vector<Title> m_vecTitles;
	public Vector<Remarks> m_vecRemarks;
	public Vector<Help> m_vecHelps;
	public Vector<Decoractor> m_vecDecoractors;
	public Vector<DisplayAttribute> m_vecDisplays;
	public Vector<Edit> m_vecEdits;
	public Vector<Validator> m_vecValidators;
	protected BeanProperty m_property;
	protected Class<?> m_wrapper;
	protected Method m_wrapperGetDisplay;
	protected boolean m_bGetDisplayNeedAdapter;
	protected Method m_wrapperGet;
	protected boolean m_bGetNeedAdapter;
	protected Class<?> m_getType;
	protected int m_iGetType;
	protected Method m_wrapperSet;
	protected boolean m_bSetNeedAdapter;
	protected Class<?> m_setType;
	protected int m_iSetType;
	protected Method m_wrapperGetList;
	protected boolean m_bGetListNeedAdapter;

	public Property() {
		m_strId = m_strName = m_strSortAsc = m_strSortDesc = m_strWrapper = "";
		m_vecLabels = new Vector<Label>();
		m_vecTitles = new Vector<Title>();
		m_vecRemarks = new Vector<Remarks>();
		m_vecHelps = new Vector<Help>();
		m_vecDecoractors = new Vector<Decoractor>();
		m_vecDisplays = new Vector<DisplayAttribute>();
		m_vecEdits = new Vector<Edit>();
		m_vecValidators = new Vector<Validator>();
	}

	public static String markupFormat(String str, boolean forTextArea) {
		EnumMarkup markup = MarkupTag.getMarkup();
		if (markup == EnumMarkup.HTML)
			if (forTextArea)
				return StringUtils.toHTML(str, true);
			else
				return StringUtils.toHTML(str);
		else if (markup == EnumMarkup.WML)
			return StringUtils.toWML(str);
		return str;
	}

	public int getTypeCode() {
		if (m_wrapperGet != null)
			return m_iGetType;
		if (m_wrapperSet != null)
			return m_iSetType;
		if (m_property != null)
			return m_property.getTypeCode();
		return PropertyTypeCode.OBJECT;
	}

	public Class<?> getType() {
		if (m_wrapperGet != null)
			return m_getType;
		if (m_wrapperSet != null)
			return m_setType;
		if (m_property != null)
			return m_property.getType();
		return Object.class;
	}

	public boolean hasDisplay() {
		return m_wrapperGetDisplay != null;
	}

	static DbAdapter getDbAdapter() throws Exception {
		return new DbAdapter(
				((HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)).getReuseDbConn());
	}

	public Object getDisplay(Object adb) throws Exception {
		if (m_wrapperGetDisplay == null)
			return null;

		try {
			if (m_bGetDisplayNeedAdapter)
				return m_wrapperGetDisplay.invoke(null, adb, getDbAdapter());
			return m_wrapperGetDisplay.invoke(null, adb);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof HideException)
				throw (HideException) e.getTargetException();
			throw e;
		}
	}

	public Object get(Object adb) throws Exception {
		try {
			if (m_wrapperGet != null)
				if (m_bGetNeedAdapter)
					return m_wrapperGet.invoke(null, adb, getDbAdapter());
				else
					return m_wrapperGet.invoke(null, adb);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof HideException)
				throw (HideException) e.getTargetException();
			throw e;
		}

		return m_property.get(adb);
	}

	public void set(Object adb, Object val) throws Exception {
		if (m_wrapperSet != null) {
			boolean[] noval = new boolean[1];
			val = BeanProperty.convert(val, m_iSetType, noval);
			if (!noval[0])
				if (m_bSetNeedAdapter)
					m_wrapperSet.invoke(null, adb, val, getDbAdapter());
				else
					m_wrapperSet.invoke(null, adb, val);
		} else
			m_property.set(adb, val);
	}

	public Object getListId(Object adb) throws Exception {
		SelectOption so = null;
		if (m_wrapperGetList == null)
			return null;
		if (m_bGetListNeedAdapter)
			so = (SelectOption) m_wrapperGetList.invoke(
					null,
					adb,
					new DbAdapter(((HttpModuleRequest) ThreadContext
							.getAttribute(THREAD_ATTR_REQUEST))
							.getReuseDbConn()), true);
		else
			so = (SelectOption) m_wrapperGetList.invoke(null, adb, true);
		return so == null ? null : so.getId();
	}

	public Collection<? extends SelectOption> getList(Object adb)
			throws Exception {
		SelectOption so = null;
		if (m_wrapperGetList == null)
			return null;
		if (m_bGetListNeedAdapter)
			so = (SelectOption) m_wrapperGetList.invoke(
					null,
					adb,
					new DbAdapter(((HttpModuleRequest) ThreadContext
							.getAttribute(THREAD_ATTR_REQUEST))
							.getReuseDbConn()), false);
		else
			so = (SelectOption) m_wrapperGetList.invoke(null, adb, false);
		return so == null ? null : so.getSubs();
	}

	public String getId() {
		return m_strId;
	}

	public String getName() {
		return m_strName;
	}

	public String getSortAsc() {
		if (m_strSortAsc == null || m_strSortAsc.equals(""))
			return null;
		return m_strSortAsc;
	}

	public String getSortDesc() {
		if (m_strSortDesc == null || m_strSortDesc.equals(""))
			return null;
		return m_strSortDesc;
	}

	public boolean isAjaxValidate() {
		return !(m_strAjaxValidate != null && m_strAjaxValidate
				.equalsIgnoreCase("no"));
	}

	public String getMandatory() {
		return m_bMandatory ? "yes" : "no";
	}

	public void setMandatory(String str) {
		m_bMandatory = str != null && str.equalsIgnoreCase("yes");
	}

	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) {
		for (Edit edit : m_vecEdits)
			edit.m_property = this;
		for (Validator val : m_vecValidators)
			val.m_property = this;
	}

	public String getLabel(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Label label = Matchable.match(m_vecLabels, markup, flags, flags1,
				flags2, flags3);
		if (label == null)
			return null;
		return label.m_strLabel;
	}

	public String getTitle(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Title title = Matchable.match(m_vecTitles, markup, flags, flags1,
				flags2, flags3);
		if (title == null)
			return null;
		return title.m_strTitle;
	}

	public String getRemarks(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Remarks remarks = Matchable.match(m_vecRemarks, markup, flags, flags1,
				flags2, flags3);
		if (remarks == null)
			return null;
		return remarks.m_strRemarks;
	}

	public String getHelp(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Help help = Matchable.match(m_vecHelps, markup, flags, flags1, flags2,
				flags3);
		if (help == null)
			return null;
		return help.m_strHelp;
	}

	public Vector<Decoractor> getDecoractors(String markup,
			HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		return Matchable.matchAll(m_vecDecoractors, markup, flags, flags1,
				flags2, flags3);
	}

	public String getDisplayAttribute(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3, String type) {
		for (DisplayAttribute display : Matchable.matchAll(m_vecDisplays,
				markup, flags, flags1, flags2, flags3))
			if (display.m_strType.equals(type))
				return display.m_strValue;

		return null;
	}

	public Edit getEdit(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		return Matchable.match(m_vecEdits, markup, flags, flags1, flags2,
				flags3);
	}

	public Vector<Validator> getValidators(String markup,
			HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		return Matchable.matchAll(m_vecValidators, markup, flags, flags1,
				flags2, flags3);
	}
}
