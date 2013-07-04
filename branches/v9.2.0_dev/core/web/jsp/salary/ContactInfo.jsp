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

<%@ page contentType="text/html; charset=UTF-8" info="Contact Info" language="java" errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider,
            net.project.util.NumberFormat,
            net.project.gui.toolbar.Button,
			net.project.gui.toolbar.ButtonType,
            net.project.base.Module,
			net.project.hibernate.service.ServiceFactory,
			net.project.admin.RegistrationBean,
			net.project.persistence.PersistenceException,
			org.apache.commons.lang.StringUtils,
			net.project.util.DateFormat,
			net.project.util.Version"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="ownerUser" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="registrationBean" class="net.project.admin.RegistrationBean" scope="session" />

<un:useConstants var="Module" className="net.project.base.Module" /> 

<%
	String versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());

	// No security validation necessary since a user can only access their own Profile
	registrationBean.setID(user.getID());
	registrationBean.setEmail(user.getEmail());
	// Load the registration information and the directory entry
	try
	{
		registrationBean.load();
	}
	catch (PersistenceException pnetEx)
	{
		// log.error("Error occurred while loading registration bean : "+pnetEx.getMessage());
	}

	// Update the registration bean from the directory entry
	registrationBean.populateFromDirectoryEntry();
	if(StringUtils.isNotEmpty(registrationBean.getState()) && registrationBean.getState().equalsIgnoreCase("XX"))
		registrationBean.setState(null);

	if(StringUtils.isNotEmpty(registrationBean.getZipcode()) && registrationBean.getZipcode().equalsIgnoreCase("null"))
		registrationBean.setZipcode(null);
	
	// Getting last login date of user
	request.setAttribute("lastLoginDate", user.getDateFormatter().formatDate(registrationBean.getLastLogin(), "hh:mm a, MMM dd, yyyy"));
	
	// Getting online presence of the user	
	request.setAttribute("isOnline", ServiceFactory.getInstance().getPnUserService().isOnline(Integer.parseInt(user.getID())));
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<link rel="stylesheet" type="text/css" href="<%=SessionManager.getJSPRootURL()%>/styles/profile.css?<%= versionNumber %>"/>

<%-- Import Javascript --%>
<template:getSpaceJS  />

<script language="javascript">
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    

	function search()
	{ 
		self.document.location = JSPRootURL + "/search/SearchController.jsp?module=<%=Module.SALARY%>&action=<%=Action.VIEW%>";
	}

	function help()
	{
			var helplocation = JSPRootURL + "/help/Help.jsp?page=contact_info";
			openwin_help(helplocation);
	}
</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport">
	<template:getSpaceMainMenu />
	<template:getSpaceNavBar />
	<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.financial.salary.contactinfo.title"
				subTitle="<%= registrationBean.getDisplayName() %>" >
		<tb:band name="standard">
		</tb:band>
	</tb:toolbar>
	
