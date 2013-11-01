package com.aggrepoint.adk.taglib.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import com.icebean.core.beanutil.NestedBeanProperty;
import com.icebean.core.common.StringUtils;

/**
 * 用于生成表格列
 * 
 * @author YJM
 */
public class TableColumnTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	protected String strTitle;
	/** 属性 */
	protected String strProperty;
	protected String strSortAsc;
	protected String strSortDesc;
	protected String strAttrs = "";
	protected String strBody = "";

	public void setTitle(String str) {
		strTitle = str;
	}

	public void setProperty(String str) {
		strProperty = str;
		if (strProperty == null || strProperty.trim().equals(""))
			strProperty = null;
	}

	public void setSortasc(String str) {
		strSortAsc = str;
	}

	public void setSortdesc(String str) {
		strSortDesc = str;
	}

	public String getSortasc() {
		return strSortAsc;
	}

	public String getSortdesc() {
		return strSortDesc;
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

		if (table == null)
			return SKIP_BODY;

		strBody = "";

		return EVAL_BODY_BUFFERED;
	}

	public String getContent(Object obj) throws Exception {
		if (strProperty == null)
			return strBody;

		Object value = new NestedBeanProperty(obj, strProperty, true, false)
				.get(obj);

		if (value == null)
			return "";
		return StringUtils.toHTML(value.toString());
	}

	@Override
	public int doAfterBody() {
		strBody = getBodyContent().getString();

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		TableTag table = (TableTag) TagSupport.findAncestorWithClass(this,
				TableTag.class);

		table.addColumn(this);

		return EVAL_PAGE;
	}
}