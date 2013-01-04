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
    info="Process Document Remove"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.*,
    net.project.base.Module,
    net.project.util.*,
            net.project.base.PnetExceptionTypes"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="document" class="net.project.document.DocumentBean" scope="page" /> 

<%
// remove the current object form the system

     int module = securityProvider.getCheckedModuleID();
     int action = securityProvider.getCheckedActionID();
     String id = securityProvider.getCheckedObjectID();
	 String status = "D"; //regular delete

// security checks are now done within docManager

	if(action == 512) {//hard delete
		status = "H";
		docManager.setListDeleted(); //look for deleted objects
	}

	if(action == 1024) {//undo delete
		status = "A";
		docManager.setListDeleted(); //look for deleted objects
	}

    IContainerObject co = docManager.getCurrentObject();

    co.setUser( docManager.getUser() );
    //Avinash:----------------------------------------------
    //SJMITTAL (this is making bfd 3363 reopen) co.setContainerID(docManager.getCurrentContainerID());
    //Avinash:----------------------------------------------

    try {
        docManager.removeObject (co,status);
    } catch (DocumentException de) {
        if (de.getReasonCode() == PnetExceptionTypes.DOCUMENT_REMOVE_FAILED_CKO_BY_ANOTHER_USER) {
            ErrorReporter errorReporter = new ErrorReporter();
            errorReporter.addError(de.getLocalizedMessage());

            session.setAttribute("errorReporter", errorReporter);
        } else {
            throw de;
        }
    }
	
	if(action == 1024 || action == 512) {
		docManager.unSetListDeleted(); //unset the flag
		request.setAttribute ("action", Integer.toString(Action.LIST_DELETED));
		request.setAttribute ("module", Integer.toString(Module.TRASHCAN));
	} else {
		request.setAttribute ("action", Integer.toString(Action.VIEW));
		request.setAttribute ("module", Integer.toString(Module.DOCUMENT));
	}

   String forwardingPage = (String)docManager.getNavigator().get("TopContainer");

   request.setAttribute ("id", "");
   //avinash:------------------------------
	ServletSecurityProvider.setAndCheckValues(request);
   if ((forwardingPage != null) && ( !forwardingPage.equals(""))) {
       //Remove parameter historyType from forwardingPage url, not needed while deleting document.
       forwardingPage = forwardingPage.replaceAll("historyType=unspecified", "");
	   //response.sendRedirect(forwardingPage+"&module="+request.getParameter("module"));
	   pageContext.forward (forwardingPage+"&module="+request.getParameter("module"));
   } else {
	   //avinash:------------------------------
        pageContext.forward ("/document/Main.jsp?module="+request.getParameter("module"));
   }
%>

