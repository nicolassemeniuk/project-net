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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20640 $
|       $Date: 2010-03-30 10:02:38 -0300 (mar, 30 mar 2010) $
|     $Author: dpatil $
|
+--------------------------------------------------------------------------------------*/
package net.project.calendar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import net.project.base.property.PropertyProvider;
import net.project.gui.tab.Tab;
import net.project.gui.tab.TabException;
import net.project.gui.tab.TabStrip;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * Provides a rendering of a visual calendar, used both for calendar popups
 * and the main calendar display.
 *
 * @author AdamKlatzkin
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Version 1
 */
public class MiniMonth {

    private static final int MODE_day = 0;
    private static final int MODE_week = 1;
    private static final int MODE_month = 2;
    private static final int MODE_year = 3;

    private static final String[] TABS = {"Day", "Week", "Month", "Year"};
    private static final String[] TABSDISPLAY = {"prm.calendar.minimonth.day.tab", "prm.calendar.minimonth.week.tab",
                                                "prm.calendar.minimonth.month.tab", "prm.calendar.minimonth.year.tab"};
    /**
     * A format used for formatting dates for use as parameters.
     */
    private final SimpleDateFormat parameterFormatter = new SimpleDateFormat();

    private Date m_date = null;
    private String m_includer = null;
    private String m_onDayClick = null;
    private int m_mode = MODE_day;
    private final PnCalendar calendar = new PnCalendar();
    private boolean m_displayTabs = true;
    private boolean m_highlightActiveDay = true;

    /**
     * Public constructor to create a new <code>MiniMonth</code> instance.
     */
    public MiniMonth() {
        // Do nothing
    }


    /**
     * sets the active date
     *
     * @param date       the active date
     */
    public void setDate(Date date) {
        parameterFormatter.setTimeZone(calendar.getUser().getTimeZone());
        m_date = date;
    }

    /**
     * sets the width of the table generated
     *
     * @param width      the width  -- number or percentage
     * @deprecated Width is never used; do not call
     */
    public void setWidth(String width) {
    }

    /**
     * sets the page that the mini month is being rendered onto.
     * this will be used in all generated HREF's
     *
     * @param includer      the page using the mini month
     */
    public void setIncluder(String includer) {
        m_includer = includer;
    }

    /**
     * sets the jsp call back used when a user clicks on a month day.
     * If this is not set, the jsp set by 'setIncluder' is used.
     *
     * @param callback the page using the mini month
     */
    public void setOnDayClick(String callback) {
        m_onDayClick = callback;
    }

    /**
     * Determine if the active day should by highlighed in the month view.  This
     * value is true by default.
     *
     * @param state a <code>boolean</code> value.  If this value contains true,
     * the active day will be highlighted in the month view.
     */
    public void setHighlightActiveDay(boolean state) {
        m_highlightActiveDay = state;
    }

    /**
     * Sets the display mode.
     * @param mode the mode to display in; one of <code>day</code>, <code>week</code>,
     * <code>month</code>, <code>year</code>
     */
    public void setMode(String mode) {
        for (int i = 0; i < TABS.length; i++) {
            if (mode.toLowerCase().equals(TABS[i].toLowerCase())) {
                m_mode = i;
                return;
            }
        }
    }

    /**
     * @param state true - display tabs when rendering mini-month (default)
     */
    public void setTabs(boolean state) {
        m_displayTabs = state;
    }


    /**
     * Generate the HTML for this calendar.
     *
     * @param out a <code>JspWriter</code> object used to write the calendar.
     */
    public void renderToStream(JspWriter out) throws IOException {
        if (m_date == null) {
            m_date = PnCalendar.currentTime();
        }
        calendar.setTimeZone(calendar.getUser().getTimeZone());
        calendar.setTime(m_date);

        if (m_onDayClick == null)
            m_onDayClick = m_includer;

        out.println("<TABLE CELLPADDING=0 BORDER=0 CELLSPACING=0>");
        if (m_displayTabs) {
            writeTabs(out);
        }
        writeBody(out);
        out.println("</TABLE>");
    }

