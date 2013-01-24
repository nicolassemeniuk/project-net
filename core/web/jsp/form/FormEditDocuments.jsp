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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Documents for a form item"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.document.Container,
            net.project.document.ContainerType,
            net.project.document.DocumentSpace,
            net.project.document.GenericObjectContainer,
            net.project.document.PathBean,
            net.project.form.Form,
            net.project.form.FormData,
            net.project.security.SessionManager,
            net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="docManager" class="net.project.document.DocumentManager" scope="session"/>
<jsp:useBean id="path" class="net.project.document.PathBean" scope="session"/>
<jsp:useBean id="form" class="net.project.form.Form" scope="session"/>
<%
    String formDataID = request.getParameter("formDataID");

    request.getSession().putValue("Document_HistoryIsLevel2Page", new Boolean(true));

    //Try to see if we can find the container for this form
    String containerID = GenericObjectContainer.findContainerForObjectID(formDataID);

    if (containerID == null) {
        String formDataContainerID = docManager.getSystemContainerIDForSpace(user.getCurrentSpace().getID(),
                ContainerType.FORM_DATA_DOCUMENT_CONTAINER, true);

		String formDataName = (String)session.getAttribute("formDataName");
        
        //There isn't a document container for this form already, create one
        GenericObjectContainer goc = new GenericObjectContainer();
        goc.setUser(user);
        goc.setParentContainerID(formDataContainerID);
        goc.setObjectID(formDataID);
        goc.setTopFolderName(formDataName+ " folder");
        goc.store();

        containerID = goc.getContainerID();    
    }

    docManager.setDocumentSpace(docManager.getDefaultDocumentSpace(user.getCurrentSpace().getID()));
    docManager.setRootContainerID(containerID);
    docManager.setCurrentContainer(containerID);
    // We are visiting a system folder; ensures root folder reset when
    // user visits workspace's doc vault
    docManager.setVisitSystemContainer(true);

    response.sendRedirect(SessionManager.getJSPRootURL() + "/document/Main.jsp?module=" + Module.DOCUMENT + "&historyType=pageLevel2&formId="+formDataID);
%>