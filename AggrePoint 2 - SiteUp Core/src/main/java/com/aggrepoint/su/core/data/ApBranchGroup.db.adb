<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BRANCH_GROUPS" timestamp="UPDATETIME" synctimeout="30"
	listsynctimeout="10">
	<propdef property="m_lGroupID" column="GroupID" primary="yes"
		sequence="S_AP_BRANCH_GROUPS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lGroupBranchID" column="GroupBranchID" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_strRule" column="Rule" />
	<propdef property="m_iMarkupType" column="MarkupType" />
	<propdef property="m_strBranchName" column="BranchName" c="no"
		r="no" rm="no" u="no" />

	<retrievemulti id="loadBySite">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
	</retrievemulti>

	<retrievemulti id="loadByBranch" table="AP_BRANCH_GROUPS a, AP_BRANCHES b"
		where="a.branchid = b.branchid and a.officialflag = b.officialflag"
		tablealias="a">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lGroupBranchID" />
		</key>
		<get>
			<property name="m_strBranchName" tablealias="b" />
		</get>
		<order id="default" value="SOrder" />
	</retrievemulti>

	<delete id="deleteByBranch" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lGroupBranchID" />
		</key>
	</delete>
</adb>