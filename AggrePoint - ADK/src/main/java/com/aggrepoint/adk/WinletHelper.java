package com.aggrepoint.adk;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.data.WinletDef;
import com.aggrepoint.adk.form.FormImpl;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.common.TypeCast;
import com.icebean.core.res.ResourceManager;

/**
 * 所有顶级Winlet实例保存在用户会话中的Hashtable中，会话关键字为WINLET_SESSION_KEY。
 * 该Hashtable中不包含由Include View产生的子Winlet。<br>
 * <br>
 * 所有WinInstance保存在另外一个Hashtable中，会话关键字为WININSTS_SESSION_KEY。<br>
 * <br>
 * 表单对象保存在另外一组Hashtable中,会话关键字为FORM_BY_WINLET_SESSION_KEY,
 * FORM_BY_ID_SESSION_KEY<br>
 * 
 * @author Jim
 * 
 */
public class WinletHelper implements IWinletConst {
	public static final String WINLET_SESSION_KEY = "com.aggrepoint.adk.winlet";
	public static final String WININSTS_SESSION_KEY = "com.aggrepoint.adk.winsts";
	public static final String FORM_BY_WINLET_SESSION_KEY = "com.aggrepoint.adk.formbywinlet";
	public static final String FORM_BY_ID_SESSION_KEY = "com.aggrepoint.adk.formbyid";

	static long FORM_ID = 0;

	static Hashtable<String, Winlet> getWinletHashtable(IModuleRequest req) {
		synchronized (((HttpServletRequest) req.getRequestObject())
				.getSession()) {
			Hashtable<String, Winlet> ht = TypeCast.cast(req
					.getSessionAttribute(WINLET_SESSION_KEY));
			if (ht == null) {
				ht = new Hashtable<String, Winlet>();
				req.setSessionAttribute(WINLET_SESSION_KEY, ht);
			}

			return ht;
		}
	}

	static Hashtable<String, Vector<WinInstance>> getWinInstances(
			IModuleRequest req) {
		synchronized (((HttpServletRequest) req.getRequestObject())
				.getSession()) {
			Hashtable<String, Vector<WinInstance>> ht = TypeCast.cast(req
					.getSessionAttribute(WININSTS_SESSION_KEY));
			if (ht == null) {
				ht = new Hashtable<String, Vector<WinInstance>>();
				req.setSessionAttribute(WININSTS_SESSION_KEY, ht);
			}
			return ht;
		}
	}

	static Hashtable<Winlet, Hashtable<String, Hashtable<String, FormImpl>>> getFormsByWinlet(
			IModuleRequest req) {
		synchronized (((HttpServletRequest) req.getRequestObject())
				.getSession()) {
			Hashtable<Winlet, Hashtable<String, Hashtable<String, FormImpl>>> ht = TypeCast
					.cast(req.getSessionAttribute(FORM_BY_WINLET_SESSION_KEY));
			if (ht == null) {
				ht = new Hashtable<Winlet, Hashtable<String, Hashtable<String, FormImpl>>>();
				req.setSessionAttribute(FORM_BY_WINLET_SESSION_KEY, ht);
			}

			return ht;
		}
	}

	static Hashtable<String, FormImpl> getFormsById(IModuleRequest req) {
		synchronized (((HttpServletRequest) req.getRequestObject())
				.getSession()) {
			Hashtable<String, FormImpl> ht = TypeCast.cast(req
					.getSessionAttribute(FORM_BY_ID_SESSION_KEY));
			if (ht == null) {
				ht = new Hashtable<String, FormImpl>();
				req.setSessionAttribute(FORM_BY_ID_SESSION_KEY, ht);
			}

			return ht;
		}
	}

