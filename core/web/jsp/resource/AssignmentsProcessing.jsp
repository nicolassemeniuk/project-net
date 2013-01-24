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

<%--
  -  $RCSfile$
  -  $Revision: 18397 $
  -  $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
  -  $Author: umesha $
  -
  -  Processes the acceptance or rejection of Assignments.  Security is ignored at the page level
  -  but checked for each accept/reject action on the objectID and module.  If a user does not
  -  have the permission for an action, a flag is set and the rest of the actions are processed.
  -  After all actions are processed an access denied exception may be thrown if needed.
  -
  --%>

<%@ page 
    contentType="text/html; charset=UTF-8"
	info="Assignment Processing" 
	language="java" 
	errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.admin.ApplicationAssignmentManager,
			net.project.configuration.ConfigurationAssignmentManager,
			net.project.base.ObjectType,
			net.project.business.BusinessAssignmentManager,
			net.project.calendar.AttendeeBean,
			net.project.calendar.Meeting,
			net.project.project.ProjectAssignmentManager,
			net.project.project.ProjectSpace,
			net.project.resource.Assignment,
			net.project.resource.Roster,
			net.project.security.SecurityProvider, 
			net.project.security.User, 
			net.project.space.Space,
            net.project.form.assignment.FormAssignment,
            net.project.resource.ScheduleEntryAssignment"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />  
<jsp:useBean id="projectAssignmentManager" class="net.project.project.ProjectAssignmentManager" scope="session" />
<jsp:useBean id="businessAssignmentManager" class="net.project.business.BusinessAssignmentManager" scope="session" />
<jsp:useBean id="applicationAssignmentManager" class="net.project.admin.ApplicationAssignmentManager" scope="session" />
<jsp:useBean id="configurationAssignmentManager" class="net.project.configuration.ConfigurationAssignmentManager" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%!
protected String getType(String assnStatus) {
    return assnStatus.substring(assnStatus.indexOf("_")+1);
}

protected String getObjectID(String assnStatus) {
    return assnStatus.substring(0,assnStatus.indexOf("_"));
}
%>


<%
boolean throwException = false;

// get the assn_statuses out of the request
String[] assignments = request.getParameterValues("assn_status");
String theAction = request.getParameter("channelAction");
String referingURL = request.getParameter("referingURL");

if(assignments != null) {
    for(int i=0; i < assignments.length; i++) {
        String objectType = getType(assignments[i]);
        String objectID = getObjectID(assignments[i]);
        String personID = user.getID();
		
		securityProvider.forceTimeOutOfSecurityCache();		
		
        if(objectType.equals(ObjectType.MEETING)) {
            if(!securityProvider.isActionAllowed(objectID, Integer.toString(net.project.base.Module.CALENDAR), net.project.security.Action.VIEW)) {
                throwException = true;
            } else {
                Meeting meeting = new Meeting();
                meeting.setID(objectID);
                meeting.load();
                AttendeeBean attendee = new AttendeeBean();
                attendee.setEvent(meeting);
                attendee.setPersonId(personID);
                attendee.load();

                if(theAction.equals("Accepted")) {
                    // Accepted Invitation
                    attendee.updateStatus("20"); 
                } else if(theAction.equals("Rejected")) {
                    // Declined Invitation
                    attendee.updateStatus("30");
                }
            }

        } else if(objectType.equals(ObjectType.TASK)) {
            if(!securityProvider.isActionAllowed(objectID, Integer.toString(net.project.base.Module.SCHEDULE), net.project.security.Action.VIEW)) {
                throwException = true;
            } else {
                ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
                assignment.setObjectID(objectID);
                
                //Load the assignment in order to be able to access to all of its properties.
                assignment.load();
                // Overwrite the personID property.
                assignment.setPersonID(personID);
                // Check with action the user has fired.
                if(theAction.equals("Accepted")) {
                    // Check this person is an active participant
                    // for the Assignment's Space
                    String spaceId = assignment.getSpaceID();
                    ProjectSpace project = new ProjectSpace(spaceId);
                    Roster roster = new Roster(project);
                    roster.load();
                    boolean projectAccepted = roster.isUserAccepted(personID); 
                    if (projectAccepted) {
                        // Accepted Invitation
                        assignment
                                .updateStatus(Assignment.ACCEPTED);
                    } else {
                        session
                                .setAttribute(
                                        "assignmentErrorMessage",
                                        PropertyProvider
                                                .get(
                                                        "prm.personal.main.channel.myassignments.error.acceptedinvitationconstraint.message",
                                                        assignment
                                                                .getSpaceName()));
                    }
                } else if(theAction.equals("Rejected")) {
                    // Declined Invitation
                    assignment.updateStatus(Assignment.REJECTED);
                }
            }

        } else if(objectType.equals(ObjectType.FORM_DATA)) {
            if(!securityProvider.isActionAllowed(objectID, Integer.toString(net.project.base.Module.FORM), net.project.security.Action.VIEW)) {
                throwException = true;
            } else {
                FormAssignment assignment = new FormAssignment();
                assignment.setObjectID(objectID);
                assignment.setPersonID(personID);
                if(theAction.equals("Accepted")) {
                    // Accepted Invitation
                    assignment.updateStatus(Assignment.ACCEPTED);
                } else if(theAction.equals("Rejected")) {
                    // Declined Invitation
                    assignment.updateStatus(Assignment.REJECTED);
                }
            }

        } else if(objectType.equals(ObjectType.PROJECT)) {
            // don't need to check permissions on project reject/accept
            String userEmail = user.getEmail();
            projectAssignmentManager.storeInvitationResponse(objectID, personID, userEmail, theAction);

        } else if(objectType.equals(ObjectType.BUSINESS)) {
            // don't need to check permissions on business reject/accept
            String userEmail = user.getEmail();
            businessAssignmentManager.storeInvitationResponse(objectID, personID, userEmail, theAction);
        }

        else if(objectType.equals(ObjectType.APPLICATION)) {
            // Do I need to check permissions?
            String userEmail = user.getEmail();
            applicationAssignmentManager.storeInvitationResponse(objectID, personID, userEmail, theAction);
        }

        else if(objectType.equals(ObjectType.CONFIGURATION)) {
            // Do I need to check permissions?
            String userEmail = user.getEmail();
            configurationAssignmentManager.storeInvitationResponse(objectID, personID, userEmail, theAction);
        }

    } //end for

}

// Check if an security exception needs to be thown.  If so then throw it
if(throwException)
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.resource.security.validationfailed.message"));


//forward back to the refering URL
if(referingURL != null) {
    response.sendRedirect(referingURL);
} else {
    // if the referer wasn't in the request then, as a
    // backup plan, forward it to the Main.jsp
//Avinash:-----------------------------------------------------------------------------
	request.setAttribute("action",Integer.toString(net.project.security.Action.VIEW));
	net.project.security.ServletSecurityProvider.setAndCheckValues(request);
//Avinash:------------------------------------------------------------------------------
    response.sendRedirect("Main.jsp");
}
%>
