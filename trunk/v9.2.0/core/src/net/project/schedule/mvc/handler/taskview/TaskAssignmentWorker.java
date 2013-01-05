/* 
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
*/

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskview;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.resource.AssignmentRoster;
import net.project.resource.AssignmentUtils;
import net.project.resource.Roster;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.Validator;

public class TaskAssignmentWorker extends Handler {
    private int totalPercentageAssigned = 0;

    public TaskAssignmentWorker(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     * a view that we are going to redirect to after processing the request.
     */
    public String getViewName() {
        return "/base/Worker.jsp";
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW);
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        ScheduleEntry entry = (ScheduleEntry)getSessionVar("scheduleEntry");
        Map assignments = parseAssignments(request);

        String work = "";
        work += writeNewAssignedHours(assignments.values(), entry);
        work += writeNewMaxPercent(assignments, entry);

        model.put("work", work);

        return model;
    }

    private Map parseAssignments(HttpServletRequest request) throws ParseException {
        Map assignments = new LinkedHashMap();

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String id = (String)parameterNames.nextElement();
            if (!Validator.isNumeric(id)) {
                continue;
            }

            // We need a roster to look up the person to get their timezone
            Roster roster = RosterBean.getFromSession(request.getSession());

            ScheduleEntryAssignment assignment = new ScheduleEntryAssignment();
            assignment.setPersonID(id);
            assignment.setPercentAssigned(ScheduleEntryAssignment.parsePercentAssigned(request.getParameter(id)));
            assignment.setTimeZone(roster.getAnyPerson(id).getTimeZone());
            assignments.put(id, assignment);

            totalPercentageAssigned += assignment.getPercentAssignedInt();
        }

        return assignments;
    }

    private String writeNewAssignedHours(Collection assignments, ScheduleEntry entry) {
        StringBuffer worker = new StringBuffer();
        TimeQuantity totalWork = entry.getWorkTQ();
        BigDecimal totalAssigned = new BigDecimal(totalPercentageAssigned);

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();
            TimeQuantity allocatedWork = AssignmentUtils.calculateAllocatedWork(
                totalWork, assignment.getPercentAssigned(), totalAssigned);

            worker.append("parent.updateWorkAssigned('"+assignment.getPersonID()+"', '").append(allocatedWork.toShortString(0, 3)).append("');\n");
        }

        return worker.toString();
    }

    /**
     * Write out all of the DHTML commands to change the "Max % Allocation During
     * Task" fields to be the correct values.
     *
     * @param assignments
     * @param entry
     */
    private String writeNewMaxPercent(Map assignments, ScheduleEntry entry) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        StringBuffer worker = new StringBuffer();
        Map oldAssignmentMap = entry.getAssignmentList().getAssignmentMap();
        AssignmentRoster roster = ((AssignmentRoster)getSessionVar("assignmentRoster"));
        boolean overAllocationExists = false;

        for (Iterator it = roster.iterator(); it.hasNext();) {
            AssignmentRoster.Person allocation = (AssignmentRoster.Person)it.next();
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)assignments.get(allocation.getID());
            ScheduleEntryAssignment oldAssignment = (ScheduleEntryAssignment)oldAssignmentMap.get(allocation.getID());

            BigDecimal oldPercent = (oldAssignment == null ? new BigDecimal(0) : oldAssignment.getPercentAssigned());
            BigDecimal percent = (assignment == null ? new BigDecimal(0) : assignment.getPercentAssigned());
            BigDecimal percentDiff = percent.subtract(oldPercent).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            BigDecimal newPercent = allocation.getMaxAllocatedDecimal().add(percentDiff);

            //Only register an overallocation if there is actually work assigned to the person
            if (percent.compareTo(new BigDecimal(0)) > 0 && newPercent.compareTo(new BigDecimal("1.0")) > 0) {
                overAllocationExists = true;
            }

            worker.append("parent.updateMaxPercent('").append(allocation.getID()).
                append("', '").append(newPercent).
                append("', ").append(newPercent.multiply(new BigDecimal(100))).append(");\n");
        }

        worker.append("\n");
        worker.append("parent.overallocationExist(").append(Boolean.valueOf(overAllocationExists).toString()).append(");\n");

        return worker.toString();
    }
}
