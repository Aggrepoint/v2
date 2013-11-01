package com.aggrepoint.adk.data;

import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.common.XMLUtil;

/**
 * WinletView
 * 
 * @author YJM
 */
public class WinletViewDef extends BaseModuleDef {
	/** 缺省状态 */
	public WinletStateDef m_defaultState;
	/** 状态 */
	public Vector<WinletStateDef> m_vecStates;
	/** 动作 */
	public Vector<WinletActionDef> m_vecActions;

	public Document m_docRetData;

	public WinletViewDef() throws AdbException {
		m_bInPath = false;
		m_defaultState = null;
		m_vecStates = new Vector<WinletStateDef>();
		m_vecActions = new Vector<WinletActionDef>();
	}

	public void afterLoaded(AdbAdapter adapter, Integer methodType,
			String methodId) throws XPathExpressionException {
		for (WinletStateDef state : m_vecStates) {
			state.m_view = this;

			if (state.m_bDefault && m_defaultState == null) {
				m_defaultState = state;
			}

			// copy retdata to retcode by reference
			for (RetCode retcode : state.m_vecRetCodes) {
				NodeList nl = (NodeList) XMLUtil.evalXPath(retcode.m_docSelf,
						"/retcode/include", XPathConstants.NODESET);

				if (nl != null && nl.getLength() > 0) {
					Element root = (Element) XMLUtil.evalXPath(
							retcode.m_docSelf, "/retcode", XPathConstants.NODE);

					for (int i = 0; i < nl.getLength(); i++) {
						Node node = nl.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE)
							XMLUtil.copyElement(m_docRetData, ((Element) node)
									.getAttribute("path"), retcode.m_docSelf,
									root);
						root.removeChild(node);
					}
				}
			}
		}

		if (m_defaultState == null && m_vecStates.size() > 0)
			m_defaultState = m_vecStates.elementAt(0);

		for (WinletActionDef action : m_vecActions)
			action.m_view = this;
	}

	public WinletActionDef findActionDef(String path) {
		for (WinletActionDef action : m_vecActions) {
			if (action.m_strPath.equals(path))
				return action;
		}

		return null;
	}

	public WinletStateDef findStateDef(String id) {
		for (WinletStateDef state : m_vecStates) {
			if (state.m_strPath.equals(id))
				return state;
		}

		return null;
	}

	public void print(String tab, java.io.PrintWriter pw) {
		printStartTag(tab, "id", pw, "view");
		printSubElements(tab, pw);
		for (WinletStateDef state : m_vecStates)
			state.print(tab + "\t", pw);
		for (WinletActionDef action : m_vecActions)
			action.print(tab + "\t", pw);
		printEndTag(tab, pw, "view");
	}
}
