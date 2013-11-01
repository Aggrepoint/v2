package com.aggrepoint.ae.core;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.IAEUrlParamConst;
import com.aggrepoint.adk.IModuleRequest;

public class Tools implements IAEUrlParamConst {
	public static String getRemoteAddr(IModuleRequest req) {
		String str = req.getParameterNotMultipart(PARAM_REMOTE_ADDR);
		if (str == null || str.equals(""))
			return req.getRemoteAddr();
		return str;
	}

	public static String getRemoteAddr(HttpServletRequest req) {
		String str = req.getParameter(PARAM_REMOTE_ADDR);
		if (str == null || str.equals(""))
			return req.getRemoteAddr();
		return str;
	}

	public static String getRequestUri(IModuleRequest req) {
		String str = req.getParameterNotMultipart(PARAM_REQUEST_URI);
		if (str == null || str.equals(""))
			return ((HttpServletRequest) req.getRequestObject())
					.getRequestURI();
		return str;
	}

	public static String getRequestUri(HttpServletRequest req) {
		String str = req.getParameter(PARAM_REQUEST_URI);
		if (str == null || str.equals(""))
			return req.getRequestURI();
		return str;
	}
}
