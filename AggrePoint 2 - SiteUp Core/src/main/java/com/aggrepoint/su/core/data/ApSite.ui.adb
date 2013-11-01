<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lSiteID" id="sid" />

	<property name="m_strSiteName" id="name" mandatory="yes">
		<label lsid="CN">站点名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入站点名。</msg>
				<msg lsid="CN">Please input site name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strSiteDesc" id="desc">
		<label lsid="CN">介绍</label>
		<label lsid="EN">Description</label>

		<out>
			<deco type="tohtml" />
			<wrap>yes</wrap>
			<align>left</align>
		</out>

		<in>
			<textarea rows="4" cols="80" />

			<validator id="maxlen">
				<arg>500</arg>

				<msg lsid="CN">介绍不能超过500个字符。</msg>
				<msg lsid="EN">
					Description can't contain more than 500 characters.
				</msg>
			</validator>
		</in>
	</property>

	<property name="m_strManageRule" id="rule" mandatory="yes">
		<label lsid="CN">管理规则</label>
		<label lsid="EN">Manage Rule</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="4" cols="80" markup="html" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>

			<validator id="maxlen">
				<arg>500</arg>

				<msg lsid="CN">管理规则不能超过500个字符。</msg>
				<msg lsid="EN">
					Manage rule can't contain more than 500 characters.
				</msg>
			</validator>
		</in>
	</property>

	<property name="m_logoFile" wrap="def" wrapname="logoFile" id="logo" ajaxvalidate="no">
		<label lsid="CN">站点Logo</label>
		<label lsid="EN">Site Logo</label>

		<remarks lsid="CN">可选。Logo文件必须是JPG或GIF格式，大小不能超过500K</remarks>
		<remarks lsid="EN">This is optional. The logo file must be a JPG or
			GIF file less than 500K</remarks>

		<out>
			<wrap>no</wrap>
			<width>5%</width>
		</out>

		<in>
			<file width="30" maxsize="" />
			<validator method="checkLogoFile" />
		</in>
	</property>

	<property name="publishBranchDir" id="pbd" ajaxvalidate="no">
		<label lsid="CN">站点页面发布根目录</label>
		<label lsid="EN">Site Pages Publish Root</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<property name="staticBranchUrl" id="sbu" ajaxvalidate="no">
		<label lsid="CN">静态站点页面根URL</label>
		<label lsid="EN">Root URL of Published Pages</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<property name="publishResDir" id="prd" ajaxvalidate="no">
		<label lsid="CN">站点资源发布根目录</label>
		<label lsid="EN">Resources Publish Root</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<property name="staticResUrl" id="sru" ajaxvalidate="no">
		<label lsid="CN">静态资源根URL</label>
		<label lsid="EN">Root URL of Published Resources</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<property name="publishChannelDir" id="pcd" ajaxvalidate="no">
		<label lsid="CN">站点栏目发布根目录</label>
		<label lsid="EN">Site Channels Publish Root</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<property name="staticChannelUrl" id="scu" ajaxvalidate="no">
		<label lsid="CN">静态站点页面根URL</label>
		<label lsid="EN">Root URL of Published Channels</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />
		</in>
	</property>

	<group id="list">
		<propref name="m_strSiteName" />
		<propref name="m_strSiteDesc" />
		<propref name="m_logoFile" />
	</group>

	<group id="edit">
		<propref name="m_strSiteName" />
		<propref name="m_strSiteDesc" />
		<propref name="m_strManageRule" />
		<propref name="m_logoFile" />
		<propref name="publishBranchDir" />
		<propref name="staticBranchUrl" />
		<propref name="publishResDir" />
		<propref name="staticResUrl" />
		<propref name="publishChannelDir" />
		<propref name="staticChannelUrl" />
	</group>

	<group id="info">
		<propref name="m_strSiteName" />
		<propref name="m_strSiteDesc" />
		<propref name="m_strManageRule" />
		<propref name="m_logoFile" />
		<propref name="publishBranchDir" />
		<propref name="staticBranchUrl" />
		<propref name="publishResDir" />
		<propref name="staticResUrl" />
		<propref name="publishChannelDir" />
		<propref name="staticChannelUrl" />
	</group>
</adb>
