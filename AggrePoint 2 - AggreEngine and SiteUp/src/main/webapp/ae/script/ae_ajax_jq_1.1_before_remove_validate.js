function ElmRect(b){if(b==null){return null}var a=b.offset();this.left=a.left;this.top=a.top;this.width=b[0].offsetWidth;this.height=b[0].offsetHeight;this.right=this.left+this.width;this.bottom=this.top+this.height}var ImgBg=new Image(1,1);ImgBg.src="/ap2/ae/images/gray.png";var ImgLoading=new Image(1,1);ImgLoading.src="/ap2/ae/images/loading2.gif";var ImgValidating=new Image(1,1);ImgValidating.src="/ap2/ae/images/hourglass.gif";var ImgValidatePass=new Image(1,1);ImgValidatePass.src="/ap2/ae/images/vpass.gif";var ImgValidateFailed=new Image(1,1);ImgValidateFailed.src="/ap2/ae/images/vfail.gif";var AeJSEngine={isStatic:false,isApme:false,reScriptAll:new RegExp("<script.*?>(?:\n|\r|.)*?<\/script>","img"),reScriptOne:new RegExp("<script(.*?)>((?:\n|\r|.)*?)<\/script>","im"),reScriptLanguage:new RegExp('.*?language.*?=.*?"(.*?)"',"im"),reScriptSrc:new RegExp('.*?src.*?=.*?"(.*?)"',"im"),reScriptType:new RegExp('.*?type.*?=.*?"(.*?)"',"im"),reScriptCharset:new RegExp('.*?charset.*?=.*?"(.*?)"',"im"),reCSSAll:new RegExp('<link.*?type="text/css".*?>',"img"),reCSSHref:new RegExp('.*?href="(.*?)"',"im"),getUrlParam:function(){var b="";var a=window.location.href.indexOf("?");if(a>0){b="&"+window.location.href.substr(a+1)}return b},ensureVisiable:function(g){if($(g)==null){return}try{var d=document.documentElement;var a=new ElmRect($(g));var f=0;if(d.scrollLeft+d.clientWidth<a.right){f=a.right-d.scrollLeft-d.clientWidth}if(d.scrollLeft+f>a.left){f=a.left-d.scrollLeft}var c=0;if(d.scrollTop+d.clientHeight<a.bottom){c=a.bottom-d.scrollTop-d.clientHeight}if(d.scrollTop+c>a.top){c=a.top-d.scrollTop}if(f!=0||c!=0){window.scrollBy(f,c)}}catch(b){}},clearLoading:function(b){try{$(b+"_loading").remove()}catch(a){}},showLoading:function(d){try{AeJSEngine.clearLoading(d);var b=new ElmRect($(d));var a;if(jQuery.browser.version=="6.0"){a="<div id='"+d.substr(1)+"_loading' style='z-index:100000;position:absolute;background-color:#999999;filter:alpha(opacity=30);-moz-opacity:0.3;left:"+b.left+"px;top:"+b.top+"px;width:"+b.width+"px;height:"+b.height+"px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"+ImgLoading.src+"'/></td></tr></table></div>"}else{a="<div id='"+d.substr(1)+"_loading' style='z-index:100000;position:absolute;background:url("+ImgBg.src+");left:"+b.left+"px;top:"+b.top+"px;width:"+b.width+"px;height:"+b.height+"px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"+ImgLoading.src+"'/></td></tr></table></div>"}$("body").append(a)}catch(c){}},getUrl:function(){if(AeJSEngine.isStatic){return"/ap2/site/"}if(AeJSEngine.isApme){return"/ap2/engine/"}var a=location.href;if(a.indexOf("?")==-1){return a}return a.substring(0,a.indexOf("?"))},procStyle:function(e){var d=e.match(AeJSEngine.reCSSAll)||[];var g=$.map(d,function(i){return(i.match(AeJSEngine.reCSSHref)||["",""])[1]});var h=document.getElementsByTagName("head")[0];var c=h.getElementsByTagName("link");var b;var a;var f;for(b=0;b<g.length;b++){if(g[b]==""){continue}for(a=0;a<c.length;a++){if(c[a].href==g[b]){break}}if(a<c.length){continue}f=document.createElement("link");f.type="text/css";f.rel="stylesheet";f.href=g[b];f.media="screen";h.appendChild(f)}return e.replace(AeJSEngine.reCSSAll,"")},procScript:function(content){var scripts=content.match(AeJSEngine.reScriptAll)||[];var scriptContent=$.map(scripts,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptOne)||["","",""])[2]});var scriptDef=$.map(scripts,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptOne)||["","",""])[1]});var scriptLanguage=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptLanguage)||["",""])[1]});var scriptSrc=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptSrc)||["",""])[1]});var scriptType=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptType)||["",""])[1]});var scriptCharset=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptCharset)||["",""])[1]});var elmHead=document.getElementsByTagName("head")[0];var elmScripts=elmHead.getElementsByTagName("script");var i;var j;var newScript;for(i=0;i<scripts.length;i++){if(scriptSrc[i]==""){continue}for(j=0;j<elmScripts.length;j++){if(elmScripts[j].src==scriptSrc[i]){break}}if(j<elmScripts.length){continue}newScript=document.createElement("script");if(scriptType[i]!=""){newScript.type=scriptType[i]}else{if(scriptLanguage[i]!=""){newScript.type="text/"+scriptLanguage[i]}else{newScript.type="text/javascript"}}if(scriptCharset[i]!=""){newScript.charset=scriptCharset[i]}newScript.src=scriptSrc[i];elmHead.appendChild(newScript)}for(i=0;i<scriptContent.length;i++){try{eval(scriptContent[i])}catch(e){alert(e.message);alert(scriptContent[i])}}},changeWindowMode:function(a,b,c){AeJSEngine.showLoading("#ap_win_"+b);$.post(AeJSEngine.getUrl(),{_x:"y",_w:b,_m:c,_pg:window.location.pathname,_purl:window.location.href},AeJSEngine.getChangeWindowModeHandler(a,b),"xml")},getChangeWindowModeHandler:function(a,b){return function(c){AeJSEngine.clearLoading("#ap_win_"+b);var d="";try{d=c.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(g){}var f="";try{f=c.getElementsByTagName("content")[0].firstChild.nodeValue}catch(g){}if(d=="current"){$("#ap_win_"+b)[0].innerHTML="";$("#ap_win_"+b)[0].innerHTML=AeJSEngine.procStyle(f.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(f);AeJSEngine.ensureVisiable("#ap_win_"+b)}else{$("#ap_area_"+a)[0].innerHTML="";$("#ap_area_"+a)[0].innerHTML=AeJSEngine.procStyle(f.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(f);AeJSEngine.ensureVisiable("#ap_area_"+a)}}},submitForm:function(c,b,d,a){if(c.enctype=="multipart/form-data"){return true}if(d==a){AeJSEngine.showLoading("#ap_win_"+d)}else{AeJSEngine.showLoading("#ap_view_"+a)}$.post(AeJSEngine.getUrl(),"_x=y&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam()+"&"+$(c).serialize(),AeJSEngine.getSubmitFormHandler(b,d,a),"xml");return false},getSubmitFormHandler:function(b,c,a){return function(i){var k;if(c==a){k="#ap_win_"+c}else{k="#ap_view_"+a}var f="";try{f=i.getElementsByTagName("message")[0].firstChild.nodeValue}catch(l){}var n="";try{n=i.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(l){}var m="";try{m=i.getElementsByTagName("content")[0].firstChild.nodeValue}catch(l){}var h="";try{h=i.getElementsByTagName("update")[0].firstChild.nodeValue}catch(l){}var j="";try{j=i.getElementsByTagName("ensurevisible")[0].firstChild.nodeValue}catch(l){}if(n=="page"||n=="url"){location.href=m;return}else{if(n=="window"){$("#ap_win_"+c)[0].innerHTML=AeJSEngine.procStyle(m.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(m);if(j==""){AeJSEngine.ensureVisiable("#ap_win_"+c)}}else{if(n=="current"){$(k)[0].innerHTML=AeJSEngine.procStyle(m.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(m);if(j==""){AeJSEngine.ensureVisiable(k)}}else{if(n=="area"){$("#ap_area_"+b)[0].innerHTML=AeJSEngine.procStyle(m.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(m);if(j==""){AeJSEngine.ensureVisiable("#ap_area_"+b)}}}}}AeJSEngine.clearLoading(k);if(j!=""){if(j.indexOf("_")==-1){AeJSEngine.ensureVisiable("#ap_win_"+j)}else{AeJSEngine.ensureVisiable("#ap_view_"+j)}}while(h!=""){var d=h.indexOf(",");var p=h;if(d!=-1){p=h.substr(0,d);h=h.substr(d+1)}else{p=h;h=""}var s;var r=false;if(p.indexOf("!")==0){r=true;p=p.substring(1)}var q=p.indexOf("_");if(q==-1){k="#ap_win_"+p;s=p}else{k="#ap_view_"+p;s=p.substr(0,q)}if($(k)!=null){AeJSEngine.showLoading(k);$.post(AeJSEngine.getUrl(),{_x:"y",_w:s,_wv:p,_pg:window.location.pathname,_purl:window.location.href},AeJSEngine.getViewWindowHandler(s,p,r),"xml")}}if(f!=null&&f!=""){var g=f.indexOf("|");var o=null;if(g>=0){o=$('<div id="dialog" title="'+f.substring(0,g)+'"><p>'+f.substring(g+1)+"</p></div>")}else{o=$('<div id="dialog" title=""><p>'+f+"</p></div>")}o.dialog({bgiframe:true,autoOpen:false,height:140,modal:true,buttons:{OK:function(){var e=$(this);e.dialog("close");if(AeJSEngine.editFocus){AeJSEngine.editFocus.select();AeJSEngine.editFocus.focus();AeJSEngine.editFocus=null}}}}).dialog("open")}}},getViewWindowHandler:function(c,a,b){return function(d){var f;if(c==a){f="#ap_win_"+c}else{f="#ap_view_"+a}AeJSEngine.clearLoading(f);var g="";try{g=d.getElementsByTagName("content")[0].firstChild.nodeValue}catch(h){}$(f)[0].innerHTML=AeJSEngine.procStyle(g.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(g);if(b){AeJSEngine.ensureVisiable(f)}}},validateField:function(d){var c=d.form;var b="#ap_field_"+c.name+d.name;if($(b)==null){return}$(b).html("<img src='"+ImgValidating.src+"' style='vertical-align:middle'/>");var a=new Ajax.Request(AeJSEngine.getUrl(),{method:"post",parameters:"_x=y&_w="+c._w.value+"&_wv="+c._mv.value+"&_f="+c.name+"&_ff="+d.name+"&"+d.name+"="+d.value,onComplete:AeJSEngine.getValidateFieldHandler(b)})},getValidateFieldHandler:function(a){return function(c){var b="";try{b=c.responseText}catch(d){}if(b==null||b==""){$(a).html("<img src='"+ImgValidatePass.src+"' style='vertical-align:middle'/>")}else{$(a).html("<img src='"+ImgValidateFailed.src+"' style='vertical-align:middle'/>&nbsp;"+b)}}},getInitWinAndContHandler:function(a){return function(b){var c="";try{c=b.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(f){}var d="";try{d=b.getElementsByTagName("content")[0].firstChild.nodeValue}catch(f){}if(c=="page"||c=="url"){location.href=d;return}a.html(AeJSEngine.procStyle(d.replace(AeJSEngine.reScriptAll,"")));AeJSEngine.procScript(d)}},initStatic:function(){AeJSEngine.isStatic=true;$("div").filter(function(){return this.id.match(/^ap_win_/)}).each(function(a){$.post("/ap2/site/","_x=y&_w="+this.id.substring(7)+"&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam(),AeJSEngine.getInitWinAndContHandler($(this)),"xml")});$("div").filter(function(){return this.id.match(/^ap_cont_/)}).each(function(a){$.post("/ap2/site/","_x=y&_c="+this.id.substring(8)+"&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam(),AeJSEngine.getInitWinAndContHandler($(this)),"xml")})},initApme:function(){AeJSEngine.isApme=true;$("div").filter(function(){return this.id.match(/^winlet!/)}).each(function(a){$.post("/ap2/engine/","_ajax=y&_wpath="+this.id.substring(7)+"&_purl="+AeJSEngine.getUrl(),AeJSEngine.getInitWinAndContHandler($(this)),"xml")})}};