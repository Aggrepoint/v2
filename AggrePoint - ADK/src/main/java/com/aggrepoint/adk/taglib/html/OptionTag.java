package com.aggrepoint.adk.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAdkConst;
import com.icebean.core.common.StringUtils;

/**
 * 用于在Window页面中构造Form
 * 
 * @author YJM
 */
public class OptionTag extends SimpleTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	protected String m_strValue;

	protected String m_strName;

	protected String m_strSelected;

	public void setValue(String value) {
		m_strValue = value;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public void setSelected(String selected) {
		m_strSelected = selected;
	}

	public void doTag() throws JspException {
		try {
			JspWriter out = getJspContext().getOut();
			out.print("<option value=\"");
			out.print(m_strValue);
			out.print("\"");
			if (m_strSelected != null && m_strSelected.equalsIgnoreCase("true"))
				out.print(" selected=\"selected\"");
			out.print(">");
			out.print(MarkupTag.getMarkup() == EnumMarkup.WML ? StringUtils.toWML(m_strName) : StringUtils.toHTML(m_strName));
			out.print("</option>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}
}