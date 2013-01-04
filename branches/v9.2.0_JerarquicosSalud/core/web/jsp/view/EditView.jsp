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
    info="Edit View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.finder.FinderIngredientHTMLProducer,
            java.util.Iterator,
            net.project.portfolio.view.ViewBuilderFilterPage,
            net.project.portfolio.view.ViewBuilderSorterPage,
            net.project.portfolio.view.DefaultScenario,
            net.project.portfolio.view.DefaultViewSetting,
            net.project.portfolio.view.PersonalPortfolioViewBuilder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="viewBuilder" type="net.project.portfolio.view.ViewBuilder"  scope="session" />

<%
    String module = request.getParameter("module");
%>
<%-- Security:  Views are loaded and stored against a context --%>
<security:verifyAccess module="<%=Integer.valueOf(module).intValue()%>" action="view" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkDate.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
    focusFirstField(theForm);
    isLoaded = true;
}

function validateDate(fld) {
    var format = '<%=user.getDateFormatter().getDateFormatExample()%>';
    var dateFormat = new DateFormat(format);
    return dateFormat.checkValidDate(fld);
}

function  validateNumeric( strValue ) {
/*****************************************************************
DESCRIPTION: Validates that a string contains only valid numbers.

PARAMETERS:
   strValue - String to be tested for validity

RETURNS:
   True if valid, otherwise false.
******************************************************************/
  var objRegExp  =  /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/;

  //check for numeric characters
  return objRegExp.test(strValue);
}

function validateForm(frm) {

	if (!verifyColumnOrder()) {
		return false;
	}
	
  	if(!checkTextbox(frm.name,"<display:get name="prm.global.view.edit.namerequired.message" />")) {
  		return false;
  	}
    if(frm.description.value.length > 250){
    	var errorMessage = "<display:get name="prm.global.view.edit.descriptioninvalid.message" />";
    	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    	return false;
    }
  	if(frm.filterfilter_3 && (frm.filterfilter_3.value != '') && ((!validateNumeric(frm.filterfilter_3.value)) ||  (parseInt(frm.filterfilter_3.value, 10) > 100))) {
		var errorMessage = "<display:get name="prm.global.view.edit.overallcompletioninvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
  	}
	if(frm.filterfilter_4Start && (frm.filterfilter_4Start.value != '') && !validateDate(frm.filterfilter_4Start)) {
		var errorMessage = "<display:get name="prm.global.view.edit.startdateinvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
   	}
   	if(frm.filterfilter_4Finish && (frm.filterfilter_4Finish.value != '') && !validateDate(frm.filterfilter_4Finish)) {
		var errorMessage = "<display:get name="prm.global.view.edit.startdateinvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
   	}
   	if(frm.filterfilter_5Start && (frm.filterfilter_5Start.value != '') && !validateDate(frm.filterfilter_5Start)) {
		var errorMessage = "<display:get name="prm.global.view.edit.enddateinvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
   	}
   	if(frm.filterfilter_5Finish && (frm.filterfilter_5Finish.value != '') && !validateDate(frm.filterfilter_5Finish)) {
		var errorMessage = "<display:get name="prm.global.view.edit.enddateinvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
   	}
   	if(typeof(frm.filterfilter_19_value)!='undefined' && !(isNumber(frm.filterfilter_19_value.value))){
   		var errorMessage = "<display:get name="prm.global.view.edit.budgetcostinvalid.message" />";
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   		return false;
   	}
   return true;
}

function verifyColumnOrder() {
	var id = 0;
	var columnOrderArr = new Array();
	var inputElement = document.getElementById("orderid" + id);
	while (inputElement != null) {
		if (inputElement.value != "") {
			columnOrderArr[id] = inputElement.value;
		}
		id++;
		inputElement = document.getElementById("orderid" + id);
	}
	columnOrderArr.sort();
	
	if(columnOrderArr.length == 1 && columnOrderArr[0] < 0 ){
		extAlert(errorTitle,  "<display:get name="prm.global.view.edit.minusnotallowed.message"/>",  Ext.MessageBox.ERROR);
		return false;
	}
	if (columnOrderArr.length > 1) {
		for (i = 0; i < columnOrderArr.length - 1; i++) {
			if(columnOrderArr[i] < 0 ){
				extAlert(errorTitle,  "<display:get name="prm.global.view.edit.minusnotallowed.message"/>",  Ext.MessageBox.ERROR);
				return false;
			}
			if (columnOrderArr[i] && columnOrderArr[i+1] && columnOrderArr[i] == columnOrderArr[i+1]) {
				extAlert(errorTitle, 
				"<display:get name="prm.global.view.edit.duplicatecolumnorder.message" />", 
				Ext.MessageBox.ERROR);
				return false;
			}
		}
	}
	return true;
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=view_create";
    openwin_help(helplocation);
}

