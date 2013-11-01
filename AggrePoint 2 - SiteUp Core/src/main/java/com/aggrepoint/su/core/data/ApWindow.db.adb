<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_WINDOWS" timestamp="UPDATETIME" synctimeout="10">
	<propdef property="m_lWindowID" column="WindowID" primary="yes"
		sequence="S_AP_WINDOWS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lAppID" column="AppID" />
	<propdef property="m_lPreviewImageID" column="PreviewImageID"
		null="0@BIGINT" />
	<propdef property="m_strURL" column="URL" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_strName" column="WinName" />
	<propdef property="m_strDesc" column="WinDesc" />
	<propdef property="m_iWinMode" column="WinMode" />
	<propdef property="m_strUUID" column="UUID" />
	<sublist property="params"
		type="ApWinParam" rmid="loadByWindow">
		<propref this="m_lWindowID" ref="m_lWindowID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>

	<retrieve id="loadWithCache" cache="50" />

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrieve id="loadWithParams">
		<get>
			<property name="params" />
		</get>
	</retrieve>

	<retrieve id="loadUUIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_lWindowID" />
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
			<property name="m_lWindowID" />
			<property name="m_iOfficialFlag" />
		</get>
	</retrieve>

	<retrievemulti id="loadByApp">
		<key>
			<property name="m_lAppID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="" value="URL"/>
	</retrievemulti>

	<retrievemulti id="loadByAppWithParams">
		<key>
			<property name="m_lAppID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="params" />
		</get>
	</retrievemulti>

	<update id="touchByApp" ondefault="no">
		<key>
			<property name="m_lAppID" />
			<property name="m_iOfficialFlag" />
		</key>
	</update>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_WIN_PARAMS where windowid = ? and officialflag = ?</stmt>
			<key>
				<property name="m_lWindowID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from S_IMAGES where imageid=?</stmt>
			<key>
				<property name="m_lPreviewImageID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_WINDOWS where windowid=? and officialflag = ?</stmt>
			<key>
				<property name="m_lWindowID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>
