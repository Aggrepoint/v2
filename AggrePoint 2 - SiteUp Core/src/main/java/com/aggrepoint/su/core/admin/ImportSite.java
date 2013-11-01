package com.aggrepoint.su.core.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;

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
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.FileUtils;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 该程序用于导入站点
 * 
 * 20060911 在站点导出、导入过程中，全面使用UUID。id仅用于将导出的site.xml和附带的资源文件关联起来，
 * site.xml内部的引用（例如页面对template的引用）直接使用UUID
 * 
 * 未完成：需要判断引用是否合法，例如不能出现A站点的页面引用了B站点的模版的情况
 * 
 * @author YJM
 */
public class ImportSite {
	String m_strNamePrefix;
	String m_strInputPath;
	DbAdapter m_adapter;
	/** 源页面内容ID到新页面内容ID的对应关系 */
	Hashtable<Long, Long> m_htContentMap = new Hashtable<Long, Long>();

	public ImportSite(String namePrefix, String inputPath, DbAdapter adapter) {
		m_strNamePrefix = namePrefix;
		m_strInputPath = inputPath;
		m_adapter = adapter;
	}

	public static void main(String[] args) {
		String inputPath = null;
		String namePrefix = "";

		try {
			inputPath = args[0];

			if (!inputPath.endsWith("/"))
				inputPath = inputPath + "/";

			File file = new File(inputPath);
			if (!file.exists() || !file.isDirectory()) {
				System.out.println("Directory \"" + inputPath
						+ "\" does not exists.");
				return;
			}

			if (args.length > 1)
				namePrefix = args[1];
		} catch (Exception e) {
			System.out.println("Usage: ImportSite inputpath");
			return;
		}

		String tempDir = System.getenv("APTEMPDIR");
		if (tempDir == null)
			tempDir = "c:/temp";

		try {
			InputStream is;
			Document doc;
			AdbAdapter adapter;

			is = new FileInputStream(inputPath + "site.xml");

			doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new org.xml.sax.InputSource(is));

			adapter = new XmlAdapter(doc);

			Connection conn = DBConnManager.getConnection();
			conn.setAutoCommit(false);

			ImportSite imp = new ImportSite(namePrefix, inputPath,
					new DbAdapter(conn, DBConnManager.getConnectionSyntax()));

			// 站点
			for (ApSite site : adapter.retrieveMulti(new ApSite()))
				imp.importSite(site, tempDir);

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void importSite(ApSite site, String tempDir) throws Exception {
		if (site.m_strUUID == null || site.m_strUUID.equals(""))
			site.m_strUUID = UUIDGen.get();

		// {创建预览图片
		if (site.m_strLogoName != null && !site.m_strLogoName.equals("")) {
			ApImage image = new ApImage();
			image.m_iImageType = ApImage.IMGTYPE_SITE;
			image.m_strImageName = site.m_strLogoName;
			image.m_strContentType = site.m_strLogoContentType;
			image.m_strImage = m_strInputPath + site.m_strLogoFile;
			m_adapter.create(image);
			site.m_lSiteLogoID = image.m_lImageID;
		}
		// }

		// 创建站点
		site.m_strSiteName = m_strNamePrefix + site.m_strSiteName;
		m_adapter.create(site);

		// 创建系统资源目录
		site.initSysResDir(m_adapter);

		// 布局
		importLayouts(site);

		// 模板
		importTemplates(site, tempDir);

		// 窗框
		importFrames(site, tempDir);

		// 应用
		importApps(site, tempDir);

		// 内容
		importContCats(site, tempDir);

		// 分支
		importBranches(site, tempDir);

		// 分支组合
		importBranchGroups(site);

		// 非系统资源
		for (ApResDir dir : site.getDirs()) {
			ApResDir resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
					null, dir.getName(), true);

			resDir.m_vecReses = dir.m_vecReses;
			resDir.m_vecChildDirs = dir.m_vecChildDirs;
			importResources(site, resDir, tempDir);
		}
	}

