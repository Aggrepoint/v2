<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="msg">
	<propdef property="m_strName" path=".name" />
	<propdef property="m_strText" path=".text" />
	<propdef property="m_strPsnRule" path=".rule" />
	<sublist property="psnVars" type="com.aggrepoint.adk.data.Message"
		r="yes" rm="yes" rmid="loadPsn">
	</sublist>
	<action name="setBaseProperties" method="setBaseProperties" />

	<!-- 用于加载Module级别的文本信息 -->
	<retrievemulti id="loadByModule" path="/msg"></retrievemulti>

	<!-- 用于加载个性化的参数 -->
	<retrievemulti id="loadPsn" path="psn_msg">
		<get>
			<property name="m_strName" mode="minus" />
			<property name="psnVars" mode="minus" />
		</get>
		<trigger action="setBaseProperties" event="before" />
	</retrievemulti>
</adb>