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
|   $Revision: 20856 $
|       $Date: 2010-05-14 12:58:53 -0300 (vie, 14 may 2010) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Team Member Details" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.Action,
            net.project.security.SessionManager,
			net.project.space.SpaceURLFactory,
			net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.group.GroupCollection,
            java.util.Iterator,
            net.project.security.group.Group,
            net.project.security.group.GroupTypeID,
            org.apache.commons.lang.StringUtils,
            net.project.resource.PersonStatus, 
            net.project.chargecode.ChargeCodeManager,
            net.project.hibernate.service.ServiceFactory,
            net.project.space.SpaceTypes,
            net.project.hibernate.model.PnChargeCode"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<jsp:useBean id="memberOfRoles" class="net.project.security.group.GroupCollection" scope="page" />

<template:getDoctype />
<html>
<head>

<%
	String id = request.getParameter("id");
	String memberid = request.getParameter("memberid");
	if (id == null){
		id = (String)request.getAttribute("id");
	}
	net.project.resource.Person rosterPerson = (net.project.resource.Person)request.getAttribute("rosterPerson");
	String referrer = request.getParameter("referrer");
	
	if ( referrer != null && referrer.equals("dashboard")){
		session.setAttribute("pnet_refLink", SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace())) ;
	}
	String[] history = { rosterPerson.getDisplayName() };
	
	 GroupCollection groupList = new GroupCollection();
	 groupList.setSpace(user.getCurrentSpace());
	 groupList.loadAll(memberid);
	 Iterator iterator = groupList.iterator();
	 String userGroups = ""; 
	 while (iterator.hasNext()) {
		Group collection = (Group) iterator.next();
		if(!collection.getGroupTypeID().equals(GroupTypeID.PRINCIPAL) && !userGroups.contains(collection.getName())){
			userGroups += collection.getName() + ", ";
		}
	}
	// to remove last comma from role string. 
	userGroups = StringUtils.isNotEmpty(userGroups) ? userGroups.substring(0, userGroups.length()-2) : ""; 
	
	String personalImagePath;
    if(rosterPerson.getImageId() != 0){
    	personalImagePath = SessionManager.getJSPRootURL()+"/servlet/photo?id="+ memberid +"&size=medium&module="+Module.DIRECTORY;
    } else {
    	personalImagePath = SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
    } 
%>
<!-- security:verifyAccess action="view"  module="<%=net.project.base.Module.DIRECTORY%>" objectID="<%=id%>" /--> 

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script type='text/javascript' src='../dwr/engine.js'></script>
<script type='text/javascript' src='../dwr/interface/LogoRemover.js'></script>
<script type='text/javascript' src='../dwr/util.js'></script>
<template:import type="javascript" src="/src/upload.js" />
<template:import type="javascript" src="/src/skypeCheck.js" />
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var userId = '<%=user.getID()%>';
	var selectedUserId='<%=rosterPerson.getID()%>';
	var moduleId=<%=Module.DIRECTORY%>;
	var confirmRemoveImageMessege = '<display:get name="prm.directory.memberview.userremovalwarning.message" />';
	var errormessage='<display:get name="prm.directory.memberview.changepersonimage.errormessage" />';
	var removeImageerrormessage = '<display:get name="prm.directory.memberview.removeimage.errormessage" />';
	var uploadFromPage = 'memberview';

function setup() {
	page = true;
	theForm = self.document.forms[0];
	isLoaded = true;
	this.focus();
}

