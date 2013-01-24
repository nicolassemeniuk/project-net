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

<%@ page contentType="text/html; charset=UTF-8" language="java"
	errorPage="/errors.jsp"
	import="net.project.security.*,
            net.project.project.*,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.space.Space,
            net.project.portfolio.view.PersonalPortfolioViewList,
            net.project.portfolio.view.PersonalPortfolioView,
            net.project.portfolio.view.PersonalPortfolioViewContext,
            net.project.portfolio.view.PersonalPortfolioViewResults,
            net.project.portfolio.view.ResultType,
            net.project.portfolio.view.PersonalPortfolioDefaultScenario,
            net.project.space.SpaceType,
            net.project.space.ISpaceTypes,
            net.project.persistence.PersistenceException,
            net.project.util.ErrorReporter,
            net.project.base.ObjectType"%>

<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider"
	class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personalSpace"
	class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="docManager"
	class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager"
	scope="session" />
<jsp:useBean id="projectPortfolio"
	class="net.project.portfolio.ProjectPortfolioBean" scope="session" />

<% 
    // no security check necessary since this is the users portfolio
    // Switch to personal space if not currently in Personal, Business or Project space

    int currentModule = 0;
    Space currentSpace = user.getCurrentSpace();
    if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
        currentModule = Module.PERSONAL_SPACE;
    } else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
        currentModule = Module.BUSINESS_SPACE;
    } else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
        currentModule = Module.PROJECT_SPACE;
    } else {
        user.setCurrentSpace(personalSpace);
        docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/personal/Main.jsp");
        currentModule = Module.PERSONAL_SPACE;
    }

    // Put the module in the request so that including channel items
    // in a scrollable frame works
    request.setAttribute("module", "" + currentModule);

    // Setup portfolio
    projectPortfolio.clear();
    projectPortfolio.setID(projectPortfolio.getUserPortfolioID(user.getID()));
    projectPortfolio.setUser(user);
    projectPortfolio.load();

    // Now set up the search object based on this portfolio
    search.clear();
    ProjectSpace entry = null;
    java.util.Iterator it = projectPortfolio.iterator();
    while (it.hasNext()) {
        entry = (ProjectSpace) it.next();
        search.addSearchSpace(entry.getID(), entry.getName(), entry.getDescription());
    }

    // Initialize the available views for the current personal space
    personalSpace.setUser(user);
    PersonalPortfolioViewContext viewContext = new PersonalPortfolioViewContext(projectPortfolio);
    viewContext.setSpace(personalSpace);
    viewContext.setCurrentUser(user);
    session.removeAttribute("viewContext");
    session.setAttribute("viewContext", viewContext);

    PersonalPortfolioViewList viewList = (PersonalPortfolioViewList) viewContext.makeViewList();
    PersonalPortfolioView view = null;

    String viewID = request.getParameter("viewID");
    // Grab the view for the viewID or the default view for the PORTFOLIO scenario
    if (viewID == null)
		viewID = (String) session.getAttribute("ppviewID");
	else
		session.setAttribute("ppviewID", viewID);
	view = (PersonalPortfolioView) viewList.getViewOrDefault(viewID, PersonalPortfolioDefaultScenario.PORTFOLIO);

	PersonalPortfolioView.SortParameters sortParameters = new PersonalPortfolioView.SortParameters();
	String sortColumn = request.getParameter("sortcolumn");
	String sortOrder = request.getParameter("sortorder");
	String sortType = request.getParameter("sorttype");

	sortParameters.setSortProperty(sortColumn);

	if ("dsc".equals(sortOrder))
		sortParameters.setAscending(false);
	if ("number".equals(sortType))
		sortParameters.setPropertyConversion(PersonalPortfolioView.SortParameters.STRING_TO_DOUBLE);
	view.setSortParameters(sortParameters);	

	session.removeAttribute("view");
    session.setAttribute("view", view);

    ErrorReporter errorReporter = new ErrorReporter();
    PersonalPortfolioViewResults viewResults = null;
    try {
        viewResults = (PersonalPortfolioViewResults) view.getResults();
    } catch (PersistenceException e) {
        // There was a problem loading the view; display Default instead
        String failedViewName = view.getName();
        view = (PersonalPortfolioView) viewList.getDefaultViews().get(0);
        session.removeAttribute("view");
        session.setAttribute("view", view);
        errorReporter.addError(PropertyProvider.get("prm.portfolio.personal.main.failedview.message", failedViewName, view.getName()));
        viewResults = (PersonalPortfolioViewResults) view.getResults();
    }
    session.removeAttribute("viewResults");
    session.setAttribute("viewResults", viewResults);

    String refLink = SessionManager.getJSPRootURL() +  "/portfolio/PersonalPortfolio.jsp?module=" + currentModule + "&portfolio=true";
    String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

    function exportPDF() {
        //self.document.location=JSPRootURL+ "/servlet/MetaColumnPdfView?" + "module=<%=currentModule%>&action=<%=Action.VIEW%>&viewID=" +getSelectedValue(theForm.viewID) + "&portfolio=true&spaceType=<%=ISpaceTypes.PROJECT_SPACE%>";
        self.document.location=JSPRootURL+ "/servlet/MetaColumnPdfView?" + "module=<%=currentModule%>&action=<%=Action.VIEW%>&viewID=" +getSelectedValue(theForm.viewID) + "&portfolio=true";
	}
     // Internationalization message for blog popup
    var noSelectionErrMes = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>';
    var spaceId = <%= user.getCurrentSpace().getID()%>;
    var moduleId = <%= currentModule%>;
	var objectTypeNameFor ='Project';
	var blogItFor = 'PersonalPortFolio';

	function setup() {
	    theForm = self.document.forms[0];
	    isLoaded = true;
	}

	function help() {
	    var helplocation=JSPRootURL+"/help/Help.jsp?page=project_portfolio";
	    openwin_help(helplocation);
	}

	function cancel() {
		self.document.location = JSPRootURL + "/personal/Main.jsp"; 
	}

	function reset() {
		changeView();
	}

	function create() {
		self.document.location = JSPRootURL + "/project/ProjectCreate.jsp?module=<%= currentModule %>&portfolio=true";
	}

	function remove() {

		var field = theForm.elements["selected"];
		if(!field && document.getElementById('channelIFrame') != null){
			field = getSelectedProjectIdFromIframe();
		}		

		if(field != null) {
			flag = verifySelectionForField(field, 'multiple', '<%=PropertyProvider.get ("prm.global.javascript.verifyselection.noselection.error.message")%>');
		} else {
			extAlert(errorTitle, "<display:get name='prm.global.javascript.verifyselection.noprojectsinlist.error.message'/>" , Ext.MessageBox.ERROR);
		}

		if (flag) {
			var m_url = JSPRootURL + "/portfolio/PersonalPortfolioDelete.jsp?selected="+ getSelectedValueLocal() +"&module=<%= Module.PROJECT_SPACE %>"+"&action=<%= net.project.security.Action.VIEW %>&portfolio=true";
			var redirect_url = JSPRootURL +"/project/Main.jsp?id="+ getSelectedValueLocal()+"&page="+ escape(m_url);
			var link_win = openwin_linker(redirect_url);
			link_win.focus();
		}		
	}

	function modify() {
		var field = theForm.elements["selected"];
		var flag = false;
		if(!field && document.getElementById('channelIFrame') != null){
			field = getSelectedProjectIdFromIframe();
		}		
		if(field != null) {
			flag = verifySelectionForField(field, 'multiple', '<%=PropertyProvider.get ("prm.global.javascript.verifyselection.noselection.error.message")%>');
		} else {
			extAlert(errorTitle, "<display:get name='prm.global.javascript.verifyselection.noprojectsinlist.error.message'/>" , Ext.MessageBox.ERROR);
		}
		if (flag) {
			var requestPage = JSPRootURL+"/project/PropertiesEdit.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>%26action=<%=net.project.security.Action.MODIFY%>%26referer=portfolio/PersonalPortfolio.jsp?portfolio=true";
			self.document.location = JSPRootURL + "/project/Main.jsp?id=" + getSelectedValueLocal() + "&page=" + requestPage;
		}
	}

	// returns a selected project id in iframe
	function getSelectedProjectIdFromIframe() {
		var iFrameTags = document.getElementsByTagName('iframe');
		for( var index = 0; index < iFrameTags.length; index++){
			if(iFrameTags[index].contentWindow.document.getElementById('iFrameForm') != null)
				return iFrameTags[index].contentWindow.document.getElementById('iFrameForm').elements['selected'];
		}
	}

	function getSelectedValueLocal() {
		var field = theForm.elements["selected"];
		if(!field) {
			field = getSelectedProjectIdFromIframe();
			var idval = field.value;
			for (var i = 0; i < field.length; i++) {
				if (field[i].checked == true) {
					idval = field[i].value;
					break;
				}
			}
   			return idval;
		} else {
			return getSelection(theForm);
		}
	}

	function search() {
	    self.document.location = JSPRootURL + "/search/Search.jsp?module=<%=currentModule%>&refLink=<%=refLinkEncoded%>&spaceType=<%=ISpaceTypes.PROJECT_SPACE%>";
	}

	function changeView() {
	    self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?"+
	        "module=<%=currentModule%>&action=<%=Action.VIEW%>&viewID=" +
	        getSelectedValue(theForm.viewID) + "&portfolio=true";
	}

	function doSort(column, valueType) {
		var oldSortColumn = '<%=sortColumn%>';
		var oldSortOrder = '<%=sortOrder%>';
		var suffix = '';
		if (valueType == "number")
			suffix = "&sorttype=number";
		if ((column == oldSortColumn) && (oldSortOrder == "asc")) {
			self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?sortcolumn=" + column + "&sortorder=dsc" + suffix + "&portfolio=true";
			return;
		}
		self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?sortcolumn=" + column + "&sortorder=asc" + suffix + "&portfolio=true";
	}
	function showTwoPaneView(){
		self.document.location = JSPRootURL + "/portfolio/Project?module=" + <%=Module.PERSONAL_SPACE%> + "&portfolio=true";
	}

	// Subscribe notification 
	function notify(){
		var targetObjectID = getSelection(theForm);
		
		if(!targetObjectID){
			extAlert(errorTitle, '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>' , Ext.MessageBox.ERROR);
		}else {
			var m_url = JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID=" + targetObjectID + "&action=<%=net.project.security.Action.MODIFY%>&module=<%=Module.PROJECT_SPACE%>&moduleType=project&spaceID=" + targetObjectID + "&projectID=" + targetObjectID;
			spaceId = targetObjectID;
			openNotifyPopup(targetObjectID, m_url);
		}
	}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<%-- Setup the history, but don't display on this page --%>
