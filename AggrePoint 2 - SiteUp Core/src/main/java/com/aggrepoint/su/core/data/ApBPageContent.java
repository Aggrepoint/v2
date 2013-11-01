package com.aggrepoint.su.core.data;

import java.util.Enumeration;
import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CombineString;

/**
 * 页面内容 为内容或窗口
 * 
 * @author YJM
 */
public class ApBPageContent extends ADB {
	public long m_lPageContID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lPageID;
	public long m_lContPageID;
	public long m_lBranchID;
	public String m_strAreaName;
	public int m_iZoneID;
	public int m_iOrder;
	public String m_strContName;
	public String m_strAccessRule;
	public boolean m_bInheritable;
	public String m_strOwnerID;
	public long m_lWindowID;
	public long m_lFrameID;
	public boolean m_bPopWinFlag;
	public String m_strWinParams;
	public long m_lContentID;
	public long m_lContainContID;
	public String m_strUUID;
	/** 用于从XML文件导入 */
	public ApContent m_content;
	public String m_strWindowUUID;
	public String m_strFrameUUID;
	public String m_strContentUUID;
	/** 包含的内容 */
	public Vector<ApBPageContent> m_vecChilds;

	public ApBPageContent() throws AdbException {
		m_strAreaName = m_strWinParams = m_strContName = m_strOwnerID = "";
		m_bInheritable = false;
		m_strAccessRule = "T";
	}

	public String getContName() {
		return m_strContName == null ? "" : m_strContName;
	}

	public void setContName(String str) {
		m_strContName = str == null ? "" : str;
	}

	public String getWinParams() {
		return m_strWinParams;
	}

	public void setWinParams(String str) {
		m_strWinParams = (str == null ? "" : str);
	}

	public long getId() {
		return m_lPageContID;
	}

	public long getPageId() {
		return m_lPageID;
	}

	public boolean isInherit() {
		return m_bInheritable;
	}

	public String getInheritable() {
		return m_bInheritable ? "y" : "n";
	}

	public void setInheritable(String str) {
		if (str != null && str.equalsIgnoreCase("y"))
			m_bInheritable = true;
		else
			m_bInheritable = false;
	}

	public String getPopWin() {
		return m_bPopWinFlag ? "y" : "n";
	}

	public void setPopWin(String str) {
		if (str != null && str.equalsIgnoreCase("y"))
			m_bPopWinFlag = true;
		else
			m_bPopWinFlag = false;
	}

	public ApContent getContent() throws Exception {
		if (m_content == null)
			m_content = new ApContent();
		return m_content;
	}

	public void setContent(ApContent content) {
		m_content = content;
	}

	public boolean isWindow() {
		return m_lWindowID > 0;
	}

	public boolean isPageContent() {
		return m_lContentID > 0 && m_lContPageID > 0;
	}

	public String getAreaName() {
		return m_strAreaName;
	}

	public int getZoneId() {
		return m_iZoneID;
	}

	public long getContPageId() {
		return m_lContPageID;
	}

	public long getContentId() {
		return m_lContentID;
	}

	public String getAccessRule() {
		return m_strAccessRule;
	}

	public Vector<ApBPageContent> getChilds() {
		if (m_vecChilds == null)
			m_vecChilds = new Vector<ApBPageContent>();

		return m_vecChilds;
	}

	public void setChilds(Vector<ApBPageContent> col) {
	}

	public ApBPageContent findContent(long contId) {
		if (contId == m_lPageContID)
			return this;
		if (m_vecChilds != null)
			for (ApBPageContent cont : m_vecChilds)
				if (cont.m_lPageContID == contId)
					return cont;
		return null;
	}

	public String getWinParam(String name, String defValue) {
		CombineString cs = new CombineString(m_strWinParams, '~');
		String str = null;
		while (cs.hasMoreStr()) {
			if (cs.nextString().equals(name)) {
				str = cs.nextString();
				break;
			}
			cs.nextString();
		}
		if (str != null)
			return str;
		return defValue;
	}

