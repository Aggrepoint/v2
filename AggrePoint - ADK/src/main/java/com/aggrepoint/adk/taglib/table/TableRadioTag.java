package com.aggrepoint.adk.taglib.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import com.icebean.core.common.StringUtils;

/**
 * 表格中的单选框
 * 
 * @author YJM
 */
public class TableRadioTag extends TagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	protected String strName;
	protected String strValue;
	protected String strAttrs = "";

	public String getName() {
		return strName;
	}

	public void setName(String name) {
		strName = name;
	}

	public String getValue() {
		return strValue;
	}

	public void setValue(String value) {
		strValue = value;
	}

	@Override
	public void setDynamicAttribute(String arg0, String arg1, Object arg2)
			throws JspException {
		if (arg1 == null || arg2 == null)
			return;

		strAttrs += " " + arg1 + "='" + StringUtils.toHTML(arg2.toString())
				+ "'";
	}

	@Override
	public int doStartTag() throws JspException {
		TableTag table = (TableTag) TagSupport.findAncestorWithClass(this,
				TableTag.class);

		return (SKIP_BODY);
	}
}