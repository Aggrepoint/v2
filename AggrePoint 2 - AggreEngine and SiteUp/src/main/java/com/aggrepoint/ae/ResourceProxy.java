package com.aggrepoint.ae;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.ae.core.proxy.HttpProxy;
import com.aggrepoint.su.core.data.ApApp;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.StringUtils;

public class ResourceProxy extends BaseModule {
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		try {
			DbAdapter adapter = new DbAdapter(req.getDBConn());

			String reqPath = req.getRequestPath().substring(1);
			int idx = reqPath.indexOf("/");
			// 应用
			ApApp app = ApApp.getApp(adapter,
					Long.parseLong(reqPath.substring(0, idx)), false);

			String url = StringUtils.appendUrl(app.m_strHostURL,
					reqPath.substring(idx + 1));
			String query = ((HttpServletRequest) req.getRequestObject())
					.getQueryString();
			if (query != null && query.length() > 0)
				url += "?" + query;

			if (!HttpProxy.proxy(req, resp, url, app.m_iConnTimeout,
					app.m_iReadTimeout))
				return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}

		// 成功返回
		return 0;
	}
}
