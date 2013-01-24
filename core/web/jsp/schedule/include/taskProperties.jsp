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
    info="Task View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.util.DateFormat,
            net.project.security.SessionManager,
            net.project.chargecode.ChargeCodeManager,
            net.project.hibernate.service.ServiceFactory,
            net.project.hibernate.model.PnChargeCode"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="scheduleEntry" type="net.project.schedule.ScheduleEntry" scope="request" />
<jsp:useBean id="user" type="net.project.security.User" scope="session" />

<%
    DateFormat dateFormat = user.getDateFormatter();
%>


<tr><td colspan="6">&nbsp;</td></tr>

	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader" width="25%"><%=PropertyProvider.get("prm.schedule.taskedit.name.label")%>&nbsp;</td>
		<td class="tableContent">
           <jsp:getProperty name="scheduleEntry" property="name" />
		</td>
        <td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.priority.label")%>&nbsp;</td>
        <td class="tableContent">
            <jsp:getProperty name="scheduleEntry" property="priorityString" />
	</tr>
	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.description.label")%>&nbsp;</td>
		<td class="tableContent">
			<%= net.project.util.HTMLUtils.escape(scheduleEntry.getDescription())%>
		</td>
		<td class="tableHeader"><display:get name="prm.schedule.taskedit.taskcalculationtype.label" />&nbsp;</td>
		<td class="tableContent">
			<%=scheduleEntry.getTaskCalculationType().formatDisplay()%>
		</td>
	</tr>

    <tr><td colspan="6">&nbsp;</td></tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.startdate.label")%>&nbsp;</td>
		<td class="tableContent">
            <div id="startTimeString"><%=dateFormat.formatDateTime(scheduleEntry.getStartTime())%></div>
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.finishdate.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <div id="endTimeString"><%=dateFormat.formatDateTime(scheduleEntry.getEndTime())%></div>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.actualstartdate.label")%>&nbsp;</td>
		<td class="tableContent">
            <%=dateFormat.formatDateTime(scheduleEntry.getActualStartTime())%>
			&nbsp;
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.actualfinishdate.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=dateFormat.formatDateTime(scheduleEntry.getActualEndTime())%>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.work.label")%>
		</td>
		<td class="tableContent">
            <div id="workFormatted"><%=scheduleEntry.getWorkTQ().toShortString(0,2)%></div>
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.workcomplete.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=scheduleEntry.getWorkCompleteTQ().toShortString(0,2)%>
			&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.duration.label")%>
		</td>
		<td class="tableContent">
            <div id="durationFormatted"><%=scheduleEntry.getDurationTQ().toShortString(0,2)%></div>
		</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.workpercentcomplete.label")%>&nbsp;</td>
		<td nowrap class="tableContent">
            <%=scheduleEntry.getWorkPercentComplete()%>
			&nbsp;
		</td>
	</tr>

	<%  if (PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
			PnChargeCode chargeCode = ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeApliedOnTask(Integer.valueOf(scheduleEntry.getID()),Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));
			if(chargeCode != null) {
	%>
	<tr align="left" valign="middle">
		<td>&nbsp;</td>
		<td nowrap class="tableHeader"><%=PropertyProvider.get("prm.business.chargecode.label")%>
		</td>
		<td class="tableContent">
            <div id="durationFormatted"><%=chargeCode.getCodeName()%></div>
		</td>
		<td nowrap class="tableHeader">&nbsp;</td>
		<td nowrap class="tableContent">&nbsp;</td>
	</tr>
	<% }}%>
	<tr><td colspan="6">&nbsp;</td></tr>
