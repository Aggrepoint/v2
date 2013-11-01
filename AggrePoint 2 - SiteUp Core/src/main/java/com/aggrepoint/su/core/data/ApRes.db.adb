<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_RESES" timestamp="UPDATETIME">
	<propdef property="m_lResID" column="ResID" primary="yes"
		sequence="S_AP_RESES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lResDirID" column="ResDirID" />
	<propdef property="fileName" column="FileName" />
	<propdef property="m_strContentType" column="ContentType" />
	<propdef property="m_strFile" column="FileData" blob="yes" r="no"
		rm="no" />
	<propdef property="m_iFileType" column="FileType" />
	<propdef property="m_strSmallIcon" column="SmallIcon" blob="yes"
		r="no" rm="no" />
	<propdef property="m_strLargeIcon" column="LargeIcon" blob="yes"
		r="no" rm="no" />
	<propdef property="m_strAttribute" column="Attribute" r="no"
		rm="no" />
	<propdef property="m_iWidth" column="Width" />
	<propdef property="m_iHeight" column="Height" />
	<propdef property="m_lSize" column="FileSize" />
	<propdef property="m_iOrder" column="SOrder" />

	<retrieve id="loadByDirAndName">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResID" mode="minus" />
			<property name="m_lResDirID" />
			<property name="fileName" />
		</key>
	</retrieve>

	<retrieve id="loadFile" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResID" />
		</key>
		<get>
			<property name="m_strFile" />
		</get>
	</retrieve>

	<retrieve id="loadSmallIcon" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResID" />
		</key>
		<get>
			<property name="m_strSmallIcon" />
		</get>
	</retrieve>

	<retrieve id="loadLargeIcon" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResID" />
		</key>
		<get>
			<property name="m_strLargeIcon" />
		</get>
	</retrieve>

	<retrievemulti id="loadByDirAndType">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResDirID" />
			<property name="m_iFileType" />
		</key>
		<order id="def" value="FILENAME ASC" />
	</retrievemulti>

	<retrievemulti id="loadByDir">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResDirID" />
		</key>
		<order id="" value="CONTENTTYPE ASC, FILENAME ASC" />
		<order id="0" value="FILENAME ASC" />
		<order id="1" value="FILENAME DESC" />
		<order id="2" value="CONTENTTYPE ASC" />
		<order id="3" value="CONTENTTYPE DESC" />
		<order id="4" value="SIZE ASC" />
		<order id="5" value="SIZE DESC" />
		<order id="6" value="WIDTH ASC" />
		<order id="7" value="WIDTH DESC" />
		<order id="8" value="HEIGHT ASC" />
		<order id="9" value="HEIGHT DESC" />
	</retrievemulti>

	<retrievemulti id="loadAppResByBranch"
		table="AP_RESES a, AP_RES_DIRS b, AP_APPS app"
		where="a.resdirid = b.resdirid and a.officialflag = b.officialflag and b.resdirid = app.resdirid and b.officialflag = app.officialflag"
		tablealias="a">
		<key>
			<exists table="AP_WINDOWS w, AP_BPAGE_CONTS c"
				where="c.windowid = w.windowid and c.officialflag = w.officialflag and w.appid = app.appid and w.officialflag = app.officialflag">
				<property name="m_iOfficialFlag" />
				<property name="m_lResID" column="BRANCHID" tablealias="C" />
			</exists>
		</key>
		<order id="" value="CONTENTTYPE ASC, FILENAME ASC" />
	</retrievemulti>

	<retrievemulti id="loadCssResByPageContent"
		table="AP_RESES a, AP_RES_DIRS b, AP_CONTENTS cont, AP_BPAGE_CONTS page"
		where="a.resdirid = b.resdirid and a.officialflag = b.officialflag and b.resdirid = cont.resdirid and b.officialflag = cont.officialflag and cont.contentid = page.contentid and cont.officialflag = page.officialflag and a.contenttype = 'text/css'"
		tablealias="a">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResDirID" column="pagecontid" tablealias="page" />
		</key>
		<get>
			<property name="m_lResDirID" column="contentid" tablealias="cont" />
		</get>
	</retrievemulti>

	<delete id="deleteInDir">
		<key>
			<property name="m_lResDirID" />
		</key>
	</delete>

	<delete id="deleteByDirAndName" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lResDirID" />
			<property name="fileName" />
		</key>
	</delete>
</adb>