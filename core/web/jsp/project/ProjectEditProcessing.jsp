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
    info="Project Properties Edit"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.business.BusinessSpace,
		    net.project.project.PercentCalculationMethod,
		    net.project.project.ProjectSpace,
    		net.project.security.SecurityProvider,
            net.project.util.Validator,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.ObjectType,
            net.project.base.EventException,
            net.project.base.EventFactory,
            net.project.events.EventType"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="projectSpace" class="net.project.project.ProjectSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />

<%--
	Modify permission for objects of type "project" is based on modify permission
	of the project space, since an object of type "project" IS a project space.
	Specifying an object id here will cause security to FAIL due to fact that
	object type "project" is not securable.
--%>
<security:verifyAccess action="modify"
                       module="<%=net.project.base.Module.PROJECT_SPACE%>" />

<%
    String theAction = request.getParameter("theAction");
    String forwardingPage = null;
	String oldStatusId = projectSpace.getStatusID();
    if (theAction.equals("submit")) {
%>

<jsp:setProperty name="projectSpace" property="name" />
<jsp:setProperty name="projectSpace" property="description" />
<jsp:setProperty name="projectSpace" property="defaultCurrencyCode" />
<jsp:setProperty name="projectSpace" property="visibility"
                 value='<%=net.project.project.ProjectVisibility.findByID(request.getParameter("visibilityID"))%>' />
<jsp:setProperty name="projectSpace" property="statusID" />
<jsp:setProperty name="projectSpace" property="colorCode"
                 value='<%=net.project.code.ColorCode.findByID(request.getParameter("colorCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="improvementCode"
                 value='<%=net.project.code.ImprovementCode.findByID(request.getParameter("improvementCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="currentStatusDescription" />

<%
		if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		    projectSpace.setDescription("");
		}
		if ((request.getParameter("currentStatusDescription") == null) || (request.getParameter("currentStatusDescription").equals(""))) {
		    projectSpace.setCurrentStatusDescription("");
		}
        projectSpace.setParentBusinessID(request.getParameter("parentBusinessID"));
        projectSpace.setParentProjectID(request.getParameter("parentProjectID"));

        // set percent complete properties
        String method = request.getParameter("percentCalculationMethod");
        String percentComplete = request.getParameter("percentComplete");

        projectSpace.setPercentCalculationMethod(method);

        if (method.equals(PercentCalculationMethod.MANUAL.getID())) {
            projectSpace.setPercentComplete(percentComplete);
        } else {
            // do nothing
        }
%>

<jsp:setProperty name="projectSpace" property="financialStatusColorCode"
                 value='<%=net.project.code.ColorCode.findByID(request.getParameter("financialStatusColorCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="financialStatusImprovementCode"
                 value='<%=net.project.code.ImprovementCode.findByID(request.getParameter("financialStatusImprovementCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="budgetedTotalCost"
                 value='<%=net.project.base.money.Money.parseFromRequest("budgetedTotalCost", user, request)%>' />
<jsp:setProperty name="projectSpace" property="currentEstimatedTotalCost"
                 value='<%=net.project.base.money.Money.parseFromRequest("currentEstimatedTotalCost", user, request)%>' />
<jsp:setProperty name="projectSpace" property="actualCostToDate"
                 value='<%=net.project.base.money.Money.parseFromRequest("actualCostToDate", user, request)%>' />
<%--<jsp:setProperty name="projectSpace" property="estimatedROI"--%>
                 <%--value='<%=net.project.base.money.Money.parseFromRequest("estimatedROI", user, request)%>' />--%>
<jsp:setProperty name="projectSpace" property="scheduleStatusColorCode"
                 value='<%=net.project.code.ColorCode.findByID(request.getParameter("scheduleStatusColorCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="scheduleStatusImprovementCode"
                 value='<%=net.project.code.ImprovementCode.findByID(request.getParameter("scheduleStatusImprovementCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="resourceStatusColorCode"
                 value='<%=net.project.code.ColorCode.findByID(request.getParameter("resourceStatusColorCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="resourceStatusImprovementCode"
                 value='<%=net.project.code.ImprovementCode.findByID(request.getParameter("resourceStatusImprovementCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="sponsor" />
<jsp:setProperty name="projectSpace" property="costCenter" />
<jsp:setProperty name="projectSpace" property="priorityCode"
                 value='<%=net.project.project.PriorityCode.findByID(request.getParameter("priorityCodeID"))%>' />
<jsp:setProperty name="projectSpace" property="riskRatingCode"
                 value='<%=net.project.project.RiskCode.findByID(request.getParameter("riskRatingCodeID"))%>' />

