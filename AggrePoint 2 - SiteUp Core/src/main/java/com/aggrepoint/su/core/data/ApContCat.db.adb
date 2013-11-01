<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_CONT_CATS" timestamp="UPDATETIME">
	<propdef property="m_lContCatID" column="ContCatID" primary="yes" sequence="S_AP_CONT_CATS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_strContCatName" column="ContCatName" />
	<propdef property="m_strContCatDesc" column="ContCatDesc" />
	<propdef property="m_strUUID" column="UUID" />

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrievemulti id="loadBySite">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
	</retrievemulti>
</adb>