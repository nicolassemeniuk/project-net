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
    info="Financial Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.financial.FinancialSpaceBean, 
				net.project.financial.FinancialSpace,
				net.project.security.User, 
				net.project.security.SecurityProvider,
				net.project.security.SessionManager,
				net.project.base.Module,
				net.project.space.Space,
				net.project.space.SpaceList,
				net.project.space.SpaceManager,
				net.project.base.property.PropertyProvider,
            	java.net.URLDecoder,
		        net.project.space.SpaceURLFactory"
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="financialSpace" class="net.project.financial.FinancialSpaceBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%--------------------------------------------------------------------------------------------------------
  -- Security Check                                                                                 
  ------------------------------------------------------------------------------------------------------%>
<%
    String id = request.getParameter("id");
	if (id != null) {
        // Security Check: Is user allowed access to requested space?
        Space testSpace = new FinancialSpaceBean();
        testSpace.setID(id);
        Space oldSpace = securityProvider.getSpace();
        securityProvider.setSpace(testSpace);
		
        if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.FINANCIAL_SPACE),
                                             net.project.security.Action.VIEW)) {
            // Passed Security Check
			financialSpace.setID(id);
    	    financialSpace.load();
			
		} 
		else {
            // Failed Security Check
            securityProvider.setSpace(oldSpace);
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace);
        }
	} 
	// id was null
	else {
		if(user.getCurrentSpace() instanceof FinancialSpaceBean)
    		financialSpace = (FinancialSpaceBean)user.getCurrentSpace();
			if (financialSpace.getID() != null) {
        	financialSpace.load();
		}
    }
	
	request.setAttribute("id", "");
	request.setAttribute("module", Integer.toString(net.project.base.Module.FINANCIAL_SPACE));
%>

<%--------------------------------------------------------------------------------------------------------
  -- Configure Objects                                                                              
  ------------------------------------------------------------------------------------------------------%>
<%
	// Set user's current space to this Financial
    user.setCurrentSpace(financialSpace);
	
	String redirect = request.getParameter("page");
	if(redirect.equals("dashboard"))
	{
		String forwardTo = SessionManager.getJSPRootURL() + "/financial/Dashboard?module=175&id="+financialSpace.getID();
	    response.sendRedirect(URLDecoder.decode(SpaceURLFactory.constructForwardFromSpaceURL(forwardTo), SessionManager.getCharacterEncoding()));
	    return;		
	}
%>
