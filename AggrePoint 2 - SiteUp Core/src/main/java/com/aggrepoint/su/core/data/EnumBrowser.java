package com.aggrepoint.su.core.data;

import java.util.regex.Pattern;

public enum EnumBrowser {
	UNKNOW(0, EnumDeviceCategory.UNKNOW, "UNKNOW"), //
	IE_MOBILE(1, EnumDeviceCategory.MOBILE, "IEMobile"), //
	OPERA_MINI(2, EnumDeviceCategory.MOBILE, "Opera Mini"), //
	UCWEB(3, EnumDeviceCategory.MOBILE, "UCWEB"), //
	CHROME(33, EnumDeviceCategory.UNKNOW, "Chrome"), //
	SAFARI(4, EnumDeviceCategory.UNKNOW, "Safari"), //
	NETFRONT(5, EnumDeviceCategory.UNKNOW, "NetFront"), //
	SEMC_JAVA(6, EnumDeviceCategory.MOBILE, "SEMC-Java"), //
	SEMC_BROWSER(7, EnumDeviceCategory.MOBILE, "SEMC-Browser"), //
	SEMC_SW(8, EnumDeviceCategory.MOBILE, "SEMC-SW"), //
	TELECA(9, EnumDeviceCategory.MOBILE, "Teleca"), //
	KONQUEROR(10, EnumDeviceCategory.UNKNOW, "Konqueror"), //
	OPERA(11, EnumDeviceCategory.UNKNOW, "Opera"), //
	FIREFOX(12, EnumDeviceCategory.UNKNOW, "Firefox"), //
	JAVA(13, EnumDeviceCategory.UNKNOW, "Java"), // SE Cell phone sometimes
	// bring the Java version in
	// agent cause fault result
	JAVA_JAKARTA(14, EnumDeviceCategory.UNKNOW, "Jakarta Commons-HttpClient"), //
	GOOGLE(15, EnumDeviceCategory.SPIDER, "Googlebot"), //
	BAIDU(16, EnumDeviceCategory.SPIDER, "Baiduspider"), //
	BAIDUP(17, EnumDeviceCategory.SPIDER, "Baiduspider+"), //
	YAHOO(18, EnumDeviceCategory.SPIDER, "Yahoo! Slurp"), //
	SOGOU(19, EnumDeviceCategory.SPIDER, "SOGOU",
			"Sogou (?:\\S+ )?spider(?: |/)?([^\\s;/\\(\\),]*)"), //
	YODAO(20, EnumDeviceCategory.SPIDER, "YodaoBot"), //
	Giga(21, EnumDeviceCategory.SPIDER, "Gigabot"), //
	IASK(22, EnumDeviceCategory.SPIDER, "iaskspider"), //
	EnaBot(23, EnumDeviceCategory.SPIDER, "EnaBot"), //
	GAISBOT(24, EnumDeviceCategory.SPIDER, "Gaisbot"), //
	MSRBOT(25, EnumDeviceCategory.SPIDER, "MSRBOT"), //
	QIHOOBOT(26, EnumDeviceCategory.SPIDER, "QihooBot"), //
	MSNBOT(27, EnumDeviceCategory.SPIDER, "msnbot"), //
	PSBOT(28, EnumDeviceCategory.SPIDER, "psbot"), //
	SOSO(29, EnumDeviceCategory.SPIDER, "soso"), //
	CAZOODLEBOT(30, EnumDeviceCategory.SPIDER, "CazoodleBot"), //
	IE(31, EnumDeviceCategory.UNKNOW, "MSIE"), //
	MOZILLA(32, EnumDeviceCategory.UNKNOW, "Mozilla");

	String key;
	String upperKey;
	EnumDeviceCategory category;
	int id;
	Pattern pattern;

	EnumBrowser(int id, EnumDeviceCategory category, String key) {
		this.key = key.toUpperCase();
		this.upperKey = key.toUpperCase();
		this.category = category;
		this.id = id;
		pattern = Pattern.compile("(?:^| |Browser/|/)" + key
				+ "(?: |/)?([^\\s;/\\(\\),]*)", Pattern.CASE_INSENSITIVE);
	}

	EnumBrowser(int id, EnumDeviceCategory category, String key, String regExp) {
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

	public static EnumBrowser fromId(int id) {
		for (EnumBrowser t : EnumBrowser.values()) {
			if (t.getId() == id)
				return t;
		}
		return UNKNOW;
	}
}
