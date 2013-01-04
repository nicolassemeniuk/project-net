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
    info="Allows user to update completion and work information for assignments."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.util.DateFormat,
            net.project.util.NumberFormat,
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider,
            net.project.util.TimeQuantityUnit,
            java.util.Date,
            java.util.Iterator,
            net.project.resource.Assignment,
            net.project.resource.ScheduleEntryAssignment,
            net.project.resource.ActivityAssignment,
            net.project.form.assignment.FormAssignment,
            net.project.util.Validator,
            net.project.util.TimeQuantity,
            net.project.resource.mvc.handler.AssignmentDate,
            net.project.resource.mvc.handler.UpdateAssignmentsHandler,
            java.math.BigDecimal,
            net.project.calendar.PnCalendar,
            net.project.security.User,
            net.project.util.HTMLUtils,
            net.project.resource.PersonProperty,
    		java.util.Calendar,
    		net.project.base.ObjectType,
    		java.util.List,
    		java.util.ArrayList,
    		org.apache.commons.lang.StringUtils,
    		net.project.schedule.TaskType,
    		org.apache.commons.collections.CollectionUtils"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="dateHeaders" type="java.util.List" scope="request"/>
<jsp:useBean id="assignments" type="java.util.List" scope="request"/>
<jsp:useBean id="dateLongNames" type="java.util.List" scope="request"/>
<jsp:useBean id="dateValues" type="java.util.Map" scope="request"/>
<jsp:useBean id="grandTotalValues" type="java.util.Map" scope="request"/>
<jsp:useBean id="showFilterPane" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="startOfWeek" type="java.util.Date" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="spaceAdminAccessCache" type="java.util.List" scope="session" />
<jsp:useBean id="property" class="net.project.resource.PersonProperty" scope="session" />
<jsp:useBean id="allWidth" type="java.lang.String" scope="request"/>
<jsp:useBean id="assignmentlistHeight" type="java.lang.String" scope="request"/>
<jsp:useBean id="blogPanelHeight" type="java.lang.String" scope="request"/>
<jsp:useBean id="splitterHeight" type="java.lang.String" scope="request"/>
<jsp:useBean id="assignmentStatus" type="java.lang.String" scope="request"/>
<% 
    NumberFormat nf = NumberFormat.getInstance();
    boolean readOnly = Boolean.valueOf(request.getParameter("readOnly")).booleanValue();
    boolean isFromTimeSheet = Boolean.valueOf(request.getParameter("isFromTimeSheet")).booleanValue();
    boolean isAccessForUser =  Boolean.valueOf(request.getParameter("isAccessForOtherUser")).booleanValue();
    String baseUrl = SessionManager.getJSPRootURL();
    String userId = request.getParameter("personId");
	boolean showBlogsOnRight = PropertyProvider.getBoolean("prm.resource.timesheet.blogpostion.isenabled");
	boolean showWeeklyTotal = PropertyProvider.getBoolean("prm.resource.timesheet.weeklytotalcolumn.isenabled");
	boolean showProjectTotal = PropertyProvider.getBoolean("prm.resource.timesheet.projecttotalcolumn.isenabled");
	boolean isEditMode = Boolean.valueOf(request.getParameter("isEditMode")).booleanValue();
	boolean isActualWork = Boolean.valueOf(request.getParameter("isActualWork")).booleanValue();
	boolean isPercentComplete = Boolean.valueOf(request.getParameter("isPercentComplete")).booleanValue();
	boolean isCollapse;
	DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
	String objectIds = StringUtils.EMPTY;
	String projectIds = StringUtils.EMPTY;
	String longValues = StringUtils.EMPTY;
	String[] timeValues = new String[7];
	timeValues = (String[])dateLongNames.toArray(timeValues);
	int colSpanCount = 8;
%>
<%!
// Method for retrieving space admin details from spaceAdminAccessCache
private boolean getSpaceAdminDetails(List spaceAdminAccessCache, String spaceID){
	try {
		return CollectionUtils.isNotEmpty(spaceAdminAccessCache) && spaceAdminAccessCache.contains(spaceID);
	} catch(Exception e) {
		System.out.println("Error occured while getting space admin detail.."+e.getMessage());
	}
	return false;
}

private String getVisiblityClass(PersonProperty props, String id, boolean collapseNode) {
    String[] expandedProps = props.get("prm.resource.timesheet", "node"+id+"expanded", true);
    String expandedProp = (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: "true");
    if(expandedProp != null && expandedProp.equals("true")){
        return collapseNode ? " h": " v";
    }else{
        return collapseNode ? " v": " h";
    }
}
%>
<style>
	.main-div {
		<%=splitterHeight%>
	}
	.timesheet-left-table {
		width: <%=allWidth%>;
	}
	#assignment-list {
		width : <%=allWidth%>;
		overflow: auto;
		<%=assignmentlistHeight%>
	}
	#timecard-footer {
		text-align: center;
		width: <%=allWidth%>;
	}
	#splitterBar {
		<%=splitterHeight%>
	}
	#splitterBarShadow {
		<%=splitterHeight%>
	}
	#taskBlogDivRight {
		overflow: auto; 
		text-align:left;
		<%=blogPanelHeight%>
	}
