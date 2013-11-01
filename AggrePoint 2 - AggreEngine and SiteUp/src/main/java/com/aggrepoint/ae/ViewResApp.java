package com.aggrepoint.ae;

import com.aggrepoint.su.core.data.ApApp;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示应用资源
 * 
 * @author YJM
 */
public class ViewResApp extends ViewRes {
	long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception {
		ApApp app = new ApApp();
		app.m_lAppID = ownerId;
		app.m_iOfficialFlag = officialFlag;
		app = adapter.retrieve(app, "loadResDirWithCache");
		if (app == null)
			return 0;

		return app.m_lResDirID;
	}
}
