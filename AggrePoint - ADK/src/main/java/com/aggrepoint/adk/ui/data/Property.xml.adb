<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="property">
	<propdef property="m_strName" path=".name" />
	<propdef property="m_strWrapper" path=".wrap" />
	<propdef property="m_strWrapName" path=".wrapname" />
	<propdef property="m_strGet" path=".get" />
	<propdef property="m_strSet" path=".set" />
	<propdef property="m_strId" path=".id" />
	<propdef property="m_strLSID" path=".lsid" />
	<propdef property="m_strMarkup" path=".markup" />
	<propdef property="flag" path=".flag" />
	<propdef property="m_strSortAsc" path=".sortasc" />
	<propdef property="m_strSortDesc" path=".sortdesc" />
	<propdef property="mandatory" path=".mandatory" />
	<propdef property="m_strAjaxValidate" path=".ajaxvalidate" />
	<sublist property="m_vecLabels" type="com.aggrepoint.adk.ui.data.Label"
		r="yes" rm="yes" />
	<sublist property="m_vecTitles" type="com.aggrepoint.adk.ui.data.Title"
		r="yes" rm="yes" />
	<sublist property="m_vecRemarks"
		type="com.aggrepoint.adk.ui.data.Remarks" r="yes" rm="yes" />
	<sublist property="m_vecHelps" type="com.aggrepoint.adk.ui.data.Help"
		r="yes" rm="yes" />
	<sublist property="m_vecDecoractors"
		type="com.aggrepoint.adk.ui.data.Decoractor" r="yes" rm="yes" />
	<sublist property="m_vecDisplays"
		type="com.aggrepoint.adk.ui.data.DisplayAttribute" r="yes" rm="yes" />
	<sublist property="m_vecEdits"
		type="com.aggrepoint.adk.ui.data.EditFactory" r="yes" rm="yes" />
	<sublist property="m_vecValidators"
		type="com.aggrepoint.adk.ui.data.Validator" r="yes" rm="yes" />
	<action name="afterLoaded" method="afterLoaded" />

	<retrieve id="">
		<trigger action="afterLoaded" event="after" />
	</retrieve>

	<retrievemulti id="">
		<trigger action="afterLoaded" event="after" />
	</retrievemulti>
</adb>