    /**
     * Writes out the HTML Day Bar.
     *
     * @param out a <code>JspWriter</code> object to use for output.
     */
    public void writeTabBar(JspWriter out) throws IOException {
        TabStrip tabStrip = new TabStrip();
        Tab tab = null;

        if (m_date == null) {
            m_date = PnCalendar.currentTime();
        }
        calendar.setTimeZone(calendar.getUser().getTimeZone());
        calendar.setTime(m_date);

        String href = m_includer + "?" + CalendarBean.PARAM_date + "=" + formatCurrentDate("MMddyyyy");
        String URL = "";
        for (int i = 0; i < TABS.length; i++) {
            tab = tabStrip.newTab();
            if (m_mode == i) {
                tab.setSelected(true);
            } else {
                tab.setSelected(false);
            }
            URL = href + "&" + CalendarBean.PARAM_mode + "=" + TABS[i].toLowerCase() +
                    "&module=" + net.project.base.Module.CALENDAR;

            tab.setLabel(PropertyProvider.get(TABSDISPLAY[i]));
            tab.setHref(URL);
        }
        try {
            out.println(tabStrip.getPresentation());
            out.println("<br />");
        } catch (TabException te) {
            out.println("");
        }
    }

    /**
     * Generate the HTML tabs.
     */
    private void writeTabs(JspWriter out) throws IOException {
        String bgColor;
        String href = m_includer + "?" + CalendarBean.PARAM_date + "=" + formatCurrentDateForParameter();
        out.println("<TR BGCOLOR=ffffff>");
        boolean theOne = false;

        for (int i = 0; i < TABS.length; i++) {

            if (m_mode == i) {
                bgColor = "class=\"channelHeader\"";
                theOne = true;
            } else {
                bgColor = "class=\"channelContent\"";
                theOne = false;
            }

            out.println("   <TD " + bgColor + " ALIGN=center>");
            if (!theOne) {
                out.println("       <A HREF=\"" + href + "&" + CalendarBean.PARAM_mode + "=" + TABS[i].toLowerCase() +
                        "&module=" + net.project.base.Module.CALENDAR + "\">");
            }

            if (theOne) {
                out.println("<B>" + PropertyProvider.get(TABSDISPLAY[i]) + "</B>");
            } else {
                out.println(PropertyProvider.get(TABSDISPLAY[i]));
            }

            out.println("       </FONT>");
            if (!theOne) {
                out.println("   </A>");
            }
            out.println("   </TD>");
        }
        out.println("</TR>");
    }

    /**
     * Generate the mini month body.
     */
    private void writeBody(JspWriter out) throws IOException {

        out.println("<TR>");
        out.println("   <TD colspan=4 WIDTH=170>");
        switch (m_mode) {
            case MODE_day:
                renderMonthView(out);
                break;
            case MODE_week:
                renderMonthView(out);
                break;
            case MODE_month:
                renderMonthsInYearView(out);
                break;
            case MODE_year:
                renderYearsView(out);
                break;
        }
        out.println("   </TD>");

        out.println("</TR>");
    }

    /**
     * Generate month display.
     */
    private void renderMonthView(JspWriter out) throws IOException {
        String prevMonthParam = formatForParameter(calendar.getPrevMonth());
        String nextMonthParam = formatForParameter(calendar.getNextMonth());

        out.println("<table cellpadding=0 cellspacing=0 border=0 width=\"100%\">");
        out.println("   <tr><td colspan=7>");
        writeHeader(out, prevMonthParam, nextMonthParam, formatCurrentDate("MMMM yyyy"));
        out.println("   </td></tr>");
        out.println("   <tr><td>");
        out.println("       <table cellpadding=0 cellspacing=1 border=0 width=\"100%\" align=center>");
        writeDays(out);
        writeMonthBody(out);
        out.println("       </table>");
        out.println("   </td></tr>");
        out.println("</table>");
    }

