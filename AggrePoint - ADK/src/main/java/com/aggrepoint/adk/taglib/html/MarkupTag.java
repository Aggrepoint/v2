package com.aggrepoint.adk.taglib.html;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.icebean.core.common.ThreadContext;

/**
 * 用于声明使用的标记语言
 * 
 * 虽然AE在请求中会声明请求的标记语言类型，但应用端返回的页面不一定符合请求的标记类型。例如，请求中说明请求XHTML但应用不支持XHTML只能返回HTML
 * 。 页面可以使用本标记明确声明页面的语言类型。若页面没有使用本标记明确声明标记类型，则视为页面标记类型与请求类型一致。
 * 该标记在页面中必须先于其他ADK标记出现
 * 
 * @author YJM
 */
public class MarkupTag extends TagSupport implements IAdkConst {
	static final long serialVersionUID = 0;

	private static final String MARKUP_REQ_KEY = MarkupTag.class.getName()
			+ ".MARKUP";

	EnumMarkup markup;

	public void setType(String mk) {
		markup = EnumMarkup.fromName(mk);
	}

	public static EnumMarkup getMarkup(ServletRequest req) {
		Integer i = (Integer) req.getAttribute(MARKUP_REQ_KEY);
		if (i == null)
			return null;
		return EnumMarkup.fromId(i);
	}

	public static String getMarkupId(ServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getStrId();
	}

	public static String getMarkupName(ServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getName();
	}

	public static EnumMarkup getMarkup(IModuleRequest req) {
		Integer i = (Integer) req.getAttribute(MARKUP_REQ_KEY);
		if (i == null)
			return null;
		return EnumMarkup.fromId(i);
	}

	public static String getMarkupId(IModuleRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getStrId();
	}

	public static String getMarkupName(IModuleRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getName();
	}

	public static EnumMarkup getMarkup() {
		return getMarkup((IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST));
	}

	public static String getMarkupId() {
		return getMarkupId((IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST));
	}

	public static String getMarkupName() {
		return getMarkupName((IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST));
	}

	public int doStartTag() throws JspException {
		try {
			pageContext.getRequest().setAttribute(MARKUP_REQ_KEY,
					new Integer(markup.getId()));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