	void importResources(ApSite site, ApResDir dir, String tempDir)
			throws Exception {
		// 导入资源
		for (ApRes res : dir.m_vecReses) {
			res.m_lResDirID = dir.m_lResDirID;
			res.m_strFile = m_strInputPath + res.m_strFile;
			res.m_lSize = new File(res.m_strFile).length();
			res.createWithIcons(tempDir, m_adapter);
			res.m_iOfficialFlag = 1;
			res.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(res);
		}

		// 导入子目录
		for (ApResDir child : dir.m_vecChildDirs) {
			ApResDir resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
					dir, child.getName(), true);
			resDir.m_vecReses = child.m_vecReses;
			resDir.m_vecChildDirs = child.m_vecChildDirs;
			importResources(site, resDir, tempDir);
		}
	}

	public void importTemplate(ApTemplate template, ApSite site,
			ApTemplate update, String tempDir) throws Exception {
		if (update == null) {
			if (template.m_strUUID == null || template.m_strUUID.equals(""))
				template.m_strUUID = UUIDGen.get();
		} else {
			template.m_lDefResDirID = update.m_lDefResDirID;
		}

		// {创建预览图片
		if (template.m_strLogoName != null
				&& !template.m_strLogoName.equals("")) {
			ApImage image = new ApImage();

			if (update != null && update.m_lPreviewImgID > 0) {
				image.m_lImageID = update.m_lPreviewImgID;
				m_adapter.delete(image);
			}

			image.m_iImageType = ApImage.IMGTYPE_TEMPLATE;
			image.m_strImageName = template.m_strLogoName;
			image.m_strContentType = template.m_strLogoContentType;
			image.m_strImage = m_strInputPath + template.m_strLogoFile;
			m_adapter.create(image);

			if (update == null)
				template.m_lPreviewImgID = image.m_lImageID;
			else
				update.m_lPreviewImgID = image.m_lImageID;
		}
		// }

		// {创建模板
		if (update == null)
			template.setContent(FileUtils.fileToString(m_strInputPath + "tmpl_"
					+ template.m_lImportID, "UTF-8"));
		else
			update.setContent(FileUtils.fileToString(m_strInputPath + "tmpl_"
					+ template.m_lImportID, "UTF-8"));

		template.m_strTmplName = m_strNamePrefix + template.m_strTmplName;
		template.m_lSiteID = site.m_lSiteID;

		// 创建资源目录
		ApResDir resDir;

		resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
				site.m_dirTemplate, "", true);

		if (update == null) {
			template.m_lDefResDirID = resDir.m_lResDirID;
			m_adapter.create(template);
			template.m_iOfficialFlag = 1;
			template.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(template);
		} else {
			update.m_lDefResDirID = resDir.m_lResDirID;
		}

		resDir.setDir(site.m_dirTemplate, Long
				.toString(update == null ? template.m_lTemplateID
						: update.m_lTemplateID));
		m_adapter.update(resDir, "updateDir");

		resDir.m_vecReses = template.m_dir.m_vecReses;
		resDir.m_vecChildDirs = template.m_dir.m_vecChildDirs;
		importResources(site, resDir, tempDir);

		// {创建参数
		if (update != null) {
			ApTmplParam param = new ApTmplParam();
			param.m_lTemplateID = update.m_lTemplateID;
			param.m_iOfficialFlag = update.m_iOfficialFlag;
			m_adapter.delete(param, "deleteByTmpl");
		}

		for (ApTmplParam param : template.getParams()) {
			param.m_lTemplateID = update == null ? template.m_lTemplateID
					: update.m_lTemplateID;
			m_adapter.create(param);
			param.m_iOfficialFlag = 1;
			param.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(param);
		}
		// }

		if (update != null) {
			m_adapter.update(update);

			resDir = new ApResDir();
			resDir.m_lResDirID = template.m_lDefResDirID;
			resDir.m_iOfficialFlag = update.m_iOfficialFlag;
			m_adapter.delete(resDir);
		}
	}

	void importTemplates(ApSite site, String tempDir) throws Exception {
		if (site.m_vecTemplates == null)
			return;

		for (ApTemplate template : site.m_vecTemplates)
			importTemplate(template, site, null, tempDir);
	}

	void importLayouts(ApSite site) throws Exception {
		if (site.m_vecLayouts == null)
			return;

		for (ApLayout layout : site.m_vecLayouts) {
			if (layout.m_strUUID == null || layout.m_strUUID.equals(""))
				layout.m_strUUID = UUIDGen.get();

			// {创建预览图片
			if (layout.m_strLogoName != null
					&& !layout.m_strLogoName.equals("")) {
				ApImage image = new ApImage();
				image.m_iImageType = ApImage.IMGTYPE_LAYOUT;
				image.m_strImageName = layout.m_strLogoName;
				image.m_strContentType = layout.m_strLogoContentType;
				image.m_strImage = m_strInputPath + layout.m_strLogoFile;
				m_adapter.create(image);
				layout.m_lPreviewImgID = image.m_lImageID;
			}
			// }

			layout.m_lSiteID = site.m_lSiteID;
			layout.setContent(FileUtils.fileToString(m_strInputPath + "layout_"
					+ layout.m_lImportID, "UTF-8"));
			layout.m_strLayoutName = m_strNamePrefix + layout.m_strLayoutName;
			m_adapter.create(layout);
			layout.m_iOfficialFlag = 1;
			layout.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(layout);
		}
	}

	void importFrames(ApSite site, String tempDir) throws Exception {
		if (site.m_vecFrames == null)
			return;

		for (ApFrame frame : site.m_vecFrames) {
			if (frame.m_strUUID == null || frame.m_strUUID.equals(""))
				frame.m_strUUID = UUIDGen.get();

			// {创建预览图片
			if (frame.m_strLogoName != null && !frame.m_strLogoName.equals("")) {
				ApImage image = new ApImage();
				image.m_iImageType = ApImage.IMGTYPE_FRAME;
				image.m_strImageName = frame.m_strLogoName;
				image.m_strContentType = frame.m_strLogoContentType;
				image.m_strImage = m_strInputPath + frame.m_strLogoFile;
				m_adapter.create(image);
				frame.m_lPreviewImgID = image.m_lImageID;
			}
			// }

			frame.m_lSiteID = site.m_lSiteID;
			frame.m_strContMax = FileUtils.fileToString(m_strInputPath
					+ "frame_" + frame.m_lImportID + "_max", "UTF-8");
			frame.m_strContMin = FileUtils.fileToString(m_strInputPath
					+ "frame_" + frame.m_lImportID + "_min", "UTF-8");
			frame.m_strContNormal = FileUtils.fileToString(m_strInputPath
					+ "frame_" + frame.m_lImportID + "_normal", "UTF-8");

			// 创建资源目录
			ApResDir resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
					site.m_dirFrame, "", true);

			frame.m_lDefResDirID = resDir.m_lResDirID;
			frame.m_strFrameName = m_strNamePrefix + frame.m_strFrameName;
			m_adapter.create(frame);
			frame.m_iOfficialFlag = 1;
			frame.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(frame);

			resDir.setDir(site.m_dirFrame, Long.toString(frame.m_lFrameID));
			m_adapter.update(resDir, "updateDir");

			resDir.m_vecReses = frame.m_dir.m_vecReses;
			resDir.m_vecChildDirs = frame.m_dir.m_vecChildDirs;
			importResources(site, resDir, tempDir);
		}
	}

	void importApps(ApSite site, String tempDir) throws Exception {
		if (site.m_vecApps == null)
			return;

		for (ApApp app : site.m_vecApps) {
			if (app.m_strUUID == null || app.m_strUUID.equals(""))
				app.m_strUUID = UUIDGen.get();

			// {创建预览图片
			if (app.m_strLogoName != null && !app.m_strLogoName.equals("")) {
				ApImage image = new ApImage();
				image.m_iImageType = ApImage.IMGTYPE_APPLICATION;
				image.m_strImageName = app.m_strLogoName;
				image.m_strContentType = app.m_strLogoContentType;
				image.m_strImage = m_strInputPath + app.m_strLogoFile;
				m_adapter.create(image);
				app.m_lLogoImageID = image.m_lImageID;
			}
			// }

			app.m_lSiteID = site.m_lSiteID;

			// 创建资源目录
			ApResDir resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
					site.m_dirApp, "", true);

			app.m_lResDirID = resDir.m_lResDirID;
			m_adapter.create(app);
			app.m_iOfficialFlag = 1;
			app.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(app);

			resDir.setDir(site.m_dirApp, Long.toString(app.m_lAppID));
			m_adapter.update(resDir, "updateDir");

			resDir.m_vecReses = app.m_dir.m_vecReses;
			resDir.m_vecChildDirs = app.m_dir.m_vecChildDirs;
			importResources(site, resDir, tempDir);

			importWindows(app);
		}
	}

	void importWindows(ApApp app) throws Exception {
		if (app.m_vecWindows == null)
			return;

		for (ApWindow window : app.m_vecWindows) {
			if (window.m_strUUID == null || window.m_strUUID.equals(""))
				window.m_strUUID = UUIDGen.get();

			// {创建预览图片
			if (window.m_strLogoName != null
					&& !window.m_strLogoName.equals("")) {
				ApImage image = new ApImage();
				image.m_iImageType = ApImage.IMGTYPE_WINDOW;
				image.m_strImageName = window.m_strLogoName;
				image.m_strContentType = window.m_strLogoContentType;
				image.m_strImage = m_strInputPath + window.m_strLogoFile;
				m_adapter.create(image);
				window.m_lPreviewImageID = image.m_lImageID;
			}
			// }

			window.m_lSiteID = app.m_lSiteID;
			window.m_lAppID = app.m_lAppID;
			m_adapter.create(window);
			window.m_iOfficialFlag = 1;
			window.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(window);

			// 创建参数
			for (ApWinParam param : window.getParams()) {
				param.m_lWindowID = window.m_lWindowID;
				m_adapter.create(param);
				param.m_iOfficialFlag = 1;
				param.setCommonData(ICommDataKey.SEQUENCE, "NO");
				m_adapter.create(param);
			}
		}
	}

	void importContCats(ApSite site, String tempDir) throws Exception {
		if (site.m_vecContCats == null)
			return;

		for (ApContCat contcat : site.m_vecContCats) {
			if (contcat.m_strUUID == null || contcat.m_strUUID.equals(""))
				contcat.m_strUUID = UUIDGen.get();

			contcat.m_lSiteID = site.m_lSiteID;
			contcat.m_strContCatName = m_strNamePrefix
					+ contcat.m_strContCatName;
			m_adapter.create(contcat);
			contcat.m_iOfficialFlag = 1;
			contcat.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(contcat);

			for (ApContent cont : contcat.getContents())
				importContent(site, cont, contcat.m_lContCatID, 0, tempDir);
		}
	}

	void importContent(ApSite site, ApContent content, long lContCatID,
			long lPageID, String tempDir) throws Exception {
		if (content.m_strUUID == null || content.m_strUUID.equals(""))
			content.m_strUUID = UUIDGen.get();

		// {创建预览图片
		if (content.m_strLogoName != null && !content.m_strLogoName.equals("")) {
			ApImage image = new ApImage();
			image.m_iImageType = ApImage.IMGTYPE_CONTENT;
			image.m_strImageName = content.m_strLogoName;
			image.m_strContentType = content.m_strLogoContentType;
			image.m_strImage = m_strInputPath + content.m_strLogoFile;
			m_adapter.create(image);
			// content.m_lPreviewImgID = image.m_lImageID;
		}
		// }

		content.setContent(FileUtils.fileToString(m_strInputPath + "cont_"
				+ content.m_lImportID, "UTF-8"));
		content.m_lContCatID = lContCatID;
		content.m_lSiteID = site.m_lSiteID;
		content.m_lPageID = lPageID;

		// 创建资源目录
		ApResDir resDir = ApResDir.createResDir(m_adapter, site.m_lSiteID,
				site.m_dirContent, "", true);

		content.m_lResDirID = resDir.m_lResDirID;

		m_adapter.create(content);

		// 更新资源引用url
		content.m_strContent = content.m_strContent.replaceAll(
				"../../res/cont/" + content.m_lImportID + "/",
				"../../res/cont/" + content.m_lContentID + "/");
		m_adapter.update(content, "updateContent");

		content.m_iOfficialFlag = 1;
		content.setCommonData(ICommDataKey.SEQUENCE, "NO");
		m_adapter.create(content);

		resDir.setDir(site.m_dirContent, Long.toString(content.m_lContentID));
		m_adapter.update(resDir, "updateDir");

		resDir.m_vecReses = content.m_dir.m_vecReses;
		resDir.m_vecChildDirs = content.m_dir.m_vecChildDirs;
		importResources(site, resDir, tempDir);
	}

	void importBranches(ApSite site, String tempDir) throws Exception {
		if (site.m_vecBranches == null)
			return;

		for (ApBranch branch : site.m_vecBranches) {
			if (branch.m_strUUID == null || branch.m_strUUID.equals(""))
				branch.m_strUUID = UUIDGen.get();

			branch.m_lSiteID = site.m_lSiteID;
			m_adapter.create(branch);
			branch.m_iOfficialFlag = 1;
			branch.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(branch);

			// {导入映射
			for (ApPathMap map : branch.getMaps()) {
				if (map.m_strUUID == null || map.m_strUUID.equals(""))
					map.m_strUUID = UUIDGen.get();
				map.m_lBranchID = branch.m_lBranchID;
				m_adapter.create(map);
				map.m_iOfficialFlag = 1;
				map.setCommonData(ICommDataKey.SEQUENCE, "NO");
				m_adapter.create(map);
			}
			// }

			importPages(site, branch.getPages(), branch, null, tempDir);

			// 修正页面继承模板的标识
			m_adapter.proc(branch, "fixInheritTmplAndChildCount");
		}
	}

	void importBranchGroups(ApSite site) throws Exception {
		if (site.m_vecBranchGroups == null)
			return;

		for (ApBranchGroup group : site.m_vecBranchGroups) {
			group.m_lBranchID = ApBranch.findIdByUuid(m_adapter,
					group.m_strBranchUUID);
			group.m_lGroupBranchID = ApBranch.findIdByUuid(m_adapter,
					group.m_strGroupBranchUUID);

			group.m_lSiteID = site.m_lSiteID;
			m_adapter.create(group);
			group.m_iOfficialFlag = 1;
			group.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(group);
		}
	}

	void importPages(ApSite site, Vector<ApBPage> vec, ApBranch branch,
			ApBPage parentPage, String tempDir) throws Exception {
		for (ApBPage page : vec) {
			if (page.m_strUUID == null || page.m_strUUID.equals(""))
				page.m_strUUID = UUIDGen.get();

			page.m_lBranchID = branch.m_lBranchID;
			page.m_lSiteID = branch.m_lSiteID;
			if (parentPage != null) {
				page.m_lParentID = parentPage.m_lPageID;
				page.m_strFullPath = parentPage.m_strFullPath
						+ page.m_strPathName + "/";
				page.m_strDirectPath = parentPage.m_strDirectPath
						+ page.m_strPathName + "/";
			} else {
				page.m_strFullPath = "/" + page.m_strPathName + "/";
				page.m_strDirectPath = "/";
			}

			ApTemplate template = ApTemplate.findIdAndTypeByUuid(m_adapter,
					page.m_strTemplateUUID);
			if (template == null) {
				System.err.println("Referenced tempalte not found, UUID is " + page.m_strTemplateUUID);
				throw new Exception();
			}
			page.m_lTemplateID = template.m_lTemplateID;
			m_adapter.create(page);
			page.m_iOfficialFlag = 1;
			page.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(page);

			importPsnNames(page);
			importPsnTmpls(page);
			importLayouts(page);
			importBPageContents(site, page, tempDir);
			importPages(site, page.getSubPages(), branch, page, tempDir);
		}
	}

	public void importPsnNames(ApBPage page) throws Exception {
		for (ApBPagePsnName psnName : page.getPsnNames()) {
			psnName.m_lPageID = page.m_lPageID;
			psnName.m_lBranchID = page.m_lBranchID;

			m_adapter.create(psnName);
			psnName.m_iOfficialFlag = 1;
			psnName.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(psnName);
		}
	}

	void importPsnTmpls(ApBPage page) throws Exception {
		for (ApBPagePsnTmpl psnTmpl : page.getPsnTmpls()) {
			psnTmpl.m_lPageID = page.m_lPageID;
			psnTmpl.m_lBranchID = page.m_lBranchID;

			ApTemplate template = ApTemplate.findIdAndTypeByUuid(m_adapter,
					psnTmpl.m_strTemplateUUID);
			if (template == null) {
				System.err.println("Referenced tempalte not found.");
				throw new Exception();
			}
			psnTmpl.m_lTemplateID = template.m_lTemplateID;

			m_adapter.create(psnTmpl);
			psnTmpl.m_iOfficialFlag = 1;
			psnTmpl.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(psnTmpl);
		}
	}

	public void importLayouts(ApBPage page) throws Exception {
		for (ApBPageLayout layout : page.getLayouts()) {
			layout.m_lPageID = page.m_lPageID;
			layout.m_lBranchID = page.m_lBranchID;
			layout.m_lLayoutID = ApLayout.findIdByUuid(m_adapter,
					layout.m_strLayoutUUID);

			m_adapter.create(layout);
			layout.m_iOfficialFlag = 1;
			layout.setCommonData(ICommDataKey.SEQUENCE, "NO");
			m_adapter.create(layout);
		}
	}

	void importBPageContents(ApSite site, ApBPage page, String tempDir)
			throws Exception {
		for (ApBPageContent content : page.getContents()) {
			long src = content.m_lPageContID;

			content.m_lPageID = page.m_lPageID;
			content.m_lBranchID = page.m_lBranchID;
			if (content.m_lContainContID > 0)
				content.m_lContainContID = m_htContentMap
						.get(content.m_lContainContID);

			if (content.m_strWindowUUID == null
					|| content.m_strWindowUUID.length() == 0) {
				if (content.m_content != null) {
					importContent(site, content.m_content, 0, page.m_lPageID,
							tempDir);

					content.m_lContPageID = page.m_lPageID;
					content.m_lContentID = content.m_content.m_lContentID;
					m_adapter.create(content);
					content.m_iOfficialFlag = 1;
					content.setCommonData(ICommDataKey.SEQUENCE, "NO");
					m_adapter.create(content);
				}
			} else {
				content.m_lWindowID = ApWindow.findIdByUuid(m_adapter,
						content.m_strWindowUUID);

				if (content.m_strFrameUUID != null
						&& content.m_strFrameUUID.length() > 0)
					content.m_lFrameID = ApFrame.findIdByUuid(m_adapter,
							content.m_strFrameUUID);

				m_adapter.create(content);
				content.m_iOfficialFlag = 1;
				content.setCommonData(ICommDataKey.SEQUENCE, "NO");
				m_adapter.create(content);
			}

			m_htContentMap.put(src, content.m_lPageContID);
		}
	}
}
