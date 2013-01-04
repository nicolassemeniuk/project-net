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
    info="modify business processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
            	net.project.business.BusinessSpaceBean,
            	net.project.security.SessionManager,
				net.project.base.property.PropertyProvider,
            	net.project.security.SecurityProvider"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />

<%
    //Make sure dropdown boxes are "unset" before continuing.  Otherwise a person
    //can't change "sub-business of" to be empty.  If we set it explicitly to empty
    //first, then it will be reset automatically if they haven't changed it.
    businessSpace.setParentSpaceID(null);
%>
<jsp:setProperty name="businessSpace" property="*" />
<%
    String theAction = request.getParameter("theAction");

    if (theAction.equals("submit")) {

		// SECURITY CHECK
		String space_id = businessSpace.getID();
		String id = securityProvider.getCheckedObjectID();
		int module = securityProvider.getCheckedModuleID();
		int action = securityProvider.getCheckedActionID();
		if (module != net.project.base.Module.BUSINESS_SPACE || action != net.project.security.Action.MODIFY || id.length() > 0)
		{
			throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.business.modifybusiness.authorizationfailed.message"));
		}
		
		// bfd - 2994 issue
		if ((request.getParameter("flavor") == null) || (request.getParameter("flavor").equals(""))) {
			businessSpace.setFlavor("");
		}
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			businessSpace.setDescription("");
		}
		if ((request.getParameter("address1") == null) || (request.getParameter("address1").equals(""))) {
			businessSpace.setAddress1("");
		}
		if ((request.getParameter("address2") == null) || (request.getParameter("address2").equals(""))) {
			businessSpace.setAddress2("");
		}
		if ((request.getParameter("address3") == null) || (request.getParameter("address3").equals(""))) {
			businessSpace.setAddress3("");
		}
		if ((request.getParameter("city") == null) || (request.getParameter("city").equals(""))) {
			businessSpace.setCity("");
		}
		if ((request.getParameter("postalCode") == null) || (request.getParameter("postalCode").equals(""))) {
			businessSpace.setPostalCode("");
		}
		if ((request.getParameter("countryCode") == null) || (request.getParameter("countryCode").equals(""))) {
			businessSpace.setCountryCode("");
		}
		if ((request.getParameter("phone") == null) || (request.getParameter("phone").equals(""))) {
			businessSpace.setPhone("");
		}
		if ((request.getParameter("fax") == null) || (request.getParameter("fax").equals(""))) {
			businessSpace.setFax("");
		}
		if ((request.getParameter("website") == null) || (request.getParameter("website").equals(""))) {
			businessSpace.setWebsite("");
		}
		//end of bfd-2994 issue
		
		// Store the changes to the business space.
		if(businessSpace.getParentSpaceID() != null){
			businessSpace.setParentChanged(!businessSpace.getParentSpaceID().equals(request.getParameter("previousParentSpaceID").trim()));
		} else {
			businessSpace.setParentChanged(!request.getParameter("previousParentSpaceID").trim().equals(""));
		}
		
		businessSpace.store();     
    }

		else if (theAction.equals("removeLogo")) 
                    {
                            businessSpace.removeLogo();
                }


		// Go back to business space dashboard.
		if(request.getParameter("referer") != null ){
			response.sendRedirect(request.getParameter("referer"));
		} else {
			response.sendRedirect(SessionManager.getJSPRootURL() + "/business/Main.jsp?module=" + net.project.base.Module.BUSINESS_SPACE + "&action=" + net.project.security.Action.VIEW);
		}
		
%>


