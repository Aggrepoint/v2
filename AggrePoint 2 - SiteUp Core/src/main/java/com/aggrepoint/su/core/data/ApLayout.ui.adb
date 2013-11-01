<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lLayoutID" id="lid" />

	<property name="m_strLayoutName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入名称。</msg>
				<msg lsid="CN">Please input frame name.</msg>
			</validator>
		</in>
	</property>

	<property name="content" id="cont">
		<label lsid="CN">内容</label>
		<label lsid="EN">Content</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="20" cols="100" markup="html" />

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
		<propref name="m_strLayoutName" />
		<propref name="m_previewImage" />
	</group>

	<group id="info">
		<propref name="m_strLayoutName" />
		<propref name="m_previewImage" />
	</group>

	<group id="edit">
		<propref name="m_strLayoutName" />
		<propref name="content" />
		<propref name="m_previewImage" />
	</group>
</adb>
