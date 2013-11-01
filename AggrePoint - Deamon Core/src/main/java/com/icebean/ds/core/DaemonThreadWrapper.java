package com.icebean.ds.core;

/**
 * @author YJM
 */
public class DaemonThreadWrapper implements StatusCode {
	/* 包装的DaemonThread */
	public IDaemonThread m_thread;
	/** 状态 */
	public int m_iStatus;
	/** 创建时间 */
	public java.util.Date m_dtCreate;
	/** 上次心跳 */
	public long m_lLastBeat;
	/** 上次检查心跳的时间 */
	public long m_lLastCheckTime;
	/** 检查HeartBeat的时间间隔，以毫秒为单位 */
	public int m_lCheckHeartBeatInterval;

	public DaemonThreadWrapper(IDaemonThread thread) {
		m_dtCreate = new java.util.Date();

		m_thread = thread;
		m_iStatus = STATUS_NORMAL;
	}
}
