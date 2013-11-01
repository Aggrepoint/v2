package com.aggrepoint.adk;

import java.lang.reflect.Method;

import com.aggrepoint.adk.annotation.ExecuteOncePerFrontRequest;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.WinletActionDef;
import com.aggrepoint.adk.data.WinletStateDef;
import com.icebean.core.common.ThreadContext;

/**
 * @author YJM
 */
public class WinletModule implements IModule, IWinletConst {
	boolean m_bIsAction;
	BaseModuleDef m_moduleDef;
	ViewInstance m_viewInstance;
	Method m_method;
	ExecuteOncePerFrontRequest m_execOnce;

	public WinletModule(BaseModuleDef moduleDef, ViewInstance viewInstance,
			Method method, ExecuteOncePerFrontRequest execOnce) {
		m_moduleDef = moduleDef;
		m_viewInstance = viewInstance;
		m_method = method;
		m_execOnce = execOnce;
	}

	public boolean isAction() {
		return m_moduleDef instanceof WinletActionDef;
	}

	public Winlet getWinlet() {
		return m_viewInstance.getWinlet();
	}

	public ViewInstance getViewInstance() {
		return m_viewInstance;
	}

	public Class<?> getImplementationObjectClass() {
		return m_viewInstance.getWinlet().getClass();
	}

	public Object getImplementationObject() {
		return m_viewInstance.getWinlet();
	}

	/**
	 * 初始化
	 */
	public void init(BaseModuleDef def, IServerContext context)
			throws WebBaseException {
	}

	/**
	 * 执行
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ThreadContext.pushAttribute(THREAD_ATTR_VIEW_INSTANCE, m_viewInstance,
				true);
		try {
			Integer ret = null;

			// 如果在一次前端请求中只需执行一次，则尝试获取上次执行后的响应码
			if (m_execOnce != null)
				ret = (Integer) req
						.getFrontRequestAttribute(getExecuteOnceAttributeKey(
								m_execOnce, m_viewInstance.getWinlet(),
								m_method));
			if (ret != null)
				throw new SkipExecuteException(ret.intValue());

			ret = (Integer) m_method.invoke(m_viewInstance.getWinlet(),
					new Object[] { req, resp });

			// 如果在一次前端请求中只需执行一次，则保存执行后的响应码
			if (m_execOnce != null)
				req.setFrontRequestAttribute(
						getExecuteOnceAttributeKey(m_execOnce,
								m_viewInstance.getWinlet(), m_method), ret);
			return ret.intValue();
		} finally {
			ThreadContext.popAttribute(THREAD_ATTR_VIEW_INSTANCE, true);
		}
	}

	public WinletStateDef getState() {
		return m_viewInstance.getViewDef().findStateDef(
				m_viewInstance.getWinlet()
						.getState(m_viewInstance.getViewDef()));
	}

	/**
	 * 设置当前状态
	 */
	public WinletStateDef setState(String state) {
		WinletStateDef stateDef = m_viewInstance.getViewDef().findStateDef(
				state);
		if (stateDef == null)
			return null;
		m_viewInstance.getWinlet().setState(m_viewInstance.getViewDef(), state);
		return stateDef;
	}

	static public String getExecuteOnceAttributeKey(
			ExecuteOncePerFrontRequest executeOnce, Object obj, Method method) {
		if (executeOnce == null)
			return null;
		switch (executeOnce.scope()) {
		case CLASS:
			return "EXECONCE.WINLET." + obj.getClass().toString();
		case INSTANCE:
			return "EXECONCE.WINLET." + obj.toString() + method.toString();
		case METHOD:
			return "EXECONCE.WINLET." + method.toString();
		}
		return null;
	}
}
