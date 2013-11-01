package com.aggrepoint.ae.core.proxy;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IWinletConst;
import com.icebean.core.common.TypeCast;

/**
 * @author YJM
 */
public class HttpProxy implements IWinletConst {
	public static final String SEESION_KEY_COOKIES = "_AE_COOKIES";

	public static void clearCookies(HttpServletRequest req) {
		req.getSession(true).removeAttribute(SEESION_KEY_COOKIES);
	}

	/**
	 * 获取Cookie数组
	 */
	static Vector<String> loadCookies(HttpServletRequest req, String host) {
		Hashtable<String, Vector<String>> htCookies = TypeCast.cast(req
				.getSession(true).getAttribute(SEESION_KEY_COOKIES));
		if (htCookies == null) {
			htCookies = new Hashtable<String, Vector<String>>();
			req.getSession().setAttribute(SEESION_KEY_COOKIES, htCookies);
		}

		Vector<String> vecCookies = htCookies.get(host);
		if (vecCookies == null) {
			vecCookies = new Vector<String>();
			htCookies.put(host, vecCookies);
		}

		return vecCookies;
	}

	/**
	 * 发起请求前设置Cookie
	 */
	static void setCookie(HttpServletRequest req, String host,
			HttpURLConnection conn) {
		Vector<String> vecCookies = loadCookies(req, host);

		StringBuffer sbCookie = null;
		for (Enumeration<String> enm = vecCookies.elements(); enm
				.hasMoreElements();) {
			if (sbCookie == null)
				sbCookie = new StringBuffer();
			else
				sbCookie.append("; ");
			sbCookie.append(enm.nextElement());
		}
		if (sbCookie != null)
			conn.setRequestProperty("Cookie", sbCookie.toString());
	}

	/**
	 * 获得响应后处理Cookie
	 */
	static void getCookie(HttpServletRequest req, String host,
			HttpURLConnection conn) {
		Vector<String> vecCookies = loadCookies(req, host);

		for (int k = 1;; k++) {
			String key = null;

			try {
				key = conn.getHeaderFieldKey(k);
			} catch (Exception e) {
			}

			if (key == null)
				break;
			if (key.equals("Set-Cookie")) {
				String value = conn.getHeaderField(k);

				int l = value.indexOf(";");
				if (l >= 0)
					vecCookies.insertElementAt(value.substring(0, l), 0);
				else
					vecCookies.insertElementAt(value, 0);
			}
		}
	}

