/*
 * 创建日期 2005-11-24
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.aggrepoint.su.core.admin;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBPageLayout;
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.ApBPagePsnTmpl;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApBranchGroup;
import com.aggrepoint.su.core.data.ApContCat;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApImage;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.ApPathMap;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.ApTmplParam;
import com.aggrepoint.su.core.data.ApWinParam;
import com.aggrepoint.su.core.data.ApWindow;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.common.StringUtils;

/**
 * @author administrator
 */
public class ExportHelper {
	String m_strOutputPath;

	PrintWriter m_pw;

	AdbAdapter m_adapter;

	SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd");

	String m_strUuidSuffix = "";

	public ExportHelper(String outputPath, PrintWriter pw, AdbAdapter adapter) {
		m_strOutputPath = outputPath;
		m_pw = pw;
		m_adapter = adapter;
	}

	String getUuidSuffix(long siteId) {
		if (siteId == 100)
			return "";
		return m_strUuidSuffix;
	}

	public void exportLogo(long id, String prefix) throws Exception {
		ApImage image = new ApImage();
		image.m_lImageID = id;
		m_adapter.retrieve(image);
		image.m_strImage = prefix + "_logo";
		m_pw.print(" logotype=\"");
		m_pw.print(StringUtils.toXMLString(image.m_strContentType));
		m_pw.print("\" logoname=\"");
		m_pw.print(StringUtils.toXMLString(image.m_strImageName));
		m_pw.print("\" logofile=\"");
		m_pw.print(StringUtils.toXMLString(image.m_strImage));
		m_pw.print("\"");
		image.m_strImage = m_strOutputPath + image.m_strImage;
		m_adapter.retrieve(image, "loadImage");
	}

