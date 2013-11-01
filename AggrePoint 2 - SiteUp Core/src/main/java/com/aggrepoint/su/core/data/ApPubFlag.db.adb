<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_PUB_FLAGS" timestamp="UPDATETIME">
	<propdef property="m_iDocTypeID" column="DocTypeID" primary="yes" />
	<propdef property="m_lDocID" column="DocID" primary="yes" />
	<propdef property="m_strServerName" column="ServerName"
		primary="yes" />
	<propdef property="m_iToRevoke" column="ToRevoke" />
	<propdef property="m_tDocTime" column="DocTime" />

	<update id="setRevoke" ondefault="no">
		<key>
			<property name="m_iDocTypeID" />
			<property name="m_lDocID" />
		</key>
		<set>
			<property name="m_iToRevoke" />
		</set>
	</update>

	<delete id="clearFlag" ondefault="no">
		<key>
			<property name="m_iDocTypeID" />
			<property name="m_lDocID" />
		</key>
	</delete>

	<delete id="deleteByServer" ondefault="no">
		<key>
			<property name="m_strServerName" />
		</key>
	</delete>
</adb>