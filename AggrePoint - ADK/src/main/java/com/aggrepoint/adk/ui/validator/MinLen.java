package com.aggrepoint.adk.ui.validator;

import java.util.Vector;

import com.aggrepoint.adk.ui.IUIValidator;
import com.aggrepoint.adk.ui.data.Property;
import com.icebean.core.common.StringUtils;

public class MinLen implements IUIValidator {
	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception {
		int len = 0;
		Object value = property.get(obj);
		if (value != null)
			if (args.size() > 1)
				len = StringUtils.getDBLength(value.toString(),
						Integer.parseInt(args.elementAt(1)));
			else
				len = value.toString().length();

		return len >= Integer.parseInt(args.elementAt(0));
	}
}
