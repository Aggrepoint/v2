package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;

/**
 * 
 * @author YJM
 */
public class ElementAttribute extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	protected String m_strName;

	protected String m_strValue;

	protected String m_strTest;

	public void setName(String name) {
		m_strName = name;
	}

	public void setValue(String str) {
		m_strValue = str;
	}

	public void setTest(String test) {
		m_strTest = test;
	}

	public int doStartTag() throws JspException {
		try {
			if (m_strTest == null || m_strTest.equals("true")) {
				Element sel = (Element) TagSupport.findAncestorWithClass(this,
						Element.class);
				sel.vecAttrNames.add(m_strName);
				sel.vecAttrValues.add(m_strValue);
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}