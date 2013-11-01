<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_CPAGES" timestamp="UPDATETIME">
	<propdef property="m_lPageID" column="PageID" primary="yes" sequence="S_AP_CPAGES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lChannelID" column="ChannelID" />
	<propdef property="channelIDs" column="ChannelID" c="no" r="no" rm="no" u="no" />
	<propdef property="m_iPageType" column="PageType" />
	<propdef property="m_iStatusID" column="StatusID" />
	<propdef property="m_sInheritTmpl" column="InheritTmpl" />
	<propdef property="m_lTemplateID" column="TemplateID" null="0@BIGINT" />
	<propdef property="m_strTmplParams" column="TmplParams" />
	<propdef property="m_strTitle" column="Title" />
	<propdef property="m_strSubTitle" column="SubTitle" rm="no" />
	<propdef property="m_strContent" column="Content" rm="no" clob="yes" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_sOpenMode" column="OpenMode" />
	<propdef property="m_strFileName" column="FileName" rm="no" />
	<propdef property="m_strFileContType" column="FileContType" rm="no" />
	<propdef property="m_strFile" column="FileData" blob="yes" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_strEditor" column="Editor" />
	<propdef property="m_strApprover" column="Approver" />
	<propdef property="m_strSource" column="Source" />
	<propdef property="m_strKeyword" column="Keyword" />
	<propdef property="m_strPath" column="Path" />
	<propdef property="createDate" column="CreateDate" />
	<propdef property="m_strUUID" column="UUID" />
	<propdef property="m_lResDirID" column="ResDirID" />
	<sublist property="reses" type="ApRes" rmid="loadByDir">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lResDirID" ref="m_lResDirID" />
	</sublist>

	<retrieve id="loadResDirWithCache" cache="200" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="m_lResDirID" />
		</get>
	</retrieve>

	<retrieve id="loadByPath">
		<key>
			<property name="m_lPageID" mode="minus" />
			<property name="m_lChannelID" />
			<property name="m_strPath" />
		</key>
	</retrieve>

	<retrieve id="loadStatus" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="m_iPageType" />
			<property name="m_iStatusID" />
			<property name="m_lChannelID" />
		</get>
	</retrieve>

	<retrievemulti id="loadByStatus">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lChannelID" />
			<property name="m_iStatusID" />
		</key>
		<order id="order" value="CHANNELID, SORDER, CREATEDATE DESC" />
	</retrievemulti>

	<retrievemulti id="loadByStatusInChannels">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="channelIDs" />
			<property name="m_iStatusID" />
		</key>
		<order id="order" value="CHANNELID, SORDER, CREATEDATE DESC" />
	</retrievemulti>

	<retrievemulti id="loadForExport">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lChannelID" />
		</key>
		<get>
			<property name="m_strSubTitle" />
			<property name="m_strContent" />
			<property name="m_strFileContType" />
			<property name="reses" />
		</get>
		<order id="order" value="CHANNELID" />
	</retrievemulti>

	<update id="updateStatus" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<set>
			<property name="m_iStatusID" />
		</set>
	</update>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_RES_DIRS where ResDirID=? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_CPAGES where pageid = ? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lPageID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>