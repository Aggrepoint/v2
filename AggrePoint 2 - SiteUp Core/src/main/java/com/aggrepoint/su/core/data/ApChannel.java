package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApChannel extends ADB implements RuleConst {
	public long m_lChannelID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lSiteID;
	public boolean m_bPsnFlag;
	public String m_strChannelName;
	public String m_strChannelDesc;
	public long m_lTemplateID;
	public String m_strTmplParams;
	public String m_strEditRule;
	public String m_strPubRule;
	public String m_strAccessRule;
	public String m_strPath;
	public long m_lRefPageId;
	public String m_strUUID;
	public int m_iEditCount;
	public int m_iApproveCount;
	public int m_iPublishCount;
	public Vector<ApCPage> m_vecPages;
	/** 模版UUID，用于从XML导入 */
	public String m_strTemplateUUID;

	public ApChannel() throws AdbException {
		m_strChannelName = m_strChannelDesc = m_strTmplParams = m_strPath = m_strUUID = "";

		m_strEditRule = SU_CHAN;
		m_strPubRule = SU_CHAN;
		m_strAccessRule = "T";

		m_iEditCount = m_iApproveCount = m_iPublishCount = 0;
	}

	public Vector<ApCPage> getPages() {
		if (m_vecPages == null)
			m_vecPages = new Vector<ApCPage>();
		return m_vecPages;
	}

	public void setPages(Vector<ApCPage> vec) {
	}
}