    /**
     * Generate a display of the months in a year.
     */
    private void renderMonthsInYearView(JspWriter out) throws IOException {
        String prevYearParam = formatForParameter(calendar.getPrevYear());
        String nextYearParam = formatForParameter(calendar.getNextYear());

        out.println("<table cellpadding=0 cellspacing=0 border=0 width=\"100%\">");
        out.println("   <tr><td colspan=3>");
        writeHeader(out, prevYearParam, nextYearParam, formatCurrentDate("yyyy"));
        out.println("   </td></tr>");
        out.println("   <tr><td>");
        out.println("       <table cellpadding=3 cellspacing=1 border=0 width=\"100%\" align=center >");
        writeMonthsInYearBody(out);
        out.println("       </table>");
        out.println("   </td></tr>");
        out.println("</table>");
    }

    /**
     * Denerate a display of the prev, current & next year.
     */
    private void renderYearsView(JspWriter out) throws IOException {
        Date prevYear = calendar.getPrevYear();
        Date nextYear = calendar.getNextYear();

        String prevYearParam = formatForParameter(prevYear);
        String nextYearParam = formatForParameter(nextYear);

        out.println("<table cellpadding=0 cellspacing=0 border=0 width=\"100%\">");
        out.println("   <tr><td colspan=3>");
        writeHeader(out, prevYearParam, nextYearParam, formatCurrentDate("yyyy"));
        out.println("   </td></tr>");
        out.println("   <tr><td>");
        out.println("       <table cellpadding=3 cellspacing=1 border=0 width=\"100%\" align=center>");
        writeYearsBody(out, prevYear, calendar.getTime(), nextYear);
        out.println("       </table>");
        out.println("   </td></tr>");
        out.println("</table>");
    }

    /**
     * Generate the mini month header.
     */
    private void writeHeader(JspWriter out, String leftParam, String rightParam,
                             String text) throws IOException {

        out.println("<table cellpadding=0 border=0 cellspacing=0 width=100%>");
        out.println("<tr class=\"channelHeader\">\n");
        out.println("<td width=1%><img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>\n");
        out.println("<td width=\"19%\" align=\"center\">");
        out.println("       <a href=\"" + m_includer + "?" + CalendarBean.PARAM_date + "=" + leftParam
                + "&" + CalendarBean.PARAM_mode + "=" + TABS[m_mode].toLowerCase()
                + "&module=" + net.project.base.Module.CALENDAR + "\">");
        out.println("<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/back.gif\" width=9 height=15 alt=\"\" border=0></a></td>\n");
        out.println("	<td width=60% align=center nowrap class=\"channelHeader\"><nobr>" + text + "</nobr></td>\n");
        out.println("<td width=19% align=center>");
        out.println("       <a href=\"" + m_includer + "?" + CalendarBean.PARAM_date + "=" + rightParam
                + "&" + CalendarBean.PARAM_mode + "=" + TABS[m_mode].toLowerCase()
                + "&module=" + net.project.base.Module.CALENDAR + "\">");

        out.println("<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/forward.gif\" width=9 height=15 alt=\"\" border=0></a></td>\n");
        out.println("	<td align=right width=1%><img src=\""+SessionManager.getJSPRootURL()+"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>\n");
        out.println("</tr>\n");

        out.println("</table>");
    }

