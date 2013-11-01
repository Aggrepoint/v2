package com.aggrepoint.adk.ui;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.ui.data.ValidatorDef;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.cfgmgr.ConfigManagerBase;
import com.icebean.core.cfgmgr.ConfigManagerException;
import com.icebean.core.cfgmgr.ConfigManagerFactory;
import com.icebean.core.common.ClassStack;
import com.icebean.core.common.Log4jIniter;

/**
 * 负责加载Validator配置信息
 * 
 * @author YJM
 */
public class UIValidatorConfigManager extends ConfigManagerBase {
	/** Validator的定义 */
	Hashtable<String, ValidatorDef> m_htValidators;

	public UIValidatorConfigManager() {
		m_htValidators = new Hashtable<String, ValidatorDef>();
	}

	/**
	 * @see com.icebean.core.cfgmgr.ConfigManagerBase#init(InputStream)
	 */
	public void init(InputStream is) throws Exception {
		/** 日志Category */
		org.apache.log4j.Category log = Log4jIniter.getCategory(ClassStack
				.getExternalCallerClass(), ClassStack.getCurrClass());

		log.debug("Initializing ValidatorConfigManager...");

		m_htValidators.clear();

		try {
			log.debug("Construct XmlAdapter on config file.");

			XmlAdapter adapter = new XmlAdapter(is);

			ValidatorDef validator = new ValidatorDef();
			log.debug("Load validator config.");
			Vector<ValidatorDef> vecValidators = adapter
					.retrieveMulti(validator);

			for (Enumeration<ValidatorDef> enm = vecValidators.elements(); enm
					.hasMoreElements();) {
				validator = enm.nextElement();
				log.debug("Initializing validator " + validator.m_strId
						+ " with class " + validator.m_strClass + "...");

				validator.m_validator = (IUIValidator) Class.forName(
						validator.m_strClass).newInstance();

				log.debug("Done.");

				m_htValidators.put(validator.m_strId, validator);
			}
		} catch (Exception e) {
			log.error("ValidatorConfigManager initialization failed.", e);
			throw e;
		}

		log.debug("ValidatorConfigManager initialized successfully.");
	}

	public ValidatorDef getValidator(String id) {
		return m_htValidators.get(id);
	}

	/**
	 * 获取属性资源管理器实例
	 */
	public static UIValidatorConfigManager getManager(Class<?> c)
			throws ConfigManagerException {
		return (UIValidatorConfigManager) ConfigManagerFactory.getManager(c,
				"/CFG-INF/uivalidator.xml", UIValidatorConfigManager.class,
				true, true);
	}
}
