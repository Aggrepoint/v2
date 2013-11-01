package com.aggrepoint.adk;


/**
 * ControlServlet内部发生的异常
 * 
 * @author YJM
 */
public class ControlException extends WebBaseException {
	private static final long serialVersionUID = 1L;

	/** 返回码 */
	int m_retcode;

	public ControlException(int retcode) {
		m_retcode = retcode;
	}

	public ControlException(int retcode, Throwable e) {
		super("", e);
		m_retcode = retcode;
	}

	public ControlException(int retcode, String message, Throwable e) {
		super(message, e);
		m_retcode = retcode;
	}

	public ControlException(int retcode, String message) {
		super(message);
		m_retcode = retcode;
	}

	public int getRetCode() {
		return m_retcode;
	}
}
