package com.aggrepoint.adk.taglib.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import com.icebean.core.common.StringUtils;

/**
 * 表格中的按钮
 * 
 * @author YJM
 */
public class TableLinkTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	protected String strAction;
	protected String strParam;
	protected String strOnClick;
	protected String strConfirm;
	protected String strAttrs = "";
	protected String strBody = "";

	public String getAction() {
		return strAction;
	}

	public void setAction(String action) {
		strAction = action;
	}

	public String getParam() {
		return strParam;
	}

	public void setParam(String param) {
		strParam = param;
	}

	public String getOnclick() {
		return strOnClick;
	}

	public void setOnclick(String onClick) {
		strOnClick = onClick;
	}

	public String getConfirm() {
		return strConfirm;
	}

	public void setConfirm(String confirm) {
		strConfirm = confirm;
	}

	public String getAttrs() {
		return strAttrs;
	}

	public void setAttrs(String attrs) {
		strAttrs = attrs;
	}

	public String getBody() {
		return strBody;
	}

	public void setBody(String body) {
		strBody = body;
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

	@Override
	public int doAfterBody() {
		strBody = getBodyContent().getString();

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		try {
			JspWriter out = pageContext.getOut();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}
}