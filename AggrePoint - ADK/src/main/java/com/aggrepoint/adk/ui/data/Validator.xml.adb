<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="in/validator">
	<propdef property="m_strLSID" path=".lsid" />
	<propdef property="m_strMarkup" path=".markup" />
	<propdef property="flag" path=".flag" />
	<propdef property="m_strId" path=".id" />
	<propdef property="m_strMethod" path=".method" />
	<propdef property="m_strSkip" path=".skip" />
	<sublist property="m_vecArgs"
		type="com.aggrepoint.adk.ui.data.ValidatorArg" r="yes" rm="yes" />
	<sublist property="m_vecMsgs"
		type="com.aggrepoint.adk.ui.data.ValidatorMsg" r="yes" rm="yes" />
</adb>
