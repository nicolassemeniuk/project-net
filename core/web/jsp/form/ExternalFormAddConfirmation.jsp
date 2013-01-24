<%--
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
--%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.space.SpaceURLFactory,
			net.project.security.*, 
            net.project.util.JSPUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title"/></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

</head>
<script language="javascript">
	function addNewForm(){
		self.document.location = "<%= SessionManager.getJSPRootURL()+"/eaf?extid="+request.getAttribute("externalFormId")+ (request.getAttribute("extSid") != null && ((String)request.getAttribute("extSid")).length() > 0 ? "&extSid=" +request.getAttribute("extSid") : "") %>";
	}
</script>

<body class="main"  id="bodyWithFixedAreasSupport">
<div id='content'>
<table>
<tr>
	<td>	
  		<%=PropertyProvider.get("prm.form.confirmationpage.thanksforsubmiting.message")%>
    </td>
</tr>
<tr>
  <td>
  	<%=PropertyProvider.get("prm.form.confirmationpage.formname.label")%> <%= request.getAttribute("formName")%>
  </td>
  <td>
    &nbsp;
  </td>  
  <td>
     <%=PropertyProvider.get("prm.form.confirmationpage.recordnumber.label")%>  <%= request.getAttribute("formDataCurrentRecord")%>
  </td>  
</tr>
</table>
<table width="97%" cellpadding="0" cellspacing="0" border="0">
  <tr class="actionBar">
  	<td width="1%" class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
    <td class="actionBar" align="right">&nbsp;
    	<nobr>&nbsp;&nbsp;&nbsp;<a href="javascript:addNewForm();" class="channelNoUnderline"><%=PropertyProvider.get("prm.form.confirmationpage.newformrecord.label")%>&nbsp;<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-submit_off.gif" width="27" height="27" alt="Submit" title="Submit" border="0" align="absmiddle"/></a></nobr>
    </td>
    <td width="1%" align="right" class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
  </tr>
</table>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>



 