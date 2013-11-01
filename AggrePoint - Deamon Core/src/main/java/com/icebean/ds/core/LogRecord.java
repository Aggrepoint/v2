package com.icebean.ds.core;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Daemon内存日志记录
 * 
 * @author YJM
 */
public class LogRecord {
	public static final int TYPE_DEBUG = 0;
	public static final int TYPE_INFO = 1;
	public static final int TYPE_WARN = 2;
	public static final int TYPE_ERROR = 3;

	static final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

	/** 类型 */
	public int m_iType;
	/** 时间 */
	public Date m_dt;
	/** 日志 */
	public String m_strLog;
	/** 异常 */
	public Throwable m_e;
	/** 线程 */
	public String m_strThread;

	public LogRecord(int type, String log, Throwable e) {
		m_iType = type;
		m_dt = new Date();
		m_strLog = log;
		if (m_strLog == null)
			m_strLog = "";
		m_e = e;
		m_strThread = Thread.currentThread().getName();
	}

	public String stackTrace() {
		if (m_e == null)
			return "";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		m_e.printStackTrace(ps);
		ps.flush();
		return baos.toString();
	}
	
	public static String format(Date dt) {
		return m_sdf.format(dt);
	}
}
