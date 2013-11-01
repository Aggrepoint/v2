package com.aggrepoint.adk.taglib.html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAEUrlParamConst;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.WinletParamParser;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.common.CombineString;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;

/**
 * 构造窗口动态资源URL
 * 
 * 对于映射参数的特别考虑：如果页面路径中带了映射参数，需要将这些影射参数传递给资源显示代理以便继续传递给资源处理方法。
 * 
 * @author YJM
 */
public class ResProxyTag extends TagSupport implements IWinletConst {
	static final long serialVersionUID = 0;

	String m_strResource;

	public void setRes(String res) {
		m_strResource = res;
	}

	static String getRandomNumber() {
		int lo = 100000000;
		int hi = 999999999;

		int rNum = lo + (int) ((hi - lo + 1) * new Random().nextFloat());

		return String.valueOf(rNum);
	}

	public static String getUrl(WinletReqInfo reqInfo, String res)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();

		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

		if (reqInfo.m_req != null) {
			// {处理映射参数或配置参数
			WinletParamParser param = new WinletParamParser(reqInfo.m_req);
			Vector<String> vecParams = new Vector<String>();
			String params = "";
			String key;
			for (Enumeration<String> enu = param.keys(); enu.hasMoreElements();) {
				key = enu.nextElement();
				vecParams.add(key);
				vecParams.add(param.getParam(key));
			}
			params = CombineString.combine(vecParams, '~');
			// }

			if (!reqInfo.m_bViaAE) { // 不是被AggrePoint Engine调用
				sb.append(reqInfo.m_req.getContext().getResourceRootPath())
						.append(reqInfo.m_req.getControlPath())
						.append(reqInfo.m_req.getDef().m_parent.m_strFullPath)
						.append("/").append(res).append("?r=")
						.append(getRandomNumber()).append("&")
						.append(REQUEST_PARAM_WIN_INST_ID).append("=")
						.append(reqInfo.m_req.getWinIID()).append("&param=")
						.append(URLEncoder.encode(params, "UTF-8"));
				if (view != null)
					sb.append("&").append(REQUEST_PARAM_VIEW_ID).append("=")
							.append(view.getId());
			} else { // 被AggrePoint Engine调用
				sb.append("?r=").append(getRandomNumber()).append("&")
						.append(IAEUrlParamConst.PARAM_WIN_ID).append("=")
						.append(reqInfo.m_req.getWinIID()).append("&")
						.append(IAEUrlParamConst.PARAM_WIN_RES).append("=")
						.append(res);
				if (view != null)
					sb.append("&").append(IAEUrlParamConst.PARAM_WIN_VIEW)
							.append("=").append(view.getId());
				if (params != null && !params.equals(""))
					sb.append("&").append(IAEUrlParamConst.PARAM_WIN_PARAM)
							.append("=")
							.append(URLEncoder.encode(params, "UTF-8"));

				if (reqInfo.m_markup == EnumMarkup.WML)
					sb = new StringBuffer(
							StringUtils.toXMLString(sb.toString()));
			}
		}

		return sb.toString();
	}

	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(
					getUrl(WinletReqInfo.getInfo(pageContext.getRequest()),
							m_strResource));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
