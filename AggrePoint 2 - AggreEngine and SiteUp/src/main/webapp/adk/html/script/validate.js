function isNumeric(a,b){if(b){return/^[+-]?\d+(\.\d+)?$/.test(a)}else{return/^\d+(\.\d+)?$/.test(a)}}function isRightStr(a,b){var c;for(var c=0;c<a.length;c++){if(b.indexOf(a.substring(c,c+1))<0){return false}}return true}function packAllSpace(e){var c,d;var b,a;c="";a=e.length;for(b=0;b<a;b++){if(e.charAt(b)!=" "){c=c+e.charAt(b)}}return c}var g_iYear,g_iMonth,g_iDay,g_strDate;function getDate(o,p){var h,e,c;var l,g,f,a,b;var n,m,d;strDate=packAllSpace(o);strFormat=packAllSpace(p);n=m=d=false;g_strDate="";b=strDate.length;a=strFormat.length;g=0;for(l=0;l<a;l++){e=strFormat.charAt(l);if(e=="Y"||e=="y"){h="";for(f=0;f<4;f++){c=strDate.charAt(g);if(c<"0"||c>"9"){break}g++;if(!(h==""&&c=="0")){h=h+c}}if(!f){return false}g_iYear=parseInt(h);if(e=="Y"){if(g_iYear<30){g_iYear=g_iYear+2000}else{if(g_iYear<100){g_iYear=g_iYear+1900}}}n=true;g_strDate=g_strDate+g_iYear}else{if(e=="M"||e=="m"){h="";for(f=0;f<2;f++){c=strDate.charAt(g);if(c<"0"||c>"9"){break}g++;if(!(h==""&&c=="0")){h=h+c}}if(!f){return false}g_iMonth=parseInt(h);if(g_iMonth>12||g_iMonth<1){return false}m=true;g_strDate=g_strDate+g_iMonth}else{if(e=="D"||e=="d"){h="";for(f=0;f<2;f++){c=strDate.charAt(g);if(c<"0"||c>"9"){break}g++;if(!(h==""&&c=="0")){h=h+c}}if(!f){return false}g_iDay=parseInt(h);if(g_iDay>31||g_iDay<1){return false}d=true;g_strDate=g_strDate+g_iDay}else{c=strDate.charAt(g);g++;if(c!=e){return false}g_strDate=g_strDate+e}}}}if(n&&m&&d){switch(g_iMonth){case 2:if(0==g_iYear%4){if(g_iDay>29){return false}if((0==g_iYear%100)&&(0!=g_iYear%8)){if(g_iDay>28){return false}}}else{if(g_iDay>28){return false}}break;case 4:case 6:case 9:case 11:if(g_iDay>30){return false}}}return true}function isEmpty(a){if(a.length==0){return true}return false}function chkboxSelectedCount(d,b){var a;var c;c=0;for(a=d.length-1;a>=0;a--){if(d.elements[a].type=="checkbox"&&d.elements[a].name==b){if(d.elements[a].checked){c++}}}return c}function setChkboxMulti(d,b,c){var a;for(a=d.length-1;a>=0;a--){if(d.elements[a].type=="checkbox"&&d.elements[a].name==b){d.elements[a].checked=c}}}var g_bValid;var g_objError;var g_strErrMsg;function validateCond(b,c,a){if(!g_bValid){return}if(!b){g_bValid=false;g_objError=c;g_strErrMsg=a}}function validateEmpty(a,b){if(!g_bValid){return}if(isEmpty(a.value)){g_bValid=false;g_objError=a;g_strErrMsg=b}}function validateEmptyWithoutSpace(a,b){if(!g_bValid){return}if(/^\s*$/.test(a.value)){g_bValid=false;g_objError=a;g_strErrMsg=b}}function validateLength(b,a,c){if(!g_bValid){return}if(b.value.length<a){g_bValid=false;g_objError=b;g_strErrMsg=c}}function validateNum(a,b,d,c){if(!g_bValid){return}if(isEmpty(a.value)){if(b){return}else{g_bValid=false;g_objError=a;g_strErrMsg=d}}if(!isNumeric(a.value,c)){g_bValid=false;g_objError=a;g_strErrMsg=d}}function validateDate(a,b,d,c){if(!g_bValid){return}if(isEmpty(a.value)){if(b){return}else{g_bValid=false;g_objError=a;g_strErrMsg=c}}if(!getDate(a.value,d)){g_bValid=false;g_objError=a;g_strErrMsg=c}else{a.value=g_strDate}}function validateEmail(a,b,c){if(!g_bValid){return}if(isEmpty(a.value)){if(b){return}}if(!/^\w+([-+.]\w+)*@{1}\w+([-.]\w+)*\.[a-zA-Z]{2,3}$/.test(a.value)){g_bValid=false;g_objError=a;g_strErrMsg=c}}function validateString(a,d,c){var b;if(!g_bValid){return}if(isEmpty(a.value)){return}if(!isRightStr(a.value,d)){g_bValid=false;g_objError=a;g_strErrMsg=c}}function validateBegin(){g_bValid=true}function validateEnd(){if(!g_bValid){alert(g_strErrMsg);if(g_objError.type=="text"){g_objError.select()}g_objError.focus()}return g_bValid};