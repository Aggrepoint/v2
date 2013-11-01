<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_xml.dtd">

<adb path="ehandler">
	<propdef property="m_strFor" path=".for" />
	<propdef property="m_strMethod" path=".method" />
	<action name="afterLoaded" method="afterLoaded" />

	<retrievemulti id="loadAll">
		<trigger action="afterLoaded" event="after" />
	</retrievemulti>
</adb>
