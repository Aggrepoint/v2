package com.icebean.ds.core;

import java.util.*;

/**
 * Daemon运行的环境
 * 
 * @author YJM
 */
public interface IDaemonContext {
	/**
	 * 获得当前CloneNo
	 */
	public String getServerName();
	
	/**
	 * 添加一个DaemonThread
	 */
	public boolean addThread(
		IDaemon daemon,
		IDaemonThread thread,
		long checkHeartBeatInterval);

	/**
	 * 删除一个DaemonThread
	 */
	public boolean removeThread(IDaemon daemon, IDaemonThread thread);

	/**
	 * 获取DaemonThread个数
	 */
	public int getThreadCount(IDaemon daemon);

	/**
	 * 获取DaemonThread，若没有则返回null
	 */
	public Enumeration<IDaemonThread> getThreads(IDaemon daemon);
}
