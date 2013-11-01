package com.aggrepoint.adk.ui;

import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.taglib.html.ContTmpl;
import com.aggrepoint.adk.ui.data.Edit;
import com.aggrepoint.adk.ui.data.Group;
import com.aggrepoint.adk.ui.data.Property;
import com.aggrepoint.adk.ui.data.UIConfig;
import com.aggrepoint.adk.ui.data.Validator;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;
import com.icebean.core.beanutil.BeanPropertyException;
import com.icebean.core.cfgmgr.ConfigManagerException;
import com.icebean.core.common.TypeCast;

public class UIAdapter {
	IModuleRequest m_request;
	public String m_strMarkup;

	public UIAdapter(IModuleRequest req) {
		m_request = req;
		m_strMarkup = req.getPsnEngine().getMarkup().getName();
	}

	/**
	 * 从ADB中获取附加数据，若不存在则创建
	 */
	public UIAttachedData getData(ADB adb) throws ConfigManagerException,
			UIConfigLoadException, BeanPropertyException {
		UIAttachedData data = TypeCast.cast(adb.getAttachedData(getClass()));
		if (data != null)
			return data;

		UIConfig config = UIConfigManager.getManager(adb.getClass())
				.getUIConfig(adb.getClass());

		data = new UIAttachedData(config);
		adb.attachData(getClass(), data);

		return data;
	}

