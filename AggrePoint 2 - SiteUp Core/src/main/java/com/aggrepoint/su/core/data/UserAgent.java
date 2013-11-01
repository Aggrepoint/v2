package com.aggrepoint.su.core.data;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.plugin.WinletUserAgent;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

public class UserAgent extends ADB {
	public long agentID;

	/** 用户代理请求头 */
	public String userAgent;
	/** 大写版userAgent */
	public String uua;
	/** 接受内容类型请求头 */
	public String accept;

	/** 设备类型 */
	public EnumDevice deviceType = EnumDevice.UNKNOW;
	/** 设备型号 */
	public String deviceModel;
	/** 设备版本 */
	public String deviceVersion;
	/** 设备主版本号 */
	public int deviceMajorVersion;
	/** 设备次版本号 */
	public int deviceMinorVersion;

	/** 操作系统类型 */
	public EnumOS osType = EnumOS.UNKNOW;
	public String osVersion;
	public int osMajorVersion;
	public int osMinorVersion;

	/** 浏览器类型 */
	public EnumBrowser browserType = EnumBrowser.UNKNOW;
	public String browserVersion;
	public int browserMajorVersion;
	public int browserMinorVersion;

	/** 是否支持HTML */
	public boolean supportHTML;
	/** 是否支持XHTML */
	public boolean supportXHTML;
	/** 是否支持WML */
	public boolean supportWML;
	/** 若支持XHTML, XHTML的内容类型 */
	public String xhtmlType;
	/** 缺省使用的标记语言 */
	public int defaultMarkup;

	/** 是否移动设备 */
	public boolean isMobile;
	/** 是否Spider */
	public boolean isSpider;
	/** 是否支持Ajax */
	public boolean supportAjax;
	/** 若该记录是在AE运行过程中自动创建的，并且未经过人工确认，则标记为true */
	public boolean autoFlag;

	private static Pattern patternVersion = Pattern
			.compile("(\\d*)(?:.(\\d*))?");

	/**
	 * 缺省UserAgent，只支持HTML
	 */
	public UserAgent() {
		userAgent = uua = accept = "";

		deviceModel = deviceVersion = osVersion = browserVersion = xhtmlType = "";
		supportHTML = true;
		autoFlag = true;
	}

	private UserAgent(String ua, String accept) {
		this();

		if (ua == null)
			ua = "";
		if (accept == null)
			accept = "";

		userAgent = ua;
		uua = ua.toUpperCase();
		this.accept = accept;

	}

	public int getDeviceTypeID() {
		return deviceType.getId();
	}

	public void setDeviceTypeID(int id) {
		deviceType = EnumDevice.fromId(id);
	}

	public int getOSTypeID() {
		return osType.getId();
	}

	public void setOSTypeID(int id) {
		osType = EnumOS.fromId(id);
	}

	public int getBrowserTypeID() {
		return browserType.getId();
	}

	public void setBrowserTypeID(int id) {
		browserType = EnumBrowser.fromId(id);
	}

	static int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public EnumMarkup getDefaultMarkup() {
		return EnumMarkup.fromId(defaultMarkup);
	}

	public void setUA(String ua) {
		userAgent = ua;
		if (ua != null)
			uua = userAgent.toUpperCase();
	}

	void determineDevice() {
		deviceType = EnumDevice.UNKNOW;

		if (userAgent == null)
			return;

		Matcher matcher;

		for (EnumDevice b : EnumDevice.values()) {
			if (b.getId() <= 0)
				continue;

			if (b.getPattern() != null && uua.indexOf(b.getUpperKey()) > -1) {
				matcher = b.getPattern().matcher(userAgent);
				if (matcher.find()) {
					deviceType = b;

					try {
						deviceModel = matcher.group(1);
						deviceVersion = matcher.group(2);
						if (deviceVersion != null) {
							matcher = patternVersion.matcher(deviceVersion);
							if (matcher.find()) {
								deviceMajorVersion = getInt(matcher.group(1));
								deviceMinorVersion = getInt(matcher.group(2));
							}
						}
					} catch (Exception e) {
					}

					return;
				}
			}
		}
	}

	void determineOS() {
		osType = EnumOS.UNKNOW;

		if (userAgent == null)
			return;

		Matcher matcher;

		for (EnumOS b : EnumOS.values()) {
			if (b.getId() <= 0)
				continue;

			if (b.getPattern() != null && uua.indexOf(b.getUpperKey()) > -1) {
				matcher = b.getPattern().matcher(userAgent);
				if (matcher.find()) {
					osType = b;

					try {
						osVersion = matcher.group(1);
						if (osVersion != null) {
							matcher = patternVersion.matcher(osVersion);
							if (matcher.find()) {
								osMajorVersion = getInt(matcher.group(1));
								osMinorVersion = getInt(matcher.group(2));
							}
						}
					} catch (Exception e) {
					}

					return;
				}
			}
		}
	}

