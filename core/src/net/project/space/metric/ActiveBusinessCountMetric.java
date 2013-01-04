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
package net.project.space.metric;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;

/**
 * A metric which will be used to calculate and display total active businesses in the system.
 * By definition, the implementation of this metric will show a count of all businesses in the application
 * with an 'A' record status..
 */
public class ActiveBusinessCountMetric extends net.project.base.metric.Metric {

        private final String METRIC_PROPERTY_NAME = "prm.global.project.metric.totalbusinesscountmetric";
    
        /**
         * Empty constructor used to create a new TotalBusinessCountMetric.
         * Implementation only calls super()
         */
        public ActiveBusinessCountMetric() {
            super();
        }
    
        /**
         * Method to initialize the Metric.
         * Should be implemented to at least set the name and the ID of the metric
         */
        protected void initialize() {
    
            setID (SpaceMetrics.ACTIVE_BUSINESS_COUNT_METRIC);
            setName (PropertyProvider.get (METRIC_PROPERTY_NAME));
        }
    
        /**
         * Method to calculate the total number of "Active" business workspaces in the application.
         * After calculating the metric, the implementation sets the "isCalculated" flag to true.
         * @since Gecko 3
         */
        public void calculate() {
    
            DBBean db = new DBBean();
            String activeCount = null;
            String qstrGetCountActiveProjects = "select count (business_id) as activeCount from pn_business_view " +
                " where record_status = 'A'";

            try {

                db.prepareStatement (qstrGetCountActiveProjects);
                db.executePrepared();

                if (db.result.next()) {

                    activeCount = db.result.getString ("activeCount");
                    isCalculated = true;

                } else {
                    activeCount = ERROR_VALUE;
                }

            } catch (SQLException sqle) {

                activeCount = ERROR_VALUE;
                isCalculated = false;   

            } finally {
                db.release();
            }

            setValue (activeCount);
    }
}

