/*
 * 创建日期 2004-10-5
 */
package com.aggrepoint.su.user;

import java.io.InputStream;
import java.util.Vector;

import org.w3c.dom.Document;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.StringUtils;

/**
 * 用户登录和退出Winlet
 * 
 * 使用包内的userlib.xml作为用户身份数据库
 * 
 * @author YJM
 */
public class LoginLogout extends Winlet {
	private static String SECONDS_WEEK = Integer.toString(60 * 60 * 24 * 7);
	private static String SECONDS_MONTH = Integer.toString(60 * 60 * 24 * 30);
	private static String SECONDS_YEAR = Integer.toString(60 * 60 * 24 * 365);

	private static final long serialVersionUID = 1L;

	public String m_strErrorMsg;

	/**
	 * 显示登录表单
	 * 
	 * @author Administrator
	 */
	public int showLoginForm(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (!req.getUserProfile().isAnonymous()) // 用户已经登录
			return 1;

		return 0;
	}

	/**
	 * 显示已登录用户的信息
	 * 
	 * @author Administrator
	 */
	public int showUserInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_strErrorMsg = null;

		if (req.getUserProfile().isAnonymous()) // 用户未登录
			return 1;

		if (getWinMode(req) == EnumWinMode.MIN)
			return 8000;

		return 0;
	}

	/**
	 * 进行登录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int doLogin(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_strErrorMsg = null;
		InputStream is;
		Document doc;
		AdbAdapter adapter;

		is = getClass().getResourceAsStream("userlib.xml");

		doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(new org.xml.sax.InputSource(is));

		adapter = new XmlAdapter(doc);

		Vector<User> vecUsers = adapter.retrieveMulti(new User(), "loadAll",
				null);
		is.close();

		String id = req.getParameter("id", "");
		if (id.equals("")) {
			m_strErrorMsg = req.getMessage("idNotFound", "");
			return 1;
		}
		String strPassword = req.getParameter("pwd", "");

		String encid = StringUtils.md5EncryptReverse(id);
		String encpwd = StringUtils.md5EncryptReverse(strPassword);

		for (User user : vecUsers) {
			if (user.m_strID.equals(encid) || user.m_strID.equals(id)) {
				if (!user.m_strPassword.equals(strPassword)
						&& !user.m_strPassword.equals(encpwd)) {
					m_strErrorMsg = req.getMessage("wrongPwd", "");
					return 2;
				}

				WinletUserProfile userProfile = new WinletUserProfile();
				userProfile.setProperty(IUserProfile.PROPERTY_ID, id);
				for (UserProperty prop : user.m_vecProperties)
					userProfile.setProperty(prop.m_strName, prop.m_strValue);
				switch (req.getParameter("keep", 0)) {
				case 1:
					userProfile.setProperty(WinletUserProfile.KEEP_TIME,
							SECONDS_WEEK);
					break;
				case 2:
					userProfile.setProperty(WinletUserProfile.KEEP_TIME,
							SECONDS_MONTH);
					break;
				case 3:
					userProfile.setProperty(WinletUserProfile.KEEP_TIME,
							SECONDS_YEAR);
					break;
				}

				setUserProfile(resp, userProfile);
				return 0;
			}
		}

		m_strErrorMsg = req.getMessage("idNotFound", "");
		return 1;
	}

	/**
	 * 退出登录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int doLogout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		clearUserProfile(resp);
		return 0;
	}
}
