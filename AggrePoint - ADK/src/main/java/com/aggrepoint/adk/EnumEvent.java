package com.aggrepoint.adk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum EnumEvent {
	/** unknow */
	UNKNOW("unknow"),
	/** before present or action */
	BEFORE("^\\s*before\\s*$"),
	/** after present or action */
	AFTER("^\\s*after\\s*$"),
	/** after present or action and got specified return code */
	RETCODE_IN("^\\s*retcode\\s*in\\s*\\(([^\\)]*)\\)\\s*$", true),
	/** after present or action and not got specified return code */
	RETCODE_NOT_IN("^\\s*retcode\\s*not\\s*in\\s*\\(([^\\)]*)\\)\\s*$", false),
	/** before present */
	BEFORE_STATE("^\\s*before\\s*state\\s*$"),
	/** after present */
	AFTER_STATE("^\\s*after\\s*state\\s*$"),
	/** after present and got specified return code */
	STATE_RETCODE_IN("^\\s*state\\s*retcode\\s*in\\s*\\(([^\\)]*)\\)\\s*$",
			true),
	/** after present and not got specified return code */
	STATE_RETCODE_NOT_IN(
			"^\\s*state\\s*retcode\\s*not\\s*in\\s*\\(([^\\)]*)\\)\\s*$", false),
	/** before action */
	BEFORE_ACTION("^\\s*before\\s*action\\s*$"),
	/** after action */
	AFTER_ACTION("^\\s*after\\s*action\\s*$"),
	/** after action and got specified return code */
	ACTION_RETCODE_IN("^\\s*action\\s*retcode\\s*in\\s*\\(([^\\)]*)\\)\\s*$",
			true),
	/** after action and got specified return code */
	ACTION_RETCODE_NOT_IN(
			"^\\s*action\\s*retcode\\s*not\\s*in\\s*\\(([^\\)]*)\\)\\s*$",
			false);

	public static EnumEvent[] beforeState = { BEFORE, BEFORE_STATE };
	public static EnumEvent[] afterState = { AFTER, AFTER_STATE, RETCODE_IN,
			RETCODE_NOT_IN, STATE_RETCODE_IN, STATE_RETCODE_NOT_IN };
	public static EnumEvent[] beforeAction = { BEFORE, BEFORE_ACTION };
	public static EnumEvent[] afterAction = { AFTER, AFTER_ACTION, RETCODE_IN,
			RETCODE_NOT_IN, ACTION_RETCODE_IN, ACTION_RETCODE_NOT_IN };

	Pattern pat;
	/** -1 : not in; 0 : no matter; 1 : in */
	int inMode;

	EnumEvent(String exp, boolean in) {
		this.pat = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
		if (in)
			inMode = 1;
		else
			inMode = -1;
	}

	EnumEvent(String exp) {
		this.pat = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
		inMode = 0;
	}

	public int getInMode() {
		return inMode;
	}

	public boolean isBefore() {
		for (EnumEvent event : beforeState)
			if (event == this)
				return true;
		for (EnumEvent event : beforeAction)
			if (event == this)
				return true;
		return false;
	}

	public static EnumEvent find(String by) {
		if (by == null)
			return UNKNOW;
		for (EnumEvent event : EnumEvent.values()) {
			Matcher m = event.pat.matcher(by);
			if (m.find())
				return event;
		}
		return UNKNOW;
	}

	public String[] getParams(String by) {
		Matcher m = pat.matcher(by);
		if (!m.find())
			return null;

		return m.group(1).split("\\s*,\\s*");
	}
}
