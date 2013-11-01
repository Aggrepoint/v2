<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_FRAMES" timestamp="UPDATETIME" synctimeout="10">
	<propdef property="m_lFrameID" column="FrameID" primary="yes"
		sequence="S_AP_FRAMES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lDefResDirID" column="DefResDirID" />
	<propdef property="m_lResDirID" column="ResDirID" null="0@BIGINT" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_strContNormal" column="ContNormal" r="no"
		rm="no" />
	<propdef property="m_strContMax" column="ContMax" r="no" rm="no" />
	<propdef property="m_strContMin" column="ContMin" r="no" rm="no" />
	<propdef property="m_strFrameName" column="FrameName" />
	<propdef property="m_lPreviewImgID" column="PreviewImgID" null="0@BIGINT" />
	<propdef property="m_strUUID" column="UUID" />

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrieve id="loadResDirWithCache" cache="200" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lFrameID" />
		</key>
		<get>
			<property name="m_lDefResDirID" />
			<property name="m_lResDirID" />
		</get>
	</retrieve>

	<retrieve id="loadDetail">
		<get>
			<property name="m_strContNormal" />
			<property name="m_strContMax" />
			<property name="m_strContMin" />
		</get>
	</retrieve>

	<retrieve id="loadUUIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_lFrameID" />
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
			<property name="m_lFrameID" />
			<property name="m_iOfficialFlag" />
		</get>
	</retrieve>

	<retrievemulti id="loadBySite">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_IMAGES where imageid=?</stmt>
			<key>
				<property name="m_lPreviewImgID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_FRAMES where frameid = ? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lFrameID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_RES_DIRS where ResDirID=? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lDefResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>