<%
		if ((request.getParameter("sponsor") == null) || (request.getParameter("sponsor").equals(""))) {
		    projectSpace.setSponsor("");
		}
		if ((request.getParameter("costCenter") == null) || (request.getParameter("costCenter").equals(""))) {
		    projectSpace.setCostCenter("");
		}
		try {
			projectSpace.setEstimatedROI(new net.project.base.money.Money(request.getParameter("estimatedROI")));
		} catch (Exception e) {
			projectSpace.setEstimatedROI(net.project.base.money.Money.EMPTY);
		}
        // Validate the parent project selection
        boolean isInvalidParentProject;

        // Use must have create action in project module in that space
        // We don't check security if the parent project selection hasn't changed
        // to allow us to preserve the existing value, even if this user doesn't have permission for it
        if (!Validator.isBlankOrNull(projectSpace.getParentProjectID()) && 
                !projectSpace.getParentProjectID().equals(projectSpace.getOriginalParentProjectID())) {

            SecurityProvider checkSecurityProvider = new SecurityProvider();
            checkSecurityProvider.setUser(user);
            checkSecurityProvider.setSpace(new ProjectSpace(projectSpace.getParentProjectID()));

            if (!checkSecurityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE),
                    net.project.security.Action.CREATE)) {
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

        if (isInvalidParentProject) {
            // Reset it back to the original value; there is code in the ProjectEdit
            // page which ignores the modified value when displaying the parent project
            // so we're trying to keep consistency here
            // This modification is for a patch, so we can't get too fancy
            projectSpace.setParentProjectID(projectSpace.getOriginalParentProjectID());
        }

        // Validate the owner business selection
        boolean isInvalidOwnerBusiness;

        // Use must have create action in project module in that space
        // We don't check security if the parent business selection hasn't changed
        // to allow us to preserve the existing value, even if this user doesn't have permission for it
        if (!Validator.isBlankOrNull(projectSpace.getParentBusinessID()) &&
                !projectSpace.getParentBusinessID().equals(projectSpace.getOriginalParentBusinessID())) {

            SecurityProvider checkSecurityProvider = new SecurityProvider();
            checkSecurityProvider.setUser(user);
            checkSecurityProvider.setSpace(new BusinessSpace(projectSpace.getParentBusinessID()));

            if (!checkSecurityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.BUSINESS_SPACE),
                    net.project.security.Action.CREATE)) {
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

        if (isInvalidOwnerBusiness) {
            // If cannot be selected, reset it
            projectSpace.setParentBusinessID(projectSpace.getOriginalParentBusinessID());
        }

    	if(projectSpace.getParentBusinessID() != null)
    		projectSpace.setParentChanged(!projectSpace.getParentBusinessID().equals(request.getParameter("previousParentSpaceID").trim()));
		else
			projectSpace.setParentChanged(!request.getParameter("previousParentSpaceID").trim().equals(""));        
        
    	if(projectSpace.getParentProjectID() != null)
    		projectSpace.setParentChanged(projectSpace.isParentChanged() || !projectSpace.getParentProjectID().equals(request.getParameter("previousParentProjectSpaceID").trim()));
    	else
    		projectSpace.setParentChanged(projectSpace.isParentChanged() || !request.getParameter("previousParentProjectSpaceID").trim().equals(""));
    	
        // Validate the entered dates
        boolean isInvalidStartDate = true;	
        String startDateString = request.getParameter("startDateString");
        if (startDateString == null || startDateString.trim().length() == 0) {
            isInvalidStartDate = false;
            projectSpace.setStartDate(null);

        } else if (user.getDateFormatter().isLegalDate(startDateString)) {
            isInvalidStartDate = false;
            projectSpace.setStartDateString(startDateString);

        }

        boolean isInvalidEndDate = true;
        String endDateString = request.getParameter("endDateString");
        if (endDateString == null || endDateString.trim().length() == 0) {
            isInvalidEndDate = false;
            projectSpace.setEndDate(null);

        } else if (user.getDateFormatter().isLegalDate(endDateString)) {
            isInvalidEndDate = false;
            projectSpace.setEndDateString(endDateString);

        }

		// Setting meta properties
		java.util.Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith("Meta")) {
				String value = request.getParameter(name);
				String propertyName = name.substring(4);
				if (value != null ) {
					projectSpace.getMetaData().setProperty(propertyName, value);
				}
			}
		}


        // Generate error messagess
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
                projectSpace.getStartDate() != null &&
                projectSpace.getEndDate() != null &&
                projectSpace.getStartDate().compareTo(projectSpace.getEndDate()) > 0) {

            errorReporter.addError(PropertyProvider.get("prm.project.projectcreate.step2.startdatevalidation.message"));
        }

        if (!errorReporter.errorsFound()) {
            // No errors, so store the changes
            projectSpace.store();
            
            if(!oldStatusId.equals(projectSpace.getStatusID())) {
				//publishing event to asynchronous queue
		        try {
		        	net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.OVERALL_STATUS_CHANGED);
		        	projectEvent.setObjectID(projectSpace.getID());
		        	projectEvent.setSpaceID(projectSpace.getID());
		        	projectEvent.setObjectType(ObjectType.PROJECT);
		        	projectEvent.setName(SessionManager.getUser().getCurrentSpace().getName());
		        	projectEvent.setObjectRecordStatus("A");
		        	projectEvent.publish();
				} catch (EventException e) {
					System.out.println("Overall Status Changed Of Project Event, Publishing Failed!" + e);
				}
            }
            request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
            session.removeAttribute("viewContext");
            if ((request.getParameter("referer") != null) && !request.getParameter("referer").equals("")) {            			
				forwardingPage = "/" + request.getParameter("referer");
            } else {
                forwardingPage = "/project/Dashboard?id="+ SessionManager.getUser().getCurrentSpace().getID();
            }

        } else {
            // Errors
            // Go back to edit page
            forwardingPage = "/project/PropertiesEdit.jsp?module="+ Module.PROJECT_SPACE;
            pageContext.forward(forwardingPage);
            return;
        }
    }
	
	response.sendRedirect(SessionManager.getJSPRootURL() +"/project/Main.jsp?id="+ user.getCurrentSpace().getID() + " &page="+ java.net.URLEncoder.encode(SessionManager.getJSPRootURL() + forwardingPage));
	
%>