    /**
     * Writes the days of the week to the output.
     * @param out
     * @throws IOException
     */
    private void writeDays(JspWriter out) throws IOException {

        // Generate locale-specific days of week
        // Starting on appropriate day of week for locale
        PnCalendar weekCal = new PnCalendar(calendar.getUser());
        // Start at the first day of week (month or year is irrelevant)
        weekCal.set(Calendar.DAY_OF_WEEK, weekCal.getFirstDayOfWeek());
        String dayPattern = "EEE";

        out.println("<tr class=\"tableHeader\">");
        // Iterate over 7 days, displaying the name of the day
        for (int i = 0; i < 7; i++) {
            out.println("   <td align=\"center\" class=\"calendar\">" + calendar.getUser()
                    .getDateFormatter()
                    .formatDate(weekCal.getTime(), dayPattern) + "</td>");
            weekCal.roll(Calendar.DAY_OF_WEEK, 1);
        }
        out.println("</tr>");
        out.println("<tr><td colspan=7 class=\"tableLine\"><img src=\""+SessionManager.getJSPRootURL()+"/images/spacers/trans.gif\" width=1 height=1 alt=\"\" border=\"0\"></td></tr>");

    }


    /**
     * Generate the body of a month.
     */
    private void writeMonthBody(JspWriter out) throws IOException {
        int iMonth = calendar.get(Calendar.MONTH);
        int iYear = calendar.get(Calendar.YEAR);

        PnCalendar tempCal = new PnCalendar(calendar.getUser());
        tempCal.setTimeZone(calendar.getUser().getTimeZone());

        //Get the last day of the month
        tempCal.setTime(calendar.endOfMonth(iMonth, iYear));
        int lastDayOfMonth = tempCal.get(Calendar.DAY_OF_MONTH);

        //Get the first day of the month
        tempCal.setTime(calendar.startOfMonth(iMonth, iYear));
        int firstDayOfMonth = tempCal.get(Calendar.DAY_OF_MONTH);
        int day = firstDayOfMonth;

        int firstDayOfWeek = tempCal.getFirstDayOfWeek();

        // This list corresponds to each week row displayed
        // There may be as many as 6 entries, depending on what day of
        // the week the month starts on
        List weeks = new ArrayList();
        while (day <= lastDayOfMonth) {

            // Build the week day array, assigning the day of month and
            // formatted day of month appropriate index position
            WeekDay[] weekDays = new WeekDay[7];

            boolean isEndOfWeek = false;
            while (day <= lastDayOfMonth && !isEndOfWeek) {

                // Calculate index position in our week array, based on
                // the starting day of week for the user's locale
                // and the day of week of the current day
                // For example:  First day of week for US is Sunday (1)
                //               if the current day is saturday (7)
                //               then index position is ( 7 - 1 == 6)
                //               therefore Saturday is the _last_ index position
                //
                //               First day of week for Austria is Monday (2)
                //               if the current day is sunday (1)
                //               then index position is ( 1 - 2 == -1)
                //               which is less than zero, so becomes ( 7 + -1 == 6)
                //               therefore Sunday is the _last_ index position
                int indexPos = tempCal.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
                if (indexPos < 0) {
                    indexPos = 7 + indexPos;
                }

                // Format the day of the week from the date
                parameterFormatter.applyPattern("dd");
                weekDays[indexPos] = new WeekDay(tempCal.get(Calendar.DAY_OF_MONTH),
                        calendar.getUser().getDateFormatter().formatDate(tempCal.getTime(), "dd"), parameterFormatter.format(tempCal.getTime()));

                // Move to next day
                tempCal.roll(Calendar.DAY_OF_MONTH, 1);
                day++;

                // If we populated the last index element
                // Then its time for a new week
                if (indexPos == 6) {
                    isEndOfWeek = true;
                }

            }

            // Save the week
            weeks.add(weekDays);
        }
        String monthNum = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        if (monthNum.length() == 1) {
            monthNum = "0" + monthNum;
        }
        String yearNum = Integer.toString(calendar.get(Calendar.YEAR));

        for (Iterator it = weeks.iterator(); it.hasNext();) {
            WeekDay[] weekDays = (WeekDay[]) it.next();

            out.println("<tr bgcolor=ffffff>");

            for (int j = 0; j < weekDays.length; j++) {
                WeekDay weekDay = weekDays[j];

                String dateParam = (weekDay != null ? monthNum + weekDay.parameterDayNumber + yearNum : "");
                String href = "<a href=\"" + m_onDayClick + "?" + CalendarBean.PARAM_date + "=" + dateParam
                        + "&" + CalendarBean.PARAM_mode + "=" + CalendarBean.MODE_day
                        + "&module=" + net.project.base.Module.CALENDAR + "\" >";
                String hrefCloser = "</a>";

                // Determine the color of the day of week, depends on whether
                // it is today or not and whether we are highlighting the
                // current day
                String color = ((m_highlightActiveDay && isToday(weekDay, tempCal)) ? "class=\"calToday\"" : "class=\"calendar\"");

                out.println("<td align=\"center\" " + color + ">");
                if (weekDay != null) {
                    out.println(href);
                    out.println(weekDay.formattedDayNumber);
                    out.println(hrefCloser);
                } else {
                    out.println("&nbsp;");
                }
                out.println("</td> ");
            }
            out.println("</tr>");
        }

    }

