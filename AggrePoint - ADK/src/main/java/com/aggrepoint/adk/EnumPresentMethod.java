package com.aggrepoint.adk;


public enum EnumPresentMethod {
	NOTDEFINED(-1, "n"), FORWARD(0, "f"), REDIRECT(1, "r"), NOACTION(2, ""), INCLUDE(
			3, "i"), FORWARD_CUSTOMIZED(4, "fc"), REDIRECT_CUSTOMIZED(5, "rc"), INCLUDE_CUSTOMIZED(
			6, "ic"), PASS(7, "p");
	int id;
	String key;

	EnumPresentMethod(int id, String key) {
		this.id = id;
		this.key = key;
	}

	public static EnumPresentMethod fromId(int id) {
		for (EnumPresentMethod m : EnumPresentMethod.values())
			if (m.id == id)
				return m;
		return NOTDEFINED;
	}

	public static EnumPresentMethod fromKey(String key) {
		if (key == null)
			return NOTDEFINED;

		for (EnumPresentMethod m : EnumPresentMethod.values())
			if (m.key.equalsIgnoreCase(key))
				return m;
		return NOTDEFINED;
	}

	public int getId() {
		return id;
	}

	public String getKey() {
		return key;
	}
}
