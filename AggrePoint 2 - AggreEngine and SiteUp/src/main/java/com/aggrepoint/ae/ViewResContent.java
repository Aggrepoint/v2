package com.aggrepoint.ae;

import com.aggrepoint.su.core.data.ApContent;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示内容资源
 * 
 * @author YJM
 */
public class ViewResContent extends ViewRes {
	long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception {
		ApContent cont = new ApContent();
		cont.m_lContentID = ownerId;
		cont.m_iOfficialFlag = officialFlag;
		cont = adapter.retrieve(cont, "loadResDirWithCache");
		if (cont == null)
			return 0;

		return cont.m_lResDirID;
	}
}
