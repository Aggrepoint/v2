<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="/retcode_level/level">
	<propdef property="m_strID" path=".id" />
	<propdef property="m_strDesc" path=".desc" />
	<propdef property="commitDB" path=".db" />
	<propdef property="default" path=".default" />
	<sublist property="loggers"
		type="com.aggrepoint.adk.data.RetCodeLevelLoggerDef" r="yes" rm="yes" />
</adb>