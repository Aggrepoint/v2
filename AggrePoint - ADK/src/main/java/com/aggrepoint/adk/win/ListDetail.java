package com.aggrepoint.adk.win;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.Winlet;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

public class ListDetail extends Winlet {
	private static final long serialVersionUID = 1L;

	protected ADB m_adb;
	protected String m_strDefaultSort;
	protected String m_strListMethod;
	protected String m_strIdKey;
	protected int m_iNormalPageNum;
	protected int m_iMaxPageNum;

	public void reset() {
		m_adb.setCommonData(ICommDataKey.SORT, m_strDefaultSort);
		m_adb.setCommonData(ICommDataKey.PAGE, "1");
		setId(-1);
	}

	public long getId() {
		return -1;
	}

	public void setId(long id) {
	}

	public ListDetail(ADB adb, String defaultSort, String list, String idkey,
			int normalPageNum, int maxPageNum) throws AdbException {
		m_adb = adb;
		m_strDefaultSort = defaultSort;
		m_strListMethod = list;
		m_strIdKey = idkey;
		m_iNormalPageNum = normalPageNum;
		m_iMaxPageNum = maxPageNum;

		reset();
	}

	public ListDetail(ADB adb, String defaultSort, String list, String idkey)
			throws AdbException {
		this(adb, defaultSort, list, idkey, 10, 100);
	}

	public int showList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		req.setAttribute(
				"LIST",
				new DbAdapter(req.getDBConn())
						.retrieveMultiDbl(
								m_adb,
								m_strListMethod,
								null,
								getWinMode(req) == EnumWinMode.NORMAL ? m_iNormalPageNum
										: m_iMaxPageNum, -1, true));

		return 0;
	}

	public int search(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_adb.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_adb.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int select(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		setId(req.getParameter(m_strIdKey, -1));
		return 0;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int checkShowDetail(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (getId() == -1)
			return 8000;

		return 0;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int showDetail(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (getId() == -1)
			return 8000;

		req.setAttribute("INFO", new DbAdapter(req.getDBConn()).retrieve(m_adb));
		return 0;
	}
}
