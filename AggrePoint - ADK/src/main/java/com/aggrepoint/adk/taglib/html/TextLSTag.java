package com.aggrepoint.adk.taglib.html;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.msg.MessageBoundle;

public class TextLSTag extends BodyTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String lsid;

	public void setId(String lsid) {
		this.lsid = lsid;
	}

	public int doStartTag() throws JspException {
		IModuleRequest request = (IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		Vector<String> lss = LocaleManager.getLSIDs(TextLSTag.class, null);
		if (request != null && lss.contains(lsid))
			return EVAL_BODY_BUFFERED;
		else
			return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		TextTag text = (TextTag) TagSupport.findAncestorWithClass(this,
				TextTag.class);
		BodyContent body = getBodyContent();
		try {
			getPreviousOut().write(
					MessageBoundle.constructMessageStatic(body.getString(),
							text.keys.toArray(new String[text.keys.size()])));
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		return SKIP_BODY;
	}
}
