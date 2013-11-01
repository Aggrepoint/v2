package com.aggrepoint.adk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Module执行结果
 * 
 * @author YJM
 */
public interface IModuleResponse {
	/** 获取被封装的响应对象 */
	public Object getResponseObject();

	/** 设置结果响应码 */
	public void setRetCode(int code);

	public void setRetAttribute(String url, String logMessage,
			String userMessage);

	public void setRetUrl(String url);

	public void setUpdate(String update);

	public void setLogMessage(String msg);

	public void setUserMessage(String msg);

	public void setTitle(String title);

	public void setRetThrow(Throwable t);

	/** 设置响应头 */
	public void setHeader(String name, String value);

	/** 设置内容类型 */
	public void setContentType(String type);

	/** 获取结果响应码 */
	public int getRetCode();

	public String getRetUrl();

	public String getUpdate();

	public String getLogMessage();

	public String getUserMessage();

	public String getTitle();

	public Throwable getRetThrow();

	public void setCommitted();

	public boolean isCommitted();

	public PrintWriter getWriter() throws IOException;

	public OutputStream getOutputStream() throws IOException;
}
