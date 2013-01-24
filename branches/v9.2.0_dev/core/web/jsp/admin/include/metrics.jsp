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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Application Metrics" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.resource.metric.ResourceMetrics,
            net.project.document.metric.DocumentMetrics,
            net.project.space.metric.SpaceMetrics,
            net.project.security.User"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="resourceMetricCollection" class="net.project.resource.metric.ResourceMetricCollection" />
<jsp:useBean id="spaceMetricCollection" class="net.project.space.metric.SpaceMetricCollection" />
<jsp:useBean id="documentMetricCollection" class="net.project.document.metric.DocumentMetricCollection" />

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<%-- Setup the space stylesheets and js files --%>
     
<body class="main">

		<table border="0" width="100%" vspace="0" cellpadding="2" cellspacing="0"> 
		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.NEW_USERS_CURRENT_MONTH_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.NEW_USERS_CURRENT_MONTH_METRIC)%></td>
            <th align="right" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.ACTIVE_USER_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.ACTIVE_USER_METRIC)%></td>
	  </tr>

		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.NEW_USERS_THREE_MONTH_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.NEW_USERS_THREE_MONTH_METRIC)%></td>
            <th align="right" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.UNREGISTERED_USER_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.UNREGISTERED_USER_METRIC)%></td>
	  </tr>

		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.AVERAGE_USERS_PER_MONTH_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.AVERAGE_USERS_PER_MONTH_METRIC)%></td>
            <th align="right" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.TOTAL_USER_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.TOTAL_USER_METRIC)%></td>
	  </tr>

		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.NEW_USER_TREND_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.NEW_USER_TREND_METRIC)%></td>
            <td colspan="3">&nbsp;</td>
	  </tr>
	  
  	  <tr><td colspan="6">&nbsp;</td></tr>
	  
		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.USER_LOGIN_TODAY_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.USER_LOGIN_TODAY_METRIC)%></td>
            <th align="right" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.AVERAGE_DAILY_USER_LOGINS_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.AVERAGE_DAILY_USER_LOGINS_METRIC)%></td>
	  </tr>

		<tr>
            <th align="left" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.TOTAL_LOGINS_TODAY_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.TOTAL_LOGINS_TODAY_METRIC)%></td>
            <th align="right" class="tableHeader"><%=resourceMetricCollection.getMetricName (ResourceMetrics.USER_LOGIN_LAST_THIRTY_DAYS_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=resourceMetricCollection.getMetricValue (ResourceMetrics.USER_LOGIN_LAST_THIRTY_DAYS_METRIC)%></td>
	  </tr>

	  <tr><td colspan="6">&nbsp;</td></tr>

	   <tr>
            <th align="left" class="tableHeader"><%=spaceMetricCollection.getMetricName (SpaceMetrics.ACTIVE_PROJECT_COUNT_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=spaceMetricCollection.getMetricValue (SpaceMetrics.ACTIVE_PROJECT_COUNT_METRIC)%></td>
            <th align="right" class="tableHeader"><%=spaceMetricCollection.getMetricName (SpaceMetrics.ACTIVE_BUSINESS_COUNT_METRIC)%>:</th>
            <td align="right">&nbsp;</td>
            <td align="left" class="tableContent"><%=spaceMetricCollection.getMetricValue (SpaceMetrics.ACTIVE_BUSINESS_COUNT_METRIC)%></td>
	  </tr>
	  
		<tr>
            <th align="left" class="tableHeader"><%=documentMetricCollection.getMetricName (DocumentMetrics.SYSTEM_DOCUMENT_STORAGE_METRIC)%>:</th>
            <td align="left">&nbsp;</td>
            <td align="left" class="tableContent"><%=documentMetricCollection.getMetricValue (DocumentMetrics.SYSTEM_DOCUMENT_STORAGE_METRIC)%></td>
            <td colspan="3">&nbsp;</td>
	  </tr>
	  
	</table>
</body>


