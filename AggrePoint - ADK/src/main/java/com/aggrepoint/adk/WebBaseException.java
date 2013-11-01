package com.aggrepoint.adk;

import com.icebean.core.common.*;

/**
 * @author YJM
 */
public class WebBaseException extends BaseException {
	private static final long serialVersionUID = 1L;

	public WebBaseException() {
	}

	public WebBaseException(String s) {
		super(s);
	}

	public WebBaseException(String s, Throwable e) {
		super(e, s);
	}
}
