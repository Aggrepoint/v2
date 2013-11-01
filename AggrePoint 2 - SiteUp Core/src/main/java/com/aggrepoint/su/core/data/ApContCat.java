package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 
 * @author YJM
 */
public class ApContCat extends ADB {
	public long m_lContCatID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lSiteID;
	public String m_strContCatName;
	public String m_strContCatDesc;
	public String m_strUUID;
	public Vector<ApContent> m_vecContents;

	public ApContCat() throws AdbException {
		m_strContCatName = m_strContCatDesc = m_strUUID = "";
	}

	public long getId() {
		return m_lContCatID;
	}

	public String getName() {
		return m_strContCatName;
	}

	public String getDesc() {
		return m_strContCatDesc;
	}

	public Vector<ApContent> getContents() {
		if (m_vecContents == null)
			m_vecContents = new Vector<ApContent>();
		return m_vecContents;
	}

	public void setContents(Vector<ApContent> vec) {
	}

	public void delete(DbAdapter adapter) throws Exception {
		ApContent c = new ApContent();
		c.m_lContCatID = this.m_lContCatID;
		c.m_iOfficialFlag = this.m_iOfficialFlag;

		for (ApContent cont : adapter.retrieveMulti(c, "loadByCat", null))
			adapter.proc(cont, "delete");

		adapter.delete(this);
	}
}
