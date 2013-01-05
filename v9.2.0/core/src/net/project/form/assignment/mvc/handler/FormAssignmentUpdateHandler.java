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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.json.JSONObject;

public class FormAssignmentUpdateHandler extends Handler {

    public FormAssignmentUpdateHandler(HttpServletRequest request) {
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
        
        String personId = request.getParameter("personID");
        String field = request.getParameter("field");
        String value = request.getParameter("value");
        String originalValue = request.getParameter("originalValue");
        
        Map assignmentMap  = (Map) getSessionVar("FormAssignmentMap");
        if(assignmentMap == null || personId == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        FormAssignment formAssignment = (FormAssignment) assignmentMap.get(personId);
        if(formAssignment == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        
        JSONObject jsonRootObject = new JSONObject();
        if(!formAssignment.isLoaded()) {
            //do nothing
            jsonRootObject.put("Warning", "Assignment was " + (formAssignment.isLoaded() ? "" : "Not ") + "Loaded");
        } else if("Work".equals(field)) {
            //store the work
            Number quantity = parseQuantity(value);
            TimeQuantityUnit unit = parseUnit(value);
            TimeQuantity work = new TimeQuantity(quantity, unit);
            if(work.compareTo(formAssignment.getWorkComplete()) < 0) {
                jsonRootObject.put("Warning", "Work cannot be less that Work Complete");
            } else if(work.compareTo(TimeQuantity.O_HOURS) < 0) {
                jsonRootObject.put("Warning", "Work cannot be negative");
            } else {
                formAssignment.setWork(work);
                formAssignment.store();
                jsonRootObject.put("PercentComplete", formAssignment.getPercentComplete().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            
        } else if("DueDate".equals(field)) {
            Date dueDate = DateFormat.getInstance().parseDateTimeString(value, "M/d/y"); 
            formAssignment.setEstimatedFinish(dueDate);
            formAssignment.store();
        } else if("PercentComplete".equals(field)) {
            if(formAssignment.getWorkComplete().compareTo(TimeQuantity.O_HOURS) <= 0) {
                jsonRootObject.put("Warning", "Cannot set % Complete as Work Complete is 0"); 
            } else {
                Number percentComplete = NumberFormat.getInstance().parseNumber(value.trim());
                BigDecimal decimalpercentComplete = new BigDecimal(percentComplete.doubleValue()).divide(new BigDecimal(100));
                TimeQuantity modifiedWork = formAssignment.getWorkComplete().divide(decimalpercentComplete, 3, BigDecimal.ROUND_HALF_UP);
                formAssignment.setWork(modifiedWork);
                formAssignment.store();
                jsonRootObject.put("Work", formAssignment.getWork().toShortString(0, 2));
            }
        } else {
            //do nothing
            jsonRootObject.put("Warning", "Unknown or UnHandled Field " + field + " Edited");            
        }
        model.put("JSON", jsonRootObject.toString());
        
        return model;
    }
    
    private Number parseQuantity(String value) throws ParseException {
        String number = value.replaceAll("[a-zA-Z]","");
        return NumberFormat.getInstance().parseNumber(number.trim());
    }
    
    private TimeQuantityUnit parseUnit(String value) throws ParseException {
        String unit = value.replaceAll("\\d", "");
        if("hrs".equals(unit.trim())) {
            return TimeQuantityUnit.HOUR;
        } else if("days".equals(unit.trim())) {
            return TimeQuantityUnit.DAY;
        } else if("days".equals(unit.trim())) {
            return TimeQuantityUnit.WEEK;
        } else {
            throw new ParseException("Unknown unit " + unit, value.length() - unit.length());
        }
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
    }

}
