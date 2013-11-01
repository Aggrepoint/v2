package com.aggrepoint.adk.ui.data;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.ui.PropertyInstance;
import com.aggrepoint.adk.ui.SelectOption;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.common.TypeCast;

public class Decoractor extends Matchable implements IAdkConst, UIConst {
	public String m_strType;
	public String m_strAttribute;

	public Object deco(ADB adb, Object val, PropertyInstance pi)
			throws Exception {
		if (val == null)
			return null;

		if (m_strType != null) {
			if (m_strType.equalsIgnoreCase("format"))
				return decoFormat(val);
			else if (m_strType.equalsIgnoreCase("selectfactory"))
				return decoSelectOptionFactory(val);
			else if (m_strType.equalsIgnoreCase("selectlist"))
				return decoSelectOptionList(adb, val);
			else if (m_strType.equalsIgnoreCase("tohtml"))
				return StringUtils.toHTML(val.toString());
			else if (m_strType.equalsIgnoreCase("custom"))
				return decoCustom(val, pi);
		}

		return val;
	}

	private DbAdapter getDbAdapter() throws Exception {
		return new DbAdapter(((HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST)).getReuseDbConn());
	}

	private Method m_selectOptionFactoryMethod;
	private boolean m_bSelectOptionFactoryMethodNeedsDB;

	/**
	 * 
	 * @param val
	 * @return
	 * @throws Exception
	 */
	public String decoSelectOptionFactory(Object val) throws Exception {
		if (val == null)
			return null;

		if (m_selectOptionFactoryMethod == null) {
			Class<?> c = Class.forName(m_strAttribute.trim());
			try {
				m_selectOptionFactoryMethod = c
						.getMethod(SELECT_OPTION_FACTORY_METHOD);
				m_bSelectOptionFactoryMethodNeedsDB = false;
			} catch (NoSuchMethodException e) {
				m_selectOptionFactoryMethod = c.getMethod(
						SELECT_OPTION_FACTORY_METHOD, AdbAdapter.class);
				m_bSelectOptionFactoryMethodNeedsDB = true;
			}
		}

		Collection<? extends SelectOption> options = null;
		if (m_bSelectOptionFactoryMethodNeedsDB)
			options = TypeCast.cast(m_selectOptionFactoryMethod.invoke(null,
					getDbAdapter()));
		else
			options = TypeCast.cast(m_selectOptionFactoryMethod.invoke(null));

		val = val.toString();
		for (SelectOption option : options)
			if (option.getId().equals(val))
				return option.getName();

		return "";
	}

	private Method m_selectOptionListMethod;
	private boolean m_bSelectOptionListMethodNeedsDB;

	/**
	 * 调用ADB方法获取SelectOption列表，返回ID与值对应的SelectOption名称
	 * 
	 * @param adb
	 * @param val
	 * @return
	 * @throws Exception
	 */
	public String decoSelectOptionList(ADB adb, Object val) throws Exception {
		if (val == null)
			return null;

		if (m_selectOptionListMethod == null) {
			try {
				m_selectOptionListMethod = adb.getClass().getMethod(
						m_strAttribute.trim());
				m_bSelectOptionListMethodNeedsDB = false;
			} catch (NoSuchMethodException e) {
				m_selectOptionListMethod = adb.getClass().getMethod(
						m_strAttribute.trim(), AdbAdapter.class);
				m_bSelectOptionListMethodNeedsDB = true;
			}
		}

		Collection<? extends SelectOption> options = null;
		if (m_bSelectOptionListMethodNeedsDB)
			options = TypeCast.cast(m_selectOptionListMethod.invoke(adb,
					getDbAdapter()));
		else
			options = TypeCast.cast(m_selectOptionListMethod.invoke(adb));

		val = val.toString();
		for (SelectOption option : options)
			if (option.getId().equals(val))
				return option.getName();

		return "";
	}

	/**
	 * 应用格式
	 * 
	 * @param obj
	 * @return
	 */
	public String decoFormat(Object obj) {
		if (obj == null)
			return null;

		if (m_strAttribute != null && !m_strAttribute.equals("")) {
			if (Date.class.isAssignableFrom(obj.getClass()))
				return Edit.getSimpleDateFormat(m_strAttribute).format(
						(Date) obj);

			if (obj instanceof Short || obj instanceof Integer
					|| obj instanceof Long)
				return Edit.getNumberFormat(m_strAttribute).format(
						((Number) obj).longValue());

			if (Number.class.isAssignableFrom(obj.getClass()))
				return Edit.getNumberFormat(m_strAttribute).format(
						((Number) obj).doubleValue());
		}

		return obj.toString();
	}

	private Method m_customDecoracorMethod;

	public Object decoCustom(Object val, PropertyInstance pi) throws Exception {
		if (m_customDecoracorMethod == null) {
			Class<?> c = Class.forName(m_strAttribute.trim());
			m_customDecoracorMethod = c.getMethod(
					CUSTOM_DECORACTOR_DECO_METHOD, PropertyInstance.class,
					Object.class);
		}
		try {
		return m_customDecoracorMethod.invoke(null, pi, val).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		}
	}
}
