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
    info="Lifecycle Entry Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.process.ProcessBean,
            net.project.channel.ChannelManager, 
            net.project.channel.Channel,
            net.project.security.User,
			net.project.base.property.PropertyProvider,
			net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=Module.PROCESS%>" />

<%
    // Add module as attribute so that included IFRAME pages can access it
    request.setAttribute("module", "" + Module.PROCESS);
%>
<%	if(m_process.loadProcess(user.getCurrentSpace().getID())) { %>
		<history:history displayHere="false">
			<history:module displayToken="@prm.process.create.label" />
		</history:history>
<%
		response.sendRedirect(SessionManager.getJSPRootURL() + "/process/modifyProcess.jsp?module=" + Module.PROCESS + "&action=" + Action.CREATE);
	} else {
%>
		<history:history displayHere="false">
			<history:module display="<%=m_process.getName()%>" 
							jspPage='<%=SessionManager.getJSPRootURL() + "/process/Main.jsp"%>'
							queryString='<%="module="+Module.PROCESS+"&action="+Action.VIEW%>' />
		</history:history>
<%	} %>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
		var noSelectionErrMes = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';
    function setup() {
       	theForm = self.document.mainForm;
       	load_menu('<%=user.getCurrentSpace().getID()%>');
		isLoaded = true;
	} // end setup()


	function cancel() {
   		var theLocation = JSPRootURL + "/project/Dashboard";
   		self.document.location = theLocation;
   	}
   
   	function modifyProcess() {
   		var theLocation = JSPRootURL + "/process/modifyProcess.jsp?module=<%=Module.PROCESS%>&action=<%=Action.MODIFY%>&id=<%= m_process.getID() %>";
   		self.document.location = theLocation;
   	}   

	function createPhase() {
		var theLocation = JSPRootURL + "/process/modifyPhase.jsp?module=<%=Module.PROCESS%>&action=<%=Action.CREATE%>&id=<%= m_process.getID() %>";
   		self.document.location = theLocation;
	}
	
	function deletePhase() {
		 if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			 Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.process.phase.delete.message")%>', 
				function(btn){
					if(btn == 'yes') {
			        	var theLocation = JSPRootURL + "/process/modifyPhaseProcessing.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.DELETE%>&id="+getSelection(theForm);
	   		            self.document.location = theLocation;
					}
		  	 });
		 }			
	}
	 
	function reset() { 
   		var theLocation = JSPRootURL + "/process/Main.jsp?module=<%=Module.PROCESS%>&action=<%=Action.VIEW%>";
   		self.document.location = theLocation;
   	}
   
	function viewPhase(id) {
 	    theAction("");
		theForm.target = "_self";
   		theForm.id.value = id;
   		theForm.submit();
   	}
   
   function help() {
   		var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main";
   		openwin_help(helplocation);
   }
   
   function workflow() {
         
   if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {	

	   if (!workflow) {
	       var workflow = openwin_wizard("workflow");

	       if (workflow) {
		   theAction("workflow");
		   theForm.target = "workflow";
		   theForm.submit();
		   workflow.focus();
	       }
	   }

   } // end verify
}
 
</script>
</head>
<body onLoad="setup();" id="bodyWithFixedAreasSupport" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="workflow" />
		<tb:button type="create" label='<%=PropertyProvider.get("prm.project.process.phase.create.button.label")%>' function='javascript:createPhase();'/>
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.project.process.phase.delete.button.label")%>' function='javascript:deletePhase();'/>
		<tb:button type="personalize_page" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%= SessionManager.getJSPRootURL() %>/process/ViewPhase.jsp" name="mainForm">
<input type="hidden" name="id">
<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>">
<input type="hidden" name="module" value="<%=net.project.base.Module.PROCESS%>">
<input type="hidden" name="theAction">

<%--  Process Channel --%>

<channel:channel name='<%="ProcessView_"+m_process.getName() %>' customizable="true">
    <channel:insert name='<%="ProcessView_Process_" + m_process.getName() %>' title='<%=PropertyProvider.get("prm.process.info.label")%>' row="0" column="0"
                    width="100%" minimizable="true" closeable="true" include="/process/include/processDetail.jsp">
	<channel:button style="channel" type="modify" label='<%=PropertyProvider.get("prm.project.process.modify.button.label")%>' href="javascript:modifyProcess();"/>				
    </channel:insert>
     
    <channel:insert name='<%="ProcessView_Phases_" + m_process.getName() %>' title='<%=PropertyProvider.get("prm.project.process.phase.label")%>' row="1" column="0"
                    width="100%" minimizable="true" closeable="true"
                    include="/process/include/phaseList.jsp">
   </channel:insert>
</channel:channel>	

<display:if name="@prm.process.diagram.isenabled">

    <table border=0>
    <tr><td align=left class="pageTitle"><a href="ProcessDiagram.jsp" target="_new" class="pageTitle"><display:get name="@prm.process.diagram.name" /></a></td></tr>
    <tr><td align=left><a href="ProcessDiagram.jsp" target="processDiagram"><display:img src="@prm.process.diagram.img.logo.src" /></a></td></tr>
    </table>

</display:if>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
