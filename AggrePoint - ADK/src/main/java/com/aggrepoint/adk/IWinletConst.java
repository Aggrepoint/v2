package com.aggrepoint.adk;

/**
 * Winlet相关的常量定义
 * 
 * @author YJM
 */
public interface IWinletConst {
	// /////////////////////////////////////////////////
	//
	// Servlet Request属性
	//
	// /////////////////////////////////////////////////
	public static final String REQUEST_ATTR_WINLET = "com.aggrepoint.adk.winlet";

	// /////////////////////////////////////////////////
	//
	// 线程属性
	//
	// /////////////////////////////////////////////////
	/** 支持一个Winlet实例多个View */
	public static final String THREAD_ATTR_VIEW_INSTANCE = "com.aggrepoint.adk.view.instance";

	// /////////////////////////////////////////////////
	//
	// HTTP请求参数
	//
	// /////////////////////////////////////////////////

	/** 请求参数名称：窗口实例ID */
	public static final String REQUEST_PARAM_WIN_INST_ID = "_adk_i";
	/** 请求参数名称：窗口所属页面ID */
	public static final String REQUEST_PARAM_WIN_PAGE_ID = "_adk_p";
	/** 请求参数名称：视图ID */
	public static final String REQUEST_PARAM_VIEW_ID = "_adk_v";
	/** 请求参数名称：Action */
	public static final String REQUEST_PARAM_ACTION = "_adk_a";
	/** 请求参数名称：表单编号 */
	public static final String REQUEST_PARAM_FORM = "_adk_f";
	/** 请求参数名称：是否字段校验 */
	public static final String REQUEST_PARAM_VALIDATE_FIELD = "_adk_vf";

	// /////////////////////////////////////////////////
	//
	// HTTP请求头
	//
	// /////////////////////////////////////////////////
	/** 请求版本号 */
	public static final String REQUEST_HEADER_VER = "ADK-Ver";

	public static final String REQUEST_HEADER_WINDOW_MODE = "ADK-Window-Mode";
	/** 请求头：将窗口状态重置为初始状态 */
	public static final String REQUEST_HEADER_RESET_WINDOW = "ADK-Reset-Window";
	/** 前端生成的请求编号，唯一标识一次用户请求 */
	public static final String REQUEST_HEADER_REQUEST_ID = "ADK-Request-ID";
	/** 说明窗口静态资源URL，为空表示AP中没有设定窗口的静态资源URL */
	public static final String REQUEST_HEADER_STATIC_WIN_RES_URL = "ADK-Static-Win-Res-Url";
	/** 用户身份 */
	public static final String REQUEST_HEADER_USER_PROFILE = "ADK-User-Profile";
	/** 用户代理 */
	public static final String REQUEST_HEADER_USER_AGENT = "ADK-User-Agent";
	/** 用户IP地址 */
	public static final String REQUEST_HEADER_USER_IP = "ADK-User-Ip";
	/** 用户请求的URL */
	public static final String REQUEST_HEADER_REQUEST_URI = "ADK-Request-Uri";
	/** 当前使用的标记语言 */
	public static final String REQUEST_HEADER_MARKUP_TYPE = "ADK-Markup-Type";
	/** 窗口参数 */
	public static final String REQUEST_HEADER_WINDOW_PARAMS = "ADK-Win-Params";
	/** 窗口所属区域名称 */
	public static final String REQUEST_HEADER_WINDOW_AREA_NAME = "ADK-Win-Area-Name";
	/** 当前分支编号 */
	public static final String REQUEST_HEADER_SITE_BRANCH = "ADK-Site-Branch";
	/** 当前应用编号 */
	public static final String REQUEST_HEADER_SITE_APP = "ADK-Site-App";
	/** 栏目URL前缀 */
	public static final String REQUEST_HEADER_URL_ROOT = "ADK-Url-Root";

	// /////////////////////////////////////////////////
	//
	// HTTP响应头
	//
	// /////////////////////////////////////////////////
	public static final String RESPONSE_HEADER_SET_WINDOW_TITLE = "ADK-Window-Title";

	public static final String RESPONSE_HEADER_SET_CACHE = "ADK-Window-Cache";

	public static final String RESPONSE_HEADER_SET_WINDOW_MODE = "ADK-Window-Mode";

	public static final String RESPONSE_HEADER_SET_UPDATE_WINDOW = "ADK-Update-Window";

	public static final String RESPONSE_HEADER_SET_MESSAGE = "ADK-Message";

	public static final String RESPONSE_HEADER_SET_ENSURE_VISIBLE = "ADK-Ensure-Visible";

	public static final String RESPONSE_HEADER_SET_USE_DIALOG = "ADK-Use-Dialog";

	public static final String RESPONSE_HEADER_SET_USER_PROFILE = "ADK-Set-User-Profile";

	public static final String RESPONSE_HEADER_SET_REDIRECT = "ADK-Set-Redirect";
	/**
	 * 取值为标记名称或者IAPP_CONST_MARKUP_SAVE。
	 * 为IAPP_CONST_MARKUP_SAVE表示将当前选定的标记类型保存为cookie，下次访问时自动使用
	 */
	/** 将选定标记和保存标记分为两个步骤的目的是防止用户误选不合适的标记后无法返回可用的标记 */
	public static final String RESPONSE_HEADER_SET_MARKUP_TYPE = "ADK-Set-Markup-Type";

	public static final String RESPONSE_HEADER_CONST_MARKUP_SAVE = "save";

	// /////////////////////////////////////////////////
	//
	// 嵌套视图请求头
	//
	// /////////////////////////////////////////////////

	/** 嵌套视图请求头：视图ID */
	public static final String REQUEST_HEADER_VIEW_HEADER_ID = "com.aggrepoint.adk.view.id";
	/** 嵌套视图请求头：RequestPath */
	public static final String REQUEST_HEADER_VIEW_HEADER_REQ_PATH = "com.aggrepoint.adk.req.path";
}
