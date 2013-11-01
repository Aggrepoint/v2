<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE adb SYSTEM "adb_db.dtd">

<adb table="AP_TEMPLATES" timestamp="UPDATETIME" synctimeout="10">
	<propdef property="m_lTemplateID" column="TemplateID" primary="yes"
		sequence="S_AP_TEMPLATES" />
	<propdef property="m_iOfficialFlag" column="OfficialFlag"
		primary="yes" />
	<propdef property="m_bPubFlag" column="PubFlag" />
	<propdef property="m_lSiteID" column="SiteID" />
	<propdef property="m_lDefResDirID" column="DefResDirID" />
	<propdef property="m_lResDirID" column="ResDirID" null="0@BIGINT" />
	<propdef property="m_iTmplType" column="TmplType" />
	<propdef property="m_lPreviewImgID" column="PreviewImgID" null="0@BIGINT" />
	<propdef property="m_strTmplName" column="TmplName" />
	<propdef property="m_iOrder" column="SOrder" />
	<propdef property="m_strContent" column="Content" r="no" rm="no" clob="yes" />
	<propdef property="m_iMarkupType" column="MarkupType" />
	<propdef property="m_strAreas" column="AREAS" />
	<propdef property="m_strUUID" column="UUID" />
	<sublist property="params" type="ApTmplParam"
		rmid="loadByTemplate">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lTemplateID" ref="m_lTemplateID" />
	</sublist>
	<sublist property="bpages" type="ApBPage"
		rmid="loadByTemplate">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lTemplateID" ref="m_lTemplateID" />
	</sublist>
	<sublist property="bpagepsntmpls" type="ApBPagePsnTmpl"
		rmid="loadByTemplate">
		<propref this="m_iOfficialFlag" ref="m_iOfficialFlag" />
		<propref this="m_lTemplateID" ref="m_lTemplateID" />
	</sublist>

	<retrieve id="loadInSite">
		<key>
			<property name="m_lSiteID" />
		</key>
	</retrieve>

	<retrieve id="loadResDirWithCache" cache="200" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lTemplateID" />
		</key>
		<get>
			<property name="m_lDefResDirID" />
			<property name="m_lResDirID" />
		</get>
	</retrieve>

	<retrieve id="loadBasicByOwnerAndName" ondefault="no">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_strTmplName" />
			<property name="m_lSiteID" />
		</key>
		<get>
			<property name="m_lTemplateID" />
			<property name="m_iTmplType" />
		</get>
	</retrieve>

	<retrieve id="loadDetail">
		<get>
			<property name="m_strContent" />
		</get>
	</retrieve>

	<retrieve id="loadWithParams">
		<get>
			<property name="params" />
		</get>
	</retrieve>

	<retrieve id="loadAll">
		<get>
			<property name="m_strContent" />
			<property name="params" />
		</get>
	</retrieve>

	<retrieve id="loadAllExtra" ondefault="no">
		<key>
			<property name="m_lTemplateID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_strContent" />
			<property name="params" />
		</get>
	</retrieve>

	<retrieve id="loadWithRefs">
		<get>
			<property name="bpages" />
			<property name="bpagepsntmpls" />
		</get>
	</retrieve>

	<retrieve id="loadUUIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_lTemplateID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_strUUID" />
			<property name="m_lSiteID" />
		</get>
	</retrieve>

	<retrieve id="loadIDWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_strUUID" />
			<property name="m_iOfficialFlag" />
		</key>
		<get>
			<property name="m_lTemplateID" />
		</get>
	</retrieve>

	<retrieve id="loadIDAndTypeWithCache" ondefault="no" cache="200">
		<key>
			<property name="m_strUUID" />
		</key>
		<get>
			<property name="m_lTemplateID" />
			<property name="m_iTmplType" />
		</get>
	</retrieve>

	<retrievemulti id="loadByType">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
			<property name="m_iTmplType" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<!-- 加载全局站点模板列表 -->
	<retrievemulti id="loadGlobalSite" where="TMPLTYPE = 0 and SITEID = 100">
		<key>
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<!-- 加载全局栏目模板列表 -->
	<retrievemulti id="loadGlobalChannel" where="TMPLTYPE = 1 and SITEID = 100">
		<key>
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<!-- 加载全局列表模板列表 -->
	<retrievemulti id="loadGlobalList" where="TMPLTYPE = 2 and SITEID = 100">
		<key>
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<!-- 加载站点中所有模板 -->
	<retrievemulti id="loadByOwner">
		<key>
			<property name="m_iOfficialFlag" />
			<property name="m_lSiteID" />
		</key>
		<order id="order" value="TmplType, SOrder, TemplateID" />
	</retrievemulti>

	<!-- 加载站点级站点模板列表 -->
	<retrievemulti id="loadSiteSite" where="TMPLTYPE = 0">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder, TmplName" />
	</retrievemulti>

	<retrievemulti id="loadSiteSiteForSel" where="TMPLTYPE = 0" ondefault="no">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
			<property name="m_iMarkupType" />
		</key>
		<get>
			<property name="m_lTemplateID" />
			<property name="m_strTmplName" />
		</get>
		<order id="order" value="SOrder, TmplName" />
	</retrievemulti>

	<!-- 加载站点级栏目模板列表 -->
	<retrievemulti id="loadSiteChannel" where="TMPLTYPE = 1">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<!-- 加载站点级列表模板列表 -->
	<retrievemulti id="loadSiteList" where="TMPLTYPE = 2">
		<key>
			<property name="m_lSiteID" />
			<property name="m_iOfficialFlag" />
		</key>
		<order id="order" value="SOrder" />
	</retrievemulti>

	<proc id="delete" atom="yes">
		<sql>
			<stmt>delete from AP_IMAGES where imageid=?</stmt>
			<key>
				<property name="m_lPreviewImgID" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_TEMPLATES where templateid=? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lTemplateID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
		<sql>
			<stmt>delete from AP_RES_DIRS where ResDirID=? and OfficialFlag=?</stmt>
			<key>
				<property name="m_lDefResDirID" />
				<property name="m_iOfficialFlag" />
			</key>
		</sql>
	</proc>
</adb>