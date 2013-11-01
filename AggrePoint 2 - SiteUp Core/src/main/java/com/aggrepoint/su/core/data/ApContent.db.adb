<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_CONTENTS" timestamp="UPDATETIME" synctimeout="10">
	<propdef property="m_lContentID" column="ContentID" primary="yes"
		sequence="S_AP_CONTENTS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lContCatID" column="ContCatID" null="0@BIGINT" />
	<propdef property="m_lPageID" column="PageID" null="0@BIGINT" />
	<propdef property="name" column="ContName" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_strContent" column="Content" clob="yes" />
	<propdef property="desc" column="ContDesc" />
	<propdef property="m_bIsPkg" column="IsPkg" />
	<propdef property="pkgName" column="PkgName" />
	<propdef property="pkgContType" column="PkgContType" />
	<propdef property="m_strPackage" column="Package" blob="yes" r="no"
		rm="no" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_lResDirID" column="ResDirID" />
	<propdef property="m_iZoneCount" column="ZoneCount" />
	<propdef property="m_strUUID" column="UUID" />

	<retrieve id="loadWithCache" cache="100" />

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrieve id="loadUUIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lContentID" />
		</key>
		<get>
			<property name="m_strUUID" />
		</get>
	</retrieve>

	<retrieve id="loadIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_strUUID" />
		</key>
		<get>
			<property name="m_lContentID" />
		</get>
	</retrieve>

	<retrieve id="loadResDirWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lContentID" />
		</key>
		<get>
			<property name="m_lResDirID" />
		</get>
	</retrieve>

	<retrievemulti id="loadByCat">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lContCatID" />
		</key>
		<order id="default" value="SORDER, CONTENTID" />
	</retrievemulti>

	<update id="updateContent" ondefault="no">
		<key>
			<property name="m_lContentID" />
		</key>
		<set>
			<property name="m_strContent" />
		</set>
	</update>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_CONTENTS where contentid = ? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lContentID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_RES_DIRS where ResDirID=? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>