package com.aggrepoint.adk.data;

import java.util.Enumeration;
import java.util.Vector;

import com.aggrepoint.adk.DynamicClassLoader;
import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletAccess;
import com.aggrepoint.adk.WinletHelper;
import com.aggrepoint.adk.WinletModule;
import com.aggrepoint.adk.WinletScope;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;

/**
 * Winlet的定义！！
 * 
 * @author YJM
 */
public class WinletDef extends BaseModuleDef implements IWinletConst {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	public WinletScope m_scope = WinletScope.INSTANCE;
	public Vector<WinletViewDef> m_vecViews;
	public String m_strView;
	public WinletViewDef m_view;

	public WinletDef() throws AdbException {
		m_vecViews = new Vector<WinletViewDef>();
		m_strView = "";
	}

	public String getScope() {
		return m_scope.getName();
	}

	public WinletViewDef findView(String name) {
		for (WinletViewDef view : m_vecViews)
			if (view.m_strPath.equals(name))
				return view;
		return null;
	}

	public void setScope(String str) {
		m_scope = WinletScope.fromName(str);
	}

	public Class<?> getModuleClass(boolean reload) throws Exception {
		if (m_moduleClass == null || reload) {
			m_htRetCodes = null;
			m_htParams = null;
			m_htMsgs = null;

			m_moduleClass = new DynamicClassLoader(getClass().getClassLoader())
					.loadClass(m_strModuleClass);

			// 清除所有Action和State上的方法对象
			if (m_view != null) {
				for (Enumeration<WinletStateDef> enm = m_view.m_vecStates
						.elements(); enm.hasMoreElements();)
					enm.nextElement().m_method = null;
				for (Enumeration<WinletActionDef> enm = m_view.m_vecActions
						.elements(); enm.hasMoreElements();)
					enm.nextElement().m_method = null;
			}
		}

		return m_moduleClass;
	}

	public Winlet newWinlet(boolean reload, IServerContext context,
			IModuleRequest req) throws Exception {
		// Create Instance
		Winlet winlet = (Winlet) getModuleClass(reload).newInstance();
		winlet.init(context, req);
		return winlet;
	}

	/**
	 * 
	 */
	public IModule getModuleInstance(boolean reload, IServerContext context,
			IModuleRequest req) throws Exception {
		ViewInstance vi = WinletHelper.getViewInstance(context, req, this);
		String action = req.getActionID();

		if (action == null) {
			String strReset = req.getHeader(REQUEST_HEADER_RESET_WINDOW);
			if (strReset != null && strReset.equalsIgnoreCase("yes")) {
				WinletHelper.resetForms(req, vi.getWinlet());
				vi.getWinlet().reset();
				WinletAccess.resetState(vi.getWinlet(), vi.getViewDef());
			}

			WinletStateDef def = vi.getViewDef().findStateDef(
					WinletAccess.getState(vi.getWinlet(), vi.getViewDef()));
			vi.clearSub();
			return new WinletModule(def, vi, def.getMethod(),
					def.executeOncePerFrontRequest());
		} else {
			WinletActionDef def = vi.getViewDef().findActionDef(action);
			if (def == null) {
				String errmsg = m_msg.constructMessage("actionNotFoundInView",
						vi.getWinlet().getClass().getName(),
						vi.getViewDef().m_strPath, action);
				m_log.error(errmsg);
				throw new Exception(errmsg);
			}

			return new WinletModule(def, vi, def.getMethod(),
					def.executeOncePerFrontRequest());
		}
	}

	/**
	 * 将Winlet上设置的User和PsnEngine设置到Action和State上
	 */
	public void cascadeUserAndPsnEngine() {
		if (m_view != null) {
			// {设置State和Action的用户引擎和个性化引擎设置
			for (WinletStateDef state : m_view.m_vecStates) {
				if (state.m_strPsnEngineID == null
						|| state.m_strPsnEngineID.equals(""))
					state.m_strPsnEngineID = m_strPsnEngineID;
				if (state.m_strUserEngineID == null
						|| state.m_strUserEngineID.equals(""))
					state.m_strUserEngineID = m_strUserEngineID;
			}

			for (WinletActionDef action : m_view.m_vecActions) {
				if (action.m_strPsnEngineID == null
						|| action.m_strPsnEngineID.equals(""))
					action.m_strPsnEngineID = m_strPsnEngineID;
				if (action.m_strUserEngineID == null
						|| action.m_strUserEngineID.equals(""))
					action.m_strUserEngineID = m_strUserEngineID;
			}
			// }
		}
	}

	/**
	 * 当Window定义在apapp.xml之外时使用
	 */
	public void copy(WinletDef def) {
		m_vecViews = def.m_vecViews;
		m_vecRetCodes = def.m_vecRetCodes;
		m_vecLogParams = def.m_vecLogParams;
		// 允许在apapp.xml中定义Winlet的参数
		m_vecParams.addAll(def.m_vecParams);
		m_vecMsgs = def.m_vecMsgs;

		for (WinletViewDef view : m_vecViews) {
			if (view.m_strPath.equals(m_strView))
				m_view = view;
		}

		for (WinletViewDef view : m_vecViews)
			view.m_parent = this;
	}

	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) {
		for (WinletViewDef view : m_vecViews) {
			if (view.m_strPath.equals(m_strView))
				m_view = view;
		}
	}

	public WinletActionDef findActionDef(String path) {
		if (m_view == null)
			return null;
		return m_view.findActionDef(path);
	}

	public WinletStateDef findStateDef(String id) {
		if (m_view == null)
			return null;
		return m_view.findStateDef(id);
	}

	public void print(String tab, java.io.PrintWriter pw) {
		printStartTag(tab, "path", pw, "win");
		printSubElements(tab, pw);
		for (WinletViewDef view : m_vecViews)
			view.print(tab + "\t", pw);
		printEndTag(tab, pw, "win");
	}
}
