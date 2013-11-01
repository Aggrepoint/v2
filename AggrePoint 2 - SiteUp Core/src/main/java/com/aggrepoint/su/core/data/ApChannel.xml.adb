<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="channel">
	<propdef property="m_strChannelName" path=".name" />
	<propdef property="m_strChannelDesc" path=".desc" />
	<propdef property="m_strTemplateUUID" path=".templateid" />
	<propdef property="m_strTmplParams" path=".tmplparams" />
	<propdef property="m_strEditRule" path=".editrule" />
	<propdef property="m_strPubRule" path=".pubrule" />
	<propdef property="m_strAccessRule" path=".accessrule" />
	<propdef property="m_strPath" path=".path" />
	<propdef property="m_strUUID" path=".uuid" />
	<sublist property="pages" type="com.aggrepoint.su.core.data.ApCPage" r="yes" rm="yes" />
</adb>