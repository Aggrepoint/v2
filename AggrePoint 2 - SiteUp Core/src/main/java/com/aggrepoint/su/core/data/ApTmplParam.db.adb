<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_TMPL_PARAMS" timestamp="UPDATETIME">
	<propdef property="m_lTmplParamID" column="TmplParamID" primary="yes" sequence="S_AP_TMPL_PARAMS" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag" primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lTemplateID" column="TemplateID" />
	<propdef property="m_strParamName" column="ParamName" />
	<propdef property="m_strParamDesc" column="ParamDesc" />
	<propdef property="m_strDefaultValue" column="DefaultValue" />

	<retrievemulti id="loadByTemplate">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lTemplateID" />
		</key>
		<order id="order" value="ParamName" />
	</retrievemulti>
	
	<delete id="deleteByTmpl">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lTemplateID" />
		</key>
	</delete>
</adb>