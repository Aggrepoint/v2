package com.aggrepoint.su.core.data;

public class ApResUI {
	public static String getDisplayFilename(ApRes res) {
		StringBuffer sb = new StringBuffer();
		sb.append("<a target='_blank' href='/ap2/res/res/" + res.m_lResID
				+ "/dl'>");
		sb.append(res.m_strFileName);
		sb.append("</a>");

		return sb.toString();
	}

	public static String getDisplaySmallIcon(ApRes res) {
		switch (res.m_iFileType) {
		case ApRes.TYPE_IMAGE:
			return ("<img src='/ap2/res/res/" + res.m_lResID + "/small'/>");
		case ApRes.TYPE_FLASH:
			return ("<img src='/ap2/su/images/flash.png'/>");
		case ApRes.TYPE_CSS:
			return ("<img src='/ap2/su/images/css.png'/>");
		case ApRes.TYPE_JS:
			return ("<img src='/ap2/su/images/js.png'/>");
		}

		return "";
	}

	public static String getDisplaySize(ApRes res) {
		if (res.m_lSize == 0)
			return "0";

		int kb = (int) (res.m_lSize / 1024);

		if (kb > 0)
			return kb + "kb";

		return res.m_lSize + "b";
	}

	public static String getDisplayWidth(ApRes res) {
		if (res.m_iFileType == ApRes.TYPE_IMAGE)
			return Integer.toString(res.m_iWidth);
		return "";
	}

	public static String getDisplayHeight(ApRes res) {
		if (res.m_iFileType == ApRes.TYPE_IMAGE)
			return Integer.toString(res.m_iHeight);
		return "";
	}
}
