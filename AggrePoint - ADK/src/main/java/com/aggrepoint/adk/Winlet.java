package com.aggrepoint.adk;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.data.WinletViewDef;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.adk.ui.UIAdapter;
import com.icebean.core.adb.ADB;
import com.icebean.core.common.ThreadContext;

/**
 * Winlet基类。
 * 
 * @author YJM
 */
public abstract class Winlet implements Serializable, IWinletConst {
	private static final long serialVersionUID = 1L;

	/** 状态 */
	Hashtable<String, String> m_htStates = new Hashtable<String, String>();

	IServerContext m_context;

	/**
	 * 获取当前生效的WinletDef<br>
	 * 多个WinletDef可能共享一个Winlet，每个Winlet展示不同的视图。
	 * 
	 * @return
	 */
	private WinletViewDef getViewDef() {
		return ((ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE)).getViewDef();
	}

	/**
	 * Winlet实例初始化时，该方法会被调用。在派生的Winlet中重载该方法时，应调用super.init()以确保父类中的初始化逻辑被执行。
	 * 
	 * @param context
	 *            Winlet所运行的服务器环境。
	 * @param req
	 *            触发Winlet实例化的请求。
	 * @throws Exception
	 *             初始化遇到异常。
	 */
	public void init(IServerContext context, IModuleRequest req)
			throws Exception {
		m_context = context;
	}

	/**
	 * 重置Winlet状态。
	 */
	public void reset() {
	}

	/**
	 * 重置状态
	 */
	void resetState(WinletViewDef def) {
		if (def == null)
			def = getViewDef();
		if (def == null)
			return;
		m_htStates.put(def.m_strPath, def.m_defaultState.m_strPath);
	}

	/**
	 * 设置视图状态
	 * 
	 * @param viewPath
	 * @param state
	 */
	public void setState(String viewPath, String state) {
		m_htStates.put(viewPath, state);
	}

	void setState(WinletViewDef def, String state) {
		if (def == null)
			def = getViewDef();
		if (def == null)
			return;
		m_htStates.put(def.m_strPath, state);
	}

	/**
	 * 获取视图状态。
	 * 
	 * @param viewPath
	 *            视图的路径。
	 * @return 视图的状态。
	 */
	public String getState(String viewPath) {
		return m_htStates.get(viewPath);
	}

	/** 获取视图状态 */
	String getState(WinletViewDef def) {
		if (def == null)
			def = getViewDef();
		if (def == null)
			return null;

		if (!m_htStates.containsKey(def.m_strPath))
			m_htStates.put(def.m_strPath, def.m_defaultState.m_strPath);

		return m_htStates.get(def.m_strPath);
	}

	/**
	 * 获取当前状态。
	 * 
	 * @return 状态id。
	 */
	public String getState() {
		return getState((WinletViewDef) null);
	}

	/**
	 * 不做任何处理。
	 * 
	 * @param req
	 *            请求。
	 * @param resp
	 *            响应。
	 * @return 返回返回码0。
	 * @throws Exception
	 *             抛出的异常。
	 */
	public int dummy(IModuleRequest req, IModuleResponse resp) throws Exception {
		return 0;
	}

	/**
	 * 获取Portal中窗口显示的模式。
	 * 
	 * @param req
	 *            请求对象
	 * @return 窗口模式。
	 */
	public static EnumWinMode getWinMode(IModuleRequest req) {
		String winMode = req.getHeader(REQUEST_HEADER_WINDOW_MODE);
		if (winMode == null || winMode.equals(""))
			return EnumWinMode.NOCHANGE;

		return EnumWinMode.fromId(Integer.parseInt(winMode) & 0xF);
	}

	/**
	 * 从请求中获得窗口的标记 ADK-Window-Mode的4~8位以上用于系统保留的窗口标记
	 * 
	 * @param req
	 * @return
	 */
	protected static int getReservedWinFlag(IModuleRequest req) {
		String winMode = req.getHeader(REQUEST_HEADER_WINDOW_MODE);
		if (winMode == null || winMode.equals(""))
			return 0;

		return (Integer.parseInt(winMode) >> 4) & 0xf;
	}

