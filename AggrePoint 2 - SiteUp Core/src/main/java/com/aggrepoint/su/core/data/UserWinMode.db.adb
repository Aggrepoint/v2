<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_USER_WIN_MODES" timestamp="UPDATETIME">
	<propdef property="userID" primary="yes" />
	<propdef property="pid" primary="yes" />
	<propdef property="areaName" primary="yes" />
	<propdef property="iid" primary="yes" />
	<propdef property="mode" column="WinMode" />

	<retrievemulti id="loadByUser">
		<key>
			<property name="userID" />
		</key>
	</retrievemulti>
</adb>
