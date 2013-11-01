package com.aggrepoint.adk.ui.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.taglib.html.ContTmpl;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.beanutil.BeanPropertyException;
import com.icebean.core.common.ClassStack;
import com.icebean.core.common.Log4jIniter;
import com.icebean.core.common.StringUtils;

public class UIConfig extends ADB {
	private Class<?> m_class;
	public Vector<UIWrapper> m_vecUIWrappers;
	public Vector<Property> m_vecProperties;
	public Vector<Group> m_vecGroups;
	public Vector<Template> m_vecTemplates;
	public Hashtable<String, Group> m_htGroups;
	public Hashtable<String, ContTmpl> m_htTemplates;
	public Hashtable<String, Class<?>> m_htUIWrappers;

	public UIConfig(Class<?> forClass) {
		m_class = forClass;
		m_vecUIWrappers = new Vector<UIWrapper>();
		m_vecProperties = new Vector<Property>();
		m_vecGroups = new Vector<Group>();
		m_vecTemplates = new Vector<Template>();
		m_htGroups = new Hashtable<String, Group>();
		m_htTemplates = new Hashtable<String, ContTmpl>();
		m_htUIWrappers = new Hashtable<String, Class<?>>();
	}

	private Property findProperty(String name) {
		for (Property prop : m_vecProperties)
			if (prop.m_strName.equals(name))
				return prop;
		return null;
	}

	static int matchMethod(Method method, String name, Class<?>[][] params) {
		if (!method.getName().equals(name))
			return -1;
		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < params.length; i++) {
			if (types.length != params[i].length)
				continue;
			int j = 0;
			for (; j < types.length; j++)
				if (params[i][j] != null
						&& !types[j].isAssignableFrom(params[i][j]))
					break;
			if (j == types.length)
				return i;
		}

