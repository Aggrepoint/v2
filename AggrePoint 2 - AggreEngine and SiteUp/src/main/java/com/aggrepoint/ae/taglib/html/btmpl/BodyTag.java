package com.aggrepoint.ae.taglib.html.btmpl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.RuleConst;

/**
 * 
 * 
 * @author YJM
 */

public class BodyTag extends BodyTagSupport implements ReqAttrConst, UrlConst,
		RuleConst {
	static final long serialVersionUID = 0;

	String m_strAttr;

	public BodyTag() {
		m_strAttr = null;
	}

	public void setAttr(String attr) {
		m_strAttr = attr;
	}

	/** 信息Boundle */
	public static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.print("<body");
			if (m_strAttr != null) {
				out.print(" ");
				out.print(m_strAttr);
			}
			out.print(">");

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			if (reqInfo.viewMode == EnumViewMode.EDIT) {
				out.println();
				if (reqInfo.psnEngine.eveluate(SU_ROOT)
						|| reqInfo.psnEngine
								.eveluate(reqInfo.branch.m_strManageRule)) {
					out.print(m_msg.constructMessage("edit_start_root",
							new Long(reqInfo.branch.m_rootPage.m_lPageID)
									.toString()));
				} else
					out.print(m_msg.getMessage("edit_start_not_root"));
			}

			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			if (reqInfo.viewMode == EnumViewMode.EDIT)
				out.println(m_msg.getMessage("edit_end"));

			out.print("</body>");

			return EVAL_PAGE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}
}
