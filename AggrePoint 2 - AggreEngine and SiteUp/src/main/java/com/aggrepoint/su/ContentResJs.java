package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 提供内容资源
 * 
 * @author YJM
 */
public class ContentResJs extends BaseModule implements RuleConst {
	public static final String CONTENT_ID = "cid";

	public int list(IModuleRequest req, IModuleResponse resp, boolean image)
			throws Exception {
		ApContent cont = new ApContent();
		cont.m_lContentID = req.getParameter(CONTENT_ID, -1);
		if (cont.m_lContentID == -1)
			return -1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		if (adapter.retrieve(cont) == null)
			return -2;

		ApRes res = new ApRes();
		res.m_lResDirID = cont.m_lResDirID;
		if (image)
			res.m_iFileType = ApRes.TYPE_IMAGE;
		else
			res.m_iFileType = ApRes.TYPE_FLASH;

		req.setAttribute("CONTENT", cont);

		req.setAttribute("RESES",
				adapter.retrieveMulti(res, "loadByDirAndType", "def"));
		return 0;
	}

	/**
	 * 列出图片供选择
	 */
	public int listImage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return list(req, resp, true);
	}

	/**
	 * 列出媒体文件供选择
	 */
	public int listMedia(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return list(req, resp, false);
	}
}
