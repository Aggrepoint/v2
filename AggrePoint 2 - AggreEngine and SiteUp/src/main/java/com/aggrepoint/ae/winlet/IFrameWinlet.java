package com.aggrepoint.ae.winlet;

import java.util.HashMap;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletParamParser;

public class IFrameWinlet extends Winlet {
	private static final long serialVersionUID = 1L;

	public int show(IModuleRequest req, IModuleResponse resp) throws Exception {
		WinletParamParser param = new WinletParamParser(req);
		String src = param.getParam("src");
		if (src == null)
			return 8000;

		req.setAttribute("SRC", src);
		req.setAttribute("WIDTH", param.getParam("width", "100%"));
		req.setAttribute("HEIGHT", param.getParam("height", "100%"));

		HashMap<String, String> others = new HashMap<String, String>();
		for (String p : param.getParamNames()) {
			if ("src".equals(p) || "width".equals(p) || "height".equals(p))
				continue;

			String v = param.getParam(p);
			if (v != null)
				others.put(p, v);
		}
		req.setAttribute("OTHERS", others);
		req.setAttribute("OTHERS_KEY", others.keySet());
		return 0;
	}
}
