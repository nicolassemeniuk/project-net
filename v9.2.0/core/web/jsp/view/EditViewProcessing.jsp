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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Edit View Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.view.IViewList,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.PnetException,
            net.project.base.finder.FinderIngredientHTMLConsumer,
            net.project.portfolio.view.ViewBuilderFilterPage,
            net.project.portfolio.view.PersonalPortfolioViewBuilder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="viewBuilder" type="net.project.portfolio.view.ViewBuilder"  scope="session" />

<%
    String module = request.getParameter("module");
%>
<security:verifyAccess module="<%=Integer.valueOf(module).intValue()%>" action="view" />

<%
    String theAction = request.getParameter("theAction");

    if (theAction != null &&
            (theAction.equals("submit") || theAction.equals("switchTab"))) {
%>
        <jsp:setProperty name="viewBuilder" property="name" />
<%
		viewBuilder.setDescription(net.project.util.StringUtils.isNotEmpty(request.getParameter("description")) ? request.getParameter("description") : "");
		// Update the default view settings
        viewBuilder.updateDefaultViewSettings(request, "defaultForScenarioID");

        // Set all the column, filter, grouper, sorter properties
        // Note: Column & Grouper not done yet
        FinderIngredientHTMLConsumer consumer = new FinderIngredientHTMLConsumer(request);

        // Process filter for current page
		if ("-1".equals(request.getParameter("currentFilterPageID"))) {
			if (viewBuilder instanceof PersonalPortfolioViewBuilder) {
				PersonalPortfolioViewBuilder personalPortfolioViewBuilder = (PersonalPortfolioViewBuilder) viewBuilder;
				personalPortfolioViewBuilder.updateMetaColumnsFromRequest(request);
			}
		} else {
			viewBuilder.getFilterPageForID(request.getParameter("currentFilterPageID")).getFinderFilterList().accept(consumer);
		}

        // Process Sorter for current page
		if (request.getParameter("currentSorterPageID") != null) {
			viewBuilder.getSorterPageForID(request.getParameter("currentSorterPageID")).getFinderSorterList().accept(consumer);
		}

		String params = "?module=" + module;
		if (request.getParameter("selectedFilterTabID") != null)
			params += "&selectedFilterTabID=" + request.getParameter("selectedFilterTabID");
        if (theAction.equals("switchTab")) {
            // Switching tab page
            // So we go back to edit screen
			response.sendRedirect(SessionManager.getJSPRootURL()+"/view/EditView.jsp"+params);
//			pageContext.forward("/view/EditView.jsp");

        } else {
        	viewBuilder.store();
            // Done submitting, go back to ManageViews page
			response.sendRedirect(SessionManager.getJSPRootURL()+"/view/ManageViews.jsp"+params);
//			pageContext.forward("/view/ManageViews.jsp");
        }

    } else if (theAction != null && theAction.equals("cancel")) {
        pageContext.forward("/view/ManageViews.jsp");

    } else {
        throw new PnetException("Missing or unknown action in EditViewProcessing.jsp");
    }
%>
