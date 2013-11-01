package com.aggrepoint.su.core.data;

import java.util.Enumeration;

import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.AdbAdapter;

/**
 * 系统公用的发布配置
 * 
 * @author YJM
 */
public class SysPublishConfig {
	/** 客户端访问静态发布的、模板中包含的资源的根URL */
	public String m_strTemplateResRootStaticUrl;
	/** 客户端访问静态发布的、内容中包含的资源的根URL */
	public String m_strContentResRootStaticUrl;
	/** 客户端访问静态发布的Frame资源的根URL */
	public String m_strFrameResRootStaticUrl;
	/** 客户端访问静态发布的CPage资源的根URL */
	public String m_strCPageResRootStaticUrl;
	/** 客户端访问静态发布的应用资源的根URL */
	public String m_strAppResRootStaticUrl;

	/** 对应的参数 */
	ADBList<SysPubCfg> m_adblCfgs;

	/** 已经加载的发布配置 */
	static SysPublishConfig m_sysPublishConfig = null;

	/**
	 * 构造函数，防止被直接构造
	 */
	SysPublishConfig() {
		m_strTemplateResRootStaticUrl = m_strContentResRootStaticUrl = m_strFrameResRootStaticUrl = m_strCPageResRootStaticUrl = m_strAppResRootStaticUrl = "";
	}

	/**
	 * 根据m_adblCfgs获取其他属性
	 */
	void constructFromCfg() {

		SysPubCfg cfg;
		for (Enumeration<SysPubCfg> enm = m_adblCfgs.m_vecObjects.elements(); enm
				.hasMoreElements();) {
			cfg = enm.nextElement();

			if (cfg.m_strCfgName.equals("TemplateResRootStaticUrl")) {
				m_strTemplateResRootStaticUrl = cfg.m_strCfgValue;
			} else if (cfg.m_strCfgName.equals("ContentResRootStaticUrl")) {
				m_strContentResRootStaticUrl = cfg.m_strCfgValue;
			} else if (cfg.m_strCfgName.equals("FrameResRootStaticUrl")) {
				m_strFrameResRootStaticUrl = cfg.m_strCfgValue;
			} else if (cfg.m_strCfgName.equals("CPageResRootStaticUrl")) {
				m_strCPageResRootStaticUrl = cfg.m_strCfgValue;
			} else if (cfg.m_strCfgName.equals("AppResRootStaticUrl")) {
				m_strAppResRootStaticUrl = cfg.m_strCfgValue;
			}
		}
	}

	/**
	 * 与数据库同步
	 */
	void sync(AdbAdapter adapter, boolean forceCheck) {
		try {
			if (!adapter.syncList(m_adblCfgs, forceCheck))
				constructFromCfg();
		} catch (Exception e) {
		}
	}

	/**
	 * 获取发布配置
	 */
	static public SysPublishConfig getConfig(AdbAdapter adapter,
			boolean forceCheck) {
		if (m_sysPublishConfig != null) { // 已经获取过
			m_sysPublishConfig.sync(adapter, forceCheck);
			return m_sysPublishConfig;
		}

		// 从数据库中加载发布配置
		try {
			SysPublishConfig config = new SysPublishConfig();
			SysPubCfg cfg = new SysPubCfg();
			config.m_adblCfgs = adapter.retrieveMultiDbl(cfg);
			if (config.m_adblCfgs.m_iTotalCount == 0)
				return null;
			config.constructFromCfg();

			m_sysPublishConfig = config;
			return m_sysPublishConfig;
		} catch (Exception e) {
		}

		return null;
	}
}