    /**
     * Indicates whether the specified week day represents today when
     * considered in conjunction with the month and year of the specified
     * calendar.
     * @param weekDay the day of the week to check; may be null, in that
     * case returns false
     * @param monthYearCal a calendar providing month and year context for
     * the week day
     * @return true if the weekDay is equal to today; false otherwise
     */
    private boolean isToday(WeekDay weekDay, PnCalendar monthYearCal) {

        boolean isToday = false;

        // Create a calendar set to today
        PnCalendar currentCalendar = new PnCalendar();
        currentCalendar.setTimeZone(calendar.getUser().getTimeZone());

        // Only if the day of month is equal to today's day of month and
        // the specified calendar's month and year matches today
        if ((weekDay != null && weekDay.dayOfMonth == currentCalendar.get(Calendar.DAY_OF_MONTH))
                && currentCalendar.get(Calendar.MONTH) == monthYearCal.get(Calendar.MONTH)
                && currentCalendar.get(Calendar.YEAR) == monthYearCal.get(Calendar.YEAR)) {

            isToday = true;
        }

        return isToday;
    }

    /**
     * Generate the body for months in year view.
     */
    private void writeMonthsInYearBody(JspWriter out) throws IOException {

        int iMonth = calendar.get(Calendar.MONTH);
        int iDay = calendar.get(Calendar.DAY_OF_MONTH);
        int iYear = calendar.get(Calendar.YEAR);
        boolean theOne = false;
        int rows = 4;
        String theDate = null;
        int iPrevMonthDay;

        net.project.util.DateFormat dateFormatter = calendar.getUser().getDateFormatter();

        PnCalendar tempCal = new PnCalendar(calendar.getUser());
        tempCal.clear();
        for (int i = 0; i < rows; i++) {
            out.println("<TR bgcolor=#ffffff>");
            for (int j = 0; j < 3; j++) {
                int curMonth = (i * 3) + j;
                if (curMonth == iMonth) {
                    theOne = true;
                } else {
                    theOne = false;
                }

                if (theOne) {
                    out.println("<TD class=\"calToday\">");
                } else {
                    out.println("<TD class=\"calendar\">");
                }

                tempCal.setTimeZone(calendar.getUser().getTimeZone());
                tempCal.set(Calendar.MONTH, curMonth);
                iPrevMonthDay = iDay;

                if (iPrevMonthDay > tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    iPrevMonthDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                }

                theDate = toPaddedString(curMonth + 1) + toPaddedString(iPrevMonthDay) + iYear;

                if (!theOne) {
                    out.println("<A HREF=\"" + m_includer + "?" + CalendarBean.PARAM_date + "=" + theDate
                            + "&" + CalendarBean.PARAM_mode + "=" + TABS[m_mode].toLowerCase()
                            + "&module=" + net.project.base.Module.CALENDAR + "\">");
                }

                try {
                    if (theOne) {
                        out.println("<B>" + dateFormatter.formatDate(dateFormatter.parseDateString(theDate, "MMddyyyy"), "MMM") + "</B>");
                    } else {
                        out.println(dateFormatter.formatDate(dateFormatter.parseDateString(theDate, "MMddyyyy"), "MMM"));
                    }

                } catch (net.project.util.InvalidDateException ide) {
                    //We don't expect this to happen, hence ignore it
                	Logger.getLogger(MiniMonth.class).debug("MiniMonth.writeMonthInYearsBody: InvalidDateException: " + ide);
                }

                if (theOne) {
                    //out.println("</FONT>");
                    out.println("</A>");
                }
                out.println("</TD>");
            }
            out.println("</TR>");
        }
    }