function cancel() { self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>"; }
function reset() { self.document.location = JSPRootURL + "/roster/PersonalDirectoryView.htm?module=<%=Module.DIRECTORY%>&memberid="+<%= rosterPerson.getID() %>; }
function modify() { self.document.location = JSPRootURL + "/roster/MemberEdit.jsp?module=<%=Module.DIRECTORY%>&action=<%=Action.MODIFY%>&memberid="+<%= rosterPerson.getID() %>; }

function help(){
	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_project&section=view";
	openwin_help(helplocation);
}

</script>
<style title="">
.profile-photo {
	margin-top:25px;
}
.project-title {
	margin-top:-12px;
}	
</style>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.directory.name" subTitle="<%=rosterPerson.getFullName()%>" imagePath="<%=personalImagePath %>">
	<tb:setAttribute name="leftTitle"> 
		<history:history>
			<history:page display='<%= PropertyProvider.get("prm.directory.memberview.history", history) %>'
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<%if(!rosterPerson.getStatus().equals(PersonStatus.UNREGISTERED)){ %>
			<tb:button type="modify" label='<%= PropertyProvider.get("prm.directory.directory.modify.button.tooltip") %>' />
			<tb:button type="custom" 
					label='<%= PropertyProvider.get("prm.directory.editproperties.viewpersonalprofile.link")%>' 
					function='<%=SessionManager.getJSPRootURL()+ "/personal/profile/" + memberid +"?module="+Module.PERSONAL_SPACE%>'
					imageEnabled='/images/icons/toolbar-myprofile-on.gif'
					imageOver = '/images/icons/toolbar-myprofile-over_.gif' />
		<%}%>					
	</tb:band>
</tb:toolbar>
</div>
<div id='content'>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
</table>
<div>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/roster/PersonalImageUpload.htm">

	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
    <input type="hidden" name="action" value="<%=Action.DELETE%>">
    <input type="hidden" name="selected" value="<c:out value="${rosterPerson.ID}"/>">
<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding-top:25px;">
	<tr>
		<th width=1% style="font-size:15;border-bottom:1px dotted #80c080; color:#1C68CB; font-weight:bold" colspan="2" ><display:get name="prm.directory.memberview.viewmemberproperty.link" /></th>
		<td width=1% align=right></td>
	</tr>
	<tr><td style="padding-left:10px;" width="100%">
    <table cellspacing="7">
    <tr>
    	<th nowrap align="left" width="18%" class="fieldNonRequired"><display:get name="prm.directory.memberview.fullname.label" /></th>
    	<td nowrap align="left" class="tableContent" colspan="3"><c:out value="${rosterPerson.fullName}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.directory.memberview.title.label" /></th>
	    <td align="left" class="tableContent" colspan="3"><c:out value="${rosterPerson.title}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.directory.memberview.responsibilities.label" /></th>
    	<td align="left" class="tableContent" colspan="3"><c:out value="${rosterPerson.responsibilities}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.directory.memberedit.role.label" /></th>
    	<td align="left" class="tableContent" colspan="3"><%=userGroups%></td>
	</tr>
	
	<% if (PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
		ChargeCodeManager chargeCodeManager = new ChargeCodeManager();	
		Integer spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
		String space;
		boolean root;
		String chargeCodeId = null;
		PnChargeCode chargeCode = ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeAppliedOnPersonInSpace(Integer.valueOf(rosterPerson.getID()), spaceId);
		if(SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.BUSINESS)){
			root = ServiceFactory.getInstance().getPnBusinessSpaceService().isRootBusines(spaceId);
			space = "business";
		} else {
			root = ServiceFactory.getInstance().getPnProjectSpaceService().isRootProject(spaceId);
			space = "project";
		}
		if(space.equals("business") && root)
			chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByBusinessId(spaceId));
		else if (space.equals("business"))
			chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getRootBusinessChargeCodeBySubBusinessId(spaceId));
		else if (space.equals("project") && root)
			chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByProjectId(spaceId));
		else
			chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByProjectId(spaceId));
	 	if(chargeCodeManager.isChargeCodeAvailable()){
	%>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.business.chargecode.label" /></th>
    	<td align="left" class="tableContent" colspan="3">
		 <% if(chargeCode != null){ %>	
    		<%=chargeCode.getCodeName()%>
		 <% } else {%>
		 	<display:get name="prm.business.chargecode.dropdownlist.defaultoption.label" />
		<%}%>
    	</td>
	</tr>
	<%}}%>
	<tr>
		<td class="tableContent">&nbsp;</td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldNonRequired" ><display:get name="prm.directory.memberedit.officephone.label" /></th>
		<td width="36%" class="tableContent"><c:out value="${rosterPerson.officePhone}"/></td>
	</tr>
	<tr>
		<th width="18%" nowrap align="left" class="fieldNonRequired" ><display:get name="prm.directory.memberedit.mobilephone.label" /></th>
		<td width="36%" align="left" class="tableContent" ><c:out value="${rosterPerson.mobilePhone}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldNonRequired" ><display:get name="prm.directory.memberedit.pagerphone.label" /></th>
		<td width="36%" class="tableContent" ><c:out value="${rosterPerson.pagerPhone}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldNonRequired"><display:get name="prm.directory.memberedit.fax.label" /></th>
		<td width="36%" class="tableContent"><c:out value="${rosterPerson.faxPhone}"/></td>
	</tr>
	<tr>
		<th width="18%" nowrap align="left" class="fieldNonRequired"><display:get name="prm.directory.memberedit.emailaddress.label" /></th>
		<td width="33%" align="left" class="tableContent">
			<a href="mailto:<c:out value="${rosterPerson.email}"/>" >
			<c:out value="${rosterPerson.email}"/></a>
	  	</td>
	</tr>
	<tr>
		<th width="18%" nowrap align="left" class="fieldNonRequired"><display:get name="prm.directory.memberview.skype.label" /></th>
		<td width="33%" align="left" class="tableContent">
		<% if(PropertyProvider.getBoolean("prm.global.skype.isenabled")) { %>
			<a href="skype:<%=rosterPerson.getSkype()%>?chat">
				<% if(!SessionManager.getSiteScheme().toLowerCase().contains("https")){ %>
					<img src="http://mystatus.skype.com/smallicon/<%=rosterPerson.getSkype()%>" style="border: none;" width="16" height="16" alt="My status" />
				<%} else {%>	
					<img src="<%=SessionManager.getJSPRootURL()%>/images/skype/SkypeBlue_16X16.png" style="border: none;" width="16" height="16" alt="My status" />
				<%} %>
			</a>
		<%} %> <%=StringUtils.isNotEmpty(rosterPerson.getSkype()) ? rosterPerson.getSkype() : ""%>
	  	</td>
	</tr>
    </table>
    </td>
  </table>
</form>
</div>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>
</html>
