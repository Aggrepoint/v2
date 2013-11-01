package com.aggrepoint.adk.taglib.form;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IAEUrlParamConst;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.WinletHelper;
import com.aggrepoint.adk.form.FormImpl;
import com.aggrepoint.adk.form.InputImpl;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.common.ThreadContext;

/**
 * 用于在Window页面中构造Form
 * 
 * @author YJM
 */
public class FormTag extends BodyTagSupport implements IAdkConst, IWinletConst {
	private static final long serialVersionUID = 1L;

	protected String m_strName;

	protected String m_strAction;

	protected String m_strTarget;

	protected String m_strMethod;

	protected String m_strOnSubmit;

	protected String m_strEncType;

	protected String m_strWinIID;

	protected String m_strFocus;

	protected String m_strValidate;

	protected String m_strTooltipClass;

	protected String m_strTooltipSelector;

	protected String m_strTooltipConfig;

	protected boolean m_bEnableTooltip;

	protected String m_strValidateAction;

	protected Object m_objResetRef;

	protected boolean m_bHideLoading;

	/** 表单对象 */
	protected FormImpl m_form;

	protected InputImpl m_currentInput;

	/** 由嵌套在FormTag中的FunctionTag生成的脚本 */
	Hashtable<String, String> m_htScripts;
	/** 由嵌套在FormTag中的FunctionTag生成的，要用adkform函数生成的函数 */
	Hashtable<String, String> m_htAdkformScripts;

	public void setName(String name) {
		m_strName = name;
	}

	public void setAction(String action) {
		m_strAction = action;
	}

	public void setTarget(String str) {
		m_strTarget = str;
	}

	public void setMethod(String str) {
		m_strMethod = str;
	}

	public void setOnsubmit(String str) {
		m_strOnSubmit = str;
	}

	public void setEnctype(String str) {
		m_strEncType = str;
	}

	public void setFocus(String focus) {
		m_strFocus = focus;
	}

	public void setValidate(String validate) {
		m_strValidate = validate;
	}

	protected void checkTooltip() {
		m_bEnableTooltip = m_strTooltipClass != null
				&& m_strTooltipSelector != null && m_strTooltipConfig != null;
	}

	public void setTipclz(String val) {
		m_strTooltipClass = val;
		checkTooltip();
	}

	public void setTipsel(String val) {
		m_strTooltipSelector = val;
		checkTooltip();
	}

	public void setTipcfg(String val) {
		m_strTooltipConfig = val;
		checkTooltip();
	}

	public String getName() {
		return m_strName;
	}

	public void setVaction(String val) {
		m_strValidateAction = val;
	}

	public void setResetref(Object obj) {
		m_objResetRef = obj;
	}

	public void setHideloading(String val) {
		m_bHideLoading = val != null && val.equalsIgnoreCase("yes");
	}

	public void setScript(String func, String content) {
		if (m_htScripts == null) {
			m_htScripts = new Hashtable<String, String>();
			m_htAdkformScripts = new Hashtable<String, String>();
		}
		m_htScripts.put(func, content);
		m_htAdkformScripts.remove(func);
	}

	public void setAdkformScript(String func, String def) {
		if (m_htAdkformScripts == null) {
			m_htScripts = new Hashtable<String, String>();
			m_htAdkformScripts = new Hashtable<String, String>();
		}
		m_htAdkformScripts.put(func, def);
		m_htScripts.remove(func);
	}

