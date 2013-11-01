<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="page">
	<propdef property="m_lPageID" path=".id" />
	<propdef property="m_iDocTypeID" path=".type" />
	<propdef property="m_iDocStatusID" path=".status" />
	<propdef property="m_strTemplateUUID" path=".templateid" />
	<propdef property="m_sInheritTmpl" path=".inherittmpl" />
	<propdef property="m_strTitle" path=".title" />
	<propdef property="m_strSubTitle" path=".subtitle" />
	<propdef property="m_iOrder" path=".order" />
	<propdef property="m_sOpenMode" path=".openmode" />
	<propdef property="m_strFileName" path=".filename" />
	<propdef property="m_strFileContType" path=".filetype" />
	<propdef property="m_strAccessRule" path=".accessrule" />
	<propdef property="m_strEditor" path=".editor" />
	<propdef property="m_strApprover" path=".approver" />
	<propdef property="m_strSource" path=".source" />
	<propdef property="m_strPath" path=".path" />
	<propdef property="m_strContent1" path=".createdate" />
	<propdef property="m_strUUID" path=".uuid" />

	<sublist property="reses" type="com.aggrepoint.su.core.data.ApCPageRes" r="yes" rm="yes"/>
</adb>