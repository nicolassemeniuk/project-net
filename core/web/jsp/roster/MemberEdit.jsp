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
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit Business Team Member" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.security.SessionManager,
		    net.project.base.Module,
		    net.project.resource.SpaceInvitationManager,
		    java.util.ArrayList,
		    java.util.Iterator,
		    net.project.directory.GroupWrapper,
		    net.project.security.User,
		    net.project.security.group.GroupCollection,
		    net.project.security.group.Group,
		    java.util.Arrays,java.util.List,
		    org.apache.commons.lang.StringUtils,
		    net.project.security.Action,
		    net.project.resource.PersonStatus,
		    org.apache.commons.collections.CollectionUtils, 
            net.project.chargecode.ChargeCodeManager,
            net.project.hibernate.service.ServiceFactory,
            net.project.space.SpaceTypes,
            net.project.hibernate.model.PnChargeCode"
%>

<jsp:useBean id="rosterPerson" class="net.project.resource.Person" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<%
	String id = request.getParameter("id");
	String memberid = request.getParameter("memberid");
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.DIRECTORY%>"
					   objectID="<%=id%>" /> 

<%
// We can not use standard security here because person id spans across all spaces.
// Therefore we will only grant edit permission on a member if it is that member or a space
// admin
if (memberid == null ||
	(!user.getID().equals(memberid) &&
     !securityProvider.isUserSpaceAdministrator()))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.security.validationfailed.message"));

	rosterPerson = roster.getPerson(memberid);
	session.setAttribute("rosterPerson",rosterPerson);
	String[] history = { rosterPerson.getDisplayName() };
	SpaceInvitationManager spaceInvitationWizard =(SpaceInvitationManager) session.getValue("spaceInvitationWizard");
	spaceInvitationWizard.setSpace(user.getCurrentSpace());
	spaceInvitationWizard.setUser(user);
	
	ArrayList<GroupWrapper> group = new ArrayList<GroupWrapper>();
	group = spaceInvitationWizard.getRoleOptionLists(spaceInvitationWizard.getAssignedRoles());

     GroupCollection groupList = new GroupCollection();
	 groupList.setSpace(user.getCurrentSpace());
	 groupList.loadAll(memberid);
	 Iterator iterator = groupList.iterator();
	 String existingGroups = ""; 
	 List<String> existingGroupList = new ArrayList<String>();
	 while (iterator.hasNext()) {
		Group collection = (Group) iterator.next();
		existingGroups += collection.getID() + "," ;
		existingGroupList.add(collection.getID());
	}
	 
	String personalImagePath;
    if((user.getImageId() != 0 && user.getID().equals(memberid)) || (rosterPerson.getImageId() != 0)){
    	personalImagePath = SessionManager.getJSPRootURL()+"/servlet/photo?id="+ memberid +"&size=medium&module="+Module.DIRECTORY;
    } else{
    	personalImagePath = SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
    }	
    
	ChargeCodeManager chargeCodeManager = new ChargeCodeManager();	
	Integer spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
	String space;
	boolean root;
	String chargeCodeId = null;
	PnChargeCode chargeCode = ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeAppliedOnPersonInSpace(Integer.valueOf(rosterPerson.getID()), spaceId);
	if(chargeCode != null)
		chargeCodeId = chargeCode.getCodeId().toString();
	if(SessionManager.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.BUSINESS)){
		root = ServiceFactory.getInstance().getPnBusinessSpaceService().isRootBusines(spaceId);
		space = "business";
	} else {
		root = ServiceFactory.getInstance().getPnProjectSpaceService().isRootProject(spaceId);
		space = "project";
	}
