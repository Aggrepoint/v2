package com.aggrepoint.su.core.data;

import java.util.Vector;

import com.icebean.core.adb.ADB;

/**
 * 
 * @author YJM
 */
public class ApCPage extends ADB {
	public static final int STATUS_NULL = 0;
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_WAIT_FOR_APPROVE = 2;
	public static final int STATUS_PUBLISHED = 3;
	public static final int STATUS_DELETED = 100;

	public static final int DOCTYPE_PAGE = 70;
	public static final int DOCTYPE_FILE = 71;
	public static final int DOCTYPE_LINK = 72;

	/** 页面编号 */
	public long m_lPageID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	/** 页面所属栏目编号 */
	public long m_lChannelID;
	/** 文档类型 */
	public int m_iPageType;
	public int m_iStatusID;
	/** 是否继承使用栏目的模板 */
	public short m_sInheritTmpl;
	/** 模板 */
	public long m_lTemplateID;
	/** 模板参数 */
	public String m_strTmplParams;
	/** 信息标题 */
	public String m_strTitle;
	/** 信息子标题 */
	public String m_strSubTitle;
	/** 信息内容 */
	public String m_strContent;
	/** 信息排序 */
	public int m_iOrder;
	/** 打开方式 */
	public short m_sOpenMode;
	/** 若是文件下载：文件名称 */
	public String m_strFileName;
	/** 若是文件下载：文件类型 */
	public String m_strFileContType;
	public String m_strFile;
	/** 访问规则 */
	public String m_strAccessRule;
	/** 编辑人 */
	public String m_strEditor;
	/** 审批人 */
	public String m_strApprover;
	/** 信息来源 */
	public String m_strSource;
	/** 信息关键字 */
	public String m_strKeyword;
	/** 信息路径 */
	public String m_strPath;
	/** 信息创建日期 */
	public java.sql.Date m_dtCreate;
	public String m_strUUID;
	/** 信息附带的资源 */
	public Vector<ApRes> m_vecReses;
	/** 用于从多个栏目中同时加载信息时的IN操作 */
	public Vector<Long> m_vecChannelIDs;
	/** 模版UUID，用于从XML导入 */
	public String m_strTemplateUUID;
	public long m_lResDirID;

	public ApCPage() {
		m_strAccessRule = "T";
		m_strTitle = m_strSubTitle = m_strContent = m_strFileName = m_strFileContType = m_strFile = m_strTmplParams = m_strEditor = m_strApprover = m_strSource = m_strKeyword = m_strPath = m_strUUID = "";
		m_sInheritTmpl = 1;
		m_dtCreate = null;
	}

	public Vector<ApRes> getReses() {
		if (m_vecReses == null)
			m_vecReses = new Vector<ApRes>();
		return m_vecReses;
	}

	public void setReses(Vector<ApRes> vec) {
	}

	public Vector<Long> getChannelIDs() {
		if (m_vecChannelIDs == null)
			m_vecChannelIDs = new Vector<Long>();
		return m_vecChannelIDs;
	}

	public void setChannelIDs(Vector<Long> vec) {
	}

	public void setContent(String content) {
		m_strContent = content;
	}

	public String getContent() {
		return m_strContent;
	}

	public java.sql.Date getCreateDate() {
		if (m_dtCreate == null)
			m_dtCreate = new java.sql.Date(System.currentTimeMillis());
		return m_dtCreate;
	}

	public void setCreateDate(java.sql.Date date) {
		m_dtCreate = date;
	}

	public void setCreateDate2(java.util.Date date) {
		m_dtCreate = new java.sql.Date(date.getTime());
	}
}
