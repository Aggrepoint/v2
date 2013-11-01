<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_CHANNELS" timestamp="UPDATETIME">
	<propdef property="m_lChannelID" column="ChannelID" primary="yes" sequence="S_AP_CHANNELS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_bPsnFlag" column="PsnFlag" />
	<propdef property="m_strChannelName" column="ChannelName" />
	<propdef property="m_strChannelDesc" column="ChannelDesc" rm="no" />
	<propdef property="m_lTemplateID" column="TemplateID" />
	<propdef property="m_strTmplParams" column="TmplParams" rm="no" />
	<propdef property="m_strEditRule" column="EditRule" />
	<propdef property="m_strPubRule" column="PubRule" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_strPath" column="Path" />
	<propdef property="m_lRefPageId" column="RegPageId" />
	<propdef property="m_strUUID" column="UUID" />
	<propdef property="m_iEditCount" column="EditCount" r="no" rm="no" c="no" u="no" />
	<propdef property="m_iApproveCount" column="ApproveCount" r="no" rm="no" c="no" u="no" />
	<propdef property="m_iPublishCount" column="PublishCount" r="no" rm="no" c="no" u="no" />

	<retrieve id="loadBySiteAndPath">
		<key>
			<property name="m_lChannelID" mode="minus" />
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
			<property name="m_strPath" />
		</key>
	</retrieve>

	<retrievemulti id="loadBySiteForExport">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
		<get>
			<property name="m_strChannelDesc" />
			<property name="m_strTmplParams" />
		</get>
		<order id="order" value="CHANNELID" />
	</retrievemulti>

	<retrievemulti id="loadBySite" sql="SELECT AP_CHANNELS.*, (select count(*) from AP_CPAGES t where t.channelid = AP_CHANNELS.ChannelID and t.statusid = 1) as EDITCOUNT, (select count(*) from AP_CPAGES t where t.channelid = AP_CHANNELS.ChannelID and t.statusid = 2) as APPROVECOUNT, (select count(*) from AP_CPAGES t where t.channelid = AP_CHANNELS.ChannelID and t.statusid = 3) as PUBLISHCOUNT FROM AP_CHANNELS where OFFICIALFLAG = ? and SITEID = ?" count="select count(*), max(updatetime) as updatetime from AP_CHANNELS where OFFICIALFLAG = ? and SITEID = ?">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
		<get>
			<property name="m_iEditCount" />
			<property name="m_iApproveCount" />
			<property name="m_iPublishCount" />
		</get>
		<order id="order" value="CHANNELID" />
	</retrievemulti>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_RES_DIRS a where exists (select * from AP_CPAGES b where a.ResDirID = b.ResDirID and a.OfficialFlag = b.OfficialFlag and b.ChannelID = ? and b.OfficialFlag = ?)</stmt>
			<key>
				<property name="m_lChannelID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CPAGES where channelid=? and officialflag=?</stmt>
			<key>
				<property name="m_lChannelID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CHANNELS where channelid=? and officialflag=?</stmt>
			<key>
				<property name="m_lChannelID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>