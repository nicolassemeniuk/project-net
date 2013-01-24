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
 * This class represents a metric which will be used to calculate and display total unregistered users in the system.
 * By definition, the implementation of this metric will show a count of all persons in the application
 * with a user status of 'Unregistered'..
 */
public class UnregisteredUserMetric extends net.project.base.metric.Metric {

        private final String METRIC_PROPERTY_NAME = "prm.global.resource.metric.unregisteredusermetric";
    
        /**
         * Empty constructor used to create a new UnregisteredUserMetric
         * Implementation only calls super()
         */
        public UnregisteredUserMetric() {
            super();
        }
    
        /**
         * Method to initialize the Metric.
         * Should be implemented to at least set the name and the ID of the metric
         */
        protected void initialize() {
    
            setID (ResourceMetrics.UNREGISTERED_USER_METRIC);
            setName (PropertyProvider.get (METRIC_PROPERTY_NAME));
        }
    
        /**
         * Method to calculate the total number of "Unregistered" users in the application.
         * The method does a count of all "persons" in the application with a user status of 'Unregistered'
         * After calculating the metric, the implementation sets the "isCalculated" flag to true.
         * 
         * @since Gecko 3
         */
        public void calculate() {
    
            DBBean db = new DBBean();
            String userCount = null;
            String qstrGetCountUnregisteredUsers = "select count(person_id) unregisteredUserCount from pn_person_view " +
                "where user_status = '" + net.project.resource.PersonStatus.UNREGISTERED.getID() + "'";

            try {

                db.prepareStatement (qstrGetCountUnregisteredUsers);
                db.executePrepared();

                if (db.result.next()) {

                    userCount = db.result.getString ("unregisteredUserCount");
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

