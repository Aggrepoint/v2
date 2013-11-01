package com.aggrepoint.ae;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApResDir;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.FileCache;
import com.icebean.core.common.FileCacheRecord;
import com.icebean.core.common.HTTPUtils;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.servlet.GZIPFilter;

/**
 * 显示资源
 * 
 * @author YJM
 */
public abstract class ViewRes extends BaseModule {
	Pattern P_URL = Pattern.compile("/[^/]+/(-?\\d+)/(.+)");

	/** 图片内存Cache */
	FileCache m_fileCache;

	abstract long getResDirId(DbAdapter adapter, long ownerId, int officialFlag)
			throws Exception;

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_fileCache == null) {
			m_fileCache = new FileCache(req.getParam("cache_total_limit", 0l),
					req.getParam("cache_unit_limit", 0l), req.getParam(
							"cache_valid_time", 0l));
		}

		long lOwnerId = -1;
		String strFullName = null;
		String strFileName = null;
		String strPathName = null;
		int iOfficialFlag = 0;
		Matcher m = null;

		try {
			synchronized (P_URL) {
				m = P_URL.matcher(req.getRequestPath());
				m.find();
			}

			// 所属对象编号
			lOwnerId = Long.parseLong(m.group(1));
			// 文件及路径
			strFullName = m.group(2);

			int idx = strFullName.lastIndexOf("/");
			if (idx > 0) {
				strPathName = strFullName.substring(0, idx);
				strFileName = strFullName.substring(idx + 1);
			} else
				strFileName = strFullName;
		} catch (Exception e) {
			return 1001;
		}

		try {
			FileCacheRecord fcr;
			fcr = m_fileCache.seek(lOwnerId, strFullName);
			if (fcr != null) {
				HTTPUtils.writeFileDirect(
						(HttpServletResponse) resp.getResponseObject(),
						fcr.m_strContentType, fcr.m_ba);
				resp.setCommitted();
				return 0;
			}

			Connection conn = req.getDBConn();
			DbAdapter adapter = new DbAdapter(conn);

			ApRes res = new ApRes();

			// 获取要显示的资源的ID
			if (lOwnerId < 0) { // 指定站点
				ApResDir dir = new ApResDir();
				dir.m_lSiteID = -lOwnerId;
				dir.m_strFullPath = "/" + strPathName + "/";
				if (adapter.retrieve(dir, "loadByPath") == null)
					return 1005;
				res.m_lResDirID = dir.m_lResDirID;
			} else {
				res.m_lResDirID = getResDirId(adapter, lOwnerId, iOfficialFlag);
				if (res.m_lResDirID == 0)
					return 1005;

				if (strPathName != null) { // 资源在子目录中
					ApResDir dir = new ApResDir();
					dir.m_lResDirID = res.m_lResDirID;
					dir.m_iOfficialFlag = iOfficialFlag;
					if (adapter.retrieve(dir) == null)
						return 1005;
					dir.m_strFullPath += strPathName + "/";
					if (adapter.retrieve(dir, "loadByPath") == null)
						return 1005;
					res.m_lResDirID = dir.m_lResDirID;
				}
			}

			res.m_iOfficialFlag = iOfficialFlag;
			res.m_strFileName = strFileName;

			if (adapter.retrieve(res, "loadByDirAndName") == null)
				return 1002;

			res.m_strFile = PathConstructor.getResDir(req) + res.m_lResID;
			Timestamp fileTime = (Timestamp) adapter.getData(res).m_timestamp;

			// {检查文件是否已经在本地生成并且有效
			ApPubFlag pubFlag = new ApPubFlag();
			pubFlag.m_iDocTypeID = ApPubFlag.TYPE_RES + res.m_iOfficialFlag;
			pubFlag.m_lDocID = res.m_lResID;
			pubFlag.m_strServerName = req.getContext().getServerName();
			boolean bLoad = true;

			if (adapter.retrieve(pubFlag) != null && pubFlag.m_tDocTime != null
					&& pubFlag.m_tDocTime.equals(fileTime)) { // 已经在本地生成并且有效
				try {
					fcr = m_fileCache.add(res.m_lResID, lOwnerId, strFullName,
							res.m_strFile, res.m_strFileName,
							res.m_strContentType);
					bLoad = false;
				} catch (FileNotFoundException e) {
				}
			}

			if (bLoad) { // 需要从数据库中加载到文件系统
				adapter.retrieve(res, "loadFile");

				pubFlag.m_tDocTime = fileTime;
				if (adapter.isNew(pubFlag))
					try {
						adapter.create(pubFlag);
					} catch (Exception e) {
						// 并发请求时发布标记可能会在处理过程中被其他线程创建
						adapter.update(pubFlag);
					}
				else
					adapter.update(pubFlag);

				fcr = m_fileCache.add(res.m_lResID, lOwnerId, strFullName,
						res.m_strFile, res.m_strFileName, res.m_strContentType);
			}
			// }

			conn.commit();

			ThreadContext.setAttribute(GZIPFilter.THREAD_ATTR_DO_NOT_FILTER,
					"yes");

			if (fcr == null) {
				HTTPUtils.writeFileDirect(
						(HttpServletResponse) resp.getResponseObject(),
						res.m_strFile, res.m_strContentType, 102400);
			} else
				HTTPUtils.writeFileDirect(
						(HttpServletResponse) resp.getResponseObject(),
						fcr.m_strContentType, fcr.m_ba);
			resp.setCommitted();

			conn.rollback();
			conn.close();
		} catch (java.net.SocketException e) {
			return 1003;
		} catch (Exception e) {
			resp.setRetThrow(e);
			return 1004;
		}

		// 成功返回
		return 0;
	}
}
