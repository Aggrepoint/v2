package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.icebean.core.common.ThreadContext;

/**
 * 用于在Window页面中将名称加上窗口ID
 * 
 * @author YJM
 */
public class EncodeNamespaceTag extends TagSupport implements IAdkConst,
		IWinletConst {
	private static final long serialVersionUID = 1L;

	String m_strValue;

	public void setValue(String value) {
		m_strValue = value;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			out.print(m_strValue);

			IModuleRequest req = (IModuleRequest) ThreadContext
					.getAttribute(THREAD_ATTR_REQUEST);
			ViewInstance view = (ViewInstance) ThreadContext
					.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

			if (req != null)
				out.print(req.getWinIID());
			if (view != null)
				out.print(view.getId());
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
