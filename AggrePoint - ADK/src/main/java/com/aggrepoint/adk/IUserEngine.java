package com.aggrepoint.adk;

import java.io.UnsupportedEncodingException;

/**
 * 用户身份识别 负责从请求中识别出访问用户的身份及相关的属性
 * 
 * @author YJM
 */
public interface IUserEngine {
	public IUserProfile getUserProfile(IModuleRequest req, IModuleResponse resp)
			throws UnsupportedEncodingException;
}
