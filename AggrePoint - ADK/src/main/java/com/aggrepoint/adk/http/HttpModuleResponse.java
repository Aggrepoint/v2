package com.aggrepoint.adk.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.IModuleResponse;

/**
 * @author YJM
 */
public class HttpModuleResponse implements IModuleResponse {
	HttpServletResponse m_resp;
	int m_code;
	String m_url;
	String m_strUpdate;
	String m_strLogMessage;
	String m_strUserMessage;
	String m_strTitle;
	Throwable m_e;
	boolean m_bCommitted;

	public HttpModuleResponse(HttpServletResponse resp) {
		m_resp = resp;
		m_url = m_strLogMessage = m_strUserMessage = null;
		m_e = null;
	}

	public HttpModuleResponse(int code, String url) {
		m_code = code;
		m_url = url;
		m_strLogMessage = m_strUserMessage = null;
	}

	public HttpModuleResponse(int code, String url, String logMessage,
			String userMessage) {
		m_code = code;
		m_url = url;
		m_strLogMessage = logMessage;
		m_strUserMessage = userMessage;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleResponse#getResponseObject()
	 */
	public Object getResponseObject() {
		return m_resp;
	}

	/**
	 * @see com.aggrepoint.adk.IModuleResponse#setRetCode(int)
	 */
	public void setRetCode(int code) {
		m_code = code;
	}

	public void setRetUrl(String url) {
		m_url = url;
	}

	public void setUpdate(String update) {
		m_strUpdate = update;
	}

	public void setLogMessage(String msg) {
		m_strLogMessage = msg;
	}

	public void setUserMessage(String msg) {
		m_strUserMessage = msg;
	}

	public void setTitle(String title) {
		m_strTitle = title;
	}

	public void setRetAttribute(String url, String logMessage,
			String userMessage) {
		m_url = url;
		m_strLogMessage = logMessage;
		m_strUserMessage = userMessage;
	}

	public void setRetThrow(Throwable t) {
		m_e = t;
	}

	/** 设置响应头 */
	public void setHeader(String name, String value) {
		m_resp.setHeader(name, value);
	}

	/** 设置内容类型 */
	public void setContentType(String type) {
		m_resp.setContentType(type);
	}

	public int getRetCode() {
		return m_code;
	}

	public String getRetUrl() {
		return m_url;
	}

	public String getUpdate() {
		return m_strUpdate;
	}

	public String getLogMessage() {
		return m_strLogMessage;
	}

	public String getUserMessage() {
		return m_strUserMessage;
	}

	public String getTitle() {
		return m_strTitle;
	}

	public Throwable getRetThrow() {
		return m_e;
	}

	public void setCommitted() {
		m_bCommitted = true;
	}

	public boolean isCommitted() {
		return m_bCommitted;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return m_resp.getWriter();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return m_resp.getOutputStream();
	}
}
