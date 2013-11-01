<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_WIN_PARAMS" timestamp="UPDATETIME">
	<propdef property="m_lWinParamID" column="WinParamID" primary="yes" sequence="S_AP_WIN_PARAMS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lWindowID" column="WindowID" />
	<propdef property="m_strParamName" column="ParamName" />
	<propdef property="paramDesc" column="ParamDesc" />
	<propdef property="m_strDefaultValue" column="DefaultValue" />

	<retrievemulti id="loadByWindow">
		<key>
			<property name="m_lWindowID"/>
			<property name="m_iOfficialFlag"/>
		</key>
	</retrievemulti>

	<delete id="deleteByWindow" ondefault="no">
		<key>
			<property name="m_lWindowID"/>
			<property name="m_iOfficialFlag"/>
		</key>
	</delete>
</adb>
