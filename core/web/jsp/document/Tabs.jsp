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
|   $RCSfile$
|   $Revision: 18405 $
|   $Date: 2008-11-23 19:38:47 -0200 (dom, 23 nov 2008) $
|   $Author: puno $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document Properties" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    		net.project.security.*,
			net.project.space.Space,
			net.project.base.property.PropertyProvider,
			net.project.base.Module"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="historyBean" class="net.project.history.HistoryBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
     int module = securityProvider.getCheckedModuleID();
     int action = securityProvider.getCheckedActionID();
     String id = securityProvider.getCheckedObjectID();
 
 	String mySpace = user.getCurrentSpace().getType();
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title> 

<%
	String myDisplay = request.getParameter("view");
   	String ImageArray[]= {SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif",SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif",SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif",SessionManager.getJSPRootURL()+"/images/icons/channelbar-minimized.gif"};
   	String ClassDisplayArray[] = {"channelHeader","channelHeader","channelHeader","channelHeader"};
 	String URLArray[] =	{ "<a HREF=\"javascript:propertyTab();\" class=\"channelNoUnderline\">"+PropertyProvider.get("prm.document.tabs.properties.link")+"</a>",
 						"<a HREF=\"javascript:versionTab();\" class=\"channelNoUnderline\">"+PropertyProvider.get("prm.document.tabs.versions.link")+"</a>",
 						"<a HREF=\"javascript:historyTab();\" class=\"channelNoUnderline\">"+PropertyProvider.get("prm.document.tabs.activitylog.link")+"</a>",
 						"<a HREF=\"javascript:discussTab();\" class=\"channelNoUnderline\">"+PropertyProvider.get("prm.document.tabs.discuss.link")+"</a>"};
						
 	String DisplayName = PropertyProvider.get("prm.document.tabs.properties.title");
 	String StartImageURLArray[] = {"<a HREF=\"javascript:propertyTab();\">",
 								"<a HREF=\"javascript:versionTab();\">",
 								"<a HREF=\"javascript:historyTab();\">",
								"<a HREF=\"javascript:discussTab();\">"};
								
 	String EndImageURLArray[] = {"</a>","</a>","</a>","</a>"};
 
  	if (myDisplay != null)
  	{
  		if (myDisplay.equalsIgnoreCase("version"))
 	 	{
 	 		ImageArray[1]= SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
 	 		ClassDisplayArray[1]="channelHeaderDarker";
 	 		URLArray[1] = PropertyProvider.get("prm.document.tabs.versions.link");
 	 		DisplayName = PropertyProvider.get("prm.document.tabs.versions.title");
 	 		StartImageURLArray[1]="";
 	 		EndImageURLArray[1]="";
 
  	}
  	else if (myDisplay.equalsIgnoreCase("log"))
 	{
 		ImageArray[2]= SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
 		ClassDisplayArray[2]="channelHeaderDarker";
 		URLArray[2] = PropertyProvider.get("prm.document.tabs.activitylog.link");
		DisplayName = PropertyProvider.get("prm.document.tabs.activitylog.title");
 		StartImageURLArray[2]="";
 	 	EndImageURLArray[2]="";
 
  	}
  	else if (myDisplay.equalsIgnoreCase("discuss"))
 	{
 		ImageArray[3]= SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
 		ClassDisplayArray[3]="channelHeaderDarker";
 		URLArray[3] = PropertyProvider.get("prm.document.tabs.discuss.link");
 		DisplayName = PropertyProvider.get("prm.document.tabs.discuss.title");
 		StartImageURLArray[3]="";
 	 	EndImageURLArray[3]="";
 
  	}
  	else
 	 	{
 	 		ImageArray[0]= SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
 	 		ClassDisplayArray[0]="channelHeaderDarker";
 	 		URLArray[0]=PropertyProvider.get("prm.document.tabs.properties.link");
 	 		StartImageURLArray[0]="";
 	 		EndImageURLArray[0]="";
 
 	 	}
 
  }
 else
 	{
 		ImageArray[0]= SessionManager.getJSPRootURL()+"/images/icons/channelbar-maximized.gif";
 		ClassDisplayArray[0]="channelHeaderDarker";
 		URLArray[0] = PropertyProvider.get("prm.document.tabs.properties.link");
 		StartImageURLArray[0]="";
 	 	EndImageURLArray[0]="";
 
 	}
 	
 IContainerObject co = docManager.getCurrentObject();
 DisplayName = co.getName();
    
  %>
  
  
<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/document_prototypes.js" />
<template:import type="javascript" src="/src/document/child-actions.js" />

<script language="javascript">
    var theForm;
    //Internationalization variables for pop up messages
    var confirmDocumentDeletionMes = "<%=PropertyProvider.get("prm.global.javascript.document.confirmdeletion.message")%>";
    var confirmUndoDeletionMes = "<%=PropertyProvider.get("prm.global.javascript.document.confirmundodeletion.message")%>";
    
    
    function propertyTab() {
       //propertySheet();
       self.location="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?view=properties&module=<%=module%>&action=<%=action%>&gotoframe=<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?theAction=property_sheet&module=<%= module %>"
    }
    
    function versionTab() {		
        self.location="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?view=version&module=<%=module%>&action=<%=action%>&gotoframe=<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?theAction=version&module=<%= module %>"
        //version();
    }
    
    function historyTab() {
        self.location="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?view=log&module=<%=module%>&action=<%=action%>&gotoframe=<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?theAction=history&module=<%= module %>"
        //history();
    }
    
    function discussTab() {
        theForm.module.value = "<%= module %>";
        self.location="<%=SessionManager.getJSPRootURL()%>/document/Tabs.jsp?view=discuss&module=<%= module %>&gotoframe=<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?theAction=discuss&module=<%= module %>";
        //discuss();
    }
		
	function view() {
		var objectID = <%=docManager.getCurrentObjectID()%>;
      	var theURL = getVar("JSPRootURL") + "/servlet/DownloadDocument?id=" + objectID;
      	window.open(theURL, "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
	}

	function loaded() {
		var s = location.href;
		var i = s.indexOf('&gotoframe');
		if (i>1) {
			i += 11;
			var gotolink = s.substr(i,s.length-i);
			parent.property_main.location.href=gotolink;
		}

	}

	function setup(){
		theForm = self.document.forms[0];
	}
		 
<%
		 String help_section="property";
		 if (myDisplay != null)
		 {
			if (myDisplay.equalsIgnoreCase("version"))
				 help_section="version";
			else if (myDisplay.equalsIgnoreCase("log"))
				help_section="activity_log";
			else if (myDisplay.equalsIgnoreCase("discuss"))
				help_section="discussion";
		 
		 }
%>
		
    function help(){
        var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=document_main&section=<%=help_section%>";
        openwin_help(helplocation);
    }

    <%
     // Only allow security in project space
     if (mySpace != null && !mySpace.equals(net.project.space.Space.PERSONAL_SPACE))
         {
    %>
     
    function security() {
             if (!security)
             var security = openwin_security("security");

             if (security) {
             theAction("security");
             theForm.target = "security";
             theForm.submit();
             security.focus();
             }
    }
    <%
         }
    %>  

</script>
</head>

<body class="main" ONLOAD="loaded(); setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Draw Toolbar
  -----------------------------------------------------------------------%>

<%if((myDisplay == null) || ((myDisplay != null) && (!myDisplay.equalsIgnoreCase("discuss")))) {
	if(module == Module.TRASHCAN) { //sjmittal speacial case for trashcan module
%>

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.document.name">
	<tb:setAttribute name="leftTitle">
		<history:history target="main">
			<history:page display="<%=DisplayName%>"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/trashcan/ViewDeletedDocuments.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.TRASHCAN%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="trashcan">
		<tb:button type="undo_delete" label='<%= PropertyProvider.get("prm.document.main.undodelete.button.tooltip")%>' />
	 </tb:band>		
	<tb:band name="standard">
		<tb:button type="properties" label='<%= PropertyProvider.get("prm.document.tabs.properties.button.tooltip") %>' />
		<tb:button type="remove" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<%   } else { %>
<tb:toolbar style="tooltitle">
	<tb:setAttribute name="leftTitle">
		<history:history target="main">
			<history:page display="<%=DisplayName%>"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/Main.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" showAll="true" enableAll="true">
		<tb:button type="new_folder" enable="false" />
	 </tb:band>		
	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%= PropertyProvider.get("prm.document.tabs.create.button.tooltip") %>' />
		<tb:button type="modify" label='<%= PropertyProvider.get("prm.document.tabs.modify.button.tooltip") %>' />
		<tb:button type="remove" />
		<tb:button type="properties" label='<%= PropertyProvider.get("prm.document.tabs.properties.button.tooltip") %>' />
		<tb:button type="workflow" />
		<%if (!mySpace.equals(Space.PERSONAL_SPACE)) {%>
			<tb:button type="security" />
		<%}%>
	</tb:band>
</tb:toolbar>
<%    }
   }  else {
%>
<tb:toolbar style="tooltitle">
	<tb:setAttribute name="leftTitle">
		<history:history target="main">
			<history:page display="<%=DisplayName%>"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/Main.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
			/>
		</history:history>
	</tb:setAttribute>
</tb:toolbar>
<%}%>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker" name="">
  <input type="hidden" name="theAction">
  <input type="hidden" name="module" value="<%= module %>">

	<table border="0" cellspacing="0" cellpadding="0" vspace="0">
		<tr>
			<td class="<%=ClassDisplayArray[0]%>" width="1%"><%=StartImageURLArray[0]%><img  src="<%=ImageArray[0]%>" width="20" height="15" border=0 hspace=0 vspace=0><%=EndImageURLArray[0]%></td>
			<td nowrap  class="<%=ClassDisplayArray[0]%>" width="18%"><%=URLArray[0]%></td>
			<td width=1% align=right class="<%=ClassDisplayArray[0]%>"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
			
			<td width="1%" class="<%=ClassDisplayArray[1]%>"><%=StartImageURLArray[1]%><img  src="<%=ImageArray[1]%>" width="20" height="15" border=0 hspace=0 vspace=0><%=EndImageURLArray[1]%></td>
			<td nowrap class="<%=ClassDisplayArray[1]%>" width="18%"><%=URLArray[1]%></td>
			<td width=1% align=right class="<%=ClassDisplayArray[1]%>"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
			
			<td class="<%=ClassDisplayArray[2]%>" width="1%"><%=StartImageURLArray[2]%><img  src="<%=ImageArray[2]%>" width="20" height="15" border=0 hspace=0 vspace=0><%=EndImageURLArray[2]%></td>
			<td nowrap class="<%=ClassDisplayArray[2]%>" width="18%"><%=URLArray[2]%></td>
			<td width=1% align=right class="<%=ClassDisplayArray[2]%>"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
			
			<td class="<%=ClassDisplayArray[3]%>" width="1%"><%=StartImageURLArray[3]%><img  src="<%=ImageArray[3]%>" width="20" height="15" border=0 hspace=0 vspace=0><%=EndImageURLArray[3]%></td>
			<td nowrap class="<%=ClassDisplayArray[3]%>" width="18%"><%=URLArray[3]%></td>
			<td width=1% align=right class="<%=ClassDisplayArray[3]%>"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>  
		</tr>
	</table>
<p>
</p>
</form>

</div>

<template:getSpaceJS />
</body>
</html>

