<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lBranchID" id="bid" />

	<property name="m_strBranchName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<remarks lsid="CN" markup="html">分支名称。</remarks>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入分支名。</msg>
				<msg lsid="CN">Please input branch name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strBranchDesc" id="desc">
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

	<property name="type" wrap="def" id="type">
		<label lsid="CN">类型</label>
		<label lsid="EN">Type</label>
	</property>

	<property name="m_iPsnType" wrapname="type" wrap="def" id="type">
		<label lsid="CN">类型</label>
		<label lsid="EN">Type</label>

		<in>
			<radio />
		</in>
	</property>

	<property name="m_iMarkupType" wrapname="markup" wrap="def" id="markup">
		<label lsid="CN">标记</label>
		<label lsid="EN">Markup</label>

		<in>
			<radio />
		</in>
	</property>

	<property name="m_strRootPath" id="rootPath">
		<label lsid="CN">根栏目路径</label>
		<label lsid="EN">Root Path</label>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入根栏目路径。</msg>
				<msg lsid="CN">Please input root path.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strAccessRule" id="accessRule">
		<label lsid="CN">访问规则</label>
		<label lsid="EN">Access Rule</label>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入访问规则。</msg>
				<msg lsid="CN">Please input access rule.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strManageRule" id="manageRule">
		<label lsid="CN">管理规则</label>
		<label lsid="EN">Manage Rule</label>

		<in>
			<text width="100" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入管理规则。</msg>
				<msg lsid="CN">Please input manage rule.</msg>
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

	<property name="m_strHomePath" id="homePath">
		<label lsid="CN">主页路径</label>
		<label lsid="EN">Home Path</label>

		<in>
			<text width="80" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入主页路径。</msg>
				<msg lsid="CN">Please input home path.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strLoginPath" id="loginPath">
		<label lsid="CN">登录页面路径</label>
		<label lsid="EN">Login Path</label>

		<in>
			<text width="80" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入登录页面路径。</msg>
				<msg lsid="CN">Please input login path.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strNoAccessPath" id="noAccessPath">
		<label lsid="CN">无权访问页路径</label>
		<label lsid="EN">No Access Path</label>

		<in>
			<text width="80" maxlength="255" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入无权访问页路径。</msg>
				<msg lsid="CN">Please input no access path.</msg>
			</validator>
		</in>
	</property>

	<property name="m_lAdminTmplID" wrapname="adminTmpl" wrap="def" id="adminTmpl">
		<label lsid="CN">管理模板</label>
		<label lsid="EN">Admin Template</label>

		<in>
			<select />
		</in>
	</property>

	<property name="defTmpl" wrap="def" id="defTmpl">
		<label lsid="CN">缺省模板</label>
		<label lsid="EN">Default Template</label>

		<in>
			<select />
		</in>
	</property>

	<group id="list">
		<propref name="m_strBranchName" />
		<propref name="m_iPsnType" />
		<propref name="m_strRootPath" />
		<propref name="m_strHomePath" />
		<propref name="m_strLoginPath" />
		<propref name="m_strNoAccessPath" />
	</group>

	<group id="typesel">
		<propref name="m_iPsnType" />
		<propref name="m_iMarkupType" />
	</group>

	<group id="edit">
		<propref name="type" />
		<propref name="m_strBranchName" />
		<propref name="m_strBranchDesc" />
		<propref name="m_strRootPath" />
		<propref name="m_strAccessRule" />
		<propref name="m_strManageRule" />
		<!-- 
		<propref name="m_strClbPsnRule" />
		<propref name="m_strPvtPsnRule" />
		-->
		<propref name="m_strHomePath" />
		<propref name="m_strLoginPath" />
		<propref name="m_strNoAccessPath" />
		<propref name="m_lAdminTmplID" />
		<propref name="defTmpl" />
	</group>

	<group id="groupedit">
		<propref name="type" />
		<propref name="m_strBranchName" />
		<propref name="m_strBranchDesc" />
		<propref name="m_strRootPath" />
		<propref name="m_strAccessRule" />
		<propref name="m_strManageRule" />
		<!-- 
		<propref name="m_strClbPsnRule" />
		<propref name="m_strPvtPsnRule" />
		-->
		<propref name="m_iMarkupType">
			<label lsid="CN">分支选择结果</label>
			<label lsid="EN">Section selection result</label>
		</propref>
	</group>
</adb>
