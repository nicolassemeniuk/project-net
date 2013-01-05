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

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;

/**
 * A metric which will be used to calculate and display total number of new registered users in the last 3 months.
 * By definition, the implementation of this metric will show a count of all new persons
 * which have registered in the last three months (calculated as this month so far + the previous 2 months)
 */
public class NewUsersThreeMonthMetric extends net.project.base.metric.Metric {

        private final String METRIC_PROPERTY_NAME = "prm.global.resource.metric.newusersthreemonthmetric";
    
        /**
         * Empty constructor used to create a new NewUsersThreeMonthMetric
         * Implementation only calls super()
         */
        public NewUsersThreeMonthMetric() {
            super();
        }
    
        /**
         * Method to initialize the Metric.
         * Should be implemented to at least set the name and the ID of the metric
         */
        protected void initialize() {
    
            setID (ResourceMetrics.NEW_USERS_THREE_MONTH_METRIC);
            setName (PropertyProvider.get (METRIC_PROPERTY_NAME));
        }
    
        /**
         * Method to calculate the number of new users that registered in the application in the last 3 months.
         * After calculating the metric, the implementation sets the "isCalculated" flag to true.
         * 
         * @since Gecko 3
         */
        public void calculate() {
    
            DBBean db = new DBBean();
            String userCount = null;
            String qstrGetCountUsers = "select count (person_id) newUserCount from pn_person " +
                " where created_date >= add_months (trunc(sysdate, 'MM'), -2) " +
                " and created_date < add_months (trunc(sysdate,'MM'), 1) ";

            try {

                db.prepareStatement (qstrGetCountUsers);
                db.executePrepared();

                if (db.result.next()) {

                    userCount = db.result.getString ("newUserCount");
                    isCalculated = true;

                } else {
                    userCount = ERROR_VALUE;
                }

            } catch (SQLException sqle) {

                userCount = ERROR_VALUE;
                isCalculated = false;   

            } finally {
                db.release();
            }

            setValue (userCount);
    }
}

