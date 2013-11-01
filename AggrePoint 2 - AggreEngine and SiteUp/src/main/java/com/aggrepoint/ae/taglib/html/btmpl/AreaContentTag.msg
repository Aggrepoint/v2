<?xml version='1.0' encoding='UTF-8'?>

<boundle>
	<msg name="style(url)">
		<text lsid="*"><![CDATA[<link rel="stylesheet" id="css" type="text/css" href="(url)" />]]></text>
	</msg>

	<msg name="script(url)">
		<text lsid="*"><![CDATA[<script language="javascript" src="(url)"></script>]]></text>
	</msg>

	<msg name="ajax_changeMode(area, iid, mode)">
		<text lsid="*">javascript:AeJSEngine.changeWindowMode(('(area)', (iid), (mode))</text>
	</msg>

	<msg name="noneajax_changeMode(area, iid, mode)">
		<text lsid="*">?_w=(iid)&amp;_m=(mode)#win(iid)</text>
	</msg>

	<msg name="areaStart(areaname)">
		<text lsid="*"><![CDATA[<div class="ae_area menuid#area# areaname#(areaname)#"><div class="opac"></div><div class="ae_border"></div><div class="solid">]]></text>
	</msg>

	<msg name="areaEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>

	<msg name="areaEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>

	<msg name="ajax_areaStart(areaname)">
		<text lsid="*"><![CDATA[<div id="ap_area_(areaname)">]]></text>
	</msg>

	<msg name="ajax_areaEnd">
		<text lsid="*"><![CDATA[</div>]]></text>
	</msg>

	<msg name="subZoneStart(container, zoneid)">
		<text lsid="*"><![CDATA[<div class="ae_zone menuid#subzone# container#(container)# zoneid#(zoneid)#"><div class="opac"></div><div class="ae_border"></div><div class="solid uitype#area#">]]></text>
	</msg>

	<msg name="subZoneEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>

	<msg name="zoneStart(areaname, zoneid)">
		<text lsid="*"><![CDATA[<div class="ae_zone menuid#zone# areaname#(areaname)# zoneid#(zoneid)#"><div class="opac"></div><div class="ae_border"></div><div class="solid uitype#area#">]]></text>
	</msg>

	<msg name="zoneEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>

	<msg name="winStart(pcontid)">
		<text lsid="*"><![CDATA[<div class="ae_window menuid#win# pcontid#(pcontid)# uitype#cont# uitype#drag#"><div class="opac"></div><div class="ae_border"></div><div class="solid">]]></text>
	</msg>

	<msg name="winEnd">
		<text lsid="*"><![CDATA[<div style="clear:both"></div></div></div>]]></text>
	</msg>

	<!-- 
	<msg name="winEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>
	 -->

	<msg name="ajax_winStart(pcontid, pop)">
		<text lsid="*"><![CDATA[<div id="ap_win_(pcontid)" pop="(pop)">]]></text>
	</msg>

	<msg name="ajax_winEnd">
		<text lsid="*"><![CDATA[</div>]]></text>
	</msg>

	<msg name="ajax_contStart(pcontid)">
		<text lsid="*"><![CDATA[<div id="ap_cont_(pcontid)">]]></text>
	</msg>

	<msg name="ajax_contEnd">
		<text lsid="*"><![CDATA[</div>]]></text>
	</msg>

	<msg name="contStart(pcontid, contid, menu)">
		<text lsid="*"><![CDATA[<div class="ae_content menuid#(menu)# pcontid#(pcontid)# contid#(contid)# uitype#cont# uitype#drag#"><div class="opac"></div><div class="ae_border"></div><div class="solid">]]></text>
	</msg>

	<msg name="contEnd">
		<text lsid="*"><![CDATA[<div style="clear:both"></div></div></div>]]></text>
	</msg>

	<!--
	<msg name="contEnd">
		<text lsid="*"><![CDATA[</div></div>]]></text>
	</msg>
	-->
</boundle>