<history:history displayHere="false">
	<history:business show="false" />
	<history:project displayToken='prm.project.portfoliopage.title'
		jspPage='<%=SessionManager.getJSPRootURL() + "/portfolio/PersonalPortfolio.jsp?module=" + currentModule + "&portfolio=true"%>' />
</history:history>

<div id='leftheading-project'><%=PropertyProvider.get("prm.portfolio.personal.main.title")%></div>
<div style='clear: both'></div>

<tb:toolbar style="tooltitle" showAll="true">
	<tb:band name="standard">

		<tb:button type="modify"
			label='<%= PropertyProvider.get("prm.project.main.modify.button.tooltip")%>' />

		<tb:button type="create"
			label='<%= PropertyProvider.get("prm.project.main.create.button.tooltip")%>' />

		<tb:button type="remove"
			label='<%= PropertyProvider.get("prm.project.main.delete.button.tooltip")%>' />

		<tb:button type="custom" label='Export View'
			imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.personalizepage.image.on") %>'
			imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.personalizepage.image.over") %>'
			function="javascript:exportPDF();" />

		<display:if name="@prm.blog.isenabled">

			<tb:button type="blogit" />

		</display:if>

		<tb:button type="personalize_page" />

		<tb:button type="custom" label="Two Pane View"  function="javascript:showTwoPaneView();"/>

		<tb:button type="notify" />

	</tb:band>

