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
    info="Save Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.*,net.project.security.SecurityProvider,
			net.project.security.SessionManager, 
            net.project.resource.*,
            net.project.form.assignment.FormAssignment,
            net.project.schedule.ScheduleTimeQuantity,
			net.project.form.Form,
            net.project.util.*,
            java.text.ParseException,
            java.math.BigDecimal,
            java.math.RoundingMode" 
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<%! void updateDueDate(FormAssignment formAssignment) {
        //Get number of work days for specified estimated work time
        //before that convert it to hours applying i day = 8 hrs and 1 wk = 5 days
        TimeQuantity workDays = ScheduleTimeQuantity.convertToHour(formAssignment.getWork()).convertTo(TimeQuantityUnit.STANDARD_WORK_DAY, 2);
        BigDecimal workAmountInDays = workDays.getAmount().setScale(0, RoundingMode.UP);
        //Calculating end date - needed to skip weekends
        int daysOffset = 1;
        Calendar cal = Calendar.getInstance();
        if (workAmountInDays.intValue() > 1){
            do{
                cal.add(Calendar.DATE, 1);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                    workAmountInDays = workAmountInDays.add(BigDecimal.valueOf(1));	
                }
                daysOffset = daysOffset + 1;
            }while (daysOffset < workAmountInDays.intValue());
        } 
        
        cal.setTime(formAssignment.getStartTime());
        //1 must be subtracted because 1 work day means work shoud be finshed the same day when started
        cal.add(Calendar.DATE, daysOffset-1);
                      
        formAssignment.setEndTime(cal.getTime());
        formAssignment.setEstimatedFinish(cal.getTime());        
}
%>
<%
	String id = securityProvider.getCheckedObjectID();
	int action = securityProvider.getCheckedActionID();
	int module = securityProvider.getCheckedModuleID();
	String qs = null ; // querystring
	
	form.clearErrors();

	form.processHttpPost(request);

	// If all data was not valid, redirect to edit page
	if (form.hasErrors()) {
		// Switch action from Create to View since FormEdit needs to know
		// view permission
		//if (action == net.project.security.Action.CREATE) {  // if clause commented for bug-1437 -> this have to be done for edit action also
			action = net.project.security.Action.VIEW;
		//}
		
			
		if(id != null && !id.trim().equals("")) {
			qs = SessionManager.getJSPRootURL() + "/form/FormEdit.jsp?module=" + module + "&action=" + action + "&id=" + id + "&useSessionForm=true";
		} else {
			qs = SessionManager.getJSPRootURL() + "/form/FormEdit.jsp?module=" + module + "&action=" + action + "&useSessionForm=true";
		}		
		
		response.sendRedirect(qs);
		
		//response.sendRedirect(SessionManager.getJSPRootURL() + "/form/FormEdit.jsp?module=" + module + "&action=" + action + "&id=" + id + "&useSessionForm=true");

	} else {
		form.storeData();
        id =  form.getData().getID();
        //now check if the form has assignments and if they need to be stored
        if (form.getSupportsAssignment()) {
            String personid = request.getParameter("assignedUser");
            String assignorid = request.getParameter("assignorUser");
            //load the assignment if any for the object
            AssignmentManager assignmentManager = new AssignmentManager();
            assignmentManager.reset();
            assignmentManager.setObjectID(id);
            assignmentManager.loadAssigneesForObject();

            List assignments = assignmentManager.getAssignments();
            boolean assignmentFound = false;
            if(assignments != null && assignments.size() > 0) {
                for(int i = 0;  i < assignments.size(); i++) {
                    FormAssignment assignment = (FormAssignment) assignments.get(i);
                    if(personid.equals(assignment.getPersonID())) {
                        //assignment previously created was found so set its status assigned
                        //and update rest of the information
                        assignmentFound = true;
                        assignment.setStatus(AssignmentStatus.ASSIGNED);
                        String work = request.getParameter("work");
                        String workUnits = request.getParameter("work_units");
                        if(work == null || work.trim().length() == 0) {
                            work = "0";
                        }
                        if(workUnits == null || workUnits.trim().length() == 0) {
                            workUnits = "4";
                        }
                        try {
                            TimeQuantity newWork = TimeQuantity.parse(work, workUnits);
                            TimeQuantity oldWorkComplete = assignment.getWorkComplete();
                            if(newWork.compareTo(oldWorkComplete) >= 0) {
                                assignment.setWork(ScheduleTimeQuantity.convertToHour(newWork));
                            }
                        } catch (ParseException e) {
                        }

                        updateDueDate(assignment);
                        assignment.setEndTime(DateUtils.max(new Date(), assignment.getEndTime()));
                        assignment.setEstimatedFinish(DateUtils.max(new Date(), assignment.getEstimatedFinish()));
                        assignment.setAssignorID(assignorid);
                        assignment.store();
                    } else if(AssignmentStatus.personalAssignmentTypes.contains(assignment.getStatus())) {
                        //remove the old assignment set by changing its status
                        assignment.setStatus(AssignmentStatus.REJECTED);
                        //set work = work complete
                        String work = request.getParameter("work_complete");
                        String workUnits = request.getParameter("work_complete_units");
                        try {
                            assignment.setWork(TimeQuantity.parse(work, workUnits));
                        } catch (ParseException e) {
                        }
                        assignment.setEndTime(DateUtils.max(new Date(), assignment.getEndTime()));
                        assignment.setEstimatedFinish(DateUtils.max(new Date(), assignment.getEstimatedFinish()));
                        assignment.store();
                        // to set the status of previous resource as rejected while reassigning the form to another resource 
                        assignment.updateStatus(AssignmentStatus.REJECTED.getID());
                        continue;
                    }
                }
            } 
            if (!assignmentFound && personid != null && personid.trim().length() > 0) {
                //create a brand new assignment
                FormAssignment formAssignment = new FormAssignment();
                formAssignment.setObjectID(id);
                formAssignment.setPersonID(personid);
                formAssignment.setSpaceID(form.getSpace().getID());
                String work = request.getParameter("work");
                String workUnits = request.getParameter("work_units");
                //do not try to store data id user did not entered esitmated work data:	 bfd-4950 
                //now as per bug bug-597    Need the ability to assign a form to a person with 0h work
                if(work == null || work.trim().length() == 0) {
                    work = "0";
                }
                if(workUnits == null || workUnits.trim().length() == 0) {
                    workUnits = "4";
                }
                try {
                    formAssignment.setWork(TimeQuantity.parse(work, workUnits));
                } catch (ParseException e) {
                }
                
                formAssignment.setPercentAssigned(100);                
                formAssignment.setStartTime(new Date());	
                formAssignment.setAssignorID(assignorid);
                updateDueDate(formAssignment);
                formAssignment.store();
            }
        }
	}

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("update")) {
        pageContext.forward ("/form/FormEdit.jsp?module=" + module + "&action=" + action + "&id=" + id + "&useSessionForm=true");
    } else {
    	pageContext.forward("FormList.jsp?load=false&id=" + form.getID() + "&action=" + net.project.security.Action.VIEW);
    }
%>
