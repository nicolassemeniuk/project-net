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

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.form.assignment.FormAssignment;
import net.project.form.assignment.mvc.view.FormAssignmentJSONView;
import net.project.security.AuthorizationFailedException;

import org.json.JSONObject;

public class FormAssignmentAddRemoveHandler extends Handler {

    public FormAssignmentAddRemoveHandler(HttpServletRequest request) {
        super(request);
    }
    
    protected String getViewName() {
        throw new UnsupportedOperationException("FormAssignmentAddRemoveHandler cannot return a simple view name.  Use getView() instead.");
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
        
        String personId = request.getParameter("personID");
        String check = request.getParameter("check");
        
        Map assignmentMap  = (Map) getSessionVar("FormAssignmentMap");
        if(assignmentMap == null || personId == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        FormAssignment formAssignment = (FormAssignment) assignmentMap.get(personId);
        if(formAssignment == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        
        JSONObject jsonRootObject = new JSONObject();
        if(!formAssignment.isLoaded() && "add".equals(check)) {
            formAssignment.store();
            jsonRootObject.put("Loaded", "true");
        } else if(formAssignment.isLoaded() && "remove".equals(check)) {
            formAssignment.remove();
            jsonRootObject.put("Loaded", "false");
        } else {
            //do nothing
            jsonRootObject.put("Warning", "Assignment was " + (formAssignment.isLoaded() ? "" : "Not ") + "Loaded, and Action was to " + check);
        }
        model.put("JSON", jsonRootObject.toString());
        
        return model;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
    }

}
