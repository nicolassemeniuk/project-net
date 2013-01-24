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

 package net.project.schedule.mvc.handler.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.crossspace.ExportFinder;
import net.project.crossspace.ShareCounter;
import net.project.crossspace.mvc.handler.MultiClassExportedObject;
import net.project.crossspace.mvc.handler.SharingTagFilter;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.taglibs.input.InputTagFilter;
import net.project.util.Conversion;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

/**
 * This class makes the necessary preparations to render the "sharing" tab for
 * schedules by loading the appropriate objects, checking security, etc.
 *
 * @author Matthew Flower
 * @since 8ish
 */
public class ShareHandler extends Handler {
    private String viewName = "/servlet/CrossSpaceController/CreateShare";

    public ShareHandler(HttpServletRequest request) {
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
        return viewName;
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
        AccessVerifier.verifyAccess(module, action);
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
        HashMap model = new HashMap();
        passThru(model, "selected");
        Object selected = model.get("selected");
        if(null == selected){
            selected = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }
        List idList;
        if(selected instanceof String[])
        	idList = new ArrayList(Arrays.asList((String[]) selected));
        else
        	idList = new ArrayList(Arrays.asList(new String[] {(String) selected }));

        //Deal with security in the target page
        passThru(model, "action");
        passThru(model, "module");

        //Check to make sure that none of the ids that the user has selected
        //are shares from another location.  We do not allow "transitive sharing".
        List disallowedObjectIDs = new LinkedList();
        List disallowedObjectNames = new LinkedList();
        String idCSV = DatabaseUtils.collectionToCSV(idList);

        DBBean db = new DBBean();
        try {
            db.executeQuery(
                "select "+
                "  s.imported_object_id, "+
                "  oname.name "+
                "from "+
                "  pn_shared s, "+
                "  pn_object_name oname "+
                "where "+
                "  s.imported_object_id = oname.object_id "+
                "  and s.imported_object_id in ("+idCSV+") "
            );

            boolean resultsFound = false;
            while (db.result.next()) {
                resultsFound = true;
                disallowedObjectIDs.add(db.result.getString("imported_object_id"));
                disallowedObjectNames.add(db.result.getString("name"));
            }

            if (resultsFound) {
                //If there are no id's to share, don't go to the create share
                //page
                if (disallowedObjectIDs.size() == idList.size()) {
                    viewName = "/workplan/taskview?action="+ Action.VIEW +"&module=" + Module.SCHEDULE;
                    model.put("action", String.valueOf(Action.VIEW));
                    model.put("module", String.valueOf(Module.SCHEDULE));
                } else {
                    //Don't let these id's be shown in the create share page.
                    idList.removeAll(disallowedObjectIDs);
                }

                //Let the user know what happened.
                ErrorReporter errorReporter = new ErrorReporter();
                errorReporter.addError(new ErrorDescription("selected", PropertyProvider.get("prm.crossspace.createshare.externaltasks.warning", Conversion.toCommaSeparatedString(disallowedObjectNames))));
                request.getSession().setAttribute("errorReporter", errorReporter);
                if(errorReporter.errorsFound()){
                	setRedirect(true);
                }
                
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to query for tasks that came from shares", sqle);
        } finally {
            db.release();
        }

        //Pass through the ids and additional information about the tasks being
        //exported
        List objectList = new ArrayList();
        Map scheduleEntryMap = ((Schedule)getSessionVar("schedule")).getEntryMap();
        for (Iterator it = idList.iterator(); it.hasNext();) {
            objectList.add(scheduleEntryMap.get(it.next()));
        }

        String[] idListArray = new String[idList.size()];
        model.put("idList", idList.toArray(idListArray));
        model.put("objectsToShare", objectList);

        //Create the tree of parents related to the objects we are going to add
        String spaceID = SessionManager.getUser().getCurrentSpace().getID();
        String workPlanID = ((Schedule)getSessionVar("schedule")).getID();

        model.put("hierarchy", spaceID + "," + workPlanID);
        model.put("referrer", "/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW);

        //Look up information on existing shares -- we will allow the user to
        //modify the existing shares.
        ExportFinder finder = new ExportFinder();
        List exportedObjects = finder.findByID(idList);
        MultiClassExportedObject mcObject = MultiClassExportedObject.processExportedObjects(exportedObjects);
        model.put("multiClassExportedObject", mcObject);

        //Create a filter object which will remove a lot of logic from the JSP page
        InputTagFilter filter = new SharingTagFilter(mcObject);
        model.put("filter", filter);

        model.put("sharedCount", String.valueOf(ShareCounter.countSharesInUse(idList)));

        return model;
    }
}
