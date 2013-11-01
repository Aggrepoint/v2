package com.icebean.ds.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Daemon类加载 实现不需重新启动JVM加载新的Daemon类
 * 
 * @author YJM
 */
public class DaemonClassLoader extends ClassLoader {
	ClassLoader m_loader;

	public DaemonClassLoader(ClassLoader loader) {
		m_loader = loader;
	}

	static String getClassPathName(String className) {
		StringTokenizer st = new StringTokenizer(className, ".");
		StringBuffer sb = new StringBuffer();
		sb.append(st.nextToken());

		while (st.hasMoreTokens()) {
			sb.append("/");
			sb.append(st.nextToken());
		}

		sb.append(".class");
		return sb.toString();
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!name.startsWith("com.icebean"))
			return m_loader.loadClass(name);
		if (name.startsWith("com.icebean.ds.core"))
			return m_loader.loadClass(name);

		byte[] b = loadClassData(name);
		if (b == null)
			throw new ClassNotFoundException();

		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name) {
		try {
			byte[] temp = new byte[1024];

			InputStream is = m_loader
					.getResourceAsStream(getClassPathName(name));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int c = is.read(temp);
			while (c > 0) {
				baos.write(temp, 0, c);
				c = is.read(temp);
			}
			is.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public URL getResource(String name) {
		return m_loader.getResource(name);
	}
}
