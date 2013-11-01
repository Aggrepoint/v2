<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="retcode">
	<propdef property="id" path=".id" />
	<propdef property="m_strThrow" path=".throw" />
	<propdef property="level" path=".level" />
	<propdef property="logMessage" path=".lmsg" />
	<propdef property="userMessage" path=".umsg" />
	<propdef property="method" path=".method" />
	<propdef property="url" path=".url" />
	<propdef property="state" path=".state" />
	<propdef property="m_strPsnRule" path=".rule" />
	<propdef property="cache" path=".cache" />
	<propdef property="dialog" path=".dialog" />
	<propdef property="logger" path=".logger" />
	<propdef property="winmode" path=".winmode" />
	<propdef property="wintitle" path=".title" />
	<propdef property="update" path=".update" />
	<propdef property="ensureVisible" path=".ensurevisible" />
	<propdef property="m_docSelf" path="" />
	<sublist property="psnVars" type="com.aggrepoint.adk.data.RetCode"
		r="yes" rm="yes" rmid="loadPsn">
	</sublist>
	<action name="setBaseProperties" method="setBaseProperties" />

	<!-- 用于加载Module级别的响应码 -->
	<retrievemulti id="loadRoot" path="/retcode"></retrievemulti>

	<!-- 用于加载个性化的响应码 -->
	<retrievemulti id="loadPsn" path="psn_retcode">
		<get>
			<property name="id" mode="minus" />
			<property name="psnVars" mode="minus" />
		</get>
		<trigger action="setBaseProperties" event="before" />
	</retrievemulti>
</adb>