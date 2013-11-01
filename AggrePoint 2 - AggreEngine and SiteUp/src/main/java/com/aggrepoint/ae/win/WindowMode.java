package com.aggrepoint.ae.win;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.ae.data.SessionConst;
import com.aggrepoint.su.core.data.UserWinMode;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.common.TypeCast;

/**
 * 窗口模式
 * 
 * 为了节省空间和提高运行速度，直接用数组方式保存每个页面中的窗口模式
 * 
 * @author YJM
 */
public class WindowMode implements SessionConst {
	static final String SESSION_KEY_WIN_MODE = "com.aggrepoint.ae.winmode";

	static final String SESSION_KEY_MAX_WIN = "com.aggrepoint.ae.maxwin";

	static final int MAX_WIN_IN_A_PAGE = 30;

	/**
	 * 获取指定页面、指定栏目中最大化窗口的编号
	 * 
	 * @param session
	 * @param pageid
	 * @param areaName
	 * @return -1表示没有最大化窗口
	 */
	public static long getMaxWin(HttpSession session, long pageid,
			String areaName) {
		Hashtable<String, Long> htMaxWin = TypeCast.cast(session
				.getAttribute(SESSION_KEY_MAX_WIN));
		if (htMaxWin == null) {
			htMaxWin = new Hashtable<String, Long>();
			session.setAttribute(SESSION_KEY_MAX_WIN, htMaxWin);
		}

		Long l = htMaxWin.get(Long.toString(pageid) + areaName);
		if (l == null)
			return -1;

		return l.longValue();
	}

	/**
	 * 设置指定页面、指定栏目中最大化窗口
	 * 
	 * @param session
	 * @param pageid
	 * @param areaName
	 * @param iid
	 *            最大化窗口的编号，为-1表示取消最大化窗口
	 */
	public static void setMaxWin(HttpSession session, long pageid,
			String areaName, long iid) {
		Hashtable<String, Long> htMaxWin = TypeCast.cast(session
				.getAttribute(SESSION_KEY_MAX_WIN));
		if (htMaxWin == null) {
			htMaxWin = new Hashtable<String, Long>();
			session.setAttribute(SESSION_KEY_MAX_WIN, htMaxWin);
		}

		if (iid == -1)
			htMaxWin.remove(Long.toString(pageid) + areaName);
		else
			htMaxWin.put(Long.toString(pageid) + areaName, new Long(iid));
	}

	/**
	 * 获取指定页面的窗口状态对象
	 * 
	 * @param session
	 * @param pageid
	 * @return
	 */
	static Object[] getPageMode(HttpSession session, long pageid) {
		Hashtable<Long, Object[]> htAll = TypeCast.cast(session
				.getAttribute(SESSION_KEY_WIN_MODE));

		if (htAll == null) {
			htAll = new Hashtable<Long, Object[]>();
			session.setAttribute(SESSION_KEY_WIN_MODE, htAll);
		}

		Long key = new Long(pageid);

		Object[] pageState = htAll.get(key);
		if (pageState == null) {
			pageState = new Object[] { new short[] { 0 }, // 窗口个数
					new long[MAX_WIN_IN_A_PAGE], // 窗口实例编号
					new EnumWinMode[MAX_WIN_IN_A_PAGE], // 窗口模式
					new String[MAX_WIN_IN_A_PAGE] }; // 栏位名称
			htAll.put(key, pageState);
		}

		return pageState;
	}

	/**
	 * 获取指定页面内窗口的模式
	 */
	public static EnumWinMode getMode(HttpSession session, long pageid,
			String areaName, long iid) {
		long lMaxIID = getMaxWin(session, pageid, areaName);
		if (lMaxIID == iid)
			return EnumWinMode.MAX;
		else if (lMaxIID > 0)
			return EnumWinMode.HIDE;

		Object[] pageState = getPageMode(session, pageid);

		synchronized (pageState) {
			short[] len = (short[]) pageState[0];
			long[] arrIIDs = (long[]) pageState[1];
			EnumWinMode[] arrStatus = (EnumWinMode[]) pageState[2];
			String[] arrAreas = (String[]) pageState[3];

			for (int i = 0; i < len[0]; i++)
				if (arrIIDs[i] == iid)
					return arrStatus[i];

			if (len[0] < MAX_WIN_IN_A_PAGE) {
				arrIIDs[len[0]] = iid;
				arrStatus[len[0]] = EnumWinMode.NORMAL;
				arrAreas[len[0]] = areaName;
				len[0]++;
			}

			return EnumWinMode.NORMAL;
		}
	}

