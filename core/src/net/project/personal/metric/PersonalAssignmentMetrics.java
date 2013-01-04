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

import net.project.metric.Metrics;
import net.project.metric.MetricsType;
import net.project.metric.MetricsTypeException;
import net.project.metric.SimpleMetric;
import net.project.metric.SimpleMetricType;
import net.project.metric.WeekAndMonthMetric;
import net.project.security.User;
import net.project.xml.document.XMLDocument;

/**
 * Abstract class to model personal assignment metrics.
 * <p>
 * By definition, a PersonalAssignmentMetrics is a subclass of Metrics and can only have one SimpleMetric entry:
 * A WeekAndMonthMetric.  These rules are enforced by the impelmentation of "validateMetric".
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public abstract class PersonalAssignmentMetrics extends Metrics {

    /** The user for which to load metrics */
    private final User user;

    /** The assignment type (sub-type) of the metric. */
    private final PersonalAssignmentMetricsType assignmentType;

    /**
     * Create a new PersonalAssignmentMetric for the specified user and assignment type.
     * <p>
     * Implementation of constructor assigns the MetricsType of the Metrics implementation to MetricsType.PERSONAL_ASSIGNMENT.
     * @param user user for which to load the metric
     * @param assignmentType
     */
    public PersonalAssignmentMetrics(User user, PersonalAssignmentMetricsType assignmentType) {
        super(MetricsType.PERSONAL_ASSIGNMENT);
        this.user = user;
        this.assignmentType = assignmentType;
    }

    /**
     * Returns the user set by the constructor.
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns a loaded WeekAndMonthMetric.
     * <p>
     * Note, this method assumes that the Metrics object is already loaded.  This is enforced by the
     * Implementing classes using private constructors and static factory methods for instantiation.
     * <p>
     * Because the <code>PersonalAssignmentMetrics</code> only ever allows one WeekAndMonthMetric to be loaded,
     * this method is implemented to get the first entry from the internal <code>Metrics</code> list.
     * @return Populated week and month metric
     */
    public WeekAndMonthMetric getMetric() {

        if (!isLoaded()) {
            throw new IllegalStateException ("The PersonalAssignmentsMetric must be loaded before calling getMetric()");
        }

        return (WeekAndMonthMetric) getMetrics().get(0);
    }

    /**
     * Returns a boolean indicating whether this Metrics object is loaded.
     * <p>
     * Due to the constraints on this type of Metrics (see validateMetric), we can determine
     * "isLoaded" by whether or not the internal List (from Metrics) has exactly one element.
     * @return
     */
    private boolean isLoaded() {

        boolean isLoaded = false;

        if (getMetrics().size() == 1) {
            isLoaded = true;
        } else {
            isLoaded = false;
        }

        return isLoaded;
    }


    /**
     * Validates that the Metric being added to the collection is of the correct type.
     * <p>
     * The implementation differs for each type of Metrics, but for this Metrics implementation
     * an entry is valid if it is of type WeekAndMonthMetric and there is at most one entry in the collection.
     * @param metric
     * @throws MetricsTypeException
     */
    protected void validateMetric (SimpleMetric metric) throws MetricsTypeException {

        if (!SimpleMetricType.WEEKANDMONTH.equals(metric.getType())) {
            throw new MetricsTypeException ("PersonalAssignmentMetrics only support metric(s) of type WeekAndMonthMetric");

        } else if (getMetrics().size()+1 != 1) {
            // basically this is say, that if after adding the metric I will add now
            // the size is not equal to one, illegal state for this metric.
            throw new IllegalStateException ("Only one WeekAndMonthMetric can be added to a PersonalAssignmentMetrics");

        } else {
            // do nothing
        }

    }

    /**
     * Must be implemented to include implementation specific XML.
     * @param xml
     */
    protected abstract void addElements (XMLDocument xml);

    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */
    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("PersonalAssignmentMetrics");
        xml.addAttribute ("type", this.assignmentType.getID());
        addElements (xml);
        addMetricsXML(xml);
        xml.endElement();

        return (xml);
    }
}
