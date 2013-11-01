<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="param">
	<propdef property="m_strName" path=".name" />
	<propdef property="m_strValue" path=".value" />
	<propdef property="m_strPsnRule" path=".rule" />
	<sublist property="psnVars" type="com.aggrepoint.adk.data.Param"
		r="yes" rm="yes" rmid="loadPsn">
	</sublist>
	<action name="setBaseProperties" method="setBaseProperties" />

	<!-- 用于加载Module级别的参数 -->
	<retrievemulti id="loadByModule" path="/param"></retrievemulti>

	<!-- 用于加载个性化的参数 -->
	<retrievemulti id="loadPsn" path="psn_param">
		<get>
			<property name="m_strName" mode="minus" />
			<property name="psnVars" mode="minus" />
		</get>
		<trigger action="setBaseProperties" event="before" />
	</retrievemulti>
</adb>
