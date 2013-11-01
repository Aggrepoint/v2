<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.attribute name="title" required="true" />
<adk:tbody>
<html>
	<head>
	<title>${title}</title>
	<link rel="stylesheet" id="css" type="text/css" href="/ap2/adk/html/styles/adk2009_5.css" />
	<link rel="stylesheet" id="css" type="text/css" href="/ap2/adk/html/styles/ui_1.0.2.css" />
	<link rel="stylesheet" id="css" type="text/css" href="/ap2/adk/html/jquery/jquery-ui-1.8.16.custom.css" />
	<link rel="stylesheet" id="css" type="text/css" href="/ap2/adk/html/jquery/tools.tooltip.css" />
	<link rel="stylesheet" id="css" type="text/css" href="/ap2/su/theme/popup.css" />
	<script language="javascript" src="/ap2/adk/html/jquery/jquery-1.7.1.min.js">&amp;nbsp;</script>
	<script language="javascript" src="/ap2/adk/html/jquery/jquery-ui-1.8.16.custom.min.js">&amp;nbsp;</script>
	<script language="javascript" src="/ap2/adk/html/jquery/ui.datepicker-zh-CN.js">&amp;nbsp;</script>
	<script language="javascript" src="/ap2/adk/html/jquery/tools.tooltip-1.1.3.min.js">&amp;nbsp;</script>
	<script language="javascript" src="/ap2/adk/html/script/ibutils_1.2.js">&amp;nbsp;</script>
	<script language="javascript" src="/ap2/adk/html/script/ui_1.1.js">&amp;nbsp;</script>
	</head>

	<body leftmargin="0" topmargin="0">
	<jsp:doBody />

	<script language="javascript">
	$(document).ready(function() { 
	  $('.formLayout').each(function(container) { 
	    var max = 0; 
	      $("label", container).each(function(){ 
	          if ($(this).width() > max) 
	              max = $(this).width();   
	      }); 
	      $("label",container).width(max); 
	  }); 
	}); 
	</script>
	</body>
</html>
</adk:tbody>
</jsp:root>
