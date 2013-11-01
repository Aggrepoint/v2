package com.aggrepoint.adk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * 通过属性访问的对象
 * 
 * @author YJM
 */
public class PropertyObject {
	public Hashtable<String, String> m_htProperties;

	public PropertyObject() {
		m_htProperties = new Hashtable<String, String>();
	}

	public PropertyObject(IPropertyObject ref) {
		m_htProperties = new Hashtable<String, String>();

		if (ref == null)
			return;

		String name;
		String value;
		for (Enumeration<String> enm = ref.getPublicPropertyNames(); enm
				.hasMoreElements();) {
			name = enm.nextElement();
			if (name == null)
				continue;

			value = (String) ref.getProperty(name);
			if (value != null)
				m_htProperties.put(name, value);
		}
	}

	public PropertyObject(String prop) throws UnsupportedEncodingException {
		m_htProperties = new Hashtable<String, String>();

		if (prop == null)
			return;

		StringTokenizer st = new StringTokenizer(prop, ";");
		String str;
		int i;
		while (st.hasMoreElements()) {
			str = st.nextToken();
			i = str.indexOf(":");
			if (i > 0)
				m_htProperties.put(
						URLDecoder.decode(str.substring(0, i), "UTF-8"),
						URLDecoder.decode(str.substring(i + 1), "UTF-8"));
		}
	}

	public Enumeration<String> getPublicPropertyNames() {
		return m_htProperties.keys();
	}

	public String getProperty(String property) {
		if (property == null)
			return null;

		return m_htProperties.get(property);
	}

	public String getProperty(String property, String def) {
		if (property == null)
			return def;

		String str = m_htProperties.get(property);
		if (str == null)
			return def;
		return str;
	}

	public int getProperty(String paramName, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public long getProperty(String paramName, long defaultValue) {
		try {
			return Long.parseLong(getProperty(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getProperty(String paramName, float defaultValue) {
		try {
			return Float.parseFloat(getProperty(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getProperty(String paramName, double defaultValue) {
		try {
			return Double.parseDouble(getProperty(paramName));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean hasProperty(String property) {
		if (property == null)
			return false;

		return m_htProperties.containsKey(property);
	}

	public void setProperty(String name, String property) {
		if (property == null)
			m_htProperties.remove(name);
		else
			m_htProperties.put(name, property);
	}

	public String serialize() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();

		String name;
		for (Enumeration<String> enm = getPublicPropertyNames(); enm
				.hasMoreElements();) {
			name = enm.nextElement();
			sb.append(URLEncoder.encode(name, "UTF-8"));
			sb.append(":");
			sb.append(URLEncoder.encode((String) getProperty(name), "UTF-8"));
			sb.append(";");
		}

		return sb.toString();
	}

	public boolean equals(PropertyObject po) {
		if (m_htProperties.size() != po.m_htProperties.size())
			return false;

		for (String key : m_htProperties.keySet())
			if (!m_htProperties.get(key).equals(po.m_htProperties.get(key)))
				return false;

		return true;
	}
}