	static Hashtable<String, FormImpl> getForms(IModuleRequest req,
			Winlet winlet, String viewPath, String formName) {
		Hashtable<Winlet, Hashtable<String, Hashtable<String, FormImpl>>> ht = getFormsByWinlet(req);

		Hashtable<String, Hashtable<String, FormImpl>> ht2 = null;
		synchronized (ht) {
			ht2 = ht.get(winlet);
			if (ht2 == null) {
				ht2 = new Hashtable<String, Hashtable<String, FormImpl>>();
				ht.put(winlet, ht2);
			}
		}

		Hashtable<String, FormImpl> ht3 = null;
		String key = viewPath + "!" + formName;
		synchronized (ht2) {
			ht3 = ht2.get(key);
			if (ht3 == null) {
				ht3 = new Hashtable<String, FormImpl>();
				ht2.put(key, ht3);
			}
		}

		return ht3;
	}

	/**
	 * 用于在Form标记中获取或生成表单对象
	 * 
	 * @param req
	 * @param winlet
	 * @param viewPath
	 * @param uri
	 * @return
	 */
	public static FormImpl getForm(IModuleRequest req, Winlet winlet,
			String viewPath, String uri, String formName) {
		Hashtable<String, FormImpl> ht = getForms(req, winlet, viewPath,
				formName);

		FormImpl f = ht.get(uri);
		if (f == null) {
			String id = Long.toString(++FORM_ID);
			f = new FormImpl(id, winlet);
			ht.put(uri, f);

			getFormsById(req).put(id, f);
		}
		return f;
	}

	/**
	 * 根据ID获取表单对象
	 * 
	 * @param req
	 * @param id
	 * @return
	 */
	public static FormImpl getForm(IModuleRequest req, String id) {
		if (id == null)
			return null;

		return getFormsById(req).get(id);
	}

	public static void resetForms(IModuleRequest req, Winlet winlet,
			String viewPath, String formName) {
		Hashtable<String, FormImpl> ht = getForms(req, winlet, viewPath,
				formName);
		for (FormImpl f : ht.values())
			f.reset();
	}

	public static void resetForms(IModuleRequest req, Winlet winlet) {
		Hashtable<String, Hashtable<String, FormImpl>> ht = getFormsByWinlet(
				req).get(winlet);

		if (ht != null)
			for (Hashtable<String, FormImpl> ht1 : ht.values())
				for (FormImpl form : ht1.values())
					form.reset();
	}

	static WinletDef getWinletDef(Class<?> ref, String clzName)
			throws Exception {
		WinletDef def;
		InputStream wis = ResourceManager.getResourceManager(ref,
				"/CFG-INF/amcfg_load.xml").loadResource(Class.forName(clzName));
		AdbAdapter wadapter = new XmlAdapter(
				javax.xml.parsers.DocumentBuilderFactory.newInstance()
						.newDocumentBuilder()
						.parse(new org.xml.sax.InputSource(wis)));

		def = new WinletDef();
		wadapter.retrieve(def, "loadSeperate");
		def.m_strModuleClass = clzName;
		return def;
	}

	static Hashtable<String, WinletDef> m_htWinletDef = new Hashtable<String, WinletDef>();

	static synchronized WinletDef getWinletDefWithCache(Class<?> ref,
			String clzName) throws Exception {
		WinletDef def = m_htWinletDef.get(clzName);
		if (def == null) {
			def = getWinletDef(ref, clzName);
			m_htWinletDef.put(clzName, def);
		}
		return def;
	}

	static Winlet getWinlet(IModuleRequest req, WinletDef winletDef) {
		Hashtable<String, Winlet> ht = getWinletHashtable(req);

		switch (winletDef.m_scope) {
		case PAGE:
			return ht.get(winletDef.m_strModuleClass + "/P/" + req.getPageID());
		case SESSION:
			return ht.get(winletDef.m_strModuleClass + "/S");
		case INSTANCE:
		default:
			return ht.get(winletDef.m_strFullPath + req.getWinIID());
		}
	}

