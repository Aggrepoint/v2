package com.aggrepoint.adk.ui.data;

import com.aggrepoint.adk.ui.IUIValidator;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * @author YJM
 */
public class ValidatorDef extends ADB {
	public String m_strId;
	public String m_strClass;
	public IUIValidator m_validator;

	public ValidatorDef() throws AdbException {
		m_strId = m_strClass = "";
		m_validator = null;
	}
}
