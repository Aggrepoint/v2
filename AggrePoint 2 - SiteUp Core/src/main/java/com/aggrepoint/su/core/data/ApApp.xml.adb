<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="app">
	<propdef property="m_strAppName" path=".name" />
	<propdef property="m_strHostURL" path=".hosturl" />
	<propdef property="m_strRootPath" path=".rootpath" />
	<propdef property="m_strAppDesc" path=".desc" />
	<propdef property="m_strAppVer" path=".ver" />
	<propdef property="m_iStatusID" path=".status" />
	<propdef property="m_strLogoContentType" path=".logotype" />
	<propdef property="m_strLogoName" path=".logoname" />
	<propdef property="m_strLogoFile" path=".logofile" />
	<propdef property="m_iConnTimeout" path=".conntimeout" />
	<propdef property="m_iReadTimeout" path=".readtimeout" />
	<propdef property="m_strUUID" path=".uuid" />
	<subbean property="dir" r="yes" rm="yes" rid="loadCurrent" />
	<sublist property="windows" type="com.aggrepoint.su.core.data.ApWindow" r="yes" rm="yes"></sublist>
</adb>