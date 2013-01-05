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

 package net.project.crossspace.mvc.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.crossspace.AllowableActionCollection;
import net.project.crossspace.ExportFinder;
import net.project.crossspace.ExportedObject;
import net.project.crossspace.ImportFinder;
import net.project.crossspace.ImportedObject;
import net.project.crossspace.TradeAgreement;
import net.project.database.DBBean;
import net.project.schedule.Task;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;

/**
 * Deal with what happens when the user clicks submit on the "CreateShare.jsp"
 * page.  This mostly includes creating the shares and storing them in the
 * database.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class CreateShareProcessingHandler extends Handler {

    /** Stores the errors that we find during processing. */
    protected ErrorReporter errorReporter = new ErrorReporter();

    public CreateShareProcessingHandler(HttpServletRequest request) {
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
        try {
            if (errorReporter.errorsFound()) {
            	return "/servlet/ScheduleController/MainProcessing/Share";
            } else {
	            return URLDecoder.decode(request.getParameter("referrer"), SessionManager.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to decode referer -- invalid " + "character encoding.");
        }
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
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.SHARE);
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
        String spaceID = SessionManager.getUser().getCurrentSpace().getID();
        setRedirect(true);
        //We need to store each item upwards in the hierarchy of objects before
        //we store the objects themselves.  We do this so we have the possibility
        //of constructing a tree of objects for our users to select.
        //
        //If we didn't do this, we'd have to do a lot of messy joins to figure
        //out the hierarchy.
        String hierarchy = request.getParameter("hierarchy");
        String[] hierarchyObjectIDs = hierarchy.split(",");

        String lastHierarchyObjectID;
        String hierarchyObjectID = null;
        for (int i = 0; i < hierarchyObjectIDs.length; i++) {
            lastHierarchyObjectID = hierarchyObjectID;
            hierarchyObjectID = hierarchyObjectIDs[i];

            ExportedObject exportedObject = new ExportedObject();
            exportedObject.setContainerID(lastHierarchyObjectID);
            exportedObject.setID(hierarchyObjectID);
            exportedObject.setPermissionType(TradeAgreement.LEAVE_UNCHANGED);
            exportedObject.setSpaceID(spaceID);
            exportedObject.store();
        }

        //Now store the object(s) being shared
        String objects = request.getParameter("selectedIDs");
        String[] objectIDs = objects.split(",");
    	
        //SHARING PERMISSIONS
        //Figure out what we'll be storing for permissions
        String[] permissionTypeArray = request.getParameterValues("permissionType");
        List permissionTypeList = Arrays.asList(permissionTypeArray == null ? new String[0] : permissionTypeArray);
        TradeAgreement permissionType;
        if (permissionTypeList.contains(TradeAgreement.NO_ACCESS.getID())) {
            permissionType = TradeAgreement.NO_ACCESS;
        } else if (permissionTypeList.contains("2")) {
            permissionType = TradeAgreement.ALL_ACCESS;
        } else if (permissionTypeList.contains("1") && permissionTypeList.size() == 1) {
        	errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.crossspace.createshare.missingactionspermission.error.message")));
        	permissionType = TradeAgreement.NO_ACCESS;
        } else {
            permissionType = TradeAgreement.SPECIFIED_ACCESS;
        }

        //ALLOWED ACTIONS
        //Error checking for no allowable actions being set by user.  
        
        //If setting NO_ACCESS, we don't require Allowed Actions to be set.
        if (permissionType != TradeAgreement.NO_ACCESS) {
            //User must choose one or more actions when setting share access.
	    	if (request.getParameterValues("allowableAction") == null || request.getParameterValues("allowableAction").length == 0) {
	    		errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.crossspace.createshare.missingactionerror.message")));
	    	}
        }
    	
    	
        //Create a collection of the users and objects to which this object is shared
        String[] shareSpaceIDs = request.getParameterValues("shareSpaceID");
        String[] shareUserID = request.getParameterValues("shareUserID");
        
        if(permissionType.equals(TradeAgreement.SPECIFIED_ACCESS) && shareSpaceIDs == null && shareUserID == null) {
        	if(permissionTypeList.contains("3"))
        		errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.crossspace.createshare.nospaces.warning")));
        	
        	if(permissionTypeList.contains("4"))
        		errorReporter.addError(new ErrorDescription(PropertyProvider.get("prm.crossspace.createshare.nousers.warning")));
        }
        
        Map model = new HashMap();
        if (errorReporter.errorsFound()) {
            model.put("errorReporter", errorReporter);
            passThru(model, "module");
            passThru(model, "action");
            model.put("selected", request.getParameter("selectedIDs").split(","));
            
            return model;
        } 

        List shareSpaces = new ArrayList();
        if (shareSpaceIDs != null) {
            for (int i = 0; i < shareSpaceIDs.length; i++) {
                String sharedSpaceID = shareSpaceIDs[i];
                ExportFinder.PermittedObject po = new ExportFinder.PermittedObject(sharedSpaceID, "space", "");
                shareSpaces.add(po);
            }
        }
        List shareUsers = new ArrayList();
        if (shareUserID != null) {
            for (int i = 0; i < shareUserID.length; i++) {
                String sharedUserID = shareUserID[i];
                ExportFinder.PermittedObject po = new ExportFinder.PermittedObject(sharedUserID, "user", "");
                shareUsers.add(po);
            }
        }

        //Determine whether we are propagating to children of this container object.
        //Examples: documents in a folder, tasks on a schedule.
        //If the object being shared is not a container, we should receive "false" here.
        String ptc = request.getParameter("propagateToChildren");
        boolean propagateToChildren = ptc != null && ptc.equals("true");

        //Figure out what allowable actions these objects are going to have.
        //Create the bit mask for allowableActions from the checkboxs that the user set.
        int allowableActionID = 0;
        String[] allowableActions = request.getParameterValues("allowableAction");

        if (allowableActions != null) {
        	for (int i = 0; i < allowableActions.length; i++) {
        		allowableActionID |= Integer.parseInt(allowableActions[i]);
        	}
        }

        //Iterate through all of the objects that the user has opted to share
        //and store them in the database.
        for (int i = 0; i < objectIDs.length; i++) {
            String objectID = objectIDs[i];

            ExportedObject exportedObject = new ExportedObject();
            exportedObject.setContainerID(hierarchyObjectID);
            exportedObject.setID(objectID);
            exportedObject.setSpaceID(spaceID);
            exportedObject.setPermissionType(permissionType);
            exportedObject.setPermittedSpaces(shareSpaces);
            exportedObject.setPermittedUsers(shareUsers);
            exportedObject.setPropagateToChildren(propagateToChildren);
            exportedObject.setAllowableActions(AllowableActionCollection.construct(allowableActionID));
            exportedObject.store();
        }
        
        //sjmittal: bfd-5674 need to remove the tasks for which share spaces are no longer valid
        if(shareSpaces.size() > 0 && permissionType.equals(TradeAgreement.SPECIFIED_ACCESS)) {
            DBBean db = new DBBean();
            try {
                db.setAutoCommit(false);
                db.openConnection();
                for (int i = 0; i < objectIDs.length; i++) {
                    String objectID = objectIDs[i];
                    ImportFinder finder = new ImportFinder();
                    List nonSharingObjects = finder.findByExportedIDAndNotInPermittedSpace(db, objectID);
                    String deleteShareSQL = "delete from pn_shared where imported_object_id = ?";
                    db.prepareStatement(deleteShareSQL);
                    for (Iterator it = nonSharingObjects.iterator(); it.hasNext();) {
                        ImportedObject importedObject = (ImportedObject) it.next();
                        Task task = new Task();
                        task.setID(importedObject.getImportedObjectID());
                        task.remove();
                        db.pstmt.setString(1, importedObject.getImportedObjectID());
                        db.executePrepared();
                    }
                }
                db.commit();
            } finally {
                db.release();
            }
        }
        
        //If the sharing type has been changed to NO_ACCESS, remove all existing shares.
        //Let the page know how many objects are being shared from this object
        if (permissionType.equals(TradeAgreement.NO_ACCESS)) {
            DBBean db = new DBBean();
            try {
                db.setAutoCommit(false);
                db.openConnection();
                
                String deleteShareSQL = "delete from pn_shared where imported_object_id = ?";
                ImportFinder finder = new ImportFinder();
                List sharingObjects = finder.findByExportedIDs(db, Arrays.asList(objectIDs));
                db.prepareStatement(deleteShareSQL);
                for (Iterator it = sharingObjects.iterator(); it.hasNext();) {
                    ImportedObject importedObject = (ImportedObject) it.next();
                    Task task = new Task();
                    task.setID(importedObject.getImportedObjectID());
                    task.remove();
                    //fix for bfd 5670 - remove this from the pn_shared table too
                    //note*: task.remove wont do that because we have not loaded the complete task here, just the reference
                    db.pstmt.setString(1, importedObject.getImportedObjectID());
                    db.executePrepared();
                }

                db.commit();
            } finally {
                db.release();
            }
        }

        //Avinash:-----Before forward setting the action to view----
        	request.setAttribute("action",""+Action.VIEW);
        //Avinash:--------------------------------------------------
        return new HashMap();
    }
}
