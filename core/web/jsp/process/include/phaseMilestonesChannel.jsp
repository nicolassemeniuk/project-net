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
    info="Phase milestones channel" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.schedule.*, 
	net.project.security.*,
	net.project.channel.*, 
	net.project.process.*,net.project.base.Module,
    net.project.security.SessionManager,
            net.project.base.finder.NumberFilter,
            net.project.base.finder.NumberComparator,
            net.project.schedule.report.PhaseFilter,
            net.project.schedule.report.TaskTypeFilter"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />                                    
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />

<%@ include file="/base/taglibInclude.jsp" %>

<%
    //String baseUrl = SessionManager.getJSPRootURL();
    schedule.clearFinderFilterList();
    schedule.setSpace(user.getCurrentSpace());
    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
    pageContext.setAttribute("schedule", schedule);

    // load the schedule
    schedule.load();

    // sort column by start_date
    schedule.setOrder("5");
    schedule.setOrderDirection(0);

    // Load all entries
    schedule.setMaximumEntries(-1);

    PhaseFilter pf = new PhaseFilter(m_phase.getID());
    pf.setPhaseID(m_phase.getID());
    pf.setSelected(true);
    schedule.addFinderFilter(pf);

    TaskTypeFilter milestonesOnly = new TaskTypeFilter("milestonesOnly");
    milestonesOnly.setSelected(true);
    milestonesOnly.setLoadMilestones(true);
    milestonesOnly.setLoadTasks(false);
    schedule.addFinderFilter(milestonesOnly);
 
    // load Milestone entries
    // We avoid loading dependencies and assignments to improve
    // performance; we don't care about those
    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
    schedule.loadEntries(false, false);

    // Reset settings
    schedule.clearFinderFilterList();
    schedule.setMaximumEntries(-1);
    schedule.setOrder("0");

    String refLink = SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + Module.PROCESS+"&action="+net.project.security.Action.VIEW+"&id="+m_phase.getID();
    String refLinkEncoded ="refLink="+java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
%>


<%-- Apply stylesheet to format task channel 
<jsp:setProperty name="schedule" property="stylesheet" value="/process/include/phase-milestones-channel.xsl" />
<jsp:getProperty name="schedule" property="presentation" />	  
--%>

<pnet-xml:transform name="schedule" scope="session" stylesheet="/process/include/phase-milestones-channel.xsl">
 	<pnet-xml:property name="refLink" value="<%= refLinkEncoded %>" />
 	<pnet-xml:property name="JSPRootURL" value="<%= SessionManager.getJSPRootURL()%>" /> 
</pnet-xml:transform>


