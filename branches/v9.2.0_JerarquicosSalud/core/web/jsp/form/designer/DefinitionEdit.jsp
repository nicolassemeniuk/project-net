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
    info="Form Designer -- Definition" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.Form,
			net.project.space.ISpaceTypes,
			net.project.space.SpaceURLFactory,
            net.project.security.SessionManager,
            net.project.util.HTMLUtils"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="featuredItemsAssociation" class="net.project.gui.navbar.FeaturedItemsAssociation"/>

<template:getDoctype />

<%@page import="net.project.space.SpaceTypes"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%
	String mySpace = user.getCurrentSpace().getType();
	String refererURL = null ;
	
	//System.out.println("Space ID"+user.getCurrentSpace().getID());
	
	// If the user is in their personal space, change context to Form Edit space
	if (mySpace == null || mySpace.equals(net.project.space.Space.APPLICATION_SPACE) && request.getParameter("spaceID")!= null) {
        String redirect = SpaceURLFactory.constructSpaceURLForMainPage(request.getParameter("spaceID")) +
                               "&page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString(), "utf-8");
        response.sendRedirect(redirect);
            
        refererURL = SessionManager.getJSPRootURL()+"/admin/form/FormsAdmin1.jsp?module="+net.project.base.Module.APPLICATION_SPACE+"&action="+net.project.security.Action.MODIFY;
        pageContext.setAttribute("refererURL",refererURL , PageContext.SESSION_SCOPE);	
        
        return;
    }
		
	// Set context for a new or existing Form
	//formDesigner.clear();
	formDesigner.setUser(user);
	formDesigner.setSpace(user.getCurrentSpace());
	
	refererURL = (String) pageContext.getAttribute("refererURL", PageContext.SESSION_SCOPE);
	
	//System.out.println("refererSpaceURL"+refererURL);

	String editMode = "create";
	// Set the context for the form, and load from persistence.
	if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
		formDesigner.setID(request.getParameter("id"));
		formDesigner.load();
		formDesigner.loadLists(true);
		formDesigner.loadWorkflows();
		editMode = "modify";

        featuredItemsAssociation.clear();
        featuredItemsAssociation.setObjectID(formDesigner.getID());
        featuredItemsAssociation.setSpaceID(user.getCurrentSpace().getID());
        featuredItemsAssociation.load();
	}
    
    String spaceTypeID = user.getCurrentSpace().getSpaceType().getID();
    boolean isSpecialFeaturesEnabled = (spaceTypeID.equals(ISpaceTypes.PROJECT_SPACE) || spaceTypeID.equals(ISpaceTypes.BUSINESS_SPACE) || SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.METHODOLOGY));
    boolean isExternalAccessAllowed = PropertyProvider.get("prm.externalformaccess.isenabled").equals("1") || PropertyProvider.get("prm.externalformaccess.isenabled").equals("true");
%>
 
<template:getSpaceCSS />
<template:import type="javascript" src="/src/standard_prototypes.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/document_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
    changeHideAssignmentControlStatus();
}

function cancel() { 
<%
	if(refererURL != null && !refererURL.trim().equals("")) {
%>
	self.document.location = '<%= refererURL %>';
<% 
	} else {
%>		
	self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>";
<%	
	}
%>	

}

function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_designer_definition";
    openwin_help(helplocation);
}

function reset()  {
    theForm.reset();
}

function validateForm (form) {
    if (!checkTextbox(form.name, '<%=PropertyProvider.get("prm.form.designer.definitionedit.namerequired.message")%>')) return false;
    if (!checkTextbox(form.abbreviation, '<%=PropertyProvider.get("prm.form.designer.definitionedit.abbrevrequired.message")%>')) return false;
	if (!checkMaxLength(theForm.description,500,'<%=PropertyProvider.get("prm.form.designer.definitionedit.descriptionsize.message")%>')) return false;
	if (!stripHTML(form.name,'<%=PropertyProvider.get("prm.form.designer.definitionedit.name.cannothtmltag.message")%>')) return false;
	if (!stripHTML(form.abbreviation,'<%=PropertyProvider.get("prm.form.designer.definitionedit.abbreviation.cannothtmltag.message")%>')) return false;
	if (!stripHTML(theForm.description,'<%=PropertyProvider.get("prm.form.designer.definitionedit.description.cannothtmltag.message")%>')) return false;
	
    return true;
}

