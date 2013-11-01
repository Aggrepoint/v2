package com.aggrepoint.adk.taglib.html;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.ViewInstance;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.common.TypeCast;
import com.icebean.core.servlet.BufferedResponseWrapper;

/**
 * 
 * @author YJM
 */
public class IncludeTag extends BodyTagSupport implements IAdkConst,
		IWinletConst {
	private static final long serialVersionUID = 1L;

	String var;

	String vars;

	String m_strView;

	Winlet winlet;

	Hashtable<String, String> m_params;

	public void setVar(String var) {
		this.var = var;
	}

	public void setVars(String var) {
		this.vars = var;
	}

	public void setView(String view) {
		m_strView = view;
	}

	public void setWinlet(Winlet winlet) {
		this.winlet = winlet;
	}

	/**
	 * 跟踪Apache代码发现Apache在include或forward过程中使用很多Request值，因此
	 * 不能简单的模拟一个全新的request环境。<br>
	 * 
	 * 虽然目前RequestWrapper没有实现任何功能，保留它以便需要时添加
	 * 
	 * @author Owner
	 * 
	 */
	public static class RequestWrapper extends HttpServletRequestWrapper {
		String viewId;
		String requestPath;

		public RequestWrapper(HttpServletRequest request, String viewId,
				String requestPath) {
			super(request);
			this.viewId = viewId;
			this.requestPath = requestPath;
		}

		public String getHeader(String name) {
			if (name.equals(REQUEST_HEADER_VIEW_HEADER_ID))
				return viewId;
			if (name.equals(REQUEST_HEADER_VIEW_HEADER_REQ_PATH))
				return requestPath;
			return super.getHeader(name);
		}
	}

	@Override
	public int doStartTag() {
		m_params = new Hashtable<String, String>();
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			HttpModuleRequest req = (HttpModuleRequest) ThreadContext
					.getAttribute(THREAD_ATTR_REQUEST);
			ViewInstance view = (ViewInstance) ThreadContext
					.getAttribute(THREAD_ATTR_VIEW_INSTANCE);

			ViewInstance newView = view.addSub(null, req, winlet, m_strView,
					null);
			newView.setParams(m_params);

			String viewPath = req.getControlPath() + req.getRequestPath();

			RequestWrapper request = new RequestWrapper(
					(HttpServletRequest) pageContext.getRequest(),
					newView.getId(), req.getRequestPath());

			BufferedResponseWrapper response = new BufferedResponseWrapper(
					(HttpServletResponse) pageContext.getResponse());

			// 注：
			// 这里使用forward而不是include，因为使用include的情况下在被include对象
			// 中使用getRequestURI()等方法获得的是当前的URI而不是被include对象的URI，
			// 因此ADK无法正确判断被include的资源。使用forward则不存在这个问题。因为已经
			// 使用了responseWrapper，因此用forward也是可行的。
			pageContext.getServletContext().getRequestDispatcher(viewPath)
					.forward(request, response);
			byte[] bytes = response.getBuffered();

			Vector<String> v = null;
			if (vars != null) {
				v = TypeCast.cast(pageContext.getAttribute(vars));
				if (v == null) {
					v = new Vector<String>();
					pageContext.setAttribute(vars, v);
				}
			}

			StringBuffer sb = new StringBuffer();
			sb.append("<span id=\"ap_view_").append(newView.getId())
					.append("\">");
			if (bytes != null)
				sb.append(new String(bytes, "UTF-8"));
			sb.append("</span>");

			if (v != null)
				v.add(sb.toString());
			else
				pageContext.setAttribute(var, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}

		return EVAL_PAGE;
	}
}
