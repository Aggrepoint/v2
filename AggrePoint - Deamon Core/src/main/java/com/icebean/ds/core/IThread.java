package com.icebean.ds.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Daemon和DaemonThread都应实现的基本线程接口
 * 
 * @author YJM
 */
public interface IThread {
	public static final String PARAM_PERFORM_INTERVAL = "perform_interval";
	public static final String PARAM_MEM_LOG_SIZE = "mem_log_size";
	public static final String PARAM_EXP_LOG_SIZE = "exp_log_size";
	public static final String PARAM_HEART_BEAT_CHECK_INTERVAL = "heart_beat_check_interval";

	/**
	 * 返回名称
	 */
	public String getName();

	/**
	 * 判断线程是否正在运行
	 */
	public boolean isRunning();

	/**
	 * 判断是否已经通知线程停止
	 */
	public boolean isToStop();

	/**
	 * 获取当前HeartBeat
	 */
	public long getHeartBeat();

	/**
	 * 获取运行时间间隔
	 */
	public long getPerformInterval();

	/**
	 * 获取心跳检查时间间隔
	 */
	public long getCheckInterval();

	/**
	 * 停止运行
	 */
	public void stopRun();

	/**
	 * 启动运行
	 */
	public void startRun();

	/**
	 * 返回可以动态设置的参数的列表
	 */
	public Vector<String> getParamNames();

	/**
	 * 返回可以动态设置的参数的说明
	 */
	public Vector<String> getParamDescs();

	/**
	 * 返回可以动态设置的参数的取值
	 */
	public Vector<String> getParamValues();

	/**
	 * 动态设置运行参数
	 */
	public boolean setParam(String name, String value);

	/**
	 * 动态获得运行参数
	 */
	public String getParam(String name);

	/**
	 * 记录日志
	 */
	public void log(LogRecord log);

	/**
	 * 返回内存日志
	 */
	public Enumeration<LogRecord> getLogs();

	/**
	 * 返回内存异常日志
	 */
	public Enumeration<LogRecord> getExpLogs();

	/**
	 * 获取运行状态
	 */
	public Hashtable<String, String> getStatistics();
}
