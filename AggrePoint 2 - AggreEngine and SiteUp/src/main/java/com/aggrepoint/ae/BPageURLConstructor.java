package com.aggrepoint.ae;

import java.util.Random;

import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqParamConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApPathMap;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApTemplate;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 用于生成各种资源的URL
 * 
 * @author YJM
 */
public class BPageURLConstructor implements UrlConst, ReqParamConst {
	/** 是否生成随机的栏目链接，通过Initer进行配置 */
	public static boolean m_bUniqueLink = false;

	static Random m_random = new Random();

	RequestInfo reqInfo;

	public boolean m_bInSite;

	public static String getRandom() {
		return Long.toString(m_random.nextLong());
	}

	public BPageURLConstructor(DbAdapter adapter, RequestInfo rinfo) {
		reqInfo = rinfo;
	}

	/**
	 * 生成到各种分支路径的URL
	 * 
	 * @param branch
	 * @param mode
	 * @return
	 */
	private String branch(ApBranch branch, int mode) {
		String path;

		switch (mode) {
		case 0:
			path = branch.m_strLoginPath;
			break;
		case 1:
			path = branch.m_strNoAccessPath;
			break;
		default:
			path = branch.m_strHomePath;
			break;
		}

		String strGroupPath = reqInfo.strBranchGroupPath;

		if (reqInfo.bViaProxy) {
			if (!path.startsWith("/"))
				path = "/" + path;
		} else if (path.length() < 4
				|| !path.substring(0, 4).equalsIgnoreCase("http")) {
			if (strGroupPath == null)
				strGroupPath = "/" + branch.m_strRootPath + "/";
			if (!path.startsWith("/"))
				path = URL_VIEW_PAGE_SITE + strGroupPath + path;
			else
				path = URL_VIEW_PAGE_SITE + strGroupPath + path.substring(1);
		}

		return path;
	}

	String branchHome(ApBranch branch) {
		return branch(branch, 2);
	}

	String branchLogin(ApBranch branch) {
		return branch(branch, 0);
	}

	String branchNoAccess(ApBranch branch) {
		return branch(branch, 2);
	}

	public static String getUrlRoot(RequestInfo rinfo) {
		if (rinfo == null || rinfo.bViaProxy)
			return "/";

		String strGroupPath = rinfo.strBranchGroupPath;
		if (strGroupPath == null)
			strGroupPath = "/" + rinfo.branch.m_strRootPath + "/";
		return URL_VIEW_PAGE_SITE + strGroupPath;
	}

	/**
	 * 生成到站点页面的URL
	 */
	public String siteBPage(ApBPage page) {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strPublishBranchDir != null
				&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
				&& reqInfo.viewMode == EnumViewMode.PUBLISH) {
			sb.append(
					reqInfo.site.m_strStaticBranchUrl == null ? ""
							: reqInfo.site.m_strStaticBranchUrl).append(
					page.m_strFullPath);
			if (m_bUniqueLink)
				sb.append("?")
						.append(PARAM_VIEW_MODE)
						.append("=")
						.append(page.m_bResetWin ? EnumViewMode.RESET.getName()
								: EnumViewMode.VIEW.getName());
			else if (page.m_bResetWin)
				sb.append("?").append(PARAM_VIEW_MODE).append("=")
						.append(EnumViewMode.RESET.getName());
		} else {
			// {对于映射的处理
			boolean bUseMap = false;
			if (reqInfo.pathMap != null
					&& (page.m_bInheritUseMap || page.m_bUseMap)) {
				String[] linkGroups = ApPathMap.executePattern(
						reqInfo.pathMap.getFromLinkPattern(),
						page.m_strFullPath);
				if (linkGroups != null) { // 匹配
					if (reqInfo.bViaProxy)
						sb.append(reqInfo.pathMap.translateLink(
								reqInfo.arrPathGroup, linkGroups, true));
					else if (!m_bInSite)
						sb.append(URL_VIEW_PAGE_SITE).append(
								reqInfo.pathMap
										.translateLink(reqInfo.arrPathGroup,
												linkGroups, false));
					else
						sb.append(URL_CONTEXT).append(
								reqInfo.pathMap
										.translateLink(reqInfo.arrPathGroup,
												linkGroups, false));

					bUseMap = true;
				}
			}
			// }

			if (!bUseMap) {
				if (reqInfo.bViaProxy)
					sb.append(page.m_strDirectPath);
				else if (!m_bInSite)
					sb.append(URL_VIEW_PAGE_SITE).append(page.m_strFullPath);
				else
					sb.append(URL_CONTEXT).append(page.m_strFullPath);
			}

			if (reqInfo.viewMode == EnumViewMode.EDIT)
				sb.append("?").append(PARAM_VIEW_MODE).append("=")
						.append(EnumViewMode.EDIT.getName());
			else if (reqInfo.viewMode == EnumViewMode.PUBLISH)
				sb.append("?").append(PARAM_VIEW_MODE).append("=")
						.append(EnumViewMode.PUBLISH.getName());
			else if (m_bUniqueLink)
				sb.append("?")
						.append(PARAM_VIEW_MODE)
						.append("=")
						.append(page.m_bResetWin ? EnumViewMode.RESET.getName()
								: EnumViewMode.VIEW.getName());
			else if (page.m_bResetWin)
				sb.append("?").append(PARAM_VIEW_MODE).append("=")
						.append(EnumViewMode.RESET.getName());
		}

