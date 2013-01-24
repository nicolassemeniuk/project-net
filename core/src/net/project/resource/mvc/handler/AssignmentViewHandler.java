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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.URLFactory;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.base.mvc.Handler;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentWorkLog;
import net.project.resource.AssignmentWorkLogFinder;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Validator;

/**
 * Handler to load the necessary things to show a read-only view of an assignment
 * given the assignment ID.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class AssignmentViewHandler extends Handler {

    /** The default URL to pass as a "returnTo" value, currently the Personal Dashboard. */
    private static final String DEFAULT_RETURN_TO_URL = "/personal/Main.jsp?module=" + Module.PERSONAL_SPACE;

    public AssignmentViewHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the
     *         request.
     */
    public String getViewName() {
        return "/resource/ViewAssignment.jsp";
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just consist
     * of verifying that the parameters that were used to access this page were
     * correct (that is, that the requester didn't try to "spoof it" by using a
     * module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that was
     * passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user
     * didn't have the proper credentials to view this page, or if they tried to
     * spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        //AccessVerifier.verifyAccess();
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

        User user = (User) getSessionVar("user");

        String personID = request.getParameter("personID");
        if (Validator.isBlankOrNull("personID")) {
            // Blank personID is always current person
            personID = user.getID();
        } else if (!user.getID().equals(personID)) {
            // personID specified and not current user;
            if (user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE) || !user.getCurrentSpace().isUserSpaceAdministrator(user)) {
                // personal space or not space admin; cannot view other people's assignments
                // Note: This isn't tokenized because it is only reproducible by subverting URLs; normal behavior
                // never presents the user with a scenario for viewing other persons assignments
                throw new AuthorizationFailedException("You do not have permission to view assignments for other participants.");
            }
        }

        Assignment assignment = loadAssignment(request.getParameter("objectID"), personID);
        model.put("assignment", assignment);

        // Load the work log entries; note we also sort by start date
        // because sometimes two entries can be logged in the exact same second
        // Thus, the earlier start date appears lower in the results
        AssignmentWorkLogFinder workLogFinder = new AssignmentWorkLogFinder();
        workLogFinder.addFinderSorter(new FinderSorter(AssignmentWorkLogFinder.LOG_DATE_COL, true));
        workLogFinder.addFinderSorter(new FinderSorter(AssignmentWorkLogFinder.WORK_START_COL, true));
        AssignmentWorkLog assignmentWorkLog = new AssignmentWorkLog(workLogFinder.findByObjectIDPersonID(assignment.getObjectID(), assignment.getPersonID()));
        model.put("assigmentWorkLog", assignmentWorkLog);

        //Make the URL to go to the property page for this object
        String propertyURL = URLFactory.makeURL(assignment.getObjectID(), assignment.getObjectType());
        model.put("propertyURL", propertyURL);

        String returnTo = request.getParameter("returnTo");
        if (Validator.isBlankOrNull(returnTo)) {
            model.put("returnTo", DEFAULT_RETURN_TO_URL);
        } else {
            passThru(model, "returnTo");
        }

        passThru(model, "objectID");

        return model;
    }

    private Assignment loadAssignment(String id, String personID) throws PersistenceException {
        AssignmentFinder finder = new AssignmentFinder();
        TextFilter objectIDFilter = new TextFilter("objectIDFilter", AssignmentFinder.OBJECT_ID_COLUMN, false);
        objectIDFilter.setSelected(true);
        objectIDFilter.setComparator((TextComparator)TextComparator.EQUALS);
        objectIDFilter.setValue(id);

        TextFilter personFilter = new TextFilter("personFilter", AssignmentFinder.PERSON_ID_COLUMN, false);
        personFilter.setSelected(true);
        personFilter.setComparator((TextComparator)TextComparator.EQUALS);
        personFilter.setValue(personID);

        finder.addFinderFilter(objectIDFilter);
        finder.addFinderFilter(personFilter);
        Collection assignments = finder.findAll();

        if (assignments.size() == 0) {
            throw new PersistenceException("No assignment exists with assignment id " + id);
        } else if (assignments.size() > 1) {
            throw new PersistenceException("More than one assignment returned when querying id " + id);
        } else {
            return (Assignment)assignments.iterator().next();
        }
    }

}
