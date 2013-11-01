<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lPageContID" id="cid" />

	<property name="type" wrap="def" id="type">
		<label lsid="CN">类型</label>
		<label lsid="EN">Type</label>
	</property>

	<property name="m_iZoneID" id="zoneid">
		<label lsid="CN">区域</label>
		<label lsid="EN">Zone</label>
	</property>

	<property name="inhe" wrap="def" id="inhe">
		<label lsid="CN">继承</label>
		<label lsid="EN">Inheritable</label>

		<in>
			<radio />
		</in>
	</property>

	<group id="list">
		<propref name="type" />
		<propref name="m_iZoneID" />
	</group>

	<group id="edit">
		<propref name="inhe" />
	</group>
</adb>
