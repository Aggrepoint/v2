<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="/site">
	<propdef property="m_strSiteName" path=".name" />
	<propdef property="m_strSiteDesc" path=".desc" />
	<propdef property="m_strManageRule" path=".managerule" />
	<propdef property="m_strPublishBranchDir" path=".publishbranchdir" />
	<propdef property="m_strStaticBranchUrl" path=".staticbranchurl" />
	<propdef property="m_strPublishChannelDir" path=".publishchanneldir" />
	<propdef property="m_strStaticChannelUrl" path=".staticchannelurl" />
	<propdef property="m_strPublishResDir" path=".publishresdir" />
	<propdef property="m_strStaticResUrl" path=".staticresurl" />
	<propdef property="m_strLogoContentType" path=".logotype" />
	<propdef property="m_strLogoName" path=".logoname" />
	<propdef property="m_strLogoFile" path=".logofile" />
	<propdef property="m_strUUID" path=".uuid" />
	<sublist property="branches" type="com.aggrepoint.su.core.data.ApBranch" r="yes" rm="yes"></sublist>
	<sublist property="branchgroups" type="com.aggrepoint.su.core.data.ApBranchGroup" r="yes" rm="yes"></sublist>
	<sublist property="layouts" type="com.aggrepoint.su.core.data.ApLayout" r="yes" rm="yes"></sublist>
	<sublist property="templates" type="com.aggrepoint.su.core.data.ApTemplate" r="yes" rm="yes"></sublist>
	<sublist property="frames" type="com.aggrepoint.su.core.data.ApFrame" r="yes" rm="yes"></sublist>
	<sublist property="apps" type="com.aggrepoint.su.core.data.ApApp" r="yes" rm="yes"></sublist>
	<sublist property="contCats" type="com.aggrepoint.su.core.data.ApContCat" r="yes" rm="yes"></sublist>
	<sublist property="channels" type="com.aggrepoint.su.core.data.ApChannel" r="yes" rm="yes"></sublist>
	<sublist property="dirs" type="com.aggrepoint.su.core.data.ApResDir" r="yes" rm="yes"></sublist>
</adb>