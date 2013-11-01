package com.aggrepoint.adk.ui;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.aggrepoint.adk.ui.data.Decoractor;
import com.aggrepoint.adk.ui.data.Edit;
import com.aggrepoint.adk.ui.data.EditCustom;
import com.aggrepoint.adk.ui.data.EditSelect;
import com.aggrepoint.adk.ui.data.EditTextArea;
import com.aggrepoint.adk.ui.data.Group;
import com.aggrepoint.adk.ui.data.Property;
import com.aggrepoint.adk.ui.data.Validator;
import com.icebean.core.adb.ADB;
import com.icebean.core.beanutil.BeanPropertyException;
import com.icebean.core.cfgmgr.ConfigManagerException;
import com.icebean.core.common.StringUtils;

/**
 * 用于将属性和Adapter和data结合在一起，以便获取各种子对象
 * 
 * @author Owner
 * 
 */
public class PropertyInstance implements IAdkConst {
	public Property m_property;
	public UIAdapter m_adapter;
	public Group m_group;
	public ADB m_filter;
	public ADB m_adb;
	public UIAttachedData m_data;

	/** Cached data */
	String label;
	String title;
	String remarks;
	String help;
	Vector<Decoractor> decoractor;
	HashMap<String, String> displayAttrs;
	Edit edit;

	/** Attached Data */
	Hashtable<String, Object> attached;

	public PropertyInstance(Group group, Property property, ADB filter,
			ADB adb, UIAdapter adapter) throws ConfigManagerException,
			UIConfigLoadException, BeanPropertyException {
		m_group = group;
		m_property = property;
		m_adapter = adapter;
		m_filter = filter;
		m_adb = adb;
		m_data = m_adapter.getData(adb);
	}

	public void bindData(ADB data) {
		m_adb = data;
	}

	public ADB getData() {
		return m_adb;
	}

	/**
	 * Clear values cached, can be call after bindData() if values are data
	 * related
	 */
	public void clearCached() {
		label = remarks = help = null;
		decoractor = null;
		displayAttrs = null;
		edit = null;
	}

	public void clearAttached() {
		attached = null;
	}

	public void attach(String key, Object value) {
		if (attached == null)
			attached = new Hashtable<String, Object>();
		attached.put(key, value);
	}

	public Object getAttached(String key) {
		if (attached == null)
			return null;
		return attached.get(key);
	}

	public String getId() {
		return m_property.getId();
	}

	public String getName() {
		return m_property.getName();
	}

	public String getSortAsc() {
		return m_property.getSortAsc();
	}

	public String getSortDesc() {
		return m_property.getSortDesc();
	}

	public boolean isAjaxValidate() {
		return m_property.isAjaxValidate();
	}

	public String getMandatory() {
		return m_property.getMandatory();
	}

	public boolean isSupportSort() {
		return m_property.getSortAsc() != null
				|| m_property.getSortDesc() != null;
	}

