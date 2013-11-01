package com.aggrepoint.su.core.admin;

import java.sql.Connection;

import com.aggrepoint.su.core.data.ApBPage;
import com.icebean.core.adb.db.DbAdapter;

public class MovePage {
	/**
	 * 移动页面
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Connection conn = com.icebean.core.conn.db.DBConnManager
					.getConnection("direct");
			DbAdapter adapter = new DbAdapter(conn);

			ApBPage pageToMove = new ApBPage();
			pageToMove.m_strFullPath = args[0];
			if (adapter.retrieve(pageToMove, "loadByFullPath") == null)
				throw new Exception("页面找不到。");

			ApBPage pageParent = new ApBPage();
			pageParent.m_strFullPath = args[1];
			if (adapter.retrieve(pageParent, "loadByFullPath") == null)
				throw new Exception("父页面找不到。");

			pageToMove.m_lParentID = pageParent.m_lPageID;
			pageToMove.m_strFullPath = pageParent.m_strFullPath
					+ pageToMove.m_strPathName + "/";
			pageToMove.m_strDirectPath = pageParent.m_strDirectPath
					+ pageToMove.m_strPathName + "/";

			if (pageToMove.m_lTemplateID <= 0) { // 继承上级页面的模板
				pageToMove.m_bInheritTmpl = true;

				pageToMove.m_lTemplateID = pageParent.m_lTemplateID;
			}

			adapter.update(pageToMove);

			pageToMove.cascadeTemplateAndPath(adapter, true, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
