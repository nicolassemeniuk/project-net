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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * This page produces the necessary prerequisites for generatating the "specify
 * hours" page for the current assignment update page.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class SpecifyHoursHandler extends Handler {


    public SpecifyHoursHandler(HttpServletRequest request) {
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
        return "/resource/UpdateAssignmentHours.jsp";
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

        User user = (User) getSessionVar("user");
        DateFormat df = new DateFormat(user);

        String objectID = request.getParameter("objectID");

        //Find the date we are entering hours for.
        Date date = new Date(Long.parseLong(request.getParameter("dateID")));
        model.put("dateDisplay", df.formatDate(date));

        //Get the log entries previously entered for this day
        List previousLogEntries = getPreviousLogEntries(date, objectID);
        model.put("previousLogEntries", previousLogEntries);

        //Get the log entries that have been entered but not yet submitted
        List currentLogEntries = getCurrentLogEntries(request);
        model.put("currentLogEntries", currentLogEntries);

        //If the user has already entered hours, figure out how those hours map
        //into times.
        //sjmittal not used anywehere
//        String workHourParam = request.getParameter("currentHours");
//        if (!Validator.isBlankOrNull(workHourParam)) {
//            TimeQuantity workHours = new TimeQuantity(new BigDecimal(workHourParam), TimeQuantityUnit.HOUR);
//        }

        // Construct an example time value
        Calendar exampleCal = new GregorianCalendar(user.getTimeZone(), user.getLocale());
        DateUtils.setTime(exampleCal, 0, 0, 0, 0);
        String exampleTime = df.formatTime(exampleCal.getTime(), java.text.DateFormat.SHORT);
        model.put("exampleTime", exampleTime);

        passThru(model, "dateID");
        passThru(model, "objectID");
        passThru(model, "showFilterPane");
        passThru(model, "errorReporter");

        return model;
    }

    private List getCurrentLogEntries(HttpServletRequest request) {
        List currentLogs = new LinkedList();
        boolean datesInLongFormat = !Validator.isBlankOrNull(request.getParameter("datesInLongFormat"));
        DateFormat df = DateFormat.getInstance();

        for (int i = 1; i < 5; i++) {
            String startString = request.getParameter("timeSpan"+i+"Start");
            String endString = request.getParameter("timeSpan"+i+"End");
            String commentString = request.getParameter("timeSpan"+i+"Comment");

            if (startString != null && endString != null) {
                if (datesInLongFormat) {
                    Date start = new Date(Long.parseLong(startString));
                    Date end = new Date(Long.parseLong(endString));

                    startString = df.formatTime(start, java.text.DateFormat.SHORT);
                    endString = df.formatTime(end, java.text.DateFormat.SHORT);
                }

                currentLogs.add(new LogEntry(startString, endString, commentString));
            }
        }

        return currentLogs;
    }

    private List getPreviousLogEntries(Date date, String objectID) throws PersistenceException {
        List previousLogs = new LinkedList();

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select " +
                "  work_start, work_end, comments " +
                "from " +
                "  pn_assignment_work aw " +
                "where" +
                "  aw.object_id = ? " +
                "  and aw.person_id = ? " +
                "  and work_start > ? " +
                "  and work_end < ?"
            );

            db.pstmt.setString(1, objectID);
            db.pstmt.setString(2, SessionManager.getUser().getID());
            DatabaseUtils.setTimestamp(db.pstmt, 3, date);
            DatabaseUtils.setTimestamp(db.pstmt, 4, new PnCalendar().endOfDay(date));

            db.executePrepared();
            DateFormat df = DateFormat.getInstance();
            while (db.result.next()) {
                Date start = DatabaseUtils.getTimestamp(db.result, 1);
                Date end = DatabaseUtils.getTimestamp(db.result, 2);
                String comment = db.result.getString(3);
                String startString = df.formatTime(start, java.text.DateFormat.SHORT);
                String endString = df.formatTime(end, java.text.DateFormat.SHORT);
                previousLogs.add(new LogEntry(startString, endString, comment));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException(sqle);
        } finally {
            db.release();
        }

        return previousLogs;
    }
}
