package com.aggrepoint.adk.plugin;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IUserEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;

/**
 * 为Winlet生成简单的用户身份
 * 
 * @author YJM
 */
public class WinletUserEngine implements IUserEngine, IWinletConst {
	public IUserProfile getUserProfile(IModuleRequest req, IModuleResponse resp)
			throws UnsupportedEncodingException {
		return new WinletUserProfile(
				((HttpServletRequest) req.getRequestObject())
						.getHeader(REQUEST_HEADER_USER_PROFILE));
	}
}
