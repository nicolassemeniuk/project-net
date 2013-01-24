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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 20942 $
|       $Date: 2010-06-10 04:30:27 -0300 (jue, 10 jun 2010) $
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Close Window"
    language="java" 
    errorPage="/errors.jsp"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:set var="m" value="${model}"/>

<html>
<head>
<title><display:get name="prm.document.closewindow.title" /></title>
<script language="javascript1.2">

function finish () {

   var targetWindow;
   if (parent.opener.name == "main")
      targetWindow = parent.opener;
   else
      targetWindow = parent.opener.parent;
	
<c:if test="${(m.reloadURL != null) and (m.reloadURL != '') and (m.checkIn == null)}">
	
    //bfd-2945 Start
	// Avinash: No need to refresh parent, just update the image source..Simple!
	
    try {
    	targetWindow.document.getElementById("projectLogo").src = "<%=net.project.security.SessionManager.getJSPRootURL()%>/servlet/ViewDocument?id=<c:out value="${m.newLogoId}"/>&module=<%=net.project.base.Module.DOCUMENT%>";
		targetWindow.document.getElementById("projectLogo").height = "40";
		targetWindow.document.getElementById("projectLogo").width = "40";	
    }catch(e){ //catch old version (<1.0) firefox exception, no need to set height/width for the same
    }
    //bfd-2945 End
</c:if>
	// set the new uploaded logo on parent window.
	
	try {
		targetWindow.document.getElementById("projectLogo").src = "<%=net.project.security.SessionManager.getJSPRootURL()%>/servlet/ViewDocument?id=<%=request.getAttribute("newLogoId")%>&module=<%=net.project.base.Module.DOCUMENT%>";
		targetWindow.document.getElementById("projectLogo").height = "40";
		targetWindow.document.getElementById("projectLogo").width = "40";	
	}catch(e){
		//As projectLogo id will be on project edit and bussiness edit pages only
		//Need to refresh page if projectLogo id not found (as common use of this page like document check-in)  
		targetWindow.location.reload(true); 
	}
	// Ljubisa bfd-3104
	// we must to reload parent window because servlet needs to resize image
		
    targetWindow.focus();
    parent.close();	
}
</script>
</head>
<body onLoad="finish()">



</body>
</html>