	/**
	 * 代理HTTP请求
	 */
	static public boolean proxy(IModuleRequest request,
			IModuleResponse response, String url, int connTimeout,
			int readTimeout) throws Exception {
		URL source = new java.net.URL(url);
		HttpURLConnection conn = null;
		InputStream is = null;

		try {
			HttpServletRequest req = (HttpServletRequest) request
					.getRequestObject();
			HttpServletResponse resp = (HttpServletResponse) response
					.getResponseObject();

			conn = (HttpURLConnection) source.openConnection();
			conn.setInstanceFollowRedirects(false);
			if (connTimeout > 0)
				conn.setConnectTimeout(connTimeout * 1000);
			if (readTimeout > 0)
				conn.setReadTimeout(readTimeout * 1000);

			// {设置请求头
			for (Enumeration<?> enm = req.getHeaderNames(); enm
					.hasMoreElements();) {
				String headerName = enm.nextElement().toString();
				if (!headerName.equalsIgnoreCase("content-length")
						&& !headerName.equalsIgnoreCase("cookie")
						&& !headerName.equalsIgnoreCase("host"))
					conn.setRequestProperty(headerName,
							req.getHeader(headerName));
			}
			conn.setRequestProperty("Connection", "close");
			// }

			// 设置Cookie
			setCookie(req, source.getHost(), conn);

			try {
				is = conn.getInputStream();
			} catch (FileNotFoundException e) {
				resp.setStatus(404);
				return false;
			}

			resp.setContentType(conn.getHeaderField("Content-Type"));
			String str = conn.getHeaderField("Content-Disposition");
			if (str != null)
				resp.setHeader("Content-Disposition", str);

			OutputStream os = resp.getOutputStream();
			byte[] buffer = new byte[10240];

			int c = is.read(buffer);
			int len = c;
			while (c > 0) {
				os.write(buffer, 0, c);
				c = is.read(buffer);
				len += c;
			}

			os.flush();
		} finally {
			response.setCommitted();

			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * mode 0 - 发起GET请求，并将响应做为String返回 <br>
	 * mode 1 - 转发HTTP请求(POST或GET)，并返回响应 <br>
	 * mode 2 - 转发HTTP请求(POST或GET)，并将响应写回response
	 */
	static public HttpClientResponse request(int mode, long reqId,
			HttpServletRequest req, IModuleResponse response /* 只有模式2才需要 */,
			String url, long branchid, long appid, String params, int winMode,
			String userProfile, String userAgent, String remoteIP, String uri,
			String markup, String resUrl, String urlRoot, String areaName,
			boolean resetWin, int connTimeout, int readTimeout)
			throws Exception {
		URL source = new java.net.URL(url);
		HttpURLConnection conn = null;
		InputStream is = null;
		HttpClientResponse hcr = null;

		try {
			HttpServletResponse resp = null;
			if (response != null)
				resp = (HttpServletResponse) response.getResponseObject();

			conn = (HttpURLConnection) source.openConnection();
			conn.setInstanceFollowRedirects(false);
			if (connTimeout > 0)
				conn.setConnectTimeout(connTimeout * 1000);
			if (readTimeout > 0)
				conn.setReadTimeout(readTimeout * 1000);

			if (mode != 0) {
				conn.setRequestMethod(req.getMethod());
				conn.setDoOutput(true);
			}

			// {设置请求头
			for (Enumeration<?> enm = req.getHeaderNames(); enm
					.hasMoreElements();) {
				String headerName = enm.nextElement().toString();
				if (!headerName.equalsIgnoreCase("content-length")
						&& !headerName.equalsIgnoreCase("cookie")
						&& !headerName.equalsIgnoreCase("host"))
					conn.setRequestProperty(headerName,
							req.getHeader(headerName));
			}
			conn.setRequestProperty("Connection", "close");
			conn.setRequestProperty(REQUEST_HEADER_VER, "1.0");
			conn.setRequestProperty(REQUEST_HEADER_REQUEST_ID,
					Long.toString(reqId));
			conn.setRequestProperty(REQUEST_HEADER_WINDOW_AREA_NAME, areaName);
			conn.setRequestProperty(REQUEST_HEADER_SITE_BRANCH,
					Long.toString(branchid));
			conn.setRequestProperty(REQUEST_HEADER_SITE_APP,
					Long.toString(appid));
			conn.setRequestProperty(REQUEST_HEADER_USER_PROFILE, userProfile);
			conn.setRequestProperty(REQUEST_HEADER_USER_AGENT, userAgent);
			conn.setRequestProperty(REQUEST_HEADER_USER_IP, remoteIP);
			conn.setRequestProperty(REQUEST_HEADER_REQUEST_URI, uri);
			conn.setRequestProperty(REQUEST_HEADER_MARKUP_TYPE, markup);
			if (winMode != EnumWinMode.NOCHANGE.getId())
				conn.setRequestProperty(REQUEST_HEADER_WINDOW_MODE,
						Integer.toString(winMode));
			conn.setRequestProperty(REQUEST_HEADER_WINDOW_PARAMS, params);
			conn.setRequestProperty(REQUEST_HEADER_STATIC_WIN_RES_URL, resUrl);
			conn.setRequestProperty(REQUEST_HEADER_URL_ROOT, urlRoot);
			if (resetWin)
				conn.setRequestProperty(REQUEST_HEADER_RESET_WINDOW, "yes");
			// }

			// 设置Cookie
			setCookie(req, source.getHost(), conn);

			byte[] buffer = new byte[10240];
			int c;

			if (mode == 0)
				is = conn.getInputStream();
			else {
				// {转发请求内容
				String contType = req.getHeader("Content-Type");

				if (contType == null
						|| contType
								.startsWith("application/x-www-form-urlencoded")) { // 通过Ajax发起请求时，contType为null
					StringBuffer sb = new StringBuffer();

					boolean bFirst = true;
					for (Enumeration<?> enm = req.getParameterNames(); enm
							.hasMoreElements();) {
						String paramName = enm.nextElement().toString();

						String[] paramValues = req
								.getParameterValues(paramName);
						for (int i = 0; i < paramValues.length; i++) {
							if (!bFirst)
								sb.append("&");
							else
								bFirst = false;
							sb.append(paramName)
									.append("=")
									.append(URLEncoder.encode(paramValues[i],
											"UTF-8"));
						}
					}

					OutputStream winOs = conn.getOutputStream();
					String str = sb.toString();
					winOs.write(str.getBytes());
				} else {
					OutputStream winOs = conn.getOutputStream();
					InputStream reqIs = req.getInputStream();
					c = reqIs.read(buffer);
					while (c > 0) {
						winOs.write(buffer, 0, c);
						c = reqIs.read(buffer);
					}
				}
				// }

				conn.connect();
			}

			hcr = new HttpClientResponse(conn);

			// 处理Cookie
			getCookie(req, source.getHost(), conn);
			if (hcr.m_strRedirect != null)
				return hcr;

			if (mode != 0)
				is = conn.getInputStream();

			switch (mode) {
			case 0:
			case 1: {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				c = is.read(buffer);
				while (c > 0) {
					os.write(buffer, 0, c);
					c = is.read(buffer);
				}

				os.flush();
				hcr.m_response = os.toString("UTF-8");
				break;
			}
			case 2: {
				resp.setContentType(conn.getHeaderField("Content-Type"));
				String str = conn.getHeaderField("Content-Disposition");
				if (str != null)
					resp.setHeader("Content-Disposition", str);

				OutputStream os = resp.getOutputStream();

				c = is.read(buffer);
				int len = c;
				while (c > 0) {
					os.write(buffer, 0, c);
					c = is.read(buffer);
					len += c;
				}

				os.flush();
				break;
			}
			}

			return hcr;
		} catch (Exception e) {
			if (mode == 0) {
				System.err.println(url);
				e.printStackTrace();
				return new HttpClientResponse();
			} else
				throw e;
		} finally {
			if (mode == 2)
				response.setCommitted();

			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
