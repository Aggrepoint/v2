<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lTemplateID" id="tid" />

	<property name="m_strTmplName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入名称。</msg>
				<msg lsid="CN">Please input template name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_iOrder" id="order">
		<label lsid="CN">顺序</label>
		<label lsid="EN">Order</label>

		<in>
			<number width="5" maxlength="5" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入顺序。</msg>
				<msg lsid="CN">Please input order.</msg>
			</validator>
		</in>
	</property>

	<property name="m_iMarkupType" wrapname="markup" id="markup" wrap="def">
		<label lsid="CN">标记</label>
		<label lsid="EN">Markup</label>

		<in>
			<radio />
		</in>
	</property>

	<property name="m_strAreas" id="areas">
		<label lsid="CN">栏位</label>
		<label lsid="EN">Areas</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="content" id="cont" wrap="def">
		<label lsid="CN">模板内容</label>
		<label lsid="EN">Content</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="20" cols="100" markup="html" />
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
		<propref name="m_strTmplName" />
		<propref name="m_iMarkupType" />
		<propref name="m_strAreas" />
		<propref name="m_previewImage" />
	</group>

	<group id="info">
		<propref name="m_strTmplName" />
		<propref name="m_iMarkupType" />
		<propref name="m_strAreas" />
		<propref name="m_previewImage" />
	</group>

	<group id="edit">
		<propref name="m_strTmplName" />
		<propref name="m_iOrder" />
		<propref name="m_iMarkupType" />
		<propref name="content" />
		<propref name="m_previewImage" />
	</group>

	<group id="upload">
		<propref name="m_strTmplName" />
		<propref name="m_previewImage" />
	</group>
</adb>
