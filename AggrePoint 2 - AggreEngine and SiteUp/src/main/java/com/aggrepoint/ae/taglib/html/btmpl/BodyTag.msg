<?xml version='1.0' encoding='UTF-8'?>

<boundle>
	<msg name="edit_start_root(pageid)">
		<text lsid="*"><![CDATA[
<span class="menuid#page_1# pid#(pageid)#">
		]]></text>
	</msg>

	<msg name="edit_start_not_root">
		<text lsid="*"><![CDATA[
<span>
		]]></text>
	</msg>

	<msg name="edit_end">
		<text lsid="*"><![CDATA[
</span>
<script language="javascript">
$(document).ready(function() {
AeEdit.init();
AeEditDrag.init(function(cont, location) {
	var area = $(location.area).parents(".ae_zone")[0];
	if (area == null)
		return;

	if (location.content != null)
		AeEdit.doAction("/ap2/app/su/bpagecontdrag", {
			pcontid: AeEdit.get(cont, AeEdit.R_PCONTID),
			areaname: AeEdit.get(area, AeEdit.R_AREANAME),
			zoneid: AeEdit.get(area, AeEdit.R_ZONEID),
			container: AeEdit.get(area, AeEdit.R_CONTAINER),
			slotid: AeEdit.get(area, AeEdit.R_SLOTID),
			oncontid: AeEdit.get(location.content, AeEdit.R_PCONTID),
			isbefore: location.before ? "1" : "0"});
	else
		AeEdit.doAction("/ap2/app/su/bpagecontdrag", {
			pcontid: AeEdit.get(cont, AeEdit.R_PCONTID),
			areaname: AeEdit.get(area, AeEdit.R_AREANAME),
			zoneid: AeEdit.get(area, AeEdit.R_ZONEID),
			container: AeEdit.get(area, AeEdit.R_CONTAINER),
			slotid: AeEdit.get(area, AeEdit.R_SLOTID)});
}, function() {
	AeEdit.animation = false;
}, function() {
	AeEdit.animation = true;
});
});
</script>
		]]></text>
	</msg>
</boundle>