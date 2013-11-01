<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<property name="m_lContentID" id="cid" />

	<property name="m_strName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入内容名。</msg>
				<msg lsid="CN">Please input content name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strDesc" id="desc">
		<label lsid="CN">说明</label>
		<label lsid="EN">Description</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="2" cols="80" markup="html" />

			<validator id="maxlen">
				<arg>300</arg>

				<msg lsid="CN">说明不能超过300个字符。</msg>
				<msg lsid="EN">
					Description can't contain more than 300 characters.
				</msg>
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
			<custom>
				<![CDATA[
				<textarea id="cont" name="cont" rows="30" cols="100" style="width: 80%" class="tinymce">$VALUE$</textarea>
				]]>
			</custom>

			<validator id="ne">
				<msg lsid="CN">请输入内容。</msg>
				<msg lsid="EN">Please input content.</msg>
			</validator>
		</in>
	</property>

	<group id="list">
		<propref name="m_strName" />
	</group>

	<group id="info">
		<propref name="m_strName" />
		<propref name="m_strDesc" />
	</group>

	<group id="edit">
		<propref name="m_strName" />
		<propref name="m_strDesc" />
		<propref name="content" />
	</group>

	<group id="edit_page">
		<propref name="m_strDesc" />
		<propref name="content" />
	</group>
</adb>
