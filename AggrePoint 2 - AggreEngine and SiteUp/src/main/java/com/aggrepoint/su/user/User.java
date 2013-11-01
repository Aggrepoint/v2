package com.aggrepoint.su.user;

import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 用户
 * 
 * @author YJM
 */
public class User extends ADB {
	/** 用户名称 */
	public String m_strID;
	/** 口令 */
	public String m_strPassword;
	/** 属性 */
	public Vector<UserProperty> m_vecProperties;

	public User() throws AdbException {
		m_strID = m_strPassword = "";
		m_vecProperties = new Vector<UserProperty>();
	}
	
	public String getId() {
		return m_strID;
	}
	
	public String getPassword() {
		return m_strPassword;
	}
}
