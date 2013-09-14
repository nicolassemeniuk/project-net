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

 package net.project.schedule.mvc.handler.tasklist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.material.MaterialAssignment;
import net.project.project.ProjectSpace;
import net.project.resource.AssignmentRoster;
import net.project.schedule.MaterialAssignmentHelper;
import net.project.schedule.MaterialAssignmentsHelper;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.ErrorReporter;

/**
 * This page is the handler implementation for the page that comes up when you
 * click on the materials toolbar button on the workplan.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public class AssignMaterialsHandler extends Handler {
    private static final String SUCCESS_VIEW = "/schedule/AssignMaterialsDialog.jsp";
    private static final String ERROR_VIEW = "/schedule/include/AssignMaterialsDialogCancel.jsp";
    private String viewName = SUCCESS_VIEW;
    public AssignMaterialsHandler(HttpServletRequest request) {
        super(request);
    }

    protected String getViewName() {
        return viewName;
    }

    public void validateSecurity(int module, int action,
                                 String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map handleRequest(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        Schedule schedule = (Schedule)getSessionVar("schedule");
        Map scheduleEntries = schedule.getEntryMap();

        String[] ids = request.getParameterValues("selected");
        
        if(null == ids){
            ids = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }

        /* Keep track of any tasks that are shared, we aren't going to be able
           to assign materials to them because it isn't allowed. */
        List sharedTasks = new ArrayList();
        List newIDList = new ArrayList();
        for (int i = 0; i < ids.length; i++) {
            ScheduleEntry entry = (ScheduleEntry)scheduleEntries.get(ids[i]);
            if (entry.isFromShare()) {
                sharedTasks.add(entry.getNameMaxLength40());
            } else {
                newIDList.add(ids[i]);
            }
        }

        if (sharedTasks.size() == ids.length) {
            //This is an error condition, we cannot assign to shared tasks
            model.put("errorMessage", PropertyProvider.get("prm.schedule.sharedtasks.allresources"));
            model.put("module", String.valueOf(Module.SCHEDULE));
            model.put("action", String.valueOf(Action.VIEW));

            viewName = ERROR_VIEW;
        } else {
            if (sharedTasks.size() > 0) {
                ErrorReporter errorReporter = new ErrorReporter();
                errorReporter.addWarning(PropertyProvider.get("prm.schedule.sharedtasks.resources.noassign", sharedTasks));
                model.put("errorReporter", errorReporter);
            }

            passThru(model, "module");
            passThru(model, "action");
            model.put("idList", DatabaseUtils.collectionToCSV(newIDList));
        }

        User user = (User)getSessionVar("user");
        ScheduleEntry scheduleEntry = (ScheduleEntry)getSessionVar("scheduleEntry");
    	String spaceId = String.valueOf(user.getCurrentSpace().getID());
    	
    	MaterialAssignmentsHelper materialAssignmentsHelper	= new MaterialAssignmentsHelper();
    	materialAssignmentsHelper.setSpaceId(spaceId);

    	if(newIDList.size() == 1)
        	materialAssignmentsHelper.setObjectId((String) newIDList.get(0));

    	materialAssignmentsHelper.load();
    	
    	// If materialAssignmentsHelper contains consumable materials they can't be assigned
    	if(newIDList.size() > 1)
    	{
    		ArrayList<MaterialAssignmentHelper> assignments = materialAssignmentsHelper.getMaterialsAssigned();
    		
    		for (MaterialAssignmentHelper assignment : assignments)
    			if(assignment.getMaterial().getConsumable())
    			assignment.setEnabledForAssignment(false);
    	}
    	
        model.put("materialAssignmentsHelper", materialAssignmentsHelper);    	
   	    
    	MaterialAssignmentsHelper materialBusinessAssignmentsHelper	= new MaterialAssignmentsHelper();
    	materialBusinessAssignmentsHelper.setSpaceId(spaceId);    	
    	
		ProjectSpace projectSpace = (ProjectSpace) user.getCurrentSpace();
		String parentBusinessID = projectSpace.getParentBusinessID();    	
    	
    	// If the project has a business owner    	
    	if(parentBusinessID != null)
    	{
	    	materialBusinessAssignmentsHelper.setParentBusinessID(parentBusinessID);
	    	
	    	if(newIDList.size() == 1)
	    		materialBusinessAssignmentsHelper.setObjectId((String) newIDList.get(0));	    	
	    	
	    	// If materialAssignmentsHelper contains consumable materials they can't be assigned
	    	if(newIDList.size() > 1)
	    	{
	    		ArrayList<MaterialAssignmentHelper> assignments = materialBusinessAssignmentsHelper.getMaterialsAssigned();
	    		
	    		for (MaterialAssignmentHelper assignment : assignments)
	    			if(assignment.getMaterial().getConsumable())
	    			assignment.setEnabledForAssignment(false);
	    	}	    	
	    		    	
	    	materialBusinessAssignmentsHelper.loadForBusiness();           
    	}
    	
        model.put("materialBusinessAssignmentsHelper", materialBusinessAssignmentsHelper);
    	
        return model;
    }
}
