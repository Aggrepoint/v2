package com.aggrepoint.ae.taglib.html.btmpl;

import java.sql.Connection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 构造模板资源的URL
 * 
 * @author YJM
 */
public class ResUrlTag extends TagSupport implements ReqAttrConst {
	static final long serialVersionUID = 0;

	String m_strName;

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			RequestInfo reqInfo = (RequestInfo) pageContext.getRequest()
					.getAttribute(ATTR_REQUEST_INFO);

			if (reqInfo.page != null) {
				Connection conn = null;

				try {
					conn = DBConnManager.getConnection();
					DbAdapter adapter = new DbAdapter(conn);

					out.print(reqInfo.urlConstructor.bpageTmplRes(adapter,
							reqInfo.page, reqInfo.template, m_strName));
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
		} catch (Exception e) {
			e.printStackTrace();

			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
