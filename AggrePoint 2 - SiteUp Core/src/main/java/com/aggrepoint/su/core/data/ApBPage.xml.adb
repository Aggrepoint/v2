<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="page">
	<propdef property="m_iPageType" path=".type" />
	<propdef property="m_strPageName" path=".name" />
	<propdef property="m_strPathName" path=".path" />
	<propdef property="m_strAccessRule" path=".accessrule" />
	<propdef property="m_strClbPsnRule" path=".clbpsnrule" />
	<propdef property="m_strPvtPsnRule" path=".pvtpsnrule" />
	<propdef property="m_bInheritTmpl" path=".inherittmpl" />
	<propdef property="m_strTemplateUUID" path=".templateid" />
	<propdef property="m_strTmplParams" path=".tmplparam" />
	<propdef property="m_iOpenMode" path=".openmode" />
	<propdef property="m_iOrder" path=".order" />
	<propdef property="m_strOwnerID" path=".owner" />
	<propdef property="skip" path=".skip" />
	<propdef property="m_bHide" path=".hide" />
	<propdef property="m_bResetWin" path=".resetwin" />
	<propdef property="m_strUUID" path=".uuid" />
	<sublist property="layouts" type="com.aggrepoint.su.core.data.ApBPageLayout" r="yes" rm="yes"></sublist>
	<sublist property="subpages" type="com.aggrepoint.su.core.data.ApBPage" r="yes" rm="yes"></sublist>
	<sublist property="psnnames" type="com.aggrepoint.su.core.data.ApBPagePsnName" r="yes" rm="yes"></sublist>
	<sublist property="psntmpls" type="com.aggrepoint.su.core.data.ApBPagePsnTmpl" r="yes" rm="yes"></sublist>
	<sublist property="contents" type="com.aggrepoint.su.core.data.ApBPageContent" r="yes" rm="yes"></sublist>
</adb>