%>
<title><display:get name="prm.global.application.title" /></title>
<template:import type="javascript" src="/src/skypeCheck.js" />
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
	var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  
	var userId = '<%=user.getID()%>';
	var selectedUserId='<%=rosterPerson.getID()%>';
	var moduleId=<%=Module.DIRECTORY%>;
	var confirmRemoveImageMessege = '<%=PropertyProvider.get("prm.personal.profile.confirmremoveimage.message").replaceAll("'", "`")%>';
	var errormessage = '<%=PropertyProvider.get("prm.directory.memberview.changepersonimage.errormessage").replaceAll("'", "`")%>';
	var removeImageerrormessage ='<%=PropertyProvider.get("prm.directory.memberview.removeimage.errormessage").replaceAll("'", "`")%>'; 
	var uploadFromPage = 'memberview';
	imageTypeNotSupportedMessage = '<%=PropertyProvider.get("prm.personal.personalimageupload.imagetypenotsupported.message").replaceAll("'", "`")%>';
	var memberId = '<%=memberid%>';
	var actionId = '<%=Action.MODIFY%>';
	function setup() {
		page = true;
		load_menu('<%=user.getCurrentSpace().getID()%>');
		theForm = self.document.forms[0];
		isLoaded = true;
		this.focus();
	}

function cancel() { 
//fix bfd-5164 Uros
//self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>"; }
self.document.location = JSPRootURL + "/roster/PersonalDirectoryView.htm?module=<%=Module.DIRECTORY%>&memberid="+<%= rosterPerson.getID() %>;
}

function reset() { theForm.reset(); }

function submitDetails() { 
	document.getElementById('existingRoles').value = '<%=existingGroups%>';	
	theForm.submit();
}
function help(){
	var helplocation = JSPRootURL + "/help/Help.jsp?page=directory_project&section=modify";
	openwin_help(helplocation);
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<% String showPopupUrl = "javascript:showUploadPopup('" + SessionManager.getJSPRootURL()+"/personal/UploadDocument',625,'null','memberview');"; %>

<tb:toolbar style="tooltitle" showAll="true" groupTitle='<%= PropertyProvider.get("prm.global.tool.directory.name")%>' subTitle="<%=rosterPerson.getFullName()%>" imagePath="<%=personalImagePath%>">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%= PropertyProvider.get("prm.directory.memberedit.history", history) %>'
					jspPage='<%=request.getRequestURI()%>'
					queryString='<%=request.getQueryString()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<% if(!rosterPerson.getStatus().equals(PersonStatus.UNREGISTERED)){ %>
			<tb:button type="custom" 
					label='<%= PropertyProvider.get("prm.directory.editproperties.viewpersonalprofile.link")%>' 
					function='<%=SessionManager.getJSPRootURL()+ "/personal/profile/" + memberid +"?module="+Module.PERSONAL_SPACE%>'
					imageEnabled='/images/icons/toolbar-myprofile-on.gif'
					imageOver = '/images/icons/toolbar-myprofile-over_.gif' />
		<% } %>
		<% if(user.getID().equals(memberid)){ %>
			<tb:button type="custom" 		
					label='<%= PropertyProvider.get("prm.directory.editproperties.changepicture.link")%>'
					function="<%=showPopupUrl%>"
					imageEnabled='/images/icons/toolbar-upload-image-on.gif'
					imageOver = '/images/icons/toolbar-rollover-upload-image.gif' />
    	<% } %>
    	<% if(user.getImageId() != 0 && user.getID().equals(memberid)){ %>
		<tb:button type="custom"
				label='<%= PropertyProvider.get("prm.directory.editproperties.removepicture.link")%>'
				function="javascript:confirmRemoveImage();"
				imageEnabled="/images/icons/toolbar-gen-remove_on.gif"
				imageOver="/images/icons/toolbar-rollover-remove.gif" />
		<%} %>
	</tb:band>
</tb:toolbar>
<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/roster/MemberEditProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>">
	<input type="hidden" name="memberid" value="<jsp:getProperty name="rosterPerson" property="ID" />">
	<input type="hidden" name="existingRoles" id="existingRoles">
			
