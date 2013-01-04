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
    info="include page for personal space myProjects channel"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.portfolio.ProjectPortfolioBean,
            net.project.security.SessionManager,
            net.project.portfolio.view.PersonalPortfolioViewContext,
            net.project.portfolio.view.PersonalPortfolioViewList,
            net.project.portfolio.view.PersonalPortfolioView,
            net.project.portfolio.view.PersonalPortfolioViewResults,
            net.project.portfolio.view.ResultType,
            net.project.base.property.PropertyProvider,
            net.project.portfolio.view.PersonalPortfolioDefaultScenario,
            net.project.persistence.PersistenceException,
            net.project.util.ErrorReporter"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="projectPortfolio" class="net.project.portfolio.ProjectPortfolioBean" scope="session" />

<%
    ErrorReporter errorReporter = new ErrorReporter();
    PersonalPortfolioView view = null;

    PersonalPortfolioViewContext viewContext = (PersonalPortfolioViewContext)session.getAttribute("viewContext");
    if (viewContext == null) {
        projectPortfolio.clear();
        projectPortfolio.setID(projectPortfolio.getUserPortfolioID(user.getID()));
        projectPortfolio.setUser(user);
        projectPortfolio.load();

        personalSpace.setUser(user);
        viewContext = new PersonalPortfolioViewContext(projectPortfolio);
        viewContext.setSpace(personalSpace);
        viewContext.setCurrentUser(user);
    }

    String viewID = request.getParameter("PersonalSpace_projects_viewID");
	String viewIDParameter = "";
	if (viewID != null) viewIDParameter = "PersonalSpace_projects_viewID="+viewID;
    PersonalPortfolioViewList viewList = (PersonalPortfolioViewList) viewContext.makeViewList();
    // Grab the view for the viewID or the default view for the Personalspace dashboard scenario
    view = (PersonalPortfolioView) viewList.getViewOrDefault(viewID, PersonalPortfolioDefaultScenario.PERSONALSPACE_DASHBOARD);

	PersonalPortfolioView.SortParameters sortParameters = new PersonalPortfolioView.SortParameters();
	String sortColumn = request.getParameter("sortcolumn");
	String sortOrder = request.getParameter("sortorder");
	String sortType = request.getParameter("sorttype");
	if (sortColumn == null) { // here we can set the default values
	}
	sortParameters.setSortProperty(sortColumn);
	if ("dsc".equals(sortOrder))
		sortParameters.setAscending(false);
	if ("number".equals(sortType))
		sortParameters.setPropertyConversion(PersonalPortfolioView.SortParameters.STRING_TO_DOUBLE);
	view.setSortParameters(sortParameters);

    PersonalPortfolioViewResults viewResults = null;
    try {
        viewResults = (PersonalPortfolioViewResults) view.getResults();
    } catch (PersistenceException e) {
        // There was a problem loading the view; display Default instead
        String failedViewName = view.getName();
        view = (PersonalPortfolioView) viewList.getDefaultViews().get(0);
        errorReporter.addError(PropertyProvider.get("prm.personal.main.channel.myprojects.failedview.message", failedViewName, view.getName()));
        viewResults = (PersonalPortfolioViewResults) view.getResults();
    }
    pageContext.setAttribute("viewResults", viewResults, PageContext.PAGE_SCOPE);

    // Decide which XSL file we need; the tree view has custom XSL to
    // assist with presentation
    String xslFile = null;
    if (viewResults.getResultType().equals(ResultType.TREE)) {
        xslFile = "/personal/include/xsl/my-projects-tree.xsl";
    } else {
        xslFile = "/personal/include/xsl/my-projects-view.xsl";
    }
%>
<script language="javascript" type="text/javascript">
function PersonalSpace_projects_refresh(field) {
    var parameters = 'PersonalSpace_projects_viewID=' + getSelectedValue(field);
    if (self.refresh) {
        self.refresh(parameters);
    } else if (parent.refresh) {
        parent.refresh(parameters);
    }
}

</script>

<form name="PersonalSpace_projects_main" method="post">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<%  if (errorReporter.errorsFound()) { %>
<tr><td colspan="3">
    <pnet-xml:transform stylesheet="/base/xsl/error-report.xsl" xml="<%=errorReporter.getXML()%>" />
</td></tr>
<%  } %>
<tr>
	<td>&nbsp;</td>
    <td class="tableContent">
    	<span class="over-table"><display:get name="prm.personal.main.channel.myprojects.selectview.label" /></span>
        &nbsp;
        <select name="PersonalSpace_projects_viewID" onChange="PersonalSpace_projects_refresh(this);" class="personal-dashboard-tree">
            <%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(viewList.getAllViews(), view)%>
        </select>
        &nbsp;        
        <span class="over-table-comment">
        	<output:text><%=view.getDescription() != null ? view.getDescription() : PropertyProvider.get("prm.portfolio.personal.main.portfolioscore.defaultname")%></output:text>
        </span>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="tableContent">
		<%
			if (viewResults.getResultType().equals(ResultType.TREE)) {
			%>
			<pnet-xml:transform name="viewResults" scope="page" stylesheet="<%=xslFile%>" />
			<%
			} else {
				String oldSortColumn = (String) request.getSession().getAttribute("oldSortColumnPersonalDashboard");
				String oldSortOrder = (String) request.getSession().getAttribute("oldSortOrderPersonalDashboard");

				out.println(net.project.portfolio.view.MetaColumnView.getHtml(viewResults, view, oldSortColumn, oldSortOrder));
				
				request.getSession().setAttribute("oldSortColumnPersonalDashboard", request.getParameter("sortcolumn"));
				request.getSession().setAttribute("oldSortOrderPersonalDashboard", request.getParameter("sortorder"));
			}
		%>
	</td>
    <td>&nbsp;</td>
</tr>
</table>
</form>