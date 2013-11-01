package com.aggrepoint.adk.taglib.html;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.ui.PropertyInstance;

/**
 * The name can contains multiple property names connected by ',', means iterate
 * the properties one by one if found.
 * 
 * The name can started by '!' means 'not in'. This will iterate properties not
 * in the list.
 * 
 * @author YJM
 */
public class UIPropTag extends BodyTagSupport implements IAdkConst {
	private static final long serialVersionUID = 1L;

	String var;

	Collection<PropertyInstance> props;

	String name;

	Vector<PropertyInstance> vecProps;

	int posi;

	public void setVar(String var) {
		this.var = var;
	}

	public void setProps(Collection<PropertyInstance> props) {
		this.props = props;
	}

	public void setName(String name) {
		this.name = name.trim();
		vecProps = null;
	}

	boolean next() {
		if (vecProps == null) {
			vecProps = new Vector<PropertyInstance>();
			posi = 0;

			boolean bExclude = false;
			if (name.startsWith("!")) {
				bExclude = true;
				name = name.substring(1);
			}

			StringTokenizer st = new StringTokenizer(name, ", ");
			String[] names = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens())
				names[i++] = st.nextToken();

			for (PropertyInstance prop : props) {
				boolean bFound = false;
				for (String n : names)
					if (prop.getName().equals(n)) {
						bFound = true;
						break;
					}
				if (bExclude && !bFound || !bExclude && bFound)
					vecProps.add(prop);
			}
		}

		if (posi < vecProps.size()) {
			pageContext.setAttribute("propidx", posi);
			pageContext.setAttribute("propcount", vecProps.size());
			pageContext.setAttribute("propfirst", posi == 0);
			pageContext.setAttribute(var, vecProps.elementAt(posi++));
			pageContext.setAttribute("proplast", posi == vecProps.size());
			return true;
		}
		return false;
	}

	public int doStartTag() {
		if (next())
			return EVAL_BODY_BUFFERED;
		else
			return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		if (next())
			return EVAL_BODY_BUFFERED;
		else
			return SKIP_BODY;
	}
}
