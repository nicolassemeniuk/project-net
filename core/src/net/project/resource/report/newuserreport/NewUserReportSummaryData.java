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
package net.project.resource.report.newuserreport;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.calendar.PnCalendar;
import net.project.resource.Invitee;
import net.project.security.SessionManager;

/**
 * Data object to calculate and contain counts of invited users appearing in the
 * New User Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NewUserReportSummaryData {
    /** Total number of users invited to this workspace. */
    private int totalNumberOfInvitedUsers;
    /** Total number of users invited in the last 7 days. */
    private int invitedInTheLast7Days;
    /** Total number of users invited in the last month. */
    private int invitedInTheLastMonth;
    /** Total number of users that responded to an invitation in the last 7 days. */
    private int respondedInTheLast7Days;
    /** Total number of users that responded to an invitation in the last month. */
    private int respondedInTheLastMonth;

    /**
     * Create a NewUserReportSummaryData object, populating it by iterating over
     * already queried data.
     *
     * @param invitedUsers a <code>List</code> of
     * {@link net.project.resource.Invitee} objects which we will iterate through
     * to calculate the fields this report presents.
     */
    public NewUserReportSummaryData(List invitedUsers) {
        //Calculate dates we will need to aggregate totals
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        Date now = new Date();
        cal.setTime(now);
        cal.add(PnCalendar.DATE,  -7);
        Date sevenDaysAgo = cal.getTime();
        cal.setTime(now);
        cal.add(PnCalendar.MONTH, -1);
        Date oneMonthAgo = cal.getTime();

        //Iterate through the invited users to calculate totals
        for (Iterator it = invitedUsers.iterator(); it.hasNext();) {
            Invitee invitee = (Invitee)it.next();

            totalNumberOfInvitedUsers++;
            if (invitee.getInvitedDate().after(sevenDaysAgo)) {
                invitedInTheLast7Days++;
            }
            if (invitee.getInvitedDate().after(oneMonthAgo)) {
                invitedInTheLastMonth++;
            }
            if (invitee.getResponseDate() != null) {
                if (invitee.getResponseDate().after(sevenDaysAgo)) {
                    respondedInTheLast7Days++;
                }
                if (invitee.getResponseDate().after(oneMonthAgo)) {
                    respondedInTheLastMonth++;
                }
            }
        }
    }

    /**
     * The total number of invited users in the reporting time period.
     *
     * @return a <code>int</code> value representing the number of invited
     * users in the reporting time period.
     */
    public int getTotalNumberOfInvitedUsers() {
        return totalNumberOfInvitedUsers;
    }

    /**
     * The number of users invited to the workspace within the last 7 days.
     *
     * @return a <code>int</code> value containing the number of users invited
     * in the last 7 days.
     */
    public int getInvitedInTheLast7Days() {
        return invitedInTheLast7Days;
    }

    /**
     * Get the number of users invited to this workspace in the last month.
     *
     * @return a <code>int</code> value containing the number of users invited
     * to this workspace in the last month.
     */
    public int getInvitedInTheLastMonth() {
        return invitedInTheLastMonth;
    }

    /**
     * Get the number of users which responded to an invitation to this
     * workspace in the last seven days.
     *
     * @return a <code>int</code> value containing the number of users which
     * responded to an invitation to this workspace in the last 7 days.
     */
    public int getRespondedInTheLast7Days() {
        return respondedInTheLast7Days;
    }

    /**
     * Get the number of users which responded to an invitation to this workspace
     * in the last month.
     *
     * @return a <code>int</code> value containing the number of users which
     * responded to an invitation to this workspace in the last month.
     */
    public int getRespondedInTheLastMonth() {
        return respondedInTheLastMonth;
    }
}
