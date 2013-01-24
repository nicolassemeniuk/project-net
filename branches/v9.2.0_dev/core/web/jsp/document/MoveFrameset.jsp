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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Process folder traversal"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
	net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	  int action = net.project.security.Action.MODIFY;
	  int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	  String id = docManager.getCurrentObjectID();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="modify"
				objectID = "<%=id%>"
/>

<%------------------------------------------------------------------------
  -- Move Verification
  ----------------------------------------------------------------------%>
<%
	  DocumentControlManager dcm = new DocumentControlManager();
      dcm.setUser( docManager.getUser() );
      dcm.verifyMove(docManager.getCurrentObjectID(), docManager.getCurrentContainerID());
%>





<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.document.moveframeset.title" /></title> 
</head>


<frameset rows="100,*" frameborder="NO"> 
  <frame src="<%=SessionManager.getJSPRootURL()%>/document/MoveObjectTop.jsp?module=<%=module%>&action=<%=net.project.security.Action.VIEW%>&id=<%=docManager.getCurrentObjectID() %>&gotoframe=<%=SessionManager.getJSPRootURL()%>/document/MoveObjectFolderBrowser.jsp?module=<%=module%>&action=<%=net.project.security.Action.VIEW%>&id=<%= docManager.getCurrentContainerID()%>" scrolling="NO" name="moveTop" frameborder="NO">
  <frame src="/blank.html" scrolling="AUTO" name="moveBrowser" frameborder="NO">
</frameset>
<noframes><body bgcolor="#FFFFFF">

</body></noframes>
</html>