function stripHTML( formId, errorMsg){
	var oldValue = formId.value;
	var auxValue = "";
	var re= /<\S[^><]*>/g
		for (i=0; i< formId.value.length; i++){
			auxValue = formId.value.replace(re, "")
		} 
	if (oldValue != auxValue) {
		extAlert(errorTitle, errorMsg , Ext.MessageBox.ERROR);
		return false;
	} else {
		return true;
	}
}

function trim( value ) {
	return value.replace(/^\s+|\s+$/g,"");
}

function submit() {

    if (validateForm (theForm)) {

	theAction("submit");
	theForm.submit();
    }
}

function tabClick(nextPage) {
    document.definition.nextPage.value = nextPage;
    document.definition.submitData.value = false;
	submit();
}

function changeHideAssignmentControlStatus(){
  if(document.getElementById("supportsExternalAccess") && document.getElementById("supportsFormAssignment")){	
	  var supportsExternalAccess = document.getElementById("supportsExternalAccess").checked;
	  var supportsFormAssignment = document.getElementById("supportsFormAssignment").checked;  
	  var enableAssignmentFields = supportsExternalAccess && supportsFormAssignment;
	  document.getElementById("showAssignmentFields").disabled = !enableAssignmentFields;
	  if (enableAssignmentFields) {
	  	document.getElementById('spanId').style.color = '#000000';
	  } else {
		 document.getElementById('spanId').style.color = '#d9d9d9';
	  }
  }   
}

</script>

</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=(editMode.equals("modify") ? formDesigner.getName() : PropertyProvider.get("prm.form.designer.definitionedit.newform.module.history"))%>'
						  jspPage='<%=SessionManager.getJSPRootURL()+ "/form/designer/DefinitionEdit.jsp"%>'
	  					  queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY + "&id=" + formDesigner.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" name="definition" action="<%=SessionManager.getJSPRootURL() + "/form/designer/DefinitionEditProcessing.jsp"%>">
<input type="hidden" name="theAction">
<input type="hidden" name="nextPage" value="">
<input type="hidden" name="submitData" value="true">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />
<input type="hidden" name="currentSharingStatus"  value="<jsp:getProperty name="formDesigner" property="shared"/>" />

<%-- Hardcoded to FORM type for now.  Must make this an option for supporting Property Sheets, Checklists, etc. later. --%>
<input type="hidden" name="classTypeID" value="<%= Form.FORM %>">

<%-- Header and Tab Bar --%>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
    <tr class="channelHeader">
        <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
        <td class="channelHeader"><%=PropertyProvider.get("prm.form.designer.main.pagetitle")%> <%=PropertyProvider.get("prm.global.display.requiredfield")%></td>
        <td class="channelHeader" align="right"><%= HTMLUtils.escape(formDesigner.getName()) %></td>
    	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
    </tr>
	<tr><td colspan="4">&nbsp;</td></tr>
</table>

<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.definitionedit.definition.tab")%>' href="javascript:tabClick('DefinitionEdit.jsp');" selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.fieldsmanager.fields.tab")%>' href="javascript:tabClick('FieldsManager.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.preview.preview.tab")%>' href="javascript:tabClick('Preview.jsp');" />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.listsmanager.lists.tab")%>' href="javascript:tabClick('ListsManager.jsp');" />
<%-- Disabled for now since they are not implemented yet
	<tab:tab label="Search" href="javascript:tabClick('SearchEdit.jsp');" />
	<tab:tab label="Links" href="javascript:tabClick('LinksManager.jsp');" />
	<tab:tab label="Documentation" href="javascript:tabClick('DocumentationManager.jsp');" />
