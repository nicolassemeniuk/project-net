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
    info="Calendar Entry Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.CalendarEvent, 
    		net.project.calendar.CalendarBean,
    		net.project.calendar.MiniMonth,
    		net.project.security.User,
    		net.project.security.SecurityProvider,
    		net.project.security.SessionManager,
    		net.project.resource.IFacility,
    		net.project.space.Space,
            net.project.security.Action,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="calendar" class="net.project.calendar.CalendarBean" scope="session" />
<jsp:useBean id="iCalendar" class="net.project.calendar.ical.ICalendar"/>

<%!
public final String baseUrl = SessionManager.getJSPRootURL();
public final String appUrl = SessionManager.getAppURL();
public static final String ATTRIB_includer	= "Includer";
public static final String ATTRIB_date		= "Date";
%>

<security:verifyAccess action="view"
					   module="<%=Module.CALENDAR%>"/>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS />
<%
    String mySpace = user.getCurrentSpace().getType();
    String spaceName = null;
    String groupTitle = "prm.project.nav.calendar";
    if( mySpace.equals(Space.BUSINESS_SPACE))
        spaceName= "business";
    else if(mySpace.equals(Space.PERSONAL_SPACE)) {
        spaceName= "personal";
        groupTitle = "prm.calendar.mainpage.personal.title";
    } else
        spaceName = "project";        
%>
<%
    // If the params are set in the request parameter we will store these in the user session
    // so the next time the user accesses Main without params set, state will be maintained
    // DEFAULT: Month, Graphical view.
    String pmDate = request.getParameter(CalendarBean.PARAM_date);
    String pmMode = request.getParameter(CalendarBean.PARAM_mode);
    String pmType = request.getParameter(CalendarBean.PARAM_type);
    if (pmDate != null)
        session.setAttribute("Calendar_State_Date", pmDate);
    else
        pmDate = (String)session.getAttribute("Calendar_State_Date");
    if (pmMode != null)
        session.setAttribute("Calendar_State_Mode", pmMode);
    else
        pmMode = (String)session.getAttribute("Calendar_State_Mode");
    if (pmType != null)
        session.setAttribute("Calendar_State_Type", pmType);
    else
        pmType = (String)session.getAttribute("Calendar_State_Type");

    // configure the calendar
    calendar.setState(pmDate, pmMode, pmType);
    String stateQueryString = calendar.getStateAsQueryString();

    // set request attributes for included jsp pages
    String myPath = baseUrl + "/calendar/Main.jsp";
    request.setAttribute(ATTRIB_includer, myPath);
    java.util.Date date = calendar.getDisplayDateAsObject();
    if (date != null) {
        request.setAttribute(ATTRIB_date, date);
        request.getSession().setAttribute("Calendar_ViewState", date);
    }

    // Configure the navigation object
    java.util.Hashtable nav = new java.util.Hashtable();
    request.getSession().setAttribute("PageNavigator", nav);
    nav.put("MeetingEdit_returnto", myPath + stateQueryString + "&module=" + Module.CALENDAR);
    nav.put("MeetingManager_returnto", myPath + stateQueryString + "&module=" + Module.CALENDAR);

    String refLink = "/calendar/Main.jsp?module=" +Module.CALENDAR+"&action="+Action.VIEW;
    String refLinkEncoded = java.net.URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
    String secureICalKey = iCalendar.getSecureICalKey( user.getID(), user.getCurrentSpace().getID());
%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= baseUrl %>';    

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.mainForm;
	isLoaded = true;
}
<%
String mode=calendar.getDisplayMode();
String section="day";
    if (mode.equals(CalendarBean.MODE_day))
        section="day";
    else if (mode.equals(CalendarBean.MODE_week))
        section="week";
    else if (mode.equals(CalendarBean.MODE_month)) 
        section="month";
    else if (mode.equals(CalendarBean.MODE_year)) 
    	section="year";
%>
function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=calendar&section=<%=section%>";
	openwin_help(helplocation);
}


function cancel() {
   self.location = "<%= baseUrl %>/<%=spaceName%>/Main.jsp?module=<%= Module.PROJECT_SPACE %>";
}

function reset() { 
   self.location = "<%= myPath %><%= stateQueryString %>&module=<%= Module.CALENDAR %>";
}

function create(cdate) {
	page = "<%= baseUrl %>/calendar/EventEdit.jsp?module=<%=Module.CALENDAR%>&action=<%=Action.CREATE%>"; //document.titleForm.compose.options[document.titleForm.compose.options.selectedIndex].value;
	
   if (cdate)
      self.location = page + "&wizard=true&DisplayDate=" + cdate + "&refLink=<%=refLinkEncoded%>";
   else
      self.location = page + "&<%=stateQueryString.substring(1)%>&wizard=true" + "&refLink=<%=refLinkEncoded%>";
}

function modify() {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
      theAction("modify");
      theForm.action.value= "<%= Action.MODIFY %>";
      theForm.id.value= getSelection(theForm);
      theForm.submit();
      }   
}

