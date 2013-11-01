<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_BPAGES" timestamp="UPDATETIME" listsynctimeout="10">
	<propdef property="m_lPageID" column="PageID" primary="yes" sequence="S_AP_BPAGES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_iPageType" column="PageType" />
	<propdef property="pageName" column="PageName" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_lTemplateID" column="TemplateID" null="0@BIGINT" />
	<propdef property="m_strTmplParams" column="TmplParams" />
	<propdef property="m_bInheritTmpl" column="InheritTmpl" />
	<propdef property="m_lParentID" column="ParentID" null="0@BIGINT"/>
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_iOpenMode" column="OpenMode" />
	<propdef property="m_strPathName" column="PathName" />
	<propdef property="m_strOwnerID" column="OwnerID" />
	<propdef property="m_strAccessRule" column="AccessRule" />
	<propdef property="m_strClbPsnRule" column="ClbPsnRule" />
	<propdef property="m_strPvtPsnRule" column="PvtPsnRule" />
	<propdef property="m_bSkipToSub" column="SkipToSub" />
	<propdef property="m_bHide" column="Hide" />
	<propdef property="m_bResetWin" column="ResetWin" />
	<propdef property="UseMap" />
	<propdef property="m_bInheritUseMap" column="InheritUseMap" />
	<propdef property="m_strFullPath" column="FullPath" />
	<propdef property="m_strDirectPath" column="DirectPath" />
	<propdef property="m_iChildCount" column="ChildCount" />
	<propdef property="m_strUUID" column="UUID" />
	<propdef property="m_iCount" column="C" r="no" rm="no" c="no" u="no" />
	<sublist property="layouts" type="ApBPageLayout" rmid="loadByPage" oid="default">
		<propref this="m_lPageID" ref="m_lPageID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>
	<sublist property="psnNames" type="ApBPagePsnName" rmid="loadPrivateByPage" oid="default">
		<propref this="m_lPageID" ref="m_lPageID" />
		<propref this="m_strOwnerID" ref="m_strOwnerID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>
	<sublist property="psnTmpls" type="ApBPagePsnTmpl" rmid="loadPrivateByPage" oid="default">
		<propref this="m_lPageID" ref="m_lPageID" />
		<propref this="m_strOwnerID" ref="m_strOwnerID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>
	<sublist property="contents" type="ApBPageContent" rmid="loadPrivateByPage" oid="default">
		<propref this="m_lPageID" ref="m_lPageID" />
		<propref this="m_strOwnerID" ref="m_strOwnerID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>
	<sublist property="subPages" type="ApBPage" rmid="loadByParent">
		<propref this="m_lPageID" ref="m_lParentID" />
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
	</sublist>
	<action name="afterLoaded" method="afterLoaded" />

	<retrieve id="loadByFullPath">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" mode="minus" />
			<property name="m_strFullPath" />
		</key>
	</retrieve>

	<retrieve id="loadByPath">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" mode="minus" />
			<property name="m_strPathName" />
			<property name="m_lParentID" />
		</key>
	</retrieve>

	<retrieve id="loadRoot" where="PARENTID is NULL">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" mode="minus" />
			<property name="m_lBranchID" />
		</key>
	</retrieve>

	<retrieve id="loadRootPath" where="PARENTID is NULL" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<get>
			<property name="m_strPathName" />
		</get>
	</retrieve>

	<retrieve id="loadCount" sql="select count(*) as c, current timestamp as updatetime from AP_BPAGES where branchid = ?" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<get>
			<property name="m_iCount" />
		</get>
	</retrieve>

	<retrieve id="loadAssoc" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="layouts" />
			<property name="psnNames" />
			<property name="psnTmpls" />
			<property name="contents" />
		</get>
	</retrieve>

	<retrieve id="loadPrivateAssoc" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="psnNames"/>
			<property name="psnTmpls" />
			<property name="contents" />
		</get>
	</retrieve>

	<retrieve id="loadPsnNames" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="psnNames" />
		</get>
	</retrieve>

	<retrieve id="loadPsnTmpls" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="psnTmpls" />
		</get>
	</retrieve>

	<retrieve id="loadSite" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="m_lSiteID" />
		</get>
	</retrieve>

	<retrieve id="loadFullPath" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<get>
			<property name="m_strFullPath" />
			<property name="m_strDirectPath" />
		</get>
	</retrieve>

	<retrievemulti id="loadGlobalForBranch" where="(OWNERID = '' or OWNERID is NULL)">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<order id="default" value="PAGEID" />
	</retrievemulti>

	<retrievemulti id="loadPrivateForBranch">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
			<property name="m_strOwnerID" when='!meta.getDatabaseProductName().equals("Oracle") || adb.m_strOwnerID != null &amp;&amp; !(adb.m_strOwnerID.equals(""))'/>
			<property name="m_strOwnerID" match="is" val="null" when='meta.getDatabaseProductName().equals("Oracle") &amp;&amp; (adb.m_strOwnerID == null || adb.m_strOwnerID.equals(""))'/>
		</key>
		<order id="default" value="PARENTID, SORDER, PAGEID" />
	</retrievemulti>

	<retrievemulti id="loadExpandMatchForBranch">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
			<property name="UseMap" val="2" />
		</key>
		<order id="default" value="PARENTID DESC, SORDER DESC, PAGEID DESC" />
	</retrievemulti>

	<retrievemulti id="loadByParent">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentID" />
			<property name="m_lParentID" column="PageID" match="&lt;&gt;"/>
		</key>
		<get>
			<property name="subPages" />
		</get>
		<order id="default" value="SORDER, PAGEID" />
		<trigger action="afterLoaded" event="after" />
	</retrievemulti>

	<retrievemulti id="loadByParentNoSub">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentID" />
			<property name="m_lParentID" column="PageID" match="&lt;&gt;"/>
		</key>
		<order id="default" value="SORDER, PAGEID" />
	</retrievemulti>

	<retrievemulti id="loadByTemplate" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lTemplateID" />
		</key>
		<get>
			<property name="m_lPageID" />
			<property name="m_lSiteID" />
			<property name="m_lBranchID" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadForCascadeTemplateAndPathAndMap" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentID" />
			<property name="m_lParentID" column="PageID" match="&lt;&gt;"/>
		</key>
		<get>
			<property name="m_lPageID" />
			<property name="m_bInheritTmpl" />
			<property name="m_lTemplateID " />
			<property name="m_strPathName" />
			<property name="m_strFullPath" />
			<property name="m_strDirectPath" />
			<property name="UseMap" />
			<property name="m_bInheritUseMap" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadForDelete" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentID" />
			<property name="m_lParentID" column="PageID" match="&lt;&gt;"/>
		</key>
		<get>
			<property name="m_lPageID" />
		</get>
	</retrievemulti>

	<retrievemulti id="loadForMove" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentID" />
			<property name="m_lParentID" column="PageID" match="&lt;&gt;"/>
			<property name="m_strOwnerID" when='!meta.getDatabaseProductName().equals("Oracle") || adb.m_strOwnerID != null &amp;&amp; !(adb.m_strOwnerID.equals(""))'/>
			<property name="m_strOwnerID" match="is" val="null" when='meta.getDatabaseProductName().equals("Oracle") &amp;&amp; (adb.m_strOwnerID == null || adb.m_strOwnerID.equals(""))'/>
		</key>
		<get>
			<property name="m_lPageID" />
			<property name="m_iOrder" />
		</get>
		<order id="default" value="SORDER, PAGEID" />
	</retrievemulti>

	<update id="updateTemplateAndPathAndMap" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<set>
			<property name="m_lTemplateID" flag="template"/>
			<property name="m_strFullPath" flag="path"/>
			<property name="m_strDirectPath" flag="path"/>
			<property name="UseMap" flag="map"/>
			<property name="m_bInheritUseMap" flag="map" />
		</set>
	</update>

	<update id="updateOrder" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lPageID" />
		</key>
		<set>
			<property name="m_iOrder" />
		</set>
	</update>

	<proc id="updateChildCount" atom="yes">
		<sql>
			<stmt>update AP_BPAGES a set CHILDCOUNT = (select count(*) from AP_BPAGES B where A.SITEID = B.SITEID and A.OFFICIALFLAG = B.OFFICIALFLAG and B.PARENTID = A.PAGEID and B.PAGEID &lt;&gt; A.PAGEID) where PAGEID = ? and OFFICIALFLAG = ?</stmt>
			<key>
				<property name="m_lPageID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>