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
    info="Templify Space Processing. Omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.methodology.*,
    		net.project.security.User,
			net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.util.ErrorReporter"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
%>

<%
      methodologySpace.setUser (user);
      methodologySpace.setName (request.getParameter("name"));
      methodologySpace.setDescription (request.getParameter("description"));
      methodologySpace.setUseScenario (request.getParameter("useScenario"));
      methodologySpace.setParentSpaceID (request.getParameter("parentSpaceID"));
      methodologySpace.setGlobal(request.getParameter("isGlobal"));

/* Not implemented yet
      methodologySpace.setIndustryID (request.getParameter("industryID"));
      methodologySpace.setCategoryID (request.getParameter("categoryID"));
      methodologySpace.setKeywords (request.getParameter("keywords"));
*/
	  methodologySpace.setSelectedModulesStringArray(request.getParameterValues("selectedModule"));

%>

<%
	if (request.getParameter("theAction").equals("submitIndustry")) {
		pageContext.forward ("TemplifySpace.jsp");

	} else if(methodologySpace.isNameExist()){
		ErrorReporter errorReporter = new ErrorReporter();	
		errorReporter.addError(PropertyProvider.get("prm.template.create.namealreadyexist.error.message"));
		request.setAttribute("errorReporter",errorReporter);
		 if (errorReporter.errorsFound()) {
			 pageContext.forward ("TemplifySpace.jsp");
		 }
	}else {
		methodologySpace.store();

		String processingURL = SessionManager.getJSPRootURL() + "/methodology/CreateTemplateProcessing.jsp";
		String processingParams = "module=" + net.project.base.Module.METHODOLOGY_SPACE + "&action=" + net.project.security.Action.CREATE + "&refLink=" + refLinkEncoded;
%>
		<jsp:forward page="/base/Busy.jsp">
			<jsp:param name="processingURL" value="<%=processingURL%>" />
			<jsp:param name="processingParams" value="<%=java.net.URLEncoder.encode(processingParams)%>" />
			<jsp:param name="title" value='<%=PropertyProvider.get("prm.template.create.warning.stoporback.dontclick.title.message")%>' />
			<jsp:param name="message" value='<%=PropertyProvider.get("prm.template.create.warning.stoporback.dontclick.message")%>' />
		</jsp:forward>
<%
	}
%>
	