function reset() { theForm.reset(); }

function submit() {
    if (validateForm(theForm)) {
    	var flag = true;
    	
    	if(flag) {
	        theAction("submit");
	        theForm.submit();
        }
    }
}

function cancel() {
    theAction("cancel");
    theForm.submit();
}

function switchFilterTab(id) {
    if (validateForm(theForm)) {
        theForm.selectedFilterTabID.value = id;
        theAction("switchTab");
        theForm.submit();
    }
}

function switchSorterTab(id) {
    if (validateForm(theForm)) {
        theForm.selectedSorterTabID.value = id;
        theAction("switchTab");
        theForm.submit();
    }
}

 <%--
function selectColumn(elementName) {
    theForm.columnID.value = getSelectedValue(theForm.elements[elementName]);
    theAction("selectColumn");
    theForm.submit();
}

function deselectColumn(elementName) {
    theForm.columnID.value = getSelectedValue(theForm.elements[elementName]);
    theAction("deselectColumn");
    theForm.submit();
}

function moveUpColumn(elementName) {
    theForm.columnID.value = getSelectedValue(theForm.elements[elementName]);
    theAction("moveUpColumn");
    theForm.submit();
}

function moveDownColumn(elementName) {
    theForm.columnID.value = getSelectedValue(theForm.elements[elementName]);
    theAction("moveDownColumn");
    theForm.submit();
}
--%>
</script>
</head>


<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.portfolio.portfolio.objecttype.description" space="project">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:page display='<%=PropertyProvider.get("prm.global.view.edit.title")%>'
                          jspPage='<%=SessionManager.getJSPRootURL() + "/view/EditView.jsp?module=" + module%>' />
            </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL()%>/view/EditViewProcessing.jsp" method="post" onsubmit="return validateForm(theForm);">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="theAction" value="submit">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr align="left" class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    <td class="channelHeader" align="left" colspan="2">&nbsp;<display:get name="prm.global.view.edit.title" />&nbsp;&nbsp;<display:get name="prm.global.display.requiredfield" /></td>
    <td width="1%" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="fieldRequired" width="20%"><display:get name="prm.global.view.edit.label.name" /></td>
    <td class="tableContent">
        <input type="text" size="40" maxlength="80" name="name" value='<c:out value="${viewBuilder.name}" />'>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2"><display:get name="prm.global.view.edit.label.description" /></td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="tableContent" colspan="2">
        <textarea rows="5" cols="80" name="description"><c:out value="${viewBuilder.description}" /></textarea>
    </td>
    <td>&nbsp;</td>
</tr>
<%
    // Now draw the default view scenarios and a checkbox that is
    // checked if this view is the default view for the scenario
    for (Iterator it = viewBuilder.getDefaultViewSettings().iterator(); it.hasNext(); ) {
        DefaultViewSetting nextSetting = (DefaultViewSetting) it.next();

%>
<tr>
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2">
        <input type="checkbox" name="defaultForScenarioID" value="<%=nextSetting.getScenario().getID()%>" <%=nextSetting.isDefault(viewBuilder.getID()) ? "checked" : ""%>>
        &nbsp;
        <%=nextSetting.getScenario().getName()%></td>
    <td>&nbsp;</td>
</tr>
<%
    }
%>
<tr><td colspan="4">&nbsp;</td></tr>

<tr>
    <td>&nbsp;</td>
    <td colspan="2">

    <%-- Columns not supported yet
        <%-- Hidden field for storing id of column selected or deselected -- %>
        <input type="hidden" name="finderColumnID">
    --%>

        <table width="100%" border="0" cellpadding="0" cellspacing="0">
<%--
            <tr>
                <td class="tableContent">XX Columns</td>
            </tr>
            <tr>
            <%
                FinderIngredientHTMLProducer columnListProducer = new FinderIngredientHTMLProducer();
                viewBuilder.getFinderIngredients().getFinderColumnList().accept(columnListProducer);
            %>
                <td class="tableContent"><%=columnListProducer.getHTML()%></td>
            </tr>

            <tr><td>&nbsp;</td></tr>
--%>

