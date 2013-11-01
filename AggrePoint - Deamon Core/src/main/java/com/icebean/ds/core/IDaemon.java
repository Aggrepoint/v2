package com.icebean.ds.core;

import com.icebean.ds.data.DaemonDef;

/**
 * Daemon必须实现的接口
 * 
 * @author YJM
 */
public interface IDaemon extends IThread {
	/**
	 * 设置运行环境
	 */
	public void init(IDaemonContext context, DaemonDef def);
}