</style>
<script language="javascript" type="text/javascript">
	var showBlogsOnRight = <%=showBlogsOnRight%>;
	var moduleId = <%=Module.PERSONAL_SPACE%>;
	var userId = <%=userId%>;
	var errorMsg = '<%=PropertyProvider.get ("prm.resource.timesheet.javascript.name.alert.message")%>';
	var errorMessage = '<%= PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", "0", "24")%>';
	var blogConfirmMessage = '<display:get name="prm.resource.timesheet.blogentryconfirm.message" />';
	var captureWorkMessage = '<display:get name="prm.resource.timesheet.capturework.message" />';
	var blogEntryContentErrorMessage = '<display:get name="prm.blog.addweblogentrycomment.validation.message" />';
	var workCaptureCommentErrorMessage = '<display:get name="prm.resource.timesheet.captureworkcomment.message" />';
	var workCaptureInvalid = '<display:get name="prm.resource.timesheet.invalidworkCapture.message" />';
	var actionsIconEnabled = <%=PropertyProvider.getBoolean("prm.global.actions.icon.isenabled")%>;
	var modifiedMessage = '<display:get name="prm.resource.timesheet.modified.confirm.message" />';
	var executed = true;
	var resetConfirmMessage = '<display:get name="prm.resource.timesheet.resetblogentry.confirm.message" />';
	var blogPostSubjectConfirm = '<display:get name="prm.resource.timesheet.blogentrypost.confirm.message" />';
	var collapseAllLink = '<display:get name="prm.resource.timesheet.collapseall.link" />';
	var expandAllLink = '<display:get name="prm.resource.timesheet.expandall.link" />';
	var totalWorkHourInvalid = '<display:get name="prm.resource.timesheet.moreworkinvalid.error" />';
	var blogSubject = '<display:get name="prm.resource.timesheet.inlineblogit.subject.emptytext" />';
	var blogMessage = '<display:get name="prm.resource.timesheet.inlineblogit.message.emptytext" />';
	var loadingMessage =  '<display:get name="prm.resource.timesheet.loading.message" />';
	var nonWorkingDayInvalid = '<display:get name="prm.resource.timesheet.nonworkingwork.errormessage" />';
	var scheduleModule = <%=Module.SCHEDULE%>;
	var formModule = <%=Module.FORM%>;
	var action = <%=Action.VIEW%>;
	var haMsg = '<display:get name="prm.resource.timesheet.hoursadded.label" />';
	var doneMsg = '<display:get name="prm.blog.timesheet.done.option.title" />';
	var blMsg = '<display:get name="prm.resource.timesheet.blogitmessage.label" />';
	var submitLink = '<display:get name="prm.resource.timesheet.submit.link" />';
	var resetLink = '<display:get name="prm.resource.timesheet.reset.link" />';
	var colSpanCount = <%=colSpanCount%>;
	var blogPostMessage = '<display:get name="prm.resource.timesheet.blogpost.message" />';
	var blogPostsMessage = '<display:get name="prm.resource.timesheet.blogposts.message" />';
	var noBlogPostsMessage = '<display:get name="prm.resource.timesheet.blogposts.empty.message" />';
	var reBlogMessage = '<display:get name="prm.resource.timesheet.inlineblogit.taskname.caption" />';
	var negativeWorkInvalid = '<display:get name="prm.resource.updatework.total.negative.error.message" />';
	
	//Use current user's locale decimal seperator
	var dec_separator = '<%="" + NumberFormat.getInstance().getDecimalSeparator()%>';
	var isExpandedTimeSheetData = false;
	//Function for submitting form the capturing work
	function submitFormAfterWorkLog(isCurrentWeek){
		if(isCurrentWeek) {
			<%PnCalendar todayCal = new PnCalendar();
			todayCal.setTime(new Date());%>
			self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE + "&action="
					+ Action.MODIFY + "&startDate=" + todayCal.startOfWeek(new Date()).getTime() + "&isFromTimeSheet=true&personId=" + userId
					+ "&assignmentStatus=" + assignmentStatus +"&isEditMode="+isEditMode+"&enableBlogsRightTab="
					+ showBlogsOnRight +"&isActualWork="+ isActualWork +"&isPercentComplete="+isPercentComplete%>";
		} else {
			self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE + "&action="
					+ Action.MODIFY + "&startDate=" + startOfWeek.getTime() + "&isFromTimeSheet=true&personId=" + userId
					+ "&assignmentStatus=" + assignmentStatus +"&isEditMode="+isEditMode+"&enableBlogsRightTab="
					+ showBlogsOnRight +"&isActualWork="+ isActualWork +"&isPercentComplete="+isPercentComplete%>";
		}
	}
	
	// Show full project name in title for the expand / collapse icon
	function po(sid, collapsedCell){
		if(!collapsedCell) {
			document.getElementById('imgId_'+sid).title = document.getElementById('p'+sid).innerHTML.trim();
		} else {
			document.getElementById('imgId_'+sid).title = document.getElementById('c'+sid).innerHTML.trim();
		}
	}
	
	// Show full assignment name in title 
	function mo(obj, id) {
		obj.title = document.getElementById('a'+id).innerHTML.trim();
	}