<div id='content' style="padding-top:20px; width:40%">

	<tab:tabStrip tabPresentation="true">
		<tab:tab label='<%=PropertyProvider.get("prm.personal.salary.tab.salaryhistory.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/PersonalSalary.jsp?module=" + Module.SALARY + "&user=" + ownerUser.getID()%>' />
		<tab:tab label='<%=PropertyProvider.get("prm.financial.salary.tab.contactinfo.title")%>' href='<%=SessionManager.getJSPRootURL() + "/salary/ContactInfo.jsp?module=" + Module.SALARY%>' selected="true" />
	</tab:tabStrip>
	
	<div class="UMTableBorder marginLeftFix">
		<table border="0" class="table" style="float: left; padding: 15px">
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.personal.profile.name.primaryemail.label")%></td>
			    <td class="info-content"><a href="mailto:<c:out value='${registrationBean.email}'/>"><c:out value='${registrationBean.email}'/></a></td>
			  </tr>
			  <c:if test='${not empty registrationBean.alternateEmail1}'>
				  <tr>
				    <td width="110" class="info-title"><%=PropertyProvider.get("prm.pe rsonal.profile.name.alt1email.label")%></td>
				    <td class="info-content"><a href="mailto:<c:out value='${registrationBean.alternateEmail1}'/>"><c:out value='${registrationBean.alternateEmail1}'/></a></td>
				  </tr>
			  </c:if>
 			  <c:if test='${not empty registrationBean.alternateEmail2}'>
				  <tr>
				    <td class="info-title"><%=PropertyProvider.get("prm.personal.profile.name.alt2email.label")%></td>
				    <td class="info-content"><a href="mailto:<c:out value='${registrationBean.alternateEmail2}'/>"><c:out value='${registrationBean.alternateEmail2}'/></a></td>
				  </tr>
			  </c:if>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.business.memberedit.officephone.label")%></td>
			    <td class="info-content"><c:out value='${registrationBean.officePhone}'/></td>
			  </tr>
			  <c:if test='${not empty registrationBean.faxPhone}'>
				  <tr>
				    <td width="110" class="info-title"><%=PropertyProvider.get("prm.business.memberview.fax.label")%></td>
				    <td class="info-content"><c:out value='${registrationBean.faxPhone}'/></td>
				  </tr>
			  </c:if>
			  <c:if test='${not empty registrationBean.pagerPhone}'>
				  <tr>
				    <td class="info-title"><%=PropertyProvider.get("prm.business.memberview.pagerphone.label")%></td>
				    <td class="info-content"><c:out value='${registrationBean.pagerPhone}'/></td>
				  </tr>
			  </c:if>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.license.detailview.purchaser.address1.label")%></td>
			    <td class="info-content"><c:out value='${registrationBean.address1}'/></td>
			  </tr>
			  <c:if test='${not empty registrationBean.address2}'>
			  <tr>
			    <td class="info-title">&nbsp;</td>
			    <td class="info-content"><c:out value='${registrationBean.address2}'/></td>
			  </tr>
			  </c:if>
			  <c:if test='${not empty registrationBean.city}'>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.global.creditcard.billinginfo.city.name")%></td>
			    <td class="info-content"><c:out value='${registrationBean.city}'/></td>
			  </tr>
			  </c:if>
			  <c:if test='${not empty registrationBean.state}'>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.global.creditcard.billinginfo.state.name")%></td>
			    <td class="info-content"><c:out value='${registrationBean.state}'/></td>
			  </tr>
			  </c:if>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.global.creditcard.billinginfo.country.name")%></td>
			    <td class="info-content"><c:out value='${registrationBean.country}'/></td>
			  </tr>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.personal.profile.timezone.label")%></td>
			    <td class="info-content"><c:out value='${registrationBean.timeZoneCode}'/></td>
			  </tr>
			  <tr>
			    <td class="info-title"><%=PropertyProvider.get("prm.personal.profile.lastlogin.label")%></td>
			    <td class="info-content"><c:out value='${lastLoginDate}'/>
			    <span class="online">
					<c:choose>
						<c:when test='${isOnline}'>
							<label style="color: green; font-weight: bold;">
								<%=PropertyProvider.get("prm.personal.profile.stastus.onlinestatus.label")%>
							</label>
						</c:when>
						<c:otherwise>
							<label style="color: gray; font-weight: bold;">
								<%=PropertyProvider.get("prm.personal.profile.stastus.Offlinestatus.label")%>
							</label>
						</c:otherwise>
					</c:choose>			    
				</span>
			    </td>
			  </tr>
		</table>
		<div style="float: right; padding: 30px">
			<c:choose>
				<c:when test='${user.imageId != 0}'>
					<img width="110px" src="<c:url value='/servlet/photo?'>
												<c:param name='id' value='${user.ID}'/>
												<c:param name='size' value="medium"/>
												<c:param name='module' value='${Module.SALARY}'/>
											</c:url>"
						 id="personImage" alt="<%= PropertyProvider.get("prm.personal.profile.mypicture.alt") %>" />
				</c:when>
				<c:otherwise>
					<img width="110px" src="<c:url value='/images/NoPicture.gif' />" id="personImage" alt="<%= PropertyProvider.get("prm.personal.profile.mypicture.alt") %>" />
				</c:otherwise>
			</c:choose>				
		</div>
		<div style="clear: both;"></div>	
	</div>
</div>
<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>