	/**
	 * 从持久会话中取出窗口状态
	 * 
	 * @param adapter
	 * @param sid
	 */
	public static void retrievePersistanceMode(AdbAdapter adapter,
			HttpSession session, String uid) {
		UserWinMode wmode = new UserWinMode();
		wmode.userID = uid;
		try {
			for (Enumeration<UserWinMode> enu = adapter.retrieveMulti(wmode,
					"loadByUser", null).elements(); enu.hasMoreElements();) {
				wmode = enu.nextElement();
				setMode(adapter, session, uid, wmode.pid, wmode.areaName,
						wmode.iid, EnumWinMode.fromId(wmode.mode), false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将窗口状态保存到用户持久会话
	 * 
	 * @param adapter
	 * @param session
	 * @param pageid
	 * @param areaName
	 * @param iid
	 * @param stat
	 */
	static void saveModeToPersistance(AdbAdapter adapter, String uid,
			long pageid, String areaName, long iid, EnumWinMode mode) {
		if (uid == null || uid.equals(""))
			return;

		UserWinMode wmode = new UserWinMode();
		wmode.userID = uid;
		wmode.pid = pageid;
		wmode.areaName = areaName;
		wmode.iid = iid;
		wmode.mode = mode.getId();
		try {
			if (adapter.update(wmode) == 0)
				adapter.create(wmode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置指定页面内窗口的模式
	 */
	static EnumWinMode setMode(AdbAdapter adapter, HttpSession session,
			String uid, long pageid, String areaName, long iid,
			EnumWinMode newMode, boolean persistance) {
		newMode = newMode.getRealMode();

		if (newMode == EnumWinMode.MAX) {
			setMaxWin(session, pageid, areaName, iid);
			return newMode;
		}

		long lMaxIID = getMaxWin(session, pageid, areaName);
		if (lMaxIID == iid && newMode != EnumWinMode.MAX)
			setMaxWin(session, pageid, areaName, -1);

		Object[] pageState = getPageMode(session, pageid);

		synchronized (pageState) {
			short[] len = (short[]) pageState[0];
			long[] arrIIDs = (long[]) pageState[1];
			EnumWinMode[] arrStatus = (EnumWinMode[]) pageState[2];
			String[] arrAreas = (String[]) pageState[3];
			int i;

			for (i = 0; i < len[0]; i++)
				if (arrIIDs[i] == iid) {
					arrStatus[i] = newMode;
					break;
				}

			if (i >= len[0]) {
				if (len[0] < MAX_WIN_IN_A_PAGE) {
					arrIIDs[len[0]] = iid;
					arrStatus[len[0]] = newMode;
					arrAreas[len[0]] = areaName;
					len[0]++;
				}
			}

			// 将窗口状态保存到持久会话
			if (persistance)
				saveModeToPersistance(adapter, uid, pageid, areaName, iid,
						newMode);
		}

		return newMode;
	}

	public static EnumWinMode setMode(AdbAdapter adapter, HttpSession session,
			String uid, long pageid, String areaName, long iid,
			EnumWinMode newMode) {
		return setMode(adapter, session, uid, pageid, areaName, iid, newMode,
				true);
	}

	static String getAreaName(HttpSession session, long pageid, long iid) {
		Object[] pageState = getPageMode(session, pageid);
		synchronized (pageState) {
			short[] len = (short[]) pageState[0];
			long[] arrIIDs = (long[]) pageState[1];
			String[] arrAreas = (String[]) pageState[3];

			for (int i = 0; i < len[0]; i++) {
				if (arrIIDs[i] == iid)
					return arrAreas[i];
			}
		}
		return null;
	}

	/**
	 * 获取指定页面内窗口的模式
	 */
	public static EnumWinMode getMode(HttpSession session, long pageid, long iid) {
		String areaName = getAreaName(session, pageid, iid);
		if (areaName == null)
			return EnumWinMode.NORMAL;
		return getMode(session, pageid, areaName, iid);
	}

	/**
	 * 设置指定页面内窗口的模式
	 */
	public static boolean setMode(AdbAdapter adapter, HttpSession session,
			String uid, long pageid, long iid, EnumWinMode newMode) {
		String areaName = getAreaName(session, pageid, iid);
		if (areaName == null)
			return false;

		setMode(adapter, session, uid, pageid, areaName, iid, newMode);
		return true;
	}
}
