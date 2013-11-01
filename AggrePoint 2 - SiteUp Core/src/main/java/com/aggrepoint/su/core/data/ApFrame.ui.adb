<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lFrameID" id="fid" />

	<property name="m_strFrameName" id="name">
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

	<property name="m_strContNormal" id="contnorm">
		<label lsid="CN">普通模式</label>
		<label lsid="EN">Normal Mode</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="8" cols="100" markup="html" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strContMax" id="contmax">
		<label lsid="CN">最大化模式</label>
		<label lsid="EN">Max Mode</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="8" cols="100" markup="html" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strContMin" id="contmin">
		<label lsid="CN">最小化模式</label>
		<label lsid="EN">Min Mode</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="8" cols="100" markup="html" />

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
		<propref name="m_strFrameName" />
		<propref name="m_previewImage" />
	</group>

	<group id="info">
		<propref name="m_strFrameName" />
		<propref name="m_iOrder" />
		<propref name="m_previewImage" />
	</group>

	<group id="edit">
		<propref name="m_strFrameName" />
		<propref name="m_iOrder" />
		<propref name="m_strContNormal" />
		<propref name="m_strContMax" />
		<propref name="m_strContMin" />
		<propref name="m_previewImage" />
	</group>
</adb>
