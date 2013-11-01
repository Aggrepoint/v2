package com.aggrepoint.su.user;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 用户的属性
 * 
 * @author YJM
 */
public class UserProperty extends ADB {
	/** 属性名称 */
	public String m_strName;
	/** 属性值 */
	public String m_strValue;

	public UserProperty() throws AdbException {
		m_strName = m_strValue = "";
	}
}