--%>
	<% if(!user.getCurrentSpace().getType().equals(net.project.space.Space.PERSONAL_SPACE)){%>
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.workflowselect.workflows.tab")%>' href="javascript:tabClick('WorkflowSelect.jsp');" />
    <%} %>
    <display:if name="prm.global.form.sharing.isenabled">
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.formsharing.sharing.tab")%>' href="javascript:tabClick('FormSharing.jsp');" />
    </display:if>
	
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.activateedit.activate.tab")%>' href="javascript:tabClick('ActivateEdit.jsp');" />
</tab:tabStrip>


<%-- Definition Form --%>	
<table border="0" width="100%" border="0" cellspacing="2" cellpadding="0">
          <tr> <td colspan="3">&nbsp;</td></tr>

<%-- Form Copy not implemented yet. 
          <tr valign="middle"> 
            <td nowrap colspan="3" class="tableHeader">Copy from Existing Form?:
              &nbsp;&nbsp; 
              <select name="CopyFromFormID">
                <option value="">None</option>
                <jsp:getProperty name="formMenu" property="formsOptionList" /> 
              </select>
              &nbsp;<a href="javascript:copy();">Copy</a> </td>
          </tr>
--%>
          <tr> <td colspan="3">&nbsp;</td></tr>
          <tr>
            <td nowrap align="left" class="fieldRequired" width="125"><%=PropertyProvider.get("prm.form.designer.definitionedit.formname.label")%></td>
            <td nowrap align="left" colspan="2"><input type="text" name="name" size="30" maxlength="80" value="<%=HTMLUtils.formatHtml(formDesigner.getName())%>"></td>
          </tr>
          <tr>
            <td nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.formabbrev.label")%></td>
            <td nowrap align="left" colspan="2"><input type="text" name="abbreviation" size="4" maxlength="10" value="<%=HTMLUtils.formatHtml(formDesigner.getAbbreviation())%>"></td>
          </tr>
          <tr>
            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.displaytools.label")%></td>
            <td nowrap align="left" colspan="2"><input type="checkbox" name="displayedInToolsMenu" value="true" <% if (featuredItemsAssociation.getDisplayInToolsMenu()) out.print(" CHECKED ");%> /></td>
          </tr>
          <tr>
            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.includediscussions.label")%></td>
            <td nowrap align="left" colspan="2"><input type="checkbox" name="supportsDiscussionGroup" value="true" <% if (formDesigner.getSupportsDiscussionGroup()) out.print(" CHECKED ");%>/></td>
          </tr>
