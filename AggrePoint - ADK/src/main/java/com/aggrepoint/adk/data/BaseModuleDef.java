package com.aggrepoint.adk.data;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;

import com.aggrepoint.adk.DynamicClassLoader;
import com.aggrepoint.adk.EnumEvent;
import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.MethodModule;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbData;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.res.ResourceManager;
import com.icebean.core.res.ResourceNotFoundException;

/**
 * URI定义
 * 
 * @author YJM
 */
public abstract class BaseModuleDef extends ADB {
	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_TEST = 2;
	public static final int STATUS_STOP = 0;

	public boolean m_bInPath;
	/** 上级定义 */
	public BaseModuleDef m_parent;
	/** 路径或ID */
	public String m_strPath;
	/** 完整路径 */
	public String m_strFullPath;
	/** 是否扩展路径匹配 */
	public boolean m_bExpandMatch;
	/** 动作模块类 */
	public String m_strModuleClass;
	/** 动作方法 */
	public String m_strMethod;
	/** 状态 */
	public int m_iStatus;
	/** 是否在初始化时加载 */
	public boolean m_bAutoLoad;
	/** 使用的个性化引擎的ID */
	public String m_strPsnEngineID;
	/** 使用的用户引擎的ID */
	public String m_strUserEngineID;
	/** 请求中是否允许出现文件对象 */
	public boolean m_bAcceptFile;
	/** 请求中若允许出现文件对象，则文件的最大尺寸(k) */
	public long m_lMaxFileSize;
	/** 用于单独限制文件对象尺寸，数组中每个元素为文件参数名称，其尺寸限制在对应m_arrFileSizes中 */
	public String[] m_arrFileParams;
	public long[] m_arrFileSizes;
	/** 请求中若允许出现文件对象，则最大的文件个数 */
	public int m_iMaxFileNum;
	/** 执行权限规则 */
	public String m_strAccessRule;
	/** 全局配置文件中定义的响应码 */
	public Vector<RetCode> m_vecRetCodes;
	/** 局部配置文件中定义的响应码 */
	public Vector<RetCode> m_vecModuleCodes;
	/** 日志记录参数定义，只能定义在全局配置文件中 */
	public Vector<LogParam> m_vecLogParams;
	/** 日志记录参数定义，从m_vecLogParams转换来，便于使用 */
	private LogParam[] m_arrLogParams;
	/** 响应码，用于查询。第一次查询时从m_vecRetCodes和m_vecModuleCodes转换过来 */
	public Hashtable<String, RetCode> m_htRetCodes;
	/** 抛出响应码，用于查询。第一次查询时从m_vecRetCodes和m_vecModuleCodes转换过来 */
	public Vector<RetCode> m_vecThrownRetCodes;
	/** 全局配置文件中定义的参数 */
	public Vector<Param> m_vecParams;
	/** 局部配置文件中定义的参数 */
	public Vector<Param> m_vecModuleParams;
	/** 参数，用于查询。第一次查询时从m_vecParams和m_vecModuleParams转换过来 */
	public Hashtable<String, Param> m_htParams;
	/** 全局配置文件中定义的文本 */
	public Vector<Message> m_vecMsgs;
	/** 局部配置文件中定义的文本 */
	public Vector<Message> m_vecModuleMsgs;
	/** 文本，用于查询。第一次查询时从m_vecMsgs和m_vecModuleMsgs转换过来 */
	public Hashtable<String, Message> m_htMsgs;
	/** 事件处理器 */
	public Vector<EventHandler> m_vecEventHandlers;

	/** 模块类 */
	public Class<?> m_moduleClass;
	/** 动作模块 */
	public IModule m_module;

	public BaseModuleDef() throws AdbException {
		m_bInPath = true;
		m_strPath = m_strFullPath = m_strModuleClass = m_strMethod = m_strPsnEngineID = m_strUserEngineID = m_strAccessRule = "";
		m_iStatus = STATUS_NORMAL;
		m_iMaxFileNum = 1;
		m_bAutoLoad = m_bAcceptFile = m_bExpandMatch = false;
		m_lMaxFileSize = 0;

		m_vecRetCodes = new Vector<RetCode>();
		m_vecModuleCodes = new Vector<RetCode>();
		m_vecParams = new Vector<Param>();
		m_vecLogParams = new Vector<LogParam>();
		m_arrLogParams = null;
		m_vecModuleParams = new Vector<Param>();
		m_htParams = null;
		m_vecMsgs = new Vector<Message>();
		m_vecModuleMsgs = new Vector<Message>();
		m_vecEventHandlers = new Vector<EventHandler>();

		m_htMsgs = null;
		m_arrFileParams = null;
		m_arrFileSizes = null;
	}

