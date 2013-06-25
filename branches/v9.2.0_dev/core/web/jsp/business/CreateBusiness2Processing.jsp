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

<%@page import="org.apache.cxf.bus.BusState"%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="New Business Processing 2"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.business.BusinessCreateWizard,
    		net.project.methodology.MethodologyProvider,
    		net.project.security.User,
    		net.project.security.SessionManager,
    		net.project.hibernate.model.PnMethodologySpace,
    		net.project.hibernate.service.ServiceFactory,
    		net.project.space.SpaceRelationship,
    		net.project.hibernate.model.PnSpaceHasSpace"
%>

<jsp:useBean id="businessWizard" class="net.project.business.BusinessCreateWizard" scope="session" />
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" /> 
<jsp:useBean id="financialWizard" class="net.project.financial.FinancialCreateWizard" scope="page" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:setProperty name="businessWizard" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("address1") == null) || ((request.getParameter("address1").equals("")))) {
		businessWizard.setAddress1("");
	}
	if ((request.getParameter("address2") == null) || ((request.getParameter("address2").equals("")))) {
		businessWizard.setAddress2("");
	}
	if ((request.getParameter("address3") == null) || ((request.getParameter("address3").equals("")))) {
		businessWizard.setAddress3("");
	}
	if ((request.getParameter("city") == null) || ((request.getParameter("city").equals("")))) {
		businessWizard.setCity("");
	}
	if ((request.getParameter("provinceCode") == null) || ((request.getParameter("provinceCode").equals("")))) {
		businessWizard.setProvinceCode("");
	}
	if ((request.getParameter("postalCode") == null) || ((request.getParameter("postalCode").equals("")))) {
		businessWizard.setPostalCode("");
	}
	if ((request.getParameter("countryCode") == null) || ((request.getParameter("countryCode").equals("")))) {
		businessWizard.setCountryCode("");
	}
	if ((request.getParameter("phone") == null) || ((request.getParameter("phone").equals("")))) {
		businessWizard.setPhone("");
	}
	if ((request.getParameter("fax") == null) || ((request.getParameter("fax").equals("")))) {
		businessWizard.setFax("");
	}
	if ((request.getParameter("website") == null) || ((request.getParameter("website").equals("")))) {
		businessWizard.setWebsite("");
	}
%>
	
<%
	//The name is the same as the business
	financialWizard.setName(businessWizard.getName());
	if((businessWizard.getDescription() == null) || (businessWizard.getDescription().equals("")))
		financialWizard.setDescription("");
	else 
		financialWizard.setDescription(businessWizard.getDescription());
	financialWizard.setUser(user);
	
	//Set the financial space/business space relational data
	financialWizard.setRelatedSpaceID(businessWizard.getID());
	financialWizard.setRelatedSpace(businessWizard);
	financialWizard.setRelationship(SpaceRelationship.FINANCIAL);
	
	PnSpaceHasSpace spaceHasSpace = null;
	
	//If the business has a parent space Id, find the financial space related to that business space.
	if(businessWizard.getParentSpaceID()!=null && !businessWizard.getParentSpaceID().equals("")){
		spaceHasSpace = ServiceFactory.getInstance().getPnSpaceHasSpaceService().getFinancialRelatedSpace(businessWizard.getParentSpaceID());
	} 
	
	//If found, set that financial space has sub-space of the new created financial space.
	if(spaceHasSpace!=null){
		financialWizard.setParentSpaceID(String.valueOf(spaceHasSpace.getComp_id().getChildSpaceId()));
	}
	
	financialWizard.store();
	businessWizard.store();
	


	// Apply the Methodology
	if (businessWizard.getMethodologyID() != null && (!businessWizard.getMethodologyID().equals(""))) {
		methodologyProvider.setUser(user);
		methodologyProvider.applyMethodology(businessWizard.getMethodologyID(), businessWizard.getID());
		// mark template as used
        PnMethodologySpace methodology = ServiceFactory.getInstance().getPnMethodologySpaceService().getMethodologySpace(Integer.valueOf(businessWizard.getMethodologyID()));
        if(methodology.getIsUsed() == null || methodology.getIsUsed() == 0){
        	methodology.setIsUsed(1);
        	ServiceFactory.getInstance().getPnMethodologySpaceService().updateMethodologySpace(methodology);
        }
	}
	if(request.getParameter("parent") != null && request.getParameter("parent").equals("null")){
		response.sendRedirect(SessionManager.getJSPRootURL()+ "/business/BusinessPortfolio.jsp?module=" + net.project.base.Module.BUSINESS_SPACE + "&portfolio=true");
	}else{
		response.sendRedirect(SessionManager.getJSPRootURL()+ "/business/subbusiness/Main.jsp?module=" + net.project.base.Module.BUSINESS_SPACE + "&portfolio=true");
	}
%>
