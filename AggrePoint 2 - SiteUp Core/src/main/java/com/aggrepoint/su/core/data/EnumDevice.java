package com.aggrepoint.su.core.data;

import java.util.regex.Pattern;

public enum EnumDevice {
	COMPUTER(-1, EnumDeviceCategory.COMPUTER, "COMPUTER"), // 
	UNKNOW(0, EnumDeviceCategory.UNKNOW, "UNKNOW"), // 
	NOKIA(1, EnumDeviceCategory.MOBILE, "Nokia"), // 
	SONYERICSSON(2, EnumDeviceCategory.MOBILE, "SonyEricsson"), //
	MOTOROLA(3, EnumDeviceCategory.MOBILE, "MOT-"), //
	SAMSUNG(4, EnumDeviceCategory.MOBILE, "Samsung"), //
	LG(5, EnumDeviceCategory.MOBILE, "LG"), //
	DOPOD(6, EnumDeviceCategory.MOBILE, "Dopod"), //
	BLACKBERRY(7, EnumDeviceCategory.MOBILE, "BlackBerry"), //
	LENOVO(8, EnumDeviceCategory.MOBILE, "Lenovo"), //
	ZTE(9, EnumDeviceCategory.MOBILE, "ZTE"), //
	AMOI(10, EnumDeviceCategory.MOBILE, "AMOI"), //
	BIRD(11, EnumDeviceCategory.MOBILE, "BIRD"), //
	NEC(12, EnumDeviceCategory.MOBILE, "NEC"), //
	PANASONIC(13, EnumDeviceCategory.MOBILE, "Panasonic"), //
	TCL(14, EnumDeviceCategory.MOBILE, "TCL"), //
	TIANYU(15, EnumDeviceCategory.MOBILE, "TIANYU"), //
	PHILIPS(16, EnumDeviceCategory.MOBILE, "Philips"), //
	SMARTPHONE(17, EnumDeviceCategory.MOBILE, "SmartPhone"), //
	IPHONE(18, EnumDeviceCategory.MOBILE, "iPhone"), //
	MAC(19, EnumDeviceCategory.COMPUTER, "Macintosh");

	static String REGEXP = "";

	int id;
	String key;
	String upperKey;
	EnumDeviceCategory category;
	Pattern pattern;

	EnumDevice(int id, EnumDeviceCategory category, String key) {
		this.key = key;
		this.upperKey = key.toUpperCase();
		this.category = category;
		this.id = id;
		pattern = Pattern
				.compile(
						"(?:^| |\\()"
								+ key
								+ " ?([^\\s;/\\(\\),]*)(?:$|(?:/([^\\s;\\(\\),]*)[,; \\t]*)?(?:\\((.*?)\\))?)",
						Pattern.CASE_INSENSITIVE);
	}

	EnumDevice(int id, EnumDeviceCategory category, String key, String regExp) {
		this.key = key;
		this.category = category;
		this.id = id;
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

	public static EnumDevice fromId(int id) {
		for (EnumDevice t : EnumDevice.values()) {
			if (t.getId() == id)
				return t;
		}
		return UNKNOW;
	}
}
