package com.aggrepoint.ae;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApRes;
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
public class ViewResDirect extends BaseModule {
	/** 图片内存Cache */
	FileCache m_fileCache;

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_fileCache == null) {
			m_fileCache = new FileCache(req.getParam("cache_total_limit", 0l),
					req.getParam("cache_unit_limit", 0l), req.getParam(
							"cache_valid_time", 0l));
		}

		long lResId = -1;
		int iOfficialFlag = 0;
		/** 0 - 文件本身 1 - 小图标 2 - 大图标 */
		int iType = 0;
		boolean bDownload = false;

		try {
			StringTokenizer st = new StringTokenizer(req.getRequestPath(), "/");
			// 跳过第一段
			st.nextToken();
			// 资源编号
			lResId = Long.parseLong(st.nextToken());
			if (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str.equalsIgnoreCase("small"))
					iType = 1;
				else if (str.equalsIgnoreCase("large"))
					iType = 2;
				else if (str.equalsIgnoreCase("dl"))
					bDownload = true;
			}
		} catch (Exception e) {
			return 1001;
		}

		try {
			FileCacheRecord fcr;
			fcr = m_fileCache.seek(lResId, iType == 0 ? "self"
					: (iType == 1 ? "small" : "large"));
			if (fcr != null) {
				if (bDownload)
					HTTPUtils.writeFile(
							(HttpServletResponse) resp.getResponseObject(),
							fcr.m_strContentType, fcr.m_strName, fcr.m_ba);
				else
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
			res.m_lResID = lResId;
			res.m_iOfficialFlag = iOfficialFlag;

			if (adapter.retrieve(res) == null)
				return 1002;

			res.m_strFile = res.m_strSmallIcon = res.m_strLargeIcon = PathConstructor
					.getResDir(req)
					+ res.m_lResID
					+ (iType == 0 ? "" : (iType == 1 ? "small" : "large"));
			Timestamp fileTime = (Timestamp) adapter.getData(res).m_timestamp;

			// {检查文件是否已经在本地生成并且有效
			ApPubFlag pubFlag = new ApPubFlag();
			pubFlag.m_iDocTypeID = ApPubFlag.TYPE_RES + res.m_iOfficialFlag
					* 10 + iType;
			pubFlag.m_lDocID = res.m_lResID;
			pubFlag.m_strServerName = req.getContext().getServerName();
			boolean bLoad = true;

			if (adapter.retrieve(pubFlag) != null && pubFlag.m_tDocTime != null
					&& pubFlag.m_tDocTime.equals(fileTime)) { // 已经在本地生成并且有效
				try {
					fcr = m_fileCache.add(res.m_lResID, res.m_lResID,
							iType == 0 ? "self" : (iType == 1 ? "small"
									: "large"), res.m_strFile,
							res.m_strFileName,
							iType == 0 ? res.m_strContentType : "image/jpg");
					bLoad = false;
				} catch (FileNotFoundException e) {
				}
			}

			if (bLoad) { // 需要从数据库中加载到文件系统
				adapter.retrieve(res, iType == 0 ? "loadFile"
						: (iType == 1 ? "loadSmallIcon" : "loadLargeIcon"));

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

				fcr = m_fileCache.add(res.m_lResID, res.m_lResID,
						iType == 0 ? "self" : (iType == 1 ? "small" : "large"),
						res.m_strFile, res.m_strFileName,
						iType == 0 ? res.m_strContentType : "image/jpg");
			}
			// }

			conn.commit();

			ThreadContext.setAttribute(GZIPFilter.THREAD_ATTR_DO_NOT_FILTER,
					"yes");

			if (fcr == null) {
				if (bDownload)
					HTTPUtils.writeFile((HttpServletResponse) resp
							.getResponseObject(), res.m_strFile,
							res.m_strFileName,
							iType == 0 ? res.m_strContentType : "image/jpg",
							102400);
				else
					HTTPUtils.writeFileDirect((HttpServletResponse) resp
							.getResponseObject(), res.m_strFile,
							iType == 0 ? res.m_strContentType : "image/jpg",
							102400);
			} else {
				if (bDownload)
					HTTPUtils.writeFile(
							(HttpServletResponse) resp.getResponseObject(),
							fcr.m_strName, fcr.m_strContentType, fcr.m_ba);
				else
					HTTPUtils.writeFileDirect(
							(HttpServletResponse) resp.getResponseObject(),
							fcr.m_strContentType, fcr.m_ba);
			}
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
