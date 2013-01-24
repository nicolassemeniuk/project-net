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
package net.project.form.assignment.mvc.handler;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.form.assignment.FormAssignment;
import net.project.form.assignment.mvc.view.FormAssignmentXMLView;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentRoster;
import net.project.resource.AssignmentType;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;

public class FormAssignmentHandler extends Handler {

    public FormAssignmentHandler(HttpServletRequest request) {
        super(request);
    }
    
    protected String getViewName() {
        throw new UnsupportedOperationException("FormAssignmentHandler cannot return a simple view name.  Use getView() instead.");
    }
    
    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new FormAssignmentXMLView();
    }


    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        
        String formDataID = request.getParameter("formDataID");
        String formDataName = request.getParameter("formDataName");
        
        User user = (User)getSessionVar("user");
        
        AssignmentManager assignmentManager = populateAssignmentManager(formDataID);
        AssignmentRoster assignmentRoster = loadAssignmentRoster(formDataID, user);
        Map assignmentsMap = assignmentManager.getAssignmentMap();
        List assignments = assignmentManager.getAssignments();
        
        Iterator personIter = assignmentRoster.iterator();
        while(personIter.hasNext()) {
            AssignmentRoster.Person person = (AssignmentRoster.Person) personIter.next();
            if(!assignmentsMap.containsKey(person.getID())) {
                FormAssignment unassignFormAssignment = FormAssignment.makeAssignmentFromRoster(formDataID, formDataName, assignmentRoster.getSpace().getID(), person);
                //set the current user in session as assignor
                unassignFormAssignment.setAssignorID(user.getID());
                assignments.add(unassignFormAssignment);
                assignmentsMap.put(person.getID(), unassignFormAssignment);
            }
        }
        
        //set this is session to avoid server round trips for future handling
        setSessionVar("FormAssignmentMap", assignmentsMap);
        
        String xml = assignmentManager.getXML();
        model.put("XML", xml);
//        System.out.println(xml);
        
        return model;
    }
    
    private AssignmentManager populateAssignmentManager(String entryID) throws PersistenceException {
        AssignmentManager assignmentManager = new AssignmentManager();
        assignmentManager.reset();
        assignmentManager.setObjectID(entryID);
        assignmentManager.loadAssigneesForObject();

        return assignmentManager;
    }

    private AssignmentRoster loadAssignmentRoster(String formDataID, User user) throws PersistenceException {
        //Determine if there are any overallocated resources
        AssignmentRoster assignmentRoster = new AssignmentRoster();
        assignmentRoster.setSpace(user.getCurrentSpace());
        assignmentRoster.setObjectID(formDataID, AssignmentType.FORM.getObjectType());
        assignmentRoster.load();
        return assignmentRoster;
    }
    
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
        
        //First verify the parameters
        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.FORM, Action.VIEW, objectID);
        } else if (action == Action.CREATE) {
            AccessVerifier.verifyAccess(Module.FORM, Action.CREATE, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.FORM, Action.MODIFY, objectID);
        }
    }

}