<table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding-top:25px;">
	<tr>
		<th width=1% style="font-size:15;border-bottom:1px dotted #80c080; color:#1C68CB; font-weight:bold" colspan="2" ><display:get name="prm.directory.memberedit.chanelbartitle.label" /></th>
		<td width=1% align=right></td>
	</tr>
	<tr><td style="padding-left:10px;" width="100%">
	<table cellspacing="7">
	<tr>
	<td colspan="2" style="font-size:14px;"><%if(request.getAttribute("groupBean") != null){Group  bean = (Group)request.getAttribute("groupBean");%>
	<%=bean.getErrorMessage("member")%><%}%></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldNonRequired"><display:get name="prm.directory.memberedit.fullname.label" /></th>
		<td  nowrap align="left" class="tableContent"><c:out value="${rosterPerson.fullName}"/></td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired" ><display:get name="prm.directory.memberedit.membertitle.label" /></th>
		<td  align="left" class="tableContent">
			 <input type="text" name="title" value="<c:out value="${rosterPerson.title}"/>" size="60" maxlength="80">
		</td>
		
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.directory.memberedit.responsibilities.label" /></th>
		<td align="left" class="tableContent">
    	  <input type="text" name="responsibilities" value="<c:out value="${rosterPerson.responsibilities}"/>" size="60" maxlength="500">
		</td>
	</tr>
	<tr>
		<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.directory.memberedit.role.label" /></th>
		<td align="left" class="tableContent">
		 <input type="checkbox" name="teammemberrole" value="Team Member" checked="checked" disabled="disabled"><display:get name="prm.directory.memberedit.teammemberrole.label" />&nbsp;&nbsp;
		<%for(GroupWrapper wrapper : group){
			if(existingGroupList.contains(wrapper.getGroupId())){%>
	    	  <input type="checkbox" name="roles" value="<%=wrapper.getGroupId()%>" checked="checked"><%=wrapper.getGroupName()%>&nbsp;&nbsp;&nbsp;
    	 <%} else{%>
    	 	<input type="checkbox" name="roles" value="<%=wrapper.getGroupId()%>"><%=wrapper.getGroupName()%>&nbsp;&nbsp;&nbsp;
    	 <%}}%> 
		</td>
	</tr>

	   <% 
	   	if (PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
  			if(space.equals("business") && root)
  				chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByBusinessId(spaceId));
 			else if (space.equals("business"))
  				chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getRootBusinessChargeCodeBySubBusinessId(spaceId));
  			else 
  				chargeCodeManager.setChargeCodeList(ServiceFactory.getInstance().getPnChargeCodeService().getChargeCodeByProjectId(spaceId));
	 		if(chargeCodeManager.isChargeCodeAvailable()){
		%>
	  		<tr align="left" valign="middle">
			<th nowrap align="left" width="18%" class="fieldRequired"><display:get name="prm.business.chargecode.label" /></th>
			<td align="left" class="tableContent">
	                <select id="chargecode" name="chargecode">
	                    <%=chargeCodeManager.getChargeCodeHtml(chargeCodeId)%>
	                </select>
			</td>
		</tr>
        <% }}%>
	
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
				<%if(!SessionManager.getSiteScheme().toLowerCase().contains("https")){ %>
					<img src="http://mystatus.skype.com/smallicon/<%=rosterPerson.getSkype()%>" style="border: none;" width="16" height="16" alt="My status" />
				<%} else {%>	
					<img src="<%=SessionManager.getJSPRootURL()%>/images/skype/SkypeBlue_16X16.png" style="border: none;" width="16" height="16" alt="My status" />
				<%} %>
			</a>
		<%} %> <%=StringUtils.isNotEmpty(rosterPerson.getSkype()) ? rosterPerson.getSkype() : ""%>
	  	</td>
	</tr>
	</table></td></tr>
	<tr class="memberEditBottom" height="30">
		<td colspan="2" align="center">
		<button onclick="return submitDetails();"><%=PropertyProvider.get("prm.directory.memberedit.membereditsubmitbutton.caption")%></button>
	</tr>
</table>

</form>
</div>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/upload.js" />
</body>
</html>
