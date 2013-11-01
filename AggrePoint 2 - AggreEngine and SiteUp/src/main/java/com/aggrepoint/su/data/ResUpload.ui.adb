<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="cleanExisting" wrap="def" id="clean">
		<label lsid="CN">现有内容</label>
		<label lsid="EN">Existing Content</label>

		<in>
			<iconsel markup="html" />
			<select />
		</in>
	</property>

	<property name="m_strPath" wrap="def" id="path">
		<label lsid="CN">服务器本地路径</label>
		<label lsid="EN">Server Local Path</label>

		<out>
			<align>left</align>
			<wrap>no</wrap>
			<width>100%</width>
			<hideoverflow>yes</hideoverflow>
		</out>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入路径。</msg>
				<msg lsid="EN">Please input the path.</msg>
			</validator>
			<validator method="checkPath">
				<msg lsid="CN">服务器上找不到输入的路径。</msg>
				<msg lsid="EN">The path doesn't exist on server.</msg>
			</validator>
		</in>
	</property>

	<group id="edit">
		<propref name="cleanExisting" />
		<propref name="m_strPath" ajaxvalidate="yes" />
	</group>
</adb>