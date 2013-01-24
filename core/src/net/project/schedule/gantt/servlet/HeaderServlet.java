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
package net.project.schedule.gantt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.calendar.PnCalendar;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.Validator;

/**
 * This class returns the appropriate headers for the schedule entries that are
 * being produced for the gantt chart.
 *
 * @author Matthew Flower
 * @since Version 7.7
 */
public class HeaderServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date scheduleStartDate = null;
        Date scheduleEndDate = null;
        List topHeaders = new ArrayList();
        List bottomHeaders = new ArrayList();
        List topHeaderWidth = new ArrayList();
        Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
        User user = SessionManager.getUser();

        float pixelsPerDay = Float.parseFloat(request.getParameter("pixelsPerDay"));

        //This servlet is called using a url like "/servlet/headerservlet/1.js
        //The "1" is the view level.  We need to call it in this way because
        //IE 6 doesn't seem to recognize the mime type of javascript file just
        //by naming it in the content type.  Instead of getting back the file
        //correctly, it was just returning an error.
        String viewLevel = request.getPathInfo().substring(1, 2);
        if (Validator.isBlankOrNull(viewLevel)) {
            viewLevel = "1";
        }

        scheduleStartDate = findScheduleStartDate(schedule);
        scheduleEndDate = findScheduleEndDate(schedule);
        
        if (user != null && schedule != null) {
	
	        if (viewLevel.equals("0")) {
	            constructHourlyHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        } else if (viewLevel.equals("1")) {
	            constructDailyHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        } else if (viewLevel.equals("2")) {
	            constructWeeklyHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth, pixelsPerDay);
	        } else if (viewLevel.equals("3")) {
	            constructMonthlyHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth, pixelsPerDay);
	        } else if (viewLevel.equals("4")) {
	            constructQuarterlyHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        } else if (viewLevel.equals("5")) {
	            constructHalfYearHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        } else if (viewLevel.equals("6")) {
	            constructYearlyWithQuarterHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        } else if (viewLevel.equals("7")) {
	            constructyearlyWithHalfViewHeaders(scheduleStartDate, scheduleEndDate, topHeaders, bottomHeaders, topHeaderWidth);
	        }
        }
        
        writeOutput(response, topHeaders, bottomHeaders, topHeaderWidth);
    }

    private Date findScheduleStartDate(Schedule schedule) {
        Date scheduleStartDate = schedule.getScheduleStartDate();
        if (scheduleStartDate == null) {
            scheduleStartDate = new Date();
        }
        if (schedule.getEarliestTaskStartTimeMS() != -1) {
            scheduleStartDate = DateUtils.min(scheduleStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
        }
        
        return scheduleStartDate;
    }
    
    private Date findScheduleEndDate(Schedule schedule) {
        Date scheduleEndDate = schedule.getScheduleEndDate();
        if (scheduleEndDate == null) {
            scheduleEndDate = new Date();
        } 
        if (schedule.getLatestTaskEndTimeMS() != -1) {
           	scheduleEndDate = DateUtils.max(scheduleEndDate, new Date(schedule.getLatestTaskEndTimeMS()));
        }
        //add some days after for clarity
        PnCalendar cal = new PnCalendar();
        cal.setTime(scheduleEndDate);
        cal.add(Calendar.DATE, 14);
        scheduleEndDate = cal.getTime();
        
        return scheduleEndDate;
    }

    private void writeOutput(HttpServletResponse response, List topHeaders, List bottomHeaders, List topHeaderWidth) throws IOException {
        response.setContentType("text/javascript");
        String content = drawHeaders(topHeaders, bottomHeaders, topHeaderWidth);
        response.setContentLength(content.getBytes().length);
        PrintWriter out = response.getWriter();
        out.write(content);
        out.flush();
        out.close();
    }

    private void constructHourlyHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            DateFormat topDateFormatter = DateFormat.getInstance();

            Date topHeaderStart = cal.startOfDay(scheduleStartDate);
            Date bottomHeaderStart = new Date(topHeaderStart.getTime());
            Date topHeaderEnd = cal.startOfDay(scheduleEndDate);

            long daysToShow = Math.round(Math.ceil(DateUtils.daysBetween(cal, topHeaderStart, topHeaderEnd)));
            daysToShow = (daysToShow < 3 ? 3 : daysToShow);

            //Construct the top headers
            cal.setTime(topHeaderStart);
            for (int i = 0; i < daysToShow; i++) {
                topHeaders.add(topDateFormatter.formatDate(cal.getTime(), java.text.DateFormat.MEDIUM));
                topHeaderWidths.add(new Integer(480));
                cal.add(PnCalendar.DATE, 1);
            }

            //Construct the bottom headers
            cal.setTime(bottomHeaderStart);
            DateFormat bottomTimeFormatter = DateFormat.getInstance();
            for (int i = 0; i < 12; i++) {
                bottomHeaders.add(bottomTimeFormatter.formatTime(cal.getTime(), "h a"));
                cal.add(PnCalendar.HOUR_OF_DAY, 2);
            }
        }
    }

    private void constructDailyHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            DateFormat topDateFormatter = DateFormat.getInstance();

            Date topHeaderStart = cal.startOfWeek(scheduleStartDate);
            Date topHeaderEnd = cal.endOfWeek(scheduleEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long weeksToShow = Math.round(Math.ceil(DateUtils.daysBetween(cal, topHeaderStart, topHeaderEnd)/7));
            weeksToShow = (weeksToShow < 8 ? 8 : weeksToShow);

            cal.setTime(topHeaderStart);
            for (int i = 0; i < weeksToShow; i++) {
                topHeaders.add(topDateFormatter.formatDate(cal.getTime(), java.text.DateFormat.MEDIUM));
                topHeaderWidths.add(new Integer(105));
                cal.add(PnCalendar.DATE, 7);
            }

            DateFormat bottomDateFormatter = DateFormat.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            int bottomHeadersToCreate = 7;
            for (int i = 0; i<bottomHeadersToCreate; i++) {
				bottomHeaders.add(bottomDateFormatter.formatDate(cal.getTime(), "E").substring(0,1));
                cal.add(PnCalendar.DATE, 1);
            }
        }
    }

    private void constructWeeklyHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths, float pixelsPerDay) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();

            Date topHeaderStart = cal.startOfMonth(scheduleStartDate);
            Date topHeaderEnd = cal.endOfMonth(scheduleEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(scheduleStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            DateFormat topDateFormatter = DateFormat.getInstance();
            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                topHeaders.add(topDateFormatter.formatDate(cal.getTime(),"MMM, yyyy"));
                int pixelsPerMonth = Math.round(cal.getActualMaximum(Calendar.DAY_OF_MONTH) * pixelsPerDay);
                topHeaderWidths.add(new Integer(pixelsPerMonth));
                cal.add(PnCalendar.MONTH,  1);
            }

            DateFormat bottomDateFormatter = DateFormat.getInstance();
            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                bottomHeaders.add(bottomDateFormatter.formatDate(cal.getTime(), "d"));
                cal.add(PnCalendar.DAY_OF_YEAR, 3);
            }
        }
    }

    private void constructMonthlyHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths, float pixelsPerDay) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            DateFormat topDateFormatter = DateFormat.getInstance();
            DateFormat bottomDateFormatter = DateFormat.getInstance();

            Date topHeaderStart = cal.startOfMonth(scheduleStartDate);
            Date topHeaderEnd = cal.endOfMonth(scheduleEndDate);

            //Make sure to show at least 10 weeks (It makes the gantt prettier)
            long monthsToShow = DateUtils.monthsBetween(topHeaderStart, topHeaderEnd);
            if (monthsToShow < 10) {
                monthsToShow = 10;

                cal.setTime(cal.endOfMonth(scheduleStartDate));
                cal.add(Calendar.MONTH,  10);
                topHeaderEnd = cal.getTime();
            }

            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                topHeaders.add(topDateFormatter.formatDate(cal.getTime(), "MMMMM, yyyy"));
                int pixelsPerMonth = Math.round(cal.getActualMaximum(Calendar.DAY_OF_MONTH) * pixelsPerDay);
                topHeaderWidths.add(new Integer(pixelsPerMonth));
                cal.add(PnCalendar.MONTH,  1);
            }

            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                bottomHeaders.add(bottomDateFormatter.formatDate(cal.getTime(), "d"));
                cal.add(PnCalendar.DATE, 7);
            }
        }
    }

    private void constructQuarterlyHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            MessageFormat quarterFormat = new MessageFormat("Qtr {0}, {1}");
            DateFormat yearFormatter = DateFormat.getInstance();
            DateFormat bottomDateFormatter = DateFormat.getInstance();

            //Find the quarter we are starting in.  We might need to adjust the
            //start date so it is in the beginning of a quarter.
            Date topHeaderStart = DateUtils.startOfQuarter(scheduleStartDate);
            Date topHeaderEnd = cal.startOfMonth(scheduleEndDate);

            long quartersToShow = DateUtils.quartersBetween(topHeaderStart, topHeaderEnd);
            quartersToShow = (quartersToShow < 15 ? 15 : quartersToShow);

            cal.setTime(topHeaderStart);
            for (int i = 0; i < quartersToShow; i++) {
                int quarter = (cal.get(PnCalendar.MONTH)/3)+1;

                topHeaders.add(quarterFormat.format(new Object[] {String.valueOf(quarter), yearFormatter.formatDate(cal.getTime(), "yyyy")}));
                topHeaderWidths.add(new Integer(105));
                cal.add(PnCalendar.MONTH,  3);
            }

            cal.setTime(topHeaderStart);
            for (int i = 0; i < 12; i++) {
                bottomHeaders.add(bottomDateFormatter.formatDate(cal.getTime(), "MMM"));
                cal.add(PnCalendar.MONTH, 1);
            }
        }
    }

    private void constructHalfYearHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            Date topHeaderStart = DateUtils.startOfYear(scheduleStartDate);
            Date topHeaderEnd = DateUtils.endOfYear(scheduleEndDate);

            //Make sure to show at least 10 years (It makes the gantt prettier)
            long yearsToShow = DateUtils.yearsBetween(topHeaderStart, topHeaderEnd);
            if (yearsToShow < 10) {
                yearsToShow = 10;

                cal.setTime(cal.endOfMonth(scheduleStartDate));
                cal.add(Calendar.YEAR,  10);
                topHeaderEnd = cal.getTime();
            }

            int half = 1;
            cal.setTime(topHeaderStart);
            DateFormat yearFormatter = DateFormat.getInstance();
            MessageFormat halfFormatter = new MessageFormat("Half {0}, {1}");
            while (cal.getTime().before(topHeaderEnd)) {
                half = (half == 1 ? 2 : 1);
                topHeaders.add(halfFormatter.format(new Object[] { String.valueOf(half), yearFormatter.formatDate(cal.getTime(), "yyyy")} ));
                topHeaderWidths.add(new Integer(120));
                cal.add(PnCalendar.YEAR, 1);
            }

            cal.setTime(topHeaderStart);
            DateFormat monthFormatter = DateFormat.getInstance();
            for (int i = 0; i < 12; i++) {
                bottomHeaders.add(monthFormatter.formatDate(cal.getTime(), "M"));
                cal.add(PnCalendar.MONTH, 1);
            }
        }
    }

    private void constructYearlyWithQuarterHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();
            Date topHeaderStart = cal.startOfMonth(scheduleStartDate);
            Date topHeaderEnd = cal.endOfMonth(scheduleEndDate);

            //Make sure to show at least 15 years (It makes the gantt prettier)
            long yearsToShow = DateUtils.yearsBetween(topHeaderStart, topHeaderEnd);
            if (yearsToShow < 15) {
                yearsToShow = 15;

                cal.setTime(cal.endOfMonth(scheduleStartDate));
                cal.add(Calendar.YEAR,  15);
                topHeaderEnd = cal.getTime();
            }

            cal.setTime(topHeaderStart);
            DateFormat yearFormatter = DateFormat.getInstance();
            while (cal.getTime().before(topHeaderEnd)) {
                topHeaders.add(yearFormatter.formatDate(cal.getTime(), "yyyy"));
                topHeaderWidths.add(new Integer(100));
                cal.add(PnCalendar.YEAR, 1);
            }

            cal.setTime(topHeaderStart);
            MessageFormat quarterFormatter = new MessageFormat("Q{0}");
            for (int i = 1; i <= 4; i++) {
                bottomHeaders.add(quarterFormatter.format(new Object[] { String.valueOf(i) }));
            }
        }
    }

    private void constructyearlyWithHalfViewHeaders(Date scheduleStartDate, Date scheduleEndDate, List topHeaders, List bottomHeaders, List topHeaderWidths) {
        if (scheduleEndDate != null && scheduleStartDate != null) {
            PnCalendar cal = new PnCalendar();

            DateFormat yearFormatter = DateFormat.getInstance();

            Date topHeaderStart = cal.startOfMonth(scheduleStartDate);
            Date topHeaderEnd = cal.endOfMonth(scheduleEndDate);

            //Make sure to show at least 15 years (It makes the gantt prettier)
            long yearsToShow = DateUtils.yearsBetween(topHeaderStart, topHeaderEnd);
            if (yearsToShow < 20) {
                yearsToShow = 20;

                cal.setTime(cal.endOfMonth(scheduleStartDate));
                cal.add(Calendar.YEAR,  20);
                topHeaderEnd = cal.getTime();
            }

            cal.setTime(topHeaderStart);
            while (cal.getTime().before(topHeaderEnd)) {
                topHeaders.add(yearFormatter.formatDate(cal.getTime(), "yyyy"));
                topHeaderWidths.add(new Integer(50));
                cal.add(PnCalendar.YEAR, 1);
            }

            cal.setTime(topHeaderStart);
            MessageFormat halfFormatter = new MessageFormat("H{0}");
            for (int i = 1; i < 3; i++) {
                bottomHeaders.add(halfFormatter.format(new Object[] { String.valueOf(i) } ));
            }
        }
    }

    private String drawHeaders(List topHeaders, List bottomHeaders, List headerWidths) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        //Print the top headers
        out.println("var topHeaders = [");
        for (Iterator it = topHeaders.iterator(); it.hasNext();) {
            out.println("'" + (String)it.next() + (it.hasNext()?"',":"'"));
        }
        out.println("];");

        //Print the bottom headers
        out.println("var bottomHeaders = [");
        for (Iterator it = bottomHeaders.iterator(); it.hasNext();) {
            out.println("'"+(String)it.next() + (it.hasNext()?"',":"'"));

        }
        out.println("];");

        //Print the widths of each header
        out.println("var headerWidths = [");
        for (Iterator it = headerWidths.iterator(); it.hasNext();) {
            out.println(((Integer)it.next()).toString() + (it.hasNext()?",":""));
        }
        out.println("];");

        out.flush();
        out.close();
        return writer.toString();
    }
}
