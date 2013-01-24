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
    info="Registration Page 1 Processing - this page does not emit any output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.admin.RegistrationBean,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.security.User,
            net.project.util.Validator,
            org.apache.commons.lang.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="historyBean" class="net.project.history.HistoryBean" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%!
    String errorMessage = "";
    String errorObject = "";
    boolean isInfoValid = true;
    String currentEmail;

    private void addErrorObject(String myErrorObject)
    {
            if (errorObject.equals(""))
            errorObject = myErrorObject;
    }
	
%>
<%
    String jspRootURL = SessionManager.getJSPRootURL();
    String userID = request.getParameter("userID");

    //We need to store the module as these pages are reused in more than one module
    String module = request.getParameter("module");

    //Save the current email, so we can detect when the user is trying to change it.
    currentEmail = registration.getEmail();
	String alternateEmail1 = registration.getAlternateEmail1();
	String alternateEmail2 = registration.getAlternateEmail2();
%>
<%-- Process the Form submit --%>
<jsp:setProperty name="registration" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("ecom_ShipTo_Postal_Name_Prefix") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Prefix").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Prefix("");
	}
	if ((request.getParameter("ecom_ShipTo_Postal_Name_Middle") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Middle").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Middle("");
	}
	if ((request.getParameter("ecom_ShipTo_Postal_Name_Suffix") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Suffix").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Suffix("");
	}
%>

<%
	// clear old error messages.
	session.removeValue("errorMsg");
	session.removeValue("errorObj");
	errorMessage = "";
	errorObject = "";
	isInfoValid = true;
	
	// Set the user record is being updated
	registration.setUpdating(true);		
	
	// Toolbar: Apply
if (request.getParameter("theAction").equals("apply")) {

        // if the user want's to change their email address,
        // make sure the new email doesn't already exist in the system.
    if (!request.getParameter("ecom_ShipTo_Online_Email").equals(currentEmail)) {
        if ((registration.checkEmailExists (request.getParameter("ecom_ShipTo_Online_Email"))) || (request.getParameter("ecom_ShipTo_Online_Email").equals(""))) {   
                registration.setEmail(currentEmail);
                errorMessage += "The e-mail address is already in use or invalid.\"" + request.getParameter("ecom_ShipTo_Online_Email") + "\".<br>";
                addErrorObject("ecom_ShipTo_Online_Email");
                isInfoValid = false;
        }
    }
	
	 // now, make sure any of the new email addresses don't already exist
    String requestAlternateEmail1 = request.getParameter ("alternateEmail1");
    if ( !Validator.isBlankOrNull(requestAlternateEmail1) &&
            !requestAlternateEmail1.equals (alternateEmail1) &&
         registration.checkEmailExists (registration.getAlternateEmail1()) ) {   
        errorMessage += "The email you specified in Alternate Email Address 1 is already in use or invalid.<br>";
        addErrorObject("alternateEmail1");
        isInfoValid = false;
    }
    String requestAlternateEmail2 = request.getParameter ("alternateEmail2");
    // now, make sure any of the new email addresses don't already exist
    if ( !Validator.isBlankOrNull(requestAlternateEmail2) &&
            !request.getParameter ("alternateEmail2").equals (alternateEmail2) &&
         registration.checkEmailExists (registration.getAlternateEmail2()) ) {   
        errorMessage += "The email you specified in Alternate Email Address 2 is already in use or invalid.<br>";
        addErrorObject("alternateEmail2");
        isInfoValid = false;
    }
    
    //bdf-2888 issue
	//QUOTE http://java.sun.com/products/jsp/tags/10/syntaxref10.fm13.html :
	//If a request parameter has an empty or null value, the corresponding Bean property is not set.
	//
	if(request.getParameter("alternateEmail1").equals(""))
	{
		registration.setAlternateEmail1("");
	}
	if(request.getParameter("alternateEmail2").equals(""))
	{
		registration.setAlternateEmail2("");
	}
	
	if (StringUtils.isNotEmpty(request.getParameter("skype"))) {
		registration.setSkype(request.getParameter("skype"));
	} else {
		registration.setSkype("");
	}
	if (StringUtils.isNotEmpty(request.getParameter("skillsBio"))) {
		registration
		.setSkillsBio(request.getParameter("skillsBio"));
	} else {
		registration.setSkillsBio("");
	}

	// Everything is OK, store goto the next tab
    if (isInfoValid)
    {
        historyBean.setProjectDisplayName(registration.getDisplayName());
        registration.updateInvitedUser();

        //If the user we are editing is the current user, update the email in the user object too
        if (userID == user.getID())
        {
            user.setEmail(registration.getEmail());
            user.load();
        }
        pageContext.forward(request.getParameter("nextPage"));
    }
    // Return for corrections
    else
    {
        session.putValue("errorMsg", errorMessage);
        session.putValue("errorObj", errorObject);
        pageContext.forward("ProfileName.jsp?userID="+userID+"&module="+module+"&action="+Action.MODIFY);
    }
}
else
{
    // go to the requested tab without applying.
    pageContext.forward(request.getParameter("nextPage"));
}
%>
