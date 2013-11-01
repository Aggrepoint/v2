package com.aggrepoint.ae;

import com.aggrepoint.su.core.data.ApTemplate;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示模板资源
 * 
 * @author YJM
 */
public class ViewResTemplate extends ViewRes {
	long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception {
		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = ownerId;
		template.m_iOfficialFlag = officialFlag;
		template = adapter.retrieve(template, "loadResDirWithCache");
		if (template == null)
			return 0;

		return template.m_lResDirID == 0 ? template.m_lDefResDirID
				: template.m_lResDirID;
	}
}