	public LogParam[] getLogParams() {
		if (m_arrLogParams == null) {
			m_arrLogParams = new LogParam[m_vecLogParams.size()];
			for (int i = 0; i < m_arrLogParams.length; i++)
				m_arrLogParams[i] = m_vecLogParams.elementAt(i);
		}
		return m_arrLogParams;
	}

	public String getStatus() {
		return Integer.toString(m_iStatus);
	}

	public void setStatus(String str) {
		if (str == null || str.equals(""))
			m_iStatus = STATUS_NORMAL;
		else
			m_iStatus = Integer.parseInt(str);
	}

	public String getExpandMatch() {
		return m_bExpandMatch ? "y" : "n";
	}

	public void setExpandMatch(String str) {
		if (str == null || str.equals(""))
			m_bExpandMatch = false;
		else
			m_bExpandMatch = str.equalsIgnoreCase("y");
	}

	public String getAutoLoad() {
		return m_bAutoLoad ? "y" : "n";
	}

	public void setAutoLoad(String str) {
		if (str == null || str.equals(""))
			m_bAutoLoad = false;
		else
			m_bAutoLoad = str.equalsIgnoreCase("y");
	}

	public String getAcceptFile() {
		return m_bAcceptFile ? "y" : "n";
	}

	public void setAcceptFile(String str) {
		if (str == null || str.equals(""))
			m_bAcceptFile = false;
		else
			m_bAcceptFile = str.equalsIgnoreCase("y");
	}

	public String getMaxFileSize() {
		return Long.toString(m_lMaxFileSize);
	}

