package com.aggrepoint.adk.form;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.ui.ValidateResultType;

public class InputFileImpl extends InputImpl {
	public Object getValue() {
		return "";
	}

	@Override
	public ValidateResultType populate(IModuleRequest req) {
		clearErrors();

		if (bDisabled)
			return ValidateResultType.PASS_CONTINUE;

		try {
			if (strProperty != null)
				getProp().set(propObject, req.getFileParameter(getName()));
			else
				objValue = req.getFileParameter(getName());

			// 校验
			return validate(req, null);
		} catch (Exception e) {
			addError(strDefaultError);
			m_log.error("Error setting upload file to property \""
					+ strProperty + "\" of winlet \""
					+ winlet.getClass().getName() + "\".");
			return ValidateResultType.FAILED_CONTINUE;
		}
	}
}
