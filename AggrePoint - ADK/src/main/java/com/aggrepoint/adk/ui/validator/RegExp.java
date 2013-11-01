package com.aggrepoint.adk.ui.validator;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import com.aggrepoint.adk.ui.IUIValidator;
import com.aggrepoint.adk.ui.data.Property;

public class RegExp implements IUIValidator {
	static Hashtable<String, Pattern> PATTERNS = new Hashtable<String, Pattern>();

	static Pattern getPattern(Vector<String> args) {
		if (args == null || args.size() == 0)
			return null;

		String key = args.elementAt(0);
		if (args.size() > 1)
			key = key + "_" + args.elementAt(1);
		Pattern p = PATTERNS.get(key);
		if (p == null) {
			if (args.size() == 1)
				p = Pattern.compile(args.elementAt(0));
			else
				p = Pattern.compile(args.elementAt(0), Integer.parseInt(args
						.elementAt(1)));
			PATTERNS.put(key, p);
		}
		return p;
	}

	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception {
		Object val = property.get(obj);
		if (val == null)
			return false;
		return getPattern(args).matcher(val.toString()).find();
	}
}
