package com.aggrepoint.adk.ui.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Vector;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.ClassStack;
import com.icebean.core.common.Log4jIniter;

public class Validator extends Matchable implements IAdkConst {
	static final Class<?> METHOD_PARAM = (new Vector<String>()).getClass();

	Property m_property;
	public String m_strId;
	public String m_strMethod;
	public String m_strSkip;
	public Vector<ValidatorArg> m_vecArgs;
	public Vector<ValidatorMsg> m_vecMsgs;
	Method m_method;
	boolean m_bOnWrapper;
	boolean m_bNeedAdapter;

	public Validator() {
		m_vecArgs = new Vector<ValidatorArg>();
		m_vecMsgs = new Vector<ValidatorMsg>();
	}

	public ValidateResult validateWithMethod(IModuleRequest req, Object obj,
			String markup, HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		if (m_strMethod == null || m_strMethod.equals(""))
			return null;

		if (m_method == null) {
			try {
				if (m_property.m_wrapper != null) {
					for (Method method : m_property.m_wrapper.getMethods()) {
						if (!method.getName().equals(m_strMethod))
							continue;

						Class<?>[] params = method.getParameterTypes();
						if (params.length == 2
								&& params[0].isAssignableFrom(obj.getClass())
								&& params[1] == METHOD_PARAM) {
							if (!Modifier.isStatic(method.getModifiers())) {
								Log4jIniter
										.getCategory(obj.getClass(),
												ClassStack.getCurrClass())
										.error("Method \""
												+ m_strMethod
												+ "\" is not static in wrapper \""
												+ m_property.m_wrapper
														.getCanonicalName()
												+ "\".");
								return ValidateResult.FAILED;
							}
							m_method = method;
							m_bOnWrapper = true;
							m_bNeedAdapter = false;
							break;
						}

						if (params.length == 3
								&& params[0].isAssignableFrom(obj.getClass())
								&& params[1] == AdbAdapter.class
								&& params[2] == METHOD_PARAM) {
							if (!Modifier.isStatic(method.getModifiers())) {
								Log4jIniter
										.getCategory(obj.getClass(),
												ClassStack.getCurrClass())
										.error("Method \""
												+ m_strMethod
												+ "\" is not static in wrapper \""
												+ m_property.m_wrapper
														.getCanonicalName()
												+ "\".");
								return ValidateResult.FAILED;
							}
							m_method = method;
							m_bOnWrapper = true;
							m_bNeedAdapter = true;
							break;
						}
					}
				}

				if (m_method == null) {
					// validate method not on wrapper, try the object itself
					m_bOnWrapper = false;
					try {
						m_method = obj.getClass().getMethod(m_strMethod,
								METHOD_PARAM);
						m_bNeedAdapter = false;
					} catch (NoSuchMethodException e) {
						m_method = obj.getClass().getMethod(m_strMethod,
								AdbAdapter.class, METHOD_PARAM);
						m_bNeedAdapter = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ValidateResult.FAILED;
			}
		}

		try {
			if (m_bOnWrapper)
				if (m_bNeedAdapter)
					return (ValidateResult) m_method.invoke(null, obj,
							new DbAdapter(req.getDBConn()),
							getArgs(markup, flags, flags1, flags2, flags3));
				else
					return (ValidateResult) m_method.invoke(null, obj,
							getArgs(markup, flags, flags1, flags2, flags3));
			else if (m_bNeedAdapter)
				return (ValidateResult) m_method.invoke(obj,
						new DbAdapter(req.getDBConn()),
						getArgs(markup, flags, flags1, flags2, flags3));
			else
				return (ValidateResult) m_method.invoke(obj,
						getArgs(markup, flags, flags1, flags2, flags3));
		} catch (Exception e) {
			e.printStackTrace();
			return ValidateResult.FAILED;
		}
	}

	public Vector<String> getArgs(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		Vector<String> vec = new Vector<String>();
		for (ValidatorArg arg : Matchable.matchAll(m_vecArgs, markup, flags,
				flags1, flags2, flags3))
			vec.add(arg.m_strArg);
		return vec;
	}

	public String getMsg(String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		ValidatorMsg msg = Matchable.match(m_vecMsgs, markup, flags, flags1,
				flags2, flags3);
		if (msg == null)
			return null;
		return msg.m_strMsg;
	}
}
