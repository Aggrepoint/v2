<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="/">
	<sublist property="m_vecUIWrappers"
		type="com.aggrepoint.adk.ui.data.UIWrapper" r="yes" rm="yes" />
	<sublist property="m_vecProperties"
		type="com.aggrepoint.adk.ui.data.Property" r="yes" rm="yes" />
	<sublist property="m_vecGroups"
		type="com.aggrepoint.adk.ui.data.Group" r="yes" rm="yes" />
	<sublist property="m_vecTemplates"
		type="com.aggrepoint.adk.ui.data.Template" r="yes" rm="yes" />
	<action name="afterLoaded" method="afterLoaded" />

	<retrieve id="">
		<trigger action="afterLoaded" event="after" />
	</retrieve>
</adb>
