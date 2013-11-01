<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lPageID" id="pid" />

	<property name="m_strPageName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<remarks lsid="CN" markup="html">页面名称。</remarks>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入页面名。</msg>
				<msg lsid="CN">Please input page name.</msg>
			</validator>
		</in>
	</property>

	<property name="type" wrap="def" id="type">
		<label lsid="CN">类型</label>
		<label lsid="EN">Type</label>
	</property>

	<property name="m_strPathName" id="path">
		<label lsid="CN">路径</label>
		<label lsid="EN">Path</label>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入栏目路径。</msg>
				<msg lsid="CN">Please input path.</msg>
			</validator>
			<validator id="re" skip="next">
				<arg><![CDATA[^[\d\w_]{1,50}$]]></arg>

				<msg lsid="CN">路径中包含不合法字符。请用字母、数字或下划线。</msg>
				<msg lsid="EN">Directory name contains invalid character.</msg>
			</validator>
			<validator method="checkPath">
				<msg lsid="CN">路径已经存在。</msg>
				<msg lsid="EN">Path already exists.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strFullPath" id="fpath">
		<label lsid="CN">全路径</label>
		<label lsid="EN">Full Path</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="m_strAccessRule" id="accessRule">
		<label lsid="CN">访问规则</label>
		<label lsid="EN">Access Rule</label>

		<in>
			<text width="80" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入访问规则。</msg>
				<msg lsid="CN">Please input access rule.</msg>
			</validator>
		</in>
	</property>

	<!--
	<property name="m_strClbPsnRule" id="clbPsnRule">
		<label lsid="CN">协作个性化规则</label>
		<label lsid="EN">Collaborative Psn Rule</label>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入协作个性化规则。</msg>
				<msg lsid="CN">Please input collaborative personalization rule.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strPvtPsnRule" id="pvtPsnRule">
		<label lsid="CN">私有个性化规则</label>
		<label lsid="EN">Private Psn Rule</label>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入私有个性化规则。</msg>
				<msg lsid="CN">Please input private personalization rule.</msg>
			</validator>
		</in>
	</property>
	-->

	<property name="tmpl" wrap="def" id="tmpl">
		<label lsid="CN">模板</label>
		<label lsid="EN">Template</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<select />
		</in>
	</property>

	<property name="m_bSkipToSub" wrap="def" wrapname="skipToSub" id="tosub">
		<label lsid="CN">跳转子页面</label>
		<label lsid="EN">Skip To Sub</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="m_bHide" wrap="def" wrapname="hide" id="hide">
		<label lsid="CN">隐藏</label>
		<label lsid="EN">Hide</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="m_bResetWin" wrap="def" wrapname="resetWin" id="reset">
		<label lsid="CN">重置窗口</label>
		<label lsid="EN">Reset Windows</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="useMap" wrap="def" wrapname="useMap" id="usemap">
		<label lsid="CN">路径转换</label>
		<label lsid="EN">Path Transform</label>

		<in>
			<radio />
		</in>
	</property>

	<group id="list">
		<propref name="m_strPageName" />
		<propref name="type" />
		<propref name="m_strPathName" />
		<propref name="m_strFullPath" />
		<propref name="tmpl" />
		<propref name="m_bSkipToSub" />
		<propref name="m_bHide" />
		<propref name="m_bResetWin" />
		<propref name="useMap" />
	</group>

	<group id="info">
		<propref name="m_strPageName" />
		<propref name="type" />
		<propref name="m_strPathName" />
		<propref name="m_strFullPath" />
		<propref name="m_strAccessRule" />
		<propref name="tmpl" />
		<propref name="m_bSkipToSub" />
		<propref name="m_bHide" />
		<propref name="m_bResetWin" />
		<propref name="useMap" />
	</group>

	<group id="edit">
		<propref name="m_strPageName" />
		<propref name="m_strPathName" />
		<propref name="m_strAccessRule" />
		<propref name="tmpl" />
	</group>

	<group id="edit_check">
		<propref name="m_bSkipToSub" />
		<propref name="m_bHide" />
		<propref name="m_bResetWin" />
		<propref name="useMap" />
	</group>
</adb>
