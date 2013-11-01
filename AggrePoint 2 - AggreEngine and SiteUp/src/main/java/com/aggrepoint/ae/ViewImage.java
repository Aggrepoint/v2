package com.aggrepoint.ae;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApImage;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.FileCache;
import com.icebean.core.common.FileCacheRecord;
import com.icebean.core.common.HTTPUtils;

/**
 * 显示AP_IMAGES中保存的图片
 * 
 * @author YJM
 */
public class ViewImage extends BaseModule {
	/** 图片内存Cache */
	FileCache m_fileCache;

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_fileCache == null) {
			m_fileCache = new FileCache(req.getParam("cache_total_limit", 0l),
					req.getParam("cache_unit_limit", 0l), req.getParam(
							"cache_valid_time", 0l));
		}

		long lID = req.getParameter("id", -1l);
		if (lID == -1)
			return 1001;

		try {
			FileCacheRecord fcr = m_fileCache.seek(lID);
			if (fcr != null) {
				HTTPUtils.writeFileDirect(
						(HttpServletResponse) resp.getResponseObject(),
						fcr.m_strContentType, fcr.m_ba);
				resp.setCommitted();
				return 0;
			}

			ApImage image = new ApImage();
			image.m_lImageID = lID;

			Connection conn = req.getDBConn();
			DbAdapter adapter = new DbAdapter(conn);
			if (adapter.retrieve(image) == null)
				return 1002;

			image.m_strImage = PathConstructor.getImageDir(req)
					+ image.m_lImageID;
			Timestamp imageTime = (Timestamp) adapter.getData(image).m_timestamp;

			// {检查文件是否已经在本地生成并且有效
			ApPubFlag pubFlag = new ApPubFlag();
			pubFlag.m_iDocTypeID = ApPubFlag.TYPE_IMAGE;
			pubFlag.m_lDocID = image.m_lImageID;
			pubFlag.m_strServerName = req.getContext().getServerName();
			boolean bLoad = true;

			if (adapter.retrieve(pubFlag) != null && pubFlag.m_tDocTime != null
					&& pubFlag.m_tDocTime.equals(imageTime)) { // 已经在本地生成并且有效
				try {
					fcr = m_fileCache.add(lID, 0l, null, image.m_strImage,
							image.m_strImageName, image.m_strContentType);
					bLoad = false;
				} catch (FileNotFoundException e) {
				}
			}

			if (bLoad) { // 需要从数据库中加载到文件系统
				adapter.retrieve(image, "loadImage");

				pubFlag.m_tDocTime = imageTime;
				if (adapter.isNew(pubFlag))
					adapter.create(pubFlag);
				else
					adapter.update(pubFlag);

				fcr = m_fileCache.add(lID, 0l, null, image.m_strImage,
						image.m_strImageName, image.m_strContentType);
			}
			// }

			conn.commit();

			if (fcr == null) {
				HTTPUtils.writeFileDirect(
						(HttpServletResponse) resp.getResponseObject(),
						image.m_strImage, image.m_strContentType, 102400);
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
