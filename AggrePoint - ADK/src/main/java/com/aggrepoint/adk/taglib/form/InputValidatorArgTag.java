package com.aggrepoint.adk.taglib.form;

import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;

public class InputValidatorArgTag extends BodyTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	InputValidatorTag validator;

	public int doStartTag() throws JspException {
		validator = (InputValidatorTag) TagSupport.findAncestorWithClass(this,
				InputValidatorTag.class);
		if (validator == null)
			return SKIP_BODY;
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		StringWriter writer = new StringWriter();
		try {
			bodyContent.writeOut(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		validator.vecArgs.add(writer.toString());
		bodyContent.clearBody();
		return SKIP_BODY;
	}
}