package com.aggrepoint.su.core.data;

import java.util.Hashtable;
import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;

/**
 * 
 * 站点分支组合
 * 
 * @author YJM
 */
public class ApBranchGroup extends ADB {
	/** 组合编号 */
	public long m_lGroupID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	/** 所属站点编号 */
	public long m_lSiteID;
	/** 组合分支编号 */
	public long m_lGroupBranchID;
	/** 被组合分支编号 */
	public long m_lBranchID;
	/** 顺序 */
	public int m_iOrder;
	/** 适配规则 */
	public String m_strRule;
	/** 被组合分支的标记类型 */
	public int m_iMarkupType;
	/** 被组合的分支的名称，仅用于显示 */
	public String m_strBranchName;
	/** 以下属性用于从XML文件中导入 */
	public String m_strGroupBranchUUID;
	public String m_strBranchUUID;


	static Hashtable<Long, ADBList<ApBranchGroup>> m_htCaches = new Hashtable<Long, ADBList<ApBranchGroup>>();

	public ApBranchGroup() throws AdbException {
		m_lGroupID = m_lSiteID = m_lGroupBranchID = m_lBranchID = -1;
		m_iOrder = 0;
		m_strRule = "";
	}

	public long getBranchId() {
		return m_lBranchID;
	}

	public int getOrder() {
		return m_iOrder;
	}

	public String getRule() {
		return m_strRule;
	}

	/**
	 * 从Cache中获取属于组合分支的所有分支
	 * 
	 * @param adapter
	 * @param siteID
	 * @return
	 * @throws Exception
	 */
	static public Vector<ApBranchGroup> getBranchGroup(AdbAdapter adapter,
			long branchID) throws Exception {
		Long id = new Long(branchID);
		ADBList<ApBranchGroup> list = m_htCaches.get(new Long(branchID));
		if (list == null) {
			ApBranchGroup group = new ApBranchGroup();
			group.m_lGroupBranchID = branchID;
			list = adapter.retrieveMultiDbl(group, "loadByBranch", "default");
			m_htCaches.put(id, list);
		} else {
			adapter.syncList(list, false);
		}

		return list.m_vecObjects;
	}
}