	/**
	 * 移动内容
	 */
	public void move(AdbAdapter adapter, int step) throws Exception {
		if (step == 0)
			return;

		Vector<ApBPageContent> vecConsts = null;
		vecConsts = adapter.retrieveMulti(this,
				m_lContainContID > 0 ? "loadForMoveInSubZone" : "loadForMove",
				"default");
		if (vecConsts.size() <= 1)
			return;

		// {找出内容当前所在位置
		int idx = 0;
		ApBPageContent cont = null;
		for (Enumeration<ApBPageContent> enm = vecConsts.elements(); enm
				.hasMoreElements(); idx++) {
			cont = enm.nextElement();
			if (cont.m_lPageContID == m_lPageContID)
				break;
		}
		// }

		// {计算要移去的位置
		int toIdx = idx + step;
		if (toIdx < 0)
			toIdx = 0;
		else if (toIdx >= vecConsts.size())
			toIdx = vecConsts.size() - 1;

		if (idx == toIdx)
			return;
		// }

		// {移动
		cont.m_iZoneID = ((ApBPageContent) vecConsts.elementAt(toIdx)).m_iZoneID;
		vecConsts.removeElementAt(idx);
		vecConsts.insertElementAt(cont, toIdx);
		// }

		// 保存
		idx = 0;
		for (Enumeration<ApBPageContent> enm = vecConsts.elements(); enm
				.hasMoreElements(); idx++) {
			cont = enm.nextElement();
			cont.m_iOrder = idx;
			adapter.update(cont, "updateZoneAndOrder");
		}
	}

	/**
	 * 拖动内容
	 */
	public void drag(AdbAdapter adapter, String areaName, int iZoneID,
			long container, long onContID, boolean before) throws Exception {
		m_strAreaName = areaName;
		m_iZoneID = iZoneID;
		m_lContainContID = container;

		if (onContID == -1) { // 没有指定参照内容，直接移到区域的最后
			adapter.retrieve(this,
					m_lContainContID > 0 ? "loadMaxOrderInSubZone"
							: "loadMaxOrderInZone");
			m_iOrder++;
			adapter.update(this, "updateAreaZoneContainerAndOrder");
			return;
		}

		Vector<ApBPageContent> vecConsts = null;
		vecConsts = adapter.retrieveMulti(this, "loadForDrag", "default");

		// {找出参照内容所在位置
		int idx = 0;
		ApBPageContent cont = null;
		for (Enumeration<ApBPageContent> enm = vecConsts.elements(); enm
				.hasMoreElements(); idx++) {
			cont = enm.nextElement();
			if (cont.m_lPageContID == onContID) {
				m_iZoneID = cont.m_iZoneID;
				break;
			}
		}
		// }

		if (idx >= vecConsts.size()) { // 找不到参照内容，直接移到区域最后
			adapter.retrieve(this,
					m_lContainContID > 0 ? "loadMaxOrderInSubZone"
							: "loadMaxOrderInZone");
			m_iOrder++;
			adapter.update(this, "updateAreaZoneContainerAndOrder");
			return;
		}

		if (before)
			vecConsts.insertElementAt(this, idx);
		else
			vecConsts.insertElementAt(this, idx + 1);

		// 保存
		idx = 0;
		for (Enumeration<ApBPageContent> enm = vecConsts.elements(); enm
				.hasMoreElements(); idx++) {
			cont = enm.nextElement();
			cont.m_iOrder = idx;
			if (cont == this)
				adapter.update(cont, "updateAreaZoneContainerAndOrder");
			else
				adapter.update(cont, "updateZoneAndOrder");
		}
	}

	public void delete(DbAdapter adapter) throws Exception {
		for (ApBPageContent cont : getChilds())
			cont.delete(adapter);

		adapter.delete(this);

		if (isPageContent()) { // 页面内容，将内容定义也删除
			ApContent content = new ApContent();
			content.m_lContentID = m_lContentID;
			adapter.proc(content, "delete");
		}
	}
}
