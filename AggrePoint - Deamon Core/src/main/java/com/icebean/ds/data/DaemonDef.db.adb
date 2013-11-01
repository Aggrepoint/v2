<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="TDAEMONS" timestamp="UPDATETIME">
	<propdef property="m_lID" column="DAEMONID" primary="yes" sequence="TSYS_DAEMON" />
	<propdef property="m_strName" column="DaemonName" />
	<propdef property="m_strClassName" column="ClassName" />
	<propdef property="m_iAutoStart" column="IsAutoStart" />
	<propdef property="m_iMultiInstance" column="IsMultiInst" />
	<propdef property="m_lCheckHeartBeatInterval" column="CheckInterval" />
	<propdef property="m_lPerformInterval" column="PerformInterval" />
	<propdef property="m_strDescription" column="DaemonDesc" />
	<propdef property="m_strServerName" column="ServerName" />
	<propdef property="m_iMemLogSize" column="MemLogSize" />
	<propdef property="m_iExpLogSize" column="ExpLogSize" />
	<sublist property="m_vecParams" type="DaemonParamDef" rmid="1" r="yes" rm="yes">
		<propref this="m_lID" ref="m_lID" />
	</sublist>

	<retrievemulti id="1">
		<key>
			<property name="m_iAutoStart" />
		</key>
	</retrievemulti>

	<retrievemulti id="2">
		<order id="1" value="DaemonName" />
	</retrievemulti>
</adb>