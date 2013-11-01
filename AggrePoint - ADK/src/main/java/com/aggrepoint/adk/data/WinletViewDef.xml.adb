<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="view">
	<propdef property="m_strPath" path=".id" />
	<propdef property="m_strAccessRule" path=".access_rule" />
	<propdef property="m_strPsnEngineID" path=".psn_engine" />
	<propdef property="m_strUserEngineID" path=".user_engine" />
	<propdef property="m_docRetData" path="retdata" />
	<sublist property="m_vecRetCodes"
		type="com.aggrepoint.adk.data.RetCode" />
	<sublist property="m_vecParams" type="com.aggrepoint.adk.data.Param" />
	<sublist property="m_vecMsgs" type="com.aggrepoint.adk.data.Message" />
	<sublist property="m_vecStates"
		type="com.aggrepoint.adk.data.WinletStateDef" r="yes" rm="yes"
		rmid="loadAll">
	</sublist>
	<sublist property="m_vecActions"
		type="com.aggrepoint.adk.data.WinletActionDef" r="yes" rm="yes"
		rmid="loadAll">
	</sublist>
	<sublist property="m_vecEventHandlers"
		type="com.aggrepoint.adk.data.EventHandler" rmid="loadAll"
		keepexisting="yes" />
	<action name="beforeSub" method="beforeSub" />
	<action name="afterLoaded" method="afterLoaded" />

	<retrievemulti id="loadAll">
		<get>
			<property name="m_vecRetCodes" />
			<property name="m_vecParams" />
			<property name="m_vecMsgs" />
			<property name="m_vecEventHandlers" />
			<property name="m_vecStates" />
			<property name="m_vecActions" />
		</get>
		<trigger action="beforeSub" event="before sub" />
		<trigger action="afterLoaded" event="after" />
	</retrievemulti>
</adb>