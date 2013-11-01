<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BPAGE_CONTS" timestamp="UPDATETIME" synctimeout="10"
	listsynctimeout="10">
	<propdef property="m_lPageContID" column="PageContID" primary="yes"
		sequence="S_AP_BPAGE_CONTS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lPageID" column="PageID" />
	<propdef property="m_lContPageID" column="ContPageID" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_strAreaName" column="AreaName" />
	<propdef property="m_iZoneID" column="ZoneID" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="contName" column="ContName" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_bInheritable" column="IsInheritable" />
	<propdef property="m_strOwnerID" column="OwnerID" />
	<propdef property="m_lWindowID" column="WindowID" null="0@BIGINT" />
	<propdef property="m_lFrameID" column="FrameID" null="0@BIGINT" />
	<propdef property="m_bPopWinFlag" column="PopWinFlag" />
	<propdef property="winParams" column="WinParams" />
	<propdef property="m_lContentID" column="ContentID" null="0@BIGINT" />
	<propdef property="m_lContainContID" column="ContainContID"
		null="0@BIGINT" />
	<sublist property="childs" type="ApBPageContent" rmid="loadChilds"
		oid="default">
		<propref this="m_lPageContID" ref="m_lContainContID" />
		<propref this="m_strOwnerID" ref="m_strOwnerID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>

	<retrieve id="loadWithCache" cache="300" />

	<retrieve id="loadByPageAndContent">
		<key>
			<property name="m_lPageContID" mode="minus" />
			<property name="m_lPageID" />
			<property name="m_lContentID" />
		</key>
	</retrieve>

	<retrieve id="loadByContent">
		<key>
			<property name="m_lPageContID" mode="minus" />
			<property name="m_lContentID" />
		</key>
	</retrieve>

	<retrieve id="loadMaxOrderInZone" ondefault="no"
		sql="select max(sorder) as sorder, max(updatetime) as updatetime from AP_BPAGE_CONTS where pageid = ? and officialflag = ? and (ownerid = '' or ownerid is null or ownerid = ?) and areaname = ? and zoneid = ?">
		<key>
			<property name="m_lPageID" />
			<property name="m_iOfficialFlag" />
			<property name="m_strOwnerID" />
			<property name="m_strAreaName" />
			<property name="m_iZoneID" />
		</key>
		<get>
			<property name="m_iOrder" />
		</get>
	</retrieve>

	<retrieve id="loadMaxOrderInSubZone" ondefault="no"
		sql="select max(sorder) as sorder, max(updatetime) as updatetime from AP_BPAGE_CONTS where pageid = ? and officialflag = ? and (ownerid = '' or ownerid is null or ownerid = ?) and containcontid = ? and zoneid = ?">
		<key>
			<property name="m_lPageID" />
			<property name="m_iOfficialFlag" />
			<property name="m_strOwnerID" />
			<property name="m_lContainContID" />
			<property name="m_iZoneID" />
		</key>
		<get>
			<property name="m_iOrder" />
		</get>
	</retrieve>

	<retrieve id="loadWindowByUrl" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
			<exists table="AP_WINDOWS"
				where="AP_BPAGE_CONTS.windowid = AP_WINDOWS.windowid and AP_BPAGE_CONTS.officialflag = AP_WINDOWS.officialflag">
				<property name="contName" column="url" tablealias="AP_WINDOWS" />
				<property name="m_lContentID" column="appid" tablealias="AP_WINDOWS" />
			</exists>
		</key>
		<get>
			<property name="m_lPageContID" />
			<property name="m_lPageID" />
		</get>
	</retrieve>

	<retrieve id="loadWindowByUrlInSamePage" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<exists table="AP_WINDOWS"
				where="AP_BPAGE_CONTS.windowid = AP_WINDOWS.windowid and AP_BPAGE_CONTS.officialflag = AP_WINDOWS.officialflag">
				<property name="contName" column="url" tablealias="AP_WINDOWS" />
				<property name="m_lContentID" column="appid" tablealias="AP_WINDOWS" />
			</exists>
		</key>
		<get>
			<property name="m_lPageContID" />
		</get>
	</retrieve>

	<retrieve id="loadWithChilds">
		<get>
			<property name="childs" />
		</get>
	</retrieve>

	<retrievemulti id="loadGlobalForBranch" where="(OwnerID = '' or OwnerID is null)">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<order id="default" value="PAGEID, CONTAINCONTID, AREANAME, ZONEID, SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadByPage">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<or>
				<property name="m_strOwnerID" val="''" />
				<property name="m_strOwnerID" val="null" match="is" />
				<property name="m_strOwnerID" />
			</or>
		</key>
		<order id="default" value="AREANAME, ZONEID, SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadPrivateByPage">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_strOwnerID" when='!meta.getDatabaseProductName().equals("Oracle") || adb.m_strOwnerID != null &amp;&amp; !(adb.m_strOwnerID.equals(""))'/>
			<property name="m_strOwnerID" match="is" val="null" when='meta.getDatabaseProductName().equals("Oracle") &amp;&amp; (adb.m_strOwnerID == null || adb.m_strOwnerID.equals(""))'/>
		</key>
		<order id="default" value="AREANAME, ZONEID, SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadForMove">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_strAreaName" />
			<property name="m_iZoneID" />
			<or>
				<property name="m_strOwnerID" val="''" />
				<property name="m_strOwnerID" val="null" match="is" />
				<property name="m_strOwnerID" />
			</or>
		</key>
		<order id="default" value="SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadForMoveInSubZone">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_lContainContID" />
			<property name="m_iZoneID" />
			<or>
				<property name="m_strOwnerID" val="''" />
				<property name="m_strOwnerID" val="null" match="is" />
				<property name="m_strOwnerID" />
			</or>
		</key>
		<order id="default" value="SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadForDrag">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageContID" match="&lt;&gt;" />
			<property name="m_lPageID" />
			<property name="m_strAreaName" />
			<or>
				<property name="m_strOwnerID" val="''" />
				<property name="m_strOwnerID" val="null" match="is" />
				<property name="m_strOwnerID" />
			</or>
		</key>
		<order id="default" value="ZONEID, SORDER, PAGECONTID" />
	</retrievemulti>

	<retrievemulti id="loadChilds">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lContainContID" />
		</key>
		<get>
			<property name="childs" />
		</get>
		<order id="default" value="ZONEID, SORDER, PAGECONTID" />
	</retrievemulti>

	<update id="updateZoneAndOrder" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageContID" />
		</key>
		<set>
			<property name="m_iZoneID " />
			<property name="m_iOrder " />
		</set>
	</update>

	<update id="updateAreaZoneContainerAndOrder" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageContID" />
		</key>
		<set>
			<property name="m_strAreaName " />
			<property name="m_iZoneID " />
			<property name="m_lContainContID" />
			<property name="m_iOrder " />
		</set>
	</update>

	<update id="updateByPageAndContent" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
			<property name="m_lContentID" />
		</key>
		<set>
			<property name="m_bInheritable" />
		</set>
	</update>
</adb>