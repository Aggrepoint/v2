<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BPAGE_LAYOUTS" timestamp="UPDATETIME">
	<propdef property="m_lBPLayoutID" column="BPLayoutID" primary="yes" sequence="S_AP_BPAGE_LAYOUTS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lPageID" column="PageID" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_lLayoutID" column="LayoutID" />
	<propdef property="m_strAreaName" column="AreaName" />
	<propdef property="m_bInheritable" column="IsInheritable" />

	<retrieve id="loadByPageAndArea">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_strAreaName" />
			<property name="m_lBPLayoutID" mode="minus" />
		</key>
	</retrieve>

	<retrievemulti id="loadGlobalForBranch">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<order id="default" value="PageID, BPLayoutID" />
	</retrievemulti>

	<retrievemulti id="loadByPage">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<order id="default" value="LayoutID" />
	</retrievemulti>
</adb>