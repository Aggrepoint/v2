package com.aggrepoint.adk;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.aggrepoint.adk.data.DLCDef;

/**
 * 类加载：可以重新加载指定的类
 * 
 * @author YJM
 */
public class DynamicClassLoader extends ClassLoader {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();
	/** 信息 */
	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	static String[] m_arrDynamicLoadClasses = null;
	static byte[] TEMP_BA = new byte[10240];

	/**
	 * 初始化
	 */
	public static void init(Vector<DLCDef> dlcs) {
		DLCDef def;

		m_arrDynamicLoadClasses = new String[dlcs.size()];

		int i = 0;
		for (Enumeration<DLCDef> enm = dlcs.elements(); enm.hasMoreElements(); i++) {
			def = enm.nextElement();
			m_arrDynamicLoadClasses[i] = def.m_strClass;
		}
	}

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	static String getClassPathName(String className) {
		StringTokenizer st = new StringTokenizer(className, ".");
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append(st.nextToken());

		while (st.hasMoreTokens()) {
			sb.append("/");
			sb.append(st.nextToken());
		}

		sb.append(".class");
		return sb.toString();
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		int i;

		for (i = m_arrDynamicLoadClasses.length - 1; i >= 0; i--)
			if (name.startsWith(m_arrDynamicLoadClasses[i]))
				break;
		if (i < 0)
			return super.loadClass(name);

		byte[] b = loadClassData(getClass(), name);
		if (b == null)
			throw new ClassNotFoundException();

		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(Class<?> caller, String name) {
		try {
			InputStream is = caller.getResourceAsStream(getClassPathName(name));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			synchronized (TEMP_BA) {
				int c = is.read(TEMP_BA);
				while (c > 0) {
					baos.write(TEMP_BA, 0, c);
					c = is.read(TEMP_BA);
				}
			}
			is.close();
			return baos.toByteArray();
		} catch (Exception e) {
			m_log.error(m_msg.constructMessage("loadError", name), e);
			return null;
		}
	}
}
