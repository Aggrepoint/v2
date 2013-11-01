package com.aggrepoint.su.core.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.aggrepoint.su.core.data.ApSite;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 该程序用于导出站点
 * 
 * 20060911 在站点导出、导入过程中，全面使用UUID。id仅用于将导出的site.xml和附带的资源文件关联起来，
 * site.xml内部的引用（例如页面对template的引用）直接使用UUID
 * 
 * 用法： ExportSite outputpath all<br>
 * ExportSite outputpath siteid1 siteid2 siteid3...<br>
 * 附加参数 -uuidprefix 指明在非全局站点内容的uuid前加上前缀
 * 
 * @author YJM
 */
public class ExportSite {
	public static void main(String[] args) {
		String outputPath;
		PrintWriter pw;
		AdbAdapter adapter;
		long lSiteID;

		try {
			outputPath = args[0];
			if (!outputPath.endsWith("/"))
				outputPath = outputPath + "/";
			File file = new File(outputPath);
			file.mkdirs();

			adapter = new DbAdapter(DBConnManager.getConnection(),
					DBConnManager.getConnectionSyntax());

			FileOutputStream fos = new FileOutputStream(outputPath + "site.xml");
			pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));

			pw.println("<?xml version='1.0' encoding='UTF-8'?>");
			pw.println("<aggrepoint>");

			ExportHelper helper = new ExportHelper(outputPath, pw, adapter);

			boolean bFirst = true;

			// { 获取uuid前缀参数
			for (int i = 1; i < args.length - 1; i++) {
				if (args[i].equalsIgnoreCase("-uuidsuffix")) {
					helper.m_strUuidSuffix = args[i + 1];
					break;
				}
			}
			// }

			if (args[1].equalsIgnoreCase("all")) {
				for (ApSite site : adapter.retrieveMulti(new ApSite())) {
					if (bFirst)
						bFirst = false;
					else
						pw.println();
					helper.exportSite(site, "\t");
				}
			} else {
				ApSite site = new ApSite();

				for (int i = 1; i < args.length; i++) {
					lSiteID = Long.parseLong(args[i]);

					site.m_lSiteID = lSiteID;
					if (adapter.retrieve(site) == null)
						System.out.println("Site \"" + lSiteID
								+ "\" not found.");
					else {
						if (bFirst)
							bFirst = false;
						else
							pw.println();

						helper.exportSite(site, "\t");
					}
				}
			}

			pw.println("</aggrepoint>");
			pw.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();

			System.out
					.println("Usage: ExportSite outputpath siteid1 siteid2 siteid3...\r\nOr: ExportSite outputpath all");
			return;
		}
	}
}