package com.aggrepoint.su.core.data;

import com.icebean.core.adb.*;

/**
 * 与SitePub有关的资源删除日志
 * 
 * @author YJM
 */
public class PubDelLog extends ADB {
	public long m_lDelLogID;
	public long m_lRecordID;
	public int m_iTableID;

	public PubDelLog() throws AdbException {
		m_lDelLogID = 0;
		m_lRecordID = 0;
		m_iTableID = 0;
	}
}
