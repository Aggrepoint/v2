package com.aggrepoint.adk;


/**
 * 跳过运行
 * 
 * @author YJM
 */
public class SkipExecuteException extends WebBaseException {
	private static final long serialVersionUID = 1L;

	/** 返回码 */
	int m_retcode;

	public SkipExecuteException(int retcode) {
		m_retcode = retcode;
	}

	public int getRetCode() {
		return m_retcode;
	}
}
