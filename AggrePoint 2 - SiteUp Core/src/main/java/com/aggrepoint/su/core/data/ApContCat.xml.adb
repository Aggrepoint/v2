<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="contcat">
	<propdef property="m_strContCatName" path=".name" />
	<propdef property="m_strContCatDesc" path=".desc" />
	<propdef property="m_strUUID" path=".uuid" />
	<sublist property="contents" type="com.aggrepoint.su.core.data.ApContent" r="yes" rm="yes"></sublist>
</adb>