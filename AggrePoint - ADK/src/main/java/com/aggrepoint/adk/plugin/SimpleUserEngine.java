package com.aggrepoint.adk.plugin;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IUserEngine;
import com.aggrepoint.adk.IUserProfile;

/**
 * 生成简单的用户身份 - 匿名用户
 * 
 * @author YJM
 */
public class SimpleUserEngine implements IUserEngine {
	static IUserProfile m_defaultProfile = new SimpleUserProfile();

	public IUserProfile getUserProfile(IModuleRequest req, IModuleResponse resp) {
		return m_defaultProfile;
	}
}
