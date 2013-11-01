package com.aggrepoint.ae;

import java.sql.Connection;
import java.util.StringTokenizer;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletParamParser;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApCPage;
import com.aggrepoint.su.core.data.ApChannel;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 提供各种信息列表
 * 
 * @author YJM
 */
public class NewsList extends Winlet implements ReqAttrConst, UrlConst,
		RuleConst, IAdkConst {
	private static final long serialVersionUID = 1L;

	/** 信息栏目路径。若是从当前站点中查找信息栏目，则路径不以/开始；若是在全局站点中查找，则路径以/开始 */
	public static final String PARAM_CHANNEL_PATH = "cpath";

	/** 使用的信息列表模版的名称 */
	public static final String PARAM_TEMPLATE = "tmpl";

	/** 列表中显示信息的个数 */
	public static final String PARAM_NUMBER = "num";

	/** 若需要显示“更多”链接，链接的URL */
	public static final String PARAM_MORE_LINK = "morelink";

	/**
	 * 显示信息列表
	 */
	public int showList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		WinletParamParser param = new WinletParamParser(req);

		String path = param.getParam(PARAM_CHANNEL_PATH);
		if (path == null)
			return 1;

		Connection conn = req.getDBConn();
		DbAdapter adapter = new DbAdapter(conn);
		ApCPage cpage = new ApCPage();

		// {获取当前页面，用于判断当前站点
		ApBPage page = new ApBPage();
		page.m_lPageID = req.getParameter(REQUEST_PARAM_WIN_PAGE_ID, -1l);
		if (page.m_lPageID == -1l)
			return 2;
		if (adapter.retrieve(page, "loadSite") == null)
			return 3;
		// }

		// {获取要显示的栏目
		ApChannel channel = new ApChannel();
		IPsnEngine psnEngine = req.getPsnEngine();
		for (StringTokenizer st = new StringTokenizer(path, " ,;"); st
				.hasMoreTokens();) {
			channel.m_strPath = st.nextToken();

			if (channel.m_strPath.startsWith("/")) {
				channel.m_lSiteID = 100;
				channel.m_strPath = channel.m_strPath.substring(1);
			} else {
				channel.m_lSiteID = page.m_lSiteID;
			}

			if (adapter.retrieve(channel, "loadByOwnerAndPath") == null)
				return 4;

			// {检查访问权限
			if (!psnEngine.eveluate(SU_ROOT)
					&& !psnEngine.eveluate(channel.m_strAccessRule))
				continue;
			// }

			cpage.getChannelIDs().add(new Long(channel.m_lChannelID));
		}
		// }

		// {获取使用的模板
		ApTemplate template = new ApTemplate();
		template.m_strTmplName = param.getParam(PARAM_TEMPLATE);
		if (template.m_strTmplName == null)
			return 5;
		if (template.m_strTmplName.startsWith("/")) {
			template.m_strTmplName = template.m_strTmplName.substring(1);
		} else {
			template.m_lSiteID = page.m_lSiteID;
		}
		if (adapter.retrieve(template, "loadBasicByOwnerAndName") == null)
			return 5;
		// }

		cpage.m_iStatusID = ApCPage.STATUS_PUBLISHED;
		req.setAttribute(ATTR_PAGES, adapter.retrieveMultiDbl(cpage,
				"loadByStatusInChannels", "order",
				param.getParam(PARAM_NUMBER, 10), 1, true));
		req.setAttribute(ATTR_LIST_TEMPLATE, template);
		conn.rollback();

		// {生成本地模板
		ApPubFlag pubFlag = new ApPubFlag();
		pubFlag.m_lDocID = template.m_lTemplateID;
		pubFlag.m_iDocTypeID = ApPubFlag.TYPE_TEMPLATE
				+ template.m_iOfficialFlag;
		pubFlag.m_strServerName = req.getContext().getServerName();
		if (adapter.retrieve(pubFlag) == null) { // 需要生成本地模板
			adapter.retrieve(template, "loadDetail");
			template.generateJspFiles(PathConstructor.getListTmplDir(req));
			adapter.create(pubFlag);
			conn.commit();
		}
		// }

		conn.rollback();
		conn.close();

		// 转页面显示
		req.setAttribute(ATTR_MORE_LINK, param.getParam(PARAM_MORE_LINK, ""));
		resp.setRetUrl(URL_TMPL_LIST + Long.toString(template.m_lTemplateID)
				+ ".jsp");
		return 0;
	}
}
