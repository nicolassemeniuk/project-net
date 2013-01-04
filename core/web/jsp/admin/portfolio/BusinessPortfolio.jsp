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
    info="Application Space Business Portfolio"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.BusinessPortfolioBean,
            net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.User,net.project.business.BusinessSpace,
            net.project.security.SessionManager,
            net.project.space.Space,net.project.security.*,net.project.business.BusinessDeleteWizard,
            net.project.space.PersonalSpaceBean, net.project.search.SearchManager,
            net.project.util.JSPUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="businessPortfolio" class="net.project.portfolio.BusinessPortfolioBean" scope="request" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="businessDeleteWizard" class="net.project.business.BusinessDeleteWizard" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<template:getDoctype />
<html>
<head>
<%
    ///-----Validate security----------------------------------------

	String modifiedCurrentSpaceType = JSPUtils.getModifiedCurrentSpaceType(SessionManager.getUser().getCurrentSpace());
	String channelIconString = null;
	String mode = request.getParameter ("mode");

	String status = "Active";
	String filter = request.getParameter("filter");
	
		
	//default to the APPLICATION_SPACE
    String currentModule = (request.getAttribute("module") == null ?
                           String.valueOf(Module.APPLICATION_SPACE) :
                           (String)request.getAttribute("module"));

	//Get the current module from the command-line, if it is available, otherwise

	if(request.getParameter("status")!= null)
		status = request.getParameter("status");
	
	if(status == null || status.equals("Active")) {
		businessPortfolio.setRecordStatusForPortfolio("A");
		channelIconString="Disable";
	} else if(status.equals("Disabled")){
		businessPortfolio.setRecordStatusForPortfolio("D");
		channelIconString = "Activate";
	}
    //SessionManager.getJSPRootURL() + "/admin/Main.jsp?module=" + currentModule);

    //Load all businesses into the businessPortfolio object.  As we are going to

	 if ( mode == null || mode.equals("initial")) {
	 	 businessPortfolio.clear();
	 } else if (mode != null && mode.equals ("search")) {
	 	 //businessPortfolio.clear();
		 //businessPortfolio.setRecordStatusForPortfolio("D");
    	 businessPortfolio.loadFiltered(request.getParameter ("filter"));
     } else {
  		 //projectPortfolio.clear();
		 //projectPortfolio.setRecordStatusForPortfolio("D");
    	 businessPortfolio.loadAll();

     }
    // Setup portfolio

    // Now set up the search object based on this portfolio
    search.clear();
    BusinessSpace entry = null;
    java.util.Iterator it = businessPortfolio.iterator();
    while (it.hasNext())
    {
        entry = (BusinessSpace) it.next();
        search.addSearchSpace(entry.getID(), entry.getName(), entry.getDescription());
    }//be showing all records, we have no filtering properties to set.

%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<script language="javascript">
    var theForm;
    var errorMsg;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var Module = '<%= Module.APPLICATION_SPACE %>';

	if (document.layers)
	document.captureEvents(Event.KEYPRESS);
	window.onkeypress = keyhandler;

	function setup() {
    	load_menu('<%=user.getCurrentSpace().getID()%>');
    	theForm = self.document.forms['mainForm'];
    	isLoaded = true;
	}

	function changeSpace(str) {
		//var str=theForm.type.value;
		if(str.match('Business')){
			self.document.location = JSPRootURL + '/admin/workspace/'+'<%="WorkSpace.jsp?spaceType=Business&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&mode="+ mode +"&status="+status +"&filter="+filter%>' ;
		}
		else
			self.document.location = JSPRootURL + '/admin/workspace/'+'<%="WorkSpace.jsp?spaceType=Project&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&mode="+ mode +"&status="+status+"&filter="+filter %>' ;
	}

	
	function changeStatus(str) {
		self.document.location = JSPRootURL + '/admin/workspace/'+'<%="WorkSpace.jsp?spaceType=Business&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY +"&mode=search&filter="+filter %>'+'&status='+str ;
	}

	function search(filter) {
		if (!filter) {
				Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.businessportfolio.showallbusiness.confirm")%>', function(btn) { 
					if(btn == 'yes'){ 
						self.document.location = JSPRootURL + '/admin/workspace/'+'<%="WorkSpace.jsp?spaceType=Business&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY +"&mode=search&status="+status +"&mode=search" %>'+'&filter='+filter ;		
					}else {
						return false;
					}
				});
		
		} else {
			self.document.location = JSPRootURL + '/admin/workspace/'+'<%="WorkSpace.jsp?spaceType=Business&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY +"&mode=search&status="+status +"&mode=search" %>'+'&filter='+filter ;
    	}
	}


	function cancel() {
    	var theLocation = JSPRootURL + "/personal/Main.jsp";
    	self.document.location = theLocation;
	}

	function reset() {
        search(theForm.key.value);
	}

	function help() {
    	var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_workspace";
    	openwin_help(helplocation);
	}

	function action() {
  		var status='<%=status%>';
		var echoString='';
		if(status.match('Disabled')){
			echoString="Are you sure you want to activate this Business ?";
			if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			if (confirm (echoString)) {
				theForm.theAction.value="active";
	    		theForm.action.value ='<%=Action.MODIFY%>';
				theForm.businessID.value =getSelection(theForm);
	    		theForm.submit();
			}
		}
		}
		if(status.match('Active')){
			echoString="Are you sure you want to disable this Business ?";
			if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
				if (confirm (echoString)) {
					theForm.theAction.value="remove";
	    			theForm.action.value ='<%=Action.MODIFY%>';
					theForm.businessID.value =getSelection(theForm);
					var m_url = JSPRootURL + "/admin/workspace/BusinessDelete.jsp?selected="+ getSelection(theForm) +'<%= "&module=" + net.project.base.Module.APPLICATION_SPACE + "&action=" + net.project.security.Action.MODIFY + "&status=Active" %>' ;
		  			var link_win = openwin_linker(m_url);
		  			link_win.focus();
	    		//theForm.submit();
				}
			}
		}

	}

	function keyhandler(e) {
   		var event = e ? e : window.event;
   		if (event.keyCode == 13){
			search(theForm.key.value);
			event.keyCode=0;
			return false;
		}
	}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<%----- Toolbar definition ------------------------------------------
  The "Project List" and "Business List" administration page include
  links to areas outside of the application space that tend to break
  the history list.  As a workaround, both the business list and the
  project list redefine the business.  The clears out the erroneous
  history created when you navigate to individual project and use the
  back button.
  -------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" leftTitle="Business Portfolio" groupTitle="prm.application.nav.workspace">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Workspaces"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/workspace/WorkSpace.jsp"%>'
                            queryString='<%="module="+currentModule+"&action="+Action.MODIFY%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
		<%--
		<tb:button type="remove" label="Disable Business"/>
		--%>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form action="<%=SessionManager.getJSPRootURL()%>/admin/portfolio/BusinessPortfolioProcessing.jsp" name="mainForm" method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%= currentModule %>">
	<input type="hidden" name="action">
	<input type="hidden" name="businessID">

