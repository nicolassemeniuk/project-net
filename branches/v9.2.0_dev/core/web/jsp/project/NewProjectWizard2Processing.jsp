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
    info="New Project Wizard -- page 1"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
            net.project.security.User,
            net.project.space.Space,
            net.project.security.SecurityProvider,
            net.project.base.property.PropertyProvider,
            net.project.util.Validator,
            net.project.business.BusinessSpace,
            net.project.util.ErrorReporter"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<script type="text/javascript">
	 javascript:window.history.forward(-1);
</script>

<%
    String mySpace=user.getCurrentSpace().getType();
    int module = -1;
    if (mySpace.equals(Space.PERSONAL_SPACE)) module = net.project.base.Module.PERSONAL_SPACE;
    if (mySpace.equals(Space.BUSINESS_SPACE)) module = net.project.base.Module.BUSINESS_SPACE;
    if (mySpace.equals(Space.PROJECT_SPACE)) module = net.project.base.Module.PROJECT_SPACE;
    String verifyAction = null;
    int action = securityProvider.getCheckedActionID();
    if (action == net.project.security.Action.VIEW) verifyAction="view";
    if (action == net.project.security.Action.CREATE) verifyAction="create";
%>
<security:verifyAccess action="<%=verifyAction%>"
                       module="<%=module%>" />

    <jsp:useBean id="projectWizard" class="net.project.project.ProjectWizard" scope="session" />

    <jsp:setProperty name="projectWizard" property="methodologyID" />
    <jsp:setProperty name="projectWizard" property="parentProjectID" />
    <jsp:setProperty name="projectWizard" property="parentBusinessID" />
    <jsp:setProperty name="projectWizard" property="name" />
    <jsp:setProperty name="projectWizard" property="description" />
    <jsp:setProperty name="projectWizard" property="colorCode"
                     value='<%=net.project.code.ColorCode.findByID(request.getParameter("colorCodeID"))%>' />
    <jsp:setProperty name="projectWizard" property="improvementCode"
                     value='<%=net.project.code.ImprovementCode.findByID(request.getParameter("improvementCodeID"))%>' />
    <jsp:setProperty name="projectWizard" property="statusID" />
    <jsp:setProperty name="projectWizard" property="visibility"
                     value='<%=net.project.project.ProjectVisibility.findByID(request.getParameter("visibilityID"))%>' />
    <jsp:setProperty name="projectWizard" property="percentComplete" />
    <jsp:setProperty name="projectWizard" property="costCenter" />
    <jsp:setProperty name="projectWizard" property="defaultCurrencyCode" />
    <jsp:setProperty name="projectWizard" property="budgetedTotalCost" value='<%=net.project.base.money.Money.parseFromRequestDefaultCurrency("budgetedTotalCost", user, request)%>' />
    <jsp:setProperty name="projectWizard" property="currentEstimatedTotalCost" value='<%=net.project.base.money.Money.parseFromRequestDefaultCurrency("currentEstimatedTotalCost", user, request)%>' />
	<jsp:setProperty name="projectWizard" property="actualCostToDate" value='<%=net.project.base.money.Money.parseFromRequestDefaultCurrency("actualCostToDate", user, request)%>' />
                 

<%
    projectWizard.setPercentCalculationMethod(request.getParameter("percentCalculationMethod"));

	if ((request.getParameter("costCenter") == null) || (request.getParameter("costCenter").equals(""))) {
		projectWizard.setCostCenter("");
	}
	try {
		projectWizard.setEstimatedROI(net.project.base.money.Money.parseFromRequestDefaultCurrency("estimatedROI", user, request));
	} catch (Exception e) {
		projectWizard.setEstimatedROI(net.project.base.money.Money.EMPTY);
	}

    // Validate the parent project selection
    boolean isInvalidParentProject;

    // Use must have create action in project module in that space
    if (!Validator.isBlankOrNull(projectWizard.getParentProjectID())) {
        SecurityProvider checkSecurityProvider = new SecurityProvider();
        checkSecurityProvider.setUser(user);
        checkSecurityProvider.setSpace(new ProjectSpace(projectWizard.getParentProjectID()));

        if (!checkSecurityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE), net.project.security.Action.CREATE)) {
            // Failed Security Check
            isInvalidParentProject = true;

        } else {
            // We passed the security check
            isInvalidParentProject = false;
        }

    } else {
        // No Parent Project ID, so they can create this project space
        isInvalidParentProject = false;
    }

    // Validate the owner business selection
    boolean isInvalidOwnerBusiness;

    // Use must have create action in project module in that space
    if (!Validator.isBlankOrNull(projectWizard.getParentBusinessID())) {
        SecurityProvider checkSecurityProvider = new SecurityProvider();
        checkSecurityProvider.setUser(user);
        checkSecurityProvider.setSpace(new BusinessSpace(projectWizard.getParentBusinessID()));

        if (!checkSecurityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.BUSINESS_SPACE), net.project.security.Action.CREATE)) {
            // Failed Security Check
            isInvalidOwnerBusiness = true;

        } else {
            // We passed the security check
            isInvalidOwnerBusiness = false;
        }

    } else {
        // No Parent Project ID, so they can create this project space
        isInvalidOwnerBusiness = false;
    }
    


    // Validate the entered dates 
    boolean isInvalidStartDate = true;
    String startDateString = request.getParameter("startDateString");
    if (startDateString == null || startDateString.trim().length() == 0) {
        isInvalidStartDate = false;
        projectWizard.setStartDate(null);

    } else if (user.getDateFormatter().isLegalDate(startDateString)) {
        isInvalidStartDate = false;
        projectWizard.setStartDateString(startDateString);

    }

    boolean isInvalidEndDate = true;
    String endDateString = request.getParameter("endDateString");
    if (endDateString == null || endDateString.trim().length() == 0) {
        isInvalidEndDate = false;
        projectWizard.setEndDate(null);

    } else if (user.getDateFormatter().isLegalDate(endDateString)) {
        isInvalidEndDate = false;
        projectWizard.setEndDateString(endDateString);

    }

	// Setting meta properties
	java.util.Enumeration names = request.getParameterNames();
	while (names.hasMoreElements()) {
		String name = (String) names.nextElement();
		if (name.startsWith("Meta")) {
			String value = request.getParameter(name);
			String propertyName = name.substring(4);
			if (value != null && !"".equals(value)) {
				projectWizard.getMetaData().setProperty(propertyName, value);
			}
		}
	}

    // Generate error messages
    if (isInvalidParentProject) {
        errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.invalidparentproject.message"));
    }
    
    if (isInvalidOwnerBusiness) {
        errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.invalidownerbusiness.message"));
    }

    if (isInvalidStartDate) {
        errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.startdatevalidation.message", new String[]{startDateString}));
    } 
    
    if (isInvalidEndDate) {
        errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.enddatevalidation.message" , new String[] {endDateString}));
    }

    // If neither date is invalid, check to make sure start is not after end
    if (!(isInvalidStartDate || isInvalidEndDate) &&
            projectWizard.getStartDate() != null &&
            projectWizard.getEndDate() != null &&
            projectWizard.getStartDate().compareTo(projectWizard.getEndDate()) > 0) {

        errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.step2.startdatevalidation.message"));
    }

    // Now navigate to appropriate page
    if (errorReporter.errorsFound()) {
        // Go back to previous page to handle errors
        pageContext.forward("/project/NewProjectWizard2.jsp");
    } else {
        // Go to next page
        pageContext.forward("NewProjectWizard3.jsp?module="+module);
    }
%>
