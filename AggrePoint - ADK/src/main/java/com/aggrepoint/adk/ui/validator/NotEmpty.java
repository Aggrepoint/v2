package com.aggrepoint.adk.ui.validator;

import java.util.Vector;

import com.aggrepoint.adk.ui.IUIValidator;
import com.aggrepoint.adk.ui.data.Property;

public class NotEmpty implements IUIValidator {
	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception {
		Object val = property.get(obj);
		if (val == null)
			return false;
		if (val.toString().equals(""))
			return false;
		return true;
	}
}
