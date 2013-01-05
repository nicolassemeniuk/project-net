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
    info="Add Meeting Link" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
    net.project.security.SecurityProvider,
    net.project.security.SessionManager,
    net.project.document.DocumentManagerBean,
	net.project.base.property.PropertyProvider,
    net.project.space.Space,
            net.project.base.ObjectType,
            net.project.document.IContainerObject,
            net.project.security.Action"
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />

<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();

String view = request.getParameter("view");
String context = request.getParameter("context");

if ((id.length() == 0) || 
    (action != net.project.security.Action.MODIFY) ||
    (module != net.project.base.Module.DOCUMENT))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.documentaddlink.authorizationfailed.message"));

 
HttpSession mySess=request.getSession() ;

//Set the proper referrer to return to after creating the link.
String refererParams = "?module="+module+"&action="+Action.VIEW+"&theAction=properties&containerID="+docManager.getCurrentContainer().getID()+"&id="+id;
IContainerObject containerObject = docManager.getCurrentObject();
if (containerObject.isTypeOf(ObjectType.CONTAINER))
    mySess.setAttribute("refererLink",SessionManager.getJSPRootURL()+"/document/ContainerProperties.jsp"+refererParams);
else if (containerObject.isTypeOf(ObjectType.DOCUMENT))
    mySess.setAttribute("refererLink",SessionManager.getJSPRootURL()+"/document/Properties.jsp"+refererParams);
else if (containerObject.isTypeOf(ObjectType.BOOKMARK))
    mySess.setAttribute("refererLink",SessionManager.getJSPRootURL()+"/document/BookmarkProperties.jsp"+refererParams);

response.sendRedirect( SessionManager.getJSPRootURL()+"/link/LinkManager.jsp?module="+module+"&action="+action+"&id="+id+"&action="+action+"&view="+view+"&context="+context);
 %>


