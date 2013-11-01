<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_USER_AGENTS" timestamp="UPDATETIME">
	<propdef property="agentID" primary="yes" sequence="S_AP_USER_AGENTS" />
	<propdef property="userAgent" />
	<propdef property="uua" />
	<propdef property="accept" />
	<propdef property="deviceTypeID" />
	<propdef property="deviceModel" />
	<propdef property="deviceVersion" />
	<propdef property="deviceMajorVersion" />
	<propdef property="deviceMinorVersion" />
	<propdef property="osTypeID" />
	<propdef property="osVersion" />
	<propdef property="osMajorVersion" />
	<propdef property="osMinorVersion" />
	<propdef property="browserTypeID" />
	<propdef property="browserVersion" />
	<propdef property="browserMajorVersion" />
	<propdef property="browserMinorVersion" />
	<propdef property="supportHTML" />
	<propdef property="supportXHTML" />
	<propdef property="supportWML" />
	<propdef property="xhtmlType" />
	<propdef property="defaultMarkup" />
	<propdef property="supportAjax" />
	<propdef property="isSpider" />
	<propdef property="isMobile" />
	<propdef property="autoFlag" />

	<retrieve id="loadByUA">
		<key>
			<property name="agentID" mode="minus" />
			<property name="userAgent" />
			<property name="accept" />
		</key>
		<get>
			<property name="agentID" />
		</get>
	</retrieve>

	<retrievemulti id="search">
		<key>
			<property name="uua" match="LIKE" flag="useragent" />
			<property name="autoFlag" flag="autoflag" />
		</key>
		<order id="default" value="USERAGENT ASC" />
	</retrievemulti>
</adb>
