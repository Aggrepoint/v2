package com.aggrepoint.adk.ui.data;

import com.icebean.core.adb.AdbException;

public class Error extends Matchable {
	public String m_strError;

	public Error() throws AdbException {
		m_strError = "";
	}
}
