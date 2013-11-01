package com.aggrepoint.adk;

/**
 * 构建访问AE的各种URL时需要用到的参数名称
 * 
 * @author Owner
 * 
 */
public interface IAEUrlParamConst {
	public final String PARAM_WIN_ID = "_w";
	public final String PARAM_WIN_VIEW = "_wv";
	/**
	 * _a的值可以包含_w和_wv的值，用!分隔，例如231!231_1!test。
	 * _w和_wv仍然需要保留，用于缩放窗口、查看窗口等操作。
	 */
	public final String PARAM_WIN_ACTION = "_a";
	public final String PARAM_WIN_VALIDATE_FIELD = "_vf";
	/**
	 * 与_a一样，_r的值可以包含_w和_wv的值，用!分隔
	 */
	public final String PARAM_WIN_RES = "_r";
	public final String PARAM_WIN_PARAM = "_p";
	public final String PARAM_CONT_ID = "_c";
	/** 路径部分,不包含域名,也不包含参数 */
	public final String PARAM_PAGE_PATH = "_pg";
	/** 完整的页面URL */
	public final String PARAM_PAGE_URL = "_purl";

	public final String PARAM_REMOTE_ADDR = "ae_remote_addr";
	public final String PARAM_REQUEST_URI = "ae_request_uri";
}
