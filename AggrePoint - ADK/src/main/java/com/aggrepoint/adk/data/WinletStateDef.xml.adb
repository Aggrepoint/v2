<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="state">
	<propdef property="m_strPath" path=".id" />
	<propdef property="default" path=".default" />
	<propdef property="m_strMethod" path=".method" />
	<propdef property="status" path=".status" />
	<propdef property="autoLoad" path=".load" />
	<propdef property="m_strPsnEngineID" path=".psn_engine" />
	<propdef property="m_strUserEngineID" path=".user_engine" />
	<propdef property="m_strAccessRule" path=".access_rule" />
	<sublist property="m_vecRetCodes"
		type="com.aggrepoint.adk.data.RetCode">
	</sublist>
	<sublist property="m_vecParams" type="com.aggrepoint.adk.data.Param"></sublist>
	<sublist property="m_vecMsgs" type="com.aggrepoint.adk.data.Message"></sublist>
	<sublist property="m_vecLogParams"
		type="com.aggrepoint.adk.data.LogParam">
	</sublist>
	<sublist property="m_vecEventHandlers"
		type="com.aggrepoint.adk.data.EventHandler" rmid="loadAll"
		keepexisting="yes" />
	<action name="beforeSub" method="beforeSub" />

	<retrievemulti id="loadAll">
		<get>
			<property name="m_vecRetCodes" />
			<property name="m_vecParams" />
			<property name="m_vecMsgs" />
			<property name="m_vecLogParams" />
			<property name="m_vecEventHandlers" />
		</get>
		<trigger action="beforeSub" event="before sub" />
	</retrievemulti>
</adb>