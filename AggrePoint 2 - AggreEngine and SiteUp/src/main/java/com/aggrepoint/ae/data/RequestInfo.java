package com.aggrepoint.ae.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.ae.BPageURLConstructor;
import com.aggrepoint.ae.plugin.AEPsnEngine;
import com.aggrepoint.ae.win.WindowMode;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.ApBPagePsnTmpl;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApBranchGroup;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApPathMap;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UserAgent;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CookieTool;
import com.icebean.core.common.TypeCast;

public class RequestInfo implements ReqParamConst, RuleConst, SessionConst,
		CookieConst, IWinletConst {
	public static enum RequestType {
		PAGE_VIEW, WIN_ACTION, WIN_RES, WIN_MODE, WIN_VIEW, CONT_VIEW
	}

	public static final String[] PARAMS_EXCLUDE = { PARAM_WIN_ID,
			PARAM_WIN_RES, PARAM_WIN_PARAM, PARAM_VIEW_MODE,
			REQUEST_PARAM_WIN_INST_ID, REQUEST_PARAM_WIN_PAGE_ID,
			REQUEST_PARAM_VIEW_ID, REQUEST_PARAM_ACTION };

	/**
	 * 以下部分为从请求中直接获得的最初始数据
	 */

	public long requestId;
	/** 请求的路径 */
	public String strRequestPath;
	public String strServerNamePort;
	/** 访问的内容编号，-1表示未指定 */
	public long lContId;
	/** 访问的窗口编号，-1表示未指定 */
	public long lWinId;
	/** 访问的窗口视图，null表示缺省视图 */
	public String strWinView;
	/** 执行的窗口动作，null表示不是窗口操作 */
	public String strWinAction;
	/** 提交的表单编号 */
	public String strFormId;
	/** 是否对单个字段进行校验 */
	public boolean bIsValidateField;
	/** 访问的窗口资源，null表示不是访问窗口资源 */
	public String strWinRes;
	/** 访问窗口资源时的影射参数 */
	public String strWinMapParams;
	/** 访问窗口资源时的其他参数 */
	public String strWinOtherParams;
	/** 窗口模式 */
	public EnumWinMode winMode;
	/** 查看模式 */
	public EnumViewMode viewMode;
	/** 是否Ajax访问 */
	public boolean bAjax;
	/** */
	public String strPagePath;
	public String strPageRoot;

	/**
	 * 为true表示请求通过Apache HTTP Server的url rewrite和proxy模块处理
	 * 经过处理的页面URL直接以/开始，无须以/ap/site/开始
	 */
	public boolean bViaProxy;
	/** 通过Apache代理访问时真正的客户地址 */
	public String strRemoteAddr;
	/** 通过Apache代理访问时真正的Request URI */
	public String strRequestUri;

	/** 请求类型 */
	public RequestType requestType;

	/** 个性化引擎 */
	public AEPsnEngine psnEngine;

	public WinletUserProfile userProfile;

	public String strUserID;

	/**
	 * 以下部分为计算被访问页面后获得的数据
	 */

	/** 经过分支和映射处理后实际访问的页面的路径 */
	public String strAccessPath;
	/** 组合分支：访问路径对应的组合分支的路径 */
	public String strBranchGroupPath;
	/** 组合分支：访问路径对应的真实分支的路径 */
	public String strBranchRealPath;
	/** 路径映射：当前请求适配的映射定义 */
	public ApPathMap pathMap;
	/** 路径映射：当前请求适配的映射定义 */
	public String[] arrPathGroup;
	/** 路径映射：根据映射从当前URL获取出的参数 */
	public String strMapParam;

	/** 访问的页面 */
	public ApBPage page;
	/** 访问的页面所属的分支 */
	public ApBranch branch;
	/** 访问的页面所属的站点 */
	public ApSite site;
	/** 当前页面在树中的位置 */
	public ApBPage currentPageInTree;
	/** 页面中是否可能存在协作个性化内容 */
	public boolean bPageCollaborate;
	/** 分支中是否可能存在私有内容 */
	public boolean bBranchPrivate;
	/** 页面中是否可能存在私有内容 */
	public boolean bPagePrivate;
	/** URL构造器 */
	public BPageURLConstructor urlConstructor;
	/** 响应时是否启用Ajax */
	public boolean bUseAjax;

	/** 当前用户定义的私有页面 */
	public Vector<ApBPage> vecPrivatePages;
	/** 当前用户定义的私有名称 */
	public Hashtable<Long, ApBPagePsnName> htPrivateNames;
	/** 是否已经合并过个性化内容 */
	boolean bPrivateMerged = false;

	/** 当前用户是否为Root。仅在编辑模式下计算 */
	public boolean bIsRoot = false;

	/**
	 * 以下部分为计算被访问窗口后获得的数据
	 */
	public ApBPageContent winst;
	public ApContent cont;
	public ApWindow win;
	/** 窗口所属应用编号 */
	public long lAppId;
	public EnumWinMode currWinMode;

	/**
	 * 以下部分为加载模版后获得的数据
	 */
	/** 模版 */
	public ApTemplate template;
	/** 当前用户是否为Manager。仅在编辑模式下计算 */
	public boolean bIsManager = false;
	/** 当前用户是否允许对当前页面进行协作个性化管理。仅在编辑模式下计算 */
	public boolean bIsClbPsn = false;

	static String getLastParameter(IModuleRequest req, String name, String def) {
		String[] vals;

		vals = req.getParameterValuesNotMultipart(name);
		if (vals == null)
			return def;

		return vals[vals.length - 1].trim();
	}

	static int getLastParameter(IModuleRequest req, String paramName,
			int defaultValue) {
		try {
			return Integer.parseInt(getLastParameter(req, paramName, ""));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	static long getLastParameter(IModuleRequest req, String paramName,
			long defaultValue) {
		try {
			return Long.parseLong(getLastParameter(req, paramName, ""));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 用于分解Action或Resource
	 */
	Pattern P_DECODE = Pattern.compile("([^!]*)!([^!]*)!([^!]+)(?:!(.*))?");

	/**
	 * 初始化，提取初始数据
	 * 
	 * @param req
	 * @param inSite
	 */
	public RequestInfo(DbAdapter adapter, IModuleRequest req,
			boolean forReloadPage) {
		requestId = req.getRequestID();

		strPagePath = getLastParameter(req, PARAM_PAGE_PATH, null);
		if (strPagePath == null)
			strPagePath = req.getRequestURI();
		strPageRoot = "";

		// 获取各项请求参数
		lContId = getLastParameter(req, PARAM_CONT_ID, -1l);
		if (forReloadPage) // 执行窗口动作后重新加载页面，不需要再次加载窗口动作相关的参数
			lWinId = -1;
		else {
			lWinId = getLastParameter(req, PARAM_WIN_ID, -1l);
			strWinView = getLastParameter(req, PARAM_WIN_VIEW, null);
			strWinAction = getLastParameter(req, PARAM_WIN_ACTION, null);
			if (strWinAction != null) {
				Matcher m;
				synchronized (P_DECODE) {
					m = P_DECODE.matcher(strWinAction);
				}

				if (m.find()) {
					try {
						lWinId = Long.parseLong(m.group(1));
						strWinView = m.group(2);
					} catch (Exception e) {
					}
					strWinAction = m.group(3);

					if (m.groupCount() > 3)
						strFormId = m.group(4);
				}
			}
			bIsValidateField = getLastParameter(req, PARAM_WIN_VALIDATE_FIELD,
					"no").equalsIgnoreCase("yes");
			strWinRes = getLastParameter(req, PARAM_WIN_RES, null);
			if (strWinRes != null) {
				Matcher m;
				synchronized (P_DECODE) {
					m = P_DECODE.matcher(strWinRes);
				}
				if (m.find()) {
					try {
						lWinId = Long.parseLong(m.group(1));
						strWinView = m.group(2);
					} catch (Exception e) {
					}
					strWinRes = m.group(3);
				}
			}
			winMode = EnumWinMode.fromId(getLastParameter(req, PARAM_WIN_MODE,
					EnumWinMode.NOCHANGE.getId()));
		}
		viewMode = EnumViewMode.fromName(getLastParameter(req, PARAM_VIEW_MODE,
				EnumViewMode.VIEW.getName()));
		bAjax = getLastParameter(req, PARAM_AJAX_FLAG, "N").equalsIgnoreCase(
				"Y");

		// 访问窗口资源时的影射参数
		if (strWinRes != null)
			strWinMapParams = req.getParameterNotMultipart(PARAM_WIN_PARAM);

		// { 将URL参数传递给Winlet
		// 20120425 原来获取strWinOtherPrarms的方法，在提交表单时会把整个表单内容都带上，并且在大量数据提交时导致URL过长
		// String[] names = req.getParameterNamesNotMultipart(PARAMS_EXCLUDE);
		// StringBuffer sbOthers = new StringBuffer();
		// for (int i = 0; i < names.length; i++) {
		// String[] values = req.getParameterValuesNotMultipart(names[i]);
		// for (int j = 0; j < values.length; j++) {
		// if (i != 0 || j != 0)
		// sbOthers.append("&");
		// sbOthers.append(names[i]);
		// sbOthers.append("=");
		// try {
		// sbOthers.append(URLEncoder.encode(values[j], "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// strWinOtherParams = sbOthers.toString();
		strWinOtherParams = ((HttpServletRequest) req.getRequestObject())
				.getQueryString();
		// }

		strRemoteAddr = req.getParameterNotMultipart(PARAM_REMOTE_ADDR, null);
		if (strRemoteAddr != null && !strRemoteAddr.equals("")) {
			// 通过代理访问
			bViaProxy = true;
			strRequestUri = req.getParameterNotMultipart(PARAM_REQUEST_URI,
					null);
		} else {
			// 不是通过代理访问
			bViaProxy = false;
			strRemoteAddr = req.getRemoteAddr();
			strRequestUri = ((HttpServletRequest) req.getRequestObject())
					.getRequestURI();
		}

		// 个性化引擎
		psnEngine = (AEPsnEngine) req.getPsnEngine();

		userProfile = new WinletUserProfile(req.getUserProfile());

		strUserID = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		if (strUserID == null)
			strUserID = "";

		// {请求路径
		strRequestPath = req.getRequestPath();

		if (!strRequestPath.endsWith("/"))
			strRequestPath = strRequestPath + "/";
		// }

		strServerNamePort = ((HttpModuleRequest) req).getServerNamePort();

		// 判断访问类型
		requestType = RequestType.PAGE_VIEW;
		if (lWinId != -1) {
			if (strWinAction != null)
				requestType = RequestType.WIN_ACTION;
			else if (winMode != EnumWinMode.NOCHANGE)
				requestType = RequestType.WIN_MODE;
			else if (strWinRes != null)
				requestType = RequestType.WIN_RES;
			else
				requestType = RequestType.WIN_VIEW;
		} else if (lContId != -1)
			requestType = RequestType.CONT_VIEW;

		if (viewMode == EnumViewMode.EDIT && psnEngine.eveluate(SU_ROOT))
			bIsRoot = true;

		// 只有刷新页面时才重置窗口。页面内操作引起Ajax窗口刷新不重置
		if (requestType != RequestType.PAGE_VIEW
				&& viewMode == EnumViewMode.RESET)
			viewMode = EnumViewMode.VIEW;
	}

	/**
	 * 重新加载用户身份
	 * 
	 * @param req
	 */
	public void reloadUserInfo(IModuleRequest req) {
		psnEngine = (AEPsnEngine) req.getPsnEngine();

		userProfile = new WinletUserProfile(req.getUserProfile());

		strUserID = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		if (strUserID == null)
			strUserID = "";
	}

	/**
	 * 获取待选的标记语言列表
	 * 
	 * @param req
	 * @param psnEngine
	 * @return
	 */
	public static Vector<EnumMarkup> getMarkupList(IModuleRequest req,
			AEPsnEngine psnEngine) {
		Vector<EnumMarkup> vec = new Vector<EnumMarkup>();

		EnumMarkup markup = (EnumMarkup) req.getSessionAttribute(ATTR_MARKUP);
		if (markup != null)
			vec.add(markup);

		int i = CookieTool.getCookie(
				(HttpServletRequest) req.getRequestObject(), COOKIE_MARKUP, -1);
		if (i != -1) {
			markup = EnumMarkup.fromId(i);
			if (!vec.contains(markup))
				vec.add(markup);
		}

		UserAgent ua = psnEngine.getUserAgent();
		vec.add(ua.getDefaultMarkup());
		if (ua.supportXHTML && !vec.contains(EnumMarkup.XHTML))
			vec.add(EnumMarkup.XHTML);
		if (ua.supportHTML && !vec.contains(EnumMarkup.HTML))
			vec.add(EnumMarkup.HTML);
		if (ua.supportWML && !vec.contains(EnumMarkup.WML))
			vec.add(EnumMarkup.WML);
		return vec;
	}

	/**
	 * 获取组合分支计算结果
	 * 
	 * @param req
	 * @param url
	 * @return
	 */
	static long getBranchSel(IModuleRequest req, String url) {
		Hashtable<String, Long> ht = TypeCast.cast(req
				.getSessionAttribute(SessionConst.ATTR_BRANCH_SEL));
		if (ht == null)
			return -1;
		Long l = ht.get(url);
		if (l == null)
			return -1;
		return l.longValue();
	}

	/**
	 * 保存组合分支计算结果
	 * 
	 * @param req
	 * @param url
	 * @param sel
	 */
	static void saveBranchSel(IModuleRequest req, String url, long sel) {
		Hashtable<String, Long> ht = TypeCast.cast(req
				.getSessionAttribute(SessionConst.ATTR_BRANCH_SEL));
		if (ht == null) {
			ht = new Hashtable<String, Long>();
			req.setSessionAttribute(SessionConst.ATTR_BRANCH_SEL, ht);
		}
		ht.put(url, sel);
	}

	/**
	 * 计算被访问的页面
	 * 
	 * @param req
	 * @param adapter
	 */
	public void findPage(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		if (strRequestPath.equals("/") && (lWinId > 0 || lContId > 0)) { // 直接访问Winlet或内容
			if (lWinId > 0) {
				findWin(req, adapter);
				if (win == null)
					return;
			} else {
				findCont(req, adapter);
				if (cont == null)
					return;
			}

			page = new ApBPage();
			page.m_lPageID = winst.m_lPageID;
			page = adapter.retrieve(page);
			if (page == null)
				return;

			strAccessPath = strRequestPath = page.m_strFullPath;
		} else {
			strAccessPath = strRequestPath;

			// {组合分支处理
			for (ApBranch br : ApBranch.getGroupBranch(adapter)) {
				if (!strRequestPath.startsWith(br.m_strRootPath))// 路径与组合分支不匹配
					continue;

				Vector<ApBranchGroup> vecGroups = ApBranchGroup.getBranchGroup(
						adapter, br.m_lBranchID);
				ApBranchGroup group = null;

				if (br.m_iMarkupType == 1) { // 组合分支设置为保存分支计算结果
					// 如果会话中已经保存下适用的分支，则不用重新计算了
					long branchID = getBranchSel(req, br.m_strRootPath);

					if (branchID != -1) {
						for (Enumeration<ApBranchGroup> enu1 = vecGroups
								.elements(); enu1.hasMoreElements();) {
							group = enu1.nextElement();
							if (group.m_lBranchID == branchID)
								break;
							group = null;
						}
					}
				}

				if (group == null) { // 不保存分支计算机过，或者会话中没有保存适用的分支，或者保存的分支找不到
					ApBranchGroup firstGroup = null;
					for (Enumeration<EnumMarkup> enuMarkup = getMarkupList(req,
							psnEngine).elements(); group == null
							&& enuMarkup.hasMoreElements();) { // 逐个尝试标记
						int markup = enuMarkup.nextElement().getId();

						// 匹配标记类型和个性化规则
						for (Enumeration<ApBranchGroup> enu1 = vecGroups
								.elements(); enu1.hasMoreElements();) {
							group = enu1.nextElement();
							if (group.m_iMarkupType != markup)
								continue;
							if (firstGroup == null)
								firstGroup = group;
							if (psnEngine.eveluate(group.m_strRule))
								break;
							group = null;
						}
					}

					if (group == null)
						group = firstGroup;
				}

				if (group == null && vecGroups.size() > 0)
					group = vecGroups.elementAt(0);

				if (group != null) { // 找到匹配的分支
					if (br.m_iMarkupType == 1) // 在会话中保存分支计算结果
						saveBranchSel(req, br.m_strRootPath, group.m_lBranchID);

					strBranchGroupPath = br.m_strRootPath;

					ApBranch tmp = new ApBranch();
					tmp.m_lBranchID = group.m_lBranchID;
					tmp = adapter.retrieve(tmp, "loadWithCache");

					strBranchRealPath = "/" + tmp.m_strRootPath + "/";
					strAccessPath = strBranchRealPath
							+ strAccessPath
									.substring(br.m_strRootPath.length());
				}

				break;
			}
			// }

			// {映射处理
			mapLoop: for (ApBranch br : ApBranch.getRootHasMap(adapter)) {
				if (!strAccessPath.startsWith(br.m_strRootPath))
					continue;

				Vector<ApPathMap> vecMaps = ApPathMap.getPathMaps(adapter,
						br.m_lBranchID);

				for (Enumeration<ApPathMap> enu1 = vecMaps.elements(); enu1
						.hasMoreElements();) {
					ApPathMap map = enu1.nextElement();
					map.setRootPath(br.m_strRootPath);
					String[] groups = ApPathMap.executePattern(
							map.getFromPathPattern(), strAccessPath);
					if (groups == null)
						continue;

					// 匹配成功
					pathMap = map;
					arrPathGroup = groups;
					strMapParam = map.constructParams(groups);
					break mapLoop;
				}
			}
			// }

			page = new ApBPage();
			page.m_strFullPath = strAccessPath;
			page = adapter.retrieve(page, "loadByFullPath");

			if (page == null) {
				// 页面找不到，尝试匹配栏目
				for (ApBranch br : ApBranch.getAllBranch(adapter))
					if (strAccessPath.startsWith(br.m_strRootPath)) { // 路径与分支匹配
						branch = br;
						break;
					}

				if (branch != null) { // 尝试匹配支持扩展匹配的页面
					branch = ApBranch.getSiteBranchWithGlobalAssocs(adapter,
							branch.m_lBranchID, true,
							viewMode != EnumViewMode.VIEW
									&& viewMode != EnumViewMode.RESET);

					for (ApBPage p : branch.m_vecExpandMatchPages)
						if (strAccessPath.startsWith(p.m_strFullPath)) {
							// 不可直接用p，因为p来自于cache，而后续逻辑会修改page的内容
							page = new ApBPage();
							page.m_lPageID = p.m_lPageID;
							adapter.retrieve(page);
							break;
						}
				}

				if (page == null) {
					if (branch != null) {
						site = ApSite.load(adapter, branch.m_lSiteID);
						urlConstructor = new BPageURLConstructor(adapter, this);
					}
					return;
				}
			}
		}

		int idx = strPagePath.indexOf(page.m_strFullPath);
		if (idx >= 0)
			strPageRoot = strPagePath.substring(0, idx);

		branch = ApBranch.getSiteBranchWithGlobalAssocs(adapter,
				page.m_lBranchID, true, viewMode != EnumViewMode.VIEW
						&& viewMode != EnumViewMode.RESET);
		site = ApSite.load(adapter, branch.m_lSiteID);
		currentPageInTree = branch.m_rootPage.findPage(page.m_lPageID);

		bPageCollaborate = !branch.m_strClbPsnRule.equalsIgnoreCase("F");
		bBranchPrivate = !branch.m_strPvtPsnRule.equalsIgnoreCase("F");
		bPagePrivate = bBranchPrivate;

		urlConstructor = new BPageURLConstructor(adapter, this);

		// 是否在响应中启用Ajax
		bUseAjax = psnEngine.getUserAgent().supportAjax
				&& branch.m_iMarkupType == EnumMarkup.HTML.getId();
	}

	/**
	 * 判断用户是否有权限访问指定的页面 指定的页面必须属于branch，因此该方法只能在findPage()之后调用
	 * 
	 * @param thePage
	 * @return
	 */
	public boolean allowAccess(ApBPage thePage) {
		return bIsRoot
				|| thePage.ownerIs(strUserID)
				|| (psnEngine.eveluate(thePage.m_strAccessRule) && psnEngine
						.eveluate(branch.m_strAccessRule));
	}

	/**
	 * 获取当前用户定义的私有页面<br>
	 * 必须在findPage()后才能调用
	 * 
	 * @param adapter
	 * @return
	 * @throws Exception
	 */
	public Vector<ApBPage> getPrivatePages(DbAdapter adapter) throws Exception {
		if (vecPrivatePages != null)
			return vecPrivatePages;
		vecPrivatePages = new Vector<ApBPage>();
		if (bBranchPrivate && strUserID != null && !strUserID.equals("")) {
			String ownerId = page.m_strOwnerID;
			page.m_strOwnerID = strUserID;
			vecPrivatePages = adapter.retrieveMulti(page,
					"loadPrivateForBranch", "default");
			page.m_strOwnerID = ownerId;
		}
		return vecPrivatePages;
	}

	/**
	 * 获取当前用户定义的私有名称<br>
	 * 必须在findPage()后才能调用
	 * 
	 * @param adapter
	 * @return
	 * @throws Exception
	 */
	public Hashtable<Long, ApBPagePsnName> getPrivateNames(DbAdapter adapter)
			throws Exception {
		if (htPrivateNames != null)
			return htPrivateNames;
		htPrivateNames = new Hashtable<Long, ApBPagePsnName>();
		if (bBranchPrivate && strUserID != null && !strUserID.equals("")) {
			ApBPagePsnName name = new ApBPagePsnName();

			name.m_lBranchID = page.m_lBranchID;
			name.m_strOwnerID = strUserID;

			Vector<ApBPagePsnName> vecNames = adapter.retrieveMulti(name,
					"loadPrivateForBranch", "default");
			if (vecNames.size() > 0) {
				for (Enumeration<ApBPagePsnName> enumNames = vecNames
						.elements(); enumNames.hasMoreElements();) {
					name = enumNames.nextElement();
					htPrivateNames.put(new Long(name.m_lPageID), name);
				}
			}
		}
		return htPrivateNames;
	}

	public void mergePrivate(DbAdapter adapter) throws Exception {
		if (bPrivateMerged)
			return;

		if (!userProfile.isAnonymous()) {
			if (bPageCollaborate || bPagePrivate) {
				String ownerId = page.m_strOwnerID;

				page.m_strOwnerID = strUserID;
				adapter.retrieve(page, "loadPrivateAssoc");

				page.m_strOwnerID = ownerId;
			}
		}

		// 将私有内容和全局内容合并
		page.mergePublicAssoc(currentPageInTree);

		bPrivateMerged = true;
	}

	/**
	 * 计算被访问的窗口
	 * 
	 * @param req
	 * @param adapter
	 * @throws Exception
	 */
	public void findWin(IModuleRequest req, DbAdapter adapter) throws Exception {
		if (winst == null) {
			winst = new ApBPageContent();
			winst.m_lPageContID = lWinId;
			winst = adapter.retrieve(winst, "loadWithCache");
			if (winst == null)
				return;

			win = new ApWindow();
			win.m_lWindowID = winst.m_lWindowID;
			win = adapter.retrieve(win, "loadWithCache");

			currWinMode = WindowMode.getMode(
					((HttpServletRequest) req.getRequestObject()).getSession(),
					winst.m_lPageID, winst.m_strAreaName, winst.m_lPageContID);
		}
	}

	/**
	 * 计算被访问的窗口
	 * 
	 * @param req
	 * @param adapter
	 * @throws Exception
	 */
	public void findCont(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		if (winst == null) {
			winst = new ApBPageContent();
			winst.m_lPageContID = lContId;
			winst = adapter.retrieve(winst, "loadWithCache");
			if (winst == null)
				return;

			cont = new ApContent();
			cont.m_lContentID = winst.m_lContentID;
			cont = adapter.retrieve(cont, "loadWithCache");
		}
	}

	/**
	 * 加载模版
	 * 
	 * @param adapter
	 * @throws Exception
	 */
	public void loadTempate(DbAdapter adapter) throws Exception {
		long lTemplateID = page.m_lTemplateID;
		if (viewMode == EnumViewMode.EDIT && branch.m_lAdminTmplID != 0) // 指定了管理模版
			lTemplateID = branch.m_lAdminTmplID;
		else { // 查找个性化模版
			if (page.m_vecPsnTmpls != null) {
				ApBPagePsnTmpl tmpl = null;
				for (Enumeration<ApBPagePsnTmpl> enumTmpls = page.m_vecPsnTmpls
						.elements(); enumTmpls.hasMoreElements();) {
					tmpl = enumTmpls.nextElement();
					if (tmpl.m_strOwnerID.equals(strUserID))
						break;
					else if (psnEngine.eveluate(tmpl.m_strAccessRule))
						break;
					tmpl = null;
				}

				if (tmpl != null) {
					lTemplateID = tmpl.m_lTemplateID;
				}
			}
		}

		// 模板
		template = ApTemplate.getTemplate(adapter, lTemplateID, false);

		// 注：只有在可视化编辑模式下才需要获取bIsRoot、bIsManager和bIsClbPsn的值
		// 只有HTML模版才支持可视化编辑
		if (viewMode == EnumViewMode.EDIT
				&& template.m_iMarkupType == EnumMarkup.HTML.getId()
				&& (psnEngine.eveluate(SU_ROOT)
						|| psnEngine.eveluate(branch.m_strManageRule) || page
						.ownerIs(strUserID)))
			bIsManager = true;

		if (viewMode == EnumViewMode.EDIT
				&& template.m_iMarkupType == EnumMarkup.HTML.getId()
				&& psnEngine.eveluate(page.m_strClbPsnRule))
			bIsClbPsn = true;
	}
}