</tb:toolbar>

<div class="project-view">

<form id="personalPortfolioForm"
	action="<%=SessionManager.getJSPRootURL()%>/project/RemoveProjectProcessing.jsp"
	method="post"><input type="hidden" name="module"
	value="<%= net.project.base.Module.PERSONAL_SPACE %>"> <input
	type="hidden" name="theAction">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<%  if (errorReporter.errorsFound()) { %>

	<tr>
		<td colspan="3"><pnet-xml:transform
			stylesheet="/base/xsl/error-report.xsl"
			xml="<%=errorReporter.getXML()%>" /></td>
	</tr>

	<%  } %>

	<tr>

		<td>&nbsp;</td>

		<td class="tableHeader"><display:get
			name="prm.portfolio.personal.main.selectview.label" /> &nbsp; <select
			name="viewID" onChange="changeView();">

			<%=net.project.gui.html.HTMLOptionList.makeHtmlOptionList(viewList.getAllViews(), view)%>
		</select> &nbsp; <a
			href="<%=SessionManager.getJSPRootURL()%>/portfolio/ManageViewsController.jsp?module=<%=currentModule%>">

		<display:get name="prm.portfolio.personal.main.manageviews.label" />

		</a></td>

		<td>&nbsp;</td>

	</tr>

</table>

<br>

<channel:channel customizable="true" name="personal_portfolio"
	settingsScope="global">

	<% if (viewResults.getProjectSpaceResultElements().size() > 0) { %>

	<channel:insert name="portfolio_status"
		title='<%=PropertyProvider.get("prm.portfolio.personal.main.channel.portfoliostatus.name")%>'
		minimizable="true" closeable="true" width="50%" channelAlign="center"
		include="/portfolio/include/PortfolioStatus.jsp" row="1" column="1" />

	<channel:insert name="portfolio_budget"
		title='<%=PropertyProvider.get("prm.portfolio.personal.main.channel.portfoliobudget.name")%>'
		minimizable="true" closeable="true" channelAlign="center"
		include="/portfolio/include/PortfolioBudget.jsp" row="1" column="2" />

	<% } %>

	<channel:insert name="portfolio_scorecard"
		title='<%=net.project.util.HTMLUtils.escape(view.getName())%>'
		minimizable="false" closeable="false" width="100%"
		include="/portfolio/include/PortfolioScorecard.jsp" row="2" column="1" />

</channel:channel></form>

<%@ include file="/help/include_outside/footer.jsp"%>

<template:getSpaceJS space="project" />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>

</html>