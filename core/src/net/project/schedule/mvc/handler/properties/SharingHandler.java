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

 package net.project.schedule.mvc.handler.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.crossspace.ExportFinder;
import net.project.crossspace.ShareCounter;
import net.project.crossspace.mvc.handler.MultiClassExportedObject;
import net.project.crossspace.mvc.handler.SharingTagFilter;
import net.project.schedule.Schedule;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.taglibs.input.InputTagFilter;
import net.project.util.Conversion;

public class SharingHandler extends Handler {
    public SharingHandler(HttpServletRequest request) {
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
        return "/schedule/properties/Sharing.jsp";
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
        Schedule schedule = (Schedule)getSessionVar("schedule");
        passThru(model, "module");
        passThru(model, "action");

        handleCommonTasks(model, schedule);

        //Load the existing sharing information
        ExportFinder finder = new ExportFinder();
        List exportedObjects = finder.findByID(Conversion.asList(schedule.getID()));
        MultiClassExportedObject mcObject = MultiClassExportedObject.processExportedObjects(exportedObjects);
        model.put("multiClassExportedObject", mcObject);

        //Create a filter object which will remove a lot of logic from the JSP page
        InputTagFilter filter = new SharingTagFilter(mcObject);
        model.put("filter", filter);

        //Let the page know how many objects are being shared from this object
        model.put("sharedCount", String.valueOf(ShareCounter.countSharesInUse(schedule.getID())));

        return model;
    }

    /**
     * Deal with tasks that are the same whether you are editing a share or
     * creating one for the first time.
     *
     * @param model a <code>Map</code> of names to parameters objects.  These
     * will ultimately be set in the response object as attributes.
     * @param schedule a <code>Schedule</code> object which we are going to be
     * sharing.
     */
    private void handleCommonTasks(Map model, Schedule schedule) {
        //List of ID's to share
        model.put("idList", schedule.getID());

        //Objects above the schedule that might need to be created
        model.put("hierarchy", SessionManager.getUser().getCurrentSpace().getID());

        //Only the schedule is in the list
        List objectsToShare = new LinkedList();
        objectsToShare.add(schedule);
        model.put("objectsToShare", objectsToShare);

        //Where to go to when were finished sharing
        model.put("referrer", "/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW);

        //Whether we will show the "container" objects
        model.put("isContainer", Boolean.TRUE);
    }
}
