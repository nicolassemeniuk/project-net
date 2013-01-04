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
package net.project.util;

import java.util.ArrayList;
import java.util.List;

import net.project.calendar.PnCalendar;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.security.User;

/**
 * Utility classes which will show drop down lists of calendar months or years.
 *
 * @author Matthew Flower
 * @version 7.5
 */
public class CalendarUtils {
    private static String DEFAULT_MONTH_PATTERN = "MMMM";
    private static String DEFAULT_YEAR_PATTERN = "yyyy";

    /**
     * Get an HTML dropdown list of months.  It is worth noting that the 
     * values will be 0-11 to match the requirements of Java's calendar
     * classes.
     *
     * @param user an <code>User</code> value which will be used to make the
     * list locale-specific.
     * @param monthToSelect a <code>String</code> value a <code>String</code>
     * value suggesting the month that should be selected in a drop down list.
     * @return a <code>String</code> value containing the html necessary to 
     * render the html options of the option list.  The caller will still need
     * to provide the select tags.
     */
    public static String getMonthOptionList(User user, String monthToSelect) {
        List months = new ArrayList();
        PnCalendar cal = new PnCalendar(user);
        DateFormat df = DateFormat.getInstance();
        cal.set(PnCalendar.MONTH, PnCalendar.JANUARY);
        for (int i=0; i<12; i++) {
            months.add(new HTMLOption(i, df.formatDate(cal.getTime(), DEFAULT_MONTH_PATTERN)));
            cal.roll(PnCalendar.MONTH, 1);
        }

        return HTMLOptionList.makeHtmlOptionList(months, monthToSelect);
    }


    /**
     * Get an HTML dropdown list of years.  This method is currently hard coded 
     * to return 20 years worth of dropdowns.
     *
     * @param user an <code>User</code> value which will be used to make the
     * list locale-specific.
     * @param yearToSelect a <code>String</code> value containing the year to 
     * preselect in the option list.
     * @return a <code>String</code> value containing the html necessary to 
     * render the html options of the option list.  The caller will still need
     * to provide the select tags.
     */
    public static String getCreditCardYearOptionList(User user, String yearToSelect) {
        List years = new ArrayList();
        PnCalendar cal = new PnCalendar(user);
        DateFormat df = DateFormat.getInstance();

        //Start at the current year, show the next 20 years
        for (int i=0; i < 20; i++) {
            years.add(new HTMLOption(cal.get(PnCalendar.YEAR), df.formatDate(cal.getTime(), DEFAULT_YEAR_PATTERN)));
            cal.roll(PnCalendar.YEAR, 1);
        }

        return HTMLOptionList.makeHtmlOptionList(years, yearToSelect);
    }
}
