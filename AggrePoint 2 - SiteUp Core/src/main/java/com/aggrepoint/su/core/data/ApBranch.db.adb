<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BRANCHES" timestamp="UPDATETIME" synctimeout="30"
	listsynctimeout="10">
	<propdef property="m_lBranchID" column="BranchID" primary="yes"
		sequence="S_AP_BRANCHES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_iPsnType" column="PsnType" />
	<propdef property="m_strBranchName" column="BranchName" />
	<propdef property="m_strBranchDesc" column="BranchDesc" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_strManageRule" column="ManageRule" />
	<propdef property="m_strClbPsnRule" column="ClbPsnRule" />
	<propdef property="m_strPvtPsnRule" column="PvtPsnRule" />
	<propdef property="m_strRootPath" column="RootPath" />
	<propdef property="m_strHomePath" column="HomePath" />
	<propdef property="m_strLoginPath" column="LoginPath" />
	<propdef property="m_strNoAccessPath" column="NoAccessPath" />
	<propdef property="m_strUUID" column="UUID" />
	<propdef property="m_lAdminTmplID" column="AdminTmplID" null="0@BIGINT" />
	<propdef property="m_iMarkupType" column="MarkupType" />

	<retrieve id="loadWithCache" cache="50"></retrieve>

	<retrieve id="loadIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_strUUID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lBranchID" />
		</get>
	</retrieve>

	<retrieve id="rootPathExists" ondefault="no">
		<key>
			<property name="m_strRootPath" />
			<property name="m_iOfficialFlag" />
		</key>
	</retrieve>

	<retrieve id="loadByRootPath">
		<key>
			<property name="m_lBranchID" mode="minus" />
			<property name="m_iOfficialFlag" />
			<property name="m_strRootPath" />
		</key>
		<get>
			<property name="m_lBranchID" />
		</get>
	</retrieve>

	<retrieve id="loadMarkupType" ondefault="no">
		<key>
			<property name="m_lBranchID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_iMarkupType" />
		</get>
	</retrieve>

	<retrievemulti id="loadNotGroupBySite" where="PsnType &lt;&gt; 2" ondefault="no">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lBranchID" />
			<property name="m_lSiteID" />
			<property name="m_iPsnType" />
			<property name="m_strBranchName" />
			<property name="m_strRootPath" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadAllBySite">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
	</retrievemulti>

	<retrievemulti id="loadIDBySite" ondefault="no">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lBranchID" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadIDAndRootPathBySite" ondefault="no">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lBranchID" />
			<property name="m_strRootPath" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadBySite">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_strAccessRule" mode="minus" />
			<property name="m_strManageRule" mode="minus" />
			<property name="m_strClbPsnRule" mode="minus" />
			<property name="m_strPvtPsnRule" mode="minus" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadAllPath" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lBranchID" />
			<property name="m_iOfficialFlag" />
			<property name="m_strRootPath" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadHasMap" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<exists where="AP_PATH_MAPS.BRANCHID = AP_BRANCHES.BRANCHID"
				table="AP_PATH_MAPS" />
		</key>
		<get>
			<property name="m_lBranchID" />
			<property name="m_strRootPath" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadAllGroup" ondefault="no" where="PSNTYPE = 2">
		<get>
			<property name="m_lBranchID" />
			<property name="m_iOfficialFlag" />
			<property name="m_strRootPath" />
			<property name="m_iMarkupType" />
		</get>
	</retrievemulti>

	<proc id="fixInheritTmplAndChildCount" atom="yes">
		<sql>
			<stmt>update ap_bpages s set inherittmpl = 0 where not exists (select * from ap_bpages p where p.pageid = s.parentid and p.officialflag = s.officialflag and p.templateid = s.templateid) and inherittmpl = 1 and branchid = ?</stmt>
			<key>
				<property name="m_lBranchID" />
			</key>
		</sql>
		<sql>
			<stmt><![CDATA[update ap_bpages a set childcount= (select count(*) from ap_bpages b where a.siteid = b.siteid and a.officialflag = b.officialflag and b.parentid = a.pageid and b.pageid <> a.pageid) where branchid = ?]]></stmt>
			<key>
				<property name="m_lBranchID" />
			</key>
		</sql>
	</proc>
</adb>