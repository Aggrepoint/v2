<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_LAYOUTS" timestamp="UPDATETIME" synctimeout="10">
	<propdef property="m_lLayoutID" column="LAYOUTID" primary="yes"
		sequence="S_AP_LAYOUTS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lPreviewImgID" column="PreviewImgID" null="0@BIGINT" />
	<propdef property="m_strLayoutName" column="LayoutName" />
	<propdef property="m_strContent" column="Content" />
	<propdef property="m_iZoneCount" column="ZoneCount" />
	<propdef property="m_strUUID" column="UUID" />

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
		<order id="default" value="layoutid" />
	</retrieve>

	<retrieve id="loadUUIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_lLayoutID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_strUUID" />
			<property name="m_lSiteID" />
		</get>
	</retrieve>

	<retrieve id="loadIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_strUUID" />
		</key>
		<get>
			<property name="m_lLayoutID" />
		</get>
	</retrieve>

	<retrievemulti id="loadAll">
		<key>
			<property name="m_iOfficialFlag" />
		</key>
		<order id="default" value="layoutid" />
	</retrievemulti>

	<retrievemulti id="loadBySite">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
		<order id="default" value="layoutid" />
	</retrievemulti>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_IMAGES where imageid=?</stmt>
			<key>
				<property name="m_lPreviewImgID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_LAYOUTS where layoutid = ? and officialflag = ?</stmt>
			<key>
				<property name="m_lLayoutID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>