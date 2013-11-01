package com.aggrepoint.adk.ui;

import java.util.Hashtable;

import com.aggrepoint.adk.ui.data.UIConfig;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.ClassStack;
import com.icebean.core.common.Log4jIniter;
import com.icebean.core.res.ResourceLoadException;
import com.icebean.core.res.ResourceLoaderDefaultImpl;

/**
 * 缺省的配置加载器，从类所在的路径加载配置
 * 
 * @author: Yang Jiang Ming
 */
public class UIConfigLoaderDefaultImpl extends ResourceLoaderDefaultImpl {
	/** 信息Boundle */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	/** 已加载的配置 */
	Hashtable<Class<?>, Hashtable<String, UIConfig>> m_htConfigs;

	/**
	 * 构造函数
	 */
	public UIConfigLoaderDefaultImpl() {
		m_htConfigs = new Hashtable<Class<?>, Hashtable<String, UIConfig>>();
	}

	/**
	 * 加载配置
	 */
	public Object loadConfigObject(Class<?> c, String cfgName)
			throws ResourceLoadException {
		/** 日志Category */
		org.apache.log4j.Category log = Log4jIniter.getCategory(ClassStack
				.getExternalCallerClass(), ClassStack.getCurrClass());
		String strClassName = c.getName();
		Hashtable<String, UIConfig> htTemp = null;

		// 尝试从已有的UIConfig中寻找
		htTemp = m_htConfigs.get(c);
		if (htTemp != null) {
			if (htTemp.containsKey(cfgName))
				return htTemp.get(cfgName);
		} else {
			htTemp = new Hashtable<String, UIConfig>();
			m_htConfigs.put(c, htTemp);
		}

		try {
			XmlAdapter adapter = new XmlAdapter(loadXMLDocumentNotStatic(c,
					cfgName, null));
			UIConfig config = new UIConfig(c);
			adapter.retrieve(config);
			htTemp.put(cfgName, config);
			return config;
		} catch (ResourceLoadException e) {
			throw e;
		} catch (Exception e) {
			log.error(m_msg.constructMessage("loadConfigError", strClassName),
					e);
			throw new ResourceLoadException(strClassName);
		}
	}
}