	public int doStartTag() throws JspException {
		m_htScripts = null;
		m_htAdkformScripts = null;

		ViewInstance view = (ViewInstance) ThreadContext
				.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

		m_form = WinletHelper.getForm((IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST), view.getWinlet(), view
				.getViewDef().m_strPath, ((HttpServletRequest) this.pageContext
				.getRequest()).getRequestURI(), m_strName);
		m_form.setAction(m_strAction);
		if (m_objResetRef != m_form.getResetRef()) {
			m_form.reset();
			m_form.setResetRef(m_objResetRef);
		}

		try {
			WinletReqInfo reqInfo = WinletReqInfo.getInfo(pageContext
					.getRequest());
			boolean bMultipart = m_strEncType != null
					&& m_strEncType.equalsIgnoreCase("multipart/form-data");

			if (reqInfo.m_req != null) {
				JspWriter out = pageContext.getOut();

				// {Name
				out.print("<form name=\"");
				out.print(m_strName);
				out.print(reqInfo.m_req.getWinIID());
				if (view != null)
					out.print(view.getId());
				// }

				out.print("\" id=\"");
				out.print(m_strName);
				out.print(reqInfo.m_req.getWinIID());
				if (view != null)
					out.print(view.getId());
				out.print("\"");

				if (m_bHideLoading)
					out.print(" hideloading=\"yes\"");

				// {Action
				if (!reqInfo.m_bViaAE) { // 不是被AE调用
					out.print(" action=\"");
					out.print(reqInfo.m_req.getContext().getResourceRootPath());
					out.print(reqInfo.m_req.getControlPath());
					out.print(reqInfo.m_req.getDef().m_parent.m_strFullPath);
					out.print("?");
					out.print(REQUEST_PARAM_WIN_INST_ID);
					out.print("=");
					out.print(reqInfo.m_req.getWinIID());
					out.print("&");
					out.print(REQUEST_PARAM_ACTION);
					out.print("=");
					out.print(m_strAction);
					out.print("&");
					out.print(REQUEST_PARAM_FORM);
					out.print("=");
					out.print(m_form.getId());
					if (view != null) {
						out.print("&");
						out.print(REQUEST_PARAM_VIEW_ID);
						out.print("=");
						out.print(view.getId());
					}
					out.print("\"");
				} else if (bMultipart) {
					// Multipart方式不能将参数作为input传递
					out.print(" action=\"");
					out.print(reqInfo.m_req
							.getHeader(REQUEST_HEADER_REQUEST_URI));
					out.print("?");
					out.print(IAEUrlParamConst.PARAM_WIN_ACTION);
					out.print("=");
					out.print(reqInfo.m_req.getWinIID());
					out.print("!");
					if (view != null)
						out.print(view.getId());
					out.print("!");
					out.print(m_strAction);
					out.print("!");
					out.print(m_form.getId());
					out.print("\"");
				} else {
					out.print(" action=\"");
					out.print(reqInfo.m_req
							.getHeader(REQUEST_HEADER_REQUEST_URI));
					out.print("\"");
				}
				// }

				// {Method
				if (m_strMethod == null)
					out.print(" method=\"post\"");
				else {
					out.print(" method=\"");
					out.print(m_strMethod);
					out.print("\"");
				}
				// }

				// Enctype
				if (m_strEncType != null && !m_strEncType.equals("")) {
					out.print(" enctype=\"");
					out.print(m_strEncType);
					out.print("\"");
				}

				out.println(">");

				if (reqInfo.m_bViaAE && !bMultipart) {
					// Multipart方式不能将参数作为input传递
					out.print("<input type=\"hidden\" name=\"");
					out.print(IAEUrlParamConst.PARAM_WIN_ACTION);
					out.print("\" value=\"");
					out.print(reqInfo.m_req.getWinIID());
					out.print("!");
					if (view != null)
						out.print(view.getId());
					out.print("!");
					out.print(m_strAction);
					out.print("!");
					out.print(m_form.getId());
					out.print("\"");
					if (reqInfo.m_markup == EnumMarkup.XHTML)
						out.println("/>");
					else
						out.println(">");
				}
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		try {
			WinletReqInfo reqInfo = WinletReqInfo.getInfo(pageContext
					.getRequest());

			ViewInstance view = (ViewInstance) ThreadContext
					.getAttribute(THREAD_ATTR_VIEW_INSTANCE);
			String name = m_strName + reqInfo.m_req.getWinIID()
					+ (view == null ? "" : view.getId());

			if (reqInfo.m_req != null) {
				JspWriter out = getPreviousOut();

				bodyContent.writeOut(out);
				out.println("</form>");

				if (reqInfo.m_markup == EnumMarkup.HTML) {
					out.println("<script language=\"javascript\" defer>");
					if (m_htScripts != null) {
						// 输出由FunctionTag生成的脚本
						for (Iterator<String> it = m_htScripts.values()
								.iterator(); it.hasNext();)
							out.println(it.next());
					}

					StringBuffer sb = new StringBuffer();
					sb.append("area: '" + reqInfo.m_strWinAreaName
							+ "', iid: '" + reqInfo.m_req.getWinIID()
							+ "', vid: '" + view.getId() + "'");
					if (m_strFocus != null)
						sb.append(", focus: '" + m_strFocus + "'");
					if (m_bEnableTooltip)
						sb.append(", tipclz: '" + m_strTooltipClass
								+ "', tipsel: \"" + m_strTooltipSelector
								+ "\", tipcfg: \"" + m_strTooltipConfig + "\"");
					if (m_strValidateAction != null
							&& !m_strValidateAction.equals(""))
						sb.append(", vaction: '" + m_strValidateAction
								+ "', formid: \"" + m_form.getId() + "\"");
					if (m_htAdkformScripts != null) {
						boolean bFirst = true;

						sb.append(", func :[");
						for (String func : m_htAdkformScripts.values()) {
							if (bFirst)
								bFirst = false;
							else
								sb.append(", ");
							sb.append(func);
						}
						sb.append("]");
					}

					out.print("$(function() {$(\"#");
					out.print(name);
					out.print("\").adkform({" + sb.toString() + "});});");

					out.print("</script>");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return SKIP_BODY;
		}

		bodyContent.clearBody();
		return SKIP_BODY;
	}
}