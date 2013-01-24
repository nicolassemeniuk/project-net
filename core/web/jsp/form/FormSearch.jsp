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
    info="Form Search" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,
			net.project.security.*,
			java.util.Iterator,
            java.util.ArrayList"
%>
<html>
<head>
<title><%=PropertyProvider.get("prm.form.searchpage.title")%></title>
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	Iterator fieldIterator = null;
	FormField field = null;
	FormList list = null;
%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:getSpaceCSS />

<security:verifyAccess objectID='<%=form.getID()%>'
					   action="view"
					   module="<%=net.project.base.Module.FORM%>"/>
					    

<template:import type="javascript" src="/src/standard_prototypes.js"/>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_search';
	openwin_help(helplocation);
}

function reset()  { theForm.reset(); }

function cancel() {
	document.location = "FormList.jsp?id=<%=form.getID()%>&module=<%=Integer.toString(net.project.base.Module.FORM)%>&action=<%=net.project.security.Action.VIEW%>"
}

function submit() {
    if (!checkIsPositiveNumber(theForm.filter__id, "<display:get name="prm.form.list.id.validation.numeric" />", true)) return;
	theAction("submit");
	theForm.submit();
}
</script>
</head>

<body bgcolor="#FFFFFF" onLoad="setup();"  id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%---------------------------------------------------------------------------
  --  Toolbar                                                                
  -------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:display />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>
<form method="post" action="FormSearchProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="form" property="ID"/>" />
<input type="hidden" name="class_id" value="<%=form.getID()%>">

<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
		<td colspan="2" align="left">
			<span class=pageTitle>
		  	<%=PropertyProvider.get("prm.form.search.pagetitle")%>
		  	</span>
		</td>
</tr>
<tr>	
		<td align="left" nowrap class="tableContent" >
			<%=PropertyProvider.get("prm.form.list.pagetitle", new Object [] {form.getName(), form.getAbbreviation()})%>
		</td>
</tr>
	<tr><td>&nbsp;</td></tr>


<%---------------------------------------------------------------------------
  --  Filter Options                                                         
  -------------------------------------------------------------------------%>
	<%
	// Create a new list titled "Custom Search Results" so we don't mess with the current display list.
	// We save the list in the form so it shows up on the list view choice list. 
	// list_id=1 is used for the search results.
	
	// Thought of , not much necessity , of removing any list with ID of 1 which was done earlier  
	// Also , made the cloned list have the ID of "cloned_list" instead of 1 which again was done
	// earlier  
	//--- deepak
	//form.removeList("1");
	
	
	list = (FormList) form.getDisplayList().clone();
	
	if(list != null) {
		list.setID("cloned_list_"+form.getListSize());
		list.setName("Custom Search Results "+form.getListSize());
		list.setDescription(null);
		list.setIsLoaded(true);
		form.addList(list);
		list.clearFilters();

		// For each field in the form, add a filter
		ArrayList fields = new ArrayList();
		fields.addAll(form.getFields());
		FormField idField = new FormID();
		// render #id as first field on search page
		%>
				<tr class="tableContent"> 
					<% idField.writeFilterHtml(list.getFieldFilter(idField), new java.io.PrintWriter(out)); %>
				</tr>
		<%
		fieldIterator = fields.iterator();
		while (fieldIterator.hasNext()) {
			field = (FormField) fieldIterator.next();
			if (field.isSearchable() && !(field instanceof FormID) ) {
		%>
				<tr class="tableContent"> 
					<% field.writeFilterHtml(list.getFieldFilter(field), new java.io.PrintWriter(out)); %>
				</tr>
		<%
			}
		}
	}
	%>

</table>

<%---------------------------------------------------------------------------
  --  Action Bar                                                             
  -------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit"/>
	</tb:band>
</tb:toolbar>

</form>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
