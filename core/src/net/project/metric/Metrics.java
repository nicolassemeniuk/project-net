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

 package net.project.metric;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.persistence.PersistenceException;
import net.project.xml.document.XMLDocument;

/**
 * Abstract <code>Metrics</code> object representing a collection of <code>SimpleMetric</code>(s) for a specified user.
 * <p>
 * Each concrete implementation of this <code>Metrics</code> object have an associated <code>MetricsType</code>.
 * The <code>MetricsType</code> is used when adding the <code>Metrics</code> to a <code>MetricsGroup</code>.
 * <p>
 * See: <code>TaskAssignmentMetrics</code> for an example implementation.
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public abstract class Metrics {

    /**
     * Internal List of loaded metrics
     */
    private List metrics = new LinkedList();

    /**
     * Represents to <code>MetricsType</code> of this Metrics
     */
    private final MetricsType type;


    /**
     * Creates a new Metrics with the specified type
     *
     * @param type of the <code>Metrics</code> object.
     */
    protected Metrics(MetricsType type) {
        this.type = type;
    }

    /**
     * Implementing classes must impelment metrics loading.
     *
     * @throws PersistenceException if there is a problem loading
     */
    protected abstract void loadMetrics() throws PersistenceException, MetricsTypeException;


    /**
     * Returns the <code>MetricsType</code> of this <code>Metrics</code>.
     * <p>The type is used by the <code>MetricsGroupTypeValidator</code> and in XSL for presentation control.
     *
     * @return the type of this Metrics
     */
    public MetricsType getType() {
        return type;
    }

    /**
     * Loads all the aggregate metrics for this assignment metric based on the user specified in the constructor.
     * <p>Calling load always clears the metrics collection.
     *
     * @throws IllegalStateException if there is not at least one metric loaded
     * @throws PersistenceException  if there is a problem loading
     */
    public final void load() throws PersistenceException {

        try {

            this.metrics.clear();
            loadMetrics();

            if (metrics.isEmpty()) {
                throw new IllegalStateException("Metrics.load() must load at least one SimpleMetric.");
            }

        } catch (MetricsTypeException me) {
            throw new PersistenceException ("MetricsLoad() failed due to a MetricsTypeException: ", me);
        }
    }


    /**
     * Returns a list of the SimpleMetrics managed by the internal collection.
     * @return
     */
    protected List getMetrics() {
        return this.metrics;
    }

    /**
     * Adds a <code>SimpleMetric</code> to the internal collection.
     *
     * @param metric
     */
    protected void addMetric(SimpleMetric metric) throws MetricsTypeException {
        validateMetric(metric);
        this.metrics.add(metric);
    }

    /**
     * Validates that the metric being added to the collection of of the correct type.
     * Must be implemented by the implementing class.
     * @param metric
     * @throws MetricsTypeException if the metric being added is not the correct type.
     */
    protected abstract void validateMetric (SimpleMetric metric) throws MetricsTypeException;


    /**
     * Given an XML document, adds XML for the internal collection of <code>SimpleMetric</code>(s).
     *
     * @param xml The XML document to be completed.
     */
    protected void addMetricsXML (XMLDocument xml) {
        Iterator metricsIterator = this.metrics.iterator();
        SimpleMetric metric;

        while (metricsIterator.hasNext()) {

            metric = (SimpleMetric) metricsIterator.next();
            xml.addElement(metric.getXMLDocument());
        }
    }

    /**
     * Must be implemented by the subclass
     *
     * @return a populated XML Document
     */
    public abstract XMLDocument getXMLDocument();

}

