package com.aggrepoint.adk;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import com.icebean.core.common.CombineString;
import com.icebean.core.common.TypeCast;

/**
 * 用于从请求头中中获取窗口配置参数
 * 
 * @author YJM
 */
public class WinletParamParser implements IWinletConst {
	Hashtable<String, String> m_ht;

	/** 为了避免在一次请求过程中多次解析参数，解析过一遍之后便将解析结果保存在请求中 */
	public static final String REQ_CACHE_KEY = WinletParamParser.class.getName();

	public WinletParamParser(IModuleRequest req) {
		m_ht = TypeCast.cast(req.getAttribute(REQ_CACHE_KEY));
		if (m_ht != null)
			return;

		m_ht = new Hashtable<String, String>();

		CombineString cs = new CombineString(req.getHeader(REQUEST_HEADER_WINDOW_PARAMS),
				'~');

		while (cs.hasMoreStr()) {
			String key = cs.nextString();
			if (cs.hasMoreStr())
				m_ht.put(key, cs.nextString());
		}

		req.setAttribute(REQ_CACHE_KEY, m_ht);
	}

	public Set<String> getParamNames() {
		return m_ht.keySet();
	}

	public String getParam(String key) {
		return m_ht.get(key);
	}

	public String getParam(String key, String def) {
		String str = getParam(key);
		return str == null ? def : str;
	}

	public short getParam(String key, short def) {
		try {
			return Short.parseShort(getParam(key));
		} catch (Exception e) {
			return def;
		}
	}

	public int getParam(String key, int def) {
		try {
			return Integer.parseInt(getParam(key));
		} catch (Exception e) {
			return def;
		}
	}

	public long getParam(String key, long def) {
		try {
			return Long.parseLong(getParam(key));
		} catch (Exception e) {
			return def;
		}
	}

	public Enumeration<String> keys() {
		return m_ht.keys();
	}
}
