package com.aggrepoint.adk.ui.validator;

import java.util.Vector;

import com.aggrepoint.adk.ui.IUIValidator;
import com.aggrepoint.adk.ui.data.Property;
import com.icebean.core.beanutil.PropertyTypeCode;

public class GreaterThan implements IUIValidator, PropertyTypeCode {
	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception {
		switch (property.getTypeCode()) {
		case SHORT:
			return ((Short) property.get(obj)).intValue() > Integer
					.parseInt(args.elementAt(0));
		case INTEGER:
			return ((Integer) property.get(obj)).intValue() > Integer
					.parseInt(args.elementAt(0));
		case LONG:
			return ((Long) property.get(obj)).intValue() > Long
					.parseLong(args.elementAt(0));
		case FLOAT:
			return ((Float) property.get(obj)).floatValue() > Float
					.parseFloat(args.elementAt(0));
		case DOUBLE:
			return ((Double) property.get(obj)).floatValue() > Double
					.parseDouble(args.elementAt(0));
		}
		return false;
	}
}
