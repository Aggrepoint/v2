package com.icebean.ds.core;

import java.util.Enumeration;
import java.util.Hashtable;

import com.icebean.ds.data.DaemonDef;

/**
 * @author YJM
 */
public class DaemonWrapper implements StatusCode {
	/** Daemon定义 */
	public DaemonDef m_def;
	/** 包装的Daemon */
	public IDaemon m_daemon;
	/** Daemon生出的Thread，下标为DaemonThread，值为DaemonThreadWrapper */
	public Hashtable<IDaemonThread, DaemonThreadWrapper> m_htThreads;
	/** 根据心跳情况判断的状态 */
	public int m_iStatus;
	/** 创建时间 */
	public java.util.Date m_dtCreate;
	/** 上次心跳 */
	public long m_lLastBeat;
	/** 上次检查心跳的时间 */
	public long m_lLastCheckTime;

	public DaemonWrapper(DaemonDef def, IDaemon daemon) {
		m_dtCreate = new java.util.Date();

		m_def = def;
		m_daemon = daemon;
		m_htThreads = new Hashtable<IDaemonThread, DaemonThreadWrapper>();
		m_iStatus = STATUS_NORMAL;
	}

	public DaemonThreadWrapper findThread(String name) {
		for (Enumeration<DaemonThreadWrapper> enm = m_htThreads.elements(); enm
				.hasMoreElements();) {
			DaemonThreadWrapper dtw = enm.nextElement();
			if (dtw.m_thread.getName().equals(name))
				return dtw;
		}

		return null;
	}
}
