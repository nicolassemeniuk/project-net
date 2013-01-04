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
 * A metric which will be used to calculate and display the change in the average new users/month.
 * By definition, the implementation of this metric will show a rate of change in the average users/month metric
 * over the preceeding three months (preceeding the current month)
 */
public class NewUserTrendMetric extends net.project.base.metric.Metric {

        private final String METRIC_PROPERTY_NAME = "prm.global.resource.metric.newusertrendmetric";
    
        /**
         * Empty constructor used to create a new NewUserTrendMetric
         * Implementation only calls super()
         */
        public NewUserTrendMetric() {
            super();
        }
    
        /**
         * Method to initialize the Metric.
         * Should be implemented to at least set the name and the ID of the metric
         */
        protected void initialize() {
    
            setID (ResourceMetrics.NEW_USER_TREND_METRIC);
            setName (PropertyProvider.get (METRIC_PROPERTY_NAME));
        }
    
        /**
         * Method to calculate the rate of change of the monthly new user average
         * After calculating the metric, the implementation sets the "isCalculated" flag to true.
         * 
         * @since Gecko 3
         */
        public void calculate() {
    
            DBBean db = new DBBean();
            String trendChange = null;
            
            try {

                db.prepareCall ("{? = call application.getNewUserTrendChangeMetric()}");
                db.cstmt.registerOutParameter(1, java.sql.Types.INTEGER);

                db.executeCallable();

                int userCount = db.cstmt.getInt(1);

                if (userCount > 0) {
                    trendChange = "+" + net.project.util.Conversion.intToString(userCount);

                } else {
                    trendChange = net.project.util.Conversion.intToString(userCount);
                }

                isCalculated = true;

            } catch (SQLException sqle) {

                trendChange = ERROR_VALUE;
                isCalculated = false;

            } finally {
                db.release();
            }

            setValue (trendChange);
    }
}

