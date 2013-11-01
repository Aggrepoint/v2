package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.common.StringUtils;
import com.icebean.core.common.ThreadContext;

/**
 * 用于在Window页面中构造资源（如图片）的URL
 * 
 * @author YJM
 */
public class ResourceUrlTag extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String m_strResource;

	String m_strPath;

	String m_strName;

	public void setRes(String res) {
		m_strResource = res;
	}

	public void setPath(String path) {
		m_strPath = path;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public static String getUrl(WinletReqInfo reqInfo, String resource,
			String path, String name, boolean isStatic) {
		if (resource == null) {
			resource = path;
			if (name != null)
				resource += name;
		}

		if (resource == null)
			return "";

		if (reqInfo.m_req != null) {
			if (isStatic && reqInfo.m_strStaticWinResUrl != null
					&& !reqInfo.m_strStaticWinResUrl.equals("")) // 静态内容，而且前端提供了静态资源主路径
				return StringUtils.appendUrl(reqInfo.m_strStaticWinResUrl,
						resource);

			// 动态内容或者前端没有提供静态资源主路径，通过代理访问
			return StringUtils.appendUrl("/ap2/rp/" + reqInfo.m_strAppId,
					StringUtils.appendUrl(reqInfo.m_req.getContext()
							.getResourceRootPath(), resource));
		}

		return StringUtils.appendUrl(
				((HttpModuleRequest) reqInfo.m_req).getServerNamePort()
						+ reqInfo.m_req.getContext().getResourceRootPath(),
				resource);
	}

	public static String getUrl(WinletReqInfo reqInfo, String resource,
			String path, String name) {
		return getUrl(reqInfo, resource, path, name, false);
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			if (m_strResource != null && m_strResource.indexOf("//") != -1) { // 如果资源使用了绝对路径，则不加处理
				out.print(m_strResource);
			} else if (m_strPath != null && m_strPath.indexOf("//") != -1) { // 如果资源使用了绝对路径，则不加处理
				out.print(m_strPath);
				if (m_strName != null)
					out.print(m_strName);
			} else {
				if (WinletReqInfo.isInWinlet())
					out.print(getUrl(
							WinletReqInfo.getInfo(pageContext.getRequest()),
							m_strResource, m_strPath, m_strName));
				else { // 不是被Winlet使用
					HttpModuleRequest req = (HttpModuleRequest) ThreadContext
							.getAttribute(THREAD_ATTR_REQUEST);

					if (req != null) {
						StringBuffer sb = new StringBuffer();

						sb.append(req.getServerNamePort()).append(
								req.getContext().getResourceRootPath());

						if (m_strResource != null)
							sb.append(m_strResource);
						else {
							sb.append(m_strPath);
							sb.append(m_strName);
						}

						out.print(sb.toString());
					}
				}
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