<%
		if(isSpecialFeaturesEnabled) {
%>		  
          <tr>
            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.includedocuments.label")%></td>
            <td nowrap align="left" colspan="2"><input type="checkbox" name="supportsDocumentVault" value="true" <% if (formDesigner.getSupportsDocumentVault()) out.print(" CHECKED ");%>/></td>
          </tr>
          
          <tr>
              <td align="left" width="1%">
              	 <table cellpadding="0" cellspacing="0">
              	   <% if(mySpace != null && !mySpace.equals(net.project.space.Space.BUSINESS_SPACE)) {%> 
			          <tr>
			            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.includeassignments.label")%></td>
			          </tr>
			        <% } %>  
			       <%if(isExternalAccessAllowed) {	%>                 	 	
			          <tr>
			            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.externalaccess.label")%></td>
			          </tr>
			       <% } %>   
			          <tr>
			          	 <td >&nbsp;</td>
			          </tr>              	 	
              	 </table>	
              </td>
              <td align="left" width="1%">
              	 <table cellpadding="2" cellspacing="0">
              	 	<% if(mySpace != null && !mySpace.equals(net.project.space.Space.BUSINESS_SPACE)) {%>
			          <tr>
			            <td nowrap align="left"><input type="checkbox" id="supportsFormAssignment" name="supportsFormAssignment" onclick="changeHideAssignmentControlStatus();" value="true" <% if (formDesigner.getSupportsAssignment()) out.print(" CHECKED ");%>/></td>
			            <td>&nbsp;</td>
			          </tr>
			        <% } %>  
			        <%if(isExternalAccessAllowed) {	%>                	 	
			          <tr>
			            <td nowrap align="left"><input type="checkbox"  id="supportsExternalAccess" name="supportsExternalAccess" onclick="changeHideAssignmentControlStatus();" value="true" <% if (formDesigner.getSupportsExternalAccess()) out.print(" CHECKED ");%>/></td>
			          </tr>
			        <% } %>  
			          <tr>
			          	 <td>&nbsp;</td>
			          </tr>              	 	
              	 </table>	
              </td>              
              <td align="left" valign="bottom" >
              	 <table>
              	     <%if(isExternalAccessAllowed && mySpace != null && !mySpace.equals(net.project.space.Space.BUSINESS_SPACE)) {	%>
			          <tr >
			          	<td valign="top"  style="padding-bottom: 5px;"><img  src="<%=SessionManager.getJSPRootURL()%>/images/default/form/connector.jpg" width="40" height="40" alt="" border="0"/></td>			            
			            <td valign="bottom"  nowrap align="left" class="fieldNonRequired" >&nbsp;&nbsp;<span id="spanId"> <%=PropertyProvider.get("prm.form.designer.definitionedit.showassignmentfield.label")%> </span>  </td>
	    		        <td valign="bottom"  nowrap align="left"><input type="checkbox" id="showAssignmentFields" name="showAssignmentFields" value="true" <% if (!formDesigner.isAssignmentFieldHiddenInEaf()) out.print(" CHECKED ");%>/></td>
			          </tr>              	 	          
			          <% } else { %>
			          	<tr>
			          	  <td>&nbsp;
			          	  </td>
			          	</tr>
			          <% } %>    	 	
              	 </table>              		
              </td>              
          </tr>
          

<%
			if(isExternalAccessAllowed) {

				if(formDesigner.getSupportsExternalAccess() && formDesigner.getExternalClassId() != null ) {					
%>	               
			          <tr>
			            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.externalurl.label")%></td>
			            <td nowrap align="left" colspan="2"> <%= SessionManager.getAppURL()+ "/eaf?extid=" + formDesigner.getExternalClassId()+ "&extSid=" +  formDesigner.getOwningSpaceID() %></td>
			          </tr>	               	                    
<%
				}
			}
		}
%>
	
	<% if(mySpace != null && mySpace.equals(net.project.space.Space.BUSINESS_SPACE)) {%>
		   <tr>
            <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.definitionedit.shared.label")%></td>
            <td nowrap align="left" colspan="2"><input type="checkbox" name="shared" value="true" <% if (formDesigner.isShared()) out.print(" CHECKED ");%> /></td>
          </tr>
	<%} %>
	
		<tr> 
			<td nowrap align="left" class="fieldNonRequired">
				<%=PropertyProvider.get("prm.form.designer.activateedit.currentstatus.label")%>
			</td>
	        <td nowrap align="left" colspan="2">
				<jsp:getProperty name="formDesigner" property="statusName"/>
	        </td>
		</tr>
	
          <tr><td nowrap align="left" colspan="3">&nbsp; </td></tr>
          <tr>
            <td nowrap colspan="3" align="left" class="fieldNonRequired">
                <%=PropertyProvider.get("prm.form.designer.definitionedit.formdescription.label")%>
                <br>
                <textarea name="description" cols="80" rows="3" wrap><c:out value="${formDesigner.description}"/></textarea>
            </td>
          </tr>
          <tr><td nowrap align="left" colspan="3">&nbsp; </td></tr>
        </table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />
<br clear=all>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
