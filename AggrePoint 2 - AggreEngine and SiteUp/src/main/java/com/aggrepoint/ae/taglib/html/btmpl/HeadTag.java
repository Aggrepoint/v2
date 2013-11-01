package com.aggrepoint.ae.taglib.html.btmpl;

import java.sql.Connection;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApRes;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 
 * 
 * @author YJM
 */
public class HeadTag extends BodyTagSupport implements ReqAttrConst, UrlConst {
	static final long serialVersionUID = 0;

	/** 信息Boundle */
	public static com.icebean.core.msg.MessageBoundle m_msg = com.icebean.core.msg.MessageManager
			.getMessageBoundleSilent();

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.print(m_msg.getMessage("headStart"));

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);
			EnumMarkup markup = EnumMarkup
					.fromId(reqInfo.template.m_iMarkupType);
			boolean bHtml = markup == EnumMarkup.HTML;
			boolean bXhtml = markup == EnumMarkup.XHTML;

			if ((reqInfo.bViaProxy || reqInfo.viewMode == EnumViewMode.PUBLISH)
					&& reqInfo.site.m_strPublishResDir != null) {
				if (bHtml) {
					out.println(m_msg.constructMessage("style_html_apache",
							reqInfo.site.m_strStaticResUrl));
					out.println(m_msg.constructMessage("script_html_apache",
							reqInfo.site.m_strStaticResUrl));
					if (reqInfo.viewMode == EnumViewMode.PUBLISH)
						out.println(m_msg.constructMessage(
								"script_html_static",
								reqInfo.site.m_strStaticResUrl));
				}

				if (bXhtml)
					out.println(m_msg.constructMessage("style_xhtml_apache",
							reqInfo.site.m_strStaticResUrl));
			} else {
				if (bHtml) {
					out.println(m_msg.getMessage("style_html"));
					out.println(m_msg.getMessage("script_html"));
				}

				if (bXhtml)
					out.println(m_msg.getMessage("style_xhtml"));
			}

			if (reqInfo.viewMode == EnumViewMode.EDIT) {
				out.println(m_msg.getMessage("style_edit"));

				// {输出页面编辑脚本
				out.println(m_msg.getMessage("script_edit"));
				out.println(m_msg.constructMessage("script_setPageID",
						new Long(reqInfo.currentPageInTree.m_lPageID)
								.toString()));
				// }
			}

			// 输出应用引用的资源
			Vector<ApRes> vec = new Vector<ApRes>();
			for (ApRes res : reqInfo.branch.m_vecAppReses)
				if (res.m_strFileName.endsWith(".css"))
					vec.add(res);
				else if (res.m_strFileName.endsWith(".js"))
					vec.add(res);

			if (vec.size() != 0) {
				Connection conn = null;

				try {
					conn = DBConnManager.getConnection();
					DbAdapter adapter = new DbAdapter(conn);

					for (ApRes res : vec) {
						if (res.m_strFileName.endsWith(".css")) {
							out.println(m_msg.constructMessage("style_app",
									reqInfo.urlConstructor.res(adapter, res)));
						} else if (res.m_strFileName.endsWith(".js")) {
							out.println(m_msg.constructMessage("script_app",
									reqInfo.urlConstructor.res(adapter, res)));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null)
						try {
							conn.close();
						} catch (Exception e) {
						}
				}
			}

			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.println(m_msg.getMessage("headEnd"));

			return EVAL_PAGE;
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}
}