</script>

<!--	Today's Date-->
	<div class="tblock" onclick="submitFormAfterWorkLog(true)" title="<%=PropertyProvider.get("prm.resource.timesheet.jumpto.today.title")%>">
		<table cellspacing="0" width="100%" cellpadding="0" height="100%">
       		<tr><td class="tlabel"><display:get name="prm.resource.timesheet.today.label" /></td></tr>
    		<tr><td class="tday"><%=DateFormat.getInstance().formatDate(new Date(), "MMM")%></td></tr>
			<tr><td class="tdate"><%=DateFormat.getInstance().formatDate(new Date(), "d")%></td></tr>
    	</table>
	</div>

<!--	Filters-->
    <div id="fMDiv">
	    <div class="filter-div">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td nowrap="nowrap" class="filter-label"><display:get name="prm.resource.timesheet.viewassignments.label" /></td>
					<td>
						<select name="fValue" onchange="javascript:submitAssignmentCriteria();">
							<option <%= assignmentStatus != null && assignmentStatus.equals("currentassignment") ? "SELECTED" : "" %> value="currentassignment"><display:get name="prm.resource.timesheet.filter.currentassignments.label" /></option>
							<option <%= assignmentStatus != null && assignmentStatus.equals("completedassignment") ? "SELECTED" : "" %> value="completedassignment"><display:get name="prm.resource.timesheet.filter.completedassignments.label" /></option>
							<option <%= assignmentStatus != null && assignmentStatus.equals("allassignment") ? "SELECTED" : "" %> value="allassignment"><display:get name="prm.resource.timesheet.filter.allassignments.label" /></option>
							<option <%= assignmentStatus != null && assignmentStatus.equals("allworkcaptured") ? "SELECTED" : "" %> value="allworkcaptured"><display:get name="prm.resource.timesheet.filter.allworkcaptured.label" /></option>
							<option <%= assignmentStatus != null && assignmentStatus.equals("prior_six") ? "SELECTED" : "" %> value="prior_six"><display:get name="prm.resource.timesheet.filter.priorsix.label" /></option>
							<option <%= assignmentStatus != null && assignmentStatus.equals("future_six") ? "SELECTED" : "" %> value="future_six"><display:get name="prm.resource.timesheet.filter.futuresix.label" /></option>
						</select>
					</td>
				</tr>
				<tr><td colspan="3" class="filter-separator"></td></tr>
				<tr>
					<td class="filter-label"><display:get name="prm.resource.timesheet.jumptodate.label" />:</td>
					<td id="jumptodate">
						<input:text elementID="jumpTimeString" name="jumpTimeString" size="10" maxLength="10" onChange="jumpToDateEntered();"/>
						<util:insertCalendarPopup fieldName="jumpTimeString" rootURL="<%=baseUrl%>"/>
					</td>
					<td>
						<table cellpadding="0" cellspacing="0">
							<tr><td id="errorreporter">
								<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
							</td>
						<%-- Provide a div for server round-trip error messaging --%>
							<td class="img-padding">
								<div id="errorLocationID" class="errorMessage h"></div>
							</td></tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
		<%
		    UpdateAssignmentsHandler.DateHeader weekString = new UpdateAssignmentsHandler.DateHeader();
		    String monthName = (String) request.getAttribute("monthName");
		    String weekStartDate = (String) request.getAttribute("weekStartDate");
		    String weekEndDate = (String) request.getAttribute("weekEndDateString");
		    String weekStart = dateFormat.formatDate(dateFormat.parseDateString(weekStartDate),"d");
		    String weekEnd = dateFormat.formatDate(dateFormat.parseDateString(weekEndDate),"d");
		    String weekStartMonthName = (String) request.getAttribute("weekStartMonthName");
		    String weekEndMonthName = (String) request.getAttribute("weekEndMonthName");
		    String endMonthName = (String)request.getAttribute("endMonthName");
		    String newMonthName = (!monthName.equals(endMonthName)) ? endMonthName : monthName;
	    %>
	
	<div id="Timesheet" onmousemove="getPos(event)" class="main-div">
		<table cellpadding="0" cellspacing="0" id="timesheet-table">
		<tr>
		<td id="assignmentList">
		
		<div id="header-main-div">
			<div id="timecard-header-div">
				<table id="timecard-header" border="0" cellpadding="0" cellspacing="0" class="timesheet-left-table tableContent disableSelection">
	        	<tr class="lightHeader">
				    <td nowrap="nowrap" class="leftHeader" rowspan="2" id="assignments-header"><b><display:get name="prm.resource.timesheet.projectandtask.label"/></b>
				    <br/> 
				    	<div id="expandCollapseLink">
					    	<% if(assignments.size() > 0) { %>
						    	<a id="pnet-links" class="pnet-links" style="color:#3399FF;" onclick="javascript: expandCollapseAll(false);" title="<%=PropertyProvider.get("prm.resource.timesheet.expandall.link")%>"><display:get name="prm.resource.timesheet.expandall.link" /></a>
								<span id="separator"> | </span>
								<a id="pnet-links" class="pnet-links" style="color:#3399FF;" onclick="javascript: expandCollapseAll(true);" title="<%=PropertyProvider.get("prm.resource.timesheet.collapseall.link")%>"><display:get name="prm.resource.timesheet.collapseall.link" /></a>							
					    	<% } else { %>
					    		<a id="pnet-disabled-links" class="pnet-disabled-links" onclick="javascript:function disable(){}" style="color: silver;" title="<%=PropertyProvider.get("prm.resource.timesheet.expandall.link")%>"><display:get name="prm.resource.timesheet.expandall.link" /></a>
					    		<span id="separator"> | </span>
					    		<a id="pnet-disabled-links" class="pnet-disabled-links" onclick="javascript:function disable(){}" style="color: silver;" title="<%=PropertyProvider.get("prm.resource.timesheet.collapseall.link")%>"><display:get name="prm.resource.timesheet.collapseall.link" /></a>
					    	<% } %>
					    </div> 
				    </td>
				    <td colspan="<%=colSpanCount-1%>" class="monthScroll" nowrap="nowrap">
				    	<table cellspacing="0" cellpadding="0" width="100%">
					    	<tr>
						    	<td class="ar">
						        	<a href="javascript: scrollBack();"><img border="0" src="<%= SessionManager.getJSPRootURL()%>/images/personal/dashboard_arrow-left.gif" class="img-padding" title="<%=PropertyProvider.get("prm.resource.timesheet.previousweek.tooltip")%>"/></a>
						        </td>
						        <td class="mn">
						    		<b><%=monthName%> <%=weekStart%> - <%= !monthName.equals(endMonthName) ? endMonthName : ""%> <%= weekEnd%></b>
						    	</td>
						    	<td>
									<a href="javascript: scrollForward();"><img border="0" src="<%= SessionManager.getJSPRootURL()%>/images/personal/dashboard_arrow-right.gif" class="img-padding" title="<%=PropertyProvider.get("prm.resource.timesheet.nextweek.tooltip")%>"/></a>
								</td>
							</tr>
						</table>
					</td>
					<% if(isPercentComplete){ %>
				    	<td class="left-bottom-border" rowspan="2"><b><display:get name="prm.resource.assignments.update.columns.percentcomplete"/></b></td>
				    <%colSpanCount++; } if(isActualWork){ %>
	                <td class="left-bottom-border" rowspan="2"><b><display:get name="prm.resource.timesheet.actualwork.label"/></b></td>
	                <% colSpanCount++; } %>
	                <% if(showWeeklyTotal) { %>
				    <td class="lightHeader left-bottom-border" rowspan="2"><display:get name="prm.resource.timesheet.weeklytotal.label"/></td> 
				    <% colSpanCount++; } 
	                   if(showProjectTotal) { %>
				    <td class="lightHeader left-bottom-border" rowspan="2" id="projecttotal-header"><display:get name="prm.resource.timesheet.projecttotal.label"/></td>
				    <% colSpanCount++; } %>
				</tr>
	            <tr class="tH">
	                <%
	                    for (Iterator it = dateHeaders.iterator(); it.hasNext();) {
	                        UpdateAssignmentsHandler.DateHeader dateHeader = (UpdateAssignmentsHandler.DateHeader) it.next();
	                        String dayToday = dateFormat.formatDate(dateFormat.parseDateString(dateHeader.date),"d");
	        				String newDate = dateFormat.formatDate(Calendar.getInstance().getTime(), "MMM dd");
	        				String todayDate = dateFormat.formatDate(dateFormat.parseDateString(dateHeader.date), "MMM dd");
	                        if (dateHeader.isWorkingDay) {
	                        	if(todayDate.equals(newDate)) { %>
	                        		<td class="date-today-header weekday-border">
				                     	<%= dateHeader.dayOfWeek %>&nbsp;<font class="tfs"><%=dayToday%></font>
	            				    </td>
	                        	<% } else {%>
		                     <td class="updateAssignmentProjectTotal weekday-border">
		                     	<%= dateHeader.dayOfWeek %>&nbsp;<font class="dfs"><%=dayToday%></font>
		                     </td>
	                		<% }
	                	} else { 
	                		if(todayDate.equals(newDate)) { %>
	                	<td align="center" class="nonWorkingDay-header-today weekday-border" height="25px">
		                	 <%= dateHeader.dayOfWeek %>&nbsp;<font class="dfs"> <%=dayToday%></font>
	                	 </td>
	                	<% } else { %>
	                	 <td align="center" class="nonWorkingDay-header weekday-border" height="25px" >
		                	 <%= dateHeader.dayOfWeek %>&nbsp;<font class="dfs"><%=dayToday%></font>
	                	 </td>
	                <% 		}
	                	}
	                }  %>
	               </tr>
			   </table>
			</div>
		</div>
		
		<div id="assignment-list" onselectstart="return false" onscroll="scrollHeader();">	
		<% if(showBlogsOnRight) {%>
        <table width="100%" height="90%" border="0" id="main-table" cellpadding="0" cellspacing="0">
        <tr><td id="assignmentsDiv" valign="top" height="100%">
        <% } %>
       <% String height = "height=\"auto\"";
       if(assignments.size() == 0){ 
    	   height = "height=\"100%\"";
       }%>
			       
