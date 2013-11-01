package com.aggrepoint.adk.ui;

/**
 * 配置加载异常，配置文件格式不对
 * 
 * @author: Yang Jiang Ming
 */
public class UIConfigLoadException extends
		com.icebean.core.common.BaseException {
	static final long serialVersionUID = 0;

	/**
	 * 构造函数
	 */
	public UIConfigLoadException() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public UIConfigLoadException(String s) {
		super(s);
	}

	/**
	 * 构造函数
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public UIConfigLoadException(Exception e, String s) {
		super(e, s);
	}
}
