package com.icebean.ds.core;

import java.util.Enumeration;
import java.util.Vector;

import com.icebean.ds.data.DaemonDef;
import com.icebean.ds.data.DaemonParamDef;

/**
 * @author YJM
 */
public abstract class DaemonBase extends ThreadBase implements IDaemon {
	/** 环境 */
	protected IDaemonContext m_context;
	/** 定义 */
	protected DaemonDef m_def;

	/**
	 * 构造
	 */
	public DaemonBase() {
		m_context = null;
		m_def = null;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#setContext(DaemonContext)
	 */
	public void init(IDaemonContext context, DaemonDef def) {
		m_context = context;
		m_def = def;
		m_lPerformInterval = m_def.m_lPerformInterval;
		m_iLogSize = m_def.m_iMemLogSize;
		m_iExpLogSize = m_def.m_iExpLogSize;
		m_lCheckHeartBeatInterval = m_def.m_lCheckHeartBeatInterval;

		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				setParam(paramDef.m_strName, paramDef.m_strValue);
			}
	}

	/**
	 * 返回可以动态设置的参数的名称
	 */
	public Vector<String> getParamNames() {
		Vector<String> vec = super.getParamNames();

		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				if (paramDef.m_iCanChangeDyna != 0)
					vec.add(paramDef.m_strName);
			}

		return vec;
	}

	/**
	 * 返回可以动态设置的参数的说明
	 */
	public Vector<String> getParamDescs() {
		Vector<String> vec = super.getParamDescs();

		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				if (paramDef.m_iCanChangeDyna != 0)
					vec.add(paramDef.m_strDescription);
			}

		return vec;
	}

	/**
	 * 返回可以动态设置的参数的值
	 */
	public Vector<String> getParamValues() {
		Vector<String> vec = super.getParamValues();

		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				if (paramDef.m_iCanChangeDyna != 0)
					vec.add(getParam(paramDef.m_strName));
			}

		return vec;
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#setParam(String, String)
	 */
	public boolean setParam(String name, String value) {
		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				if (paramDef.m_iCanChangeDyna != 0
						&& paramDef.m_strName.equals(name)) {
					paramDef.m_strValue = value;
					return true;
				}
			}

		return super.setParam(name, value);
	}

	/**
	 * @see com.bmcc.daemon.core.Daemon#getParam(String)
	 */
	public String getParam(String name) {
		if (m_def.m_vecParams != null)
			for (Enumeration<DaemonParamDef> enm = m_def.m_vecParams.elements(); enm
					.hasMoreElements();) {
				DaemonParamDef paramDef = enm.nextElement();
				if (paramDef.m_iCanChangeDyna != 0
						&& paramDef.m_strName.equals(name))
					return paramDef.m_strValue;
			}

		return super.getParam(name);
	}

	/**
	 * 停止所有子线程
	 */
	public void cleanUp() throws Exception {
		IDaemonThread dt;

		for (Enumeration<IDaemonThread> enm = m_context.getThreads(this); enm != null
				&& enm.hasMoreElements();) {
			dt = enm.nextElement();

			if (dt.isRunning() && !dt.isToStop())
				dt.stopRun();
		}
	}
}