function remove() {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
      Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.calendar.main.remove.message" />', function(btn) { 
			if(btn == 'yes'){ 
				theAction("remove");
         		theForm.action.value= "<%= Action.DELETE %>";
         		theForm.id.value= getSelection(theForm);
         		theForm.submit();
			}else{
			 	return false;
			}
		});
  }    
}
<%
// Only allow security in project space
if (mySpace != null && !mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {
%>
function security() {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {    
      if (!security)
         var security = openwin_security("security");
    
      if (security) {
         var idval = getSelection(theForm);
         theAction("security");
         theForm.target = "security";
         theForm.action.value = "<%=Action.MODIFY_PERMISSIONS%>";
         theForm.module.value = "<%=Module.SECURITY%>";
         theForm.id.value = idval;
         theForm.submit();
      }
  }
}
<%
}
%>

function showUrl(){
	document.getElementById('iCalUrl').style.display='block';
}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle='<%=PropertyProvider.get(groupTitle)%>'>
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.calendar.module.history")%>' 
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/Main.jsp"%>'
					queryString='<%="module="+Module.CALENDAR%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<%------ only enable remove, modify and security if the calendar mode is not graphical --------%>
	<% if ( !calendar.getDisplayType().equals(CalendarBean.TYPE_graphic)) { %>
		<tb:button type="remove" />
		<tb:button type="modify" />
		<%if (mySpace != null && !mySpace.equals(Space.PERSONAL_SPACE)) {%>
			<tb:button type="security" />
		<%}%>
	<% } %>
		<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.over")%>' label='<%=PropertyProvider.get("all.global.toolbar.standard.create") + " " + PropertyProvider.get("prm.calendar.main.option.meeting.name")%>' function='<%= baseUrl + "/calendar/MeetingEdit.jsp?module=" + Module.CALENDAR + "&action=" + Action.CREATE + "&wizard=true"%>'/>
		<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.over")%>' label='<%=PropertyProvider.get("all.global.toolbar.standard.create") + " " + PropertyProvider.get("prm.calendar.main.option.event.name")%>' function='<%= baseUrl + "/calendar/EventEdit.jsp?module=" + Module.CALENDAR + "&action=" + Action.CREATE%>'/>
		<% if (mySpace!=null && !mySpace.equals(Space.PERSONAL_SPACE) && !mySpace.equals(Space.BUSINESS_SPACE)) { %>
			<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.create.image.over")%>' label='<%=PropertyProvider.get("all.global.toolbar.standard.create") + " " + PropertyProvider.get("prm.calendar.main.option.task.name")%>' function='<%=baseUrl + "/servlet/ScheduleController/TaskCreate?action=" + Action.CREATE + "&module=" + Module.SCHEDULE + "&refLink="+ refLinkEncoded%>'/>
		<% } %>
	</tb:band>
	<tb:band name="action" showAll="true" groupHeading='<%=PropertyProvider.get("prm.calendar.groupheading.icalendar.label") %>'>
		<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.document.check_out.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.document.check_out.image.over")%>' label='<%=PropertyProvider.get("prm.calendar.icalendar.export.label") %>' function='<%= baseUrl + "/ical/icalendar.jsp?export=true&key=" + iCalendar.getSecureICalKey( user.getID(), user.getCurrentSpace().getID())%>'/>
		<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.document.view.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.document.view.image.over")%>' label='<%=PropertyProvider.get("prm.calendar.icalendar.viewurl.label") %>' function="javascript:showUrl();"/>
	</tb:band>	
</tb:toolbar>

<div id='content'>

<% if(secureICalKey != null) {%>
<table border="0" cellpadding="0" cellspacing="0" width="100%" vspace="0">
<tr id="iCalUrl" style="display:none">
<td class="historyText"  > 

<br><b>Url: </b> <%= appUrl %>/ical/icalendar.jsp?key=<%= secureICalKey %>
</td>
</tr>
</table>
<br>
<% } %>

<%
// set up the mini month
MiniMonth miniMonth = new MiniMonth();
miniMonth.setDate(date);
miniMonth.setMode(calendar.getDisplayMode());
miniMonth.setIncluder(myPath);
// write out the top day/week/month/year toolbar
miniMonth.writeTabBar(out);
%>


<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<form method="post" name="mainForm" action="MainProcessing.jsp">        
    <input type="hidden" name="action" value="">
    <input type="hidden" name="id" value="">
    <input type="hidden" name="module" value="<%= Module.CALENDAR %>">
    <input type="hidden" name="theAction"> 
<%
    // Include the calendar view
    
    String include = getPathToInclude(calendar.getDisplayMode(), calendar.getDisplayType());
    request.setAttribute("Width", "100%");                
%> 
    <td  valign=top>
	    <%= calendar.getStateAsFormBody()%>
	    <jsp:include page="<%= include %>" flush="true" />
    </td>
    </form>
    <td>&nbsp;</td>
    <td width="190" valign=top align=left>

    <%
	miniMonth.setTabs(false);
	miniMonth.renderToStream(out);
    %>               
<br><br>
<%
    request.setAttribute("Width", "172");                
%>
   <jsp:include page="include/includeGoToDate.jsp" flush="true" />                

<%
    // if we are currently dispaying graphical month view, include
    // the entry legend
    if ((calendar.getDisplayMode().equals(CalendarBean.MODE_month)) && 
        (calendar.getDisplayType().equals(CalendarBean.TYPE_graphic))) { %>
	<br><br>
         <jsp:include page="include/includeEntryLegend.jsp" flush="true" />
<%  } %>
    
    </td>
</tr>
</table>

</div>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

<%!
public String getPathToInclude(String mode, String type) {
    if (mode.equals(CalendarBean.MODE_day))
        return "include/includeViewDay.jsp";
    else if (mode.equals(CalendarBean.MODE_week))
        return "include/includeViewWeek.jsp";
    else if (mode.equals(CalendarBean.MODE_month)) {
        if (type.equals(CalendarBean.TYPE_graphic))
            return "include/includeViewMonthGraphical.jsp";
        else
            return "include/includeViewMonth.jsp";
    } else if (mode.equals(CalendarBean.MODE_year))
        return "include/includeViewYear.jsp";

    return "";
}
%>
