<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<property name="m_lResDirID" id="id"/>

	<property name="m_strDirName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入目录名。</msg>
				<msg lsid="EN">Please input directory name.</msg>
			</validator>
			<validator id="re" skip="next">
				<arg><![CDATA[^[\d\w_]{1,50}$]]></arg>

				<msg lsid="CN">目录名中包含不合法字符。请用字母、数字或下划线。</msg>
				<msg lsid="EN">Directory name contains invalid character.</msg>
			</validator>
			<validator method="checkDir">
				<msg lsid="CN">目录已经存在。</msg>
				<msg lsid="EN">Directory already exists.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strFullPath">
		<label lsid="CN">路径</label>
		<label lsid="EN">Path</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<group id="list">
		<propref name="m_strDirName" />
		<propref name="m_strFullPath" />
	</group>

	<group id="edit">
		<propref name="m_strDirName" ajaxvalidate="yes"/>
	</group>
</adb>
