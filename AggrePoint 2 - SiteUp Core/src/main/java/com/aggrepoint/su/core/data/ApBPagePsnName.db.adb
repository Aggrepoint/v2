<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BPAGE_PSN_NAMES" timestamp="UPDATETIME" listsynctimeout="10">
	<propdef property="m_lPsnNameID" column="PsnNameID" primary="yes" sequence="S_AP_BPAGE_PSN_NAMES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_lPageID" column="PageID" />
	<propdef property="m_strPageName" column="PageName" />
	<propdef property="ownerId" column="OwnerID" />
	<propdef property="m_strAccessRule" column="AccessRule" />

	<retrievemulti id="loadGlobalForBranch" where="(OwnerID = '' or OwnerID is null)">
		<key>
			<property name="m_lBranchID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="default" value="PageID, PsnNameID" />
	</retrievemulti>

	<retrievemulti id="loadPrivateForBranch">
		<key>
			<property name="m_lBranchID" />
			<property name="ownerId" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="default" value="PageID, PsnNameID" />
	</retrievemulti>

	<retrievemulti id="loadByPage">
		<key>
			<property name="m_lPageID" />
			<property name="m_iOfficialFlag" />
			<or>
				<property name="ownerId" val="''"/>
				<property name="ownerId" val="null" match="is"/>
				<property name="ownerId" />
			</or>
		</key>
		<order id="default" value="OwnerID, PsnNameID" />
	</retrievemulti>

	<retrievemulti id="loadPrivateByPage">
		<key>
			<property name="m_lPageID" />
			<property name="ownerId" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="default" value="OwnerID, PsnNameID" />
	</retrievemulti>

	<delete id="delByPageAndOwner" ondefault="no">
		<key>
			<property name="m_lPageID" />
			<property name="ownerId" />
			<property name="m_iOfficialFlag" />
		</key>
	</delete>
</adb>