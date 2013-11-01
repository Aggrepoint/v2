<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<property name="m_lMapID" id="mid" />

	<property name="m_strFromPath" id="fpath">
		<label lsid="CN">正向映射模式（正则表达式）</label>
		<label lsid="EN">Mapping Pattern(Regexp)</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strToPath" id="tpath">
		<label lsid="CN">正向映射路径</label>
		<label lsid="EN">Mapping Path</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strFromLink" id="flink">
		<label lsid="CN">反向映射模式（正则表达式）</label>
		<label lsid="EN">Reverse Mapping Pattern(Regexp)</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strToLink" id="tlink">
		<label lsid="CN">反向映射路径</label>
		<label lsid="EN">Reverse Mapping Path</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="50" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strParamName1" id="name1">
		<label lsid="CN">参数名称1</label>
		<label lsid="EN">Parameter Name 1</label>

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

	<property name="m_strParamName2" id="name2">
		<label lsid="CN">参数名称2</label>
		<label lsid="EN">Parameter Name 2</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />
		</in>
	</property>

	<property name="m_strParamName3" id="name3">
		<label lsid="CN">参数名称3</label>
		<label lsid="EN">Parameter Name 3</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />
		</in>
	</property>

	<property name="m_strParamName4" id="name4">
		<label lsid="CN">参数名称4</label>
		<label lsid="EN">Parameter Name 4</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />
		</in>
	</property>

	<property name="m_strParamName5" id="name5">
		<label lsid="CN">参数名称5</label>
		<label lsid="EN">Parameter Name 5</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />
		</in>
	</property>

	<group id="list">
		<propref name="m_strFromPath" />
		<propref name="m_strToPath" />
	</group>

	<group id="edit">
		<propref name="m_strFromPath" />
		<propref name="m_strToPath" />
		<propref name="m_strFromLink" />
		<propref name="m_strToLink" />
		<propref name="m_strParamName1" />
		<propref name="m_strParamName2" />
		<propref name="m_strParamName3" />
		<propref name="m_strParamName4" />
		<propref name="m_strParamName5" />
	</group>
</adb>
