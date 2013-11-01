<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<property name="m_lContCatID" id="cid" />

	<property name="m_strContCatName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入分类名。</msg>
				<msg lsid="CN">Please input category name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strContCatDesc" id="desc">
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

	<group id="list">
		<propref name="m_strContCatName" />
	</group>

	<group id="edit">
		<propref name="m_strContCatName" />
		<propref name="m_strContCatDesc" />
	</group>
</adb>
