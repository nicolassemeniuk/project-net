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
    info="DefinitionEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*,
            net.project.form.*,
            net.project.gui.navbar.FeaturedItemsAssociation,
            net.project.security.User,
            net.project.base.property.PropertyProvider" 
%>

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="featuredItemsAssociation" class="net.project.gui.navbar.FeaturedItemsAssociation" scope="session"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<jsp:setProperty name="formDesigner" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		formDesigner.setDescription("");
	}
%>

<%
	// Due to presence of ID field on DefinitionEdit, must take action if it
	// was the empty string ("")
	if (request.getParameter("id") != null && request.getParameter("id").equals("")) {
		formDesigner.setID(null);
	}

	// Toolbar: Submit button
	if (request.getParameter("theAction").equals("submit")) {
				
			if (request.getParameter("supportsDocumentVault") != null && request.getParameter("supportsDocumentVault").equals("true"))
	        	formDesigner.setSupportsDocumentVault(true);
	        else
	            formDesigner.setSupportsDocumentVault(false);
						
			if (request.getParameter("supportsDiscussionGroup") != null && request.getParameter("supportsDiscussionGroup").equals("true"))
	        	formDesigner.setSupportsDiscussionGroup(true);
	        else
	            formDesigner.setSupportsDiscussionGroup(false);		
	
	        if (request.getParameter("supportsFormAssignment") != null && request.getParameter("supportsFormAssignment").equals("true"))
	        	formDesigner.setSupportsAssignment(true);
	        else
	            formDesigner.setSupportsAssignment(false);
	       
	        boolean isExternalAccessAllowed = PropertyProvider.get("prm.externalformaccess.isenabled").equals("1") || PropertyProvider.get("prm.externalformaccess.isenabled").equals("true");
	        if (isExternalAccessAllowed){  
		        if (request.getParameter("supportsExternalAccess") != null && request.getParameter("supportsExternalAccess").equals("true")){
		        	formDesigner.setSupportsExternalAccess(true);
		        	formDesigner.setClearExternalClassId(false);
		        }  else {
		            formDesigner.setSupportsExternalAccess(false);
		            formDesigner.setClearExternalClassId(true);
		        }
	        }
	        
	        if (request.getParameter("showAssignmentFields") != null && request.getParameter("showAssignmentFields").equals("true"))	        	
	        	formDesigner.setAssignmentFieldHiddenInEaf(false);
	        else
	            formDesigner.setAssignmentFieldHiddenInEaf(true);	        
	    
	        formDesigner.setChangeSharingStatus(!request.getParameter("currentSharingStatus").equals(request.getParameter("shared") != null ? request.getParameter("shared") : "false" ));
	        if (request.getParameter("shared") != null && request.getParameter("shared").equals("true"))	        	
	        	formDesigner.setShared(true);
	        else
	            formDesigner.setShared(false);	        
	        
	        
	        
			formDesigner.store();
		
		request.setAttribute("id", formDesigner.getID());
        request.setAttribute("reloadToolsMenu", "true");
        

        //Show the item in the left nav bar, if the user has selected it
        featuredItemsAssociation.clear();
        featuredItemsAssociation.setObjectID(formDesigner.getID());
        featuredItemsAssociation.setSpaceID(user.getCurrentSpace().getID());

        if (request.getParameter("displayedInToolsMenu") != null)
            featuredItemsAssociation.store();
        else
            featuredItemsAssociation.remove();

        //---Avibha:-----setting the and checking security violation--
        request.setAttribute("id",formDesigner.getID());
        request.setAttribute("action",""+Action.MODIFY);
        ServletSecurityProvider.setAndCheckValues(request);
         //-------------------------------------------------------------
        	      
		if ((request.getParameter("nextPage") != null) && !request.getParameter("nextPage").equals(""))
			pageContext.forward(request.getParameter("nextPage"));
		else
			pageContext.forward("FieldsManager.jsp");
	}
%>