		return -1;
	}

	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) throws BeanPropertyException {
		for (UIWrapper wrapper : m_vecUIWrappers) {
			if (wrapper.m_strClass == null || wrapper.m_strClass.equals(""))
				wrapper.m_strClass = m_class.getName() + "UI";
			else if (wrapper.m_strClass.indexOf(".") == -1) {
				int posi = m_class.getName().lastIndexOf(".");
				if (posi >= 0)
					wrapper.m_strClass = m_class.getName().substring(0,
							posi + 1)
							+ wrapper.m_strClass;
			}

			try {
				m_htUIWrappers.put(wrapper.m_strName,
						Class.forName(wrapper.m_strClass));
			} catch (Exception e) {
				Log4jIniter.getCategory(m_class, ClassStack.getCurrClass())
						.error("Error initializing UI wrapper class \""
								+ wrapper.m_strClass + "\".", e);
			}
		}

		for (Property prop : m_vecProperties) {
			if (prop.m_strWrapper != null && !prop.m_strWrapper.equals("")) {
				prop.m_wrapper = m_htUIWrappers.get(prop.m_strWrapper);
				if (prop.m_wrapper == null) {
					Log4jIniter.getCategory(m_class, ClassStack.getCurrClass())
							.error("Property reference undefined wrapper \""
									+ prop.m_strWrapper + "\".");
				} else {
					String propName = prop.m_strWrapName == null
							|| prop.m_strWrapName.equals("") ? prop.m_strName
							: prop.m_strWrapName;
					propName = StringUtils.capitalize(propName);
					Method[] methods = prop.m_wrapper.getMethods();

					// wrapper getDisplay
					String name = "getDisplay" + propName;
					Class<?>[][] params = new Class<?>[][] {
							new Class<?>[] { m_class },
							new Class<?>[] { m_class, AdbAdapter.class } };
					for (Method method : methods) {
						int type = matchMethod(method, name, params);
						if (type < 0)
							continue;

						if (!Modifier.isStatic(method.getModifiers())) {
							Log4jIniter.getCategory(m_class,
									ClassStack.getCurrClass()).error(
									"Method \"" + name
											+ "\" is not static in wrapper \""
											+ prop.m_wrapper.getCanonicalName()
											+ "\".");
						} else {
							prop.m_wrapperGetDisplay = method;
							prop.m_bGetDisplayNeedAdapter = type == 1;
						}
						break;
					}

					// wrapper get
					name = "get" + propName;
					for (Method method : methods) {
						int type = matchMethod(method, name, params);
						if (type < 0)
							continue;

						if (!Modifier.isStatic(method.getModifiers())) {
							Log4jIniter.getCategory(m_class,
									ClassStack.getCurrClass()).error(
									"Method \"" + name
											+ "\" is not static in wrapper \""
											+ prop.m_wrapper.getCanonicalName()
											+ "\".");
						} else {
							prop.m_wrapperGet = method;
							prop.m_bGetNeedAdapter = type == 1;
							prop.m_getType = prop.m_wrapperGet.getReturnType();
							prop.m_iGetType = BeanProperty
									.getTypeCode(prop.m_getType);
						}
						break;
					}

					params = new Class<?>[][] {
							new Class<?>[] { m_class, null },
							new Class<?>[] { m_class, null, AdbAdapter.class } };

					name = "set" + propName;
					// wrapper set
					for (Method method : methods) {
						int type = matchMethod(method, name, params);
						if (type < 0)
							continue;

						if (!Modifier.isStatic(method.getModifiers())) {
							Log4jIniter.getCategory(m_class,
									ClassStack.getCurrClass()).error(
									"Method \"" + name
											+ "\" is not static in wrapper \""
											+ prop.m_wrapper.getCanonicalName()
											+ "\".");
						} else {
							prop.m_wrapperSet = method;
							prop.m_bSetNeedAdapter = type == 1;
							prop.m_setType = method.getParameterTypes()[1];
							prop.m_iSetType = BeanProperty
									.getTypeCode(prop.m_setType);
						}
						break;
					}

					if (prop.m_wrapperGet != null && prop.m_wrapperSet != null
							&& prop.m_setType != prop.m_getType) {
						Log4jIniter.getCategory(m_class,
								ClassStack.getCurrClass()).error(
								"The get method and set method have different type for property \""
										+ propName + "\" in wrapper \""
										+ prop.m_wrapper.getCanonicalName()
										+ "\".");
					}

					if (prop.m_wrapperGet == null && prop.m_wrapperSet == null) {
						try {
							if (prop.m_strGet != null
									&& !prop.m_strGet.equals("")
									&& prop.m_strSet != null
									&& !prop.m_strSet.equals(""))
								prop.m_property = new BeanProperty(m_class,
										null, prop.m_strGet, prop.m_strSet);
							else
								prop.m_property = new BeanProperty(m_class,
										prop.m_strName, true, true);
						} catch (Exception e) {
						}

						if (prop.m_property == null
								&& prop.m_wrapperGetDisplay == null
								&& prop.m_wrapperGet == null) {
							Log4jIniter.getCategory(m_class,
									ClassStack.getCurrClass()).error(
									"No getDisplay or get method defined for property \""
											+ propName + "\" in wrapper \""
											+ prop.m_wrapper.getCanonicalName()
											+ "\".");
						}
					}

					// wrapper getList
					name = "get" + propName + "List";
					params = new Class<?>[][] {
							new Class<?>[] { m_class },
							new Class<?>[] { m_class, boolean.class },
							new Class<?>[] { m_class, AdbAdapter.class,
									boolean.class } };

					for (Method method : methods) {
						int type = matchMethod(method, name, params);
						if (type == -1)
							continue;

						if (!Modifier.isStatic(method.getModifiers())) {
							Log4jIniter.getCategory(m_class,
									ClassStack.getCurrClass()).error(
									"Method \"" + name
											+ "\" is not static in wrapper \""
											+ prop.m_wrapper.getCanonicalName()
											+ "\".");
						} else {
							prop.m_wrapperGetList = method;
							prop.m_bGetListNeedAdapter = type == 2;
						}
						break;
					}
				}
			} else {
				if (prop.m_strGet != null && !prop.m_strGet.equals("")
						&& prop.m_strSet != null && !prop.m_strSet.equals(""))
					prop.m_property = new BeanProperty(m_class, null,
							prop.m_strGet, prop.m_strSet);
				else
					prop.m_property = new BeanProperty(m_class, prop.m_strName,
							true, true);
			}
		}

		for (Group group : m_vecGroups) {
			for (PropertyRef ref : group.m_vecPropertyRefs) {
				ref.setRef(findProperty(ref.m_strName));
				if (ref.getRef() == null) {
					Log4jIniter
							.getCategory(m_class, ClassStack.getCurrClass())
							.error("Undefined property \""
									+ ref.m_strName
									+ "\" referenced in IO configuration for class \""
									+ m_class.getName() + "\".");
				}
			}

			m_htGroups.put(group.m_strId, group);
		}

		for (Template tmpl : m_vecTemplates)
			m_htTemplates.put(tmpl.m_strId, new ContTmpl(tmpl.m_strTemplate));
	}

	public Group getGroup(String id) {
		if (id == null)
			return null;
		return m_htGroups.get(id);
	}

	public ContTmpl getTemplate(String id) {
		if (id == null)
			return null;
		return m_htTemplates.get(id);
	}

	public Vector<? extends Property> getProperties(ADB adb, String markup,
			HashSet<String> flags, String groupId) {
		if (groupId == null || groupId.equals(""))
			return Matchable.matchAll(m_vecProperties, markup, flags, null,
					adb.getFlags(), null);

		Group group = getGroup(groupId);
		if (group != null)
			return Matchable.matchAll(group.m_vecPropertyRefs, markup, flags,
					group.getFlags(), adb.getFlags(), null);
		return null;
	}
}
