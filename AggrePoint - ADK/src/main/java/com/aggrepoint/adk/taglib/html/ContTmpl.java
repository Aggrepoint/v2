package com.aggrepoint.adk.taglib.html;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.adk.ui.PropertyInstance;

/**
 * The purpose of ContTmpl is to make data column generation in list.tag faster,
 * avoid redoing a lot of re-evaluation for every rows. <br>
 * 
 * Supported place holders:<br>
 * <br>
 * %VALUE(propname)% Get the value of property propname from full property set<br>
 * %DISPLAY(propname)% Get the display value of property propname from list
 * property set
 * 
 * @author Jim Yang, 20090408
 * 
 */
public class ContTmpl {
	static enum REPLACE_TYPE {
		VALUE("VALUE"), DISPLAY("DISPLAY"), DISPLAY_TEXTAREA("DISPLAY_TEXTAREA"), LABEL(
				"LABEL");

		String name;

		REPLACE_TYPE(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		static REPLACE_TYPE fromName(String name) {
			for (REPLACE_TYPE type : REPLACE_TYPE.values())
				if (type.name.equals(name))
					return type;
			return null;
		}
	}

	/** Pattern for parsing the place holders */
	static Pattern m_pPlaceHolder;
	static {
		String pattern = "%(";
		boolean bFirst = true;
		for (REPLACE_TYPE type : REPLACE_TYPE.values()) {
			if (bFirst)
				bFirst = false;
			else
				pattern += "|";
			pattern += type.getName();
		}

		pattern += ")\\((\\w*)\\)%";
		m_pPlaceHolder = Pattern.compile(pattern);
	}

	static class ToReplace {
		REPLACE_TYPE m_type;
		String m_strParam;

		public ToReplace(Matcher matcher) {
			m_type = REPLACE_TYPE.fromName(matcher.group(1));
			m_strParam = matcher.group(2);
		}

		public String execute(Vector<PropertyInstance> fullProps,
				Vector<PropertyInstance> props) throws Exception {
			switch (m_type) {
			case VALUE:
				for (PropertyInstance prop : fullProps)
					if (prop.getName().equals(m_strParam))
						return prop.getValue().toString();
				break;
			case LABEL:
				for (PropertyInstance prop : fullProps)
					if (prop.getName().equals(m_strParam))
						return prop.getLabel().toString();
				break;
			case DISPLAY:
			case DISPLAY_TEXTAREA:
				for (PropertyInstance prop : props)
					if (prop.getName().equals(m_strParam)) {
						Object obj = prop
								.getDisplay(m_type == REPLACE_TYPE.DISPLAY_TEXTAREA);
						if (obj != null)
							return obj.toString();
						break;
					}
			}
			return "";
		}
	}

	String m_strTemplate;
	Hashtable<String, ToReplace> m_htToReplaces = new Hashtable<String, ToReplace>();

	public ContTmpl(String tmpl) {
		m_strTemplate = tmpl;

		Matcher matcher = m_pPlaceHolder.matcher(tmpl);
		while (matcher.find()) {
			String g = matcher.group();
			if (!m_htToReplaces.containsKey(g))
				m_htToReplaces.put(matcher.group(), new ToReplace(matcher));
		}
	}

	public String execute(Vector<PropertyInstance> fullProps,
			Vector<PropertyInstance> props) throws Exception {
		String str = m_strTemplate;
		for (String replace : m_htToReplaces.keySet())
			str = str.replace(replace, m_htToReplaces.get(replace).execute(
					fullProps, props));
		return str;
	}
}
