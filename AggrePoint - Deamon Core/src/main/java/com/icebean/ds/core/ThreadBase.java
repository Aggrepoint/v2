package com.icebean.ds.core;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Category;

import com.icebean.core.common.ClassStack;
import com.icebean.core.common.Log4jIniter;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.conn.db.DBConnManager;

/**
 * DaemonBase和DaemonThreadBase的基类 提供了线程启动、停止、心跳的支持 提供数据库连接关闭检查
 * 
 * 可以动态设置运行时间间隔参数和异常堆栈大小
 * 
 * @author YJM
 */
public abstract class ThreadBase extends Thread implements IThread {
	/** 保存在ThreadContext中的数据库连接数组的键值 */
	static final String THREADCONTEXT_KEY = "TC_TB_CONNS";
	/** 保存在ThreadContext中的数据库连接个数的键值 */
	static final String THREADCONTEXT_COUNT_KEY = "TC_TB_CONN_COUNT";
	/** 保存在ThreadContext中的最多的数据库个数 */
	static final int THREADCONTEXT_CONN_MAX_NUM = 20;
	/** 线程索引，每创建一个实例则加一 */
	static int m_iStaticIndex = 0;

	public final int m_iThreadIndex = m_iStaticIndex++;
	/** 是否正在运行 */
	protected boolean m_bRunning;
	/** 是否要停止运行 */
	protected boolean m_bToStop;
	/** 心跳数 */
	protected long m_lHeartBeat;
	/** 运行时间间隔 */
	public long m_lPerformInterval;
	/** 心跳检查时间间隔 */
	public long m_lCheckHeartBeatInterval;
	/** 内存日志最大容量 */
	public int m_iLogSize;
	/** 内存异常日志最大容量 */
	public int m_iExpLogSize;
	/** 内存日志 */
	Vector<LogRecord> m_vecLogs;
	/** 异常日志 */
	Vector<LogRecord> m_vecExpLogs;

	/** 统计数据：运行次数 */
	protected long m_lRunTimes = 0;

