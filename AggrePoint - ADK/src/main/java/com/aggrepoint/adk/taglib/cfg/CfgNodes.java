package com.aggrepoint.adk.taglib.cfg;

import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import com.aggrepoint.adk.IAdkConst;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.xml.MatchElement;

public class CfgNodes implements IAdkConst {
	Vector<MatchElement> nodes;
	int idx;

	public CfgNodes(MatchElement elm, String path)
			throws XPathExpressionException {
		idx = 0;
		nodes = elm.match(path);
	}

	public MatchElement getNextNode(String markup) {
		Vector<String> lss = LocaleManager.getLSIDs(CfgNodes.class, null);

		while (nodes != null && idx < nodes.size()) {
			MatchElement node = nodes.elementAt(idx++);

			String str = node.getAttribute("lsid");
			if (!(str == null || str.equals("") || lss.contains(str)))
				continue;

			str = node.getAttribute("markup");
			if (!(markup == null || str == null || markup.equals("")
					|| str.equals("") || markup.equalsIgnoreCase(str)))
				continue;

			return node;
		}

		return null;
	}
}
