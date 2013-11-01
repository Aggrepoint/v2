<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_APPS" timestamp="UPDATETIME">
	<propdef property="m_lAppID" column="AppID" primary="yes"
		sequence="S_AP_APPS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lResDirID" column="ResDirID" />
	<propdef property="m_strAppDesc" column="AppDesc" />
	<propdef property="m_lLogoImageID" column="LogoImageID" null="0@BIGINT" />
	<propdef property="m_strAppName" column="AppName" />
	<propdef property="m_strAppVer" column="AppVer" />
	<propdef property="m_iStatusID" column="StatusID" />
	<propdef property="m_strHostURL" column="HostURL" />
	<propdef property="m_strRootPath" column="RootPath" />
	<propdef property="m_iConnTimeout" column="ConnTimeout" />
	<propdef property="m_iReadTimeout" column="ReadTimeout" />
	<propdef property="m_strUUID" column="UUID" />
	<sublist property="windows" type="ApWindow" rmid="loadByApp">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lAppID" ref="m_lAppID" />
	</sublist>

	<retrieve id="loadResDirWithCache" cache="200" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lAppID" />
		</key>
		<get>
			<property name="m_lResDirID" />
		</get>
	</retrieve>

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

	<retrievemulti id="loadForInsert">
		<key>
			<property name="m_iOfficialFlag" />
			<or>
				<property name="m_lSiteID" />
				<property name="m_lSiteID" val="100" />
			</or>
		</key>
		<get>
			<property name="windows" />
		</get>
		<order id="def" value="SiteID, AppName" />
	</retrievemulti>

	<update id="updateStatus" ondefault="no">
		<key>
			<property name="m_lAppID" />
			<property name="m_iOfficialFlag" />
		</key>
		<set>
			<property name="m_iStatusID" />
		</set>
	</update>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_IMAGES where imageid=?</stmt>
			<key>
				<property name="m_lLogoImageID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_APPS where appid=? and officialflag=?</stmt>
			<key>
				<property name="m_lAppID" />
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