    /**
     * Generate the body for years view.
     */
    private void writeYearsBody(JspWriter out, Date prevYear, Date thisYear,
                                Date nextYear) throws IOException {

        net.project.util.DateFormat dateFormatter = calendar.getUser()
                .getDateFormatter();
        String theDate;

        out.println("<TR bgcolor=#ffffff>");

        theDate = formatForParameter(prevYear);
        out.println("   <TD ALIGN=CENTER class=\"calendar\">");
        out.println("       <A HREF=\"" + m_includer + "?" + CalendarBean.PARAM_date + "=" + theDate
                + "&" + CalendarBean.PARAM_mode + "=" + TABS[m_mode].toLowerCase()
                + "&module=" + net.project.base.Module.CALENDAR + "\">");
        out.println(dateFormatter.formatDate(prevYear, "yyyy"));
        out.println("       </A>");
        out.println("   </TD>");

        out.println("   <TD ALIGN=CENTER class=\"calToday\">");
        out.println(dateFormatter.formatDate(thisYear, "yyyy"));
        out.println("   </TD>");

        theDate = formatForParameter(nextYear);
        out.println("   <TD ALIGN=CENTER class=\"calendar\">");
        out.println("       <A HREF=\"" + m_includer + "?" + CalendarBean.PARAM_date + "=" + theDate
                + "&" + CalendarBean.PARAM_mode + "=" + TABS[m_mode].toLowerCase()
                + "&module=" + net.project.base.Module.CALENDAR + "\">");
        out.println(dateFormatter.formatDate(nextYear, "yyyy"));
        out.println("       </A>");
        out.println("   </TD>");

        out.println("</TR>");
    }

    /**
     * Pads the specified number to 2 digits by prefixing a "0".
     * <b>Note:</b> This is not localized; the result should <i>not</i> be
     * used for display.  Only useful for a parameter.
     */
    private static String toPaddedString(int num) {
        // Warning: Do not localize; used for parameter only
        StringBuffer result = new StringBuffer();
        if (num >= 0 && num < 10) {
            result.append("0").append(num);
        } else {
            result.append(num);
        }

        return result.toString();
    }

    /**
     * Convert the active date to the specified pattern.
     */
    private String formatCurrentDateForParameter() {
        return parameterFormatter.format(calendar.getTime());
    }

    /**
     * Formats the current date using the specified pattern.
     * @param pattern the pattern to use
     * @return the current date for the specified pattern
     */
    private String formatCurrentDate(String pattern) {
        return calendar.getUser().getDateFormatter().formatDate(calendar.getTime(), pattern);
    }

    /**
     * Convert the specified date to the pattern <code>MMddyyyy</code>.
     * <b>Note:</b> Not a localized pattern; Do not use for display.
     */
    private String formatForParameter(Date date) {
        parameterFormatter.applyPattern("MMddyyyy");
        return parameterFormatter.format(date);
    }

    /**
     * Provides a week day display.
     * Maintains the day of the month and the formatted display of that
     * value.
     */
    private static class WeekDay {

        private int dayOfMonth;
        private String formattedDayNumber;
        private String parameterDayNumber; 

        private WeekDay(int dayOfMonth, String formattedDayNumber, String parameterDayNumber) {
            this.dayOfMonth = dayOfMonth;
            this.formattedDayNumber = formattedDayNumber;
            this.parameterDayNumber = parameterDayNumber;
        }
    }
}
