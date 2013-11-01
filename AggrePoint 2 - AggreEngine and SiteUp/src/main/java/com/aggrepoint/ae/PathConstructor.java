package com.aggrepoint.ae;

import com.aggrepoint.adk.IModuleRequest;

/**
 * 负责提供各种资源的文件系统路径（如模板、图片等）
 * 
 * @author YJM
 */
public class PathConstructor {
	public static String m_strRoot;

	public static final String getBPageTmplDir(IModuleRequest req) {
		if (m_strRoot == null)
			m_strRoot = req.getContext().getRootDir();
		return m_strRoot + "WEB-INF/ae/tmpls/bpage/";
	}

	public static final String getCPageTmplDir(IModuleRequest req) {
		if (m_strRoot == null)
			m_strRoot = req.getContext().getRootDir();
		return m_strRoot + "WEB-INF/ae/tmpls/cpage/";
	}

	public static final String getListTmplDir(IModuleRequest req) {
		if (m_strRoot == null)
			m_strRoot = req.getContext().getRootDir();
		return m_strRoot + "WEB-INF/ae/tmpls/list/";
	}

	public static final String getResDir(IModuleRequest req) {
		if (m_strRoot == null)
			m_strRoot = req.getContext().getRootDir();
		return m_strRoot + "WEB-INF/res/";
	}

	public static final String getImageDir(IModuleRequest req) {
		if (m_strRoot == null)
			m_strRoot = req.getContext().getRootDir();
		return m_strRoot + "WEB-INF/image/";
	}
}
