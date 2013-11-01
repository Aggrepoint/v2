package com.aggrepoint.adk;


/**
 * 用于表示请求中包含的上传文件
 * 
 * @author YJM
 */
public class FileParameter {
	public IModuleRequest m_request;
	/** 上传文件名称 */
	public String m_strFileName;
	/** 上传文件的ContentType */
	public String m_strContentType;
	/** 上传文件被保存到本地后的全路径 */
	public String m_strFullPath;
	/** 文件大小，为负数则表示文件大小超过限制 */
	public long m_lSize;

	public FileParameter(IModuleRequest req) {
		m_request = req;
		m_strFileName = m_strContentType = m_strFullPath = "";
		m_lSize = 0;
	}

	/**
	 * 获取不带任何路径的文件名称
	 */
	public String getFileName() {
		int i = m_strFileName.lastIndexOf('\\');
		if (i <= 0)
			i = m_strFileName.lastIndexOf('/');
		if (i > 0)
			return m_strFileName.substring(i + 1);
		return m_strFileName;
	}
}
