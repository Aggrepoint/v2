<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_PATH_MAPS" timestamp="UPDATETIME" listsynctimeout="10">
	<propdef property="m_lMapID" column="MapID" primary="yes"
		sequence="S_AP_PATH_MAPS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lBranchID" column="BranchID" />
	<propdef property="m_strFromPath" column="FromPath" />
	<propdef property="m_strToPath" column="ToPath" />
	<propdef property="m_strFromLink" column="FromLink" />
	<propdef property="m_strToLink" column="ToLink" />
	<propdef property="m_iStatusID" column="StatusID" />
	<propdef property="m_strParamName1" column="ParamName1" />
	<propdef property="m_strParamName2" column="ParamName2" />
	<propdef property="m_strParamName3" column="ParamName3" />
	<propdef property="m_strParamName4" column="ParamName4" />
	<propdef property="m_strParamName5" column="ParamName5" />
	<propdef property="m_strUUID" column="UUID" />

	<retrievemulti id="loadByBranch">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lBranchID" />
		</key>
		<order id="order" value="FROMPATH ASC" />
	</retrievemulti>

	<update id="updateStatus" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lMapID" />
		</key>
		<set>
			<property name="m_iStatusID" />
		</set>
	</update>
</adb>