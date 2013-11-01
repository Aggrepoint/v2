package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAEUrlParamConst;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;

/**
 * 用于在Window页面中构造窗口Action的URL
 * 
 * @author YJM
 */
public class ActionUrlTag extends TagSupport implements IAdkConst, IWinletConst {
	static final long serialVersionUID = 0;

	String m_strAction;

	public void setAction(String action) {
		m_strAction = action;
	}

	public static String getUrl(WinletReqInfo reqInfo, String action) {
		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

		if (reqInfo.m_req != null) {
			StringBuffer sb = new StringBuffer();

			if (!reqInfo.m_bViaAE) { // 不是被AggrePoint
				// Engine调用，生成窗口动作URL
				sb.append(reqInfo.m_req.getContext().getResourceRootPath())
						.append(reqInfo.m_req.getControlPath())
						.append(reqInfo.m_req.getDef().m_parent.m_strFullPath)
						.append("?").append(REQUEST_PARAM_WIN_INST_ID)
						.append("=").append(reqInfo.m_req.getWinIID());
				if (view != null)
					sb.append("&").append(REQUEST_PARAM_VIEW_ID).append("=")
							.append(view.getId());
				sb.append("&").append(REQUEST_PARAM_ACTION).append("=")
						.append(action);

				return sb.toString();
			} else { // 被AggrePoint Engine调用，生成AE窗口动作URL
				sb.append("?").append(IAEUrlParamConst.PARAM_WIN_ACTION)
						.append("=").append(reqInfo.m_req.getWinIID())
						.append("!");
				if (view != null)
					sb.append(view.getId());
				sb.append("!").append(action);

				if (reqInfo.m_markup == EnumMarkup.WML)
					return StringUtils.toXMLString(sb.toString());
				else
					return sb.toString();
			}
		}

		return "";
	}

	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(
					getUrl(WinletReqInfo.getInfo(pageContext.getRequest()),
							m_strAction));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
