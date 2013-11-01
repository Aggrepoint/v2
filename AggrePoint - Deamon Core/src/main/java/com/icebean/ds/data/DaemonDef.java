package com.icebean.ds.data;

import java.util.*;
import com.icebean.core.adb.*;

/**
 * Daemon定义
 * 
 * @author YJM
 */
public class DaemonDef extends ADB {
	/** 编号 */
	public long m_lID;
	/** Daemon名称 */
	public String m_strName;
	/** 类名 */
	public String m_strClassName;
	/** 是否自动启动 */
	public int m_iAutoStart;
	/** 是否可以启动多个实例 */
	public int m_iMultiInstance;
	/** 检查HeartBeat的时间间隔，以毫秒为单位 */
	public long m_lCheckHeartBeatInterval;
	/** 运行时间间隔，以毫秒为单位 */
	public long m_lPerformInterval;
	/** 介绍 */
	public String m_strDescription;
	/** 可以运行的服务器的名称 */
	public String m_strServerName;
	/** 内存日志大小 */
	public int m_iMemLogSize;
	/** 内存异常日志大小 */
	public int m_iExpLogSize;
	/** 附带参数 */
	public Vector<DaemonParamDef> m_vecParams;

	public DaemonDef() throws AdbException {
		m_lID = -1;
		m_strName = "";
		m_strClassName = "";
		m_iAutoStart = 0;
		m_iMultiInstance = 0;
		m_lCheckHeartBeatInterval = 0;
		m_lPerformInterval = 0;
		m_strDescription = "";
		m_strServerName = "";
		m_iMemLogSize = 0;
		m_vecParams = new Vector<DaemonParamDef>();
	}
}