	void determineBrowser() {
		browserType = EnumBrowser.UNKNOW;

		if (userAgent == null)
			return;

		Matcher matcher;

		for (EnumBrowser b : EnumBrowser.values()) {
			if (b.getId() <= 0)
				continue;

			if (b.getPattern() != null && uua.indexOf(b.getUpperKey()) > -1) {
				matcher = b.getPattern().matcher(userAgent);
				if (matcher.find()) {
					browserType = b;

					try {
						browserVersion = matcher.group(1);
						if (browserVersion != null) {
							matcher = patternVersion.matcher(browserVersion);
							if (matcher.find()) {
								browserMajorVersion = getInt(matcher.group(1));
								browserMinorVersion = getInt(matcher.group(2));
							}
						}
					} catch (Exception e) {
					}

					return;
				}
			}
		}
	}

	public void determine() {
		determineDevice();
		determineOS();
		determineBrowser();

		String[] accepts = accept.split("\\s*,\\s*");

		if (accepts != null) {
			EnumMarkup currentDefault = EnumMarkup.fromId(defaultMarkup);
			defaultMarkup = EnumMarkup.UNDEFINED.getId();
			for (int i = 0; i < accepts.length; i++)
				if (accepts[i].equals("*/*") || accepts[i].equals("text/html")) {
					supportHTML = true;
					if (defaultMarkup == EnumMarkup.UNDEFINED.getId())
						defaultMarkup = EnumMarkup.HTML.getId();
				} else if (accepts[i].indexOf("text/vnd.wap.wml") >= 0) {
					supportWML = true;
					if (defaultMarkup == EnumMarkup.UNDEFINED.getId())
						defaultMarkup = EnumMarkup.WML.getId();
				} else if (accepts[i].indexOf("application/vnd.wap.xhtml+xml") >= 0
						|| accepts[i].indexOf("application/xhtml+xml") >= 0) {
					supportXHTML = true;
					if (defaultMarkup == EnumMarkup.UNDEFINED.getId())
						defaultMarkup = EnumMarkup.XHTML.getId();
					if (xhtmlType == null || xhtmlType.equals(""))
						xhtmlType = accepts[i];
				}

			if (defaultMarkup == EnumMarkup.UNDEFINED.getId())
				defaultMarkup = currentDefault.getId();
		}

		// Chrome Browser put XHTML support before HTML, adjust
		if (browserType == EnumBrowser.CHROME && supportHTML)
			defaultMarkup = EnumMarkup.HTML.getId();

		isMobile = deviceType.getCategory() == EnumDeviceCategory.MOBILE
				|| osType.getCategory() == EnumDeviceCategory.MOBILE
				|| browserType.getCategory() == EnumDeviceCategory.MOBILE;

		// Mobile use XHTML insteand of HTML
		if (isMobile && defaultMarkup == EnumMarkup.HTML.getId()
				&& supportXHTML)
			defaultMarkup = EnumMarkup.XHTML.getId();

		isSpider = browserType.getCategory() == EnumDeviceCategory.SPIDER;

		supportAjax = !isMobile
				&& (browserType == EnumBrowser.IE && browserMajorVersion >= 5
						|| browserType == EnumBrowser.FIREFOX
						&& browserMajorVersion >= 2
						|| browserType == EnumBrowser.CHROME
						|| browserType == EnumBrowser.OPERA
						&& browserMajorVersion >= 8 || browserType == EnumBrowser.KONQUEROR
						&& browserMajorVersion >= 3);
	}

	public static UserAgent getUserAgent(AdbAdapter adapter, String uastr,
			String accept) throws Exception {
		UserAgent ua = new UserAgent(uastr, accept);

		// 从数据库中查找是否已存在对应的记录
		if (adapter.retrieve(ua, "loadByUA") != null)
			return ua;

		// 自动识别
		ua.determine();
		if (ua.userAgent != null && !ua.userAgent.equals(""))
			adapter.create(ua);

		return ua;
	}

