package com.aggrepoint.adk.taglib.cfg;

import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.msg.MessageBoundle;
import com.icebean.core.xml.MatchElement;

/**
 * 
 * @author YJM
 */
public class CfgMsgTag extends TagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String id;
	String p0;
	String p1;
	String p2;
	String p3;
	String p4;
	String p5;
	String p6;
	String p7;
	String p8;
	String p9;

	public void setId(String id) {
		this.id = id;
	}

	public void setP0(String str) {
		this.p0 = str;
	}

	public void setP1(String str) {
		this.p1 = str;
	}

	public void setP2(String str) {
		this.p2 = str;
	}

	public void setP3(String str) {
		this.p3 = str;
	}

	public void setP4(String str) {
		this.p4 = str;
	}

	public void setP5(String str) {
		this.p5 = str;
	}

	public void setP6(String str) {
		this.p6 = str;
	}

	public void setP7(String str) {
		this.p7 = str;
	}

	public void setP8(String str) {
		this.p8 = str;
	}

	public void setP9(String str) {
		this.p9 = str;
	}

	public int doStartTag() {
		MatchElement elm = ((RetCode) ThreadContext
				.getAttribute(THREAD_ATTR_RETCODE)).getMatchElement();

		if (elm == null)
			return SKIP_BODY;

		try {
			MatchElement node = new CfgNodes(elm, "/msg|id=" + id)
					.getNextNode(MarkupTag.getMarkupName(pageContext
							.getRequest()));
			if (node == null)
				return SKIP_BODY;

			pageContext.getOut().write(
					MessageBoundle.constructMessageStatic(node.getContent(),
							new String[] { p0, p1, p2, p3, p4, p5, p6, p7, p8,
									p9 }));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}
}
