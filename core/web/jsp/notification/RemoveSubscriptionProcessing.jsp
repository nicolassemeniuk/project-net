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
    info="Manage Subscriptions" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.notification.*,
    net.project.security.User"
%>

     <jsp:useBean id="subscriptionBean" class="net.project.notification.SubscriptionBean" scope="session"/> 
     <jsp:useBean id="user" class="net.project.security.User" scope="session"/>  


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<%--
<security:verifyAccess 
				module="<%=net.project.base.Module.NOTIFICATION%>"
				action="remove" 
/>
--%>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

          
<%
     subscriptionBean.setUser (user);
	 subscriptionBean.setID ( request.getParameter("subscriptionID") );

	 subscriptionBean.remove();

	 response.sendRedirect ("ClosePopup.jsp");
%>



