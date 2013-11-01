package com.aggrepoint.ae;

import com.aggrepoint.su.core.data.ApFrame;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示Frame资源
 * 
 * @author YJM
 */
public class ViewResFrame extends ViewRes {
	long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception {
		ApFrame frame = new ApFrame();
		frame.m_lFrameID = ownerId;
		frame.m_iOfficialFlag = officialFlag;
		frame = adapter.retrieve(frame, "loadResDirWithCache");
		if (frame == null)
			return 0;

		return frame.m_lResDirID == 0 ? frame.m_lDefResDirID
				: frame.m_lResDirID;
	}
}
