package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.icebean.core.common.ThreadContext;

/**
 * 用于在Window页面中构造访问URI的URL
 * 
 * @author YJM
 */
public class UrlTag extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String m_strPath;

	public void setPath(String path) {
		m_strPath = path;
	}

	public int doStartTag() throws JspException {
		try {
			HttpModuleRequest req = (HttpModuleRequest) ThreadContext
					.getAttribute(THREAD_ATTR_REQUEST);

			if (req != null) {
				JspWriter out = pageContext.getOut();

				StringBuffer sb = new StringBuffer();

				sb.append(req.getServerNamePort()).append(
						req.getContext().getResourceRootPath()).append(
						m_strPath);

				out.print(sb.toString());
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
