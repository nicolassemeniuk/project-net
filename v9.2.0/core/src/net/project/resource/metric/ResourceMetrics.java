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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource.metric;

/**
 * This class is used to house constants for all of the resource metrics availalbe in the
 * net.project.resource.metrics package.
 * NOTE:  ID's for metrics in this package are in the 100 range.
*/

public class ResourceMetrics {

    /** Metric ID for the ActiveUserMetric */
    public static final String ACTIVE_USER_METRIC = "100";

    /** Metric ID for the UnregisteredUserMetric */
    public static final String UNREGISTERED_USER_METRIC = "101";

    /** Metric ID for the TotalUserMetric */
    public static final String TOTAL_USER_METRIC = "102";

    /** Metric ID for the NewUsersCurrentMonthMetric */
    public static final String NEW_USERS_CURRENT_MONTH_METRIC = "103";

    /** Metric ID for the NewUsersThreeMonthMetric */
    public static final String NEW_USERS_THREE_MONTH_METRIC = "104";

    /** Metric ID for the AverageUsersPerMonthMetric */
    public static final String AVERAGE_USERS_PER_MONTH_METRIC = "105";

    /** Metric ID for the NewUserTrendMetric */
    public static final String NEW_USER_TREND_METRIC = "106";

    /** Metric ID for the UserLoginTodayMetric */
    public static final String USER_LOGIN_TODAY_METRIC = "107";

    /** Metric ID for the UserLoginLastThirtyDaysMetric */
    public static final String USER_LOGIN_LAST_THIRTY_DAYS_METRIC = "108";

    /** Metric ID for the TotalLoginsTodayMetric */
    public static final String TOTAL_LOGINS_TODAY_METRIC = "109";

    /** Metric ID for the AverageDailyUserLoginsMetric */
    public static final String AVERAGE_DAILY_USER_LOGINS_METRIC = "110";
}
