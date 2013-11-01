/******************************************************************************
*
*  级联菜单
*  
*  构造参数：
*  	options			选项数组
*  	iconRoot		图标根URL
*  	allowSelSub		是否允许选择带子选项的选项
*  	className		使用样式名称
*  	onSelect		选项选中时调用的方法，传入唯一参数为被选中选项的值
*  	onHide			菜单隐藏时调用的方法，无参数
*  
 * 选项属性：
 * 	type 	类型，包括sep，label和option
 *  label	选项名称
 *  value	选项值
 *  logo	图标
 *  tip		提示信息
 *  sub		子选项数组
*  
*  Jim Yang, 20091222
*
******************************************************************************/
jQuery.fn.adkcmenu = function() {
	var args = arguments;

	if (arguments[0] == 'show') {
		return this.each(function() {
			if (this.m_cascadedMenu) {
				if (args.length == 3)
					this.m_cascadedMenu.show(args[1], args[2]);
				else
					this.m_cascadedMenu.show();
			}
		});
	} else if (arguments[0] == 'onselect') {
		return this.each(function() {
			this.m_cascadedMenu.onSelect = args[1];
		});
	} else if (arguments[0] == 'onhide') {
		return this.each(function() {
			this.m_cascadedMenu.onHide = args[1];
		});
	} else {
		var settings = jQuery.extend({allowSelSub: false, className: 'cmenu'}, arguments[0]);

		return this.each(function() {
			IBCMenu.attach(this, settings.options, settings.iconRoot, settings.allowSelSub, settings.className, settings.onSelect, settings.onHide);
		});
	}
};

/******************************************************************************
*
*  Icon Select
*  Jim Yang, 20091222
*
******************************************************************************/
jQuery.fn.adkiconsel = function() {
	var settings = jQuery.extend({allowSelSub: true, className: 'cmenu'}, arguments[0]);

	return this.each(function() {
		if (this.m_input)
			return;

		this.updateIconSel = function(val) {
			var opt = this.m_cascadedMenu.getOptionByValue(null, val);
			if (opt != null) {
				var str = "";
				while (opt != null && (opt.type == "option" || str != "" && opt.type == "sub")) {
					if (str != "")
						str = " - " + str;
					if (opt.logo == null)
						str = opt.label + str;
					else if (this.m_cascadedMenu.m_iconRoot != null && this.m_cascadedMenu.m_iconRoot != "")
						str = '<img border=0 align=absmiddle src="' + this.m_cascadedMenu.m_iconRoot + opt.logo + '"> ' + opt.label + str;
					else
						str = '<img border=0 align=absmiddle src="' + opt.logo + '"> ' + opt.label + str;
					opt = opt.oparent;
				}
				if (str != "")
					this.m_select.html(str);
			}
		};

		this.iconSelSelected = function(iconsel) {
			return function(val) {
				iconsel.updateIconSel(val);
				if (iconsel.m_input != null)
					iconsel.m_input.value = val;
				iconsel.m_cascadedMenu.hide();
				if (iconsel.onchange != null)
					iconsel.onchange(val);
			}
		},

		this.onHide = function(settings) {
			return function() {
				if (settings.onHide != null)
					settings.onHide();
			}
		},

		$(this).after('<span style="line-height: ' + this.offsetHeight + 'px">&nbsp;</span>');
		this.m_input = settings.input;
		if (this.m_input != null)
			this.m_input.value = settings.value;
		$(this).html('<span class="adk_iconsel"></span>');
		this.m_select = $(this).find("span.adk_iconsel");
		IBCMenu.attach(this, settings.options, settings.iconRoot, settings.allowSelSub, settings.className, this.iconSelSelected(this), this.onHide);
		this.updateIconSel(settings.value);
		$(this).mousedown(function(){this.m_cascadedMenu.show()});
	});
}

/******************************************************************************
*
*  可编辑Table
*
*  Jim Yang, 20091222
*
******************************************************************************/

