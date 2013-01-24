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
    info="Milestone Channel"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.schedule.*, net.project.security.*,net.project.channel.*,
		    net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
            net.project.calendar.PnCalendar,
            net.project.util.DateRange,
            net.project.base.finder.NumberFilter,
            net.project.base.finder.NumberComparator"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />

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

    //schedule.setStartDateFilter(cal.getMidnight());
    NumberFilter percentCompleteFilter = new NumberFilter("percentComplete", TaskFinder.PERCENT_COMPLETE_COLUMN, false);
    percentCompleteFilter.setSelected(true);
    percentCompleteFilter.setComparator(NumberComparator.LESS_THAN);
    percentCompleteFilter.setNumber(100);
    schedule.addFinderFilter(percentCompleteFilter);

    // load Milestone entries
    // We avoid loading dependencies and assignments to improve
    // performance; we don't care about those
    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
    schedule.loadEntries(new TaskType[] {TaskType.MILESTONE}, false, false);

    // Reset settings
    schedule.clearFinderFilterList();
    schedule.setMaximumEntries(-1);
    schedule.setOrder("0");
%>

<%-- Apply stylesheet to format milestone channel --%>
<pnet-xml:transform stylesheet="/schedule/xsl/milestone-channel.xsl" name="schedule" scope="page"/>



