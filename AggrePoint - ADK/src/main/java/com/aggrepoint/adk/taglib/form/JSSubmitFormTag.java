package com.aggrepoint.adk.taglib.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.common.ThreadContext;

/**
 * 用于生成可支持Ajax表单提交Javascript脚本
 * 
 * @author YJM
 */
public class JSSubmitFormTag extends TagSupport implements IWinletConst {
	private static final long serialVersionUID = 1L;

	/** 用于指定要提交的表单的名称 */
	String m_strForm = null;

	/** 用于指定要提交的表单的对象 */
	String m_strName = null;

	public void setName(String name) {
		m_strName = name;
	}

	public void setForm(String form) {
		m_strForm = form;
	}

	public int doStartTag() throws JspException {
		try {
			ViewInstance view = (ViewInstance) ThreadContext
					.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

			if (m_strName == null && m_strForm == null)
				throw new JspException("必须指定name或者form属性。");

			WinletReqInfo reqInfo = WinletReqInfo.getInfo(pageContext
					.getRequest());

			if (reqInfo.m_req != null) {
				JspWriter out = pageContext.getOut();

				String str = null;
				if (m_strForm != null) {
					str = "document." + m_strForm;
					str += reqInfo.m_req.getWinIID();
					if (view != null)
						str += view.getId();
				} else
					str = m_strName;

				if (reqInfo.m_markup == EnumMarkup.HTML) {
					out.print("if (");
					out.print(str);
					out.print(".onsubmit()) ");
				}
				out.print(str);
				out.print(".submit();");
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
