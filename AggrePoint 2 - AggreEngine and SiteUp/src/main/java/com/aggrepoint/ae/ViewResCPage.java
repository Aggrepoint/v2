package com.aggrepoint.ae;

import com.aggrepoint.su.core.data.ApCPage;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示栏目页面资源文件
 * 
 * @author YJM
 */
public class ViewResCPage extends ViewRes {
	long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception {
		ApCPage cpage = new ApCPage();
		cpage.m_lPageID = ownerId;
		cpage.m_iOfficialFlag = officialFlag;
		cpage = adapter.retrieve(cpage, "loadResDirWithCache");
		if (cpage == null)
			return 0;

		return cpage.m_lResDirID;
	}
}
