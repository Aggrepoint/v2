<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="TDAEMON_PARAMS" timestamp="UPDATETIME">
	<propdef property="m_lID" column="DAEMONID" primary="yes" />
	<propdef property="m_strName" column="ParamName" primary="yes" />
	<propdef property="m_iCanChangeDyna" column="CanChangeDyna" />
	<propdef property="m_strValue" column="DefaultValue" />
	<propdef property="m_strDescription" column="ParamDesc" />

	<retrievemulti id="1">
		<key>
			<property name="m_lID" />
		</key>
	</retrievemulti>

	<delete id="1" ondefault="no">
		<key>
			<property name="m_lID" />
		</key>
	</delete>
</adb>