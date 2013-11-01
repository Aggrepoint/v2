package com.aggrepoint.adk.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.MethodModule;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletHelper;
import com.aggrepoint.adk.WinletModule;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.Message;
import com.aggrepoint.adk.data.Param;
import com.aggrepoint.adk.data.WinletMethodDef;
import com.aggrepoint.adk.form.Form;
import com.aggrepoint.adk.form.FormImpl;
import com.aggrepoint.adk.form.InputImpl;
import com.icebean.core.common.ClassStack;
import com.icebean.core.common.FileUtils;
import com.icebean.core.common.MultipartHeader;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.common.TypeCast;
import com.icebean.core.common.UploadReader;
import com.icebean.core.conn.db.DBConnManager;

/**
 * @author YJM
 */
public class HttpModuleRequest implements IModuleRequest, IAdkConst,
		IWinletConst {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	/** 信息 */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	static final String REQ_KEY_REQ_ID = HttpModuleRequest.class.getName()
			+ ".REQ_ID";
	static final String REQ_KEY_PROPS = HttpModuleRequest.class.getName()
			+ ".PROPS";

	long m_lReqId;

	/** 服务器环境 */
	IServerContext m_context;

	/** 被封装的请求对象 */
	HttpServletRequest m_req;

	/** ControlServlet被映射到的路径，以/开头，不以/结尾 */
	String m_strControlPath;

	/** WebSphere中请求被forward到JSP后PathInfo和RequestURI都被改为JSP相关，若要获取原始值，需要先保存 */
	String m_strPathInfo;
	String m_strRequestURI;

	/** 模块定义 */
	BaseModuleDef m_def;

	/** 个性化引擎 */
	IPsnEngine m_psnEngine;

	/** 用户身份 */
	IUserProfile m_userProfile;

	/** 当前请求对应的窗口实例的ID */
	String m_strWinIID;

	String m_strViewID;

	String m_strPageID;

	String m_strActionID;

	String m_strFormID;

	boolean m_bIsValidateField;

	FormImpl m_form;

	/** 临时请求目录 */
	String m_strTempDir;

	/** 请求内容是否为multipart/form-data */
	boolean m_bMultipart;

	/**
	 * 若请求内容是multipart/form-data，则保存所有字符串参数，下标是参数名，若单值，则值为参数值，若多值，则值为Vector，
	 * 其中各个元素为各个值
	 */
	Hashtable<String, Object> m_htParams;

	/** 若请求内容是multipart/form-data，则保存所有文件参数 */
	Hashtable<String, Object> m_htFileParams;

	/** 是否已经处理过multipart/form-data形式的请求 */
	boolean m_bMultipartHeaderParsed;

	/** 已经分配的数据库连接 */
	Vector<Connection> m_vecConns;

	/** 重复使用的数据库连接 */
	Connection m_reuseConnection;

	/** 创建时间 */
	long m_lCreateTime;

	Object m_moduleObject;

	public void parseMultipartHeader() {
		if (!m_bMultipartHeaderParsed && m_bMultipart) {
			try {
				m_htParams = new Hashtable<String, Object>();
				m_htFileParams = new Hashtable<String, Object>();

				MultipartHeader multipartHeader;
				String paramName, fileName;
				Object value;
				Vector<Object> vecValues;
				UploadReader uploadReader = new UploadReader(m_req, "UTF-8");
				FileOutputStream fos;
				FileParameter fileParam;
				int fileIdx = 0;
				int c;

				while ((multipartHeader = uploadReader.getHeader()) != null) {
					paramName = multipartHeader.getParamName();

					if (multipartHeader.isFile()) {
						fileName = multipartHeader.getFileName();
						if (!fileName.equals("")) {
							if (!m_def.m_bAcceptFile)
								throw new Exception(
										m_msg.getMessage("notAcceptFile"));

							if (m_htFileParams.size() == m_def.m_iMaxFileNum)
								throw new Exception(
										m_msg.getMessage("exceedFileNumLimit"));

							fileParam = new FileParameter(this);
							fileParam.m_strFileName = fileName;
							fileParam.m_strContentType = multipartHeader
									.getContentType();
							fileParam.m_strFullPath = getTempDir() + "/"
									+ (++fileIdx);

							long sizeLimit = m_def.m_lMaxFileSize;
							if (m_def.m_arrFileParams != null)
								for (int i = 0; i < m_def.m_arrFileParams.length; i++)
									if (m_def.m_arrFileParams[i]
											.equals(paramName)) {
										sizeLimit = m_def.m_arrFileSizes[i];
										break;
									}

							boolean bExceedFileSizeLimit = false;

							fos = new FileOutputStream(fileParam.m_strFullPath);
							while ((c = uploadReader.getBodyChar()) != -1) {
								fos.write(c);
								fileParam.m_lSize++;

								if (fileParam.m_lSize > sizeLimit) {
									fileParam.m_lSize = -fileParam.m_lSize;
									bExceedFileSizeLimit = true;
									break;
								}
							}
							fos.close();

							value = m_htFileParams.get(paramName);
							if (value == null)
								m_htFileParams.put(paramName, fileParam);
							else if (value instanceof FileParameter) {
								vecValues = new Vector<Object>();
								vecValues.add(value);
								vecValues.add(fileParam);
								m_htFileParams.put(paramName, vecValues);
							} else {
								vecValues = TypeCast.cast(value);
								vecValues.add(fileParam);
							}

							if (bExceedFileSizeLimit)
								throw new Exception(
										m_msg.getMessage("exceedFileSizeLimit"));
						} else
							uploadReader.skipFile();
					} else {
						value = m_htParams.get(paramName);
						if (value == null)
							m_htParams.put(paramName,
									uploadReader.getParamValue());
						else if (value instanceof String) {
							vecValues = new Vector<Object>();
							vecValues.add(value);
							vecValues.add(uploadReader.getParamValue());
							m_htParams.put(paramName, vecValues);
						} else {
							vecValues = TypeCast.cast(value);
							vecValues.add(uploadReader.getParamValue());
						}
					}
				}
			} catch (Exception e) {
				m_log.error(
						m_msg.getMessage("parseMultipartHeaderException", ""),
						e);
			}
		}

		m_bMultipartHeaderParsed = true;
	}

	/**
	 * 构造和初始化分为两个阶段： 1.接收到请求、未找到请求对应的Module定义时，使用构造函数构造；
	 * 2.找到请求对应的Module定义后，用bind()方法与Module定义绑定。
	 */
	public HttpModuleRequest(IServerContext context, HttpServletRequest req,
			long reqId) {
		m_lCreateTime = System.currentTimeMillis();
		m_lReqId = reqId;

		m_context = context;
		m_req = req;
		m_def = null;
		m_psnEngine = null;
		m_userProfile = null;
		m_htParams = m_htFileParams = null;
		m_bMultipartHeaderParsed = false;
		m_strTempDir = null;

		m_strPathInfo = req.getPathInfo();
		m_strControlPath = m_strRequestURI = req.getRequestURI();

		if (m_strPathInfo == null)
			m_strPathInfo = "/";
		else
			m_strControlPath = m_strControlPath.substring(0,
					(m_strControlPath.lastIndexOf(m_strPathInfo)));

		m_strControlPath = m_strControlPath.substring(m_strControlPath
				.lastIndexOf('/'));

		m_bMultipart = UploadReader.isMultiPart(req);
		m_strWinIID = getParameterNotMultipart(REQUEST_PARAM_WIN_INST_ID, "");
		m_strViewID = getHeader(REQUEST_HEADER_VIEW_HEADER_ID);
		if (m_strViewID == null || m_strViewID.equals(""))
			m_strViewID = getParameterNotMultipart(REQUEST_PARAM_VIEW_ID, null);
		if (m_strViewID == null || m_strViewID.equals(""))
			m_strViewID = m_strWinIID;
		m_strPageID = getParameterNotMultipart(REQUEST_PARAM_WIN_PAGE_ID, "");
		m_strActionID = getParameterNotMultipart(REQUEST_PARAM_ACTION, null);
		m_strFormID = getParameterNotMultipart(REQUEST_PARAM_FORM, null);
		m_bIsValidateField = m_strFormID != null
				&& !m_strFormID.trim().equals("")
				&& getParameterNotMultipart(REQUEST_PARAM_VALIDATE_FIELD, "no")
						.equalsIgnoreCase("yes");
	}

	public void bind(IModule module, BaseModuleDef def,
			IUserProfile userProfile, IPsnEngine psnEngine, IModuleResponse resp) {
		m_def = def;
		m_userProfile = userProfile;
		m_psnEngine = psnEngine;
		m_form = null;

		if (module != null) {
			if (module instanceof WinletModule) {
				m_moduleObject = ((WinletModule) module).getViewInstance()
						.getWinlet();

				if (((WinletModule) module).isAction()) {
					try {
						m_form = WinletHelper.getForm(
								this,
								getParameterNotMultipart(REQUEST_PARAM_FORM,
										null));
						if (m_form == null)
							return;

						// 对表单进行处理
						m_form.clearError();

						if (m_bIsValidateField) { // 单个字段校验，Action可以不匹配（单独指定字段校验action）
							m_form.setValidateField(null);

							m_form.startRecordChanges();

							InputImpl input = m_form
									.getInputByName(getParameterNotMultipart(
											"name", null));

							if (input == null)
								return;

							m_form.setValidateField(input);
							input.populate(this, getParameterValues("value"));
						} else if (m_form.getAction().equals(m_strActionID)) { // 整个表单提交，Action必须匹配
							for (InputImpl input : m_form.getInputs())
								input.populate(this);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (module instanceof MethodModule) {
				m_moduleObject = ((MethodModule) module)
						.getImplementationObject();
			} else {
				m_moduleObject = module;
			}
		}
	}

	/** 获取请求序列号 */
	public long getRequestID() {
		return m_lReqId;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#getRequestObject()
	 */
	public Object getRequestObject() {
		return m_req;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#getContext()
	 */
	public IServerContext getContext() {
		return m_context;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#getDef()
	 */
	public BaseModuleDef getDef() {
		return m_def;
	}

	/** 获取ControlServlet被映射到的路径，以/开头，不以/结尾 */
	public String getControlPath() {
		return m_strControlPath;
	}

	/** 在Pass过程中，可能会将控制传向另外一个映射路径，这时可以调用这个方法来更改ControlPath */
	public void setControlPath(String path) {
		m_strControlPath = path;
	}

	/** 获取请求路径，不会返回null */
	public String getRequestPath() {
		String str = m_req.getHeader(REQUEST_HEADER_VIEW_HEADER_REQ_PATH);
		if (str == null || str.equals(""))
			str = m_strPathInfo;
		if (str == null)
			str = "";
		return str;
	}

	public String getRequestURI() {
		return m_strRequestURI;
	}

	public boolean isMultipart() {
		return m_bMultipart;
	}

	public boolean isValidateField() {
		return m_form != null && m_bIsValidateField;
	}

	public Form getForm() {
		return m_form;
	}

	/**
	 * 清除当前表单
	 * 
	 */
	public void resetForm() {
		if (m_form != null)
			m_form.reset();
	}

	/**
	 * 清除当前视图内的表单
	 * 
	 * @param name
	 */
	public void resetForm(Winlet winlet, String name) {
		if (winlet == null || name == null)
			return;

		if (m_def instanceof WinletMethodDef)
			WinletHelper.resetForms(this, winlet,
					((WinletMethodDef) m_def).m_view.m_strPath, name);
	}

	/**
	 * 清除指定视图的表单
	 * 
	 * @param name
	 */
	public void resetForm(Winlet winlet, String view, String name) {
		if (winlet == null || name == null || view == null)
			return;

		WinletHelper.resetForms(this, winlet, view, name);
	}

	/**
	 * 获取上传的文件参数
	 */
	public FileParameter getFileParameter(String paraName) {
		if (!m_bMultipartHeaderParsed && m_bMultipart)
			parseMultipartHeader();

		if (m_htFileParams == null)
			return null;

		Object obj = m_htFileParams.get(paraName);
		if (obj == null)
			return null;

		if (obj instanceof FileParameter)
			return (FileParameter) obj;

		if (obj instanceof Vector) {
			Vector<FileParameter> vec = TypeCast.cast(obj);
			return vec.elementAt(1);
		}

		return null;
	}

	/**
	 * 获取多值的上传文件参数
	 */
	public FileParameter[] getFileParameters(String paraName) {
		if (!m_bMultipartHeaderParsed && m_bMultipart)
			parseMultipartHeader();

		if (m_htFileParams == null)
			return null;

		Object obj = m_htFileParams.get(paraName);
		if (obj == null)
			return null;

		if (obj instanceof FileParameter)
			return new FileParameter[] { (FileParameter) obj };

		if (obj instanceof Vector) {
			Vector<FileParameter> vec = TypeCast.cast(obj);
			FileParameter[] params = new FileParameter[vec.size()];
			int i = 0;
			for (Enumeration<FileParameter> enm = vec.elements(); enm
					.hasMoreElements(); i++)
				params[i] = enm.nextElement();

			return params;
		}

		return null;
	}

	/**
	 * 获取参数名称列表
	 */
	public String[] getParameterNamesNotMultipart(String[] excludes) {
		Vector<String> vecNames = new Vector<String>();
		boolean bExclude;
		String name;

		for (Enumeration<?> enm = m_req.getParameterNames(); enm
				.hasMoreElements();) {
			bExclude = false;

			name = enm.nextElement().toString();
			if (name == null)
				continue;

			if (excludes != null) {
				for (int i = 0; i < excludes.length; i++)
					if (excludes[i] != null && name.equals(excludes[i])) {
						bExclude = true;
						break;
					}
			}

			if (!bExclude)
				vecNames.add(name);
		}

		String[] names = new String[vecNames.size()];
		int i = 0;
		for (Enumeration<String> enm = vecNames.elements(); enm
				.hasMoreElements();)
			names[i++] = enm.nextElement();

		return names;
	}

	/**
	 * 获取参数，不分解MultiPart参数
	 */
	public String getParameterNotMultipart(String name, String def) {
		String str;

		str = m_req.getParameter(name);
		if (str == null)
			return def;
		return str.trim();
	}

	public String getParameterNotMultipart(String paraName) {
		return getParameterNotMultipart(paraName, "");
	}

	/**
	 * 获取参数
	 */
	public String getParameter(String paraName, String defaultValue) {
		if (!m_bMultipartHeaderParsed && m_bMultipart)
			parseMultipartHeader();

		String str;

		if (m_htParams == null)
			str = m_req.getParameter(paraName);
		else {
			Object obj = m_htParams.get(paraName);

			if (obj == null)
				str = null;
			else if (obj instanceof String)
				str = (String) obj;
			else if (obj instanceof Vector) {
				Vector<String> vec = TypeCast.cast(obj);
				str = vec.elementAt(0);
			} else
				str = null;

			if (str == null)
				str = m_req.getParameter(paraName);
		}

		if (str == null)
			str = defaultValue;

		if (str != null)
			return str.trim();

		return null;
	}

	public String[] getParameterValuesNotMultipart(String name) {
		return m_req.getParameterValues(name);
	}

	public String[] getParameterNames(String[] excludes) {
		if (!m_bMultipartHeaderParsed && m_bMultipart)
			parseMultipartHeader();

		if (m_htParams == null)
			return getParameterNamesNotMultipart(excludes);
		else {
			Vector<String> vecNames = new Vector<String>();
			boolean bExclude;
			String name;

			for (Enumeration<String> enm = m_htParams.keys(); enm
					.hasMoreElements();) {
				bExclude = false;

				name = enm.nextElement();
				if (name == null)
					continue;

				if (excludes != null) {
					for (int i = 0; i < excludes.length; i++)
						if (excludes[i] != null && name.equals(excludes[i])) {
							bExclude = true;
							break;
						}
				}

				if (!bExclude)
					vecNames.add(name);
			}

			String[] names = new String[vecNames.size()];
			int i = 0;
			for (Enumeration<String> enm = vecNames.elements(); enm
					.hasMoreElements();)
				names[i++] = enm.nextElement();

			return names;
		}
	}

	public String getParameter(String paraName) {
		return getParameter(paraName, "");
	}

	public String[] getParameterValues(String paraName) {
		if (!m_bMultipartHeaderParsed && m_bMultipart)
			parseMultipartHeader();

		if (m_htParams == null)
			return m_req.getParameterValues(paraName);
		else {
			Object obj = m_htParams.get(paraName);
			if (obj == null)
				return null;

			if (obj instanceof String)
				return new String[] { (String) obj };

			if (obj instanceof Vector) {
				Vector<String> vec = TypeCast.cast(obj);
				String[] values = new String[vec.size()];
				int i = 0;
				for (Enumeration<String> enm = vec.elements(); enm
						.hasMoreElements(); i++)
					values[i] = enm.nextElement();
				return values;
			} else
				return null;
		}
	}

	public int getParameter(String paramName, int defaultValue) {
		try {
			return Integer.parseInt(getParameter(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public long getParameter(String paramName, long defaultValue) {
		try {
			return Long.parseLong(getParameter(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getParameter(String paramName, float defaultValue) {
		try {
			return Float.parseFloat(getParameter(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getParameter(String paramName, double defaultValue) {
		try {
			return Double.parseDouble(getParameter(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public int getParameterNotMultipart(String paramName, int defaultValue) {
		try {
			return Integer.parseInt(getParameterNotMultipart(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public long getParameterNotMultipart(String paramName, long defaultValue) {
		try {
			return Long.parseLong(getParameterNotMultipart(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getParameterNotMultipart(String paramName, float defaultValue) {
		try {
			return Float.parseFloat(getParameterNotMultipart(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getParameterNotMultipart(String paramName, double defaultValue) {
		try {
			return Double.parseDouble(getParameterNotMultipart(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Hashtable<String, Object> getParameters() {
		Hashtable<String, Object> ht = new Hashtable<String, Object>();

		for (String str : getParameterNames(null))
			if (m_htParams == null) {
				String[] values = m_req.getParameterValues(str);
				if (values == null)
					continue;
				if (values.length == 1)
					ht.put(str, values[0]);
				else
					ht.put(str, values);
			} else
				ht.put(str, m_htParams.get(str));
		return ht;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#setAttribute(Object, Object)
	 */
	public void setAttribute(String key, Object value) {
		m_req.setAttribute(key, value);
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#getAttribute(Object)
	 */
	public Object getAttribute(String key) {
		if (key == null)
			return null;

		return m_req.getAttribute(key);
	}

	public String getAttribute(String key, String def) {
		if (key == null)
			return def;

		try {
			return m_req.getAttribute(key).toString();
		} catch (Exception e) {
			return def;
		}
	}

	public int getAttribute(String key, int def) {
		try {
			return Integer.parseInt(getAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public long getAttribute(String key, long def) {
		try {
			return Long.parseLong(getAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public float getAttribute(String key, float def) {
		try {
			return Float.parseFloat(getAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public double getAttribute(String key, double def) {
		try {
			return Double.parseDouble(getAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#removeAttribute(Object)
	 */
	public void removeAttribute(String key) {
		m_req.removeAttribute(key);
	}

	/**
	 * @see com.aggrepoint.adk.IModuleRequest#getPsnEngine()
	 */
	public IPsnEngine getPsnEngine() {
		return m_psnEngine;
	}

	public String getParam(String name, String def) {
		if (name == null)
			return def;

		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

		String val;
		if (view != null) {
			val = view.getParam(name);
			if (val != null)
				return val;
		}

		Param param = m_def.getParameter(name);
		if (param == null)
			return def;
		val = param.getPsnParam(m_psnEngine).m_strValue;
		if (val == null)
			val = def;
		return val;
	}

	public String getParam(String name) {
		return getParam(name, null);
	}

	public int getParam(String name, int def) {
		try {
			return Integer.parseInt(getParam(name, null));
		} catch (Exception e) {
			return def;
		}
	}

	public long getParam(String name, long def) {
		try {
			return Long.parseLong(getParam(name, null));
		} catch (Exception e) {
			return def;
		}
	}

	public float getParam(String name, float def) {
		try {
			return Float.parseFloat(getParam(name, null));
		} catch (Exception e) {
			return def;
		}
	}

	public double getParam(String name, double def) {
		try {
			return Double.parseDouble(getParam(name, null));
		} catch (Exception e) {
			return def;
		}
	}

	public String getMessage(String name, String[] inPlaceString) {
		Message msg = m_def.getMessage(name);
		if (msg == null)
			return "";
		return msg.getPsnMessage(m_psnEngine).constructMessage(inPlaceString);
	}

	public String getMessage(String name, String str) {
		return getMessage(name, new String[] { str });
	}

	public String getMessage(String name, String str1, String str2) {
		return getMessage(name, new String[] { str1, str2 });
	}

	public String getMessage(String name, String str1, String str2, String str3) {
		return getMessage(name, new String[] { str1, str2, str3 });
	}

	public String getMessage(String name, String str1, String str2,
			String str3, String str4) {
		return getMessage(name, new String[] { str1, str2, str3, str4 });
	}

	public String getMessage(String name, String str1, String str2,
			String str3, String str4, String str5) {
		return getMessage(name, new String[] { str1, str2, str3, str4, str5 });
	}

	public Object getSessionAttribute(String key) {
		if (key == null)
			return null;

		return m_req.getSession(true).getAttribute(key);
	}

	public String getSessionAttribute(String key, String def) {
		if (key == null)
			return def;

		try {
			return m_req.getSession(true).getAttribute(key).toString();
		} catch (Exception e) {
			return def;
		}
	}

	public int getSessionAttribute(String key, int def) {
		try {
			return Integer.parseInt(getSessionAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public long getSessionAttribute(String key, long def) {
		try {
			return Long.parseLong(getSessionAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public float getSessionAttribute(String key, float def) {
		try {
			return Float.parseFloat(getSessionAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public double getSessionAttribute(String key, double def) {
		try {
			return Double.parseDouble(getSessionAttribute(key, ""));
		} catch (Exception e) {
			return def;
		}
	}

	public void setSessionAttribute(String key, Object value) {
		m_req.getSession(true).setAttribute(key, value);
	}

	public void removeSessionAttribute(String key) {
		m_req.getSession(true).removeAttribute(key);
	}

	public void invalidSession() {
		m_req.getSession().invalidate();
	}

	/**
	 * FrontRequest 用于在一次前端请求引起的多次后端请求中保存共享数据
	 * 
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Hashtable<String, Object> getFrontRequestAttributes() {
		String reqid = getHeader(REQUEST_HEADER_REQUEST_ID);
		if (reqid == null)
			return null;

		Hashtable<String, Object> htProps = null;
		if (reqid.equals(getSessionAttribute(REQ_KEY_REQ_ID)))
			htProps = (Hashtable<String, Object>) getSessionAttribute(REQ_KEY_PROPS);
		else
			setSessionAttribute(REQ_KEY_REQ_ID, reqid);
		if (htProps == null) {
			htProps = new Hashtable<String, Object>();
			setSessionAttribute(REQ_KEY_PROPS, htProps);
		}
		return htProps;
	}

	public Object setFrontRequestAttribute(String key, Object val) {
		if (key == null)
			return val;
		Hashtable<String, Object> props = getFrontRequestAttributes();
		if (props == null)
			return val;
		if (val == null)
			props.remove(key);
		else
			props.put(key, val);
		return val;
	}

	public Object getFrontRequestAttribute(String key) {
		if (key == null)
			return null;
		Hashtable<String, Object> props = getFrontRequestAttributes();
		if (props == null)
			return null;
		return props.get(key);
	}

	/** 获取当前请求对应的窗口实例的ID */
	public String getWinIID() {
		return m_strWinIID;
	}

	public String getViewID() {
		return m_strViewID;
	}

	public String getPageID() {
		return m_strPageID;
	}

	public String getActionID() {
		return m_strActionID;
	}

	public void setActionID(String action) {
		m_strActionID = action;
	}

	/**
	 * 获取位于同一页的、指定类型的Winlet
	 */
	public Winlet getWinlet(String className) throws Exception {
		return WinletHelper.getWinletInPage(this, className);
	}

	/**
	 * 获取指定类型的Winlet
	 */
	public Winlet getWinletInSite(String className) {
		return WinletHelper.getWinletInSite(this, className);
	}

	/**
	 * 获取当前请求的参数列表以构造URL
	 * 
	 * @param arrExcept
	 *            要排除在返回的URL之外的参数名称，可以为null
	 * @return String 若没有任何参数则返回null
	 * @throws UnsupportedEncodingException
	 */
	public String getParameterUrl(String[] arrExcept)
			throws UnsupportedEncodingException {
		String strParamName;
		String[] arrParamValues;
		int i;
		String strParamUrl = null;

		for (Enumeration<?> e = m_req.getParameterNames(); e.hasMoreElements();) {
			strParamName = e.nextElement().toString();

			// {检查参数是否在指明要去除之列
			if (arrExcept != null) {
				for (i = arrExcept.length - 1; i >= 0; i--)
					if (strParamName.equals(arrExcept[i]))
						break;
				if (i >= 0)
					continue;
			}
			// }

			arrParamValues = m_req.getParameterValues(strParamName);
			if (arrParamValues != null)
				for (i = 0; i < arrParamValues.length; i++) {
					if (strParamUrl != null)
						strParamUrl = strParamUrl + "&";
					else
						strParamUrl = "";

					strParamUrl = strParamUrl
							+ strParamName
							+ "="
							+ java.net.URLEncoder.encode(arrParamValues[i],
									"UTF-8");
				}
		}

		return strParamUrl;
	}

	/**
	 * 获取当前请求的URL
	 * 
	 * @param arrExcept
	 *            要排除在返回的URL之外的参数名称，可以为null
	 * @throws UnsupportedEncodingException
	 */
	public String getUrlThis(String[] arrExcept)
			throws UnsupportedEncodingException {
		String strUrlThis = m_req.getRequestURL().toString();
		String strParamUrl = getParameterUrl(arrExcept);

		if (strParamUrl == null)
			return strUrlThis;
		return strUrlThis + "?" + strParamUrl;
	}

	/**
	 * 获取当前请求对应的服务名称和端口，返回String, 格式 :
	 * [http/https]://[ServerName]:[Port]，如果Port为80或者443,则不显示.
	 */
	public String getServerNamePort() {
		StringBuffer sb = new StringBuffer();

		if (m_req.getRequestURL().toString().startsWith("https")) {
			sb.append("https://");
		} else {
			sb.append("http://");
		}

		sb.append(m_req.getServerName());
		if (m_req.getServerPort() != 80 && m_req.getServerPort() != 443) {
			sb.append(":").append(m_req.getServerPort());
		}
		return sb.toString();
	}

	public String getRemoteAddr() {
		return m_req.getRemoteAddr();
	}

	/** 获取客户的Locale */
	public String getLocaleStr() {
		return m_req.getLocale().toString();
	}

	/** 获取客户的Locale */
	public Locale getLocale() {
		return m_req.getLocale();
	}

	/** 获取客户的设备 */
	public String getDevice() {
		return "html";
	}

	/** 返回用户身份 */
	public IUserProfile getUserProfile() {
		return m_userProfile;
	}

	/** 获取请求头 */
	public String getHeader(String name) {
		return m_req.getHeader(name);
	}

	public String getTempDir() {
		if (m_strTempDir == null) {
			m_strTempDir = m_context.getTempDir()
					+ StringUtils.getRandomKey(20);
			FileUtils.createDirectory(m_strTempDir);
		}

		return m_strTempDir;
	}

	/**
	 * 获取数据库连接。每次调用都返回一个新的连接
	 */
	public Connection getDBConn(Class<?> c) throws Exception {
		if (m_vecConns == null)
			m_vecConns = new Vector<Connection>();

		Connection conn = DBConnManager.getManager(c).getConnectionNotStatic();
		m_vecConns.add(conn);
		return conn;
	}

	public Connection getDBConn() throws Exception {
		return getDBConn(m_moduleObject == null ? ClassStack.getCallerClass()
				: m_moduleObject.getClass());
	}

	public Connection getReuseDbConn() throws Exception {
		if (m_reuseConnection == null || m_reuseConnection.isClosed())
			m_reuseConnection = getDBConn(m_moduleObject == null ? ClassStack
					.getCallerClass() : m_moduleObject.getClass());
		return m_reuseConnection;
	}

	public Connection getDBConn(String name) throws Exception {
		if (m_vecConns == null)
			m_vecConns = new Vector<Connection>();

		Connection conn = DBConnManager.getManager(ClassStack.getCallerClass())
				.getConnectionNotStatic(name);
		m_vecConns.add(conn);
		return conn;
	}

	public String getDBConnSyntax() throws Exception {
		return DBConnManager.getManager(ClassStack.getCallerClass())
				.getConnectionSyntaxNotStatic();
	}

	public String getDBConnSyntax(String name) throws Exception {
		return DBConnManager.getManager(ClassStack.getCallerClass())
				.getConnectionSyntaxNotStatic(name);
	}

	/**
	 * 提交所有已分配的数据库连接
	 */
	public void commitDBConn(boolean close) {
		if (m_vecConns == null)
			return;

		for (Enumeration<Connection> enm = m_vecConns.elements(); enm
				.hasMoreElements();) {
			Connection conn = enm.nextElement();

			try {
				if (conn.isClosed())
					continue;
			} catch (Exception e) {
				continue;
			}

			try {
				conn.commit();
			} catch (Exception e) {
			}

			if (close)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}

		if (close)
			m_vecConns.clear();
	}

	/**
	 * 回滚所有已分配的数据库连接
	 */
	public void rollbackDBConn(boolean close) {
		if (m_vecConns == null)
			return;

		for (Enumeration<Connection> enm = m_vecConns.elements(); enm
				.hasMoreElements();) {
			Connection conn = enm.nextElement();

			try {
				if (conn.isClosed())
					continue;
			} catch (Exception e) {
				continue;
			}

			try {
				conn.rollback();
			} catch (Exception e) {
			}

			if (close)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}

		if (close)
			m_vecConns.clear();
	}

	public void cleanUp() {
		if (m_strTempDir != null)
			FileUtils.removeDirectory(new File(m_strTempDir));

		rollbackDBConn(true);
	}

	/** 获取请求对象创建的时间 */
	public long getCreateTime() {
		return m_lCreateTime;
	}
}
