<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="dir">
	<propdef property="m_strDirName" path=".name" />

	<sublist property="reses" type="ApRes" rm="yes" r="yes" />
	<sublist property="childDirs" type="ApResDir" rm="yes" r="yes" />

	<retrieve id="loadCurrent" path="!" />
</adb>
