package com.aggrepoint.adk.ui.validator;

import java.util.Vector;
import java.util.regex.Pattern;

import com.aggrepoint.adk.ui.IUIValidator;
import com.aggrepoint.adk.ui.data.Property;

public class Email implements IUIValidator {
	static Pattern EMAIL = Pattern.compile(
			"^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
			Pattern.CASE_INSENSITIVE);

	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception {
		Object val = property.get(obj);
		if (val == null)
			return false;
		return EMAIL.matcher(val.toString()).find();
	}
}
