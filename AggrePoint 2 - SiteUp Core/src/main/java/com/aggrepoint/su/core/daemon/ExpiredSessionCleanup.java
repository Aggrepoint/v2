package com.aggrepoint.su.core.daemon;

import java.sql.Connection;
import java.sql.Statement;

import com.icebean.ds.core.DaemonBase;

/**
 * 清除过期的会话
 * 
 * @author Owner
 */
public class ExpiredSessionCleanup extends DaemonBase {
	@Override
	synchronized public void perform() throws Exception {
		try {
			logInfo("开始运行。");

			Connection conn = getConnection(null);
			Statement stmt = conn.createStatement();
			stmt
					.executeUpdate("delete from USER_SESSIONS where VALIDTIME < current timestamp");
			conn.commit();
			conn.close();

			logInfo("运行结束。");
		} catch (Exception e) {
			logError(e);
		}
	}
}
