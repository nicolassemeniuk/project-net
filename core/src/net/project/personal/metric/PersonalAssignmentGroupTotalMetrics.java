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

 package net.project.personal.metric;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.metric.MetricsTypeException;
import net.project.metric.WeekAndMonthMetric;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.document.XMLDocument;

/**
 * Special Metric which represents the totals of PersonalAssignmentMetrics
 * <p>
 * The metrics include weekly and monthly QuantityAndWorkMetric values
 * <pre>
 * For Example:
 *
 * Item             Week Qty.   Week Work       Month Qty.  Month Work
 * Activity Totals    17           43h             64          212h
 * </pre>
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class PersonalAssignmentGroupTotalMetrics extends PersonalAssignmentMetrics {

    /** internal List containing all PersonalAssignmentMetrics to be summed */
    private List metricsSummationList = new LinkedList();


    /**
     * Creates a new Metrics
     * @param user
     */
    public PersonalAssignmentGroupTotalMetrics(User user) {
        super(user, PersonalAssignmentMetricsType.GROUP_TOTAL);
    }


    /**
     * Adds the specified PersonalAssignmentMetrics to an internal list for later calculation.
     * <p>
     * Note, summation calculations are not performed until load() is called.
     * @param metrics
     */
    public void addMetricsToTotal (PersonalAssignmentMetrics metrics) {

        this.metricsSummationList.add(metrics.getMetric());
    }


    /**
     * Iterates through internal collection of PersonalAssignmentMetrics, suming all qty and work values.
     * @throws PersistenceException
     * @throws MetricsTypeException
     */
    protected void loadMetrics() throws PersistenceException, MetricsTypeException {

        WeekAndMonthMetric result = new WeekAndMonthMetric();

        for (Iterator i = this.metricsSummationList.iterator(); i.hasNext();) {
            result = result.add ((WeekAndMonthMetric) i.next());
        }

        addMetric(result);
    }



    protected void addElements(XMLDocument xml) {

        xml.addElement("DisplayName", PropertyProvider.get("prm.personal.assignmentmetrics.type.grouptotal"));
        }


}