<!--start of timecard -->
        <table id="timecard" <%=height %> border="0" cellpadding="0" cellspacing="0" class="timesheet-left-table tableContent disableSelection">
        	<%
				int rowCount = 0;	
            	int counter = 0;
            	String spaceName = "";
            	int cntCssChange = 0;
            	boolean isUserAccess = true;
            	boolean showNewBlogEntry = false;
            	double projectTotal[] = new double[7];
            	TimeQuantity zeroWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
            	if(assignments.size() == 0){ %>
            		<tr class="tC">
            			<td colspan="<%=colSpanCount %>" class="topBorder">
           					<span class="noContent-row"> <display:get name="prm.resource.assignments.update.noassignmentsmatch"/> </span>
           				</td>
            		</tr>
            	<% } %>
            	<!-- Assignment Div with fixed height-->
               <%for (Assignment assignment : (List<Assignment>)assignments) {
               			double percenComplete = 1.0;
		                double strWork = 0.0;
               		    String actualWork = "";
						TimeQuantity strWorkComplete = TimeQuantity.O_HOURS;
						String strWorkRemaining = TimeQuantity.O_HOURS.toShortString(0,2);
						String taskType = "";

						String spaceID = assignment.getSpaceID();
						if(assignment instanceof ScheduleEntryAssignment) {
							ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;
							percenComplete = seAssignment.getPercentComplete().doubleValue();
							strWork = seAssignment.getWork().getAmount().doubleValue();
							strWorkComplete = seAssignment.getWorkComplete();
							strWorkRemaining = seAssignment.getWorkRemaining().toShortString(0,2);
							taskType = seAssignment.getTaskType();
						}  else if (assignment instanceof FormAssignment) {
							FormAssignment fAssignment = (FormAssignment) assignment;
							percenComplete = fAssignment.getPercentComplete().doubleValue();
							strWork = fAssignment.getWork().getAmount().doubleValue();
							strWorkComplete = fAssignment.getWorkComplete();
							strWorkRemaining = fAssignment.getWorkRemaining().toShortString(0,2);
						}

						objectIds += assignment.getObjectID() + ",";
					
						spaceName = assignment.getSpaceName() == null? SessionManager.getUser().getDisplayName() : assignment.getSpaceName();
                   		if(cntCssChange == 0 || !((Assignment) assignments.get(cntCssChange-1)).getSpaceID().equals(spaceID)) {
	    	               	projectIds += spaceID + ",";
    	    	           	projectTotal = new double[7];
            		%>
           		<tr id="<%=++rowCount%>_row" sid="<%=spaceID%>" lvl="0" ondblclick="rp(<%=spaceID%>)" class="<%=getVisiblityClass(property, spaceID, false)%>"> 
    	        	<td colspan="<%=colSpanCount %>" nowrap="nowrap" class="pn">
		            	<a class="ol" onclick="javascript: cP(<%=rowCount%>);" ondblclick="stopEventPropagationFor(event);">
		            		<img id="imgId_<%=spaceID%>" src="<%=SessionManager.getJSPRootURL()%>/u.gif" onmousemove="po(<%=spaceID%>, false)"/>
						</a>
		           			&nbsp;<span class="pnf" id="p<%=spaceID%>"><%=HTMLUtils.escape(assignment.getSpaceName())%></span>
           			</td>
            	</tr>

				<!--S A -->

				<% } 
				%>
				<tr class="t <%=getVisiblityClass(property, spaceID, false)%>" id="<%=++rowCount%>_row" 
					onclick="b(<%=assignment.getObjectID()%>, <%=(String) dateLongNames.get(dateLongNames.size()-1)%>, this, '<%=monthName%>', '<%=weekEnd%>', '<%=newMonthName%>', <%=assignment.getSpaceID()%>);">
					<td class="tbdr an"
						ondblclick="r('<%=assignment.getObjectType()%>', <%=assignment.getObjectID()%>, <%=assignment.getSpaceID()%>)" onmouseover="mo(this, <%=assignment.getObjectID()%>)">
						<div id="a<%=assignment.getObjectID()%>" class="at">
			           		<%=HTMLUtils.escape(assignment.getObjectName()).replaceAll("\"", "&quot;")%>
						</div>
	                </td>
                <% 

                double weekWorkDates = 0.0f;
                int count = 0;
                for (String dayName : (List<String>)dateLongNames) {
                    double tWork = 0.0f;
                    tWork = dateValues.get(assignment.getObjectID()+"X"+dayName) != null ? (Double)dateValues.get(assignment.getObjectID()+"X"+dayName) : 0.0f;
                    String bWork = (tWork == 0) ? StringUtils.EMPTY : nf.formatNumber(tWork, 0, 2);
                    weekWorkDates += tWork ;
                    String valueName = "dX"+assignment.getObjectID()+"X"+dayName;
                    String dateWithObjectId = "dW"+assignment.getObjectID()+"X"+dayName;
                    String hs = "dH"+assignment.getObjectID()+"H"+dayName;
	                projectTotal[count] += tWork;
                %>
		            <td id='wCF_<%=dateWithObjectId%>' class="ics ws" name="<%=hs%>" ov="<%=tWork%>" onclick="e('<%=dateWithObjectId%>', '<%=taskType%>', '<%=valueName%>',
	            				 <%=assignment.getObjectID()%>, <%=dayName%>, <%=count%>, <%=spaceID%>, <%=rowCount%>);">
	            	
	            		<% if(!nf.formatNumber(tWork, 0, 2).equals("0")) {
            			%>  <%=nf.formatNumber(tWork, 0, 2)%>
            			<% 	} else {%>
							&nbsp;              		
	            <% } count++;%>
					</td>
            	<%
            	}
				 if(showWeeklyTotal) { %>
               		<td class="tlbw">
		              	<span id="wT<%=assignment.getObjectID()%>" class="b">
		               		<%=nf.formatNumber(weekWorkDates, 0, 2) %>
		               	</span>
		            </td>
                <% } if(showProjectTotal) { %>
	                <td class="tlbw">
	                	<span id="at<%=assignment.getObjectID()%>" class="b"><%=nf.formatNumber(strWorkComplete.getAmount().doubleValue(),0,2) %></span>
	                </td>
                <% } %>
            	</tr>
				<!--E A -->

				<% //Print project total for each project
	            if(((counter+1) != assignments.size() && !((Assignment) assignments.get(counter+1)).getSpaceID().equals(spaceID)) ||(counter+1) == assignments.size()) {
                %>
				<!-- P T -->
			    <tr class="ptr <%=getVisiblityClass(property, spaceID, false)%>" lvl="1" id="<%=++rowCount%>_row">
		           	<td class="ptfc" title="<%=PropertyProvider.get("prm.resource.timesheet.totalforproject.label", HTMLUtils.escape(spaceName))%>">
						<div class="at">
			           		<%=PropertyProvider.get("prm.resource.timesheet.totalforproject.label", HTMLUtils.escape(spaceName))%>
						</div>
	               	</td>
				<%  
				 for (int countProject = 0; countProject < 7; countProject++) {
                       String spaceTotal = timeValues[countProject]+"X"+spaceID; %>
                 <%if(countProject == 6){ %>
                 	<td class="lptc" id="pT_<%=spaceTotal%>">
				<% } else {%>
					<td class="iptc" id="pT_<%=spaceTotal%>">
           		<% } String projectTotalDisplay = nf.formatNumber(projectTotal[countProject], 0, 2);%>
						<%=projectTotalDisplay.equals("0.0") ? "0" : projectTotalDisplay %>
                    </td>
                <% }
            	  	if(isPercentComplete) {%>
			        <td class="ptrc">&nbsp;</td>
                <% } if(isActualWork) {%>
					<td class="ptrc">&nbsp;</td>
                <% } if(showWeeklyTotal) {%>
					<td class="iptc">&nbsp;</td>
                <% } if(showProjectTotal) {%>
					<td class="iptc">&nbsp;</td>
                <% } %>
                </tr>
				<!-- E P T -->
			    <% } %>

				<% // Print project total for each project
		            if(((counter + 1) != assignments.size() && !((Assignment) assignments.get(counter+1)).getSpaceID().equals(spaceID)) || (counter+1) == assignments.size()) {
                %>

				<!-- C P T -->
		        <tr id="<%=++rowCount%>_row" lvl="0" sid="<%=spaceID%>" collapsednode="true" class="ptr <%=getVisiblityClass(property, spaceID, true)%>">
					<td class="fpcc" onmousemove="po(<%=spaceID%>, true)">
						<div class="at b">
	                   		<a class="ol" onclick="javascript: eP(<%=rowCount%>);">
	                   			<img id="imgId_<%=spaceID%>" src="<%=SessionManager.getJSPRootURL()%>/e.gif"/>
	                   		</a>
	           				 &nbsp;<span id="c<%=spaceID%>"><%=HTMLUtils.escape(assignment.getSpaceName())%></span>
	           			</div>
	                </td>
				<%
                    for (int countProject = 0; countProject<7; countProject++) {
						String spaceTotal = timeValues[countProject]+"X"+spaceID; %>
           	    	<td class="ipcc" id="pT_<%=spaceTotal%>">
						<% String projectTotalDisplay = nf.formatNumber(projectTotal[countProject], 0, 2); %>
						<%= projectTotalDisplay .equals("0.0") ? "0" : projectTotalDisplay %>
					</td>
				<%} 
                    if(isPercentComplete) {%>
					<td class="ipcc">&nbsp;</td>
				<% } if(isActualWork) {%>
					<td class="ipcc">&nbsp;</td>
				<% } if(showWeeklyTotal) {%>
					<td class="ipcc">&nbsp;</td>
				<% } if(showProjectTotal) {%>
					<td class="ipcc">&nbsp;</td>
				<% }%>
                </tr>
				<!-- E C -->
			    <% }
				    cntCssChange++;
			        counter++;
	            }
		        %>


				<% if(assignments.size() > 0) {
	            	if(!showBlogsOnRight) {%>
            
				<!--START BLOG IN BOTTOM -->
				<tr height="15px;">
	            	<td colspan="<%=colSpanCount %>" class="bbs"></td>
	            </tr>
	            <tr>
	            	<td colspan="<%=colSpanCount %>" class="bbm">
	            		<span class="bbms"><%=PropertyProvider.get("prm.resource.timesheet.blogentries.message", (weekStartMonthName +"&nbsp;"+ dateFormat.formatDate(dateFormat.parseDateString(weekStartDate),"d")), (!monthName.equals(endMonthName) ? endMonthName : "" +"&nbsp;"+ dateFormat.formatDate(dateFormat.parseDateString(weekEndDate),"d")))%>
	            		<span id="blogEntriesHeader"></span></span>
	            	</td>
	            </tr>
	            <tr>
	            	<td colspan="<%=colSpanCount %>" id="TaskBlogDivBottom" class="blogEntriesCountMessage">
	            		<display:get name="prm.resource.timesheet.selectassignment.message" />
	            	</td>
	            </tr>
	            <% } if(showNewBlogEntry && isEditMode && PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled")) { %>
	            <tr>
	            	<td colspan="<%=colSpanCount %>">
	            		<table cellpadding="1" cellspacing="1" class="nbw"> 
	            			<tr> 
			 					<td class="message-subject bbl" nowrap="nowrap" colspan="2">
			 						<display:get name="prm.resource.timesheet.newblogentry.label" />
			 					</td> 
		 					</tr>
		 					<tr> <td colspan="2" >&nbsp;</td> </tr>
		 					<tr> 
			 					<td class="message-subject bbl ar" nowrap="nowrap">
			 						<display:get name="prm.resource.assignments.update.blogitsubject.label" />
			 					</td> 
			 					<td> <input id="blog-it_title" maxlength="240" name="title" size="60" type="text" value="">  </td> 
		 					</tr>
		 					<tr> <td colspan="2" >&nbsp;</td> </tr>
						    <tr> 
							    <td valign="top" class="message-subject ar" nowrap="nowrap">
							    	<display:get name="prm.resource.assignments.update.blogitmessage.label" />
							    </td>
							     <td> <div id="contentTextarea"></div> </td> 
						     </tr>
						    <tr><td></td>
						    	<td colspan="2">
						    		<table width="100%">
							    		<tr>
											<td class="table-content-done" nowrap="nowrap">&nbsp;
												<input type="checkbox" id="isImportantEntry" onclick="setImportantFlag();"/>&nbsp;Important
											</td>
											<td nowrap="nowrap" id="impMsg" class="show-message h">
												&nbsp;<display:get name="prm.blog.addweblogentry.explainwhyimportant.message" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
	            	</td>
           		 </tr>
	            <tr class="buttonBackground">
		            <td align="right" colspan="<%=(Math.round(colSpanCount/2))+1 %>" class="sb">
		            	<input type="button" id="btnSubmit" value="Submit" onclick="javascript:update();" class="btnS"/>
		        	</td>
		            <td align="left" colspan="<%=(Math.round(colSpanCount/2))+1 %>">
		            	<input type="button" id="btnCancel" value="Cancel" onclick="javascript:cancel();" class="btnS"/>
		            </td>
	            </tr>
	            <%		}
          		} else if(PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled")){ %>
	            <tr class="buttonBackground">
		            <td align="center" colspan="<%=colSpanCount %>">
		            	<input type="button" value="Cancel" onclick="javascript:cancel();" class="btnS"/>
		            </td>
	            </tr>
				<!--E B B -->
				<% } %>
        </table>
		</td>
		</tr>
		</table>
        </div>
        <div id="footer-main-div">
			<div id="timecard-footer-div">
				<table id="timecard-footer" border="0" cellpadding="0" cellspacing="0" class="timesheet-left-table tableContent disableSelection">
					<!--START GRAND TOTAL ROW -->
					<tr class="tableContent">
						<td class="totalLight fdt" height="25px" id="grandTotal" align="left"><b><display:get name="prm.resource.timesheet.grandtotal.label" /></b></td>
		            
					<%
					for (Iterator it = dateLongNames.iterator(); it.hasNext();) {
						String longName = (String)it.next();
						longValues += longName + ",";
						Date grandDate = new Date(Long.parseLong(longName));
						TimeQuantity summaryWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
						if(grandTotalValues.get(grandDate) != null) {
							summaryWork = (TimeQuantity)grandTotalValues.get(grandDate);
						}
					%>
		                <td class="totalLight totalDark-leftBorder grand-total" align="center" ><span id="dln<%=longName%>" class="b"><%= nf.formatNumber(summaryWork.getAmount().doubleValue(), 0, 2) %></span></td>
					<%} if(isPercentComplete) {%>
	                    <td class="totalDark totalDark-leftBorder grand-total"></td>
	                <% } if(isActualWork) {%>
	                    <td class="totalDark totalDark-leftBorder grand-total"></td>
	                <% } if(showWeeklyTotal) {%>
	                    <td class="totalDark totalDark-leftBorder grand-total"></td>
	                <% } if(showProjectTotal) {%>
	                    <td class="totalDark totalDark-leftBorder grand-total"></td>
	                <% } %>
					</tr>
				</table>
			</div>
		</div>
		
		<% if(showBlogsOnRight) {%>
        
        <div id="splitterBar" class="splitter-bar" onmousedown="setPos(event)" style="display:none;">
			<table height="100%" cellspacing="0" cellpadding="0" valign="middle">
				<tr><td><img onclick="toggleRightPanel(true);" id="splitterimgopen" src="<%=SessionManager.getJSPRootURL()%>/images/personal/close.gif" title="<%=PropertyProvider.get("prm.resource.timesheet.closeblogpanel.title")%>"/></td></tr>
			</table>
		</div>
		<div id="splitterBarShadow" class="splitter-bar" style="display:none;"></div>
		</td><!--	closing of timesheet assignmentList td-->
				
		<td width="10%" id="blogsDiv" valign="top" height="100%">
		    	<table id="blogsExpanded" width="100%" height="100%" cellpadding="0" cellspacing="0" style="display: none;">
		    		<tr>
		    			  <td class="bE">
		    			  <div id="rightTabSet">
								<div class="blog-tab" id="blog-tab">
									<b><display:get name="prm.resource.timesheet.blogheader.label" /></b>
								</div>
						  </div>
						  </td>
		    		</tr>
			        <tr>
			        	<td valign="top" align="right" width="100%">
				        		<div id="taskBlogDivRight" class="disableSelection" onselectstart="return false;">
				        			<div id="blogDivTop"><display:get name="prm.resource.timesheet.selectassignment.message" /></div>
				        			<div id="assignment-blog-div"></div>
				        		</div>
				        </td>
					</tr>
		        </table>
		        <table id="blogsCollapsed" height="100%" cellspacing="0" cellpadding="0" valign="middle">
					<tr><td class= "rbc"><img onclick="toggleRightPanel(false);" id="splitterimgclose" src="<%=SessionManager.getJSPRootURL()%>/images/personal/open.gif" title="<%=PropertyProvider.get("prm.resource.timesheet.openblogpanel.title")%>"/></td></tr>
				</table>
			<% } %>
			</td>
			</tr>
		</table>
	</div>
<script language="javascript" type="text/javascript">
	<% if(StringUtils.isNotEmpty(projectIds)) {%>
		var objectIds = '<%=objectIds.substring(0, (objectIds.length() - 1))%>'.split(",");
		var projectIds = '<%=projectIds.substring(0, (projectIds.length() - 1))%>'.split(",");
		var timeValues = '<%=longValues.substring(0, (longValues.length() - 1))%>'.split(",");
	<%}%>
	document.getElementById('splitterBar').style.display = 'none';
	var rowCount = <%=rowCount%>;
</script>
