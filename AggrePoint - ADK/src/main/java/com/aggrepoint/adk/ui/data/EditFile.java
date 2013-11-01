package com.aggrepoint.adk.ui.data;

import com.aggrepoint.adk.IModuleRequest;
import com.icebean.core.adb.ADB;

public class EditFile extends EditText {
	public EditFile() {
		m_strType = "file";
	}

	public void populate(ADB adb, IModuleRequest req) throws Exception {
		m_property.set(adb, req.getFileParameter(m_property.getId()));
	}
}
