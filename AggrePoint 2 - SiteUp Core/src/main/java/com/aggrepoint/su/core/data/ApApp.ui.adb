<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lAppID" id="aid" />

	<property name="m_strAppName" id="name" mandatory="yes">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入应用名。</msg>
				<msg lsid="CN">Please input application name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strAppVer" id="ver">
		<label lsid="CN">版本号</label>
		<label lsid="EN">Version</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="8" maxlength="10" />
		</in>
	</property>

	<property name="m_iStatusID" wrap="def" wrapname="status" id="status">
		<label lsid="CN">状态</label>
		<label lsid="EN">Status</label>

		<in>
			<radio />
		</in>
	</property>

	<property name="m_strHostURL" id="url" mandatory="yes">
		<label lsid="CN">Host</label>
		<label lsid="EN">Host</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入URL。</msg>
				<msg lsid="CN">Please input URL.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strRootPath" id="rootpath" mandatory="yes">
		<label lsid="CN">主路径</label>
		<label lsid="EN">Root Path</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入主路径。</msg>
				<msg lsid="CN">Please input root path.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strAppDesc" id="desc">
		<label lsid="CN">说明</label>
		<label lsid="EN">Description</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="4" cols="80" markup="html" />

			<validator id="maxlen">
				<arg>300</arg>

				<msg lsid="CN">说明不能超过300个字符。</msg>
				<msg lsid="EN">
					Description can't contain more than 300 characters.
				</msg>
			</validator>
		</in>
	</property>

	<property name="m_iConnTimeout" id="ctime" mandatory="yes">
		<label lsid="CN">连接超时（秒）</label>
		<label lsid="EN">Connection Timeout (s)</label>

		<in>
			<number width="3" maxlength="3" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_iReadTimeout" id="rtime" mandatory="yes">
		<label lsid="CN">读超时（秒）</label>
		<label lsid="EN">Read Timeout (s)</label>

		<in>
			<number width="3" maxlength="3" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_previewImage" wrap="def" wrapname="previewImg" id="img" ajaxvalidate="no">
		<label lsid="CN">预览</label>
		<label lsid="EN">Preview</label>

		<remarks lsid="CN">可选。图片文件必须是JPG或GIF格式，大小不能超过500K</remarks>
		<remarks lsid="EN">This is optional. The image file must be a JPG or
			GIF file less than 500K</remarks>

		<out>
			<wrap>no</wrap>
			<width>5%</width>
		</out>

		<in>
			<file width="30" maxsize="" />
			<validator method="checkPreviewImg" />
		</in>
	</property>

	<group id="list">
		<propref name="m_strAppName" />
		<propref name="m_strAppVer" />
		<propref name="m_iStatusID" />
		<propref name="m_strHostURL" />
		<propref name="m_strRootPath" />
		<propref name="m_iConnTimeout" />
		<propref name="m_iReadTimeout" />
		<propref name="m_previewImage" />
	</group>

	<group id="edit">
		<propref name="m_strAppName" />
		<propref name="m_strAppVer" />
		<propref name="m_iStatusID" />
		<propref name="m_strHostURL" />
		<propref name="m_strRootPath" />
		<propref name="m_strAppDesc" />
		<propref name="m_iConnTimeout" />
		<propref name="m_iReadTimeout" />
		<propref name="m_previewImage" />
	</group>
</adb>
