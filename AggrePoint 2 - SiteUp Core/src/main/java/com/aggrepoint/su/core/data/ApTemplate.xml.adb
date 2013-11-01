<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="template">
	<propdef property="m_lImportID" path=".id" />
	<propdef property="m_strTmplName" path=".name" />
	<propdef property="m_iTmplType" path=".type" />
	<propdef property="m_iOrder" path=".order" />
	<propdef property="m_strLogoContentType" path=".logotype" />
	<propdef property="m_strLogoName" path=".logoname" />
	<propdef property="m_strLogoFile" path=".logofile" />
	<propdef property="m_iMarkupType" path=".markup" />
	<propdef property="m_strUUID" path=".uuid" />
	<propdef property="m_strAreas" path=".areas" />
	<subbean property="dir" r="yes" rm="yes" rid="loadCurrent" />

	<retrieve id="loadSeperate" path="/" />
</adb>