var AeEdit = {
	R_MENUID : new RegExp('menuid#(.+?)#'),
	R_PAGEID : new RegExp('pid#(.+?)#'),
	R_PAGENAME : new RegExp('pname#(.+?)#'),
	R_PCONTID : new RegExp('pcontid#(.+?)#'),
	R_CONTID : new RegExp(' contid#(.+?)#'),
	R_AREANAME : new RegExp('areaname#(.+?)#'),
	R_ZONEID : new RegExp('zoneid#(.+?)#'),
	R_CONTAINER : new RegExp('container#(.+?)#'),
	ANIMATE_SHOW: true,
	animation: true,

	get: function(elm, exp) {
		var m = elm.className.match(exp);
		if (m == null)
			return null;
		return m[1];
	},

	getMenu: function (elm) {
		while(elm) {
			if (elm.className) {
				var m = elm.className.match(this.R_MENUID);
				if (m != null) {
					this.m_pid = this.get(elm, this.R_PAGEID);
					this.m_pname = this.get(elm, this.R_PAGENAME);
					this.m_pcontid = this.get(elm, this.R_PCONTID);
					this.m_contid = this.get(elm, this.R_CONTID);
					this.m_areaname = this.get(elm, this.R_AREANAME);
					this.m_zoneid = this.get(elm, this.R_ZONEID);
					this.m_container = this.get(elm, this.R_CONTAINER);
	
					return m[1];
				}
			}
			elm = $(elm).parent()[0];
		}
		return null;
	},

	highlight: function(elm) {
		if (elm == null || this.m_elmHighlight == elm)
			return;

		if (this.m_highlight) {
			if (this.m_highlight.hasClass("ae_page"))
				this.m_highlight.removeClass("ae_page_highlight");
			else {
				this.m_highlight.removeClass("ae_border_highlight");
				this.m_highlight.addClass("ae_border");
			}
		}

		this.m_elmHighlight = elm;
		this.m_highlight = $(elm);
		if (this.m_highlight.hasClass("ae_page"))
			this.m_highlight.addClass("ae_page_highlight");
		else {
			this.m_highlight.removeClass("ae_border");
			this.m_highlight.addClass("ae_border_highlight");
		}
	},

	animate: function() {
		if (AeEdit.ANIMATE_SHOW) {
			$(".opac").animate({opacity: 0.8}, 2000, function() {});
			AeEdit.ANIMATE_SHOW = false;
			window.setTimeout(AeEdit.animate, 4000);
		} else {
			if (AeEdit.animation) {
				$(".opac").animate({opacity: 0}, 2000, function() {});
				AeEdit.ANIMATE_SHOW = true;
				window.setTimeout(AeEdit.animate, 4000);
			} else
				window.setTimeout(AeEdit.animate, 1000);
		}
	},

	init: function() {
		$(".ae_area").each(function(i) {
			var elm = $(this);
			elm.css("width", elm.outerWidth() + 32 + "px");
		});

		$(document.body).append(
			$('<span id="ae_siteup_menu_page_1"></span>').adkcmenu({
				options: [{"type": "label", "value": "", "label": "根页面"},
						{"type": "option", "value": "new_sub", "logo": "new.gif", "label": "新建子页面"}], 
				iconRoot: "/ap2/su/images/",
				onSelect: function(id){AeEdit.pageAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_page_2"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "页面"},
						{"type": "option", "value": "move_left", "logo": "left.gif", "label": "前移", "tip": "将页面的位置提前一位"},
						{"type": "option", "value": "move_right", "logo": "right.gif", "label": "后移", "tip": "将页面的位置移后一位"},
						{"type": "sep"},
						{"type": "option", "value": "edit_psn_name", "logo": "edit.gif", "label": "设置个性化名称", "tip": "设置页面的个性化名称"},
						{"type": "option", "value": "edit_psn_tmpl", "logo": "edit.gif", "label": "设置个性化模板", "tip": "设置页面的个性化模板"},
						{"type": "sep"},
						{"type": "option", "value": "edit", "logo": "edit.gif", "label": "编辑", "tip": "编辑页面的属性"},
						{"type": "option", "value": "new_sub", "logo": "edit.gif", "label": "新建子页面", "tip": "在选定页面下新建页面"},
						{"type": "option", "value": "del", "logo": "del.gif", "label": "删除", "tip": "删除页面"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.pageAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_page_3"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "页面"},
						{"type": "option", "value": "move_left", "logo": "left.gif", "label": "前移", "tip": "将页面的位置提前一位"},
						{"type": "option", "value": "move_right", "logo": "right.gif", "label": "后移", "tip": "将页面的位置移后一位"},
						{"type": "sep"},
						{"type": "option", "value": "edit", "logo": "edit.gif", "label": "编辑", "tip": "编辑页面的属性"},
						{"type": "option", "value": "del", "logo": "del.gif", "label": "删除", "tip": "删除页面"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.pageAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_page_4"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "页面"},
						{"type": "option", "value": "new_sub", "logo": "new.gif", "label": "新建子页面"}, 
						{"type": "sep"},
						{"type": "option", "value": "edit_psn_name", "logo": "edit.gif", "label": "设置个性化名称", "tip": "设置页面的个性化名称"},
						{"type": "option", "value": "edit_psn_tmpl", "logo": "edit.gif", "label": "设置个性化模板", "tip": "设置页面的个性化模板"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.pageAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_page_5"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "页面"},
						{"type": "option", "value": "edit_psn_name", "logo": "edit.gif", "label": "设置个性化名称", "tip": "设置页面的个性化名称"},
						{"type": "option", "value": "edit_psn_tmpl", "logo": "edit.gif", "label": "设置个性化模板", "tip": "设置页面的个性化模板"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.pageAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_cont"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "内容"},
						{"type": "option", "value": "edit_psn_rule", "logo": "edit.gif", "label": "设置个性化规则", "tip": "设置内容的个性化访问规则"},
						{"type": "sep"},
						{"type": "option", "value": "move_left", "logo": "left.gif", "label": "前移", "tip": "将内容的位置提前一位"},
						{"type": "option", "value": "move_right", "logo": "right.gif", "label": "后移", "tip": "将内容的位置移后一位"},
						{"type": "sep"},
						{"type": "option", "value": "del", "logo": "del.gif", "label": "删除", "tip": "删除内容"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.contAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_cont_page"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "内容"},
						{"type": "option", "value": "edit", "logo": "edit.gif", "label": "编辑", "tip": "编辑内容的属性"},
						{"type": "sep"},
						{"type": "option", "value": "move_left", "logo": "left.gif", "label": "前移", "tip": "将内容的位置提前一位"},
						{"type": "option", "value": "move_right", "logo": "right.gif", "label": "后移", "tip": "将内容的位置移后一位"},
						{"type": "sep"},
						{"type": "option", "value": "del", "logo": "del.gif", "label": "删除", "tip": "删除内容"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.contAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_win"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "窗口"},
						{"type": "option", "value": "edit", "logo": "edit.gif", "label": "编辑", "tip": "编辑窗口的属性"},
						{"type": "sep"},
						{"type": "option", "value": "move_left", "logo": "left.gif", "label": "前移", "tip": "将窗口的位置提前一位"},
						{"type": "option", "value": "move_right", "logo": "right.gif", "label": "后移", "tip": "将窗口的位置移后一位"},
						{"type": "sep"},
						{"type": "option", "value": "del", "logo": "del.gif", "label": "删除", "tip": "删除窗口"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.winAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_area"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "栏位"},
						{"type": "option", "value": "layout", "logo": "edit.gif", "label": "设置布局", "tip": "设置当前栏位的布局方式"},
						{"type": "option", "value": "newcont", "logo": "new.gif", "label": "创建内容", "tip": "在当前栏位内创建内容"},
						{"type": "option", "value": "addcont", "logo": "new.gif", "label": "选择插入内容", "tip": "在当前栏位内插入已有内容"},
						{"type": "option", "value": "addwin", "logo": "new.gif", "label": "添加窗口", "tip": "在当前栏位内添加窗口"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.areaAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_zone"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "区域"},
						{"type": "option", "value": "newcont", "logo": "new.gif", "label": "创建内容", "tip": "在当前区域内创建内容"},
						{"type": "option", "value": "addcont", "logo": "new.gif", "label": "选择插入内容", "tip": "在当前区域内插入已有内容"},
						{"type": "option", "value": "addwin", "logo": "new.gif", "label": "添加窗口", "tip": "在当前区域内添加窗口"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.zoneAction(id)}}));

		$(document.body).append(
				$('<span id="ae_siteup_menu_subzone"></span>').adkcmenu({
					options: [{"type": "label", "value": "", "label": "子区域"},
						{"type": "option", "value": "newcont", "logo": "new.gif", "label": "创建内容", "tip": "在当前区域内创建内容"},
						{"type": "option", "value": "addcont", "logo": "new.gif", "label": "选择插入内容", "tip": "在当前区域内插入已有内容"},
						{"type": "option", "value": "addwin", "logo": "new.gif", "label": "添加窗口", "tip": "在当前区域内添加窗口"}], 
					iconRoot: "/ap2/su/images/",
					onSelect: function(id){AeEdit.subZoneAction(id)}}));

		$(".ae_area, .ae_zone, .ae_window, .ae_content").mouseover(function() {
			AeEdit.highlight($(this).find(".ae_border")[0]); return false;
		});
		$(".ae_page").mouseover(function() {
			AeEdit.highlight(this); return false;
		});

		$(document).bind("contextmenu",function(e){
			var menu = AeEdit.getMenu(e.target);
			if (menu == null)
				return false;

			$("#ae_siteup_menu_" + menu).adkcmenu("show", e.pageX, e.pageY);
			return false;
		});

		AeEdit.animate();
	},

	doAction: function(url, param, confirm) {
		if (confirm)
			if (!window.confirm(confirm))
				return;
		param = jQuery.extend(param, {urlback: location.href});
		location.href = url + "?" + jQuery.param(param);
	},

	popWindow: function(url, param, w, h, title, confirm) {
		var d = $('<div title="' + title + '"></div>');
		var f = $('<iframe style="border: 0px" marginheight="0" marginwidth="0" frameborder="0"></iframe>');
		d.append(f);
		var btns = {
				取消: function() {
					$(this).dialog( "close" );
				}
			};
		if (confirm)
			btns['确认'] = function() {
				$($(this).find("iframe")[0].contentDocument).find("form")[0].submit();
			};
		f[0].m_dialog = d.dialog({modal:true, width: w, height: h, resizable: false, buttons: btns});
		f.attr("width", w - 30);
		f.attr("height", h - 120);
		f.attr("src", url + "?" + jQuery.param(param));
	},

	pageAction: function(id) {
		if (id == "move_left")
			this.doAction("/ap2/app/su/bpagemove", {pid: this.m_pid, step: -1});
		else if (id == "move_right")
			this.doAction("/ap2/app/su/bpagemove", {pid: this.m_pid, step: 1});
		else if (id == "new_sub")
			this.popWindow("/ap2/app/su/bpageedit", {ppid: this.m_pid}, 610, 320, '添加页面', true);
		else if (id == "edit")
			this.popWindow("/ap2/app/su/bpageedit", {pid: this.m_pid}, 610, 320, '编辑页面', true);
		else if (id == "del")
			this.doAction("/ap2/app/su/bpagedel", {pid: this.m_pid}, '要删除页面"' + this.m_pname + '"吗？');
		else if (id == "edit_psn_name")
			this.popWindow("/ap2/app/su/psnnameedit", {pid: this.m_pid}, 570, 320, '设置页面个性化名称', true);
		else if (id == "edit_psn_tmpl")
			this.popWindow("/ap2/app/su/psntmpledit", {pid: this.m_pid}, 540, 290, '设置页面个性化模板', true);
	},

	contAction: function(id) {
		if (id == "move_left")
			this.doAction("/ap2/app/su/bpagecontmove", {pcontid: this.m_pcontid, step: -1});
		else if (id == "move_right")
			this.doAction("/ap2/app/su/bpagecontmove", {pcontid: this.m_pcontid, step: 1});
		else if (id == "edit")
			this.popWindow("/ap2/app/su/contedit", {contid: this.m_contid}, 950, 630, '编辑内容');
		else if (id == "del")
			this.doAction("/ap2/app/su/bpagecontdel", {pcontid: this.m_pcontid}, '要删除选定的内容吗？');
	},

	winAction: function(id) {
		if (id == "move_left")
			this.doAction("/ap2/app/su/bpagecontmove", {pcontid: this.m_pcontid, step: -1});
		else if (id == "move_right")
			this.doAction("/ap2/app/su/bpagecontmove", {pcontid: this.m_pcontid, step: 1});
		else if (id == "edit")
			this.popWindow("/ap2/app/su/winedit", {pcontid: this.m_pcontid}, 800, 600, '编辑窗口');
		else if (id == "del")
			this.doAction("/ap2/app/su/bpagecontdel", {pcontid: this.m_pcontid}, '要删除选定的窗口吗？');
	},

	areaAction: function(id) {
		if (id == "layout")
			this.popWindow("/ap2/app/su/setlayout", {pid: window.siteup_pageID, areaname: this.m_areaname}, 820, 380, '选择布局', true);
		else if (id == "newcont")
			this.popWindow("/ap2/app/su/contedit", {pid: window.siteup_pageID, areaname: this.m_areaname}, 950, 630, '新建内容');
		else if (id == "addcont")
			this.popWindow("/ap2/app/su/contins_selcat", {pid: window.siteup_pageID, areaname: this.m_areaname}, 600, 480, '添加内容');
		else if (id == "addwin")
			this.popWindow("/ap2/app/su/winins_sel", {pid: window.siteup_pageID, areaname: this.m_areaname}, 800, 600, '添加窗口');
	},

	zoneAction: function(id) {
		if (id == "newcont")
			this.popWindow("/ap2/app/su/contedit", {pid: window.siteup_pageID, areaname: this.m_areaname, zoneid: this.m_zoneid}, 950, 630, '新建内容');
		else if (id == "addcont")
			this.popWindow("/ap2/app/su/contins_selcat", {pid: window.siteup_pageID, areaname: this.m_areaname, zoneid: this.m_zoneid}, 600, 480, '添加内容');
		else if (id == "addwin")
			this.popWindow("/ap2/app/su/winins_sel", {pid: window.siteup_pageID, areaname: this.m_areaname, zoneid: this.m_zoneid}, 800, 600, '添加窗口');
	},

	subZoneAction: function(id) {
		if (id == "newcont")
			this.popWindow("/ap2/app/su/contedit", {pid: window.siteup_pageID, container: this.m_container, zoneid: this.m_zoneid}, 950, 630, '新建内容');
		else if (id == "addcont")
			this.popWindow("/ap2/app/su/contins_selcat", {pid: window.siteup_pageID, container: this.m_container, zoneid: this.m_zoneid}, 600, 480, '添加内容');
		else if (id == "addwin")
			this.popWindow("/ap2/app/su/winins_sel", {pid: window.siteup_pageID, container: this.m_container, zoneid: this.m_zoneid}, 800, 600, '添加窗口');
	}
};
