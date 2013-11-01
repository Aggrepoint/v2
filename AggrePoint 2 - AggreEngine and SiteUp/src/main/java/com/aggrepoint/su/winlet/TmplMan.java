package com.aggrepoint.su.winlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.form.Input;
import com.aggrepoint.adk.form.Validate;
import com.aggrepoint.adk.form.Validates;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.adk.ui.ValidateResult;
import com.aggrepoint.adk.ui.ValidateResultType;
import com.aggrepoint.su.core.admin.ExportHelper;
import com.aggrepoint.su.core.admin.ImportSite;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.ApTmplParam;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.FileUtils;

/**
 * 模板管理窗口
 * 
 * @author YJM
 */
public class TmplMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	public ResTree tree = new ResTree();
	public ResMan man = new ResMan();

	/** 用于列表的模板对象 */
	public ApTemplate m_tmpl;

	/** 查看的模板 */
	public long m_lTmplId;

	/** 模板编辑对象 */
	ApTemplate m_tmplEdit;

	/** 是否更新模板 */
	public boolean m_bUpdate;

	/** 模板参数编辑 */
	ApTmplParam m_paramEdit;

	/** 抓取地址 */
	public String m_strCrawlUrl;

	public TmplMan() throws Exception {
		m_tmpl = new ApTemplate();
	}

	public void reset() {
		super.reset();
		m_tmpl.m_iTmplType = ApTemplate.TYPE_BPAGE;
		m_tmpl.setCommonData(ICommDataKey.SORT, "0");
		m_tmpl.setCommonData(ICommDataKey.PAGE, "1");
		m_lTmplId = -1;
		m_tmplEdit = null;
		m_paramEdit = null;
	}

	/**
	 * 选择模板
	 */
	public int selectTemplate(IModuleRequest req, long tid) throws Exception {
		if (tid <= ApTemplate.TYPE_LIST && tid >= ApTemplate.TYPE_BPAGE) {
			m_tmpl.m_iTmplType = (int) tid;
			m_lTmplId = -1;
		} else {
			ApTemplate tmpl = new ApTemplate();
			tmpl.m_lSiteID = getSiteId(req);
			tmpl.m_lTemplateID = tid;

			DbAdapter adapter = new DbAdapter(req.getDBConn());
			if (adapter.retrieve(tmpl, "loadInSite") == null)
				return 1002;

			m_lTmplId = tid;
			tree.setRoot(tmpl.m_iOfficialFlag, tmpl.m_lDefResDirID);
			man.selectDir(req, tmpl.m_lDefResDirID);
		}
		m_tmplEdit = null;
		m_paramEdit = null;
		return 0;
	}

	ApTemplate getSelectedTemplate(DbAdapter adapter, long siteId)
			throws Exception {
		if (m_lTmplId <= 0)
			return null;
		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = m_lTmplId;
		if (adapter.retrieve(template) == null || template.m_lSiteID != siteId
				|| template.m_iTmplType != m_tmpl.m_iTmplType) {
			m_lTmplId = -1;
			return null;
		}
		return template;
	}

	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lTmplId <= 0)
			return m_tmpl.m_iTmplType;

		return 10;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 模板列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_tmpl.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_tmpl.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示模板列表页面
	 */
	public int showTemplateList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_tmpl.m_lSiteID = getSiteId(req);

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_tmpl, "loadByType", "order",
						getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100, -1,
						true));
		return m_tmpl.m_iTmplType;
	}

	/**
	 * 新建模板
	 */
	public int addTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lTmplId = -1;
		m_tmplEdit = new ApTemplate();
		m_tmplEdit.m_iTmplType = m_tmpl.m_iTmplType;
		m_paramEdit = null;
		return 0;
	}

	/**
	 * 启动抓取模板
	 */
	public int crawlTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		addTemplate(req, resp);
		m_strCrawlUrl = new String("");
		return 0;
	}

	/**
	 * 选择模板
	 */
	public int selectTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		selectTemplate(req, req.getParameter("tid", -1l));
		return 0;
	}

	/**
	 * 删除模板
	 */
	public int delTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = req.getParameter("tid", -1l);
		if (template.m_lTemplateID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// 加载资源目录等属性以便删除
		if (adapter.retrieve(template) == null)
			return 1002;

		// 删除模板及连带的数据
		adapter.proc(template, "delete");

		if (m_lTmplId == template.m_lTemplateID)
			closeTemplate(req, resp);

		return 0;
	}

	/**
	 * 导出模板
	 */
	public int exportTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = req.getParameter("id", -1l);
		if (template.m_lTemplateID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		if (adapter.retrieve(template) == null)
			return 1002;

		adapter.retrieve(template, "loadAllExtra");

		String tempPath = req.getTempDir() + File.separator;
		PrintWriter pw = null;

		try {
			FileOutputStream fos = new FileOutputStream(tempPath + "tmpl.xml");
			pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
			pw.println("<?xml version='1.0' encoding='UTF-8'?>");
			ExportHelper helper = new ExportHelper(tempPath, pw, adapter);
			helper.exportTemplate(template, "");
			pw.flush();

			HttpServletResponse httprep = (HttpServletResponse) resp
					.getResponseObject();
			httprep.setContentType("application/zip");
			httprep.addHeader("Content-Disposition",
					"attachment;filename=tmpl.zip");
			OutputStream os = httprep.getOutputStream();
			FileUtils.zip(tempPath, os);
			os.flush();
			os.close();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pw.close();
			} catch (Exception ee) {
			}
		}

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 添加模板或抓取模板
	//
	//
	// /////////////////////////////////////////////////////////

	public int showTemplateAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_tmplEdit == null || m_tmplEdit.m_lTemplateID > 0)
			return 8000;

		// 编辑
		req.setAttribute("EDIT", m_tmplEdit);
		return 0;
	}

	@Validates({ @Validate(name = "name", id = "ne", error = "模板名称不能为空。"),
			@Validate(name = "order", id = "ne", error = "模板顺序不能为空。"),
			@Validate(name = "tmpl", id = "ne", error = "模板内容不能为空。"),
			@Validate(name = "url", id = "ne", error = "抓取地址不能为空。") })
	public ValidateResult validate(IModuleRequest req, Input inp) {
		if (inp.getName().equals("tmpl")) {
			String error = m_tmplEdit.validateContent();
			if (error != null)
				return new ValidateResult(
						ValidateResultType.FAILED_SKIP_PROPERTY, error);
		} else if (inp.getName().equals("upload")) {
			try {
				FileParameter upload = (FileParameter) inp.getPropValue();

				if (upload.m_lSize <= 0)
					return new ValidateResult(
							ValidateResultType.FAILED_SKIP_PROPERTY,
							req.getMessage("uploadtoolarge", ""));

				if (upload.m_strContentType.indexOf("zip") < 0)
					return new ValidateResult(
							ValidateResultType.FAILED_SKIP_PROPERTY,
							req.getMessage("uploadformatwrong", ""));
			} catch (Exception e) {
			}
		} else if (inp.getName().equals("preview")) {
			long[] id = new long[1];

			try {
				ValidateResult vr = validateImage(req, inp, id);
				if (id[0] > 0)
					m_tmplEdit.m_lPreviewImgID = id[0];
				return vr;
			} catch (Exception e) {
			}
		} else if (inp.getName().equals("url")) {
			try {
				URL source = new java.net.URL(m_strCrawlUrl);
				source.openConnection().getInputStream().read();
			} catch (Exception e) {
				return new ValidateResult(
						ValidateResultType.FAILED_SKIP_PROPERTY, "无法读取给定的URL。");
			}
		}

		return ValidateResult.PASS;
	}

	/**
	 * 保存模板或抓取模板编辑
	 */
	public int saveTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (req.isValidateField())
			return 10;

		if (req.getForm().hasError())
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		long siteId = getSiteId(req);

		m_tmplEdit.m_lSiteID = siteId;

		if (m_strCrawlUrl != null) {
			m_tmplEdit.crawl(adapter, m_strCrawlUrl);
		} else {
			if (adapter.isNew(m_tmplEdit)) {
				ApResDir dir = new ApResDir();
				dir.m_lSiteID = siteId;
				dir.m_strFullPath = "/sys/tmpl/";
				adapter.retrieve(dir, "loadByPath");

				ApResDir resDir = ApResDir.createResDir(adapter, siteId, dir,
						"", true);

				m_tmplEdit.m_lDefResDirID = resDir.m_lResDirID;
				m_tmplEdit.m_strUUID = UUIDGen.get();
				adapter.create(m_tmplEdit);

				resDir.setDir(dir, Long.toString(m_tmplEdit.m_lTemplateID));
				adapter.update(resDir, "updateDir");
			} else {
				if (m_bUpdate) {
					String temp = req.getTempDir() + File.separator;
					FileParameter file = req.getFileParameter("upload");
					FileInputStream fis = new FileInputStream(
							file.m_strFullPath);
					FileUtils.unzip(temp, fis);
					fis.close();
					fis = new FileInputStream(temp + "tmpl.xml");
					XmlAdapter ad = new XmlAdapter(fis);
					ApTemplate tmpl = new ApTemplate();
					ad.retrieve(tmpl, "loadSeperate");

					ApSite site = new ApSite();
					site.m_lSiteID = m_tmplEdit.m_lSiteID;
					site = adapter.retrieve(site);

					site.m_dirTemplate = new ApResDir();
					site.m_dirTemplate.m_lSiteID = site.m_lSiteID;
					site.m_dirTemplate.m_strFullPath = "/sys/tmpl/";
					site.m_dirTemplate = adapter.retrieve(site.m_dirTemplate,
							"loadByPath");

					ImportSite imp = new ImportSite("", temp, adapter);
					imp.importTemplate(tmpl, site, m_tmplEdit, temp);
				} else
					adapter.update(m_tmplEdit);

				// 清除发布标记
				ApPubFlag pubFlag = new ApPubFlag();
				pubFlag.m_lDocID = m_tmplEdit.m_lTemplateID;
				pubFlag.m_iDocTypeID = ApPubFlag.TYPE_TEMPLATE
						+ m_tmplEdit.m_iOfficialFlag;
				adapter.delete(pubFlag, "clearFlag");
			}
		}

		m_tmplEdit = null;
		m_strCrawlUrl = null;
		return 0;
	}

	/**
	 * 取消模板编辑
	 */
	public int cancelTemplateEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_tmplEdit = null;
		m_paramEdit = null;
		m_strCrawlUrl = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 模板详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showTemplateInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_tmplEdit != null || m_lTmplId <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApTemplate template = getSelectedTemplate(adapter, getSiteId(req));
		if (template == null)
			return 8000;

		req.setAttribute("INFO", template);

		return 0;
	}

	/**
	 * 编辑模板
	 */
	public int editTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_bUpdate = false;

		if (m_lTmplId == -1)
			return 1001;

		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = m_lTmplId;

		// 加载要编辑的模板
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_tmplEdit = adapter.retrieve(template);

		// 清空其他编辑对象
		m_paramEdit = null;

		if (m_tmplEdit == null)
			return 1002;

		adapter.retrieve(m_tmplEdit, "loadDetail");

		return 0;
	}

	/**
	 * 上传覆盖模板
	 */
	public int updateTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		int retcode = editTemplate(req, resp);
		if (retcode == 0)
			m_bUpdate = true;
		return retcode;
	}

	/**
	 * 关闭模板显示
	 */
	public int closeTemplate(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lTmplId = -1;
		m_tmplEdit = null;
		m_paramEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 模板编辑
	//
	//
	// /////////////////////////////////////////////////////////

	public int showTemplateEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_tmplEdit == null || m_tmplEdit.m_lTemplateID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_tmplEdit);
		return m_bUpdate ? 10 : 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 参数列表
	//
	//
	// /////////////////////////////////////////////////////////

	/**
	 * 显示参数列表
	 */
	public int showParamList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApTemplate template = getSelectedTemplate(adapter, getSiteId(req));
		if (template == null)
			return 8000;

		ApTmplParam param = new ApTmplParam();
		param.m_lTemplateID = template.m_lTemplateID;
		param.m_iOfficialFlag = template.m_iOfficialFlag;
		req.setAttribute("LIST",
				adapter.retrieveMultiDbl(param, "loadByTemplate", "order"));
		return 0;
	}

	public int newParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_paramEdit = new ApTmplParam();
		m_paramEdit.m_lTemplateID = m_lTmplId;
		return 0;
	}

	public int editParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_paramEdit = null;
		ApTmplParam param = new ApTmplParam();
		param.m_lTmplParamID = req.getParameter("pid", -1);
		if (param.m_lTmplParamID == -1)
			return 3001;
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_paramEdit = adapter.retrieve(param);
		if (m_paramEdit == null)
			return 3002;

		return 0;
	}

	public int delParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApTmplParam param = new ApTmplParam();
		param.m_lTmplParamID = req.getParameter("pid", -1);
		if (param.m_lTmplParamID == -1)
			return 3001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		adapter.delete(param);
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 参数编辑
	//
	//
	// /////////////////////////////////////////////////////////
	public int showParamEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_paramEdit == null)
			return 8000;
		req.setAttribute("EDIT", m_paramEdit);
		return 0;
	}

	public int validateParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_paramEdit, "edit");
	}

	public int cancelParamEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_paramEdit = null;
		return 0;
	}

	public int saveParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_paramEdit).populate(m_paramEdit, "edit"))
			return 1;

		adapter.createOrUpdate(m_paramEdit);
		m_paramEdit = null;
		return 0;
	}
}
