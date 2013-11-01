package com.aggrepoint.su.core.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.FileParameter;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.HTTPUtils;

/**
 * 
 * @author YJM
 */
public class ApTemplate extends ADB {
	static Pattern patternArea = Pattern.compile(
			"\\s*<ae:area\\s*name=\\s*\"\\s*(\\S*)\\s*\"\\s*/>\\s*",
			Pattern.CASE_INSENSITIVE);

	public static final int TYPE_BPAGE = 0;
	public static final int TYPE_CPAGE = 1;
	public static final int TYPE_LIST = 2;

	/** 使用的模板的编号 */
	public long m_lTemplateID;

	public int m_iOfficialFlag;

	public boolean m_bPubFlag;

	public boolean m_bForPage;

	public long m_lSiteID;

	public long m_lDefResDirID;

	public long m_lResDirID;

	public int m_iTmplType;

	public long m_lPreviewImgID;

	public String m_strTmplName;

	public int m_iOrder;

	public String m_strContent;

	public int m_iMarkupType;

	public String m_strAreas;

	public String m_strUUID;

	public Vector<ApTmplParam> m_vecParams;

	/** 引用该模板的BPage */
	public Vector<ApBPage> m_vecBPages;

	/** 引用该模板的个性化BPage模板设置 */
	public Vector<ApBPagePsnTmpl> m_vecBPagePsnTmpls;

	/** 引用该模板的CPage */
	public Vector<ApCPage> m_vecCPages;

	/** 以下属性用于从XML文件中导入 */
	public long m_lImportID;

	public String m_strLogoContentType;

	public String m_strLogoName;

	public String m_strLogoFile;

	public ApResDir m_dir;

	/** Template Cache */
	static Hashtable<String, ApTemplate> m_htTemplates = new Hashtable<String, ApTemplate>();

	public FileParameter m_previewImage;

	public ApTemplate() throws AdbException {
		m_lTemplateID = m_lSiteID = m_lPreviewImgID = m_lImportID = 0;
		m_iOfficialFlag = m_iTmplType = m_iOrder = 0;
		m_strTmplName = m_strContent = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strUUID = m_strAreas = "";
		m_iMarkupType = EnumMarkup.HTML.getId();
	}

	public EnumMarkup[] getMarkups() {
		return EnumMarkup.getValidValues();
	}

	public String getMarkupId() {
		return EnumMarkup.fromId(m_iMarkupType).getStrId();
	}

	public void setMarkupId(String str) {
		m_iMarkupType = EnumMarkup.fromStrId(str).getId();
	}

	public ApResDir getDir() throws Exception {
		if (m_dir == null)
			m_dir = new ApResDir();
		return m_dir;
	}

	public long getPreviewImgId() {
		return m_lPreviewImgID;
	}

	public void setDir(ApResDir dir) {
		m_dir = dir;
	}

	public String getMarkup() {
		return EnumMarkup.fromId(m_iMarkupType).getName();
	}

	public long getId() {
		return m_lTemplateID;
	}

	public String getName() {
		return m_strTmplName;
	}

	public boolean isForSite() {
		return m_iTmplType == TYPE_BPAGE;
	}

	public boolean isForChannel() {
		return m_iTmplType == TYPE_CPAGE;
	}

	public String getAreas() {
		return m_strAreas == null ? "" : m_strAreas;
	}

	public Collection<ApTmplParam> getParams() {
		if (m_vecParams == null)
			m_vecParams = new Vector<ApTmplParam>();
		return m_vecParams;
	}

	public void setParams(Collection<ApTmplParam> col) {
	}

	public Collection<ApBPage> getBPages() {
		if (m_vecBPages == null)
			m_vecBPages = new Vector<ApBPage>();
		return m_vecBPages;
	}

	public void setBPages(Collection<ApBPage> col) {
	}

	public Collection<ApBPagePsnTmpl> getBPagePsnTmpls() {
		if (m_vecBPagePsnTmpls == null)
			m_vecBPagePsnTmpls = new Vector<ApBPagePsnTmpl>();
		return m_vecBPagePsnTmpls;
	}

	public void setBPagePsnTmpls(Collection<ApBPagePsnTmpl> col) {
	}

	public Collection<ApCPage> getCPages() {
		if (m_vecCPages == null)
			m_vecCPages = new Vector<ApCPage>();
		return m_vecCPages;
	}

	public void setCPages(Collection<ApCPage> col) {
	}

