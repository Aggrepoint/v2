<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_strLoginId" id="uid">
		<label lsid="CN">登录帐号</label>
		<label lsid="EN">Login Id</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="m_strUserName" id="name">
		<label lsid="CN">用户姓名</label>
		<label lsid="EN">User Name</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="rights" wrap="def" id="rights">
		<label lsid="CN">权限</label>
		<label lsid="EN">Rights</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="sites" wrap="def" id="sites">
		<label lsid="CN">站点</label>
		<label lsid="EN">Sites</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<group id="list">
		<propref name="m_strLoginId" />
		<propref name="m_strUserName" />
		<propref name="rights" />
		<propref name="sites" />
	</group>
</adb>