<table border="0" cellspacing="0" cellpadding="1" width="100%">
	<tr>
    <td colspan="5">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
	    <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
		<td colspan="5" class="channelHeader"><%=PropertyProvider.get("project.admin.portfolio.business.label")%></td>
		<td align="right" class="channelHeader"><span class="channelHeader"><nobr>&nbsp;<A href="javascript:action();" class="channelNoUnderline"><%=channelIconString%><IMG alt="Disable" width="20" height="15" border="0" hspace="0" vspace="0" align="top" src="<%=SessionManager.getJSPRootURL()%>/images/<%=modifiedCurrentSpaceType %>/channelbar-edit.gif" name="imgmodify"></A>&nbsp;</nobr></span>&nbsp;</td>
		<td align="right" class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0 hspace=0 vspace=0></td>
    </tr>
    </table>
    </td>
    </tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
  <td colspan="3">&nbsp;&nbsp;
  <span class=tableContent><search:letter /></span>
	&nbsp;&nbsp;
  </td>
  <td class="tableHeader">&nbsp;&nbsp;<b><%=PropertyProvider.get("prm.project.admin.domain.keyword.label")%></b>
    <input type="text" onkeyPress="keyhandler(event)" name="key" size="15" maxlength="40" value="<%=filter%>">
	<a href="javascript:search(self.document.forms['mainForm'].key.value);"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"></a>
	&nbsp;&nbsp;
  </td>
</tr>
<tr>
  <td colspan="4" class="tableContent"><i>
  <%=PropertyProvider.get("prm.project.admin.portfolio.letter.workspace.message")%></i></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
 <td class="tableHeader" nowrap>
    	&nbsp;&nbsp;<%=PropertyProvider.get("prm.project.admin.portfolio.workspace.label")%> &nbsp;&nbsp;</br>
    	<select name="spaceType" height="2" onChange='changeSpace(this.options[this.selectedIndex].value);'>
    	<option SELECTED value='Business'><%=PropertyProvider.get("prm.project.admin.portfolio.business.label")%>
    	<option value='Project'><%=PropertyProvider.get("prm.project.admin.portfolio.project.label")%>
    	</select>
	</td>
	<td class="tableHeader" nowrap>
    	&nbsp;&nbsp; <%=PropertyProvider.get("prm.project.admin.portfolio.status.label")%> &nbsp;&nbsp;</br>
    	<select name="status" height="2" onChange='changeStatus(this.options[this.selectedIndex].value);'>
    	<option <%= status != null && status.equals("Active") ? "SELECTED" :"" %> value='Active'><%=PropertyProvider.get("prm.project.admin.portfolio.active.label")%>
    	<option <%= status != null && status.equals("Disabled") ? "SELECTED" :"" %> value='Disabled'><%=PropertyProvider.get("prm.project.admin.portfolio.disabled.label")%>
    	</select>
	</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td>&nbsp;</td></tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr>
    <td>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr class="channelHeader">
			<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
			<td nowrap class="channelHeader" align="left" width="85%"><%=PropertyProvider.get("prm.project.admin.portfolio.filter.results.label")%> <%= businessPortfolio.size() %></td>
			<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
		</tr>
    	<tr class="channelHeader">
          	<td class="channelContent">&nbsp;</td>
            <td colspan="2" align="center">
			 	<jsp:setProperty name="businessPortfolio" property="stylesheet" value="/admin/portfolio/xsl/business-portfolio.xsl" />
        	 	<jsp:getProperty name="businessPortfolio" property="presentation" />
             </td>
     	</tr>
     </table>

    </td>
  </tr>
</table>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="application"/>
<template:getSpaceJS space="application" />
</body>
</html>