	/**
	 * 转为可以由AE传递给后端应用的字符串
	 * 
	 * @return
	 */
	public String serialize() throws UnsupportedEncodingException {
		WinletUserAgent agent = new WinletUserAgent();
		if (supportAjax)
			agent.setProperty(WinletUserAgent.PROPERTY_SUPPORT_AJAX,
					WinletUserAgent.BOOLEAN_TRUE);
		if (supportHTML)
			agent.setProperty(WinletUserAgent.PROPERTY_SUPPORT_HTML,
					WinletUserAgent.BOOLEAN_TRUE);
		if (supportWML)
			agent.setProperty(WinletUserAgent.PROPERTY_SUPPORT_WML,
					WinletUserAgent.BOOLEAN_TRUE);
		if (supportXHTML)
			agent.setProperty(WinletUserAgent.PROPERTY_SUPPORT_XHTML,
					WinletUserAgent.BOOLEAN_TRUE);
		if (isSpider)
			agent.setProperty(WinletUserAgent.PROPERTY_IS_SPIDER,
					WinletUserAgent.BOOLEAN_TRUE);
		if (isMobile)
			agent.setProperty(WinletUserAgent.PROPERTY_IS_MOBILE,
					WinletUserAgent.BOOLEAN_TRUE);
		agent.setProperty(WinletUserAgent.PROPERTY_DEFAULT_MARKUP,
				getDefaultMarkup().getName());
		return agent.serialize();
	}

	public void dump(PrintStream ps) {
		ps.println(userAgent);

		ps.print("\t");
		ps.println("Device");
		ps.print("\t\t");
		ps.print(deviceType.getKey());
		ps.print(" ");
		ps.print(deviceModel);
		ps.print(" ");
		ps.print(deviceVersion);
		ps.print(" ");
		ps.print(deviceMajorVersion);
		ps.print(" ");
		ps.println(deviceMinorVersion);

		ps.print("\t");
		ps.println("OS");
		ps.print("\t\t");
		ps.print(osType.getKey());
		ps.print(" ");
		ps.print(osVersion);
		ps.print(" ");
		ps.print(osMajorVersion);
		ps.print(" ");
		ps.println(osMinorVersion);

		ps.print("\t");
		ps.println("Browser");
		ps.print("\t\t");
		ps.print(browserType.getKey());
		ps.print(" ");
		ps.print(browserVersion);
		ps.print(" ");
		ps.print(browserMajorVersion);
		ps.print(" ");
		ps.println(browserMinorVersion);

		ps.print("\t");
		if (isMobile)
			ps.print("!!!MOBILE!!!");
		if (supportAjax)
			ps.print("!!!AJAX!!!");
		if (isSpider)
			ps.print("!!!SPIDER!!!");
		ps.println();
	}

	boolean same(UserAgent with, StringBuffer diff) {
		boolean bChanged = false;
		if (osType != with.osType) {
			bChanged = true;
			diff.append("osType from " + osType.toString() + " to "
					+ with.osType.toString() + "\r\n");
		}
		if (!osVersion.equals(with.osVersion)) {
			bChanged = true;
			diff.append("osVersion from " + osVersion + " to " + with.osVersion
					+ "\r\n");
		}
		if (browserType != with.browserType) {
			bChanged = true;
			diff.append("browserType from " + browserType.toString() + " to "
					+ with.browserType.toString() + "\r\n");
		}
		if (defaultMarkup != with.defaultMarkup) {
			bChanged = true;
			diff.append("defaultMarkup from " + defaultMarkup + " to "
					+ with.defaultMarkup + "\r\n");
		}
		return !bChanged;
	}

	public UserAgent clone() {
		UserAgent ua = new UserAgent();
		ua.osType = osType;
		ua.osVersion = osVersion;
		ua.browserType = browserType;
		ua.defaultMarkup = defaultMarkup;
		return ua;
	}

	/**
	 * 更新数据库中已经存在的记录
	 */
	public static void main(String args[]) {
		// UserAgent ua = new UserAgent(
		// "Mozilla/4.0 (compatible; MSIE 4.01; Windows CE; PPC)/UCWEB7.0.0.33/31/6500",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8, dn/642819591-4035db97,text/vnd.wap.wml;q=0.6,ss/447x748");
		// ua.determine();

		try {
			DbAdapter adapter = new DbAdapter(
					DBConnManager.getConnection(args[0]));
			for (UserAgent ua : adapter.retrieveMulti(new UserAgent())) {
				UserAgent original = ua.clone();
				StringBuffer diff = new StringBuffer();
				ua.determine();
				if (!original.same(ua, diff)) {
					System.out.println("------------------------------");
					System.out.println(ua.userAgent);
					System.out.println(ua.accept);
					System.out.println(diff.toString());

					adapter.update(ua);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
