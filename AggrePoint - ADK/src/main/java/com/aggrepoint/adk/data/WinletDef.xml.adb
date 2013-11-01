<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="win">
	<propdef property="m_strPath" path=".path" />
	<propdef property="m_strModuleClass" path=".module" />
	<propdef property="m_strAccessRule" path=".access_rule" />
	<propdef property="m_strPsnEngineID" path=".psn_engine" />
	<propdef property="m_strUserEngineID" path=".user_engine" />
	<propdef property="scope" path=".scope" />
	<propdef property="m_strView" path=".view" />
	<sublist property="m_vecRetCodes"
		type="com.aggrepoint.adk.data.RetCode" />
	<sublist property="m_vecParams" type="com.aggrepoint.adk.data.Param" />
	<sublist property="m_vecMsgs" type="com.aggrepoint.adk.data.Message" />
	<sublist property="m_vecEventHandlers"
		type="com.aggrepoint.adk.data.EventHandler" rmid="loadAll" keepexisting="yes">
	</sublist>
	<sublist property="m_vecViews"
		type="com.aggrepoint.adk.data.WinletViewDef" rmid="loadAll">
	</sublist>
	<action name="beforeSub" method="beforeSub" />
	<action name="afterLoaded" method="afterLoaded" />

	<retrieve id="loadSeperate" path="/">
		<get>
			<property name="m_vecRetCodes" />
			<property name="m_vecParams" />
			<property name="m_vecMsgs" />
			<property name="m_vecEventHandlers" />
			<property name="m_vecViews" />
		</get>
		<trigger action="beforeSub" event="before sub" />
		<trigger action="afterLoaded" event="after" />
	</retrieve>

	<retrievemulti id="loadAll">
		<get>
			<property name="m_vecRetCodes" />
			<property name="m_vecParams" />
			<property name="m_vecMsgs" />
			<property name="m_vecEventHandlers" />
			<property name="m_vecViews" />
		</get>
		<trigger action="beforeSub" event="before sub" />
		<trigger action="afterLoaded" event="after" />
	</retrievemulti>
</adb>