	static void putWinlet(IModuleRequest req, WinletDef winletDef, Winlet winlet) {
		Hashtable<String, Winlet> ht = getWinletHashtable(req);
		switch (winletDef.m_scope) {
		case PAGE:
			ht.put(winletDef.m_strModuleClass + "/P/" + req.getPageID(), winlet);
			break;
		case SESSION:
			ht.put(winletDef.m_strModuleClass + "/S", winlet);
			break;
		case INSTANCE:
		default:
			ht.put(winletDef.m_strFullPath + req.getWinIID(), winlet);
		}
		ht.put(winletDef.m_strModuleClass + req.getPageID(), winlet);
		ht.put(winletDef.m_strModuleClass, winlet);
	}

	static Winlet getWinlet(IServerContext context, IModuleRequest req,
			WinletDef winletDef) throws Exception {
		Winlet winlet = getWinlet(req, winletDef);
		if (winlet == null) {
			winlet = winletDef.newWinlet(false, context, req);
			putWinlet(req, winletDef, winlet);
		}
		return winlet;
	}

	/**
	 * 根据Winlet类名在当前页面查找Winlet
	 */
	public static Winlet getWinletInPage(IModuleRequest req, String className)
			throws Exception {
		// 首先在嵌套Winlet中查找
		Winlet w = ((ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE)).m_wis.m_view
				.getEmbedded(className);
		if (w != null)
			return w;

		// 其次在页面中寻找
		String pid = req
				.getParameterNotMultipart(REQUEST_PARAM_WIN_PAGE_ID, "");
		Hashtable<String, Winlet> ht = getWinletHashtable(req);
		return ht.get(className + pid);
	}

	/**
	 * 根据Winlet类在当前页面查找Winlet
	 */
	public static Winlet getWinletInPage(IModuleRequest req, Class<?> clz)
			throws Exception {
		// 首先在嵌套Winlet中查找
		Winlet w = ((ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE)).m_wis.m_view
				.getEmbedded(clz);
		if (w != null)
			return w;

		// 其次在页面中寻找
		String pid = req
				.getParameterNotMultipart(REQUEST_PARAM_WIN_PAGE_ID, "");
		Hashtable<String, Winlet> ht = getWinletHashtable(req);
		return ht.get(clz.getName() + pid);
	}

	/**
	 * 根据Winlet类名在所有已出浏览过的（若未浏览过Winlet实例不存在）页面中查找Winlet
	 * 
	 * @param req
	 * @param className
	 * @return
	 */
	public static Winlet getWinletInSite(IModuleRequest req, String className) {
		Hashtable<String, Winlet> ht = getWinletHashtable(req);
		return ht.get(className);
	}

	public static Vector<WinInstance> getWinInstancesInPage(IModuleRequest req) {
		return getWinInstances(req).get(req.getPageID());
	}

	public static WinInstance getWinInstance(IServerContext context,
			IModuleRequest req, WinletDef def) throws Exception {
		Hashtable<String, Vector<WinInstance>> ht = getWinInstances(req);
		Vector<WinInstance> wis;
		synchronized (ht) {
			wis = ht.get(req.getPageID());
			if (wis == null) {
				wis = new Vector<WinInstance>();
				ht.put(req.getPageID(), wis);
			}
		}

		for (WinInstance wi : wis) {
			if (wi.m_strIId.equals(req.getWinIID())) {
				if (wi.m_view.m_winletDef != def) { // Update the WinInstance
					wis.remove(wi);
					wi = new WinInstance(req.getWinIID(), def, getWinlet(
							context, req, def));
					wis.add(wi);
				}
				return wi;
			}
		}

		WinInstance wi = new WinInstance(req.getWinIID(), def, getWinlet(
				context, req, def));
		wis.add(wi);

		return wi;
	}

	public static ViewInstance getViewInstance(IServerContext context,
			IModuleRequest req, WinletDef def) throws Exception {
		return getWinInstance(context, req, def).findView(req.getViewID());
	}
}
