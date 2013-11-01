<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BPAGE_PSN_TMPLS" timestamp="UPDATETIME">
	<propdef property="m_lPsnTmplID" column="PsnTmplID" primary="yes" sequence="S_AP_BPAGE_PSN_TMPLS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lPageID" column="PageID" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_lTemplateID" column="TemplateID" />
	<propdef property="m_strTmplParams" column="TmplParams" />
	<propdef property="m_strOwnerID" column="OwnerID" />
	<propdef property="m_strAccessRule" column="AccessRule" />

	<retrievemulti id="loadGlobalForBranch" where="(OwnerID = '' or OwnerID is null)">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<order id="default" value="PageID, PsnTmplID" />
	</retrievemulti>

	<retrievemulti id="loadByPage">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<or>
				<property name="m_strOwnerID" val="''"/>
				<property name="m_strOwnerID" val="null" match="is" />
				<property name="m_strOwnerID" />
			</or>
		</key>
		<order id="default" value="OwnerID, PsnTmplID" />
	</retrievemulti>

	<retrievemulti id="loadPrivateByPage">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_strOwnerID" when='!meta.getDatabaseProductName().equals("Oracle") || adb.m_strOwnerID != null &amp;&amp; !(adb.m_strOwnerID.equals(""))'/>
			<property name="m_strOwnerID" match="is" val="null" when='meta.getDatabaseProductName().equals("Oracle") &amp;&amp; (adb.m_strOwnerID == null || adb.m_strOwnerID.equals(""))'/>
		</key>
		<order id="default" value="OwnerID, PsnTmplID" />
	</retrievemulti>

	<retrievemulti id="loadByTemplate" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lTemplateID" />
		</key>
		<get>
			<property name="m_lPageID" />
		</get>
	</retrievemulti>

	<delete id="delByPageAndOwner" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_strOwnerID" when='!meta.getDatabaseProductName().equals("Oracle") || adb.m_strOwnerID != null &amp;&amp; !(adb.m_strOwnerID.equals(""))'/>
			<property name="m_strOwnerID" match="is" val="null" when='meta.getDatabaseProductName().equals("Oracle") &amp;&amp; (adb.m_strOwnerID == null || adb.m_strOwnerID.equals(""))'/>
		</key>
	</delete>
</adb>