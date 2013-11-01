package com.aggrepoint.ae.taglib.html.btmpl;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.ae.BPageURLConstructor;
import com.aggrepoint.ae.core.proxy.HttpClientResponse;
import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.ae.win.WindowMode;
import com.aggrepoint.ae.win.WindowProxy;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBPageLayout;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CombineString;
import com.icebean.core.common.StringUtils;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 输出栏位内容
 * 
 * @author YJM
 */
public class AreaContentTag extends TagSupport implements UrlConst,
		ReqAttrConst, IWinletConst, RuleConst {
	static final long serialVersionUID = 0;

	/** 预加载的窗口 */
	static final String REQUEST_KEY_REVIEW_WINDOWS = AreaContentTag.class
			.getName().toString() + ".PREVIEW_WINDOWS";
	static final String SESSION_KEY_VISITED_WINDOWS = AreaContentTag.class
			.getName().toString() + ".VISITED_WINDOWS";

	static Pattern PT_MIN = Pattern
			.compile(
					"<a\\s+href\\s*=\\s*\"\\s*\\$MINURL\\$\\s*\"\\s*(title\\s*=\\s*\"[^\"]*\"\\s*)?>[\\s\\S]*?</a>",
					Pattern.CASE_INSENSITIVE);
	static Pattern PT_MAX = Pattern
			.compile(
					"<a\\s+href\\s*=\\s*\"\\s*\\$MAXURL\\$\\s*\"\\s*(title\\s*=\\s*\"[^\"]*\"\\s*)?>[\\s\\S]*?</a>",
					Pattern.CASE_INSENSITIVE);
	static Pattern PT_NORMAL = Pattern
			.compile(
					"<a\\s+href\\s*=\\s*\"\\s*\\$RESTOREURL\\$\\s*\"\\s*(title\\s*=\\s*\"[^\"]*\"\\s*)?>[\\s\\S]*?</a>",
					Pattern.CASE_INSENSITIVE);
	static String PT_LINK = "\\$LINK\\((.+)\\)\\$";
	static String PT_RES = "\\$RES\\((.+)\\)\\$";

	/** 信息Boundle */
	public static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	String m_strName;
	boolean m_bClear = true;

	public AreaContentTag() {
	}

	public void setName(String name) {
		m_strName = name;
	}

	public void setClear(String clear) {
		m_bClear = !("no".equalsIgnoreCase(clear));
	}

	static int constructWinMode(EnumWinMode baseMode, boolean edit) {
		if (edit)
			return baseMode.getId() | 0x10;
		return baseMode.getId();
	}

	static String replaceChangeModeUrl(ApWindow win, boolean bAjax, String str,
			String areaName, long iid) {
		String strIID = new Long(iid).toString();
		String msg = bAjax ? "ajax_changeMode" : "noneajax_changeMode";

		if (win != null && win.getSupportMinFlag())
			str = StringUtils.replaceString(str, "$MINURL$", m_msg
					.constructMessage(msg, areaName, strIID,
							EnumWinMode.MIN.getStrId()));
		else
			str = PT_MIN.matcher(str).replaceAll("");

		if (win != null && win.getSupportMaxFlag())
			str = StringUtils.replaceString(str, "$MAXURL$", m_msg
					.constructMessage(msg, areaName, strIID,
							EnumWinMode.MAX.getStrId()));
		else
			str = PT_MAX.matcher(str).replaceAll("");

		if (win != null && (win.getSupportMinFlag() || win.getSupportMaxFlag()))
			str = StringUtils.replaceString(str, "$RESTOREURL$", m_msg
					.constructMessage(msg, areaName, strIID,
							EnumWinMode.NORMAL.getStrId()));
		else
			str = PT_NORMAL.matcher(str).replaceAll("");

		return str;
	}

	/**
	 * 处理页面或者内容中的$LINK()$
	 */
	static String processPageLink(RequestInfo reqInfo, String str) {
		Pattern pt = Pattern.compile(PT_LINK);
		Vector<String> vecLinks = new Vector<String>();
		Matcher m = pt.matcher(str);
		while (m.find())
			vecLinks.add(m.group(1));

		if (vecLinks.size() > 0) {
			for (String link : vecLinks) {
				String path = link;
				if (!path.startsWith("/"))
					path = "/" + path;
				if (!path.endsWith("/"))
					path += "/";
				path = "/" + reqInfo.branch.m_rootPage.m_strPathName + path;

				if (reqInfo.site.m_strPublishBranchDir != null
						&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH)
					// 发布模式，参考BPageURLConstructor
					path = (reqInfo.site.m_strStaticBranchUrl == null ? ""
							: reqInfo.site.m_strStaticBranchUrl) + path;
				else
					path = reqInfo.strPageRoot + path;

				str = str.replaceAll("\\$LINK\\(" + link + "\\)\\$", path);
			}
		}
		return str;
	}

	static String processResLink(RequestInfo reqInfo, String str) {
		Pattern pt = Pattern.compile(PT_RES);
		Vector<String> vecReses = new Vector<String>();
		Matcher m = pt.matcher(str);
		while (m.find())
			vecReses.add(m.group(1));

		if (vecReses.size() > 0) {
			for (String res : vecReses) {
				String path = res;
				if (!path.startsWith("/"))
					path = "/" + path;

				if (reqInfo.site.m_strStaticResUrl != null
						&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC
						&& reqInfo.viewMode == EnumViewMode.PUBLISH)
					path = reqInfo.site.m_strStaticResUrl + path;
				else
					path = URL_VIEW_RES_CONTENT + "/-" + reqInfo.site.m_lSiteID
							+ path;

				str = str.replaceAll("\\$RES\\(" + res + "\\)\\$", path);
			}
		}
		return str;
	}

	/**
	 * 生成一个编辑内容（非窗口内容）
	 * 
	 * @param sb
	 * @param adapter
	 * @param request
	 * @param response
	 * @param reqInfo
	 * @param content
	 * @param includeSpan
	 * @param cont
	 * @param wins
	 * 
	 * @throws Exception
	 */
	static void constructContent(StringBuffer sb, DbAdapter adapter,
			HttpServletRequest request, HttpServletResponse response,
			RequestInfo reqInfo, ApBPageContent content, boolean includeSpan,
			ApContent cont, Vector<ApBPageContent> wins) throws Exception {
		if (cont.m_strContent == null || cont.m_strContent.equals(""))
			return;

		boolean bIsOwner = reqInfo.psnEngine
				.eveluate(reqInfo.page.m_strClbPsnRule)
				&& reqInfo.strUserID.equals(content.m_strOwnerID);

		int iCurrent = 0;
		int iStart, iEnd;
		String strMacro;
		String strValue;
		int iZoneIdx = -1;

		if (wins != null) { // 查找需要引用的窗口
			String str = cont.m_strContent;

			while (true) {
				iStart = str.indexOf("$", iCurrent);
				if (iStart == -1)
					break;
				iEnd = str.indexOf("$", iStart + 1);
				if (iEnd == -1)
					break;

				strMacro = str.substring(iStart + 1, iEnd);
				if (strMacro.equals("ZONE")) {
					iZoneIdx++;

					for (ApBPageContent c : content.getChilds())
						if (c.m_iZoneID == iZoneIdx)
							processContent(sb, null, adapter, request,
									response, reqInfo, c, includeSpan, wins);

				}

				iCurrent = iEnd + 1;
			}

			return;
		}

		// 通过后台录入的内容
		String str = cont.m_strContent.replaceAll("/ap2/res/cont/"
				+ cont.m_lContentID + "/",
				reqInfo.urlConstructor.contentRes(adapter, cont));
		str = str.replaceAll("(\\.\\./)+res/cont/" + cont.m_lContentID + "/",
				reqInfo.urlConstructor.contentRes(adapter, cont));

		// 替换&lt;和&gt;
		if (reqInfo.viewMode != EnumViewMode.EDIT)
			str = str.replaceAll("&amp;lt;", "<").replaceAll("&amp;gt;", ">");

		str = processResLink(reqInfo, processPageLink(reqInfo, str));

		// {加载样式
		ApRes res = new ApRes();
		res.m_lResDirID = cont.m_lResDirID;
		res.m_iFileType = ApRes.TYPE_CSS;
		for (ApRes r : adapter.retrieveMulti(res, "loadByDirAndType", "def"))
			sb.append(m_msg.constructMessage("style",
					reqInfo.urlConstructor.contentRes(adapter, cont)
							+ r.m_strFileName));
		// }

		// {加载脚本
		res.m_iFileType = ApRes.TYPE_JS;
		for (ApRes r : adapter.retrieveMulti(res, "loadByDirAndType", "def"))
			sb.append(m_msg.constructMessage("script",
					reqInfo.urlConstructor.contentRes(adapter, cont)
							+ r.m_strFileName));
		// }

		while (true) {
			iStart = str.indexOf("$", iCurrent);
			if (iStart == -1) {
				sb.append(str.substring(iCurrent));
				break;
			}
			iEnd = str.indexOf("$", iStart + 1);
			if (iEnd == -1) {
				sb.append(str.substring(iCurrent));
				break;
			}

			strMacro = str.substring(iStart + 1, iEnd);
			if (strMacro.equals("RESPATH"))
				strValue = reqInfo.urlConstructor.contentRes(adapter, cont);
			else if (strMacro.startsWith("USER.")) {
				strMacro = strMacro.substring(5);
				strValue = reqInfo.userProfile.getProperty(strMacro);
				if (strValue == null)
					strValue = "";
			} else if (strMacro.equals("BRANCHID"))
				strValue = Long.toString(reqInfo.branch.m_lBranchID);
			else if (strMacro.equals("SITEID"))
				strValue = Long.toString(reqInfo.branch.m_lSiteID);
			else if (strMacro.equals("ZONE")) {
				iZoneIdx++;
				StringBuffer s = new StringBuffer();

				if (reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner)
					s.append(m_msg.constructMessage("subZoneStart", new Long(
							content.m_lPageContID).toString(), new Integer(
							iZoneIdx).toString()));

				for (ApBPageContent c : content.getChilds())
					if (c.m_iZoneID == iZoneIdx)
						processContent(s, null, adapter, request, response,
								reqInfo, c, includeSpan, wins);

				if (reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner)
					s.append(m_msg.getMessage("subZoneEnd"));

				strValue = s.toString();
			} else
				strValue = null;

			if (strValue == null)
				sb.append(str.substring(iCurrent, iEnd + 1));
			else {
				sb.append(str.substring(iCurrent, iStart));
				sb.append(strValue);
			}

			iCurrent = iEnd + 1;
		}
	}

	/**
	 * 生成栏位内容 注意，修改该方法逻辑时应同步修改previewWindow
	 * 
	 * @param includeSpan
	 *            是否生成包含内容的span
	 * 
	 * @return 0表示内容隐藏或用户无权查看或显示出错, 1表示生成了内容, 2表示重定向
	 * 
	 * @throws Exception
	 */
	public static int processContent(StringBuffer sb, StringBuffer sbTitle,
			DbAdapter adapter, HttpServletRequest request,
			HttpServletResponse response, RequestInfo reqInfo,
			ApBPageContent content, boolean includeSpan,
			Vector<ApBPageContent> wins) throws Exception {
		// 对于发布的处理
		if (reqInfo.viewMode == EnumViewMode.PUBLISH
				&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC) {
			if (wins != null)
				return 0;

			if (content.m_lWindowID == 0) { // 静态内容
				ApContent cont = ApContent.getFromCache(adapter,
						content.m_lContentID,
						reqInfo.viewMode != EnumViewMode.VIEW);
				if (cont == null)
					return 0;

				if (!"T".equalsIgnoreCase(cont.m_strAccessRule)) {
					sb.append(m_msg.constructMessage("ajax_contStart",
							new Long(content.m_lPageContID).toString()));
					sb.append(m_msg.getMessage("ajax_contEnd"));
				} else
					constructContent(sb, adapter, request, response, reqInfo,
							content, includeSpan, cont, wins);

				return 0;
			}

			// 窗口
			ApWindow win = ApWindow.getWindow(adapter, content.m_lWindowID,
					reqInfo.viewMode != EnumViewMode.VIEW);
			if (win == null)
				return 0;
			sb.append(m_msg.constructMessage("ajax_winStart", new Long(
					content.m_lPageContID).toString(),
					content.m_bPopWinFlag ? "yes" : ""));
			sb.append(m_msg.getMessage("ajax_winEnd"));
			return 0;
		}

		boolean bIsForView = reqInfo.strWinView != null
				&& !reqInfo.strWinView.equals(Long.toString(reqInfo.lWinId));

		// 若内容为窗口，判断是否处于隐藏状态，若是则直接跳过不显示
		EnumWinMode winMode = EnumWinMode.NORMAL;
		if (content.m_lWindowID != 0) {
			winMode = WindowMode.getMode(request.getSession(),
					reqInfo.page.m_lPageID, content.m_strAreaName,
					content.m_lPageContID);

			// 若窗口状态是隐藏则不用显示
			if (winMode == EnumWinMode.HIDE)
				return 0;
		}

		// {检查用户对内容的访问权限
		// 是否内容的主人
		boolean bIsOwner = reqInfo.psnEngine
				.eveluate(reqInfo.page.m_strClbPsnRule)
				&& reqInfo.strUserID.equals(content.m_strOwnerID);

		if (!reqInfo.bIsRoot && !bIsOwner
				&& !reqInfo.psnEngine.eveluate(content.m_strAccessRule))
			return 0;

		String rule = "";
		ApWindow win = null;
		ApContent cont = null;

		if (content.m_lWindowID != 0) {
			win = ApWindow.getWindow(adapter, content.m_lWindowID,
					reqInfo.viewMode != EnumViewMode.VIEW);
			if (win == null)
				return 0;
			rule = win.m_strAccessRule;
		} else {
			cont = ApContent.getFromCache(adapter, content.m_lContentID,
					reqInfo.viewMode != EnumViewMode.VIEW);
			if (cont == null)
				return 0;
			rule = cont.m_strAccessRule;
		}

		if (!reqInfo.bIsRoot && !reqInfo.psnEngine.eveluate(rule))
			return 0;
		// }

		if (wins == null)
			if (!bIsForView)
				if (reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner) {
					if (win != null)
						sb.append(m_msg.constructMessage("winStart", new Long(
								content.m_lPageContID).toString()));
					else
						sb.append(m_msg.constructMessage("contStart", new Long(
								content.m_lPageContID).toString(), new Long(
								content.m_lContentID).toString(), content
								.isPageContent() ? "cont_page" : "cont"));
				} else {
					if (reqInfo.bUseAjax && includeSpan && win != null)
						sb.append(m_msg.constructMessage("ajax_winStart",
								new Long(content.m_lPageContID).toString(),
								content.m_bPopWinFlag ? "yes" : ""));
					else if (reqInfo.template != null
							&& (reqInfo.template.m_iMarkupType == EnumMarkup.HTML
									.getId() || reqInfo.template.m_iMarkupType == EnumMarkup.XHTML
									.getId()))
						sb.append("<div>");
				}

		if (cont != null) { // 显示内容
			constructContent(sb, adapter, request, response, reqInfo, content,
					includeSpan, cont, wins);
		} else { // 显示窗口
			// 发起请求
			HttpClientResponse hcr = null;

			if (win.getMinRequestFlag() || winMode != EnumWinMode.MIN) {
				if (wins != null) { // 只是查找要显示的窗口
					wins.add(content);
					return 1;
				}

				@SuppressWarnings("unchecked")
				Hashtable<ApBPageContent, HttpClientResponse> ht = (Hashtable<ApBPageContent, HttpClientResponse>) request
						.getAttribute(REQUEST_KEY_REVIEW_WINDOWS);
				if (ht != null)
					hcr = ht.get(content);

				if (hcr == null)
					hcr = WindowProxy.request(
							adapter,
							reqInfo.urlConstructor,
							reqInfo.requestId,
							request,
							response,
							reqInfo.strWinView,
							content.m_lWindowID,
							content.m_lPageContID,
							content.m_lPageID,
							content.m_strAreaName,
							content.m_lBranchID,
							CombineString.combine(content.m_strWinParams,
									reqInfo.strMapParam, '~'),
							constructWinMode(winMode, reqInfo.bIsManager
									|| reqInfo.bIsClbPsn && bIsOwner),
							reqInfo.strWinOtherParams, reqInfo.userProfile
									.getProperty(IUserProfile.PROPERTY_ID),
							reqInfo.userProfile.serialize(), reqInfo.psnEngine
									.getUserAgent().serialize(),
							reqInfo.strRemoteAddr, reqInfo.strRequestUri,
							reqInfo.template.getMarkup(),
							reqInfo.viewMode == EnumViewMode.RESET,
							BPageURLConstructor.getUrlRoot(reqInfo),
							reqInfo.viewMode != EnumViewMode.VIEW);

			} else
				// 处于最小化模式，而且无需发起请求
				hcr = new HttpClientResponse();

			if (wins != null)
				return 0;

			if (hcr.m_strRedirect != null) {
				sb.delete(0, sb.length());
				sb.append(hcr.m_strRedirect);
				return 2;
			}

			// { 处理返回的窗口状态
			if (win.getDynaWinStateFlag()
					&& hcr.m_headerSetWindowMode != EnumWinMode.NOCHANGE) {
				winMode = WindowMode.getMode(request.getSession(),
						reqInfo.page.m_lPageID, content.m_strAreaName,
						content.m_lPageContID);
			}
			// }

			if (bIsForView)
				sb.append(processResLink(reqInfo,
						processPageLink(reqInfo, hcr.m_response.toString())));
			else if (content.m_bPopWinFlag && sbTitle == null
					&& !(reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner)) {
				// 如果内容是弹出窗口，只有当通过Ajax访问时(sbTitle != null)，或者在编辑状态中才予以显示
				// 在不显示弹出窗口的时候仍然向后端请求窗口页面目的是建立窗口实例，以便后续刷新弹出窗口时可以找到窗口实例
			} else if (reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner
					|| hcr.m_headerSetWindowMode != EnumWinMode.HIDE) {
				if (winMode == EnumWinMode.MIN)
					// 最小化，忽略收到的内容
					hcr.m_response = "";
				if (hcr.m_strHeaderWindowTitle == null
						|| !win.getDynaTitleFlag())
					hcr.m_strHeaderWindowTitle = content.m_strContName;
				if (sbTitle != null)
					sbTitle.append(hcr.m_strHeaderWindowTitle);

				// { Frame内容
				ApFrame frame = null;
				String strFrame = "";
				String strFrameNormal = "";
				String strFrameMax = "";
				String strFrameMin = "";
				if (content.m_lFrameID != 0) { // 指定了Frame
					frame = ApFrame.getFrame(adapter, content.m_lFrameID,
							reqInfo.viewMode != EnumViewMode.VIEW);
					if (frame != null) {
						strFrameNormal = frame.m_strContNormal;
						strFrameMax = frame.m_strContMax;
						strFrameMin = frame.m_strContMin;
					}
				}
				// }

				if (frame == null)
					sb.append(processResLink(reqInfo,
							processPageLink(reqInfo, hcr.m_response.toString())));
				else {
					// 替换Frame控制按钮
					switch (winMode) {
					case MAX:
						strFrame = replaceChangeModeUrl(win, reqInfo.bUseAjax,
								strFrameMax, content.m_strAreaName,
								content.m_lPageContID);
						break;
					case MIN:
						strFrame = replaceChangeModeUrl(win, reqInfo.bUseAjax,
								strFrameMin, content.m_strAreaName,
								content.m_lPageContID);
						break;
					default:
						strFrame = replaceChangeModeUrl(win, reqInfo.bUseAjax,
								strFrameNormal, content.m_strAreaName,
								content.m_lPageContID);
						break;
					} // 替换Frame标题
					strFrame = StringUtils.replaceString(strFrame, "$TITLE$",
							hcr.m_strHeaderWindowTitle);
					// 替换Frame内容中资源占位符号
					strFrame = StringUtils.replaceString(strFrame, "$RESPATH$",
							reqInfo.urlConstructor.frameRes(adapter, frame));
					// 替换Frame中的内容
					strFrame = StringUtils.replaceString(
							strFrame,
							"$CONTENT$",
							processResLink(
									reqInfo,
									processPageLink(reqInfo,
											hcr.m_response.toString())));
					sb.append(strFrame);
				}
			}

		}

		if (wins != null)
			return 1;

		if (!bIsForView)
			if (reqInfo.bIsManager || reqInfo.bIsClbPsn && bIsOwner) {
				if (win != null)
					sb.append(m_msg.getMessage("winEnd"));
				else
					sb.append(m_msg.getMessage("contEnd"));
			} else {
				if (win != null && includeSpan && reqInfo.bUseAjax)
					sb.append(m_msg.getMessage("ajax_winEnd"));
				else if (reqInfo.template != null
						&& (reqInfo.template.m_iMarkupType == EnumMarkup.HTML
								.getId() || reqInfo.template.m_iMarkupType == EnumMarkup.XHTML
								.getId()))
					sb.append("</div>");
			}

		return 1;
	}

	public static void processArea(StringBuffer sb, DbAdapter adapter,
			HttpServletRequest request, HttpServletResponse response,
			RequestInfo reqInfo, String areaName, Vector<ApBPageContent> wins)
			throws Exception {
		// {处理栏位中有最大化窗口的情况。若栏位中存在最大化窗口，则只显示最大化窗口，其他内容全部不用显示
		boolean hasMax = false;
		long lMaxIID = WindowMode.getMaxWin(request.getSession(),
				reqInfo.page.m_lPageID, areaName);
		if (lMaxIID > 0) {
			for (ApBPageContent content : reqInfo.page.getContents()) {
				if (content.m_lPageContID != lMaxIID)
					continue;
				hasMax = (processContent(sb, null, adapter, request, response,
						reqInfo, content, true, wins) == 1);
				break;
			}
		}
		// }

		if (!hasMax) { // 栏位中不存在最大化窗口
			if (wins != null) {
				for (ApBPageContent content : reqInfo.page.getContents())
					if (content.m_strAreaName.equalsIgnoreCase(areaName))
						processContent(sb, null, adapter, request, response,
								reqInfo, content, true, wins);

				return;
			}

			// {获取栏位布局
			String strLayout = "$ZONE$";
			ApBPageLayout layout = reqInfo.page.getLayoutForArea(areaName);
			if (layout != null) {
				ApLayout sl = ApLayout.getLayout(adapter, layout.m_lLayoutID,
						reqInfo.page.m_iOfficialFlag,
						reqInfo.viewMode != EnumViewMode.VIEW);
				if (sl != null)
					strLayout = sl.m_strContent;
			}

			if (strLayout.indexOf("$ZONE$") == -1)
				strLayout = "$ZONE$";
			// }

			int iZonePosi = strLayout.indexOf("$ZONE$");
			int iZoneID = -1;

			// {生成内容
			// 是否已经出现过在本栏位内的内容。由于内容都是以栏位为首关键字进行排序，因此若
			// 已出现过内容后，遇到第一个不是本栏位的内容时，表示本栏位内容已经处理完毕
			boolean bStarted = false;

			for (ApBPageContent content : reqInfo.page.getContents()) {
				if (!content.m_strAreaName.equalsIgnoreCase(areaName))
					if (bStarted)
						break;
					else
						continue;

				// 跳过内容中的窗口
				if (content.m_lContainContID > 0)
					continue;

				bStarted = true;

				// {寻找对应的区域
				while (iZonePosi != -1 && iZoneID != content.m_iZoneID) {
					if ((reqInfo.bIsManager || reqInfo.bIsClbPsn)
							&& iZoneID != -1)
						sb.append(m_msg.getMessage("zoneEnd"));
					iZoneID++;

					sb.append(strLayout.substring(0, iZonePosi));
					if (reqInfo.bIsManager || reqInfo.bIsClbPsn)
						sb.append(m_msg.constructMessage("zoneStart", areaName,
								new Integer(iZoneID).toString()));

					strLayout = strLayout.substring(iZonePosi + 6);
					iZonePosi = strLayout.indexOf("$ZONE$");
				}
				// }

				processContent(sb, null, adapter, request, response, reqInfo,
						content, true, wins);
			}
			// }

			// {输出剩余的区域
			while (iZonePosi != -1) {
				if ((reqInfo.bIsManager || reqInfo.bIsClbPsn) && iZoneID != -1)
					sb.append(m_msg.getMessage("zoneEnd"));
				iZoneID++;
				sb.append(strLayout.substring(0, iZonePosi));
				if (reqInfo.bIsManager || reqInfo.bIsClbPsn)
					sb.append(m_msg.constructMessage("zoneStart", areaName,
							new Integer(iZoneID).toString()));

				strLayout = strLayout.substring(iZonePosi + 6);
				iZonePosi = strLayout.indexOf("$ZONE$");
			}
			if (reqInfo.bIsManager || reqInfo.bIsClbPsn)
				sb.append(m_msg.getMessage("zoneEnd"));
			sb.append(strLayout);
			// }
		}

	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			if (reqInfo.page == null || reqInfo.urlConstructor == null)
				return SKIP_BODY;

			StringBuffer sb = new StringBuffer();

			Connection conn = null;

			try {
				conn = DBConnManager.getConnection();
				DbAdapter adapter = new DbAdapter(conn);

				if (reqInfo.bIsManager || reqInfo.bIsClbPsn) // 可视化编辑而且当前用户具备权限
					sb.append(m_msg.constructMessage("areaStart", m_strName));
				else if (reqInfo.bUseAjax)
					sb.append(m_msg.constructMessage("ajax_areaStart",
							m_strName));

				processArea(sb, adapter,
						(HttpServletRequest) pageContext.getRequest(),
						(HttpServletResponse) pageContext.getResponse(),
						reqInfo, m_strName, null);

				if (reqInfo.bIsManager || reqInfo.bIsClbPsn) // 可视化编辑而且当前用户具备权限
					sb.append(m_msg.getMessage("areaEnd"));
				else if (reqInfo.bUseAjax)
					sb.append(m_msg.getMessage("ajax_areaEnd"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
					} catch (Exception e) {
					}
			}

			out.print(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}

	/**
	 * 并行处理不成熟，所以回滚为全串行处理。包含并行处理的方法改名为preloadWindows_disabled<br>
	 * 遇到的问题有：<br>
	 * X 一些多视图的Winlet代码逻辑里会假定某个view会被第一个调用。并行处理后打破了这个假定，造成代码逻辑不正常<br>
	 * X 某些场景下会出错，View实例没有正确生成，窗口显示不出，应该代码逻辑还不完善。<br>
	 * 
	 * @param adapter
	 * @param request
	 * @param response
	 * @param reqInfo
	 * @param wins
	 * @return
	 * @throws Exception
	 */
	public static Vector<HttpClientResponse> preloadWindows(
			final DbAdapter adapter, final HttpServletRequest request,
			final HttpServletResponse response, final RequestInfo reqInfo,
			Vector<ApBPageContent> wins) throws Exception {
		Vector<HttpClientResponse> vec = new Vector<HttpClientResponse>();

		// 窗口访问结果，存在request中
		@SuppressWarnings("unchecked")
		Hashtable<ApBPageContent, HttpClientResponse> ht = (Hashtable<ApBPageContent, HttpClientResponse>) request
				.getAttribute(REQUEST_KEY_REVIEW_WINDOWS);
		if (ht == null) {
			ht = new Hashtable<ApBPageContent, HttpClientResponse>();
			request.setAttribute(REQUEST_KEY_REVIEW_WINDOWS, ht);
		}

		for (final ApBPageContent content : wins) {
			HttpClientResponse hcr = null;

			try {
				EnumWinMode winMode = WindowMode.getMode(request.getSession(),
						reqInfo.page.m_lPageID, content.m_strAreaName,
						content.m_lPageContID);

				boolean bIsOwner = reqInfo.psnEngine
						.eveluate(reqInfo.page.m_strClbPsnRule)
						&& reqInfo.strUserID.equals(content.m_strOwnerID);

				hcr = WindowProxy.request(
						adapter,
						reqInfo.urlConstructor,
						reqInfo.requestId,
						request,
						response,
						reqInfo.strWinView,
						content.m_lWindowID,
						content.m_lPageContID,
						content.m_lPageID,
						content.m_strAreaName,
						content.m_lBranchID,
						CombineString.combine(content.m_strWinParams,
								reqInfo.strMapParam, '~'),
						constructWinMode(winMode, reqInfo.bIsManager
								|| reqInfo.bIsClbPsn && bIsOwner),
						reqInfo.strWinOtherParams, reqInfo.userProfile
								.getProperty(IUserProfile.PROPERTY_ID),
						reqInfo.userProfile.serialize(), reqInfo.psnEngine
								.getUserAgent().serialize(),
						reqInfo.strRemoteAddr, reqInfo.strRequestUri,
						reqInfo.template.getMarkup(),
						reqInfo.viewMode == EnumViewMode.RESET,
						BPageURLConstructor.getUrlRoot(reqInfo),
						reqInfo.viewMode != EnumViewMode.VIEW);
			} catch (Exception e) {
				hcr = new HttpClientResponse();
				e.printStackTrace();
			}
			vec.add(hcr);
			ht.put(content, hcr);
		}
		return vec;
	}

	public static Vector<HttpClientResponse> preloadWindows_disabled(
			final DbAdapter adapter, final HttpServletRequest request,
			final HttpServletResponse response, final RequestInfo reqInfo,
			Vector<ApBPageContent> wins) throws Exception {
		final Vector<HttpClientResponse> vec = new Vector<HttpClientResponse>();

		// 窗口访问结果，存在request中
		@SuppressWarnings("unchecked")
		Hashtable<ApBPageContent, HttpClientResponse> ht = (Hashtable<ApBPageContent, HttpClientResponse>) request
				.getAttribute(REQUEST_KEY_REVIEW_WINDOWS);
		if (ht == null) {
			ht = new Hashtable<ApBPageContent, HttpClientResponse>();
			request.setAttribute(REQUEST_KEY_REVIEW_WINDOWS, ht);
		}

		// 会话中已经访问过的ApApp。未访问过的ApApp不能并行访问，以免造成会话问题。
		// 关于会话问题：在ap与后端应用尚未建立会话的时候，同时对后端同一个应用并行访问，会造成最终只有一个访问建立的会话有效，其它会话中的应用状态丢失
		@SuppressWarnings("unchecked")
		HashSet<ApApp> visited = (HashSet<ApApp>) request.getSession(true)
				.getAttribute(SESSION_KEY_VISITED_WINDOWS);
		if (visited == null) {
			visited = new HashSet<ApApp>();
			request.getSession(true).setAttribute(SESSION_KEY_VISITED_WINDOWS,
					visited);
		}

		final Hashtable<ApBPageContent, HttpClientResponse> theHt = ht;

		// { 找出可并行调用和必须串行调用的窗口
		Vector<ApBPageContent> parallel = new Vector<ApBPageContent>();
		Hashtable<ApBPageContent, ApApp> sequential = new Hashtable<ApBPageContent, ApApp>();
		for (ApBPageContent content : wins) {
			ApWindow win = ApWindow.getWindow(adapter, content.m_lWindowID,
					reqInfo.viewMode != EnumViewMode.VIEW);
			if (win == null)
				continue;
			ApApp app = ApApp.getApp(adapter, win.m_lAppID, false);
			if (visited.contains(app))
				parallel.add(content);
			else
				sequential.put(content, app);
		}
		// }

		// 先串行
		for (final ApBPageContent content : sequential.keySet()) {
			HttpClientResponse hcr = null;

			try {
				EnumWinMode winMode = WindowMode.getMode(request.getSession(),
						reqInfo.page.m_lPageID, content.m_strAreaName,
						content.m_lPageContID);

				boolean bIsOwner = reqInfo.psnEngine
						.eveluate(reqInfo.page.m_strClbPsnRule)
						&& reqInfo.strUserID.equals(content.m_strOwnerID);

				hcr = WindowProxy.request(
						adapter,
						reqInfo.urlConstructor,
						reqInfo.requestId,
						request,
						response,
						reqInfo.strWinView,
						content.m_lWindowID,
						content.m_lPageContID,
						content.m_lPageID,
						content.m_strAreaName,
						content.m_lBranchID,
						CombineString.combine(content.m_strWinParams,
								reqInfo.strMapParam, '~'),
						constructWinMode(winMode, reqInfo.bIsManager
								|| reqInfo.bIsClbPsn && bIsOwner),
						reqInfo.strWinOtherParams, reqInfo.userProfile
								.getProperty(IUserProfile.PROPERTY_ID),
						reqInfo.userProfile.serialize(), reqInfo.psnEngine
								.getUserAgent().serialize(),
						reqInfo.strRemoteAddr, reqInfo.strRequestUri,
						reqInfo.template.getMarkup(),
						reqInfo.viewMode == EnumViewMode.RESET,
						BPageURLConstructor.getUrlRoot(reqInfo),
						reqInfo.viewMode != EnumViewMode.VIEW);

				visited.add(sequential.get(content));
			} catch (Exception e) {
				hcr = new HttpClientResponse();
				e.printStackTrace();
			}

			vec.add(hcr);
			theHt.put(content, hcr);
		}

		// 再并行
		Vector<Thread> threads = new Vector<Thread>();

		for (final ApBPageContent content : parallel) {
			Thread t = new Thread() {
				public void run() {
					HttpClientResponse hcr = null;

					try {
						EnumWinMode winMode = WindowMode.getMode(
								request.getSession(), reqInfo.page.m_lPageID,
								content.m_strAreaName, content.m_lPageContID);

						boolean bIsOwner = reqInfo.psnEngine
								.eveluate(reqInfo.page.m_strClbPsnRule)
								&& reqInfo.strUserID
										.equals(content.m_strOwnerID);

						hcr = WindowProxy.request(
								adapter,
								reqInfo.urlConstructor,
								reqInfo.requestId,
								request,
								response,
								reqInfo.strWinView,
								content.m_lWindowID,
								content.m_lPageContID,
								content.m_lPageID,
								content.m_strAreaName,
								content.m_lBranchID,
								CombineString.combine(content.m_strWinParams,
										reqInfo.strMapParam, '~'),
								constructWinMode(winMode, reqInfo.bIsManager
										|| reqInfo.bIsClbPsn && bIsOwner),
								reqInfo.strWinOtherParams, reqInfo.userProfile
										.getProperty(IUserProfile.PROPERTY_ID),
								reqInfo.userProfile.serialize(),
								reqInfo.psnEngine.getUserAgent().serialize(),
								reqInfo.strRemoteAddr, reqInfo.strRequestUri,
								reqInfo.template.getMarkup(),
								reqInfo.viewMode == EnumViewMode.RESET,
								BPageURLConstructor.getUrlRoot(reqInfo),
								reqInfo.viewMode != EnumViewMode.VIEW);
					} catch (Exception e) {
						hcr = new HttpClientResponse();
						e.printStackTrace();
					}

					vec.add(hcr);
					theHt.put(content, hcr);
				}
			};

			t.start();
			threads.add(t);
		}

		for (Thread t : threads)
			t.join();

		return vec;
	}

	/**
	 * <pre>
	 * 为显示整个页面预加载窗口。
	 * 要加载的窗口包括：
	 * 1) 会在显示时改变用户身份的窗口 (不管窗口是否可见)
	 * 2) 可见的窗口
	 * </pre>
	 * 
	 * @param adapter
	 * @param request
	 * @param response
	 * @param reqInfo
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Vector<HttpClientResponse> preloadWindowsForPage(
			DbAdapter adapter, HttpServletRequest request,
			HttpServletResponse response, RequestInfo reqInfo) throws Exception {
		if (reqInfo.viewMode == EnumViewMode.PUBLISH
				&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC)
			return null;

		Vector<ApBPageContent> vecWins = new Vector<ApBPageContent>();

		StringTokenizer st = new StringTokenizer(reqInfo.template.m_strAreas,
				", ");
		while (st.hasMoreTokens())
			processArea(null, adapter, request, response, reqInfo,
					st.nextToken(), vecWins);

		return preloadWindows(adapter, request, response, reqInfo, vecWins);
	}

	/**
	 * <pre>
	 * 为显示单个区域预加载窗口。
	 * 要加载的窗口包括：
	 * 1) 会在显示时改变用户身份的窗口 (不管窗口是否可见)
	 * 2) 可见的窗口
	 * </pre>
	 * 
	 * @param adapter
	 * @param request
	 * @param response
	 * @param reqInfo
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Vector<HttpClientResponse> preloadWindowsForArea(
			DbAdapter adapter, HttpServletRequest request,
			HttpServletResponse response, RequestInfo reqInfo, String area)
			throws Exception {
		if (reqInfo.viewMode == EnumViewMode.PUBLISH
				&& reqInfo.branch.m_iPsnType == ApBranch.PSN_TYPE_STATIC)
			return null;

		Vector<ApBPageContent> vecWins = new Vector<ApBPageContent>();
		processArea(null, adapter, request, response, reqInfo, area, vecWins);
		return preloadWindows(adapter, request, response, reqInfo, vecWins);
	}
}
