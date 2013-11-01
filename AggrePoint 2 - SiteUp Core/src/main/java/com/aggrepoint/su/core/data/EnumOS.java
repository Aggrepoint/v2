package com.aggrepoint.su.core.data;

import java.util.regex.Pattern;

public enum EnumOS {
	UNKNOW(0, EnumDeviceCategory.UNKNOW, "UNKNOW", null), //
	WINDOWS_NT(1, EnumDeviceCategory.UNKNOW, "Windows NT"), //
	WINDOWS_XP(2, EnumDeviceCategory.UNKNOW, "Windows XP"), //
	WINDOWS_98(3, EnumDeviceCategory.UNKNOW, "Windows 98"), //
	WINDOWS_CE(4, EnumDeviceCategory.MOBILE, "Windows CE"), //
	LINUX(5, EnumDeviceCategory.UNKNOW, "Linux"), //
	MAC_INTEL(6, EnumDeviceCategory.UNKNOW, "Intel Mac OS"), //
	MAC_PPC(7, EnumDeviceCategory.UNKNOW, "PPC Mac OS"), //
	SYMBIAN_OS(8, EnumDeviceCategory.MOBILE, "SymbianOS"), //
	SYMBIAN(9, EnumDeviceCategory.MOBILE, "Symbian"), //
	WINDOWS(10, EnumDeviceCategory.UNKNOW, "Windows");

	String key;
	String upperKey;
	EnumDeviceCategory category;
	int id;
	Pattern pattern;

	EnumOS(int id, EnumDeviceCategory category, String key) {
		this.key = key;
		this.upperKey = key.toUpperCase();
		this.category = category;
		this.id = id;
		pattern = Pattern.compile("(?:^| |\\()" + key
				+ "(?: |/)?([^\\s;/\\(\\),]*)", Pattern.CASE_INSENSITIVE);
	}

	EnumOS(int id, EnumDeviceCategory category, String key, String regExp) {
		this(id, category, key);
		if (regExp != null)
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
	}

	public int getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getUpperKey() {
		return upperKey;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public EnumDeviceCategory getCategory() {
		return category;
	}

	public static EnumOS fromId(int id) {
		for (EnumOS t : EnumOS.values()) {
			if (t.getId() == id)
				return t;
		}
		return UNKNOW;
	}
}
