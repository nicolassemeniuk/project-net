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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.project.base.PnetException;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.PercentFilter;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.form.assignment.mvc.view.FormAssignmentJSONView;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentStatus;
import net.project.resource.AssignmentType;
import net.project.resource.mvc.handler.PersonalAssignmentsFilterHandler;
import net.project.security.AuthorizationFailedException;

public class FormAssignmentCheckAssignedUserHander extends Handler {

    public FormAssignmentCheckAssignedUserHander(HttpServletRequest request) {
        super(request);
    }
    
    protected String getViewName() {
        throw new UnsupportedOperationException("FormAssignmentUpdateHandler cannot return a simple view name.  Use getView() instead.");
    }
    
    /**
     * Returns a JSONView view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new FormAssignmentJSONView();
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        
        String objectId = request.getParameter("ObjectId");
        String personId = request.getParameter("PersonId");
        AssignmentManager assignmentManager = new AssignmentManager();
        assignmentManager.reset();
        
        assignmentManager.setObjectID(objectId);
        assignmentManager.setAssignmentTypeFilter(AssignmentType.FORM);
        assignmentManager.setPersonID(personId);
        assignmentManager.setStatusFilter(AssignmentStatus.personalAssignmentTypes);

        PercentFilter percentCompleteFilter = new PercentFilter(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_NAME, AssignmentFinder.PERCENT_COMPLETE_COLUMN, false, true);
        percentCompleteFilter.setSelected(true);
        percentCompleteFilter.setComparator(NumberComparator.NOT_EQUAL);
        percentCompleteFilter.setNumber(100);
        
        assignmentManager.addFilter(percentCompleteFilter);
        
        assignmentManager.loadAssignments();
        
        JSONObject jsonRootObject = new JSONObject();
        jsonRootObject.put("Matched", assignmentManager.getAssignments().size() == 1);
        model.put("JSON", jsonRootObject.toString());
        
        return model;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
    }

}
