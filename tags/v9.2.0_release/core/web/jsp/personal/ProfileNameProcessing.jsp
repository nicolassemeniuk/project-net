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
    import="net.project.base.property.PropertyProvider,
			net.project.admin.RegistrationBean,
			net.project.security.User,
			net.project.resource.PersonStatus,
            org.apache.log4j.Logger,
            org.apache.commons.lang.StringUtils"
%>
<jsp:useBean id="historyBean" class="net.project.history.HistoryBean" scope="session" />
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
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
	// save the current email, so we can detect when the user is trying to change it.
	currentEmail = registration.getEmail();
	String alternateEmail1 = (registration.getAlternateEmail1() == null ? "" : registration.getAlternateEmail1());
	String alternateEmail2 = (registration.getAlternateEmail2() == null ? "" : registration.getAlternateEmail2());
	//String alternateEmail3 = (registration.getAlternateEmail3() == null ? "" : registration.getAlternateEmail3());
%>
<%-- Process the Form submit --%>
<jsp:setProperty name="registration" property="*" />

<%
	// bfd - 2994 issue
	if((request.getParameter("ecom_ShipTo_Postal_Name_Prefix") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Prefix").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Prefix("");
	}
	if((request.getParameter("ecom_ShipTo_Postal_Name_Middle") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Middle").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Middle("");
	}
	if((request.getParameter("ecom_ShipTo_Postal_Name_Suffix") == null) || (request.getParameter("ecom_ShipTo_Postal_Name_Suffix").equals(""))) {
		registration.setEcom_ShipTo_Postal_Name_Suffix("");
	}
%>

<%
// clear old error messages.
errorMessage = "";
errorObject = "";
isInfoValid = true;

// Toolbar: Apply
if (request.getParameter("theAction").equals("apply"))
{
	// if the user want's to change their email address,
	// make sure the new email doesn't already exist in the system.
    if ( !request.getParameter("ecom_ShipTo_Online_Email").equals (currentEmail) &&
         !request.getParameter("ecom_ShipTo_Online_Email").equals("") &&
         registration.checkEmailExists (registration.getEcom_ShipTo_Online_Email()) ) {
        registration.setEmail(currentEmail);
        errorMessage += PropertyProvider.get("prm.personal.profile.name.error.primaryemail.message") + "<br>";
        addErrorObject("ecom_ShipTo_Online_Email");
        isInfoValid = false;
    }

    // now, make sure any of the new email addresses don't already exist
    if ( !request.getParameter ("alternateEmail1").equals (alternateEmail1) &&
         registration.checkEmailExists (registration.getAlternateEmail1()) ) {   
        errorMessage += PropertyProvider.get("prm.personal.profile.name.error.alt1email.message") + "<br>";
        addErrorObject("alternateEmail1");
        isInfoValid = false;
    }

    // now, make sure any of the new email addresses don't already exist
    if ( !request.getParameter ("alternateEmail2").equals (alternateEmail2) &&
         registration.checkEmailExists (registration.getAlternateEmail2()) ) {   
        errorMessage += PropertyProvider.get("prm.personal.profile.name.error.alt2email.message") + "<br>";
        addErrorObject("alternateEmail2");
        isInfoValid = false;
    }

    // now, make sure any of the new email addresses don't already exist
    /*if ( !request.getParameter ("alternateEmail3").equals (alternateEmail3) &&
         registration.checkEmailExists (registration.getAlternateEmail3()) ) {   
        errorMessage += PropertyProvider.get("prm.personal.profile.name.error.alt3email.message") + "<br>";
        addErrorObject("alternateEmail3");
        isInfoValid = false;
    }*/
    
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
	/*if(request.getParameter("alternateEmail3").equals(""))
	{
		registration.setAlternateEmail3("");
	}*/
	
	if(StringUtils.isNotEmpty(request.getParameter("skype"))) {
		registration.setSkype(request.getParameter("skype"));
	} else {
		registration.setSkype("");
	}
	if(StringUtils.isNotEmpty(request.getParameter("skillsBio"))) {
		registration.setSkillsBio(request.getParameter("skillsBio"));
	} else {
		registration.setSkillsBio("");
	}

	// Everything is OK, store goto the next tab
	if (isInfoValid)
	{   

		// added following code to verify alernate email updated by user
		//BFD-2062 Start
		//only one alternate email can be verified for each time if found new
		String unveryfiedEmailAddress = null;
		if(!((String)request.getParameter ("alternateEmail1")).equals (alternateEmail1)){
			unveryfiedEmailAddress = (String)request.getParameter ("alternateEmail1");
		}else if(!((String)request.getParameter ("alternateEmail2")).equals (alternateEmail2)){
			unveryfiedEmailAddress = (String)request.getParameter ("alternateEmail2");
		}/*else if(!((String)request.getParameter ("alternateEmail3")).equals (alternateEmail3)){
			unveryfiedEmailAddress = (String)request.getParameter ("alternateEmail3");
		}*/
		if(null != unveryfiedEmailAddress ){
			try{
				registration.setStatus(PersonStatus.UNCONFIRMED); // set status to UNCONFIRMED
				registration.sendVerificationEmailForAlternateEmail(unveryfiedEmailAddress);
			}catch(Exception sendVerificationMailException){
		        errorMessage += PropertyProvider.get("prm.personal.profile.name.error.verificationemailaddress") + "<br>";
		        addErrorObject("veryficationEmailAddress");				
			}
		} // end code addition for 2062

		historyBean.setProjectDisplayName(registration.getDisplayName());
		registration.updateInvitedUser();
        user.setEmail(registration.getEmail());
        user.load();
		
		pageContext.forward(request.getParameter("nextPage"));
	}
	// Return for corrections
	else
	{
        request.setAttribute("errorMsg", errorMessage);
        request.setAttribute("errorObj", errorObject);
		pageContext.forward("ProfileName.jsp");
   }
}
// go to the requested tab without applying.
else
{
	pageContext.forward(request.getParameter("nextPage"));
}
%>
<script language="javascript" src="/src/util.js"></script>

<script Language="JavaScript">
refresh_banner();
</script>