	/**
	 * 判断Portal管理员是否正在对当前页面进行可视化编辑。
	 * 
	 * @param req
	 *            请求对象。
	 * @return true - 管理员正在对当前页面进行可视化编辑。
	 */
	public static boolean isInEditMode(IModuleRequest req) {
		return (getReservedWinFlag(req) & 1) > 0;
	}

	/**
	 * 从请求中获得自定义的窗口的标记。 ADK-Window-Mode的8位以上用于应用自定义窗口标记
	 * 
	 * @param req
	 *            请求对象。
	 * @return
	 */
	protected static int getWinFlag(IModuleRequest req) {
		String winMode = req.getHeader(REQUEST_HEADER_WINDOW_MODE);
		if (winMode == null || winMode.equals(""))
			return 0;

		return Integer.parseInt(winMode) >> 8;
	}

	/**
	 * 设置前端Portal中窗口显示的模式。
	 * 
	 * @param resp
	 *            请求对象。
	 * @param mode
	 *            要设置的模式。
	 */
	public void setWinMode(IModuleResponse resp, EnumWinMode mode) {
		resp.setHeader(RESPONSE_HEADER_SET_WINDOW_MODE,
				Integer.toString(mode.getId()));
	}

	/**
	 * 设置前端Portal中窗口的标题。
	 * 
	 * @param resp
	 *            请求对象。
	 * @param title
	 *            要设置的标题。
	 */
	public void setWinTitle(IModuleResponse resp, String title) {
		try {
			resp.setHeader(RESPONSE_HEADER_SET_WINDOW_TITLE,
					URLEncoder.encode(title, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
	}

	/**
	 * 设置当前用户身份。
	 * 
	 * @param resp
	 *            响应对象。
	 * @param userProfile
	 *            用户身份。
	 * @throws UnsupportedEncodingException
	 *             处理异常。
	 */
	public static void setUserProfile(IModuleResponse resp,
			WinletUserProfile userProfile) throws UnsupportedEncodingException {
		if (userProfile != null)
			resp.setHeader(RESPONSE_HEADER_SET_USER_PROFILE,
					userProfile.serialize());
	}

	/**
	 * 清除当前用户身份。
	 * 
	 * @param resp
	 *            响应对象。
	 */
	public static void clearUserProfile(IModuleResponse resp) {
		resp.setHeader(RESPONSE_HEADER_SET_USER_PROFILE, "");
	}

	/**
	 * 设置用户选用的标记类型。
	 * 
	 * @param resp
	 *            响应对象。
	 * @param markup
	 *            选用的标记类型。
	 * @throws UnsupportedEncodingException
	 *             处理异常。
	 */
	public static void setMarkup(IModuleResponse resp, String markup)
			throws UnsupportedEncodingException {
		resp.setHeader(RESPONSE_HEADER_SET_MARKUP_TYPE, markup);
	}

	/**
	 * 保存用户选用的标记类型。
	 * 
	 * @param resp
	 *            响应对象。
	 * @throws UnsupportedEncodingException
	 *             处理异常。
	 */
	public static void saveMarkup(IModuleResponse resp)
			throws UnsupportedEncodingException {
		resp.setHeader(RESPONSE_HEADER_SET_MARKUP_TYPE,
				RESPONSE_HEADER_CONST_MARKUP_SAVE);
	}

	/**
	 * 在用户输入过程中使用UIAdapter对用户刚刚录入的数据进行校验。
	 * 
	 * @param req
	 *            请求对象。
	 * @param resp
	 *            响应对象。
	 * @param edit
	 *            被编辑的数据对象。
	 * @param group
	 *            用于数据对象录入的ui group的id。
	 * 
	 * @return 0 - 没有错误 10 - 校验出错
	 * 
	 * @throws Exception
	 *             处理异常。
	 */
	protected int ajaxValidate(IModuleRequest req, IModuleResponse resp,
			ADB edit, String group) throws Exception {
		Vector<String> errors = new UIAdapter(req).populate(edit, group,
				req.getParameter("name"), req.getParameter("value"));
		if (errors.size() == 0)
			return 0;

		StringBuffer sb = new StringBuffer();
		boolean bFirst = true;
		for (String err : errors) {
			if (!bFirst)
				sb.append(" ");
			else
				bFirst = false;
			sb.append(err);
		}

		resp.setUserMessage(sb.toString());
		return 10;
	}
}
