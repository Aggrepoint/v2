package com.icebean.ds.core;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.WebBaseException;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.Log4jIniter;
import com.icebean.ds.data.DaemonDef;

/**
 * Daemon运行环境
 * 
 * 2003-5-31
 * 
 * @author JMYANG
 */
public class DaemonServer extends BaseModule implements IDaemonContext,
		StatusCode {
	public static final String HEADER_STATUS = "DSSTATUS";
	static DaemonServer m_this;
	/** 日志 */
	static Category m_log = Log4jIniter.getCategory();
	/** 是否已经被初始化 */
	static boolean m_bInited = false;
	/** 已启动的Daemon */
	public static Hashtable<IDaemon, DaemonWrapper> m_htDaemons;
	/** 服务器名称 */
	public static String m_strServerName;

	/**
	 * 构造函数
	 */
	public DaemonServer() {
		super();
		m_this = this;
	}

	/**
	 * 初始化：启动所有被标记为自动启动的Daemon
	 */
	public void init(BaseModuleDef def, IServerContext server)
			throws WebBaseException {
		if (!m_bInited) {
			m_strServerName = server.getServerName();

			m_htDaemons = new Hashtable<IDaemon, DaemonWrapper>();
			Connection conn = null;

			try {
				conn = getConnection();

				AdbAdapter adapter = new DbAdapter(conn);

				// {加载并启动被标记为自动启动的Daemon
				DaemonDef daemonDef = new DaemonDef();
				daemonDef.m_iAutoStart = 1;
				Vector<DaemonDef> vecDaemons = adapter.retrieveMulti(daemonDef,
						"1", "");
				conn.commit();
				conn.close();
				conn = null;

				for (Enumeration<DaemonDef> enm = vecDaemons.elements(); enm
						.hasMoreElements();) {
					daemonDef = enm.nextElement();
					if (daemonDef.m_strServerName.indexOf(server
							.getServerName()) == -1)
						continue;

					// 创建Deamon实例
					try {
						startDaemon(daemonDef);
					} catch (Exception e) {
						m_log.error(e);
					}
				}
				// }
			} catch (Exception e) {
				m_log.error(e);
			} finally {
				if (conn != null) {
					try {
						conn.rollback();
					} catch (Exception ee) {
					}
					try {
						conn.close();
					} catch (Exception ee) {
					}
				}
			}

			m_bInited = true;
		}
	}

	/**
	 * 
	 */
	public String getServerName() {
		return m_strServerName;
	}

	/**
	 * 添加一个DaemonThread
	 */
	public boolean addThread(IDaemon daemon, IDaemonThread thread,
			long checkHeartBeatInterval) {
		DaemonWrapper wrapper = m_htDaemons.get(daemon);
		if (wrapper == null)
			return false;
		wrapper.m_htThreads.put(thread, new DaemonThreadWrapper(thread));
		thread.startRun();
		return true;
	}

	/**
	 * 删除一个DaemonThread
	 */
	public boolean removeThread(IDaemon daemon, IDaemonThread thread) {
		DaemonWrapper wrapper = m_htDaemons.get(daemon);
		if (wrapper == null)
			return false;
		thread.stopRun();
		return wrapper.m_htThreads.remove(thread) != null;
	}

	/**
	 * 启动一个Daemon
	 */
	public static void startDaemon(DaemonDef daemonDef) throws Exception {
		synchronized (m_htDaemons) {
			IDaemon daemon = (IDaemon) (new DaemonClassLoader(daemonDef
					.getClass().getClassLoader())
					.loadClass(daemonDef.m_strClassName).newInstance());
			daemon.init(m_this, daemonDef);
			DaemonWrapper wrapper = new DaemonWrapper(daemonDef, daemon);
			m_htDaemons.put(daemon, wrapper);
			daemon.startRun();
		}
	}

	/**
	 * 找出已启动的实例
	 */
	public static Vector<DaemonWrapper> getInstances(DaemonDef def) {
		synchronized (m_htDaemons) {
			if (m_htDaemons.size() == 0)
				return null;

			Vector<DaemonWrapper> vecInstances = null;

			for (Enumeration<DaemonWrapper> enm = m_htDaemons.elements(); enm
					.hasMoreElements();) {
				DaemonWrapper wrapper = enm.nextElement();

				if (wrapper.m_def.m_lID == def.m_lID) {
					if (vecInstances == null)
						vecInstances = new Vector<DaemonWrapper>();
					vecInstances.add(wrapper);
				}
			}

			return vecInstances;
		}
	}

	/**
	 * 根据名称查找实例
	 */
	public static DaemonWrapper findInstance(String name) {
		for (Enumeration<DaemonWrapper> enm = m_htDaemons.elements(); enm
				.hasMoreElements();) {
			DaemonWrapper wrapper = enm.nextElement();
			if (wrapper.m_daemon.getName().equals(name))
				return wrapper;
		}

		return null;
	}

	/**
	 * 获取DaemonThread个数
	 */
	public int getThreadCount(IDaemon daemon) {
		DaemonWrapper wrapper = m_htDaemons.get(daemon);
		if (wrapper == null)
			return 0;
		return wrapper.m_htThreads.size();
	}

	/**
	 * 获取DaemonThread，若没有则返回null
	 */
	public Enumeration<IDaemonThread> getThreads(IDaemon daemon) {
		DaemonWrapper wrapper = m_htDaemons.get(daemon);
		if (wrapper == null)
			return null;
		Enumeration<IDaemonThread> enm = wrapper.m_htThreads.keys();
		if (enm.hasMoreElements())
			return enm;
		return null;
	}

	/**
	 * 检查各个Daemon的运行情况，将结果放在响应头中返回给调用者。 Daemon运行情况响应头名称为DSSTATUS
	 * 若所有Daemon运行正常，则DSSTATUS内容为0；否则DSSTATUS以1开头，后面紧跟着用竖线
	 * 分隔的有问题的Daemon的名称，如：1|心跳检查|短信定时发送
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long l = System.currentTimeMillis();
		Vector<String> vecNoHeartBeatDeamons = new Vector<String>();

		synchronized (DaemonServer.m_htDaemons) {
			for (Enumeration<DaemonWrapper> enm = DaemonServer.m_htDaemons
					.elements(); enm.hasMoreElements();) {
				DaemonWrapper wrapper = enm.nextElement();

				// 检查Daemon心跳
				if (wrapper.m_daemon.getCheckInterval() != 0) {
					if (l - wrapper.m_lLastCheckTime > wrapper.m_daemon
							.getCheckInterval()) {
						// 需要进行检查
						if (wrapper.m_daemon.getHeartBeat() == wrapper.m_lLastBeat) {
							wrapper.m_iStatus = STATUS_NO_HEART_BEAT;
							vecNoHeartBeatDeamons.add(wrapper.m_def.m_strName);
						} else
							wrapper.m_iStatus = STATUS_NORMAL;

						wrapper.m_lLastBeat = wrapper.m_daemon.getHeartBeat();
						wrapper.m_lLastCheckTime = l;
					} else if (wrapper.m_iStatus != STATUS_NORMAL)
						vecNoHeartBeatDeamons.add(wrapper.m_def.m_strName);
				}
			}
		}

		// {构造响应结果
		if (vecNoHeartBeatDeamons.size() == 0) { // 所有Daemon运行正常
			((HttpServletResponse) resp.getResponseObject()).setHeader(
					HEADER_STATUS, "0");
		} else {
			StringBuffer sb = new StringBuffer();
			for (Enumeration<String> enm = vecNoHeartBeatDeamons.elements(); enm
					.hasMoreElements();) {
				sb.append("|");
				sb.append((String) enm.nextElement());
			}

			((HttpServletResponse) resp.getResponseObject()).setHeader(
					HEADER_STATUS, "1"
							+ new String(sb.toString().getBytes("GBK"),
									"ISO8859-1"));
		}
		// }

		return 1000;
	}
}
