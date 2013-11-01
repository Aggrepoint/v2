package com.aggrepoint.adk;

import java.sql.Connection;

import com.aggrepoint.adk.data.BaseModuleDef;
import com.icebean.core.cfgmgr.ConfigManagerException;
import com.icebean.core.conn.db.DBConnException;
import com.icebean.core.conn.db.DBConnManager;

/**
 * Module的基本实现
 * 
 * @author YJM
 */
public class BaseModule implements IModule {
	public void init(BaseModuleDef def, IServerContext server)
			throws WebBaseException {
	}

	public Class<?> getImplementationObjectClass() {
		return getClass();
	}

	public Object getImplementationObject() {
		return this;
	}

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return 0;
	}

	/**
	 * 获取数据库连接，该方法只应用在init中
	 */
	public Connection getConnection() throws DBConnException,
			ConfigManagerException {
		return DBConnManager.getConnection();
	}

	/**
	 * 获取数据库连接，该方法只应用在init中
	 */
	public Connection getConnection(String conn) throws DBConnException,
			ConfigManagerException {
		return DBConnManager.getConnection(conn);
	}

	/**
	 * 关闭数据库连接，该方法只应用在init中
	 */
	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.getAutoCommit())
					conn.rollback();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
}