	/**
	 * 设置标记
	 * 
	 * @param adb
	 * @param flag
	 * @throws AdbException
	 */
	public void setFlag(ADB adb, String flag) throws AdbException,
			ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		UIAttachedData data = getData(adb);
		data.getFlags().add(flag);
	}

	/**
	 * 清除标记
	 * 
	 * @param adb
	 * @param flag
	 * @throws AdbException
	 */
	public <T extends ADB> void clearFlag(T adb, String flag)
			throws AdbException, ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		UIAttachedData data = getData(adb);
		if (flag == null)
			data.clearFlags();
		else
			data.getFlags().remove(flag);
	}

	/**
	 * 将数据加载到对象中
	 * 
	 * @param adb
	 * @return true 表示验证没有错误，false表示验证遇到错误
	 */
	public boolean populate(ADB adb, String group) throws Exception {
		return populate(adb, group, null, null) == null;
	}

	public Vector<String> populate(ADB adb, String group, String prop,
			String value) throws Exception {
		boolean bNoError = true;
		UIAttachedData data = getData(adb);
		UIValidatorConfigManager ucm = UIValidatorConfigManager.getManager(adb
				.getClass());

		// 只有当指定了一个属性时才返回错误Vector
		Vector<String> vecErrors = null;
		if (prop != null)
			vecErrors = new Vector<String>();

		Group gp = data.m_config.getGroup(group);
		if (gp == null)
			throw new Exception("UI组\"" + group + "\"找不到");

		Hashtable<Property, Edit> htValidate = new Hashtable<Property, Edit>();

		// 装载值
		for (Property property : data.m_config.getProperties(adb, m_strMarkup,
				data.getFlags(), group)) {
			if (prop != null && !property.getId().equals(prop))
				continue;

			if (prop != null) { // populate a single property, clear errors
								// before populate
				data.clearErrorMsgs(property);
				data.setErrorValue(property, null);
			}

			Edit edit = property.getEdit(m_strMarkup, data.getFlags(),
					gp == null ? null : gp.getFlags(), adb.getFlags(), null);

			if (edit == null)
				continue;

			// 装载值
			try {
				if (value != null)
					edit.populate(adb, value);
				else
					edit.populate(adb, m_request);
			} catch (Exception e) {
				String error = edit
						.getError(m_strMarkup, data.getFlags(),
								gp == null ? null : gp.getFlags(),
								adb.getFlags(), null);
				if (error != null) {
					data.getErrorMsgs(property).add(error);
					if (vecErrors != null)
						vecErrors.add(error);
				} else {
					data.getErrorMsgs(property).add(e.getMessage());
					if (vecErrors != null)
						vecErrors.add(e.getMessage());
				}

				data.setErrorValue(property,
						value != null ? value : edit.getValue(adb, m_request));
				bNoError = false;
				continue;
			}

			htValidate.put(property, edit);

			if (prop != null)
				break;
		}

		// 检验合法性
		for (Property property : data.m_config.getProperties(adb, m_strMarkup,
				data.getFlags(), group)) {
			Edit edit = htValidate.get(property);

			if (edit == null)
				continue;

			boolean bSkipValidation = false;

			// 用检验器检验数据合法性
			for (Validator v : property.getValidators(m_strMarkup,
					data.getFlags(), gp == null ? null : gp.getFlags(),
					adb.getFlags(), null)) {
				ValidateResult vr = v
						.validateWithMethod(m_request, adb, m_strMarkup,
								data.getFlags(),
								gp == null ? null : gp.getFlags(),
								adb.getFlags(), null);

				if (vr == null) { // no method defined, validate with id
					if (v.m_strId != null
							&& !v.m_strId.equals("")
							&& !ucm.getValidator(v.m_strId).m_validator
									.validate(adb, property, v.getArgs(
											m_strMarkup, data.getFlags(),
											gp == null ? null : gp.getFlags(),
											adb.getFlags(), null))) { // Validate
						// failed
						String error = v.getMsg(m_strMarkup, data.getFlags(),
								gp == null ? null : gp.getFlags(),
								adb.getFlags(), null);
						if (error != null) {
							data.getErrorMsgs(property).add(error);
							if (vecErrors != null)
								vecErrors.add(error);
						}

						data.setErrorValue(property, value != null ? value
								: edit.getValue(adb, m_request));
						bNoError = false;

						if (v.m_strSkip != null)
							if (v.m_strSkip.equalsIgnoreCase("all")) {
								// Skip all following validation
								bSkipValidation = true;
								break;
							} else if (v.m_strSkip.equalsIgnoreCase("next")) {
								// Skip following validation within this
								// property
								break;
							}
					}
				} else {
					if (vr.getType() == ValidateResultType.PASS_CONTINUE)
						continue;
					if (vr.getType() == ValidateResultType.PASS_SKIP_PROPERTY)
						break;
					if (vr.getType() == ValidateResultType.PASS_SKIP_ALL) {
						bSkipValidation = true;
						break;
					}

					String error = vr.getMsg();
					if (error == null)
						error = v.getMsg(m_strMarkup, data.getFlags(),
								gp == null ? null : gp.getFlags(),
								adb.getFlags(), null);
					if (error != null) {
						data.getErrorMsgs(property).add(error);
						if (vecErrors != null)
							vecErrors.add(error);
					}

					data.setErrorValue(
							property,
							value != null ? value : edit.getValue(adb,
									m_request));
					bNoError = false;

					if (vr.getType() == ValidateResultType.FAILED_SKIP_PROPERTY)
						break;
					if (vr.getType() == ValidateResultType.FAILED_SKIP_ALL) {
						bSkipValidation = true;
						break;
					}
				}
			}

			if (bSkipValidation)
				break;
		}

		if (vecErrors != null)
			return vecErrors;

		return bNoError ? null : new Vector<String>();
	}

	/**
	 * 获取属性定义列表
	 * 
	 * @param adbFilter
	 *            用于过滤属性的ADB
	 * @param adbData
	 *            用于取值的ADB
	 * @param group
	 * @return
	 * @throws ConfigManagerException
	 * @throws UIConfigLoadException
	 * @throws BeanPropertyException
	 */
	public Vector<PropertyInstance> getProperties(ADB adbFilter, ADB adbData,
			String group) throws ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		UIAttachedData data = getData(adbFilter);
		Vector<PropertyInstance> vecInsts = new Vector<PropertyInstance>();
		Group gp = data.m_config.getGroup(group);
		for (Property property : data.m_config.getProperties(adbFilter,
				m_strMarkup, data.getFlags(), group)) {
			vecInsts.add(new PropertyInstance(gp, property, adbFilter, adbData,
					this));
		}

		return vecInsts;
	}

	/**
	 * 获取属性定义列表
	 * 
	 * @param adb
	 * @param group
	 * @return
	 */
	public Vector<PropertyInstance> getProperties(ADB adb, String group)
			throws ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		return getProperties(adb, adb, group);
	}

	public PropertyInstance getProperty(ADB adbFilter, ADB adbData,
			String group, String name) throws ConfigManagerException,
			UIConfigLoadException, BeanPropertyException {
		UIAttachedData data = getData(adbFilter);
		Group gp = data.m_config.getGroup(group);
		for (Property property : data.m_config.getProperties(adbFilter,
				m_strMarkup, data.getFlags(), group)) {
			if (property.getName().equals(name))
				return new PropertyInstance(gp, property, adbFilter, adbData,
						this);
		}
		return null;
	}

	/**
	 * 获取加载属性时遇到的错误信息
	 * 
	 * @param adb
	 * @param property
	 * @return null表示没有错误，否则返回错误信息列表
	 */
	public Vector<String> getErrorMsg(ADB adb, PropertyInstance property)
			throws ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		return getData(adb).getErrorMsgs(property.m_property);
	}

	/**
	 * 清除错误信息和错误值
	 * 
	 * @param adb
	 * @throws ConfigManagerException
	 * @throws UIConfigLoadException
	 * @throws BeanPropertyException
	 */
	public UIAdapter clearErrors(ADB adb) throws ConfigManagerException,
			UIConfigLoadException, BeanPropertyException {
		getData(adb).clearErrors();
		return this;
	}

	public ContTmpl getTemplate(ADB adbFilter, String template)
			throws ConfigManagerException, UIConfigLoadException,
			BeanPropertyException {
		return getData(adbFilter).m_config.getTemplate(template);
	}
}
