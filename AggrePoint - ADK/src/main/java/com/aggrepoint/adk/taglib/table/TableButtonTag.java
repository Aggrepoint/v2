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
public class TableButtonTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	protected String strTitle;
	protected String strAction;
	protected String strParam;
	protected String strCheckParam;
	protected String strImage;
	protected String strOnClick;
	protected String strNoSelect;
	protected String strConfirm;
	protected String strAttrs = "";
	protected String strBody = "";

	public String getTitle() {
		return strTitle;
	}

	public void setTitle(String title) {
		strTitle = title;
	}

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

	public String getCheckparam() {
		return strCheckParam;
	}

	public void setCheckparam(String checkParam) {
		strCheckParam = checkParam;
	}

	public String getImage() {
		return strImage;
	}

	public void setImage(String image) {
		this.strImage = image;
	}

	public String getOnclick() {
		return strOnClick;
	}

	public void setOnclick(String onClick) {
		strOnClick = onClick;
	}

	public String getNoselect() {
		return strNoSelect;
	}

	public void setNoselect(String noSelect) {
		strNoSelect = noSelect;
	}

	public String getStrConfirm() {
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