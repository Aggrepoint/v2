package com.aggrepoint.adk.taglib.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;

/**
 * 
 * @author YJM
 */
public class InputAttrTag extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String strName;
	Object objValue;

	public void setName(String str) {
		strName = str;
	}

	public void setValue(Object str) {
		objValue = str;
	}

	public int doStartTag() throws JspException {
		try {
			GetInputTag getInput = (GetInputTag) TagSupport
					.findAncestorWithClass(this, GetInputTag.class);
			if (getInput != null && objValue != null)
				getInput.htAttrs.put(strName, objValue);
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}

		return SKIP_BODY;
	}
}