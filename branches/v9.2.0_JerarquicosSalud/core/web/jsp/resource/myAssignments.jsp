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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
       info="My Assignments Channel" 
       language="java" 
       errorPage="/errors.jsp"
       import="org.apache.commons.lang.StringUtils,
               net.project.security.User, 
               net.project.security.SessionManager,
               net.project.resource.Assignment,
               net.project.base.property.PropertyProvider,
               net.project.resource.AssignmentManagerBean,
               net.project.space.Space,
               net.project.resource.AssignmentStatus"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="assignments" class="net.project.resource.AssignmentManagerBean" scope="session" />

<%

/* Note:
   No security validation is necessary on this page because it works off the authenticated users id
*/
    assignments.setStatusFilter(AssignmentStatus.ASSIGNED);
    assignments.setPersonID(user.getID());
    assignments.loadAssignments();

    String queryString = request.getQueryString();
    String referingURL;
    if(queryString != null)
        referingURL = request.getRequestURI() + "?" + queryString;
    else
        referingURL = request.getRequestURI();

%>
    
<%
    int hiddenModule = -1;
    String mySpace=user.getCurrentSpace().getType();
    if (mySpace.equals(Space.PERSONAL_SPACE))
        hiddenModule = net.project.base.Module.PERSONAL_SPACE;
    else if (mySpace.equals(Space.PROJECT_SPACE))
        hiddenModule = net.project.base.Module.PROJECT_SPACE;
%>

<% if(assignments.getNumberOfAssignments() != 0)  { %>
<script language="javascript">
    function accept() {
    	flag = false;
    	var assnStatus = document.getElementsByName('assn_status');
    	if (assnStatus) {
		    if(assnStatus.length) {
		    	for (var i = 0; i < assnStatus.length; i++) {
		    		var status = assnStatus[i];
		    		if (status.checked) {
		    			flag = true;
		    		}
		    	};
		    } 
	    }
	    if (flag) {
	        self.document.assignmentsForm.channelAction.value = "Accepted";
	        self.document.assignmentsForm.submit();
		} else {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}
    }

    function reject() {
        flag = false;
    	var assnStatus = document.getElementsByName('assn_status');
    	if (assnStatus) {
		    if(assnStatus.length) {
		    	for (var i = 0; i < assnStatus.length; i++) {
		    		var status = assnStatus[i];
		    		if (status.checked) {
		    			flag = true;
		    		}
		    	};
		    } 
	    }
	     if (flag) {
	        self.document.assignmentsForm.channelAction.value = "Rejected";
	        self.document.assignmentsForm.submit();
		} else {
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}
    }

    function changeSelection() {
        changeCheckedState(self.document.assignmentsForm.assn_status,
            self.document.assignmentsForm.changeCheckedState.checked);
    }
</script>

<form name="assignmentsForm" method="post" target="_self" action="<%=SessionManager.getJSPRootURL()%>/resource/AssignmentsProcessing.jsp">
    <input type="hidden" name="channelAction" value=""/>
    <input type="hidden" name="module" value="<%= hiddenModule %>">
    <input type="hidden" name="referingURL" value=<%=referingURL%> />
    
    <%-- Check whether it happened an error when processing assignments. --%>
	<c:if test="${sessionScope.assignmentErrorMessage != null}">
	   <div id="errorLocationID" class="errorMessage">
	       <c:out value="${sessionScope.assignmentErrorMessage}"/>
	   </div>
	   <%-- Remove the error message from session so it won't appears again.--%>
	   <c:remove var="assignmentErrorMessage" scope="session"  />
    </c:if>
<% } %>

	<pnet-xml:transform name="assignments" scope="session" stylesheet="/resource/xsl/my-assignments.xsl" />

<% if(assignments.getNumberOfAssignments() != 0)  { %>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="accept" target="_self" />
		<tb:button type="cancel" target="_self" labelToken="@prm.personal.main.myassignments.cancel.button.label" function="javascript:reject();" />
	</tb:band>
</tb:toolbar>
</form>
<% } %>
