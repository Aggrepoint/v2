package com.aggrepoint.adk.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

import com.aggrepoint.adk.EnumEvent;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.annotation.ExecuteOncePerFrontRequest;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;

/**
 * 
 * @author YJM
 */
public class EventHandler extends ADB {
	public String m_strFor;
	public String m_strMethod;

	EnumEvent m_event;
	ArrayList<Integer> m_arrIntCodes;
	ArrayList<String> m_arrExpCodes;
	Hashtable<Class<?>, Method> m_htMethods = new Hashtable<Class<?>, Method>();

	Method getMethod(Class<?> forClass) throws NoSuchMethodException {
		Method method = m_htMethods.get(forClass);
		if (method != null)
			return method;

		if (m_event.isBefore())
			method = forClass.getMethod(m_strMethod, IModuleRequest.class,
					IModuleResponse.class);
		else
			method = forClass.getMethod(m_strMethod, IModuleRequest.class,
					IModuleResponse.class, RetCode.class);
		m_htMethods.put(forClass, method);
		return method;
	}

	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) {
		m_event = EnumEvent.find(m_strFor);
		if (m_event.getInMode() != 0) {
			m_arrIntCodes = new ArrayList<Integer>();
			m_arrExpCodes = new ArrayList<String>();

			String[] retCodes = m_event.getParams(m_strFor);
			for (String retCode : retCodes) {
				try {
					m_arrIntCodes.add(Integer.parseInt(retCode));
				} catch (Exception e) {
					m_arrExpCodes.add(retCode);
				}
			}
		}
	}

	public void execute(Object obj, EnumEvent[] events, IModuleRequest req,
			IModuleResponse resp, RetCode retCode) throws Exception {
		for (EnumEvent event : events)
			if (event == m_event) {
				boolean bMatched = true;

				if (event.getInMode() == -1) { // Not In
					if (retCode.m_strThrow == null) {
						for (Integer i : m_arrIntCodes) {
							if (i.intValue() == retCode.m_iID) {
								bMatched = false;
								break;
							}
						}
					} else {
						for (String str : m_arrExpCodes) {
							if (str.equals(retCode.m_strThrow)) {
								bMatched = false;
								break;
							}
						}
					}
				} else if (event.getInMode() == -1) { // In
					bMatched = false;

					if (retCode.m_strThrow == null) {
						for (Integer i : m_arrIntCodes) {
							if (i.intValue() == retCode.m_iID) {
								bMatched = true;
								break;
							}
						}
					} else {
						for (String str : m_arrExpCodes) {
							if (str.equals(retCode.m_strThrow)) {
								bMatched = true;
								break;
							}
						}
					}
				}

				if (bMatched) { // Execute
					Method method = getMethod(obj.getClass());
					ExecuteOncePerFrontRequest executeOnce = method
							.getAnnotation(ExecuteOncePerFrontRequest.class);

					if (executeOnce == null
							|| req
									.getFrontRequestAttribute(getExecuteOnceAttributeKey(
											executeOnce, obj, method)) == null) {
						if (m_event.isBefore())
							method.invoke(obj, req, resp);
						else
							method.invoke(obj, req, resp, retCode);

						if (executeOnce != null)
							req.setFrontRequestAttribute(
									getExecuteOnceAttributeKey(executeOnce,
											obj, method), "EXECUTED");
					}
				}
				break;
			}
	}

	public void print(String tab, java.io.PrintWriter pw, String tag) {
		pw.print(tab);
		pw.print("<");
		pw.print(tag);
		pw.print(" for=\"");
		pw.print(m_strFor);
		pw.print("\" method=\"");
		pw.print(m_strMethod);
		pw.println("\"/>");
	}

	static public String getExecuteOnceAttributeKey(
			ExecuteOncePerFrontRequest executeOnce, Object obj, Method method) {
		if (executeOnce == null)
			return null;
		switch (executeOnce.scope()) {
		case CLASS:
			return "EXECONCE.EVENTHANDLER." + obj.getClass().toString();
		case INSTANCE:
			return "EXECONCE.EVENTHANDLER." + obj.toString()
					+ method.toString();
		case METHOD:
			return "EXECONCE.EVENTHANDLER." + method.toString();
		}
		return null;
	}
}
