function ElmRect(b){if(b==null){return null}var a=b.offset();this.left=a.left;this.top=a.top;this.width=b[0].offsetWidth;this.height=b[0].offsetHeight;this.right=this.left+this.width;this.bottom=this.top+this.height}var ImgBg=new Image(1,1);ImgBg.src="/ap2/ae/images/gray.png";var ImgLoading=new Image(1,1);ImgLoading.src="/ap2/ae/images/loading2.gif";var ImgValidating=new Image(1,1);ImgValidating.src="/ap2/ae/images/hourglass.gif";jQuery.fn.adkform=function(){var a=jQuery.extend({},arguments[0]);return this.each(function(){if(this.adksubmit){return}var c=$(this).attr("name");this.adksubmit=function(){if($(this).attr("enctype")=="multipart/form-data"){return true}try{if($(this).attr("hideloading")!="yes"){if(a.iid==a.vid){AeJSEngine.showLoading("#ap_win_"+a.iid)}else{AeJSEngine.showLoading("#ap_view_"+a.vid)}}}catch(d){}$.ajax({type:"POST",url:AeJSEngine.getUrl(),data:"_x=y&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam()+"&"+$(this).serialize(),success:AeJSEngine.getSubmitFormHandler(a.area,a.iid,a.vid),error:AeJSEngine.getErrorHandler(a.iid,a.vid),dataType:"xml"});return false};this.invokeFunc=function(d,f,e){return function(){var g;for(g=0;g<f.length;g++){d.find('input[name="'+f[g]+'"]')[0].value=arguments[g]}if(e){d.submit()}}};$(this).submit(this.adksubmit);if(a.focus){$(this).find('input[name="'+a.focus+'"]').focus()}if(a.tipclz&&a.tipsel&&a.tipcfg){$(this).after('<div class="'+a.tipclz+'" id="tooltip_'+c+'"></div>');$(function(){$("#"+c+" "+a.tipsel).tooltip({position:"center right",offset:[-2,35],effect:"fade",opacity:0.7,tip:"#tooltip_"+c})})}if(a.vaction){$(function(){$("#"+c).adkvalform({iid:a.iid,vid:a.vid,action:a.vaction,form:a.formid})})}if(a.func){var b;for(b=0;b<a.func.length;b++){document[a.func[b].name+a.iid+a.vid]=this.invokeFunc($(this),a.func[b].params,a.func[b].submit)}}})};var AeJSEngine={isStatic:false,isApme:false,reScriptAll:new RegExp("<script.*?>(?:\n|\r|.)*?<\/script>","img"),reScriptOne:new RegExp("<script(.*?)>((?:\n|\r|.)*?)<\/script>","im"),reScriptLanguage:new RegExp('.*?language.*?=.*?"(.*?)"',"im"),reScriptSrc:new RegExp('.*?src.*?=.*?"(.*?)"',"im"),reScriptType:new RegExp('.*?type.*?=.*?"(.*?)"',"im"),reScriptCharset:new RegExp('.*?charset.*?=.*?"(.*?)"',"im"),reCSSAll:new RegExp('<link.*?type="text/css".*?>',"img"),reCSSHref:new RegExp('.*?href="(.*?)"',"im"),getUrlParam:function(){var b="";var a=window.location.href.indexOf("?");if(a>0){b="&"+window.location.href.substr(a+1)}return b},ensureVisiable:function(g){if($(g)==null){return}try{var d=document.documentElement;var a=new ElmRect($(g));var f=0;if(d.scrollLeft+d.clientWidth<a.right){f=a.right-d.scrollLeft-d.clientWidth}if(d.scrollLeft+f>a.left){f=a.left-d.scrollLeft}var c=0;if(d.scrollTop+d.clientHeight<a.bottom){c=a.bottom-d.scrollTop-d.clientHeight}if(d.scrollTop+c>a.top){c=a.top-d.scrollTop}if(f!=0||c!=0){window.scrollBy(f,c)}}catch(b){}},clearLoading:function(b){try{$(b+"_loading").remove()}catch(a){}},showLoading:function(d){try{AeJSEngine.clearLoading(d);var b=new ElmRect($(d));try{if(AeJSEngine.dlg!=null&&AeJSEngine.dlg.dialog("isOpen")===true){b=new ElmRect(AeJSEngine.dlg)}}catch(c){}var a;if(jQuery.browser.version=="6.0"){a="<div id='"+d.substr(1)+"_loading' style='z-index:100000;position:absolute;background-color:#999999;filter:alpha(opacity=30);-moz-opacity:0.3;left:"+b.left+"px;top:"+b.top+"px;width:"+b.width+"px;height:"+b.height+"px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"+ImgLoading.src+"'/></td></tr></table></div>"}else{a="<div id='"+d.substr(1)+"_loading' style='z-index:100000;position:absolute;background:url("+ImgBg.src+");left:"+b.left+"px;top:"+b.top+"px;width:"+b.width+"px;height:"+b.height+"px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"+ImgLoading.src+"'/></td></tr></table></div>"}$("body").append(a)}catch(c){}},getUrl:function(){if(AeJSEngine.isStatic){return"/ap2/site/"}if(AeJSEngine.isApme){return"/ap2/engine/"}var a=location.href;if(a.indexOf("?")==-1){return a}return a.substring(0,a.indexOf("?"))},procStyle:function(e){var d=e.match(AeJSEngine.reCSSAll)||[];var g=$.map(d,function(i){return(i.match(AeJSEngine.reCSSHref)||["",""])[1]});var h=document.getElementsByTagName("head")[0];var c=h.getElementsByTagName("link");var b;var a;var f;for(b=0;b<g.length;b++){if(g[b]==""){continue}for(a=0;a<c.length;a++){if(c[a].href==g[b]){break}}if(a<c.length){continue}f=document.createElement("link");f.type="text/css";f.rel="stylesheet";f.href=g[b];f.media="screen";h.appendChild(f)}return e.replace(AeJSEngine.reCSSAll,"")},procScript:function(content){var scripts=content.match(AeJSEngine.reScriptAll)||[];var scriptContent=$.map(scripts,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptOne)||["","",""])[2]});var scriptDef=$.map(scripts,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptOne)||["","",""])[1]});var scriptLanguage=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptLanguage)||["",""])[1]});var scriptSrc=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptSrc)||["",""])[1]});var scriptType=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptType)||["",""])[1]});var scriptCharset=$.map(scriptDef,function(scriptTag){return(scriptTag.match(AeJSEngine.reScriptCharset)||["",""])[1]});var elmHead=document.getElementsByTagName("head")[0];var elmScripts=elmHead.getElementsByTagName("script");var i;var j;var newScript;for(i=0;i<scripts.length;i++){if(scriptSrc[i]==""){continue}for(j=0;j<elmScripts.length;j++){if(elmScripts[j].src==scriptSrc[i]){break}}if(j<elmScripts.length){continue}newScript=document.createElement("script");if(scriptType[i]!=""){newScript.type=scriptType[i]}else{if(scriptLanguage[i]!=""){newScript.type="text/"+scriptLanguage[i]}else{newScript.type="text/javascript"}}if(scriptCharset[i]!=""){newScript.charset=scriptCharset[i]}newScript.src=scriptSrc[i];elmHead.appendChild(newScript)}for(i=0;i<scriptContent.length;i++){try{eval(scriptContent[i])}catch(e){alert(e.message);alert(scriptContent[i])}}},changeWindowMode:function(a,b,c){AeJSEngine.showLoading("#ap_win_"+b);$.ajax({type:"POST",url:AeJSEngine.getUrl(),data:{_x:"y",_w:b,_m:c,_pg:window.location.pathname,_purl:window.location.href},success:AeJSEngine.getChangeWindowModeHandler(a,b),error:AeJSEngine.getErrorHandler(b),dataType:"xml"})},getChangeWindowModeHandler:function(a,b){return function(c){AeJSEngine.clearLoading("#ap_win_"+b);var d="";try{d=c.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(g){}var f="";try{f=c.getElementsByTagName("content")[0].firstChild.nodeValue}catch(g){}if(d=="current"){$("#ap_win_"+b)[0].innerHTML="";$("#ap_win_"+b)[0].innerHTML=AeJSEngine.procStyle(f.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(f);AeJSEngine.ensureVisiable("#ap_win_"+b)}else{$("#ap_area_"+a)[0].innerHTML="";$("#ap_area_"+a)[0].innerHTML=AeJSEngine.procStyle(f.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(f);AeJSEngine.ensureVisiable("#ap_area_"+a)}}},submitForm:function(c,b,f,a){if(c.enctype=="multipart/form-data"){return true}try{if($(c).attr("hideloading")!="yes"){if(f==a){AeJSEngine.showLoading("#ap_win_"+f)}else{AeJSEngine.showLoading("#ap_view_"+a)}}}catch(d){}$.ajax({type:"POST",url:AeJSEngine.getUrl(),data:"_x=y&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam()+"&"+$(c).serialize(),success:AeJSEngine.getSubmitFormHandler(b,f,a),error:AeJSEngine.getErrorHandler(f,a),dataType:"xml"});return false},reFuncParam:new RegExp("(.+)(?: ((.*)))?"),getActionFunc:function(b,d,a,c,e){return function(){e=(e||"").split(",");var g=arguments.length;if(g>e.length){g=e.length}var j="_x=y&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam()+"&_w="+d+"&_wv="+a+"&_a="+c;var f;for(f=0;f<g;f++){reqParam=reqParam+"&"+$.trim(e[f])+"="+escape(arguments[f])}try{if(d==a){AeJSEngine.showLoading("#ap_win_"+d)}else{AeJSEngine.showLoading("#ap_view_"+a)}}catch(h){}$.ajax({type:"POST",url:AeJSEngine.getUrl(),data:j,success:AeJSEngine.getSubmitFormHandler(b,d,a),error:AeJSEngine.getErrorHandler(d,a),dataType:"xml"})}},closeDialog:function(){if(AeJSEngine.dlg==null){AeJSEngine.dlg=$("<div></div>");$(document.body).append(AeJSEngine.dlg)}else{try{AeJSEngine.dlg.dialog("destroy")}catch(a){}AeJSEngine.dlg.empty()}},invokeAfterLoad:function(){if(AeJSEngine.afterLoad){try{AeJSEngine.afterLoad()}catch(a){}}},handlePopWin:function(c,d){if(c.attr("pop")!="yes"){return}try{if(d==null||d==""){d=c.dialog("option","title")}}catch(b){}var a="";if(c[0].innerText==undefined){a=$.trim(c[0].innerHTML)}else{a=$.trim(c[0].innerText)}if(a==""||a=="<div></div>"){c.dialog("close");c.dialog("destroy")}else{c.dialog("close");c.dialog("destroy");c.dialog({title:d,autoOpen:true,height:"auto",width:"auto",resizable:false,modal:true,close:function(e,f){}})}c.show()},getSubmitFormHandler:function(b,c,a){return function(j){var l;if(c==a){l="#ap_win_"+c}else{l="#ap_view_"+a}var f="";try{f=j.getElementsByTagName("message")[0].firstChild.nodeValue}catch(m){}var o="";try{o=j.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(m){}var n="";try{n=j.getElementsByTagName("content")[0].firstChild.nodeValue}catch(m){}var r="";try{r=j.getElementsByTagName("title")[0].firstChild.nodeValue}catch(m){}var p="";try{p=j.getElementsByTagName("dialog")[0].firstChild.nodeValue}catch(m){}var i="";try{i=j.getElementsByTagName("update")[0].firstChild.nodeValue}catch(m){}var k="";try{k=j.getElementsByTagName("ensurevisible")[0].firstChild.nodeValue}catch(m){}if(o=="page"||o=="url"){location.href=n;return}else{if(o=="window"){$("#ap_win_"+c)[0].innerHTML=AeJSEngine.procStyle(n.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(n);if(k==""){AeJSEngine.ensureVisiable("#ap_win_"+c)}AeJSEngine.handlePopWin($("#ap_win_"+c),r)}else{if(o=="current"){$(l)[0].innerHTML=AeJSEngine.procStyle(n.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(n);if(k==""){AeJSEngine.ensureVisiable(l)}AeJSEngine.handlePopWin($("#ap_win_"+c),r)}else{if(o=="area"){$("#ap_area_"+b)[0].innerHTML=AeJSEngine.procStyle(n.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(n);if(k==""){AeJSEngine.ensureVisiable("#ap_area_"+b)}}}}}AeJSEngine.clearLoading(l);var h=l;if(k!=""){if(k.indexOf("_")==-1){AeJSEngine.ensureVisiable("#ap_win_"+k)}else{AeJSEngine.ensureVisiable("#ap_view_"+k)}}while(i!=""){var d=i.indexOf(",");var q=i;if(d!=-1){q=i.substr(0,d);i=i.substr(d+1)}else{q=i;i=""}var u;var t=false;if(q.indexOf("!")==0){t=true;q=q.substring(1)}var s=q.indexOf("_");if(s==-1){l="#ap_win_"+q;u=q}else{l="#ap_view_"+q;u=q.substr(0,s)}if($(l)!=null){if($("#ap_win_"+u).attr("pop")=="yes"){$("#ap_win_"+u)[0].puid=h;setTimeout(function(){AeJSEngine.showLoading(h)},100)}else{AeJSEngine.showLoading(l)}$.ajax({type:"POST",url:AeJSEngine.getUrl(),data:{_x:"y",_w:u,_wv:q,_pg:window.location.pathname,_purl:window.location.href},success:AeJSEngine.getViewWindowHandler(u,q,t),error:AeJSEngine.getErrorHandler(u,q),dataType:"xml"})}}AeJSEngine.closeDialog();if(p!=null&&p!=""){AeJSEngine.dlg.append(AeJSEngine.procStyle(p.replace(AeJSEngine.reScriptAll,"")));AeJSEngine.procScript(p);$(function(){AeJSEngine.dlg.dialog({title:r,autoOpen:true,height:"auto",width:"auto",resizable:false,modal:true,close:function(e,v){AeJSEngine.closeDialog()}})})}else{if(f!=null&&f!=""){var g=f.indexOf("|");if(g>=0){AeJSEngine.dlg.append("<p>"+f.substring(g+1)+"</p>")}else{AeJSEngine.dlg.append("<p>"+f+"</p>")}AeJSEngine.dlg.dialog({title:g>=0?f.substring(0,g):"",autoOpen:true,height:"auto",width:"auto",resizable:false,modal:true,buttons:{OK:function(){var e=$(this);e.dialog("close");if(AeJSEngine.editFocus){AeJSEngine.editFocus.select();AeJSEngine.editFocus.focus();AeJSEngine.editFocus=null}}}})}}AeJSEngine.invokeAfterLoad()}},getViewWindowHandler:function(c,a,b){return function(d){var f;if(c==a){f="#ap_win_"+c}else{f="#ap_view_"+a}var i=$("#ap_win_"+c);if(i.attr("pop")=="yes"){setTimeout(function(){AeJSEngine.clearLoading(i[0].puid)},100);i.hide()}AeJSEngine.clearLoading(f);var g="";try{g=d.getElementsByTagName("content")[0].firstChild.nodeValue}catch(h){}var j="";try{j=d.getElementsByTagName("title")[0].firstChild.nodeValue}catch(h){}$(f)[0].innerHTML=AeJSEngine.procStyle(g.replace(AeJSEngine.reScriptAll,""));AeJSEngine.procScript(g);if(b){AeJSEngine.ensureVisiable(f)}AeJSEngine.handlePopWin(i,j);AeJSEngine.invokeAfterLoad()}},getErrorHandler:function(b,a){return function(c,e,d){document.location.reload(true)}},getInitWinAndContHandler:function(a){return function(b){var c="";try{c=b.getElementsByTagName("refresh")[0].firstChild.nodeValue}catch(f){}var d="";try{d=b.getElementsByTagName("content")[0].firstChild.nodeValue}catch(f){}if(c=="page"||c=="url"){location.href=d;return}a.html(AeJSEngine.procStyle(d.replace(AeJSEngine.reScriptAll,"")));AeJSEngine.procScript(d);AeJSEngine.invokeAfterLoad()}},initStatic:function(){AeJSEngine.isStatic=true;$("div").filter(function(){return this.id.match(/^ap_win_/)}).each(function(a){$.ajax({type:"POST",url:"/ap2/site/",data:"_x=y&_w="+this.id.substring(7)+"&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam(),success:AeJSEngine.getInitWinAndContHandler($(this)),error:AeJSEngine.getErrorHandler(),dataType:"xml"})});$("div").filter(function(){return this.id.match(/^ap_cont_/)}).each(function(a){$.ajax({type:"POST",url:"/ap2/site/",data:"_x=y&_c="+this.id.substring(8)+"&_pg="+window.location.pathname+"&_purl="+escape(window.location.href)+AeJSEngine.getUrlParam(),success:AeJSEngine.getInitWinAndContHandler($(this)),error:AeJSEngine.getErrorHandler(),dataType:"xml"})})},initApme:function(){AeJSEngine.isApme=true;$("div").filter(function(){return this.id.match(/^winlet!/)}).each(function(a){$.ajax({type:"POST",url:"/ap2/engine/",data:"_ajax=y&_wpath="+this.id.substring(7)+"&_purl="+AeJSEngine.getUrl(),success:AeJSEngine.getInitWinAndContHandler($(this)),error:AeJSEngine.getErrorHandler(),dataType:"xml"})})}};