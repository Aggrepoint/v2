package com.icebean.ds.core;

import org.apache.log4j.Category;

import com.icebean.core.common.Log4jIniter;

/**
 * @author YJM
 */
public abstract class DaemonThreadBase
	extends ThreadBase
	implements IDaemonThread {
	public IDaemon m_parent = null;

	/**
	 * 获取Log4j日志Category
	 */
	protected Category getLogger() {
		if (m_parent == null)
			return Log4jIniter.getCategory(getClass());
		else
			return Log4jIniter.getCategory(m_parent.getClass());
	}

	/**
	 * 记录内存日志
	 */
	public void log(LogRecord lr) {
		if (m_parent == null)
			super.log(lr);
		else
			m_parent.log(lr);
	}

	public void init(IDaemon parent) {
		m_parent = parent;
	}
}
