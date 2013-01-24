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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
    		net.project.base.Module"
%>
<html>
<SCRIPT language="javascript" type="text/javascript">
function complete() {
    document.body.style.cursor = "default";
    try{
    	// 10/08/2006 Alex: BFD-3207: We try to call the resourcesComplete() method from the opener window
    	// to close and refresh the list of tasks
    	window.opener.resourcesComplete();
    }catch(error){
    	// 10/08/2006 Alex: BFD-3207: If the method resourcesComplete() not exists in the opener window
    	// we should refresh the task list and close the DeleteTaskDialog
    	theLocation="<%=SessionManager.getJSPRootURL()%>/workplan/taskview?module=<%=Module.SCHEDULE %>";
		window.opener.location = theLocation;
    	window.close();
    }
}
</SCRIPT>
<body onLoad="complete();">
<input type="button" onClick="complete();">
</body>
</html>
