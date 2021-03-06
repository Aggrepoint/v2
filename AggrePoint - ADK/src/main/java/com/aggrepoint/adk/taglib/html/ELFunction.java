package com.aggrepoint.adk.taglib.html;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletRequest;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.LangTextProcessor;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.WinletModule;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.http.AdkResolver;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.aggrepoint.adk.ui.PropertyInstance;
import com.aggrepoint.adk.ui.UIAdapter;
import com.icebean.core.adb.ADB;
import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.xml.MatchElement;

public class ELFunction implements IAdkConst, IWinletConst {
	static Hashtable<String, SimpleDateFormat> m_htSDFs = new Hashtable<String, SimpleDateFormat>();
	static Hashtable<String, DecimalFormat> m_htDFs = new Hashtable<String, DecimalFormat>();
	static Hashtable<String, BeanProperty> m_htProperties = new Hashtable<String, BeanProperty>();

	static String strBOM;
	static {
		try {
			strBOM = new String(new byte[] { (byte) 0xef, (byte) 0xbb,
					(byte) 0xbf }, "UTF-8");
		} catch (Exception e) {
		}
	}

	public static String bom() {
		return strBOM;
	}

	public static String htmlEncode(String str) {
		try {
			return StringUtils.toHTML(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static String textAreaEncode(String str) {
		try {
			return StringUtils.toHTML(str, true);
		} catch (Exception e) {
			return str;
		}
	}

	public static String wmlEncode(String str) {
		try {
			return StringUtils.toWML(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}

	public static String jsonEncode(String str) {
		try {
			return StringUtils.toJson(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static Object funcReqAttr(String name) {
		HttpModuleRequest req = (HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		return req.getAttribute(name);
	}

	public static Object funcFrontAttr(String name) {
		HttpModuleRequest req = (HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		return req.getFrontRequestAttribute(name);
	}

	public static String funcMarkup() {
		HttpModuleRequest req = (HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		return MarkupTag.getMarkupName((ServletRequest) req.getRequestObject());
	}

	public static String funcEncodeNameSpace(String text) throws IOException {
		if (text == null)
			return null;

		IModuleRequest req = (IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);
		if (req != null)
			text = text + req.getWinIID();
		if (view != null)
			text = text + view.getId();
		return text;
	}

	public static String funcText(String text) throws IOException {
		return LangTextProcessor.parse(text);
	}

	public static String funcIf(Boolean b, String s) {
		if (b)
			return s;
		return "";
	}

	public static String funcIfElse(Boolean b, String s, String e) {
		if (b)
			return s;
		return e;
	}

	public static String funcDateFormat(String fmt, Date date) {
		if (fmt == null || date == null)
			return "";

		Locale locale = ((HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST)).getLocale();

		SimpleDateFormat sdf = m_htSDFs.get(fmt + "_" + locale == null ? ""
				: locale.toString());
		if (sdf == null) {
			sdf = new SimpleDateFormat(fmt, locale);
			m_htSDFs.put(fmt, sdf);
		}

		return sdf.format(date);
	}

	public static String funcDecimalFormat(String fmt, java.lang.Double val) {
		if (fmt == null)
			return "";

		DecimalFormat df = m_htDFs.get(fmt);
		if (df == null) {
			df = new DecimalFormat(fmt);
			m_htDFs.put(fmt, df);
		}

		return df.format(new BigDecimal(val.doubleValue()).setScale(5,
				RoundingMode.HALF_EVEN));
	}

	public static String funcPercent(Double val, Double compTo) {
		if (compTo == 0.0d)
			return "";

		return funcDecimalFormat("#,###,###,###,##0.00", val / compTo * 100.0);
	}

	public static String funcChangePercent(Double newval, Double oldval) {
		if (oldval == 0.0d)
			return "";

		double percent = new BigDecimal((newval - oldval) / Math.abs(oldval)
				* 100.0).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
		if (percent > 0.0d)
			return "+" + funcDecimalFormat("#,###,###,###,##0.00", percent);
		else
			return funcDecimalFormat("#,###,###,###,##0.00", percent);
	}

	public static Object funcGet(Object o, String prop) throws Exception {
		String key = o.getClass().getName() + "_" + prop;
		BeanProperty bp = m_htProperties.get(key);
		if (bp == null) {
			bp = new BeanProperty(o.getClass(), prop, true, false, null);
			m_htProperties.put(key, bp);
		}
		return bp.get(o);
	}

	public static Object funcSet(Object o, String prop, Object val)
			throws Exception {
		String key = o.getClass().getName() + "_" + prop;
		BeanProperty bp = m_htProperties.get(key);
		if (bp == null) {
			bp = new BeanProperty(o.getClass(), prop, true, false, null);
			m_htProperties.put(key, bp);
		}
		bp.set(o, val);
		return o;
	}

	public static Object funcListGet(List<?> o, Integer i) {
		return o.get(i);
	}

	private static Method findMethod(Class<?> cls, String name,
			Class<?>... params) {
		for (Method method : cls.getMethods()) {
			if (!method.getName().equals(name))
				continue;
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != params.length)
				continue;
			int i = 0;
			for (Class<?> c : paramTypes) {
				if (!c.equals(params[i]) && !c.isAssignableFrom(params[i]))
					break;
				i++;
			}
			if (i >= params.length)
				return method;
		}
		return null;
	}

	public static Object funcExec(Object o, String method) throws Exception {
		return o.getClass().getMethod(method).invoke(o);
	}

	public static Object funcExec1(Object o, String method, Object param)
			throws Exception {
		try {
			return findMethod(o.getClass(), method, param.getClass()).invoke(o,
					param);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Object funcExec2(Object o, String method, Object param1,
			Object param2) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass()).invoke(o, param1, param2);
	}

	public static Object funcExec3(Object o, String method, Object param1,
			Object param2, Object param3) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass()).invoke(o, param1, param2,
				param3);
	}

	public static Object funcExec4(Object o, String method, Object param1,
			Object param2, Object param3, Object param4) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass(), param4.getClass())
				.invoke(o, param1, param2, param3, param4);
	}

	public static Object funcExec5(Object o, String method, Object param1,
			Object param2, Object param3, Object param4, Object param5)
			throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass(), param4.getClass(),
				param5.getClass()).invoke(o, param1, param2, param3, param4,
				param5);
	}

	public static String funcResurl(String param) {
		if (WinletReqInfo.isInWinlet())
			return ResourceUrlTag.getUrl(WinletReqInfo
					.getInfo((HttpModuleRequest) ThreadContext
							.getAttribute(THREAD_ATTR_REQUEST)), param, null,
					null, true);
		else {
			HttpModuleRequest req = (HttpModuleRequest) ThreadContext
					.getAttribute(THREAD_ATTR_REQUEST);

			return new StringBuffer().append(req.getServerNamePort())
					.append(req.getContext().getResourceRootPath())
					.append(param).toString();
		}
	}

	/**
	 * Get a single content template by name
	 * 
	 * @param adbFilter
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public static ContTmpl uitmpl(ADB adbFilter, String template)
			throws Exception {
		return new UIAdapter(
				(HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)).getTemplate(
				adbFilter, template);
	}

	/**
	 * Execute content templates by name in an array
	 * 
	 * @param adbFilter
	 * @param templates
	 * @param fullProps
	 * @param props
	 * @return
	 * @throws Exception
	 */
	public static String tmplexec(ADB adbFilter, String[] templates,
			Vector<PropertyInstance> fullProps, Vector<PropertyInstance> props)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		UIAdapter adapter = new UIAdapter(
				(HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST));
		for (String tmpl : templates)
			sb.append(adapter.getTemplate(adbFilter, tmpl).execute(fullProps,
					props));
		return sb.toString();
	}

	public static Collection<PropertyInstance> uiprops(ADB adbFilter,
			ADB adbData, String group) throws Exception {
		if (adbData == null)
			adbData = adbFilter;

		return new UIAdapter(
				(HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)).getProperties(
				adbFilter, adbData, group);
	}

	public static Collection<PropertyInstance> propsbind(
			Collection<PropertyInstance> props, ADB adbData) {
		for (PropertyInstance prop : props)
			prop.bindData(adbData);
		return props;
	}

	public static Collection<PropertyInstance> propsclearcached(
			Collection<PropertyInstance> props) {
		for (PropertyInstance prop : props)
			prop.clearCached();
		return props;
	}

	public static PropertyInstance uiprop(Collection<PropertyInstance> props,
			String name) throws Exception {
		for (PropertyInstance prop : props)
			if (prop.getName().equals(name))
				return prop;
		return null;
	}

	public static String uiattr(PropertyInstance prop, String type, String def) {
		String val = prop.getDisplayAttribute(type);
		if (val == null || val.equals(""))
			return def;
		return val;
	}

	public static String uiattach(PropertyInstance prop, String key, Object val) {
		if (key != null && val != null)
			prop.attach(key, val);
		return "";
	}

	public static Object uiget(PropertyInstance prop, String key) {
		return prop.getAttached(key);
	}

	public static String cfgval(MatchElement o, String attr) {
		if (o == null)
			return null;

		// {return attribute value if attribute exists
		String val = o.getAttribute(attr);
		if (val != null)
			return val;
		// }

		// Search for child elements
		HttpModuleRequest req = (HttpModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		Vector<String> lss = LocaleManager.getLSIDs(ELFunction.class, null);
		String markup = MarkupTag.getMarkupName((ServletRequest) req
				.getRequestObject());

		for (MatchElement sub : o.getSubs()) {
			if (!sub.getName().equalsIgnoreCase(attr))
				continue;
			String str = sub.getAttribute("lsid");
			if (!(str == null || str.equals("") || lss.contains(str)))
				continue;

			str = sub.getAttribute("markup");
			if (!(markup == null || str == null || markup.equals("")
					|| str.equals("") || markup.equalsIgnoreCase(str)))
				continue;

			return sub.getContent();
		}
		return null;
	}

	public static Object adbData(ADB adb, String key) {
		return adb.getCommonData(key);
	}

	public static Object m(String property) throws Exception {
		Object val = ThreadContext.getAttribute(THREAD_ATTR_MODULE);
		if (val instanceof WinletModule)
			return AdkResolver.getObjectValue(((WinletModule) val).getWinlet(),
					property);

		return null;
	}

	// ///////////////////////////////////////////////////////
	//
	// 以下部分是Winlet专用
	//
	// ///////////////////////////////////////////////////////

	/**
	 * 生成函数调用名称<br>
	 * 不支持函数定义，函数定义必须使用TagLib
	 * 
	 * @param name
	 * @return
	 */
	public static String func(String name) {
		IModuleRequest req = (IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		WinletReqInfo reqInfo = WinletReqInfo.getInfo(req);

		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

		StringBuffer sb = new StringBuffer();
		if (reqInfo.m_bUseAjax)
			sb.append("document.");
		sb.append(name);
		sb.append(req.getWinIID());
		if (view != null)
			sb.append(view.getId());

		return sb.toString();
	}

	public static String resurl(String param, boolean isStatic) {
		return ResourceUrlTag.getUrl(WinletReqInfo
				.getInfo((HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)), param, null, null,
				isStatic);
	}

	public static String resurl(String param) {
		return resurl(param, true);
	}

	public static String actionurl(String param) {
		return ActionUrlTag.getUrl(WinletReqInfo
				.getInfo((HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)), param);
	}

	public static String resproxy(String res) throws Exception {
		return ResProxyTag.getUrl(WinletReqInfo
				.getInfo((HttpModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST)), res);
	}
}
