<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_CPAGE_KWRDS" timestamp="UPDATETIME">
	<propdef property="m_lKeywordID" column="KeywordID" primary="yes" />
	<propdef property="m_lPageID" column="pageID" primary="yes" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />

	<proc id="deleteByPage" atom="yes">
		<sql>
			<stmt>delete from AP_CPAGE_KWRDS where pageid = ? and officialflag = ?</stmt>
			<key>
				<property name="m_lPageID" />
			<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt syntax="mysql">delete from AP_KEYWORDS where keywordid not in (select keywordid from AP_CPAGE_KWRDS)</stmt>
			<stmt>delete from AP_KEYWORDS a where not exists(select * from AP_CPAGE_KWRDS b where a.keywordid = b.keywordid)</stmt>
		</sql>
	</proc>
</adb>
