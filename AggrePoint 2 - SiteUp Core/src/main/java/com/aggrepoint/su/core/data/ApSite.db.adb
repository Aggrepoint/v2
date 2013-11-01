<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_SITES" timestamp="UPDATETIME">
	<propdef property="m_lSiteID" column="SiteID" primary="yes" sequence="S_AP_SITES" />
	<propdef property="m_strSiteName" column="SiteName" />
	<propdef property="m_strSiteDesc" column="SiteDesc" />
	<propdef property="m_lSiteLogoID" column="SiteLogoID" null="0@BIGINT" />
	<propdef property="m_strManageRule" column="ManageRule" />
	<propdef property="m_strPublishBranchDir" column="PublishBranchDir" />
	<propdef property="m_strStaticBranchUrl" column="StaticBranchUrl" />
	<propdef property="m_strPublishChannelDir" column="PublishChannelDir" />
	<propdef property="m_strStaticChannelUrl" column="StaticChannelUrl" />
	<propdef property="m_strPublishResDir" column="PublishResDir" />
	<propdef property="m_strStaticResUrl" column="StaticResUrl" />
	<propdef property="m_strUUID" column="UUID" />
	<sublist property="branches" type="ApBranch" rmid="loadBySite">
		<propref this="m_lSiteID" ref="m_lSiteID" />
	</sublist>

	<retrieve id="loadWithCache" cache="50"></retrieve>

	<retrieve id="loadWithBranches">
		<get>
			<property name="branches" />
		</get>
	</retrieve>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_BRANCHES where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CHANNELS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_TEMPLATES where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_LAYOUTS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_FRAMES where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CONTENTS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CONT_CATS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_APPS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_RES_DIRS where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES where ImageID = ?</stmt>
			<key>
				<property name="m_lSiteLogoID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_SITES where siteid = ?</stmt>
			<key>
				<property name="m_lSiteID" />
			</key>
		</sql>
	</proc>
</adb>