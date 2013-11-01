<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_RES_DIRS" timestamp="UPDATETIME">
	<propdef property="m_lResDirID" column="ResDirID" primary="yes" sequence="S_AP_RES_DIRS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_bSysFlag" column="SysFlag" />
	<propdef property="m_bContFlag" column="ContFlag" />
	<propdef property="m_bChildFlag" column="ChildFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lParentDirID" column="ParentDirID" null="0@BIGINT"/>
	<propdef property="name" column="DirName" />
	<propdef property="m_strFullPath" column="FullPath" />
	<propdef property="m_strUUID" column="UUID" />
	<sublist property="reses" type="ApRes"
		rmid="loadByDir">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lResDirID" ref="m_lResDirID" />
	</sublist>
	<sublist property="childDirs" type="ApResDir"
		rmid="loadByDir">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lResDirID" ref="m_lParentDirID" />
		<propref this="m_lSiteID" ref="m_lSiteID" />
	</sublist>

	<retrieve id="loadWithCache" cache="200"></retrieve>

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrieve id="loadWithResAndChild">
		<get>
			<property name="reses" />
			<property name="childDirs" />
		</get>
	</retrieve>

	<retrieve id="loadByPath">
		<key>
			<property name="m_lResDirID" mode="minus" />
			<property name="m_lSiteID" />
			<property name="m_strFullPath" />
		</key>
	</retrieve>

	<retrieve id="loadRootByName" where="PARENTDIRID is NULL">
		<key>
			<property name="m_lResDirID" mode="minus" />
			<property name="m_lSiteID" />
			<property name="name" />
		</key>
	</retrieve>

	<retrieve id="loadByName">
		<key>
			<property name="m_lResDirID" mode="minus" />
			<property name="m_lParentDirID" />
			<property name="name" />
		</key>
	</retrieve>

	<retrievemulti id="loadRoot" where="parentdirid is null">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
		<order id="def" value="DirName" />
	</retrievemulti>

	<retrievemulti id="loadByDir">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lParentDirID" />
			<property name="m_lSiteID" />
		</key>
		<order id="def" value="DirName" />
	</retrievemulti>

	<update id="updateAllChildFlag" ondefault="no">
		<key>
			<property name="m_lResDirID" />
		</key>
		<set>
			<property name="m_bChildFlag" />
		</set>
	</update>

	<update id="updateDir" ondefault="no">
		<key>
			<property name="m_lResDirID" />
		</key>
		<set>
			<property name="name" />
			<property name="m_strFullPath" />
		</set>
	</update>

	<proc id="clearContent" atom="yes">
		<sql>
			<stmt>delete from AP_RES_DIRS where ParentDirId = ? and OfficialFlag = ?</stmt>
			<key>
				<property name="m_lResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_RESES where ResDirId = ? and OfficialFlag = ?</stmt>
			<key>
				<property name="m_lResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>

	<proc id="updateChildFlag" atom="yes">
		<sql>
			<stmt>update AP_RES_DIRS A set CHILDFLAG = (select count(*) from AP_RES_DIRS where OFFICIALFLAG = A.OFFICIALFLAG and PARENTDIRID = A.RESDIRID) where RESDIRID = ? and OFFICIALFLAG = ?</stmt>
			<key>
				<property name="m_lResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>