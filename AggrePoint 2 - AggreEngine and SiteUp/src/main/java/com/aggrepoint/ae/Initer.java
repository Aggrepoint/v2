package com.aggrepoint.ae;

import java.sql.Connection;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.WebBaseException;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.Param;
import com.aggrepoint.su.core.data.ApImage;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.conn.db.DBConnManager;

/**
 * 初始化AE
 * 
 * @author YJM
 */
public class Initer extends BaseModule {
	public void init(BaseModuleDef def, IServerContext server)
			throws WebBaseException {
		String str = null;

		Param param = def.getParameter("uniquelink");
		if (param != null)
			str = param.m_strValue;
		if (str != null && str.equalsIgnoreCase("n"))
			BPageURLConstructor.m_bUniqueLink = false;
		else
			BPageURLConstructor.m_bUniqueLink = true;

		Connection conn = null;
		try {
			conn = DBConnManager.getConnection();
			DbAdapter adapter = new DbAdapter(conn);

			// 清除本地发布标记
			ApPubFlag flag = new ApPubFlag();
			flag.m_strServerName = server.getServerName();
			adapter.delete(flag, "deleteByServer");

			// 删除没用的IMAGE
			adapter.proc(new ApImage(), "deleteUnused");
		} catch (Exception e) {
			throw new WebBaseException("Error deleting pub flags.", e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}
	}
}