jQuery.fn.adktable = function() {
	var settings = arguments[0];

	return this.each(function() {
		if (this.m_tablesetting)
			return;

		var target = $(this);

		this.m_tablesetting = settings;
		target.addClass("adk_table");

		// { process icon select definitions
		if (settings.lists)
			for (var i = 0; i < settings.lists.length; i++) {
				var menu = $('<div></div>');
				target.after(menu);
				menu.adkcmenu(settings.lists[i]);
				if (this.m_menus == null)
					this.m_menus = {};
				this.m_menus[settings.lists[i].name] = menu[0];
			}
		// }

		// { process row button definitions
		if (settings.btns)
			for (var i = 0; i < settings.btns.length; i++) {
				if (this.m_btns == null)
					this.m_btns = {};
				this.m_btns[settings.btns[i][0]] = {func: settings.btns[i][1], icon: settings.btns[i][2], title: settings.btns[i][3], confirm: settings.btns[i][4], link: settings.btns[i][5]};
			}
		//}

		// { decorate header
		var headers = target.find("thead tr").find("th");
		for (var i = 0; i < headers.length; i++) {
			headers[i].noWrap = true;
			headers[i].m_table = this;

			if (settings.cols[i].w)
				headers[i].Width = settings.cols[i].w;

			// current sorting direction
			if (settings.cols[i].so)
				$(headers[i]).contents().wrapAll('<span class="' + settings.cols[i].so + '"></span>');

			// sortable
			if (settings.cols[i].s) {
				$(headers[i]).addClass("sortable");
				headers[i].m_sid = settings.cols[i].s;
				headers[i].onclick = function() {settings.sortFunc(this.m_sid)};
			}
		}

		var sortable = target.find("thead tr").find("th.sortable"); 
		sortable.mouseover(function(){$(this).addClass("sortable_over")});
		sortable.mouseout(function(){$(this).removeClass("sortable_over")});
		sortable.mousedown(function(){$(this).addClass("sortable_down")});
		sortable.mouseup(function(){$(this).removeClass("sortable_down")});
		// }

		// { decorate rows
		var rows = target.find("tbody tr");
		
		for (var i = 0; i < rows.length; i++) {
			var row = $(rows[i]);

			// row click
			if (settings.rows && settings.rows[i].link)
				row.click(IBUtils.invokeFunc(settings.rows[i].link, settings.rows[i].id));

			// row class
			if (settings.rows && settings.rows[i].clz)
				target.addClass(settings.rows[i].clz);

			var cols = $(rows[i]).find("td");
			var idx = 0;
			for (var j = 0; j < cols.length; j++) {
				var jcol = $(cols[j]);

				// { decorate td content
				if (settings.cols[j].nw == 'y')
					cols[j].noWrap = true;
				if (settings.cols[j].w != undefined)
					cols[j].width = settings.cols[j].w;
				if (settings.cols[j].btn == 'y') {
					// generate row icon buttons
					if (settings.rows[i].btns) {
						var html = '';
						for (var k = 0; k < settings.rows[i].btns.length; k++) {
							var btn = this.m_btns[settings.rows[i].btns[k]];
							if (btn) {
								html += '<button title="'
									+ btn.title
									+ '" class="adk_btn2 adk_btnsilver__normal" onclick="';
								if (btn.confirm != null && btn.confirm != '')
									html += "if (!window.confirm('" + btn.confirm + "')) return; ";
								if (btn.link != undefined && btn.link != '')
									html += "location.href = '" + btn.link + "&id=" + settings.rows[i].id + "'";
								else
									html += btn.func + '(' + settings.rows[i].id + ')';
								html += '" type="button"><img align="absmiddle" border="0" src="'
									+ btn.icon
									+ '"/></button>';
							}
						}
						if (html != null)
							jcol.html(html);
					}
				} else {
					if (settings.cols[j].link)
						jcol.contents().wrapAll('<a href="javascript:' + settings.cols[j].link + "('" + settings.rows[i].id + "'" + ')"></a>');
					if (settings.cols[j].pre == 'y')
						jcol.contents().wrapAll('<pre></pre>');
					if (settings.cols[j].ov == 'y') {
						var html = '<table width="100%" border="0" class="hideoverflow"><tr><td nowrap="nowrap">' + jcol.html() + '</td></tr></table>';
						jcol.html(html);
					}
				}
				// }

				// { enable in-place edit
				if (settings.cols[j].et) {
					if (settings.cols[j].ev == 'y')
						cols[j].m_editvalue = $(cols[j]).text();
					else {
						cols[j].m_editvalue = settings.conts[i][idx];
						idx++;
					}
					if (cols[j].m_editvalue == null)
						continue;

					cols[j].m_edittype = settings.cols[j].et;
					cols[j].m_editname = settings.cols[j].en;
					cols[j].m_editid = settings.rows[i].id;
					cols[j].m_root = this;

					$(cols[j]).addClass("editable");
					
					if (cols[j].m_edittype == 'iconsel') {
						var posi = cols[j].m_editvalue.indexOf(':');
						cols[j].m_editmenu = cols[j].m_editvalue.substring(0, posi);
						cols[j].m_editvalue = cols[j].m_editvalue.substring(posi + 1);
					}
				}
				// }
			}
		}

		rows.mouseover(function(){$(this).addClass("over")});
		rows.mouseout(function(){$(this).removeClass("over")});

		// handle click on editable td
		target.find("td.editable").click(function(){
			arguments[0].stopPropagation();

			if (this.m_root.m_editing != this)
				this.m_root.editSubmit();

			if (this.m_root.m_editing == null) {
				$(this.parentNode).addClass("editing");
				var target = $(this);

				if (this.m_edittype == 'iconsel') {
					var posi = target.position();
					var menu = $(this.m_root.m_menus[this.m_editmenu]);
					var func = function(root, editing, oldval) {
						return function(val) {
							root.editSubmit(editing, oldval, val);
						}
					};
					var funcOnHide = function(toClear) {
						return function() {
							toClear.removeClass("editing");
						}
					}

					menu.adkcmenu('onselect', func(this.m_root, this, this.m_editvalue));
					menu.adkcmenu('onhide', funcOnHide($(this.parentNode)));
					menu.adkcmenu('show', posi.left, posi.top + this.offsetHeight);
				} else {
					this.m_root.m_editing = this;

					this.editsave = target.html();
					target.html('<input type="text" style="width: ' + (target.innerWidth() - 12) + 'px;" value="' + this.m_editvalue + '">');
					$(function(){target.find("input")[0].select();});

					if (this.m_edittype == 'date')
						$(function(){target.find("input").datepicker({changeMonth: true, changeYear: true}).datepicker('show');});
				}
			}
		});

		// press enter while editing
		target.find("td.editable").keypress(function(event){
			var key = event.keyCode || event.which;
			if (key == 13) {
				event.preventDefault();
				event.stopPropagation();
				this.m_root.editSubmit();
			}
		});
		// }

		this.editSubmit = function() {
			if (arguments.length == 0) {
				if (this.m_editing == null)
					return false;
				if ($(this.m_editing).find("input")[0].value == this.m_editing.m_editvalue) {
					$(this.m_editing).html(this.m_editing.editsave);
					$(this.m_editing.parentNode).removeClass("editing");
					this.m_editing = null;
					return false;
				}

				if (AeJSEngine)
					AeJSEngine.editFocus = $(this.m_editing).find("input")[0];
				this.m_tablesetting.editFunc(this.m_editing.m_editid, this.m_editing.m_editname, $(this.m_editing).find("input")[0].value);
			} else { // iconsel selected
				if (arguments[1] == arguments[2]) {
					this.m_editing = null;
					return false;
				}

			this.m_tablesetting.editFunc(arguments[0].m_editid, arguments[0].m_editname, arguments[2]);
			}
			return true;
		}

		target.click(function(){
			this.editSubmit();
		});
	});
};

