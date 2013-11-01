package com.aggrepoint.su.core.data;

import java.sql.Timestamp;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApPubFlag extends ADB {
	public static final int TYPE_TEMPLATE = 1;
	public static final int TYPE_RES = 1000;
	public static final int TYPE_IMAGE = 2000;
	public static final int TYPE_CHANNEL = 3000;

	public int m_iDocTypeID;
	public long m_lDocID;
	public String m_strServerName;
	public Timestamp m_tDocTime;
	public int m_iToRevoke;

	public ApPubFlag() throws AdbException {
		m_iDocTypeID = 0;
		m_lDocID = 0;
		m_strServerName = "";
		m_tDocTime = null;
		m_iToRevoke = 0;
	}
}