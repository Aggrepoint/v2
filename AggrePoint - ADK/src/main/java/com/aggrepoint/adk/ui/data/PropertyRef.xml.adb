<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="propref">
	<propdef property="m_strName" path=".name" />
	<propdef property="m_strLSID" path=".lsid" />
	<propdef property="m_strMarkup" path=".markup" />
	<propdef property="m_strSortAsc" path=".sortasc" />
	<propdef property="m_strSortDesc" path=".sortdesc" />
	<propdef property="m_strAjaxValidate" path=".ajaxvalidate" />
	<propdef property="flag" path=".flag" />
	<sublist property="m_vecLabels"
		type="com.aggrepoint.adk.ui.data.Label" r="yes" rm="yes" />
	<sublist property="m_vecRemarks"
		type="com.aggrepoint.adk.ui.data.Remarks" r="yes" rm="yes" />
	<sublist property="m_vecHelps"
		type="com.aggrepoint.adk.ui.data.Help" r="yes" rm="yes" />
	<sublist property="m_vecEdits"
		type="com.aggrepoint.adk.ui.data.EditFactory" r="yes" rm="yes" />
	<sublist property="m_vecValidators"
		type="com.aggrepoint.adk.ui.data.Validator" r="yes" rm="yes" />
</adb>