package com.aggrepoint.adk.data;

import java.lang.reflect.Method;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.annotation.ExecuteOncePerFrontRequest;
import com.icebean.core.adb.AdbException;

public abstract class WinletMethodDef extends BaseModuleDef implements
		IAdkConst {
	/** 方法 */
	public Method m_method;
	public WinletViewDef m_view;

	public WinletMethodDef() throws AdbException {
		m_strMethod = "";
		m_method = null;
	}

	public Method getMethod() throws Exception {
		WinletDef def = (WinletDef) ((WinletViewDef) m_parent).m_parent;

		// 创建WinletModule实例
		if (m_method == null)
			m_method = def.getModuleClass(false).getMethod(
					m_strMethod == null || m_strMethod.equals("") ? m_strPath
							: m_strMethod, MODULE_METHOD_PARAM_TYPE);
		return m_method;
	}

	public ExecuteOncePerFrontRequest executeOncePerFrontRequest()
			throws Exception {
		return getMethod().getAnnotation(ExecuteOncePerFrontRequest.class);
	}
}
