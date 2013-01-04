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
    info="Edit Domain Processing"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,            
            net.project.security.SessionManager"
 %>
<%@ include file="/base/taglibInclude.jsp" %>


<%@page import="net.project.base.property.PropertyProvider"%>
<%@page import="net.project.hibernate.model.PnUserDomain"%>
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.base.property.Token"%>
<%@page import="net.project.base.RecordStatus"%>
<%@page import="net.project.util.JSPUtils"%>
<%@page import="net.project.util.StringUtils"%>
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    String theAction = request.getParameter("theAction");
    String domainID = request.getParameter("domainID");
    System.out.println("\n\n\n1. theAction:"+theAction+"\n\n\n\n");

    if (theAction != null && theAction.equals("submit")) {
%>
		<jsp:setProperty name="domain" property="*" />
<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			domain.setDescription("");
		}
		if ((request.getParameter("supportedConfigurations") == null) || (request.getParameter("supportedConfigurations").equals(""))) {
			domain.setSupportedConfigurations(new String[0]);
		}
		if ((request.getParameter("registrationInstructions") == null) || (request.getParameter("registrationInstructions").equals(""))) {
			domain.setRegistrationInstructions("");
		}
        String supportsCreditCardPurchases = request.getParameter("supportsCreditCards");
        domain.setSupportsCreditCardPurchases(supportsCreditCardPurchases != null &&
            supportsCreditCardPurchases.equals("1"));

        // Set the domainID if one was being edited
    	if(domainID != null && domainID.trim().length() > 0) {
    		domain.setID(domainID);
    	}

    	domain.store();

        if (request.getParameter("nextPage") != null && request.getParameter("nextPage").trim().length() > 0) {
            response.sendRedirect(SessionManager.getJSPRootURL() + request.getParameter("nextPage"));
        } else {
            response.sendRedirect(SessionManager.getJSPRootURL() + "/admin/domain/Main.jsp?module="+net.project.base.Module.APPLICATION_SPACE);
        }
 
    } else if (String.valueOf(net.project.security.Action.DELETE).equals(theAction)){
    	if(domainID != null){
    		PnUserDomain userDomain = ServiceFactory.getInstance().getPnUserDomainService().getUserDomain(Integer.valueOf(domainID));
    		userDomain.setRecordStatus("D");
    		ServiceFactory.getInstance().getPnUserDomainService().updateUserDomain(userDomain);    		
    	}
    	pageContext.forward("Main.jsp?module=240");
    	
    } else {
        throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.domain.invalid.missing.action.label"));
    }
%>
