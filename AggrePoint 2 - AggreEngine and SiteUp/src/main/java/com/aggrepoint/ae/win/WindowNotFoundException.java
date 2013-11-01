package com.aggrepoint.ae.win;

import com.icebean.core.common.BaseException;

/**
 * @author YJM
 */
public class WindowNotFoundException extends BaseException {
	static final long serialVersionUID = 0;

	public WindowNotFoundException() {
	}

	public WindowNotFoundException(String s) {
		super(s);
	}

	public WindowNotFoundException(String s, Throwable e) {
		super(e, s);
	}
}
