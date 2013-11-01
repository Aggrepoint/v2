<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="/user">
	<propdef property="m_strID" path=".id" />
	<propdef property="m_strPassword" path=".password" />
	<sublist property="m_vecProperties" type="com.aggrepoint.su.user.UserProperty"></sublist>

	<!-- 在子路径上调用：连带加载所有 -->
	<retrievemulti id="loadAll">
		<get>
			<property name="m_vecProperties" />
		</get>
	</retrievemulti>
</adb>