<%-- Filters --%>
            <tr class="tableContent">
                <td colspan="3" class="tableContent">
                    <tab:tabStrip>
                    <%
                        // Figure out the currently selected tab
                        String selectedFilterTabID = request.getParameter("selectedFilterTabID");
                        if (selectedFilterTabID == null) {
                            selectedFilterTabID = "-1";
                        }
						%>
						<tab:tab label="Select Fields to Display"  href="javascript:switchFilterTab(-1);" selected="<%=String.valueOf(selectedFilterTabID.equals(String.valueOf(-1)))%>">
							<%
								if (viewBuilder instanceof PersonalPortfolioViewBuilder) {
									PersonalPortfolioViewBuilder personalPortfolioViewBuilder = (PersonalPortfolioViewBuilder) viewBuilder;
									out.println(personalPortfolioViewBuilder.getMetaColumnsHTML());
								}
							%>
							<input type="hidden" name="currentFilterPageID" value="-1">
						</tab:tab>
						<%
                        // Iterate over filter pages, putting each in a tab
                        int i = 0;
                        for (Iterator it = viewBuilder.getFilterPages().iterator(); it.hasNext(); i++ ) {
                            ViewBuilderFilterPage nextPage = (ViewBuilderFilterPage) it.next();
                    %>
                            <tab:tab label="<%=nextPage.getName()%>"  href='<%="javascript:switchFilterTab(" + i + ");"%>' selected="<%=String.valueOf(selectedFilterTabID.equals(String.valueOf(i)))%>">
                            <%
                                // Important to keep this inside the tab so that
                                // it is only executed if the tab is selected
                                FinderIngredientHTMLProducer filterListProducer = new FinderIngredientHTMLProducer();
                                nextPage.getFinderFilterList().accept(filterListProducer);
                            %>
                                <%=filterListProducer.getHTML()%>
                                <%-- Store the currently selected filter page for processing --%>
                                <input type="hidden" name="currentFilterPageID" value="<%=nextPage.getID()%>">
                            </tab:tab>
                    <%
                        }
                    %>
						<input type="hidden" name="selectedFilterTabID" value="<%=selectedFilterTabID%>">
                    </tab:tabStrip>
                </td>
            </tr>
            <tr><td colspan="3">&nbsp;</td></tr>

<%-- Groupers not supported yet
            <tr>
                <td class="tableContent">XX Groupers</td>
            </tr>
            <tr>
            <%
                FinderIngredientHTMLProducer groupingListProducer = new FinderIngredientHTMLProducer();
                viewBuilder.getFinderIngredients().getFinderGroupingList().accept(groupingListProducer);
            %>
                <td class="tableContent"><%=groupingListProducer.getHTML()%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
--%>

            <tr class="tableContent">
                <td colspan="3" class="tableContent">
					<%
					String selectedFilterTabID = request.getParameter("selectedFilterTabID");
					if (selectedFilterTabID == null || "-1".equals(selectedFilterTabID)) {
					%>
					<tab:tabStrip>
                    <%
                        // Figure out the currently selected tab
                        String selectedSorterTabID = request.getParameter("selectedSorterTabID");
                        if (selectedSorterTabID == null) {
                            selectedSorterTabID = "0";
                        }

                        // Iterate over sorter pages, putting each in a tab
                        int i = 0;
                        for (Iterator it = viewBuilder.getSorterPages().iterator(); it.hasNext(); i++ ) {
                            ViewBuilderSorterPage nextPage = (ViewBuilderSorterPage) it.next();
                    %>
                            <tab:tab label="<%=nextPage.getName()%>"  href='<%="javascript:switchSorterTab(" + i + ");"%>' selected="<%=String.valueOf(selectedSorterTabID.equals(String.valueOf(i)))%>">
                            <%
                                // Important to keep this inside the tab so that
                                // it is only executed if the tab is selected
                                FinderIngredientHTMLProducer sorterListProducer = new FinderIngredientHTMLProducer();
                                nextPage.getFinderSorterList().accept(sorterListProducer);
                            %>
                                <%=sorterListProducer.getHTML()%>
                                <%-- Store the currently selected sorter page for processing --%>
                                <input type="hidden" name="currentSorterPageID" value="<%=nextPage.getID()%>">
                            </tab:tab>
                    <%
                        }
                    %>
                        <input type="hidden" name="selectedSorterTabID" value="<%=selectedSorterTabID%>">
                    </tab:tabStrip>
					<%
					}
					%>
				</td>
            </tr>
        </table>

    </td>
    <td>&nbsp;</td>
</tr>
</table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit" />
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS space="project" />
</body>
</html>
