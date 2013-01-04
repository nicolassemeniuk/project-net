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
    info="Phase View Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.ProcessBean,
            net.project.channel.Channel,  
            net.project.channel.ChannelManager,
            net.project.process.PhaseBean, 
            net.project.process.Phase,net.project.base.Module,
            net.project.process.PhaseList, 
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="session" />
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.PROCESS%>" /> 
<%
String id = securityProvider.getCheckedObjectID();

if (request.getParameter("rl") != null)
        {
        m_process.loadPhases();
        }

m_process.loadProcess(user.getCurrentSpace().getID());

if (id.length() != 0)
	{
	m_phase.setID(id);
	m_phase.load();
	m_phase.loadDeliverables();
	}

if ((request.getParameter("theAction") != null) && request.getParameter("theAction").equals("workflow"))
{
	pageContext.forward("/workflow/envelope/EnvelopeWizardStart.jsp?id=" + request.getParameter("selected"));
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
	var noSelectionErrMes = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';
	
function setup() {
	theForm = self.document.mainForm;
	load_menu('<%=user.getCurrentSpace().getID()%>');
	isLoaded = true;
}

function cancel() 
   {
   var theLocation = JSPRootURL + "/process/Main.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>";
   self.document.location = theLocation;
   }

// Method to delete deliverable
function deleteDeliverable() {
	var deliverableID = 0;
	var channelForm = self.document.channelForm;
	if(channelForm.selected != null && typeof channelForm.selected != 'undefined') {
		if(channelForm.selected.length > 1) {
			for (var deliverableIndex = 0; deliverableIndex < channelForm.selected.length; deliverableIndex++) {
	   			if (channelForm.selected[deliverableIndex].checked) {
	      			deliverableID = channelForm.selected[deliverableIndex].value;
	      		}
			}
		 } else {
				if(channelForm.selected.checked) {
					deliverableID = channelForm.selected.value;
				}
		 }
		 if(deliverableID != 0) {
				Ext.MessageBox.confirm('Confirm', '<%=PropertyProvider.get("prm.project.process.deletedeliverable.confirmmsg")%>', function(btn) { 
					if(btn == 'yes') {
						var theLocation = JSPRootURL + "/process/modifyDeliverableProcessing.jsp?action=<%=net.project.security.Action.DELETE%>&module=<%=net.project.base.Module.PROCESS%>&id="+deliverableID+"&phase_id=<%=m_phase.getID()%>";
						self.document.location = theLocation;
					}
				});
		 } else {
				extAlert('Error', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', Ext.MessageBox.ERROR);
		 }
	 } else {
	 		extAlert('Error', '<%=PropertyProvider.get("prm.project.process.nodeliverableinlist.error.message")%>', Ext.MessageBox.ERROR);
	 }
}   

function reset() 
   { 
   var theLocation = JSPRootURL + "/process/ViewPhase.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>";
   self.document.location = theLocation;
   }
   
function viewPhase()
   {
   theForm.submit();
   }
   
function viewDeliverable(i)
   {
   document.deliverableForm.id.value = i;
   document.deliverableForm.submit();
   }
   
   
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=phase_main";
	openwin_help(helplocation);
}

function workflow() {
	if (!workflow) {
		var workflow = openwin_wizard("workflow");
		if (workflow) {
			theAction("workflow");
			theForm.target = "workflow";
			theForm.selected.value=<%=m_phase.getID()%>;
			theForm.submit();
			workflow.focus();
		}
	}
}
</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=m_phase.getName()%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp"%>'
					queryString='<%="module="+net.project.base.Module.PROCESS+"&action="+net.project.security.Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="workflow" />
		<tb:button type="personalize_page"/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action= "<%=SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp"%>" name="mainForm">
<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>">
<input type="hidden" name="module" value="<%=net.project.base.Module.PROCESS%>">
<input type="hidden" name="theAction">
<input type="hidden" name="selected">
<table border="0" width="100%" cellpadding="0" cellspacing="0">	
    <tr align="left">      

      <td valign="top" colspan="2"><span class="pageTitle"><%=PropertyProvider.get("prm.project.process.viewphase.switch.label")%>
                    <select name="id" onChange="javascript:viewPhase();">
                    <%
                    PhaseList m_phases = m_process.getPhaseList();
                    for (int i=0; i < m_phases.size();i++)
                    	{
                    	Phase l_phase = (Phase) m_phases.get(i);
                    	String drop_select = "";
                    	if (l_phase.getID().equals(m_phase.getID()))
                    		drop_select = "SELECTED";	
                    	out.println("<option value=\"" + l_phase.getID() + "\"" + drop_select +" >" + net.project.util.HTMLUtils.escape(l_phase.getName()) + "</option>");
                    	}
                    %> 
                    </select>
		    </span>&nbsp;</td>
    </tr>
    <tr>
        <td colspan=2>&nbsp;</td>
    </tr>
    </form>
    <form method="get" name="deliverableForm" action="<%= SessionManager.getJSPRootURL() %>/process/ViewDeliverable.jsp">
    <input type="hidden" name="id">
    <input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>">
    <input type="hidden" name="module" value="<%=net.project.base.Module.PROCESS%>">
    </form>
</table>
<form method="get" acton="" name="channelForm">
<%
// Initialize the channel manager
//int numChannels = 5;
int pos = 0;
ChannelManager manager = new ChannelManager(pageContext);

request.setAttribute("module", Integer.toString(net.project.base.Module.PROCESS));

// Phase Channel
Channel phaseDetail = new Channel("PhaseView_Phase_" + m_phase.getName());
phaseDetail.setTitleToken("prm.project.process.phase.label");
phaseDetail.setWidth("100%");
phaseDetail.setMinimizable(true);
phaseDetail.setCloseable(true);
phaseDetail.setInclude("/process/include/phaseDetail.jsp");
phaseDetail.addChannelButton("modify", null, SessionManager.getJSPRootURL()+"/process/modifyPhase.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.MODIFY + "&id=" + m_phase.getID());
manager.addChannel(phaseDetail, pos++, 0);

// Gate Channel
Channel gateDetail = new Channel("PhaseView_Gate_" + m_phase.getName());
gateDetail.setTitleToken("prm.project.process.gate.label");
gateDetail.setWidth("100%");
gateDetail.setMinimizable(true);
gateDetail.setCloseable(true);
if (m_phase.hasGate()) {
	gateDetail.setInclude("/process/include/gateDetail.jsp");
	gateDetail.addChannelButton("modify", null,SessionManager.getJSPRootURL()+"/process/modifyGate.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.MODIFY + "&id=" + m_phase.getGate().getID());} else {
	gateDetail.addChannelButton("create", PropertyProvider.get("prm.project.process.viewphase.gate.add.button.label"), SessionManager.getJSPRootURL()+"/process/modifyGate.jsp?module="+net.project.base.Module.PROCESS+"&action="+net.project.security.Action.CREATE+"&id="+m_phase.getID());
}
manager.addChannel(gateDetail, pos++, 0);

// Deliverables Channel
Channel deliverablesList = new Channel("PhaseView_Deliverables_" + m_phase.getName());
deliverablesList.setTitleToken("prm.project.process.deliverables.label");
deliverablesList.setWidth("100%");
deliverablesList.setMinimizable(true);
deliverablesList.setCloseable(true);
deliverablesList.setInclude("/process/include/deliverableList.jsp");
deliverablesList.addChannelButton("create", PropertyProvider.get("prm.project.process.viewphase.deliverable.create.button.label"), SessionManager.getJSPRootURL()+"/process/modifyDeliverable.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.CREATE + "&id=" + m_phase.getID());
deliverablesList.addChannelButton("remove", "Delete Deliverable", "javascript:deleteDeliverable();");
manager.addChannel(deliverablesList, pos++, 0);

// Milestones Channel
Channel milestones = new Channel("PhaseView_Milestones_" + m_phase.getName());
milestones.setTitleToken("prm.project.process.milestones.label");
milestones.setWidth("100%");
milestones.setMinimizable(true);
milestones.setCloseable(true);
milestones.setInclude("/process/include/phaseMilestonesChannel.jsp");
manager.addChannel(milestones, pos++, 0);

// Tasks Channel
Channel tasks = new Channel("PhaseView_Tasks_" + m_phase.getName());
tasks.setTitleToken("prm.project.process.tasks.label");
tasks.setWidth("100%");
tasks.setMinimizable(true);
tasks.setCloseable(true);
tasks.setInclude("/process/include/phaseTasksChannel.jsp");

// Construct a reflink 
String refLink = "/process/ViewPhase.jsp?module=" + Module.PROCESS+"&action="+net.project.security.Action.VIEW+"&id="+m_phase.getID();
String refLinkEncoded = java.net.URLEncoder.encode(refLink);

tasks.addChannelButton("create", PropertyProvider.get("prm.project.process.viewphase.task.add.button.label"), SessionManager.getJSPRootURL()+"/servlet/ScheduleController/TaskCreate?module="+net.project.base.Module.SCHEDULE+"&action="+net.project.security.Action.CREATE+"&phaseID="+m_phase.getID()+"&refLink="+refLinkEncoded);
manager.addChannel(tasks, pos++, 0);

manager.display();
%>
</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
