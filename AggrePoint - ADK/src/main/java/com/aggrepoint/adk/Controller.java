package com.aggrepoint.adk;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Priority;

import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.BasicParam;
import com.aggrepoint.adk.data.DLCDef;
import com.aggrepoint.adk.data.LoggerDef;
import com.aggrepoint.adk.data.PsnEngineDef;
import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.data.RetCodeLevelDef;
import com.aggrepoint.adk.data.RetCodeLevelLoggerDef;
import com.aggrepoint.adk.data.URIDef;
import com.aggrepoint.adk.data.UserEngineDef;
import com.aggrepoint.adk.data.WinletActionDef;
import com.aggrepoint.adk.data.WinletDef;
import com.aggrepoint.adk.data.WinletStateDef;
import com.aggrepoint.adk.form.FormImpl;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.res.ResourceManager;
import com.icebean.core.res.ResourceNotFoundException;

/**
 * @author YJM
 */
public abstract class Controller implements IAdkConst, IWinletConst,
		IServerContext {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	/** 信息 */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	/** 配置参数 */
	Hashtable<String, String> m_htParams = new Hashtable<String, String>();

	/** 所有URI的定义 */
	Vector<URIDef> m_vecUris;

	/** 请求路径与URIDef映照表，用于加快检索速度 */
	Hashtable<String, BaseModuleDef> m_htPath = new Hashtable<String, BaseModuleDef>();

	/** 用户身份引擎定义 */
	Hashtable<String, UserEngineDef> m_htUserEngines;

	/** 缺省的用户身份引擎定义 */
	UserEngineDef m_defaultUserEngine;

	/** 个性化引擎定义 */
	Hashtable<String, PsnEngineDef> m_htPsnEngines;

	/** 缺省的个性化引擎定义 */
	PsnEngineDef m_defaultPsnEngine;

	/** Logger定义 */
	Hashtable<String, LoggerDef> m_htLoggers;

	/** 缺省的Logger定义 */
	protected LoggerDef m_defaultLogger;

	/** 响应码级别定义 */
	Hashtable<String, RetCodeLevelDef> m_htRetCodeLevels;

	/** 缺省的响应码级别定义 */
	RetCodeLevelDef m_defaultRetCodeLevel;

	/** ContextRoot，以/开头，不以/结尾 */
	public String m_strResourceRootPath;

	/** 应用根目录，以/结尾 */
	public String m_strRootDir;

	// 以下部分属性从icebean_web.xml的/webapp/param标记中获取
	/** 服务器名称 */
	protected String m_strServerName;

	/** 临时目录 */
	String m_strTempDir;

	/** 是否在开发环境 */
	protected boolean m_bDev;

	/** 获取服务器名称 */
	public String getServerName() {
		return m_strServerName;
	}

	/** 获取临时路径，以/结尾 */
	public String getTempDir() {
		return m_strTempDir;
	}

	/** 获取应用根目录，以/结尾 */
	public String getRootDir() {
		return m_strRootDir;
	}

	/** 获取ContextRoot，以/开头，不以/结尾 */
	public String getResourceRootPath() {
		return m_strResourceRootPath;
	}

	/**
	 * 处理需自动加载的URI定义
	 */
	protected void autoLoad(URIDef uri) {
		try {
			if (uri.m_bAutoLoad)
				uri.getModuleInstance(false, this, null);
		} catch (Exception e) {
		}

		for (Enumeration<URIDef> enm = uri.m_vecSubUris.elements(); enm
				.hasMoreElements();) {
			URIDef subUri = enm.nextElement();
			autoLoad(subUri);
		}
	}

	/** 获取全局参数配置 */
	public String getParam(String name) {
		return m_htParams.get(name);
	}

	public void log(Priority priority, IModuleRequest req, String message,
			Throwable exp) {
		if (m_defaultLogger == null) {
			if (priority == Priority.ERROR) {
				System.err.println(message);
			} else {
				System.out.println(message);
			}
			if (exp != null)
				exp.printStackTrace();
			return;
		}

		try {
			m_defaultLogger.getLoggerInstance(false, this).contextLog(priority,
					req, message, exp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据配置文件进行初始化
	 */
	protected boolean init(AdbAdapter adapter, String rootDir,
			String defaultTempDir) throws Exception {
		m_strRootDir = rootDir;

		try {
			// {获取配置参数
			m_strServerName = "SERVER";
			m_strTempDir = null;
			m_bDev = true;

			BasicParam param = new BasicParam();
			Vector<BasicParam> vecParams = adapter.retrieveMulti(param);

			for (Enumeration<BasicParam> enm = vecParams.elements(); enm
					.hasMoreElements();) {
				param = enm.nextElement();

				// If parameter name starts with "system", then it's a system
				// property
				if (param.m_strName.startsWith("system"))
					System.setProperty(param.m_strName.substring(7),
							param.m_strValue);
				else if (param.m_strName.equalsIgnoreCase("servername"))
					m_strServerName = param.m_strValue;
				else if (param.m_strName.equalsIgnoreCase("tempdir"))
					m_strTempDir = param.m_strValue;
				else if (param.m_strName.equalsIgnoreCase("release"))
					m_bDev = false;
				else
					m_htParams.put(param.m_strName, param.m_strValue);
			}
			// }

			m_strServerName = InetAddress.getLocalHost().getHostName()
					+ "_" + m_strServerName;

			if (m_strTempDir == null)
				m_strTempDir = defaultTempDir;

			File tempDir = new File(m_strTempDir);
			tempDir.mkdirs();

			// { 加载需动态加载的类的定义
			DLCDef dlcDef = new DLCDef();
			DynamicClassLoader.init(adapter.retrieveMulti(dlcDef));
			// }

			// {加载用户身份引擎定义
			m_htUserEngines = new Hashtable<String, UserEngineDef>();
			m_defaultUserEngine = null;

			UserEngineDef userEngine = new UserEngineDef();
			Vector<UserEngineDef> vecUserEngines = adapter
					.retrieveMulti(userEngine);

			if (vecUserEngines.size() == 0) { // 未定义用户身份引擎，使用缺省引擎
				m_htUserEngines.put("", userEngine);
				m_defaultUserEngine = userEngine;
			} else {
				for (Enumeration<UserEngineDef> enm = vecUserEngines.elements(); enm
						.hasMoreElements();) {
					userEngine = enm.nextElement();
					m_htUserEngines.put(userEngine.m_strID, userEngine);
					if (userEngine.m_bDefault && m_defaultUserEngine == null)
						m_defaultUserEngine = userEngine;
				}

				if (m_defaultUserEngine == null)
					m_defaultUserEngine = vecUserEngines.elementAt(0);
			}
			// }

			// {加载个性化引擎定义
			m_htPsnEngines = new Hashtable<String, PsnEngineDef>();
			m_defaultPsnEngine = null;

			PsnEngineDef psnEngine = new PsnEngineDef();
			Vector<PsnEngineDef> vecPsnEngines = adapter
					.retrieveMulti(psnEngine);

			if (vecPsnEngines.size() == 0) { // 未定义个性化引擎，使用缺省引擎
				m_htPsnEngines.put("", psnEngine);
				m_defaultPsnEngine = psnEngine;
			} else {
				for (Enumeration<PsnEngineDef> enm = vecPsnEngines.elements(); enm
						.hasMoreElements();) {
					psnEngine = enm.nextElement();
					m_htPsnEngines.put(psnEngine.m_strID, psnEngine);
					if (psnEngine.m_bDefault && m_defaultPsnEngine == null)
						m_defaultPsnEngine = psnEngine;
				}

				if (m_defaultPsnEngine == null)
					m_defaultPsnEngine = vecPsnEngines.elementAt(0);
			}
			// }

			// {加载Logger定义
			m_htLoggers = new Hashtable<String, LoggerDef>();
			m_defaultLogger = null;

			LoggerDef logger = new LoggerDef();
			Vector<LoggerDef> vecLoggers = adapter.retrieveMulti(logger);

			if (vecLoggers.size() != 0) {
				for (Enumeration<LoggerDef> enm = vecLoggers.elements(); enm
						.hasMoreElements();) {
					logger = enm.nextElement();
					m_htLoggers.put(logger.m_strID, logger);
					if (logger.m_bDefault && m_defaultLogger == null)
						m_defaultLogger = logger;
				}

				if (m_defaultLogger == null)
					m_defaultLogger = vecLoggers.elementAt(0);
			}
			// }

			// {加载响应码级别定义
			m_htRetCodeLevels = new Hashtable<String, RetCodeLevelDef>();
			m_defaultRetCodeLevel = null;

			RetCodeLevelDef retCodeLevel = new RetCodeLevelDef();
			Vector<RetCodeLevelDef> vecRetCodeLevelDef = adapter
					.retrieveMulti(retCodeLevel);

			if (vecRetCodeLevelDef.size() == 0) {
				m_htRetCodeLevels.put("", retCodeLevel);
				m_defaultRetCodeLevel = retCodeLevel;
			} else {
				for (Enumeration<RetCodeLevelDef> enm = vecRetCodeLevelDef
						.elements(); enm.hasMoreElements();) {
					retCodeLevel = enm.nextElement();
					m_htRetCodeLevels.put(retCodeLevel.m_strID, retCodeLevel);
					if (retCodeLevel.m_bDefault
							&& m_defaultRetCodeLevel == null)
						m_defaultRetCodeLevel = retCodeLevel;
				}

				if (m_defaultRetCodeLevel == null)
					m_defaultRetCodeLevel = vecRetCodeLevelDef.elementAt(0);
			}
			// }

			// {加载全局响应码定义
			RetCode retCode = new RetCode();
			Vector<RetCode> vecRetCodes = adapter.retrieveMulti(retCode,
					"loadRoot", null);
			// }

			// {加载URI定义
			URIDef uriDef = new URIDef();
			m_vecUris = adapter.retrieveMulti(uriDef, "loadAll", null);
			// }

			// {处理在apapp.xml之外定义的窗口
			for (Enumeration<URIDef> enum0 = m_vecUris.elements(); enum0
					.hasMoreElements();) {
				uriDef = enum0.nextElement();

				// 将全局响应码复制到各个顶级URI上
				uriDef.m_vecRetCodes.addAll(vecRetCodes);

				for (Enumeration<WinletDef> enm = uriDef.m_vecWins.elements(); enm
						.hasMoreElements();) {
					WinletDef winDef = enm.nextElement();
					InputStream wis = null;

					try {
						wis = ResourceManager.getResourceManager(getClass(),
								"/CFG-INF/amcfg_load.xml").loadResource(
								winDef.getModuleClass(false));

						AdbAdapter wadapter = new XmlAdapter(
								javax.xml.parsers.DocumentBuilderFactory
										.newInstance()
										.newDocumentBuilder()
										.parse(new org.xml.sax.InputSource(wis)));

						WinletDef newDef = new WinletDef();
						newDef.m_vecEventHandlers = winDef.m_vecEventHandlers;
						wadapter.retrieve(newDef, "loadSeperate");
						winDef.copy(newDef);
					} catch (ResourceNotFoundException e) { // 可以不在外部定义
					} catch (Exception e) {
						m_log.error(m_msg.constructMessage(
								"loadWinletExtDefError",
								winDef.m_strModuleClass), e);
					} finally {
						if (wis != null)
							try {
								wis.close();
							} catch (Exception e) {
							}
					}

					winDef.cascadeUserAndPsnEngine();
				}
			}
			// }

			for (Enumeration<URIDef> enum0 = m_vecUris.elements(); enum0
					.hasMoreElements();) {
				uriDef = enum0.nextElement();
				autoLoad(uriDef);
			}
		} catch (Exception e) {
			m_vecUris = null;
			throw e;
		}

		return m_vecUris != null;
	}

	/**
	 * 根据当前请求和URI定义获取用户身份
	 */
	IUserEngine getUserEngine(BaseModuleDef uri) throws Exception {
		UserEngineDef def;

		if (uri.m_strUserEngineID == null || uri.m_strUserEngineID.equals("")) // URI没有指定的用户引擎，使用缺省代替
			def = m_defaultUserEngine;
		else { // 指定了个性化引擎
			def = m_htUserEngines.get(uri.m_strUserEngineID);
			if (def == null) // 找不到指定的引擎，使用缺省的代替
				def = m_defaultUserEngine;
		}

		return def.getEngineInstance();
	}

	/**
	 * 获取个性化引擎
	 */
	IPsnEngine newPsnEngine(IModuleRequest req, BaseModuleDef uri,
			IUserProfile userProfile) throws Exception {
		PsnEngineDef def;

		if (uri.m_strPsnEngineID == null || uri.m_strPsnEngineID.equals("")) // URI没有指定的个性化引擎，使用缺省代替
			def = m_defaultPsnEngine;
		else { // 指定了个性化引擎
			def = m_htPsnEngines.get(uri.m_strPsnEngineID);
			if (def == null) // 找不到指定的引擎，使用缺省的代替
				def = m_defaultPsnEngine;
		}

		IPsnEngine engine = def.newEngineInstance();

		// 初始化
		engine.init(req, userProfile);
		return engine;
	}

	/**
	 * 根据请求路径查找相应的Module 若找不到则直接抛出ControlException
	 */
	public BaseModuleDef findModule(IModuleRequest req, String path)
			throws ControlException {
		String controlPath;
		String requestPath;
		if (path == null || path.equals("")) {
			controlPath = req.getControlPath();
			requestPath = req.getRequestPath();
		} else {
			int idx = path.indexOf("/", 1);
			if (idx == -1) {
				controlPath = path;
				requestPath = "";
			} else {
				controlPath = path.substring(0, idx);
				requestPath = path.substring(idx);
			}
		}

		// {根据请求路径找到对应的URI定义
		BaseModuleDef moduleDef = m_htPath.get(controlPath + requestPath);

		if (moduleDef == null) {
			URIDef uriDef = null;
			for (Enumeration<URIDef> enm = m_vecUris.elements(); enm
					.hasMoreElements();) {
				uriDef = enm.nextElement();
				if (uriDef.m_strFullPath.equals(controlPath))
					break;
				uriDef = null;
			}

			if (uriDef == null) { // 没有找到相应的URI
				throw new ControlException(ERROR_NOT_FOUND,
						m_msg.constructMessage("uriNotDefined", controlPath
								+ requestPath), null);
			}

			// 先查找URI
			moduleDef = uriDef.findUri(requestPath);

			// 再查找WinletDef
			if (moduleDef == null)
				moduleDef = uriDef.findWin(requestPath);

			if (moduleDef == null) { // 没有找到路径对应的定义
				throw new ControlException(ERROR_NOT_FOUND,
						m_msg.constructMessage("uriNotDefined", controlPath
								+ requestPath), null);
			}

			m_htPath.put(controlPath + requestPath, moduleDef);
		}

		if (path != null && !path.equals(""))
			req.setControlPath(controlPath);
		// }

		return moduleDef;
	}

	/**
	 * 根据响应码定义处理展现
	 */
	public abstract void present(IModuleRequest req, IModuleResponse res,
			RetCode code) throws Exception;

	/**
	 * 执行Module
	 */
	public BaseModuleDef executeModule(IModuleRequest req, IModuleResponse res,
			BaseModuleDef moduleDef) throws ControlException {
		BaseModuleDef moduleDefBak = moduleDef;

		if (moduleDef instanceof URIDef
				&& (moduleDef.m_strModuleClass == null || moduleDef.m_strModuleClass
						.equals("")))
			throw new ControlException(ERROR_NOT_FOUND);

		// {获取请求用户身份
		IUserEngine userEngine = null;
		IUserProfile userProfile = null;

		try {
			userEngine = getUserEngine(moduleDef);
			userProfile = userEngine.getUserProfile(req, res);
		} catch (Exception e) {
			throw new ControlException(ERROR_PRE_EXECUTE,
					m_msg.constructMessage("userProfileError",
							req.getRequestPath()), e);
		}
		// }

		// {构造个性化引擎
		IPsnEngine psnEngine = null;

		try {
			psnEngine = newPsnEngine(req, moduleDef, userProfile);
		} catch (Exception e) {
			throw new ControlException(ERROR_PRE_EXECUTE,
					m_msg.constructMessage("psnEngineError",
							req.getRequestPath()), e);
		}
		// }

		req.bind(null, moduleDef, userProfile, psnEngine, res);

		// {获取ActionModule
		IModule module = null;
		try {
			module = moduleDef.getModuleInstance(false, this, req);
		} catch (Exception e) { // 加载ActionModule失败
			throw new ControlException(ERROR_PRE_EXECUTE,
					m_msg.constructMessage("loadModuleFailed",
							req.getRequestPath()), e);
		}
		// }

		if (module instanceof WinletModule)
			moduleDef = ((WinletModule) module).m_moduleDef;

		// {检查访问权限
		BaseModuleDef bmd = moduleDef;

		while (bmd != null) {
			if (bmd.m_strAccessRule != null && !bmd.m_strAccessRule.equals("")) {
				if (userProfile.isAnonymous())
					throw new ControlException(ERROR_NOT_AUTHENTIC);
				if (!psnEngine.eveluate(bmd.m_strAccessRule))
					throw new ControlException(ERROR_NOT_AUTHORIZED);
			}
			bmd = bmd.m_parent;
		}
		// }

		req.bind(module, moduleDef, userProfile, psnEngine, res);

		if (moduleDef instanceof WinletActionDef)
			// 对于Winlet的操作，预先将返回码中的url设置为urlback
			res.setRetUrl(req.getParameterNotMultipart("urlback"));

		RetCode retCode = null;
		boolean bSkipExecution = false;

		try {
			if (moduleDef instanceof WinletStateDef)
				moduleDef.executeEventHandler(module.getImplementationObject(),
						EnumEvent.beforeState, req, res, null);
			else
				moduleDef.executeEventHandler(module.getImplementationObject(),
						EnumEvent.beforeAction, req, res, null);

			try {
				ThreadContext.pushAttribute(THREAD_ATTR_MODULE, module, true);
				res.setRetCode(module.execute(req, res));
			} catch (SkipExecuteException e) {
				bSkipExecution = true;
				res.setRetCode(e.getRetCode());
			} finally {
				ThreadContext.popAttribute(THREAD_ATTR_MODULE, true);
			}

			retCode = moduleDef.getRetCode(res.getRetCode());
		} catch (Throwable t) {
			res.setRetAttribute(null, null, null);

			// 在执行的方法中抛出的异常，会被包含在InvocationTargetException中
			if (t instanceof InvocationTargetException) {
				res.setRetThrow(((InvocationTargetException) t)
						.getTargetException());
				retCode = moduleDef.getRetCode(((InvocationTargetException) t)
						.getTargetException());
			} else {
				res.setRetThrow(t);
				retCode = moduleDef.getRetCode(t);
			}

			if (retCode == null)
				log(Priority.ERROR,
						req,
						m_msg.constructMessage("throwRetCodeNotDefined",
								req.getRequestPath(), t.toString()), t);
		}

		if (retCode == null) { // 没有找到响应码定义
			// 回滚数据库操作
			req.rollbackDBConn(true);

			// 用缺省的Logger记录日志
			try {
				m_defaultLogger.getLoggerInstance(false, this).log(moduleDef,
						module, null, req, res, bSkipExecution);
			} catch (Throwable t) {
			}

			throw new ControlException(ERROR_PRESENT, m_msg.constructMessage(
					"retCodeNotDefined", req.getRequestPath(),
					Integer.toString(res.getRetCode())));
		}

		// 获取个性化响应码
		retCode = retCode.getPsnRetCode(psnEngine);

		try {
			if (moduleDef instanceof WinletStateDef)
				moduleDef.executeEventHandler(module.getImplementationObject(),
						EnumEvent.afterState, req, res, retCode);
			else
				moduleDef.executeEventHandler(module.getImplementationObject(),
						EnumEvent.afterAction, req, res, retCode);
		} catch (Exception t) {
			throw new ControlException(ERROR_PRESENT, t);
		}

		// {查找响应码级别定义
		RetCodeLevelDef retCodeLevel = m_htRetCodeLevels.get(new Integer(
				retCode.m_iLevel).toString());
		if (retCodeLevel == null)
			retCodeLevel = m_defaultRetCodeLevel;
		// }

		// {根据响应码级别处理数据库连接
		if (retCodeLevel.m_iDBAction == RetCodeLevelDef.DB_COMMIT)
			req.commitDBConn(true);
		else if (retCodeLevel.m_iDBAction == RetCodeLevelDef.DB_ROLLBACK)
			req.rollbackDBConn(true);
		// }

		if (res.getLogMessage() == null)
			res.setLogMessage(retCode.getLogMessage());
		if (res.getUserMessage() == null)
			res.setUserMessage(retCode.getUserMessage());

		// {记录日志处理
		if (retCode.m_strLogger == null) { // 不记录日志
		} else if (retCode.m_strLogger.equals("")) { // 按响应码级别记录日志
			if (retCodeLevel.m_vecLoggers != null)
				for (Enumeration<RetCodeLevelLoggerDef> enm = retCodeLevel.m_vecLoggers
						.elements(); enm.hasMoreElements();) {
					RetCodeLevelLoggerDef loggerLevelDef = enm.nextElement();

					LoggerDef def = m_htLoggers.get(loggerLevelDef.m_strID);
					if (def != null)
						try {
							def.getLoggerInstance(false, this).log(moduleDef,
									module, retCode, req, res, bSkipExecution);
						} catch (Throwable t) {
						}
				}
		} else { // 指定了Logger
			LoggerDef def = m_htLoggers.get(retCode.m_strLogger);
			if (def != null)
				try {
					def.getLoggerInstance(false, this).log(moduleDef, module,
							retCode, req, res, bSkipExecution);
				} catch (Throwable t) {
				}
		}
		// }

		// 防止Logger中使用了数据库连接没有关闭
		req.rollbackDBConn(true);

		if (module instanceof WinletModule) {
			if (!retCode.m_strState.equals("")) { // 切换状态
				// 获取当前状态，以备检查状态是否变更
				WinletStateDef currentState = ((WinletModule) module)
						.getState();
				WinletStateDef newState = ((WinletModule) module)
						.setState(retCode.m_strState);

				if (newState == null) { // 状态未定义
					throw new ControlException(ERROR_PRESENT,
							m_msg.constructMessage("stateNotDefined",
									req.getRequestPath(), retCode.m_strState),
							null);
				}

				if (newState != currentState
						&& moduleDef instanceof WinletStateDef) // 在显示状态过程中，当前状态改变
					return newState.m_parent.m_parent;
			}
		}

		if (!res.isCommitted()) {
			// 设置窗口模式
			if (retCode.m_winMode != EnumWinMode.NOCHANGE)
				res.setHeader(RESPONSE_HEADER_SET_WINDOW_MODE,
						retCode.m_winMode.getStrId());

			// 设置窗口标题
			String title = res.getTitle();
			if (title == null)
				title = retCode.m_strWinTitle;
			if (!title.equals(""))
				try {
					res.setHeader(RESPONSE_HEADER_SET_WINDOW_TITLE,
							URLEncoder.encode(title, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}

			// 设置Cache标记
			if (retCode.m_bCache)
				res.setHeader(RESPONSE_HEADER_SET_CACHE, "YES");
			else
				res.setHeader(RESPONSE_HEADER_SET_CACHE, "NO");

			// 设置使用对话框标记
			if (retCode.m_bDialog)
				res.setHeader(RESPONSE_HEADER_SET_USE_DIALOG, "YES");
			else
				res.setHeader(RESPONSE_HEADER_SET_USE_DIALOG, "NO");

			// 设置更新的窗口
			if (module instanceof WinletModule) {
				String update = res.getUpdate();
				if (update == null || update.equals(""))
					update = retCode.m_strUpdate;

				if (!update.equals(""))
					res.setHeader(RESPONSE_HEADER_SET_UPDATE_WINDOW,
							((WinletModule) module).getViewInstance()
									.translateUpdateViews(req, update));
			}

			if (!retCode.m_strEnsureVisible.equals("")
					&& module instanceof WinletModule)
				if (retCode.m_strEnsureVisible.equalsIgnoreCase("no"))
					res.setHeader(RESPONSE_HEADER_SET_ENSURE_VISIBLE, "no");
				else
					res.setHeader(
							RESPONSE_HEADER_SET_ENSURE_VISIBLE,
							((WinletModule) module).getViewInstance()
									.translateUpdateViews(req,
											retCode.m_strEnsureVisible));

			// 设置提示信息
			try {
				res.setHeader(RESPONSE_HEADER_SET_MESSAGE, java.net.URLEncoder
						.encode(res.getUserMessage(), "UTF-8"));
			} catch (Exception e) {
			}
		}

		if (retCode.m_pMethod == EnumPresentMethod.PASS) { // 传递给下一个模块执行
			// {执行下一模块前清除当前的日志信息
			res.setUserMessage(null);
			res.setLogMessage(null);
			// }

			if (retCode.m_strUrl.startsWith("action:")) {
				// 传递给其他方法执行
				req.setActionID(retCode.m_strUrl.substring(7));
				return moduleDefBak;
			}

			return findModule(req, retCode.m_strUrl);
		}

		// 展现
		if (req.isValidateField() && req.getForm().getValidateField() != null) { // 表单字段校验，返回校验结果
			try {
				res.setHeader("Content-Type", "text/text;charset=UTF-8");
				res.getOutputStream().write(
						StringUtils.fixJson(
								((FormImpl) req.getForm()).getJsonChanges())
								.getBytes("UTF-8"));
			} catch (Exception e) {
				throw new ControlException(ERROR_PRESENT,
						m_msg.constructMessage("presentError",
								req.getRequestPath(),
								Integer.toString(res.getRetCode())), e);
			}
		} else {
			try {
				ThreadContext.pushAttribute(THREAD_ATTR_MODULE, module, true);
				ThreadContext.pushAttribute(THREAD_ATTR_RETCODE, retCode, true);
				ThreadContext.pushAttribute(THREAD_ATTR_EXCEPTION,
						res.getRetThrow(), true);
				if (module instanceof WinletModule)
					ThreadContext.pushAttribute(THREAD_ATTR_VIEW_INSTANCE,
							((WinletModule) module).getViewInstance(), true);

				present(req, res, retCode);
				req.commitDBConn(true);
			} catch (Exception e) {
				req.rollbackDBConn(true);

				throw new ControlException(ERROR_PRESENT,
						m_msg.constructMessage("presentError",
								req.getRequestPath(),
								Integer.toString(res.getRetCode())), e);
			} finally {
				if (module instanceof WinletModule)
					ThreadContext.popAttribute(THREAD_ATTR_VIEW_INSTANCE, true);
				ThreadContext.popAttribute(THREAD_ATTR_MODULE, true);
				ThreadContext.popAttribute(THREAD_ATTR_RETCODE, true);
				ThreadContext.popAttribute(THREAD_ATTR_EXCEPTION, true);
			}
		}

		return null;
	}

	public void handleControlException(IModuleRequest request,
			IModuleResponse response, ControlException e) {
		log(Priority.ERROR, request, e.getMessage(), e.m_e);

		URIDef m_uriDef = m_vecUris.elementAt(0);

		RetCode retCode = m_uriDef.getRetCode(e.getRetCode());

		if (retCode == null) { // 未定义系统级的响应码
			log(Priority.ERROR,
					request,
					m_msg.constructMessage("systemRetCodeNotDefined",
							Integer.toString(e.getRetCode())), null);
			return;
		}

		// {获取请求用户身份
		IUserEngine userEngine = null;
		IUserProfile userProfile = null;

		try {
			userEngine = getUserEngine(m_uriDef);
			userProfile = userEngine.getUserProfile(request, response);
		} catch (Exception ee) {
			log(Priority.ERROR,
					request,
					m_msg.constructMessage("systemUserProfileError",
							request.getRequestPath()), ee);
			return;
		}
		// }

		// {构造个性化引擎
		IPsnEngine psnEngine = null;

		try {
			psnEngine = newPsnEngine(request, m_uriDef, userProfile);
		} catch (Exception ee) {
			log(Priority.ERROR,
					request,
					m_msg.constructMessage("systemPsnEngineError",
							request.getRequestPath()), ee);
			return;
		}
		// }

		request.bind(null, m_uriDef, userProfile, psnEngine, response);
		response.setRetCode(retCode.m_iID);
		response.setRetAttribute(null, null, null);

		// 获取个性化的响应码定义
		retCode = retCode.getPsnRetCode(psnEngine);

		try {
			ThreadContext.pushAttribute(THREAD_ATTR_REQUEST, request, true);
			ThreadContext.pushAttribute(THREAD_ATTR_RETCODE, retCode, true);
			ThreadContext.pushAttribute(THREAD_ATTR_EXCEPTION, e.m_e, true);

			present(request, response, retCode);
		} catch (Exception ee) { // 处理系统展现遇到异常
			log(Priority.ERROR,
					request,
					m_msg.constructMessage("systemPresentError",
							Integer.toString(e.getRetCode())), e.m_e);
			return;
		} finally {
			ThreadContext.popAttribute(THREAD_ATTR_REQUEST, true);
			ThreadContext.popAttribute(THREAD_ATTR_RETCODE, true);
			ThreadContext.popAttribute(THREAD_ATTR_EXCEPTION, true);
		}
	}

	public void defaultProc(IModuleRequest request, IModuleResponse response)
			throws ControlException {
		// 根据请求查找对应的Module
		BaseModuleDef moduleDef = findModule(request, null);

		// 设置Locale Set，以便在POJO中使用Message Boundle
		LocaleManager.pushThreadLocale(request.getLocaleStr().toString());

		// {执行Module
		try {
			ThreadContext.pushAttribute(THREAD_ATTR_REQUEST, request, true);

			moduleDef = executeModule(request, response, moduleDef);
			while (moduleDef != null)
				moduleDef = executeModule(request, response, moduleDef);
		} finally {
			ThreadContext.popAttribute(THREAD_ATTR_REQUEST, true);

			LocaleManager.popThreadLocale();
			request.cleanUp();
		}
		// }
	}
}
