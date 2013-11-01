package com.aggrepoint.su.core.admin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;

import com.aggrepoint.su.core.data.UserAgent;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 将ua.txt中的浏览器配置倒入数据库
 * 
 * @author Jim
 * 
 */
public class InitUserAgent {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Connection conn = DBConnManager.getConnection("direct");
			conn.setAutoCommit(true);
			DbAdapter adapter = new DbAdapter(conn, DBConnManager
					.getConnectionSyntax());

			InputStream is = InitUserAgent.class
					.getResourceAsStream("ua.txt");
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
					is));
			String line;
			while ((line = lnr.readLine()) != null) {
				line = line.trim();
				if (line.equals(""))
					continue;

				int idx = line.indexOf("~");
				if (idx == -1)
					continue;

				UserAgent.getUserAgent(adapter, line.substring(0, idx), line
						.substring(idx + 1));
			}

			System.out.println("处理完毕。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
