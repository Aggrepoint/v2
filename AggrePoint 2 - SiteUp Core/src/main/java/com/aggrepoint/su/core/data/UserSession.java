package com.aggrepoint.su.core.data;

import java.sql.Timestamp;

import com.icebean.core.adb.ADB;

/**
 * 用户会话
 * 
 * @author YJM
 */
public class UserSession extends ADB {
	public String sessionID;
	public String ip;
	public String profile;
	public Timestamp validTime;
}