/******************************************************************************
*
*  Ajax Validated Form
*  Jim Yang, 20100122
*
******************************************************************************/
jQuery.fn.adkvalform = function() {
	var settings = jQuery.extend({}, arguments[0]);

	return this.each(function() {
		if (this.ajaxValidate)
			return;

		this.ajaxValidate = function(input) {
			if (input.m_result != null)
				$(input.m_result).html('<span class="adk_validating"></span>');
			var val = {value: ''};
			if (input.type == 'checkbox') {
				if (input.checked)
					val.value = input.value;
			} else
				val.value = input.value;
			$.post(AeJSEngine.getUrl(), "_x=y&_a=" + settings.iid + "!" + settings.vid + "!" + settings.action + "!" + settings.form + "&_vf=yes" + "&name=" + input.name + "&" + $.param(val), this.getResponseHandler(input), "xml");
		};

		this.getResponseHandler = function(input) {
			return function(xml) {
				if (input.m_result != null)
					$(input.m_result).html('');

				var changes = null;
				try {
					changes = jQuery.parseJSON(xml.getElementsByTagName("content")[0].firstChild.nodeValue);
				} catch (e) {
				}
				
				if (changes != null) {
					for (var i = 0; i < changes.length; i++) {
						var inp = $(input.form).find(":input[name='" + changes[i].input + "']")[0];
						if (inp == null)
							continue;
						if (changes[i].type == 'v') { // 校验结果
							if (inp.m_result != null)
								if (changes[i].message == '')
									$(inp.m_result).html('<span class="adk_valpassed">&nbsp;</span>');
								else
									$(inp.m_result).html('<div class="adk_valfailed">' + $('<div/>').text(changes[i].message).html() + '</div>');
						} else if (changes[i].type == 'u') { // 更新值
							if (inp.type == 'radio')
								$(input.form).children(":input[name='" + changes[i].input + "'][value='" + changes[i].value + "']").attr('checked', 'checked');
							else if (inp.type == 'checkbox')
								inp.checked = changes[i].value;
							else
								$(inp).val(changes[i].value);
						} else if (changes[i].type == 'd') {
							inp.disabled = true;
							if (inp.m_result != null)
								$(inp.m_result).html('');
						} else if (changes[i].type == 'e') {
							inp.disabled = false;
						} else if (changes[i].type == 'l') { // 更新列表
							if (inp.type = 'select') {
								$(inp).empty();
								for (var j = 0; j < changes[i].list.length; j++)
									$(inp).append('<option value="' + changes[i].list[j].id + '">' + changes[i].list[j].name + '</option>');
							}
						}
					}
				} else {
					var msg = "";
					try {
						msg = xml.getElementsByTagName("message")[0].firstChild.nodeValue;
					} catch (e) {
					}

					if (input.m_result != null) {
						if (msg == "")
							$(input.m_result).html('<span class="adk_valpassed"></span>');
						else
							$(input.m_result).html('<div class="adk_valfailed">' + msg + '</div>');
					}
				}
			}
		};

		// 查找具备validate_res_的输入项
		var spans = $(this).find("span");
		for (var i = 0; i < spans.length; i++) {
			if(spans[i].id && spans[i].id.indexOf("validate_res_") == 0) {
				var input = $(this).find(":input[name='" + spans[i].id.substring(13) + "']");
				if (input.length > 0) {
					input[0].m_result = spans[i];
					$(input[0]).change(function(){
						this.form.ajaxValidate(this);
					});
				}
			}
		}

		// 查找不具备validate_res_但具备validate='yes'的输入项
		var input = $(this).find(":input[validate='yes']");
		for (var i = 0; i < input.length; i++) {
			if (input[i].m_result == null) {
				$(input[i]).change(function(){
					this.form.ajaxValidate(this);
				});
			}
		}
	});
}
