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
    info="New Project Wizard Page 3 Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.project.*,
	        net.project.security.*,
	        net.project.space.Space,
	        net.project.space.SpaceURLFactory,
	        net.project.base.Module,
            net.project.methodology.MethodologyProvider,
	        net.project.form.FormMenu,
            net.project.util.Validator,
            net.project.base.property.PropertyProvider,
            net.project.hibernate.service.ServiceFactory,
			net.project.hibernate.model.PnMethodologySpace,
			net.project.schedule.Schedule,
			net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider,
			net.project.calendar.workingtime.IWorkingTimeCalendarProvider,
			net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper,
			net.project.calendar.workingtime.WorkingTimeCalendarListHelper"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="projectWizard" class="net.project.project.ProjectWizard" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<script type="text/javascript">
	 javascript:window.history.forward(-1);
</script>

<%
	String mySpace = user.getCurrentSpace().getType();
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

<%
    // Create the new project.
    String refLink = (String) pageContext.getAttribute("pnet_refLink" ,  pageContext.SESSION_SCOPE);

    if(refLink == null) {
    	//refLink = "/portfolio/PersonalPortfolio.jsp?module="+Module.PERSONAL_SPACE+"&portfolio=true";
    	refLink = "/portfolio/Project?module="+Module.PERSONAL_SPACE+"&portfolio=true";
    }

    boolean isActionAllowed = false;

    // If this is a subproject, check to make sure that the user has permission to create
    // a subproject of the given project.
    if (!Validator.isBlankOrNull(projectWizard.getParentProjectID())) {
        // Create an object pointing to the parent project space so we can use the
        // security provider in that space.
        Space testSpace = new ProjectSpace();
        testSpace.setID(projectWizard.getParentProjectID());
        Space oldSpace = securityProvider.getSpace();
        securityProvider.setSpace(testSpace);

        // In the new security context, check that we have permission to create a project.
        if (!securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE), net.project.security.Action.CREATE)) {
            // Failed Security Check
            isActionAllowed = false;

        } else {
            // We passed the security check
            isActionAllowed = true;
        }

        securityProvider.setSpace(oldSpace);

    } else {
        // No Parent Project ID, so they can create this project space
        isActionAllowed = true;
    }

    if (isActionAllowed) {

        // Store the new project
        projectWizard.store();
        user.refreshGroups();

        // Apply the Methodology
        if (projectWizard.getMethodologyID() != null &&(!projectWizard.getMethodologyID().equals(""))) {
            MethodologyProvider methodologyProvider = new MethodologyProvider();
            methodologyProvider.setUser (user);
            methodologyProvider.applyMethodology( projectWizard.getMethodologyID(), projectWizard.getID() );
            PnMethodologySpace methodology = ServiceFactory.getInstance().getPnMethodologySpaceService().getMethodologySpace(Integer.valueOf(projectWizard.getMethodologyID()));
            if(methodology.getIsUsed() == null || methodology.getIsUsed() == 0){
            	methodology.setIsUsed(1);
            	ServiceFactory.getInstance().getPnMethodologySpaceService().updateMethodologySpace(methodology);
            }
        }
        
        //create a dfault working time calendar for projct schedule
        //First load scheule
        Schedule schedule = new Schedule();
        schedule.setSpace(new ProjectSpace(projectWizard.getID()));
	    schedule.load();
	    //make working time calendar provider
	    IWorkingTimeCalendarProvider provider = ScheduleWorkingTimeCalendarProvider.make(schedule);
	    //Crate a calendar.
	    WorkingTimeCalendarCreateHelper helper = new WorkingTimeCalendarCreateHelper(request, provider);
		helper.setCalendarType(WorkingTimeCalendarCreateHelper.CalendarType.BASE.getID());
		helper.setName(PropertyProvider.get("prm.schedule.workingtimecalendar.defaultbase.name", projectWizard.getName()));
		String calendarID = helper.store();
		//Set it as default schedule calendar
		WorkingTimeCalendarListHelper listHelper = new WorkingTimeCalendarListHelper(request, provider);
        listHelper.changeDefaultCalendar(calendarID);
        
        // There is a possibility that creating this project has granted the user new privledges
        // such as access to their document module.  Therefore clear the security cache
        securityProvider.clearCache();
        projectWizard.clear();
        projectWizard.setID(null);

        // Go back to page which started the Create Project Wizard
        response.sendRedirect(refLink);

    } else {
        // Go back to previous page
        request.setAttribute("errorMessage", PropertyProvider.get("prm.project.projectcreate.step3.insufficientprivelegecreate.message"));
        pageContext.forward("/project/NewProjectWizard2.jsp?&module=" + module + "&action=" + action);

    }
%>
