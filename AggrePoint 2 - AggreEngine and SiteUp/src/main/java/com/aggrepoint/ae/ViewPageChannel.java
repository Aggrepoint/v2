package com.aggrepoint.ae;

import java.sql.Connection;
import java.util.StringTokenizer;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApCPage;
import com.aggrepoint.su.core.data.ApChannel;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 动态显示栏目页面
 * 
 * @author YJM
 */
public class ViewPageChannel extends BaseModule implements ReqAttrConst,
		UrlConst, RuleConst {
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApCPage page = new ApCPage();
		ApChannel channel = new ApChannel();
		Connection conn = null;
		DbAdapter adapter = null;

		// {获取要显示的页面的ID或者路径
		try {
			// 如果是通过ID的方式来指定要显示的页面，则URL附加路径只有一段，是页面的ID
			// 如果是通过路径的方式来指定要显示的页面，则URL附加路径有两或者三段。
			// 两段用于全局栏目页面，第一段是全局栏目路径，第二段是页面路径
			// 三段用于站点栏目页面，第一段是分支路径，第二段是栏目路径，第三段是页面路径
			StringTokenizer st = new StringTokenizer(req.getRequestPath(), "/");
			switch (st.countTokens()) {
			case 1: // 通过ID的方式来指定要显示的页面
				page.m_lPageID = Long.parseLong(st.nextToken());
				if (page.m_lPageID <= 0) // 没有指定ID
					return 1001;

				conn = req.getDBConn();
				adapter = new DbAdapter(conn);

				if (adapter.retrieve(page) == null)
					return 9002;

				channel.m_lChannelID = page.m_lChannelID;
				if (adapter.retrieve(channel) == null)
					return 9002;
				break;
			case 2: // 通过路径的方式来指定要显示的全局栏目页面
				channel.m_strPath = st.nextToken();
				page.m_strPath = st.nextToken();
				if (channel.m_strPath.equals("") || page.m_strPath.equals(""))
					return 1001;

				conn = req.getDBConn();
				adapter = new DbAdapter(conn);

				if (adapter.retrieve(channel, "loadByOwnerAndPath") == null)
					return 1001;
				page.m_lChannelID = channel.m_lChannelID;
				if (adapter.retrieve(page, "loadByPath") == null)
					return 1001;
				break;
			case 3: // 通过路径的方式来指定要显示的分支栏目页面
				ApBranch branch = new ApBranch();
				branch.m_strRootPath = st.nextToken();
				channel.m_strPath = st.nextToken();
				page.m_strPath = st.nextToken();
				if (branch.m_strRootPath.equals("")
						|| channel.m_strPath.equals("")
						|| page.m_strPath.equals(""))
					return 1001;

				conn = req.getDBConn();
				adapter = new DbAdapter(conn);

				if (adapter.retrieve(branch, "loadByRootPath") == null)
					return 1001;
				channel.m_lSiteID = branch.m_lSiteID;
				if (adapter.retrieve(channel, "loadByOwnerAndPath") == null)
					return 1001;
				page.m_lChannelID = channel.m_lChannelID;
				if (adapter.retrieve(page, "loadByPath") == null)
					return 1001;
				break;
			default:
				return 1001;
			}
		} catch (Exception e) {
			return 1001;
		}
		// }

		// {显示模式
		EnumViewMode vpm = EnumViewMode.VIEW;
		if (req.getParameter(EnumViewMode.EDIT.getName(), null) != null)
			vpm = EnumViewMode.EDIT;
		else if (req.getParameter(EnumViewMode.PUBLISH.getName(), null) != null)
			vpm = EnumViewMode.PUBLISH;
		else if (req.getParameter(EnumViewMode.RESET.getName(), null) != null)
			vpm = EnumViewMode.RESET;
		// }

		try {
			// {检查页面的访问规则
			IPsnEngine psnEngine = req.getPsnEngine();
			IUserProfile userProfile = req.getUserProfile();
			String strUserID = userProfile
					.getProperty(IUserProfile.PROPERTY_ID);
			if (strUserID == null)
				strUserID = "";

			switch (vpm) {
			case PUBLISH: // 发布系统发布页面
				if (channel.m_bPsnFlag)
					return 1003;
				if (page.m_iStatusID != ApCPage.STATUS_PUBLISHED)
					return 1003;
				break;
			case VIEW:
			case RESET: // 查看或者预览页面内容
				if (!psnEngine.eveluate(SU_ROOT)) {
					switch (page.m_iStatusID) {
					case ApCPage.STATUS_CREATED:
					case ApCPage.STATUS_WAIT_FOR_APPROVE:
						// 对于信息编、审人员，检查站点管理权限
						if (channel.m_lSiteID == 100) {
							if (!psnEngine.eveluate(SU_GLOBAL))
								return 8002;
						} else {
							ApSite site = new ApSite();
							site.m_lSiteID = channel.m_lSiteID;
							if (adapter.retrieve(site) == null)
								return 8002;
							if (!psnEngine.eveluate(site.m_strManageRule))
								return 8002;
						}
						// }

						if (page.m_iStatusID == ApCPage.STATUS_CREATED
								&& !psnEngine.eveluate(channel.m_strEditRule))
							return 8002;
						if (page.m_iStatusID == ApCPage.STATUS_WAIT_FOR_APPROVE
								&& !psnEngine.eveluate(channel.m_strPubRule))
							return 8002;

						break;
					case ApCPage.STATUS_PUBLISHED:
						if (channel.m_bPsnFlag)
							if (!psnEngine.eveluate(page.m_strAccessRule)
									|| !psnEngine
											.eveluate(channel.m_strAccessRule))
								return 8002;
						break;
					}
				}
			}
			// }

			req.setAttribute(ATTR_CURRENT_PAGE, page);
			req.setAttribute(ATTR_URL_CONSTRUCTOR, new CPageURLConstructor(
					ApSite.load(adapter, channel.m_lSiteID), channel, page,
					ApTemplate.getTemplate(adapter, page.m_lTemplateID, false)));
			conn.rollback();

			// {生成本地模板
			ApPubFlag pubFlag = new ApPubFlag();
			pubFlag.m_lDocID = page.m_lTemplateID;
			pubFlag.m_iDocTypeID = ApPubFlag.TYPE_TEMPLATE
					+ page.m_iOfficialFlag;
			pubFlag.m_strServerName = req.getContext().getServerName();
			if (adapter.retrieve(pubFlag) == null) { // 需要生成本地模板
				ApTemplate template = new ApTemplate();
				template.m_lTemplateID = page.m_lTemplateID;
				adapter.retrieve(template, "loadDetail");
				template.generateJspFiles(PathConstructor.getCPageTmplDir(req));
				adapter.create(pubFlag);
				conn.commit();
			}
			// }

			conn.rollback();
			conn.close();

			// 转页面显示
			resp.setRetUrl(URL_TMPL_CPAGE + Long.toString(page.m_lTemplateID)
					+ ".jsp");
			return 0;
		} catch (Exception e) {
			resp.setRetThrow(e);

			return 1004;
		}
	}
}
