package com.aggrepoint.ae.data;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.ae.core.proxy.HttpClientResponse;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;

public class ResponseInfo {
	public static enum ResponseType {
		UNDEFINED, // 还未计算响应类型
		PAGE, // 非Ajax模式下返回整个页面
		RELOAD_PAGE, // 非Ajax模式中，执行窗口动作后用户身份或标记类型被改变，需要重新计算被访问页面后再返回整个页面
		REDIRECT_HOME, // 重定向到栏目首页，适用于Ajax模式及非Ajax模式
		REDIRECT_NOACCESS, // 重定向到栏目无权访问页面，适用于Ajax模式及非Ajax模式
		REDIRECT_LOGIN, // 重定向到栏目登陆页面，适用于Ajax模式及非Ajax模式
		REDIRECT_PAGE, // 重定向到指定页面，适用于Ajax模式及非Ajax模式
		REDIRECT_URL, // 重定向到指定URL，适用于Ajax模式及非Ajax模式
		WIN_RES, // 非Ajax模式下访问窗口资源
		NONE, // 无需返回，适用于Ajax模式及非Ajax模式
		NOT_FOUND, // 页面找不到
		AJAX_VALIDATE, // AJAX模式下数据校验
		AJAX_UPDATE_CONTENT, // AJAX模式下刷新窗口或区域
		AJAX_UPDATE_PAGE, // AJAX模式下刷新当前页面
		AJAX_DIALOG
		// AJAX模式下使用对话框
	}

	/** 返回类型 */
	public ResponseType responseType = ResponseType.UNDEFINED;
	/** 返回代码 */
	public int iRetCode;
	/** 对于REDIRECT_HOME：分支 */
	public ApBranch redirectBranch;
	/** 对于REDIRECT_PAGE:页面 */
	public ApBPage redirectPage;
	/** 对于REDIRECT_URL: URL */
	public String strRedirectUrl;
	/** 新的窗口模式 */
	public EnumWinMode newWinMode = EnumWinMode.NOCHANGE;
	/** 返回消息 */
	public String strMessage;
	/** Ajax模式下需要刷新的其他窗口的ID */
	public String strAjaxWinIds;
	public String strAjaxEnsureVisible;
	/** 请求返回内容 */
	public HttpClientResponse hcr;

	public ResponseInfo returnNone(int code) {
		iRetCode = code;
		responseType = ResponseType.NONE;
		return this;
	}

	public ResponseInfo returnNotFound(int code) {
		iRetCode = code;
		responseType = ResponseType.NOT_FOUND;
		return this;
	}

	public ResponseInfo returnPage(int code) {
		iRetCode = code;
		responseType = ResponseType.PAGE;
		return this;
	}

	/**
	 * 执行窗口动作后如果用户身份改变了或者标记语言改变了，而且要返回整个页面内容，则需要重新计算要返回的页面
	 * 这里的code与其他方法中的code不一样，是AccessPointProcess中定义的return code；其他方法中的code
	 * 为AccessPointPresent中定义的code
	 * 
	 * @param code
	 * @return
	 */
	public ResponseInfo returnReloadPagePage(int code) {
		iRetCode = code;
		responseType = ResponseType.RELOAD_PAGE;
		return this;
	}

	public ResponseInfo returnRedirectHome(int code, ApBranch branch) {
		iRetCode = code;
		redirectBranch = branch;
		responseType = ResponseType.REDIRECT_HOME;
		return this;
	}

	public ResponseInfo returnRedirectNoAccess(int code, ApBranch branch) {
		iRetCode = code;
		redirectBranch = branch;
		responseType = ResponseType.REDIRECT_NOACCESS;
		return this;
	}

	public ResponseInfo returnRedirectLogin(int code, ApBranch branch) {
		iRetCode = code;
		redirectBranch = branch;
		responseType = ResponseType.REDIRECT_LOGIN;
		return this;
	}

	public ResponseInfo returnRedirectPage(int code, ApBPage page) {
		iRetCode = code;
		redirectPage = page;
		responseType = ResponseType.REDIRECT_PAGE;
		return this;
	}

	public ResponseInfo returnRedirectUrl(int code, String url) {
		iRetCode = code;
		strRedirectUrl = url;
		responseType = ResponseType.REDIRECT_URL;
		return this;
	}

	public ResponseInfo returnAjaxUpdatePage(int code) {
		iRetCode = code;
		responseType = ResponseType.AJAX_UPDATE_PAGE;
		return this;
	}

	public ResponseInfo returnAjaxUpdateContent(int code) {
		iRetCode = code;
		responseType = ResponseType.AJAX_UPDATE_CONTENT;
		return this;
	}

	public ResponseInfo returnAjaxDialog(int code) {
		iRetCode = code;
		responseType = ResponseType.AJAX_DIALOG;
		return this;
	}

	public ResponseInfo returnAjaxValidate(int code) {
		iRetCode = code;
		responseType = ResponseType.AJAX_VALIDATE;
		return this;
	}

	public ResponseInfo returnWinRes(int code) {
		iRetCode = code;
		responseType = ResponseType.WIN_RES;
		return this;
	}
}