	public void exportSite(ApSite site, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<site name=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strSiteName));
		m_pw.print("\" uuid=\"");
		m_pw.print(site.m_strUUID);
		if (site.m_lSiteID == 100)
			m_pw.print("\" global=\"yes");
		m_pw.print("\" desc=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strSiteDesc));
		m_pw.print("\" managerule=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strManageRule));
		m_pw.print("\" publishbranchdir=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strPublishBranchDir));
		m_pw.print("\" staticbranchurl=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strStaticBranchUrl));
		m_pw.print("\" publishchanneldir=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strPublishChannelDir));
		m_pw.print("\" staticchannelurl=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strStaticChannelUrl));
		m_pw.print("\" publishresdir=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strPublishResDir));
		m_pw.print("\" staticresurl=\"");
		m_pw.print(StringUtils.toXMLString(site.m_strStaticResUrl));
		m_pw.print("\"");
		if (site.m_lSiteLogoID != 0)
			exportLogo(site.m_lSiteLogoID, "site_" + site.m_lSiteID);
		m_pw.println(">");

		boolean bExported = false;
		// {导出站点模板
		ApTemplate template = new ApTemplate();
		template.m_lSiteID = site.m_lSiteID;
		for (ApTemplate tmpl : m_adapter.retrieveMulti(template, "loadByOwner",
				"order")) {
			m_adapter.retrieve(tmpl, "loadAllExtra");
			exportTemplate(tmpl, tab + "\t");
			bExported = true;
		}
		// }

		if (bExported)
			m_pw.println();
		bExported = false;

		// {导出布局
		ApLayout layout = new ApLayout();
		layout.m_lSiteID = site.m_lSiteID;
		for (ApLayout l : m_adapter
				.retrieveMulti(layout, "loadBySite", "order")) {
			exportLayout(l, tab + "\t");
			bExported = true;
		}
		// }

		if (bExported)
			m_pw.println();
		bExported = false;

		// {导出框架
		ApFrame frame = new ApFrame();
		frame.m_lSiteID = site.m_lSiteID;
		for (ApFrame f : m_adapter.retrieveMulti(frame, "loadBySite", "order")) {
			m_adapter.retrieve(f, "loadDetail");
			exportFrame(f, tab + "\t");
			bExported = true;
		}
		// }

		if (bExported)
			m_pw.println();
		bExported = false;

		// {导出应用
		ApApp app = new ApApp();
		app.m_lSiteID = site.m_lSiteID;
		for (ApApp a : m_adapter.retrieveMulti(app, "loadBySite", null)) {
			exportApp(a, tab + "\t");
			bExported = true;
		}
		// }

		if (bExported)
			m_pw.println();
		bExported = false;

		// {导出内容分类
		ApContCat contcat = new ApContCat();
		contcat.m_lSiteID = site.m_lSiteID;
		for (ApContCat c : m_adapter.retrieveMulti(contcat, "loadBySite", null)) {
			exportContCat(c, tab + "\t");
			bExported = true;
		}
		// }

		if (bExported)
			m_pw.println();
		bExported = false;

		// {导出分支
		ApBranch branch = new ApBranch();
		branch.m_lSiteID = site.m_lSiteID;
		for (ApBranch b : m_adapter
				.retrieveMulti(branch, "loadAllBySite", null)) {
			exportBranch(b, tab + "\t");
			bExported = true;
		}
		// }

		// {导出分支组合
		ApBranchGroup group = new ApBranchGroup();
		group.m_lSiteID = site.m_lSiteID;
		for (ApBranchGroup g : m_adapter.retrieveMulti(group, "loadBySite",
				null))
			exportBranchGroup(g, tab + "\t");
		// }

		if (bExported)
			m_pw.println();

		// { 导出非系统资源
		ApResDir dir = new ApResDir();
		dir.m_lSiteID = site.m_lSiteID;
		for (ApResDir d : m_adapter.retrieveMulti(dir, "loadRoot", "def")) {
			if (d.m_bSysFlag)
				continue;

			exportDir(d, tab + "\t", "global");
		}
		// }

		m_pw.print(tab);
		m_pw.println("</site>");
	}

	public void exportDir(ApResDir dir, String tab, String prefix)
			throws Exception {
		m_pw.print(tab);
		m_pw.print("<dir name=\"");
		m_pw.print(StringUtils.toXMLString(dir.getName()));
		m_pw.println("\">");
		m_adapter.retrieve(dir, "loadWithResAndChild");
		exportResource(dir, tab + "\t", prefix);
		m_pw.print(tab);
		m_pw.println("</dir>");
	}

	public void exportResource(ApResDir dir, String tab, String prefix)
			throws Exception {
		for (ApRes res : dir.getReses()) {
			res.m_strFile = prefix + "_res_" + res.m_lResID;
			m_pw.print(tab);
			m_pw.print("<res type=\"");
			m_pw.print(StringUtils.toXMLString(res.m_strContentType));
			m_pw.print("\" name=\"");
			m_pw.print(StringUtils.toXMLString(res.m_strFileName));
			m_pw.print("\" file=\"");
			m_pw.print(StringUtils.toXMLString(res.m_strFile));
			m_pw.print("\" order=\"");
			m_pw.print(res.m_iOrder);
			m_pw.print("\" attr=\"");
			m_pw.print(StringUtils.toXMLString(res.m_strAttribute));
			m_pw.println("\"/>");
			res.m_strFile = m_strOutputPath + res.m_strFile;
			m_adapter.retrieve(res, "loadFile");
		}

		for (ApResDir child : dir.getChildDirs())
			exportDir(child, tab, prefix);
	}

	public void exportResource(long resdir, String tab, String owner)
			throws Exception {
		ApResDir dir = new ApResDir();
		dir.m_lResDirID = resdir;
		if (m_adapter.retrieve(dir, "loadWithResAndChild") == null)
			return;
		exportResource(dir, tab, owner);
	}

	public void exportTemplate(ApTemplate template, String tab)
			throws Exception {
		FileOutputStream fos = new FileOutputStream(m_strOutputPath + "tmpl_"
				+ template.m_lTemplateID);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(template.m_strContent);
		pw.flush();
		fos.close();

		m_pw.print(tab);
		m_pw.print("<template id=\"");
		m_pw.print(template.m_lTemplateID);
		m_pw.print("\" uuid=\"");
		m_pw.print(template.m_strUUID);
		m_pw.print(getUuidSuffix(template.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(template.m_strTmplName));
		m_pw.print("\" order=\"");
		m_pw.print(template.m_iOrder);
		m_pw.print("\" type=\"");
		m_pw.print(template.m_iTmplType);
		m_pw.print("\" markup=\"");
		m_pw.print(template.m_iMarkupType);
		m_pw.print("\" areas=\"");
		m_pw.print(StringUtils.toXMLString(template.getAreas()));
		m_pw.print("\"");
		if (template.m_lPreviewImgID != 0)
			exportLogo(template.m_lPreviewImgID, "tmpl_"
					+ template.m_lTemplateID);
		m_pw.println(">");

		exportResource(template.m_lResDirID == 0 ? template.m_lDefResDirID
				: template.m_lResDirID, tab + "\t", "tmpl_"
				+ template.m_lTemplateID);

		for (ApTmplParam param : template.m_vecParams) {
			m_pw.print(tab);
			m_pw.print("\t<param name=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strParamName));
			m_pw.print("\" value=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strDefaultValue));
			m_pw.print("\" desc=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strParamDesc));
			m_pw.println("\"/>");
		}

		m_pw.print(tab);
		m_pw.println("</template>");
	}

	public void exportLayout(ApLayout layout, String tab) throws Exception {
		FileOutputStream fos = new FileOutputStream(m_strOutputPath + "layout_"
				+ layout.m_lLayoutID);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(layout.m_strContent);
		pw.flush();
		fos.close();

		m_pw.print(tab);
		m_pw.print("<layout id=\"");
		m_pw.print(layout.m_lLayoutID);
		m_pw.print("\" uuid=\"");
		m_pw.print(layout.m_strUUID);
		m_pw.print(getUuidSuffix(layout.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(layout.m_strLayoutName));
		m_pw.print("\"");
		if (layout.m_lPreviewImgID != 0)
			exportLogo(layout.m_lPreviewImgID, "layout_" + layout.m_lLayoutID);
		m_pw.println("/>");
	}

	public void exportFrame(ApFrame frame, String tab) throws Exception {
		FileOutputStream fos = new FileOutputStream(m_strOutputPath + "frame_"
				+ frame.m_lFrameID + "_normal");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(frame.m_strContNormal);
		pw.flush();
		fos.close();
		fos = new FileOutputStream(m_strOutputPath + "frame_"
				+ frame.m_lFrameID + "_min");
		pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(frame.m_strContMin);
		pw.flush();
		fos.close();
		fos = new FileOutputStream(m_strOutputPath + "frame_"
				+ frame.m_lFrameID + "_max");
		pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(frame.m_strContMax);
		pw.flush();
		fos.close();

		m_pw.print(tab);
		m_pw.print("<frame id=\"");
		m_pw.print(frame.m_lFrameID);
		m_pw.print("\" uuid=\"");
		m_pw.print(frame.m_strUUID);
		m_pw.print(getUuidSuffix(frame.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(frame.m_strFrameName));
		m_pw.print("\" order=\"");
		m_pw.print(frame.m_iOrder);
		m_pw.print("\"");
		if (frame.m_lPreviewImgID != 0)
			exportLogo(frame.m_lPreviewImgID, "frame_" + frame.m_lFrameID);
		m_pw.println(">");

		exportResource(frame.m_lResDirID == 0 ? frame.m_lDefResDirID
				: frame.m_lResDirID, tab + "\t", "frame_" + frame.m_lFrameID);

		m_pw.print(tab);
		m_pw.println("</frame>");
	}

	public void exportApp(ApApp app, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<app name=\"");
		m_pw.print(StringUtils.toXMLString(app.m_strAppName));
		m_pw.print("\" uuid=\"");
		m_pw.print(app.m_strUUID);
		m_pw.print(getUuidSuffix(app.m_lSiteID));
		m_pw.print("\" hosturl=\"");
		m_pw.print(StringUtils.toXMLString(app.m_strHostURL));
		m_pw.print("\" rootpath=\"");
		m_pw.print(StringUtils.toXMLString(app.m_strRootPath));
		m_pw.print("\" desc=\"");
		m_pw.print(StringUtils.toXMLString(app.m_strAppDesc));
		m_pw.print("\" ver=\"");
		m_pw.print(StringUtils.toXMLString(app.m_strAppVer));
		m_pw.print("\" status=\"");
		m_pw.print(app.m_iStatusID);
		m_pw.print("\" conntimeout=\"");
		m_pw.print(app.m_iConnTimeout);
		m_pw.print("\" readtimeout=\"");
		m_pw.print(app.m_iReadTimeout);
		m_pw.print("\"");
		if (app.m_lLogoImageID != 0)
			exportLogo(app.m_lLogoImageID, "app_" + app.m_lAppID);
		m_pw.println(">");

		exportResource(app.m_lResDirID, tab + "\t", "app_" + app.m_lAppID);

		ApWindow win = new ApWindow();
		win.m_lAppID = app.m_lAppID;
		for (ApWindow w : m_adapter.retrieveMulti(win, "loadByAppWithParams",
				null))
			exportWindow(w, tab + "\t");

		m_pw.print(tab);
		m_pw.println("</app>");
	}

	public void exportWindow(ApWindow win, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<window id=\"");
		m_pw.print(win.m_lWindowID);
		m_pw.print("\" uuid=\"");
		m_pw.print(win.m_strUUID);
		m_pw.print(getUuidSuffix(win.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(win.m_strName));
		m_pw.print("\" url=\"");
		m_pw.print(StringUtils.toXMLString(win.m_strURL));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(win.m_strAccessRule));
		m_pw.print("\" mode=\"");
		m_pw.print(win.m_iWinMode);
		m_pw.print("\"");
		if (win.m_lPreviewImageID != 0)
			exportLogo(win.m_lPreviewImageID, "win_" + win.m_lWindowID);
		m_pw.println(">");

		for (ApWinParam param : win.getParams()) {
			m_pw.print(tab);
			m_pw.print("\t");
			m_pw.print("<param name=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strParamName));
			m_pw.print("\" desc=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strParamDesc));
			m_pw.print("\" value=\"");
			m_pw.print(StringUtils.toXMLString(param.m_strDefaultValue));
			m_pw.println("\"/>");
		}

		m_pw.print(tab);
		m_pw.println("</window>");
	}

	public void exportContCat(ApContCat contcat, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<contcat name=\"");
		m_pw.print(StringUtils.toXMLString(contcat.m_strContCatName));
		m_pw.print("\" uuid=\"");
		m_pw.print(contcat.m_strUUID);
		m_pw.print(getUuidSuffix(contcat.m_lSiteID));
		m_pw.print("\" desc=\"");
		m_pw.print(StringUtils.toXMLString(contcat.m_strContCatDesc));
		m_pw.println("\">");

		ApContent content = new ApContent();
		content.m_lContCatID = contcat.m_lContCatID;
		for (ApContent c : m_adapter.retrieveMulti(content, "loadByCat", null))
			exportContent(c, tab + "\t");

		m_pw.print(tab);
		m_pw.println("</contcat>");
	}

	public void exportContent(ApContent cont, String tab) throws Exception {
		FileOutputStream fos = new FileOutputStream(m_strOutputPath + "cont_"
				+ cont.m_lContentID);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
		pw.print(cont.m_strContent);
		pw.flush();
		fos.close();

		m_pw.print(tab);
		m_pw.print("<content id=\"");
		m_pw.print(cont.m_lContentID);
		m_pw.print("\" uuid=\"");
		m_pw.print(cont.m_strUUID);
		m_pw.print(getUuidSuffix(cont.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strName));
		m_pw.print("\" desc=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strDesc));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strAccessRule));
		m_pw.print("\" order=\"");
		m_pw.print(cont.m_iOrder);
		m_pw.print("\"");
		m_pw.println(">");

		exportResource(cont.m_lResDirID, tab + "\t", "cont_"
				+ cont.m_lContentID);

		m_pw.print(tab);
		m_pw.println("</content>");
	}

	public void exportBranch(ApBranch branch, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<branch name=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strBranchName));
		m_pw.print("\" uuid=\"");
		m_pw.print(branch.m_strUUID);
		m_pw.print(getUuidSuffix(branch.m_lSiteID));
		m_pw.print("\" desc=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strBranchDesc));
		m_pw.print("\" psntype=\"");
		m_pw.print(branch.m_iPsnType);
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strAccessRule));
		m_pw.print("\" managerule=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strManageRule));
		m_pw.print("\" clbpsnrule=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strClbPsnRule));
		m_pw.print("\" pvtpsnrule=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strPvtPsnRule));
		m_pw.print("\" rootpath=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strRootPath));
		m_pw.print("\" homepath=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strHomePath));
		m_pw.print("\" loginpath=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strLoginPath));
		m_pw.print("\" noaccesspath=\"");
		m_pw.print(StringUtils.toXMLString(branch.m_strNoAccessPath));
		m_pw.println("\">");

		ApPathMap map = new ApPathMap();
		map.m_lBranchID = branch.m_lBranchID;
		for (ApPathMap m : m_adapter
				.retrieveMulti(map, "loadByBranch", "order"))
			exportMap(m, tab + "\t");

		ApBPage page = new ApBPage();
		page.m_lBranchID = branch.m_lBranchID;
		if (m_adapter.retrieve(page, "loadRoot") != null)
			exportPage(page, tab + "\t");

		m_pw.print(tab);
		m_pw.println("</branch>");
	}

	public void exportBranchGroup(ApBranchGroup group, String tab)
			throws Exception {
		ApBranch branch = new ApBranch();

		m_pw.print(tab);
		m_pw.print("<branchgroup group=\"");
		branch.m_lBranchID = group.m_lGroupBranchID;
		m_adapter.retrieve(branch);
		m_pw.print(branch.m_strUUID);
		m_pw.print("\" branch=\"");
		branch.m_lBranchID = group.m_lBranchID;
		m_adapter.retrieve(branch);
		m_pw.print(branch.m_strUUID);
		m_pw.print("\" order=\"");
		m_pw.print(group.m_iOrder);
		m_pw.print("\" rule=\"");
		m_pw.print(group.m_strRule);
		m_pw.print("\" markup=\"");
		m_pw.print(group.m_iMarkupType);
		m_pw.println("\"/>");
	}

	public void exportMap(ApPathMap map, String tab) throws Exception {
		m_pw.print(tab);
		m_pw.print("<map uuid=\"");
		m_pw.print(map.m_strUUID);
		m_pw.print("\" frompath=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strFromPath));
		m_pw.print("\" topath=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strToPath));
		m_pw.print("\" fromlink=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strFromLink));
		m_pw.print("\" tolink=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strToLink));
		m_pw.print("\" status=\"");
		m_pw.print(map.m_iStatusID);
		m_pw.print("\" paramname1=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strParamName1));
		m_pw.print("\" paramname2=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strParamName2));
		m_pw.print("\" paramname3=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strParamName3));
		m_pw.print("\" paramname4=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strParamName4));
		m_pw.print("\" paramname5=\"");
		m_pw.print(StringUtils.toXMLString(map.m_strParamName5));
		m_pw.println("\">");
	}

	public void exportPage(ApBPage page, String tab) throws Exception {
		m_adapter.retrieve(page, "loadAssoc");

		m_pw.print(tab);
		m_pw.print("<page type=\"");
		m_pw.print(page.m_iPageType);
		m_pw.print("\" uuid=\"");
		m_pw.print(page.m_strUUID);
		m_pw.print(getUuidSuffix(page.m_lSiteID));
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strPageName));
		m_pw.print("\" path=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strPathName));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strAccessRule));
		m_pw.print("\" clbpsnrule=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strClbPsnRule));
		m_pw.print("\" pvtpsnrule=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strPvtPsnRule));
		m_pw.print("\" inherittmpl=\"");
		m_pw.print(page.m_bInheritTmpl ? 1 : 0);
		m_pw.print("\" templateid=\"");
		ApTemplate template = ApTemplate.findUuidById(m_adapter,
				page.m_lTemplateID);
		m_pw.print(template.m_strUUID);
		m_pw.print(getUuidSuffix(template.m_lSiteID));
		m_pw.print("\" tmplparam=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strTmplParams));
		m_pw.print("\" openmode=\"");
		m_pw.print(page.m_iOpenMode);
		m_pw.print("\" order=\"");
		m_pw.print(page.m_iOrder);
		m_pw.print("\" owner=\"");
		m_pw.print(StringUtils.toXMLString(page.m_strOwnerID));
		m_pw.print("\" skip=\"");
		m_pw.print(page.getSkip());
		m_pw.print("\" hide=\"");
		m_pw.print(page.m_bHide ? 1 : 0);
		m_pw.print("\" resetwin=\"");
		m_pw.print(page.m_bResetWin ? 1 : 0);
		m_pw.println("\">");

		for (ApBPagePsnName psnName : page.getPsnNames())
			exportBPagePsnName(psnName, tab + "\t");

		for (ApBPageLayout layout : page.getLayouts())
			exportBPageLayout(layout, tab + "\t");

		for (ApBPagePsnTmpl tmpl : page.getPsnTmpls())
			exportBPagePsnTmpl(tmpl, tab + "\t");

		// { 将内容排序，外围内容先行
		Vector<ApBPageContent> conts = new Vector<ApBPageContent>();
		boolean bMoved = true;
		while (page.getContents().size() > 0 && bMoved) {
			bMoved = false;
			for (ApBPageContent cont : page.getContents())
				if (cont.m_lContainContID == 0) {
					conts.add(cont);
					bMoved = true;
				} else {
					for (ApBPageContent c : conts)
						if (cont.m_lContainContID == c.m_lPageContID) {
							conts.add(cont);
							bMoved = true;
							break;
						}
				}

			if (bMoved)
				page.getContents().removeAll(conts);
		}

		if (page.getContents().size() > 0)
			conts.addAll(page.getContents());

		page.m_vecContents = conts;
		// }

		for (ApBPageContent cont : page.getContents()) {
			exportBPageContent(cont, tab + "\t");
		}

		ApBPage sub = new ApBPage();
		sub.m_lParentID = page.m_lPageID;
		sub.m_lBranchID = page.m_lBranchID;
		for (ApBPage s : m_adapter.retrieveMulti(sub, "loadByParentNoSub",
				"default"))
			exportPage(s, tab + "\t");

		m_pw.print(tab);
		m_pw.println("</page>");
	}

	public void exportBPagePsnName(ApBPagePsnName name, String tab)
			throws Exception {
		m_pw.print(tab);
		m_pw.print("<psnname name=\"");
		m_pw.print(StringUtils.toXMLString(name.m_strPageName));
		m_pw.print("\" owner=\"");
		m_pw.print(StringUtils.toXMLString(name.m_strOwnerID));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(name.m_strAccessRule));
		m_pw.println("\"/>");
	}

	public void exportBPageLayout(ApBPageLayout layout, String tab)
			throws Exception {
		m_pw.print(tab);
		m_pw.print("<layout id=\"");
		ApLayout l = ApLayout.findUuidById(m_adapter, layout.m_lLayoutID);
		m_pw.print(l.m_strUUID);
		m_pw.print(getUuidSuffix(l.m_lSiteID));
		m_pw.print("\" area=\"");
		m_pw.print(StringUtils.toXMLString(layout.m_strAreaName));
		m_pw.print("\" inherit=\"");
		m_pw.print(layout.m_bInheritable ? "y" : "n");
		m_pw.println("\"/>");
	}

	public void exportBPagePsnTmpl(ApBPagePsnTmpl tmpl, String tab)
			throws Exception {
		m_pw.print(tab);
		m_pw.print("<psntmpl id=\"");
		ApTemplate template = ApTemplate.findUuidById(m_adapter,
				tmpl.m_lTemplateID);
		m_pw.print(template.m_strUUID);
		m_pw.print(getUuidSuffix(template.m_lSiteID));
		m_pw.print("\" param=\"");
		m_pw.print(StringUtils.toXMLString(tmpl.m_strTmplParams));
		m_pw.print("\" owner=\"");
		m_pw.print(StringUtils.toXMLString(tmpl.m_strOwnerID));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(tmpl.m_strAccessRule));
		m_pw.println("\"/>");
	}

	public void exportBPageContent(ApBPageContent cont, String tab)
			throws Exception {
		m_pw.print(tab);
		m_pw.print("<cont id=\"");
		m_pw.print(cont.m_lPageContID);
		m_pw.print("\" area=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strAreaName));
		m_pw.print("\" zone=\"");
		m_pw.print(cont.m_iZoneID);
		m_pw.print("\" order=\"");
		m_pw.print(cont.m_iOrder);
		m_pw.print("\" inherit=\"");
		m_pw.print(cont.m_bInheritable ? "y" : "n");
		m_pw.print("\" name=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strContName));
		m_pw.print("\" owner=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strOwnerID));
		m_pw.print("\" accessrule=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strAccessRule));
		if (cont.m_lContainContID != 0) {
			m_pw.print("\" containcontid=\"");
			m_pw.print(cont.m_lContainContID);
		}
		if (cont.m_lWindowID != 0) {
			m_pw.print("\" windowid=\"");
			ApWindow win = ApWindow.findUuidById(m_adapter, cont.m_lWindowID);
			m_pw.print(win.m_strUUID);
			m_pw.print(getUuidSuffix(win.m_lSiteID));
		}
		if (cont.m_lFrameID != 0) {
			m_pw.print("\" frameid=\"");
			ApFrame frame = ApFrame.findUuidById(m_adapter, cont.m_lFrameID);
			m_pw.print(frame.m_strUUID);
			m_pw.print(getUuidSuffix(frame.m_lSiteID));
		}
		m_pw.print("\" popwin=\"");
		m_pw.print(cont.m_bPopWinFlag ? "y" : "n");
		m_pw.print("\" winprarm=\"");
		m_pw.print(StringUtils.toXMLString(cont.m_strWinParams));
		if (cont.isPageContent()) {
			m_pw.println("\">");
			ApContent content = new ApContent();
			content.m_lContentID = cont.m_lContentID;

			if (m_adapter.retrieve(content) != null) {
				exportContent(content, tab + "\t");
			} else {
				System.err.println("Warning: Page content " + cont.m_lContentID
						+ " not found.");
			}

			m_pw.print(tab);
			m_pw.println("</cont>");
		} else {
			if (cont.m_lContentID != 0) {
				m_pw.print("\" contentid=\"");
				ApContent c = ApContent.findUuidById(m_adapter,
						cont.m_lContentID);
				m_pw.print(c.m_strUUID);
				m_pw.print(getUuidSuffix(c.m_lSiteID));
			}
			m_pw.println("\"/>");
		}
	}
}
