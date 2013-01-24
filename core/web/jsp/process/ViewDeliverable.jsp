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
|   $Revision: 20341 $
|       $Date: 2010-01-28 12:57:14 -0300 (jue, 28 ene 2010) $
|     $Author: ritesh $  
|
|   the main page for a view of a deliverable
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Detailed Deliverable View" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.link.LinkManagerBean,
            net.project.process.DeliverableBean,
            net.project.process.DeliverableList,
            net.project.process.Deliverable,
            net.project.channel.ChannelManager,
            net.project.channel.Channel,
            net.project.process.PhaseBean,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />
<jsp:useBean id="m_deliverable" class="net.project.process.DeliverableBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.PROCESS%>" /> 
<%
if (request.getParameter("rl") != null)
        {
        m_phase.loadDeliverables();
        }

%>
<%
String id = securityProvider.getCheckedObjectID();
if (id.length() != 0)
	{
	m_deliverable.setID(id);
	m_deliverable.load();
	}
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';        
	
function setup() {
	theForm = self.document.mainForm;
	load_menu('<%=user.getCurrentSpace().getID()%>');
	isLoaded = true;

}

function cancel() 
   {
   var theLocation = JSPRootURL + "/process/ViewPhase.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>";
   self.document.location = theLocation;
   }

function reset() 
   { 
   var theLocation = JSPRootURL + "/process/ViewDeliverable.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>";
   self.document.location = theLocation;
   }
   
function viewDeliverable()
   {
   theForm.submit();
   }


function deleteDeliverable()
{
	Ext.MessageBox.confirm('Confirm', '<%=PropertyProvider.get("prm.project.process.deletedeliverable.confirmmsg")%>', function(btn) { 
		if(btn == 'yes') {
			var theLocation = JSPRootURL + "/process/modifyDeliverableProcessing.jsp?action=<%=net.project.security.Action.DELETE%>&module=<%=net.project.base.Module.PROCESS%>&id=<%=m_deliverable.getID()%>&phase_id=<%=m_deliverable.getPhaseID()%>";
			self.document.location = theLocation;
		}	
	});
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=deliverable_main";
	openwin_help(helplocation);
}

</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="1" display='<%=PropertyProvider.get("prm.project.process.viewdeliverable.module.history", new Object[] {m_deliverable.getName()})%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/process/ViewDeliverable.jsp"%>'
						  queryString='<%="module="+net.project.base.Module.PROCESS+"&action="+net.project.security.Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type='custom' label='<%=PropertyProvider.get("prm.project.process.deletedeliverable.label")%>' imageEnabled='<%= PropertyProvider.get("prm.project.process.deletedeliverable.image.on") %>' imageOver='<%= PropertyProvider.get("prm.project.process.deletedeliverable.image.over") %>' function='javascript:deleteDeliverable();'/>
		<tb:button type="personalize_page" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="ViewDeliverable.jsp" name="mainForm">
<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>">
<input type="hidden" name="module" value="<%=net.project.base.Module.PROCESS%>">
<input type="hidden" name="theAction">
<table border="0" width="100%" cellpadding="2" cellspacing="0">

    <tr align="left">      
      <td valign="top" colspan="2"><span class="pageTitle"><%=PropertyProvider.get("prm.project.process.viewdeliverable.switch.label")%>
      		    <select name="id" onChange="javascript: viewDeliverable();">
                    <%
                    DeliverableList m_deliverables = m_phase.getDeliverableList();
                    for (int i=0; i < m_deliverables.size();i++)
                    	{
                    	Deliverable l_deliverable = (Deliverable) m_deliverables.get(i);
                    	String drop_select = "";
                    	if (l_deliverable.getID().equals(m_deliverable.getID()))
                    		drop_select = "SELECTED";	
                    	out.println("<option VALUE=\"" + l_deliverable.getID() + "\"" + drop_select +" >" + HTMLUtils.escape(l_deliverable.getName()) + "</option>");
                    	}
                    %> 
                    </select>
		    </span>&nbsp;</td>
    </tr>
    <tr>
        <td colspan=2>&nbsp;</td>
    </tr>
</table>
</form>

<%
// Initialize the channel manager
//int numChannels = 2;
ChannelManager manager = new ChannelManager(pageContext);
request.setAttribute("module", Integer.toString(net.project.base.Module.PROCESS));

%>
<%--  Deliverable Channel --%>
<%
Channel deliverableDetail = new Channel("DeliverableView_Deliverable_" + m_deliverable.getName());
deliverableDetail.setTitleToken("prm.process.deliverable.info.label");
deliverableDetail.setWidth("100%");
deliverableDetail.setMinimizable(true);
deliverableDetail.setCloseable(false);
deliverableDetail.setInclude("/process/include/deliverableDetail.jsp");
deliverableDetail.addChannelButton("modify", null, SessionManager.getJSPRootURL() + "/process/modifyDeliverable.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.MODIFY + "&id=" + m_deliverable.getID());
manager.addChannel(deliverableDetail, 0, 0);
%>
<%-- Links Channel --%>
<%
display_linkMgr.setView(display_linkMgr.VIEW_ALL);
display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
display_linkMgr.setRootObject(m_deliverable);
Channel linksList = new Channel("DeliverableView_Links_" + m_deliverable.getName());
linksList.setTitle(PropertyProvider.get("prm.project.process.viewdeliverable.channel.links.title"));
linksList.setWidth("100%");
linksList.setMinimizable(true);
linksList.setCloseable(false);
linksList.setInclude("/link/displayLinks.jsp");
linksList.addChannelButton("modify", null, SessionManager.getJSPRootURL() + "/process/addLink.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.MODIFY + "&id=" + m_deliverable.getID() + "&context=" + net.project.link.ILinkableObject.GENERAL + "&view=" + display_linkMgr.VIEW_ALL);
manager.addChannel(linksList, 1, 0);
%>

<%
// Links display requires id passed to it
request.setAttribute("id", m_deliverable.getID());
manager.display();
%>        

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
