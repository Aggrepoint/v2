package com.icebean.ds.core;


/**
 * Daemon中生出的线程必须实现的接口
 * 
 * @author YJM
 */
public interface IDaemonThread extends IThread {
	public void init(IDaemon parent);
}
