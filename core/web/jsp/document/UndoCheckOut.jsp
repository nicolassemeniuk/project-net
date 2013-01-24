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
    info="Process Document Undo Check Out"
    language="java" 
    errorPage="DocumentErrorHandler.jsp"
    import="net.project.document.*,
    net.project.security.User,
    net.project.util.Conversion,
	net.project.base.property.PropertyProvider,
	net.project.security.SessionManager"
 %>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
      Document document = (Document) docManager.getCurrentObject();

      int module = docManager.getModuleFromContainerID(document.getContainerID());
      int action = net.project.security.Action.MODIFY;
      String id = document.getID();

      // now set the ignoreUserMismatch constraint as appropriate
      String ignoreString = request.getParameter ("ignoreUserMismatchConstraint");
      boolean ignoreConstraint = Conversion.toBoolean (ignoreString);
      document.setIgnoreUserMismatchConstraint (ignoreConstraint);
%>

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="modify"
				objectID = "<%=docManager.getCurrentObjectID()%>"
/>

<%------------------------------------------------------------------------
  -- more page setup
  ----------------------------------------------------------------------%>

<%
      DocumentControlManager dcm = new DocumentControlManager();

      dcm.setUser (docManager.getUser());
      dcm.verifyUndoCheckOut (document);

      docManager.setCurrentObject (document);
	  
	  String[] props = { document.getName() };
	  
%>


<html>
<head>
<title><display:get name="prm.document.undocheckout.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

</head>


<body bgcolor="#FFFFFF">
<CENTER>
<p><font face="Arial, Helvetica, sans-serif"><b><%=PropertyProvider.get("prm.document.undocheckout.instructions" , props) %></b></font></p>
<form name="undoCKO" method="post" action="<%=SessionManager.getJSPRootURL()%>/document/UndoCheckOutProcessing.jsp">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="action" value="<%=action%>">  
<input type="hidden" name="id" value="<%=id%>"> 

<tb:toolbar style="action" showLabels="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" label='<%=PropertyProvider.get("prm.document.undocheckout.submit.button.label") %>' function="javascript:document.undoCKO.submit();" />
        <tb:button type="cancel" label='<%=PropertyProvider.get("prm.document.undocheckout.cancel.button.label") %>' function="javascript:parent.opener.focus();self.close();" />
    </tb:band>
</tb:toolbar>

</form>
<p>&nbsp; </p>
</CENTER>
</body>
</html>
