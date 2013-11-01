package com.aggrepoint.adk;

import java.lang.reflect.Method;

import com.aggrepoint.adk.data.BaseModuleDef;

/**
 * 用来支持实现业务逻辑不在execute()方法中的模块
 * 
 * @author YJM
 */
public class MethodModule implements IModule {
	IModule m_module;
	Method m_method;

	public MethodModule(IModule module, Method method) {
		m_module = module;
		m_method = method;
	}

	public Class<?> getImplementationObjectClass() {
		return m_module.getClass();
	}

	public IModule getImplementationObject() {
		return m_module;
	}

	/**
	 * 初始化
	 */
	public void init(BaseModuleDef def, IServerContext context)
			throws WebBaseException {
		m_module.init(def, context);
	}

	/**
	 * 执行
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		Integer ret = (Integer) m_method.invoke(m_module, new Object[] { req,
				resp });
		return ret.intValue();
	}
}
