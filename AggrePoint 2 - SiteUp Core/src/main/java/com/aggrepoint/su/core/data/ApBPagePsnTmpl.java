package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 站点页面个性化模板
 * 
 * @author YJM
 */
public class ApBPagePsnTmpl extends ADB {
	public long m_lPsnTmplID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lPageID;
	public long m_lBranchID;
	/** 模板编号 */
	public long m_lTemplateID;
	public String m_strTmplParams;
	public String m_strOwnerID;
	public String m_strAccessRule;
	/** 模版UUID，用于从XML导入 */
	public String m_strTemplateUUID;

	public ApBPagePsnTmpl() throws AdbException {
		m_strAccessRule = "T";
		m_strTmplParams = m_strOwnerID = "";
	}

	public long getTemplateId() {
		return m_lTemplateID;
	}
	
	public String getAccessRule() {
		return m_strAccessRule;
	}
}