	/**
	 * MaxFileSize
	 * 
	 * 若没有值，则认为限制尺寸为0 若格式中没有分号，则表示只指定最大尺寸
	 * 若格式中有分号，则表示在指定最大尺寸同时指定各个文件参数的尺寸限制，例如"800;slogo:10;llogo:100"
	 * 表示若不特别指明，最大尺寸为800K，对于名为slogo的上传文件，尺寸限制为10K，对于名为llogo的上传文件， 尺寸限制为100K
	 * 
	 * @param str
	 */
	public void setMaxFileSize(String str) {
		try {
			if (str == null || str.equals(""))
				m_lMaxFileSize = 0;
			else if (str.indexOf(";") == -1)
				m_lMaxFileSize = Long.parseLong(str) * 1024;
			else {
				StringTokenizer st = new StringTokenizer(str, "; ");

				int count = st.countTokens();
				if (count > 0)
					m_lMaxFileSize = Long.parseLong(st.nextToken()) * 1024;

				if (count > 1) {
					count--;
					m_arrFileParams = new String[count];
					m_arrFileSizes = new long[count];
					int p;

					for (int i = 0; i < count; i++) {
						m_arrFileParams[i] = "";
						m_arrFileSizes[i] = 0;

						str = st.nextToken();
						p = str.indexOf(":");
						if (p == -1)
							continue;

						m_arrFileParams[i] = str.substring(0, p).trim();
						m_arrFileSizes[i] = Long.parseLong(str.substring(p + 1)
								.trim()) * 1024;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMaxFileNum() {
		return Integer.toString(m_iMaxFileNum);
	}

	public void setMaxFileNum(String str) {
		if (str == null || str.equals(""))
			m_iMaxFileNum = 1;
		else
			m_iMaxFileNum = Integer.parseInt(str);
	}

	public Class<?> getModuleClass(boolean reload) throws Exception {
		synchronized (this) {
			if (m_moduleClass == null || reload)
				m_moduleClass = new DynamicClassLoader(getClass()
						.getClassLoader()).loadClass(m_strModuleClass);
		}
		return m_moduleClass;
	}

	/**
	 * 获取模块实例
	 */
	public IModule getModuleInstance(boolean reload, IServerContext context,
			IModuleRequest req) throws Exception {
		try {
			if (m_module == null || reload) {
				m_htRetCodes = null;
				m_htParams = null;
				m_htMsgs = null;

				getModuleClass(reload);

				// {加载Module上定义的配置
				try {
					InputStream is = ResourceManager.getResourceManager(
							getClass(), "/CFG-INF/amcfg_load.xml")
							.loadResource(m_moduleClass);

					Document doc = javax.xml.parsers.DocumentBuilderFactory
							.newInstance().newDocumentBuilder().parse(
									new org.xml.sax.InputSource(is));
					AdbAdapter adapter = new XmlAdapter(doc);

					RetCode retCode = new RetCode();
					m_vecModuleCodes = adapter.retrieveMulti(retCode,
							"loadRoot", "");

					Param param = new Param();
					m_vecModuleParams = adapter.retrieveMulti(param,
							"loadByModule", "");

					Message msg = new Message();
					m_vecModuleMsgs = adapter.retrieveMulti(msg,
							"loadByModule", "");

					is.close();
				} catch (ResourceNotFoundException e) { // Module上可以不定义配置
				}
				// }

				// {构建并初始化Module实例
				if (m_strMethod == null || m_strMethod.equals("")) {
					m_module = (IModule) m_moduleClass.newInstance();
					m_module.init(this, context);
				} else {
					m_module = new MethodModule((IModule) m_moduleClass
							.newInstance(), m_moduleClass.getMethod(
							m_strMethod, IAdkConst.MODULE_METHOD_PARAM_TYPE));
					m_module.init(this, context);
				}
				// }
			}
		} catch (Exception e) {
			m_module = null;
			throw e;
		}

		return m_module;
	}

	/**
	 * 对象加载成功后，加载子对象前作为Trigger被调用，负责获取上级URI定义，构造完整的请求路径，继承 上级个性化引擎定义
	 */
	public void beforeSub(AdbAdapter adapter, Integer methodType,
			String methodId) {
		try {
			AdbData<BaseModuleDef> data = adapter.getData(this);
			if (data.m_objMaster != null
					&& data.m_objMaster instanceof BaseModuleDef) {
				m_parent = data.m_objMaster;
				if (m_strPsnEngineID.equals(""))
					m_strPsnEngineID = m_parent.m_strPsnEngineID;
				if (m_strUserEngineID.equals(""))
					m_strUserEngineID = m_parent.m_strUserEngineID;

				// 从上级继承事件处理器
				m_vecEventHandlers.addAll(m_parent.m_vecEventHandlers);

				BaseModuleDef parentPathDef = m_parent;
				while (parentPathDef != null && !parentPathDef.m_bInPath)
					parentPathDef = parentPathDef.m_parent;

				if (parentPathDef != null) {
					if (parentPathDef.m_strFullPath.equals(""))
						parentPathDef.m_strFullPath = "/"
								+ parentPathDef.m_strPath;
					m_strFullPath = parentPathDef.m_strFullPath + "/"
							+ m_strPath;
				} else {
					m_strFullPath = "/" + m_strPath;
				}
			} else {
				m_strFullPath = "/" + m_strPath;
			}
		} catch (Exception e) {
		}
	}

	void buildRetCodeHashtable() {
		if (m_htRetCodes == null) { // 第一次获取
			m_htRetCodes = new Hashtable<String, RetCode>();
			m_vecThrownRetCodes = new Vector<RetCode>();

			for (Enumeration<RetCode> enm = m_vecRetCodes.elements(); enm
					.hasMoreElements();) {
				RetCode retCode = enm.nextElement();
				if (retCode.getThrowClass() == null)
					m_htRetCodes.put(Integer.toString(retCode.m_iID), retCode);
				else
					m_vecThrownRetCodes.add(retCode);
			}
			// 保留m_vecRetCodes以备模块重新加载时使用

			for (Enumeration<RetCode> enm = m_vecModuleCodes.elements(); enm
					.hasMoreElements();) {
				RetCode retCode = enm.nextElement();
				if (retCode.getThrowClass() == null)
					m_htRetCodes.put(Integer.toString(retCode.m_iID), retCode);
				else
					m_vecThrownRetCodes.add(retCode);
			}
			m_vecModuleCodes.clear();
		}
	}

	/**
	 * 查找响应码定义
	 */
	public RetCode getRetCode(int code) {
		buildRetCodeHashtable();

		RetCode retCode = m_htRetCodes.get(Integer.toString(code));
		if (retCode != null)
			return retCode;

		if (m_parent == null)
			return null;

		return m_parent.getRetCode(code);
	}

	/**
	 * 根据抛出查找匹配的响应码
	 */
	public RetCode getRetCode(Throwable t) {
		buildRetCodeHashtable();

		for (Enumeration<RetCode> enm = m_vecThrownRetCodes.elements(); enm
				.hasMoreElements();) {
			RetCode retCode = enm.nextElement();
			if (retCode.getThrowClass().isAssignableFrom(t.getClass()))
				return retCode;
		}

		if (m_parent == null)
			return null;

		return m_parent.getRetCode(t);
	}

	/**
	 * 获取参数
	 */
	public Param getParameter(String name) {
		if (m_htParams == null) { // 第一次获取
			m_htParams = new Hashtable<String, Param>();

			for (Enumeration<Param> enm = m_vecParams.elements(); enm
					.hasMoreElements();) {
				Param param = enm.nextElement();
				m_htParams.put(param.m_strName, param);
			}
			// 保留m_vecParams以备模块重新加载时使用

			for (Enumeration<Param> enm = m_vecModuleParams.elements(); enm
					.hasMoreElements();) {
				Param param = enm.nextElement();
				m_htParams.put(param.m_strName, param);
			}
			m_vecModuleParams.clear();
		}

		Param param = m_htParams.get(name);
		if (param != null)
			return param;

		if (m_parent == null)
			return null;

		return m_parent.getParameter(name);
	}

	/**
	 * 获取文本
	 */
	public Message getMessage(String name) {
		if (m_htMsgs == null) { // 第一次获取
			m_htMsgs = new Hashtable<String, Message>();

			for (Enumeration<Message> enm = m_vecMsgs.elements(); enm
					.hasMoreElements();) {
				Message msg = enm.nextElement();
				m_htMsgs.put(msg.getName(), msg);
			}

			// 保留m_vecMsgs以备模块重新加载时使用
			for (Enumeration<Message> enm = m_vecModuleMsgs.elements(); enm
					.hasMoreElements();) {
				Message msg = enm.nextElement();
				m_htMsgs.put(msg.getName(), msg);
			}
			m_vecModuleMsgs.clear();
		}

		Message msg = m_htMsgs.get(name);
		if (msg != null)
			return msg;

		if (m_parent == null)
			return null;

		return m_parent.getMessage(name);
	}

	public void executeEventHandler(Object obj, EnumEvent[] events,
			IModuleRequest req, IModuleResponse resp, RetCode retCode)
			throws Exception {
		for (EventHandler handler : m_vecEventHandlers)
			handler.execute(obj, events, req, resp, retCode);
	}

	public abstract void print(String tab, java.io.PrintWriter pw);

	public void printStartTag(String tab, String pathAttr,
			java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("<");
		pw.print(tag);
		pw.print(" ");
		pw.print(pathAttr);
		pw.print("=\"");
		pw.print(m_strPath);
		if (getExpandMatch().equals("y")) {
			pw.print("\" expand_match=\"y");
		}
		if (!m_strModuleClass.equals("")) {
			pw.print("\" module=\"");
			pw.print(m_strModuleClass);
		}
		if (!m_strMethod.equals("")) {
			pw.print("\" method=\"");
			pw.print(m_strMethod);
		}
		if (m_iStatus != STATUS_NORMAL) {
			pw.print("\" status=\"");
			pw.print(getStatus());
		}
		if (getAutoLoad().equals("y"))
			pw.print("\" load=\"y");
		if (!m_strPsnEngineID.equals("")) {
			pw.print("\" psn_engine=\"");
			pw.print(m_strPsnEngineID);
		}
		if (!m_strUserEngineID.equals("")) {
			pw.print("\" user_engine=\"");
			pw.print(m_strUserEngineID);
		}
		if (getAcceptFile().equals("y")) {
			pw.print("\" accept_file=\"y");
			pw.print("\" max_file_size=\"");
			pw.print(getMaxFileSize());
			pw.print("\" max_file_num=\"");
			pw.print(getMaxFileNum());
		}
		if (!m_strAccessRule.equals("")) {
			pw.print("\" access_rule=\"");
			pw.print(m_strAccessRule);
		}
		pw.println("\">");
	}

	public void printSubElements(String tab, java.io.PrintWriter pw) {
		for (Enumeration<RetCode> enm = m_vecRetCodes.elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "retcode");
		for (Enumeration<Param> enm = m_vecParams.elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "param");
		for (Enumeration<Message> enm = m_vecMsgs.elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw, "msg");
		for (Enumeration<LogParam> enm = m_vecLogParams.elements(); enm
				.hasMoreElements();)
			enm.nextElement().print(tab + "\t", pw);
	}

	public void printEndTag(String tab, java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("</");
		pw.print(tag);
		pw.println(">");
	}
}
