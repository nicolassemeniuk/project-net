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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.PnetRuntimeException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.database.DBBean;
import net.project.form.assignment.FormAssignment;
import net.project.form.assignment.mvc.view.FormAssignmentJSONView;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentWorkLogDAO;
import net.project.resource.AssignmentWorkLogEntry;
import net.project.resource.AssignmentWorkLogFinder;
import net.project.resource.AssignmentWorkLogUtils;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.DateUtils;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class FormAssignmentWorkUpdateHandler extends Handler {

    public FormAssignmentWorkUpdateHandler(HttpServletRequest request) {
        super(request);
    }
    
    protected String getViewName() {
        throw new UnsupportedOperationException("FormAssignmentWorkUpdateHandler cannot return a simple view name.  Use getView() instead.");
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
        
        String formDataID = request.getParameter("formDataID");
        String personID = request.getParameter("personID");
        String logDate = request.getParameter("logDate");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String comment = request.getParameter("comment");
        
        Map assignmentMap  = (Map) getSessionVar("FormAssignmentMap");
        if(assignmentMap == null || personID == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        FormAssignment formAssignment = (FormAssignment) assignmentMap.get(personID);
        if(formAssignment == null) {
            throw new PnetException("Assignment List Not Loaded");
        }
        
        DateFormat dateFormat = DateFormat.getInstance();
        Date dateLogged = dateFormat.parseDateTimeString(logDate, "M/d/y");
        NumberFormat numberFormat = NumberFormat.getInstance();
        int startHours = numberFormat.parseNumber(StringUtils.substringBefore(startTime, ":")).intValue();
        int startMinutes = numberFormat.parseNumber(StringUtils.substringAfter(startTime, ":")).intValue();
        
        int endHours = numberFormat.parseNumber(StringUtils.substringBefore(endTime, ":")).intValue();
        int endMinutes = numberFormat.parseNumber(StringUtils.substringAfter(endTime, ":")).intValue();

        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeZone(formAssignment.getTimeZone());

        calendar.setTime(dateLogged);
        calendar.set(Calendar.HOUR_OF_DAY, startHours);
        calendar.set(Calendar.MINUTE, startMinutes);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.setTime(dateLogged);
        calendar.set(Calendar.HOUR_OF_DAY, endHours);
        calendar.set(Calendar.MINUTE, endMinutes);
        calendar.set(Calendar.SECOND, 0);
        Date endDate = calendar.getTime();
        
        AssignmentWorkLogEntry workLog = new AssignmentWorkLogEntry();
        workLog.setAssigneeID(personID);
        workLog.setObjectID(formDataID);
        workLog.setDatesWorked(new DateRange(startDate, endDate));
        
        //current hours worked
        TimeQuantity hoursWorked = new TimeQuantity(endHours + endMinutes/60.0 - startHours - startMinutes/60.0, TimeQuantityUnit.HOUR);
        //get previous work hours
        TimeQuantity previousWork = findPreviousAssignmentWork(formDataID, personID);
        if(hoursWorked.add(previousWork).compareTo(TimeQuantity.O_HOURS) < 0) {
            JSONObject jsonRootObject = new JSONObject();
            jsonRootObject.put("Warning", "Total Work Logged cannot be -ive.");
            model.put("JSON", jsonRootObject.toString());
            return model;
        }
        
        //update assignment work complete
        formAssignment.setWorkComplete(previousWork.add(hoursWorked));
        //get update work complete
        TimeQuantity workComplete = formAssignment.getWorkComplete();
        //get original work
        TimeQuantity work = formAssignment.getWork();
        if(workComplete.compareTo(work) > 0 ) {// in this case make the work = work complete
            formAssignment.setWork(workComplete);
            work = workComplete;
        }
        
        workLog.setHoursWorked(hoursWorked);
        workLog.setComment(comment);
        workLog.setRemainingWork(work.subtract(workComplete));
        workLog.setModifiedByID(SessionManager.getUser().getID());
        workLog.setScheduledWork(work);

        // Figure out percent complete
        BigDecimal currentPercentComplete;
        if (work.isZero()) {
            // Currently has no work; then they're 100% complete because they've done
            // some work.  This occurs when adding a new assignment while updating work complete
            currentPercentComplete = new BigDecimal("1.00");
        } else {
            // Currently has work; percent complete is ratio of work complete to work, at most 100%
            currentPercentComplete = hoursWorked.divide(work, 2, BigDecimal.ROUND_HALF_UP);
            currentPercentComplete = currentPercentComplete.min(new BigDecimal("1.00"));
        }
        workLog.setPercentComplete(currentPercentComplete);
        
        //update the assignments start and end date
        Date assignmentStartDate = DateUtils.min(dateLogged, formAssignment.getStartTime());
        Date assignmentEndDate = DateUtils.max(dateLogged, formAssignment.getEndTime());
        formAssignment.setStartTime(assignmentStartDate);
        formAssignment.setEndTime(assignmentEndDate);
        
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            formAssignment.store(db);
            AssignmentWorkLogDAO dao = new AssignmentWorkLogDAO();
            dao.store(workLog, db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Pass on original SQLException
            }
            throw new PersistenceException("Unexpected SQLException", sqle);
        } finally {
            db.release();
        }
        JSONObject jsonRootObject = new JSONObject();
        jsonRootObject.put("StartDate", dateFormat.formatDate(formAssignment.getStartTime(), "MMM dd, yyyy"));
        jsonRootObject.put("EndDate", dateFormat.formatDate(formAssignment.getEndTime(), "MMM dd, yyyy"));
        jsonRootObject.put("Work", formAssignment.getWork().toShortString(0, 2));
        jsonRootObject.put("WorkComplete", formAssignment.getWorkComplete().toShortString(0, 2));
        jsonRootObject.put("PercentComplete", formAssignment.getPercentComplete().multiply(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP)));
        jsonRootObject.put("Id", formDataID);
        jsonRootObject.put("HoursWorked", hoursWorked.toShortString(0, 2));
        
        model.put("JSON", jsonRootObject.toString());
        return model;
    }

    private TimeQuantity findPreviousAssignmentWork(String formDataId, String personId) {

        TimeQuantity previousWork = TimeQuantity.O_DAYS;
        try {
            List assignmentLogs = new AssignmentWorkLogFinder().findByObjectIDPersonID(formDataId, personId);
            previousWork = AssignmentWorkLogUtils.getWorkLoggedForAssignee(assignmentLogs, personId);
        } catch (PersistenceException e) {
            throw new PnetRuntimeException(e);
        }
        return previousWork;
    }
    
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
    }
}