		if (m_bUniqueLink)
			sb.append(getRandom());

		// { 组合分支的处理
		String str = sb.toString();
		if (reqInfo.strBranchGroupPath != null
				&& reqInfo.strBranchRealPath != null) {
			int i = str.indexOf(reqInfo.strBranchRealPath);
			if (i == 0)
				str = reqInfo.strBranchGroupPath
						+ str.substring(reqInfo.strBranchRealPath.length());
			else if (i > 0)
				str = str.substring(0, i) + reqInfo.strBranchGroupPath
						+ str.substring(i + reqInfo.strBranchRealPath.length());
		}
		// }

		return str;
	}

	/**
	 * 生成到资源的URL
	 */
	public String res(DbAdapter adapter, ApRes res) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strStaticResUrl != null
				&& (reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH || reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_DYNAMIC
						&& reqInfo.bViaProxy))
			sb.append(reqInfo.site.m_strStaticResUrl)
					.append(ApResDir.load(adapter, res.m_lResDirID,
							res.m_iOfficialFlag).m_strFullPath).append("/");
		else
			sb.append(URL_VIEW_RES).append("/").append(res.m_lResID);

		sb.append(res.m_strFileName);
		return sb.toString();
	}

	/**
	 * 生成到模板资源的URL
	 */
	public String bpageTmplRes(DbAdapter adapter, ApBPage page,
			ApTemplate template, String name) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strStaticResUrl != null
				&& (reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH || reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_DYNAMIC
						&& reqInfo.bViaProxy))
			sb.append(reqInfo.site.m_strStaticResUrl)
					.append(ApResDir.load(adapter,
							template.m_lResDirID == 0 ? template.m_lDefResDirID
									: template.m_lResDirID,
							template.m_iOfficialFlag).m_strFullPath)
					.append(name);
		else
			sb.append(URL_VIEW_RES_TEMPLATE).append("/")
					.append(template.m_lTemplateID).append("/").append(name);

		return sb.toString();
	}

	/**
	 * 生成到Frame资源的URL
	 */
	public String frameRes(DbAdapter adapter, ApFrame frame) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strStaticResUrl != null
				&& (reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH || reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_DYNAMIC
						&& reqInfo.bViaProxy))
			sb.append(reqInfo.site.m_strStaticResUrl)
					.append(ApResDir.load(adapter,
							frame.m_lResDirID == 0 ? frame.m_lDefResDirID
									: frame.m_lResDirID, frame.m_iOfficialFlag).m_strFullPath);
		else
			sb.append(URL_VIEW_RES_FRAME).append("/").append(frame.m_lFrameID)
					.append("/");

		return sb.toString();
	}

	/**
	 * 生成传递给窗口的到应用资源的URL
	 */
	public String appRes(DbAdapter adapter, ApApp app) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strStaticResUrl != null
				&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
				&& reqInfo.viewMode == EnumViewMode.PUBLISH)
			sb.append(reqInfo.site.m_strStaticResUrl)
					.append(ApResDir.load(adapter, app.m_lResDirID,
							app.m_iOfficialFlag).m_strFullPath);
		else
			sb.append("");

		return sb.toString();
	}

	/**
	 * 生成到内容的资源的URL
	 * 
	 * 该URL不是完整的URL，用于替换资源内容中的占位符，占位符后应该是内容资源文件名称； 该URL加上内容资源文件名称才组成完成的URL
	 */
	public String contentRes(DbAdapter adapter, ApContent content)
			throws Exception {
		StringBuffer sb = new StringBuffer();

		if (reqInfo.site.m_strStaticResUrl != null
				&& (reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH || reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_DYNAMIC
						&& reqInfo.bViaProxy))
			sb.append(reqInfo.site.m_strStaticResUrl).append(
					ApResDir.load(adapter, content.m_lResDirID,
							content.m_iOfficialFlag).m_strFullPath);
		else
			sb.append(URL_VIEW_RES_CONTENT).append("/")
					.append(content.m_lContentID).append("/");

		return sb.toString();
	}
}