	public String getLabel() {
		if (label == null)
			label = m_property.getLabel(m_adapter.m_strMarkup,
					m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return label;
	}

	public String getTitle() {
		if (title == null)
			title = m_property.getTitle(m_adapter.m_strMarkup,
					m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return title;
	}

	public String getRemarks() {
		if (remarks == null)
			remarks = m_property.getRemarks(m_adapter.m_strMarkup,
					m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return remarks;
	}

	public String getHelp() {
		if (help == null)
			help = m_property.getHelp(m_adapter.m_strMarkup, m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return help;
	}

	public Vector<Decoractor> getDecoractors() {
		if (decoractor == null)
			decoractor = m_property.getDecoractors(m_adapter.m_strMarkup,
					m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return decoractor;
	}

	public String getDisplayAttribute(String type) {
		if (displayAttrs == null)
			displayAttrs = new HashMap<String, String>();
		if (!displayAttrs.containsKey(type))
			displayAttrs.put(type, m_property.getDisplayAttribute(
					m_adapter.m_strMarkup, m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(), m_adb
							.getFlags(),
					m_filter == null ? null : m_filter.getFlags(), type));
		return displayAttrs.get(type);
	}

	public Edit getEdit() {
		if (edit == null)
			edit = m_property.getEdit(m_adapter.m_strMarkup, m_data.getFlags(),
					m_group == null ? null : m_group.getFlags(),
					m_adb.getFlags(),
					m_filter == null ? null : m_filter.getFlags());
		return edit;
	}

	public static String markupFormat(String str, boolean inputValue) {
		EnumMarkup markup = MarkupTag.getMarkup();
		if (markup == EnumMarkup.HTML)
			return StringUtils.toHTML(str, inputValue);
		else if (markup == EnumMarkup.WML)
			return StringUtils.toWML(str);
		return str;
	}

	public Object getValue() throws Exception {
		Object obj = m_property.get(m_adb);
		if (obj instanceof String)
			return markupFormat((String) obj, false);
		return obj;
	}

	public String getValueId() throws Exception {
		Object value = getValue();
		if (value == null)
			return null;

		if (value instanceof SelectOption)
			return ((SelectOption) value).getId();
		return value.toString();
	}

	/**
	 * 取出用于编辑的值。如果存在编辑错误的值，则返回错误值。<br>
	 * 如果不存在编辑错误，则由适用的Edit来应用格式
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getEditValue() throws Exception {
		Object obj = m_data.getErrorValue(m_property);
		if (obj != null)
			return obj;

		obj = m_property.get(m_adb);
		Edit edit = getEdit();
		if (edit != null) {
			obj = edit.format(obj);
			if (edit instanceof EditTextArea && obj instanceof String)
				return markupFormat((String) obj, true);
			if (edit instanceof EditCustom && ((EditCustom) edit).isTextArea())
				return markupFormat((String) obj, true);
		}

		if (obj instanceof String)
			return markupFormat((String) obj, true);

		return obj;
	}

	/**
	 * 取出用于显示的值。与getValue()的区别是:<br>
	 * 1) 如果值实现了SelectOption接口，则返回SelectOption的name以便于显示<br>
	 * 2) 如果在out中指定了decoractor，则应用decoractor
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getDisplay(boolean forTextArea) throws Exception {
		Object obj = null;

		try {
			if (m_property.hasDisplay())
				obj = m_property.getDisplay(m_adb);
			else
				obj = m_property.get(m_adb);
		} catch (HideException e) {
			if (MarkupTag.getMarkup() == EnumMarkup.HTML)
				return "&nbsp;";
			else
				return "";
		}

		Vector<Decoractor> decos = getDecoractors();
		if (decos != null && decos.size() > 0) {
			for (Decoractor deco : decos)
				obj = deco.deco(m_adb, obj, this);
			return obj;
		} else if (obj == null)
			return obj;
		else if (obj instanceof SelectOption)
			obj = ((SelectOption) obj).getName();

		if (m_property.hasDisplay())
			return obj;
		else
			return markupFormat(obj.toString(), forTextArea);
	}

	public Object getDisplay() throws Exception {
		return getDisplay(false);
	}

	public String getInlineEditValue(Hashtable<String, String> lists) {
		try {
			Object val = getEditValue();
			if (val == null)
				return "null";
			if (EditSelect.class.isAssignableFrom(getEdit().getClass())) {
				String listId = (String) m_property.getListId(m_adb);
				if (listId != null) {
					if (!lists.contains(listId))
						lists.put(listId, BasicSelectOption.toJson(m_property
								.getList(m_adb)));
					val = listId + ":" + val;
				}
			}
			return "'" + StringUtils.toJson(val.toString()) + "'";
		} catch (HideException e) {
			return "null";
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		}
	}

	public Vector<String> getErrors() {
		Vector<String> vec = m_data.getErrorMsgs(m_property);
		if (vec == null || vec.size() == 0)
			return null;
		return vec;
	}

	public Vector<Validator> getValidators() {
		return m_property
				.getValidators(m_adapter.m_strMarkup, m_data.getFlags(),
						m_group == null ? null : m_group.getFlags(),
						m_adb.getFlags(),
						m_filter == null ? null : m_filter.getFlags());
	}

	/**
	 * 获取显示属性：对齐方式
	 * 
	 * @return
	 */
	public String getAlign() {
		return getDisplayAttribute("align");
	}
}
