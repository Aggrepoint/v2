package com.aggrepoint.ae;

import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApCPage;
import com.aggrepoint.su.core.data.ApChannel;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 为栏目页面和信息列表生成URL
 * 
 * @author YJM
 */
public class CPageURLConstructor implements UrlConst {
	public ApSite site;
	public ApChannel channel;
	public ApCPage page;
	public ApTemplate template;

	CPageURLConstructor(ApSite site, ApChannel channel, ApCPage page,
			ApTemplate template) {
		this.site = site;
		this.channel = channel;
		this.page = page;
		this.template = template;
	}

	/**
	 * 生成到栏目页面资源的URL
	 */
	public String cpageRes(long pageID) {
		StringBuffer sb = new StringBuffer();

		sb.append(URL_VIEW_RES_CPAGE).append("/").append(pageID).append("/");

		return sb.toString();
	}

	/**
	 * 生成到模板资源的URL
	 */
	public String cpageTmplRes(DbAdapter adapter, String name) throws Exception {
		StringBuffer sb = new StringBuffer();

		if (page.m_iOfficialFlag == 1 && site.m_strStaticResUrl != null)
			sb.append(site.m_strStaticResUrl)
					.append(ApResDir.load(adapter,
							template.m_lResDirID == 0 ? template.m_lDefResDirID
									: template.m_lResDirID,
							template.m_iOfficialFlag).m_strFullPath)
					.append(name);
		else
			sb.append(URL_VIEW_RES_TEMPLATE).append("/")
					.append(template.m_lTemplateID).append("/").append(name);

		return sb.toString();
	}
}
