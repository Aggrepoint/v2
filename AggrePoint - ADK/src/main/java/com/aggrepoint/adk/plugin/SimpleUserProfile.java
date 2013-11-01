package com.aggrepoint.adk.plugin;

import java.util.Enumeration;
import java.util.Vector;

import com.aggrepoint.adk.IUserProfile;

/**
 * 最简单的用户身份 - 匿名用户
 * 
 * @author YJM
 */
public class SimpleUserProfile implements IUserProfile {
	public Enumeration<String> getPublicPropertyNames() {
		Vector<String> vec = new Vector<String>();
		vec.add(PROPERTY_ISANONYMOUS);
		return vec.elements();
	}

	/** 获取用户身份属性 */
	@Override
	public String getProperty(String property) {
		if (property.equalsIgnoreCase(PROPERTY_ISANONYMOUS))
			return "T";

		return "";
	}

	@Override
	public boolean hasProperty(String property) {
		if (property.equalsIgnoreCase(PROPERTY_ISANONYMOUS))
			return true;
		return false;
	}

	/** 判断是否匿名用户 */
	@Override
	public boolean isAnonymous() {
		return true;
	}
}
