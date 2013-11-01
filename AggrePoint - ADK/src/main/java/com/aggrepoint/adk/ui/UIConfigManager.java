package com.aggrepoint.adk.ui;

import com.aggrepoint.adk.ui.data.UIConfig;
import com.icebean.core.beanutil.BeanPropertyException;
import com.icebean.core.cfgmgr.ConfigManagerException;
import com.icebean.core.cfgmgr.ConfigManagerFactory;
import com.icebean.core.res.ResourceManager;

/**
 * @author YJM
 */
public class UIConfigManager extends ResourceManager {
	/** 信息Boundle */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	/**
	 * 获管理器实例
	 */
	public static UIConfigManager getManager(Class<?> c)
			throws ConfigManagerException {
		UIConfigManager manager = (UIConfigManager) ConfigManagerFactory
				.getManager(c, "/CFG-INF/uicfg_load.xml",
						UIConfigManager.class, true, true);

		return manager;
	}

	/**
	 * 获取UIConfig
	 */
	public UIConfig getUIConfig(Class<?> c, String cfgName)
			throws UIConfigLoadException, BeanPropertyException {
		Object obj = null;

		// 加载UIConfig
		try {
			obj = loadResourceObject(c, cfgName);
		} catch (Exception e) {
			throw new UIConfigLoadException(e, m_msg.constructMessage(
					"loadError", c.getName(), cfgName, e.getMessage()));
		}

		if (!(obj instanceof UIConfig)) {
			// ConfigManager加载返回的不是ValidateConfig对象
			throw new UIConfigLoadException(m_msg.constructMessage(
					"notValidateConfig", c.getName(), cfgName));
		}

		return (UIConfig) obj;
	}

	/**
	 * 获取UIConfig
	 */
	public UIConfig getUIConfig(Class<?> c) throws UIConfigLoadException,
			BeanPropertyException {
		return getUIConfig(c, c.getName().substring(
				c.getName().lastIndexOf(".") + 1));
	}
}
