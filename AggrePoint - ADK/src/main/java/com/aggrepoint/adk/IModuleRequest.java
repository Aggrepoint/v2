package com.aggrepoint.adk;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Locale;

import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.form.Form;

/**
 * Module执行请求对象
 * 
 * @author YJM
 */
public interface IModuleRequest {
	public void bind(IModule module, BaseModuleDef def,
			IUserProfile userProfile, IPsnEngine psnEngine, IModuleResponse resp);

	/** 获取请求序列号 */
	public long getRequestID();

	/** 获取被封装的请求对象 */
	public Object getRequestObject();

	/** 获取服务器环境 */
	public IServerContext getContext();

	/** 获取模块定义 */
	public BaseModuleDef getDef();

	/** 获取ControlServlet被映射到的路径，以/开头，不以/结尾 */
	public String getControlPath();

	/** 在Pass过程中，可能会将控制传向另外一个映射路径，这时可以调用这个方法来更改ControlPath */
	public void setControlPath(String path);

	/** 获取请求路径，不包括ContextRoot和ControlServlet被映射到的路径，以/开头。不会返回null，若为空则返回"" */
	public String getRequestPath();

	public String getRequestURI();

	/** 请求是否以Multipart方式提交 */
	public boolean isMultipart();

	/** 请求是否对字段值进行校验 */
	public boolean isValidateField();

	/** 获取输入表单 */
	public Form getForm();

	/** 清除当前表单 */
	public void resetForm();

	/** 清除当前视图内的表单 */
	public void resetForm(Winlet winlet, String name);

	/** 清除指定视图的表单 */
	public void resetForm(Winlet winlet, String view, String name);

	/** 获取请求参数。使用不带NotMultipart的方法获取参数时，若参数是以multipart方式提交的，会首先分解multipart参数 */
	public String getParameter(String name);

	public String getParameter(String name, String def);

	public int getParameter(String name, int def);

	public long getParameter(String name, long def);

	public float getParameter(String name, float def);

	public double getParameter(String name, double def);

	public String[] getParameterValues(String paraName);

	public String[] getParameterNames(String[] excludes);

	public FileParameter getFileParameter(String paraName);

	public FileParameter[] getFileParameters(String paraName);

	public String[] getParameterValuesNotMultipart(String name);

	public String getParameterNotMultipart(String name);

	public String getParameterNotMultipart(String name, String def);

	public int getParameterNotMultipart(String name, int def);

	public long getParameterNotMultipart(String name, long def);

	public float getParameterNotMultipart(String name, float def);

	public double getParameterNotMultipart(String name, double def);

	public String[] getParameterNamesNotMultipart(String[] excludes);

	public Hashtable<String, Object> getParameters();

	/** 请求属性 */
	public void setAttribute(String key, Object value);

	public Object getAttribute(String key);

	public String getAttribute(String key, String def);

	public int getAttribute(String key, int def);

	public long getAttribute(String key, long def);

	public float getAttribute(String key, float def);

	public double getAttribute(String key, double def);

	public void removeAttribute(String key);

	/** 获取个性化引擎 */
	IPsnEngine getPsnEngine();

	/** 获取个性化配置参数 */
	public String getParam(String name);

	public String getParam(String name, String def);

	public int getParam(String name, int def);

	public long getParam(String name, long def);

	public float getParam(String name, float def);

	public double getParam(String name, double def);

	/** 获取个性化文本信息 */
	public String getMessage(String name, String[] inPlaceString);

	public String getMessage(String name, String str);

	public String getMessage(String name, String str1, String str2);

	public String getMessage(String name, String str1, String str2, String str3);

	public String getMessage(String name, String str1, String str2,
			String str3, String str4);

	public String getMessage(String name, String str1, String str2,
			String str3, String str4, String str5);

	/** 获取当前请求对应的窗口实例的ID */
	public String getWinIID();

	public String getViewID();

	public String getPageID();

	public String getActionID();

	public void setActionID(String action);

	/** 获取位于同一页的、指定类型的Winlet */
	public Winlet getWinlet(String className) throws Exception;

	/** 获取指定类型的Winlet */
	public Winlet getWinletInSite(String className);

	/** Session中保存的值 */
	public Object getSessionAttribute(String key);

	public String getSessionAttribute(String key, String def);

	public int getSessionAttribute(String key, int def);

	public long getSessionAttribute(String key, long def);

	public float getSessionAttribute(String key, float def);

	public double getSessionAttribute(String key, double def);

	public void setSessionAttribute(String key, Object value);

	public void removeSessionAttribute(String key);

	public void invalidSession();

	public Object setFrontRequestAttribute(String key, Object val);

	public Object getFrontRequestAttribute(String key);

	/** 获取客户地址 */
	public String getRemoteAddr();

	/** 获取客户的Locale */
	public String getLocaleStr();

	/** 获取客户的Locale */
	public Locale getLocale();

	/** 获取客户的设备 */
	public String getDevice();

	/** 获取用户身份 */
	public IUserProfile getUserProfile();

	/** 获取请求头 */
	public String getHeader(String name);

	/** 获取临时工作目录，不以路径分隔符结束 */
	public String getTempDir();

	/** 释放资源 */
	public void cleanUp();

	/** 获取数据库连接。每次调用都返回一个新的连接 */
	public Connection getDBConn() throws Exception;

	/** 获取数据库连接。每次调用都返回一个新的连接 */
	public Connection getDBConn(Class<?> c) throws Exception;

	/** 获取数据库连接，每次调用都返回同一个连接 */
	public Connection getReuseDbConn() throws Exception;

	public Connection getDBConn(String name) throws Exception;

	public String getDBConnSyntax() throws Exception;

	public String getDBConnSyntax(String name) throws Exception;

	/** 提交所有已分配的数据库连接 */
	public void commitDBConn(boolean close);

	/** 回滚所有已分配的数据库连接 */
	public void rollbackDBConn(boolean close);

	/** 获取请求对象创建的时间 */
	public long getCreateTime();
}
