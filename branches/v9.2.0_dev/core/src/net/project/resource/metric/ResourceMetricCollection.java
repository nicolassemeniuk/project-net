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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource.metric;

import net.project.base.property.PropertyProvider;

/**
 * A metric which will be used to calculate and display the change in the average new users/month.
 * By definition, the implementation of this metric will show a rate of change in the average users/month metric
 * over the preceeding three months (preceeding the current month)
 */
public class ResourceMetricCollection extends net.project.base.metric.MetricCollection {

    public ResourceMetricCollection() {
        super();
        setName (PropertyProvider.get("prm.global.resource.metric.collection.name"));
    }

    /**
     * Register all metrics in the net.project.resource.metric package.
     * Registration is done by simply adding a new instance of the metric to the collection
     */
    protected void registerMetrics() {

        put (ResourceMetrics.ACTIVE_USER_METRIC, new ActiveUserMetric());
        put (ResourceMetrics.UNREGISTERED_USER_METRIC, new UnregisteredUserMetric());
        put (ResourceMetrics.TOTAL_USER_METRIC, new TotalUserMetric());
        put (ResourceMetrics.AVERAGE_USERS_PER_MONTH_METRIC, new AverageUsersPerMonthMetric());
        put (ResourceMetrics.NEW_USERS_CURRENT_MONTH_METRIC, new NewUsersCurrentMonthMetric());
        put (ResourceMetrics.NEW_USERS_THREE_MONTH_METRIC, new NewUsersThreeMonthMetric());
        put (ResourceMetrics.NEW_USER_TREND_METRIC, new NewUserTrendMetric());
        put (ResourceMetrics.USER_LOGIN_TODAY_METRIC, new UserLoginTodayMetric());
        put (ResourceMetrics.USER_LOGIN_LAST_THIRTY_DAYS_METRIC, new UserLoginLastThirtyDaysMetric());
        put (ResourceMetrics.TOTAL_LOGINS_TODAY_METRIC, new TotalLoginsTodayMetric());
        put (ResourceMetrics.AVERAGE_DAILY_USER_LOGINS_METRIC, new AverageDailyUserLoginsMetric());
    }
}
