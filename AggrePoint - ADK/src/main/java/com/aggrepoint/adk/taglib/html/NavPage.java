package com.aggrepoint.adk.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.icebean.core.adb.ADBList;

public class NavPage extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	String m_strPid;
	ADBList<?> m_list;
	int m_iSize;
	int m_iSt, m_iEd, m_iCurr;

	public void setPid(String pid) {
		m_strPid = pid;
	}

	public void setList(ADBList<?> list) {
		m_list = list;
	}

	public void setSize(int size) {
		m_iSize = size;
	}

	public int doStartTag() throws JspException {
		if (m_list.m_iPageCount == 0)
			return SKIP_BODY;

		m_iSt = m_list.m_iPageNo - m_iSize / 2;
		if (m_iSt + m_iSize - 1 > m_list.m_iPageCount)
			m_iSt = m_list.m_iPageCount - m_iSize + 1;
		if (m_iSt < 1)
			m_iSt = 1;
		m_iEd = m_iSt + m_iSize - 1;
		if (m_iEd > m_list.m_iPageCount)
			m_iEd = m_list.m_iPageCount;

		if (m_iSt > 1)
			m_iCurr = m_list.m_iPageNo - m_iSize;
		else
			m_iCurr = m_iSt;

		pageContext.setAttribute(m_strPid, new Integer(m_iCurr));
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();

		if (m_iCurr < m_iSt)
			m_iCurr = m_iSt;
		else if (m_iCurr >= m_iSt && m_iCurr < m_iEd)
			m_iCurr++;
		else if (m_iCurr == m_iEd && m_iCurr < m_list.m_iPageCount)
			m_iCurr = m_list.m_iPageNo + m_iSize;
		else
			return SKIP_BODY;

		pageContext.setAttribute(m_strPid, new Integer(m_iCurr));
		return EVAL_BODY_BUFFERED;
	}
}
