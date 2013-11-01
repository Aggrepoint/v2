<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<property name="m_lTmplParamID" id="pid" />

	<property name="m_strParamName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strParamDesc" id="desc">
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

	<property name="m_strDefaultValue" id="def">
		<label lsid="CN">缺省值</label>
		<label lsid="EN">Default</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<group id="list">
		<propref name="m_strParamName" />
		<propref name="m_strParamDesc" />
		<propref name="m_strDefaultValue" />
	</group>

	<group id="edit">
		<propref name="m_strParamName" />
		<propref name="m_strParamDesc" />
		<propref name="m_strDefaultValue" />
	</group>
</adb>