	/**
	 * 构造
	 */
	public ThreadBase() {
		m_bRunning = false;
		m_bToStop = false;
		m_lHeartBeat = 0;
		m_vecLogs = new Vector<LogRecord>();
		m_vecExpLogs = new Vector<LogRecord>();

		setName(Integer.toString(m_iThreadIndex));
		setDaemon(true);
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#isRunning()
	 */
	public boolean isRunning() {
		return m_bRunning;
	}

	public boolean isToStop() {
		return m_bToStop;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#getHeartBeat()
	 */
	public long getHeartBeat() {
		return m_lHeartBeat;
	}

	/**
	 * 获取运行时间间隔
	 */
	public long getPerformInterval() {
		return m_lPerformInterval;
	}

	/**
	 * 获取心跳检查时间间隔
	 */
	public long getCheckInterval() {
		return m_lCheckHeartBeatInterval;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#stopRun()
	 */
	public void stopRun() {
		m_bToStop = true;
		try {
			interrupt();
		} catch (Exception e) {
		}
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#startRun()
	 */
	public void startRun() {
		if (!m_bRunning)
			this.start();
	}

	/**
	 * 返回可以动态设置的参数的列表
	 */
	public Vector<String> getParamNames() {
		Vector<String> vec = new Vector<String>();
		vec.add(PARAM_PERFORM_INTERVAL);
		vec.add(PARAM_HEART_BEAT_CHECK_INTERVAL);
		vec.add(PARAM_MEM_LOG_SIZE);
		vec.add(PARAM_EXP_LOG_SIZE);
		return vec;
	}

	/**
	 * 返回可以动态设置的参数的说明
	 */
	public Vector<String> getParamDescs() {
		Vector<String> vec = new Vector<String>();
		vec.add("运行时间间隔");
		vec.add("心跳检查间隔");
		vec.add("内存日志大小");
		vec.add("异常日志大小");
		return vec;
	}

	/**
	 * 返回可以动态设置的参数的说明
	 */
	public Vector<String> getParamValues() {
		Vector<String> vec = new Vector<String>();
		vec.add(Long.toString(m_lPerformInterval));
		vec.add(Long.toString(m_lCheckHeartBeatInterval));
		vec.add(Integer.toString(m_iLogSize));
		vec.add(Integer.toString(m_iExpLogSize));
		return vec;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#setParam(String, String)
	 */
	public boolean setParam(String name, String value) {
		if (name.equals(PARAM_PERFORM_INTERVAL)) {
			try {
				m_lPerformInterval = Long.parseLong(value);
				if (m_lPerformInterval < 0)
					m_lPerformInterval = 0;

				return true;
			} catch (Exception e) {
				return false;
			}
		} else if (name.equals(PARAM_HEART_BEAT_CHECK_INTERVAL)) {
			try {
				m_lCheckHeartBeatInterval = Long.parseLong(value);
				if (m_lCheckHeartBeatInterval < 0)
					m_lCheckHeartBeatInterval = 0;

				return true;
			} catch (Exception e) {
				return false;
			}
		} else if (name.equals(PARAM_MEM_LOG_SIZE)) {
			try {
				synchronized (m_vecLogs) {
					m_iLogSize = Integer.parseInt(value);
					if (m_iLogSize < 0)
						m_iLogSize = 0;

					// 删除多余的日志
					for (int i = m_vecLogs.size() - m_iLogSize; i > 0; i--)
						m_vecLogs.removeElementAt(0);
				}

				return true;
			} catch (Exception e) {
				return false;
			}
		} else if (name.equals(PARAM_EXP_LOG_SIZE)) {
			try {
				synchronized (m_vecExpLogs) {
					m_iExpLogSize = Integer.parseInt(value);
					if (m_iExpLogSize < 0)
						m_iExpLogSize = 0;

					// 删除多余的日志
					for (int i = m_vecExpLogs.size() - m_iExpLogSize; i > 0; i--)
						m_vecExpLogs.removeElementAt(0);
				}

				return true;
			} catch (Exception e) {
				return false;
			}
		}

		return false;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#getParam(String)
	 */
	public String getParam(String name) {
		if (name.equals(PARAM_PERFORM_INTERVAL))
			return Long.toString(m_lPerformInterval);
		if (name.equals(PARAM_HEART_BEAT_CHECK_INTERVAL))
			return Long.toString(m_lCheckHeartBeatInterval);
		if (name.equals(PARAM_MEM_LOG_SIZE))
			return Integer.toString(m_iLogSize);
		if (name.equals(PARAM_EXP_LOG_SIZE))
			return Integer.toString(m_iExpLogSize);

		return "[not defined]";
	}

	/**
	 * 获取Log4j日志Category
	 */
	protected Category getLogger() {
		return Log4jIniter.getCategory(getClass());
	}

	/**
	 * 记录内存日志
	 */
	public void log(LogRecord lr) {
		if (m_iLogSize <= 0)
			return;

		if (lr.m_iType == LogRecord.TYPE_ERROR) {
			synchronized (m_vecExpLogs) {
				for (int i = m_vecExpLogs.size() - m_iExpLogSize; i >= 0; i--)
					m_vecExpLogs.removeElementAt(0);
				m_vecExpLogs.add(lr);
			}
		} else {
			synchronized (m_vecLogs) {
				for (int i = m_vecLogs.size() - m_iLogSize; i >= 0; i--)
					m_vecLogs.removeElementAt(0);
				m_vecLogs.add(lr);
			}
		}
	}

	/**
	 * 记录调试日志
	 */
	public void logDebug(String log) {
		log(new LogRecord(LogRecord.TYPE_DEBUG, log, null));
		getLogger().debug(log);
	}

	/**
	 * 记录信息日志
	 */
	public void logInfo(String log) {
		log(new LogRecord(LogRecord.TYPE_INFO, log, null));
		getLogger().info(log);
	}

	/**
	 * 记录警告日志
	 */
	public void logWarn(String log) {
		log(new LogRecord(LogRecord.TYPE_WARN, log, null));
		getLogger().warn(log);
	}

	/**
	 * 记录异常日志
	 */
	public void logError(String log, Throwable e) {
		log(new LogRecord(LogRecord.TYPE_ERROR, log, e));
		getLogger().error(log, e);
	}

	/**
	 * 记录异常日志
	 */
	public void logError(String log) {
		log(new LogRecord(LogRecord.TYPE_ERROR, log, null));
		getLogger().error(log);
	}

	/**
	 * 记录异常日志
	 */
	public void logError(Throwable e) {
		log(new LogRecord(LogRecord.TYPE_ERROR, null, e));
		getLogger().error("", e);
	}

	/**
	 * 返回内存日志
	 */
	public Enumeration<LogRecord> getLogs() {
		return m_vecLogs.elements();
	}

	/**
	 * 返回内存异常日志
	 */
	public Enumeration<LogRecord> getExpLogs() {
		return m_vecExpLogs.elements();
	}

	/**
	 * 获取数据库连接
	 */
	protected Connection getConnection(String name) throws Exception {
		Class<?> c = ClassStack.getCallerClass();
		Connection conn = DBConnManager.getManager(c).getConnectionNotStatic(
				name);

		// 由于getStatistics()等方法与perform()等方法会在不同线程中被调用，所以
		// 不能只用一个成员数组保存已分配的数据库连接
		try {
			Connection[] conns = (Connection[]) ThreadContext
					.getAttribute(THREADCONTEXT_KEY);
			Integer count = (Integer) ThreadContext
					.getAttribute(THREADCONTEXT_COUNT_KEY);
			if (conns == null) {
				conns = new Connection[THREADCONTEXT_CONN_MAX_NUM];
				ThreadContext.setAttribute(THREADCONTEXT_KEY, conns);
				count = new Integer(0);
				ThreadContext.setAttribute(THREADCONTEXT_COUNT_KEY, count);
			}

			if (count.intValue() < THREADCONTEXT_CONN_MAX_NUM) {
				conns[count.intValue()] = conn;
				ThreadContext.setAttribute(THREADCONTEXT_COUNT_KEY,
						new Integer(count.intValue() + 1));
			}
		} catch (Exception e) {
			logError(e);
		}
		return conn;
	}

	/**
	 * 关闭所有已打开的数据库连接
	 */
	protected void closeConnections() {
		Connection[] conns = (Connection[]) ThreadContext
				.getAttribute(THREADCONTEXT_KEY);

		if (conns != null) {
			Integer count = (Integer) ThreadContext
					.getAttribute(THREADCONTEXT_COUNT_KEY);

			for (int i = count.intValue() - 1; i >= 0; i--) {
				boolean bIsClosed = false;

				try {
					bIsClosed = conns[i].isClosed();
				} catch (Throwable e) {
					logError(e);
				}

				if (!bIsClosed) {
					logError("发现一个未关闭的连接。");

					try {
						conns[i].rollback();
					} catch (Throwable e) {
						logError(e);
					}
					try {
						conns[i].close();
					} catch (Throwable ee) {
						logError(ee);
					}

					conns[i] = null;
				}
			}

			ThreadContext.setAttribute(THREADCONTEXT_COUNT_KEY, new Integer(0));
		}
	}

	/**
	 * 停止运行前的清理工作
	 */
	public void cleanUp() throws Exception {
	}

	/**
	 * 循环运行逻辑
	 */
	public abstract void perform() throws Exception;

	/**
	 * 准备运行状况
	 */
	public Hashtable<String, String> prepareStatistics() throws Exception {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("完成运行次数", Long.toString(m_lRunTimes));
		return ht;
	}

	public void heartBeat() {
		m_lHeartBeat++;
	}

	public void run() {
		logDebug("开始运行");

		m_bRunning = true;

		m_lHeartBeat++;

		// 确保关闭数据库
		closeConnections();

		while (!m_bToStop) {
			try {
				perform();
			} catch (Throwable e) {
				logError(e);
			}

			m_lRunTimes++;
			m_lHeartBeat++;

			// 确保关闭数据库
			closeConnections();

			// 休眠运行间隔
			if (!m_bToStop)
				try {
					sleep(m_lPerformInterval);
				} catch (InterruptedException e) {
				}
		}

		try {
			cleanUp();
		} catch (Exception e) {
			logError(e);
		}

		// 确保关闭数据库
		closeConnections();

		m_bRunning = false;

		logDebug("结束运行");
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#getStatistics()
	 */
	public final Hashtable<String, String> getStatistics() {
		Hashtable<String, String> ht = null;

		try {
			ht = prepareStatistics();
		} catch (Exception e) {
			logError(e);
		}

		closeConnections();
		return ht;
	}
}
