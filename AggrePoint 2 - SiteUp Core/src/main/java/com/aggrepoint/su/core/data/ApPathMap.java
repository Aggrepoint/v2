package com.aggrepoint.su.core.data;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.common.CombineString;

/**
 * 
 * @author YJM
 */
public class ApPathMap extends ADB {
	public static final int STATUS_ENABLE = 1;

	public static final int STATUS_DISABLE = 2;

	public long m_lMapID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;

	public long m_lBranchID;

	public int m_iStatusID;

	/** 用于匹配当前路径，判断是否需要进行映射 */
	public String m_strFromPath;

	/** 加上分支根路径 */
	public String m_strFromPathFull;

	Pattern m_patternFromPath;

	/** 映射为的实际路径 */
	public String m_strToPath;

	/** 加上分支根路径 */
	public String m_strToPathFull;

	public String m_strParamName1;

	public String m_strParamName2;

	public String m_strParamName3;

	public String m_strParamName4;

	public String m_strParamName5;

	/** 用于构造链接，符合该模式的链接需要修改为m_strToLink的模式 */
	public String m_strFromLink;

	/** 加上分支根路径 */
	public String m_strFromLinkFull;

	Pattern m_patternFromLink;

	/** 用于构造链接 */
	public String m_strToLink;

	/** 加上分支根路径 */
	public String m_strToLinkFull;

	public String m_strToLinkDirect;

	public String m_strUUID;

	static Hashtable<Long, ADBList<ApPathMap>> m_htCaches = new Hashtable<Long, ADBList<ApPathMap>>();

	public String m_strRootPath;

	public ApPathMap() {
		m_strFromPath = m_strToPath = m_strFromLink = m_strToLink = m_strParamName1 = m_strParamName2 = m_strParamName3 = m_strParamName4 = m_strParamName5 = "";
	}

	/**
	 * 设置映射所属分支的根目录
	 * 
	 * @param path
	 *            以/开头，不以/结尾
	 */
	public void setRootPath(String path) {
		if (m_strRootPath == null || !m_strRootPath.equals(path)) {
			m_strRootPath = path;
			m_patternFromPath = null;
			m_patternFromLink = null;

			m_strFromPathFull = path + "/" + m_strFromPath;
			m_strToPathFull = path + "/" + m_strToPath;
			m_strFromLinkFull = path + "/" + m_strFromLink;
			m_strToLinkFull = path + "/" + m_strToLink;
			m_strToLinkDirect = "/" + m_strToLink;
		}
	}

	public Pattern getFromPathPattern() {
		if (m_patternFromPath == null)
			m_patternFromPath = Pattern.compile(m_strFromPathFull);
		return m_patternFromPath;
	}

	public Pattern getFromLinkPattern() {
		if (m_patternFromLink == null)
			m_patternFromLink = Pattern.compile(m_strFromLinkFull);
		return m_patternFromLink;
	}

	/**
	 * 用Pattern匹配字符串，返回匹配出来的group
	 * 
	 * @param p
	 * @param str
	 * @return
	 */
	public static String[] executePattern(Pattern p, String str) {
		Matcher matcher = p.matcher(str);
		if (!matcher.find())
			return null;
		String[] group = new String[matcher.groupCount()];
		for (int i = 0; i < group.length; i++)
			group[i] = matcher.group(i + 1);
		return group;
	}

	/**
	 * 映射路径
	 * 
	 * @param path
	 * @param pathGroup
	 * @return
	 */
	public String translatePath(String[] pathGroup) {
		char[] chars = m_strToPathFull.toCharArray();
		char[] digits = new char[10];
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				int j = 0;
				for (i++; i < chars.length && Character.isDigit(chars[i]); i++, j++)
					digits[j] = chars[i];
				sb.append(pathGroup[Integer.parseInt(new String(digits, 0, j)) - 1]);
			} else
				sb.append(chars[i]);
		}
		return sb.toString();
	}

	public String constructParams(String[] pathGroup) {
		Vector<String> vec = new Vector<String>();
		if (m_strParamName1 != null && !m_strParamName1.equals("")
				&& pathGroup.length > 1) {
			vec.add(m_strParamName1);
			vec.add(pathGroup[0]);
		}
		if (m_strParamName2 != null && !m_strParamName2.equals("")
				&& pathGroup.length > 2) {
			vec.add(m_strParamName2);
			vec.add(pathGroup[1]);
		}
		if (m_strParamName3 != null && !m_strParamName3.equals("")
				&& pathGroup.length > 3) {
			vec.add(m_strParamName3);
			vec.add(pathGroup[2]);
		}
		if (m_strParamName4 != null && !m_strParamName4.equals("")
				&& pathGroup.length > 4) {
			vec.add(m_strParamName4);
			vec.add(pathGroup[3]);
		}
		if (m_strParamName5 != null && !m_strParamName5.equals("")
				&& pathGroup.length > 5) {
			vec.add(m_strParamName5);
			vec.add(pathGroup[4]);
		}
		return CombineString.combine(vec, '~');
	}

	/**
	 * 映射路径反向链接
	 * 
	 * @param path
	 * @param pathGroup
	 * @return
	 */
	public String translateLink(String[] pathGroup, String[] linkGroup,
			boolean direct) {
		char[] chars;
		if (direct)
			chars = m_strToLinkDirect.toCharArray();
		else
			chars = m_strToLinkFull.toCharArray();

		char[] digits = new char[10];
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '[') {
				int j = 0;
				for (i++; i < chars.length && Character.isDigit(chars[i]); i++, j++)
					digits[j] = chars[i];
				sb.append(pathGroup[Integer.parseInt(new String(digits, 0, j)) - 1]);
			} else if (chars[i] == '{') {
				int j = 0;
				for (i++; i < chars.length && Character.isDigit(chars[i]); i++, j++)
					digits[j] = chars[i];
				sb.append(linkGroup[Integer.parseInt(new String(digits, 0, j)) - 1]);
			} else
				sb.append(chars[i]);
		}
		return sb.toString();
	}

	/**
	 * 从Cache中获取定义在某个站点上的映射列表
	 * 
	 * @param adapter
	 * @param siteID
	 * @return
	 * @throws Exception
	 */
	static public Vector<ApPathMap> getPathMaps(AdbAdapter adapter,
			long branchID) throws Exception {
		Long id = new Long(branchID);
		ADBList<ApPathMap> list = m_htCaches.get(new Long(branchID));
		if (list == null) {
			ApPathMap map = new ApPathMap();
			map.m_lBranchID = branchID;
			list = adapter.retrieveMultiDbl(map, "loadByBranch", "order");
			m_htCaches.put(id, list);
		} else {
			adapter.syncList(list, false);
		}

		return list.m_vecObjects;
	}
}
