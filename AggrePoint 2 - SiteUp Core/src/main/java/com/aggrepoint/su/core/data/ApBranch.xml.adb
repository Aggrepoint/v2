<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="branch">
	<propdef property="m_strBranchName" path=".name" />
	<propdef property="m_strBranchDesc" path=".desc" />
	<propdef property="m_iPsnType" path=".psntype" />
	<propdef property="m_strAccessRule" path=".accessrule" />
	<propdef property="m_strManageRule" path=".managerule" />
	<propdef property="m_strClbPsnRule" path=".clbpsnrule" />
	<propdef property="m_strPvtPsnRule" path=".pvtpsnrule" />
	<propdef property="m_strRootPath" path=".rootpath" />
	<propdef property="m_strHomePath" path=".homepath" />
	<propdef property="m_strLoginPath" path=".loginpath" />
	<propdef property="m_strNoAccessPath" path=".noaccesspath" />
	<propdef property="m_strUUID" path=".uuid" />
	<sublist property="pages" type="com.aggrepoint.su.core.data.ApBPage" r="yes" rm="yes"></sublist>
	<sublist property="maps" type="com.aggrepoint.su.core.data.ApPathMap" r="yes" rm="yes"></sublist>
</adb>