	public String getContent() {
		return m_strContent;
	}

	public void setContent(String content) {
		m_strContent = content;

		Matcher matcher = patternArea.matcher(content);
		m_strAreas = "";
		while (matcher.find()) {
			if (m_strAreas.equals(""))
				m_strAreas = matcher.group(1);
			else
				m_strAreas = m_strAreas + ", " + matcher.group(1);
		}
	}

	static final Pattern[] PATTERN_VALIDATE = new Pattern[] {
			Pattern.compile("<\\s*(ae\\:)?html(>|\\s+)",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile("</\\s*(ae\\:)?html\\s*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<\\s*(ae\\:)?body(>|\\s+)",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile("</\\s*(ae\\:)?body\\s*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<\\s*(ae\\:)?head(>|\\s+)",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile("</\\s*(ae\\:)?head\\s*>", Pattern.CASE_INSENSITIVE) };
	static final String[] ELEMENT_VALIDATE = new String[] { "html", "body",
			"head" };

	public String validateContent() {
		if (m_strContent == null)
			return "模板内容不能为空。";

		for (int i = 0; i < ELEMENT_VALIDATE.length; i++) {
			Matcher m = PATTERN_VALIDATE[i * 2].matcher(m_strContent);
			String startPrefix = null;
			if (!m.find())
				return "必须有<" + ELEMENT_VALIDATE[i] + ">标签。";
			startPrefix = m.group(1);
			if (m.find())
				return "不能出现多于一个<" + ELEMENT_VALIDATE[i] + ">标签。";

			String endPrefix = null;
			m = PATTERN_VALIDATE[i * 2 + 1].matcher(m_strContent);
			if (!m.find())
				return "必须有</" + ELEMENT_VALIDATE[i] + ">标签。";
			endPrefix = m.group(1);
			if (m.find())
				return "不能出现多于一个</" + ELEMENT_VALIDATE[i] + ">标签。";
			if (!(startPrefix == null && endPrefix == null))
				if (startPrefix == null && endPrefix != null
						|| startPrefix != null && endPrefix == null
						|| !startPrefix.equals(endPrefix))
					return "<" + ELEMENT_VALIDATE[i] + ">的起始和结束标签不匹配。";
		}

		return null;
	}

	static final Pattern[] RES_PATTERNS = new Pattern[] {
			Pattern.compile("<script .*?src.*?=.*?(\"|')(.*?)\\1.*?(?:>|/>)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
			Pattern.compile("<link .*?href.*?=.*?(\"|')(.*?)\\1.*?(?:>|/>)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
			Pattern.compile("<img .*?src.*?=.*?(\"|')(.*?)\\1.*?(?:>|/>)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE) };
	static final Pattern PATTERN_HTML_START = Pattern.compile(
			"<\\s*html\\s*(.*?)\\s*>", Pattern.CASE_INSENSITIVE);
	static final Pattern PATTERN_BODY_START = Pattern.compile(
			"<\\s*body\\s*(.*?)\\s*>", Pattern.CASE_INSENSITIVE);
	static final Pattern[] REPLACE_PATTERNS = new Pattern[] {
			Pattern.compile("</\\s*html\\s*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("</\\s*body\\s*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<\\s*head\\s*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("</\\s*head\\s*>", Pattern.CASE_INSENSITIVE) };
	static final String[] REPLACE_WITHS = new String[] { "</ae:html>",
			"</ae:body>", "<ae:head>", "</ae:head>" };

	/**
	 * 生成JSP文件
	 */
	public void generateJspFiles(String path) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + m_lTemplateID + ".jsp");
			if (isForSite())
				fos.write("<%@ include file=\"_bpage_header.jsp\"%>"
						.getBytes("UTF-8"));
			else if (isForChannel())
				fos.write("<%@ include file=\"_cpage_header.jsp\"%>"
						.getBytes("UTF-8"));
			else
				fos.write("<%@ include file=\"_list_header.jsp\"%>"
						.getBytes("UTF-8"));

			fos.write("<%@ page language=\"java\"".getBytes("UTF-8"));
			if (m_iMarkupType == EnumMarkup.WML.getId())
				fos.write(" contentType=\"text/vnd.wap.wml; charset=UTF-8\""
						.getBytes("UTF-8"));
			else if (m_iMarkupType == EnumMarkup.HTML.getId())
				fos.write(" contentType=\"text/html; charset=UTF-8\""
						.getBytes("UTF-8"));
			fos.write(" pageEncoding=\"UTF-8\"%>".getBytes("UTF-8"));

			// { 处理样式，脚本及图片对资源的引用
			String content = m_strContent;
			for (Pattern p : RES_PATTERNS) {
				StringBuffer sb = new StringBuffer();
				int start = 0;

				Matcher m = p.matcher(content);
				while (m.find()) {
					sb.append(content.substring(start, m.start()));
					start = m.end();

					String url = m.group(2).toLowerCase();

					if (url.startsWith("<ae:res") || url.startsWith("http:")
							|| url.startsWith("https:")) {
						sb.append(m.group(0));
					} else {
						sb.append(content.substring(m.start(), m.start(2)));
						sb.append("<ae:res name=");
						sb.append(m.group(1));
						sb.append(m.group(2));
						sb.append(m.group(1));
						sb.append("/>");
						sb.append(content.substring(m.end(2), m.end()));
					}
				}
				sb.append(content.substring(start));
				content = sb.toString();
			}
			// }

			// { 处理html开始标签
			Matcher m = PATTERN_HTML_START.matcher(content);
			if (m.find()) {
				StringBuffer sb = new StringBuffer();
				sb.append(content.substring(0, m.start()));
				sb.append("<ae:html attr=\"");
				sb.append(m.group(1).replaceAll("\"", "\\\\\""));
				sb.append("\"");
				sb.append(content.substring(m.end(1)));
				content = sb.toString();
			}
			// }

			// { 处理body开始标签
			m = PATTERN_BODY_START.matcher(content);
			if (m.find()) {
				StringBuffer sb = new StringBuffer();
				sb.append(content.substring(0, m.start()));
				sb.append("<ae:body attr=\"");
				sb.append(m.group(1).replaceAll("\"", "\\\\\""));
				sb.append("\"");
				sb.append(content.substring(m.end(1)));
				content = sb.toString();
			}
			// }

			// 处理html结束,body结束,head标签
			for (int i = 0; i < REPLACE_PATTERNS.length; i++) {
				m = REPLACE_PATTERNS[i].matcher(content);
				if (m.find())
					content = m.replaceFirst(REPLACE_WITHS[i]);
			}

			fos.write(content.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * 清除已生成的JSP文件
	 */
	public void removeJspFiles(String path) {
		try {
			File file = new File(path + m_lTemplateID + ".jsp");
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从Cache中获取Template
	 */
	public static ApTemplate getTemplate(AdbAdapter adapter, long templateID,
			boolean forceCheck) throws Exception {
		ApTemplate template = null;
		String key = Long.toString(templateID);

		template = m_htTemplates.get(key);

		if (template != null) {
			if (!adapter.syncCheck(template, forceCheck, false)) {
				// 不同步时不直接在原对象上进行同步，以防影响已经获取该对象的线程的处理
				m_htTemplates.remove(key);
				template = null;
			}
		}

		if (template == null) { // 需要加载
			template = new ApTemplate();
			template.m_lTemplateID = templateID;

			if (adapter.retrieve(template) == null)
				return null;

			m_htTemplates.put(key, template);
		}

		return template;
	}

	/**
	 * 根据id查找UUID
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ApTemplate findUuidById(AdbAdapter adapter, long id)
			throws Exception {
		ApTemplate template = new ApTemplate();

		template.m_lTemplateID = id;
		template = adapter.retrieve(template, "loadUUIDWithCache");
		if (template == null)
			throw new Exception("Template " + id + " not found!");
		return template;
	}

	/**
	 * 根据UUID查找ID
	 * 
	 * @param adapter
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static long findIdByUuid(AdbAdapter adapter, String uuid)
			throws Exception {
		ApTemplate template = new ApTemplate();

		template.m_strUUID = uuid;
		template = adapter.retrieve(template, "loadIDWithCache");
		if (template == null)
			throw new Exception("Template " + uuid + " not found!");
		return template.m_lTemplateID;
	}

	public static ApTemplate findIdAndTypeByUuid(AdbAdapter adapter, String uuid)
			throws Exception {
		ApTemplate template = new ApTemplate();

		template.m_strUUID = uuid;
		return adapter.retrieve(template, "loadIDAndTypeWithCache");

	}

	public void crawl(DbAdapter adapter, String url) {
		try {
			String file = HTTPUtils.httpToString(url, "gb2312");
		} catch (Exception e) {
		}
	}
}
