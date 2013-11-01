<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_IMAGES" timestamp="UPDATETIME">
	<propdef property="m_lImageID" column="IMAGEID" primary="yes" sequence="S_AP_IMAGES" />
	<propdef property="m_iImageType" column="ImageType" />
	<propdef property="m_strImageName" column="ImageName" />
	<propdef property="m_strContentType" column="ImageContType" />
	<propdef property="m_strImage" column="Image" blob="yes" r="no" rm="no" />
	<propdef property="m_iWidth" column="ImageWidth" />
	<propdef property="m_iHeight" column="ImageHeight" />
	<propdef property="m_iUserType" column="UserType" />

	<retrieve id="loadImage" ondefault="no">
		<key>
			<property name="m_lImageID" />
		</key>
		<get>
			<property name="m_strImage" />
		</get>
	</retrieve>

	<proc id="deleteUnused" atom="yes">
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 15 and not exists (select * from AP_SITES where SITELOGOID = A.IMAGEID)</stmt>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 11 and not exists (select * from AP_TEMPLATES where PREVIEWIMGID = A.IMAGEID)</stmt>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 16 and not exists (select * from AP_LAYOUTS where PREVIEWIMGID = A.IMAGEID)</stmt>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 17 and not exists (select * from AP_FRAMES where PREVIEWIMGID = A.IMAGEID)</stmt>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 13 and not exists (select * from AP_APPS where LOGOIMAGEID = A.IMAGEID)</stmt>
		</sql>
		<sql>
			<stmt>delete from AP_IMAGES A where IMAGETYPE = 14 and not exists (select * from AP_WINDOWS where PREVIEWIMAGEID = A.IMAGEID)</stmt>
		</sql>
	</proc>
</adb>