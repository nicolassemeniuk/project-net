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
|   $Revision: 19037 $
|       $Date: 2009-03-24 21:23:09 -0300 (mar, 24 mar 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="include page for personal space myProjects channel" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.form.FormMenu,
			net.project.base.property.PropertyProvider,
			net.project.form.FormException,
			net.project.form.FormFilterConstraint,net.project.util.DateUtils,
			net.project.portfolio.ProjectPortfolioBean,net.project.database.DBFormat" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="request" />

<%
		int daysModified = 7 ;
		
		formMenu.setSpace(user.getCurrentSpace());
		FormFilterConstraint formFilterConstraint = new FormFilterConstraint();
		
		daysModified = PropertyProvider.getInt("prm.global.main.channel.formsmodified.days.value") < 0 ? daysModified : PropertyProvider.getInt("prm.global.main.channel.formsmodified.days.value") ;
		
		formFilterConstraint.addParameter("date_modified" , DBFormat.dateTime(DateUtils.addDay(new java.util.Date() , -daysModified ))); 
		formFilterConstraint.setOperator(" > ");
        formMenu.setFilterConstraint(formFilterConstraint);
        formMenu.setFormDataListRequired(true);    
		formMenu.setLoadVisibleFormsOnly(true);
		try {  
			formMenu.load();	
		} catch (FormException fe) {
			//Catch it ... XSL will take care of that 
		}
		
%>

<%-- Apply stylesheet to format myForms portfolio rows --%>
<jsp:setProperty name="formMenu" property="stylesheet" value="/form/xsl/modified-data.xsl" />
<jsp:getProperty name="formMenu" property="presentation" />							
