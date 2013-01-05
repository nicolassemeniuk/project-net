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

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;

/**
 * A <code>MetricsGroup</code> represents a collection of <code>Metrics</code> objects of the same <code>MetricsType</code>.
 * <p>
 * The <code>MetricsType</code> specified for each MetricsGroup identifies which <code>MetricsType</code> is supported.
 * For each <code>MetricsType</code> there will be a concrete implementation of this class.
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public abstract class MetricsGroup implements IXMLPersistence {

    /**
     * The MetricsType supported by this MetricsGroup.
     * <p>
     * Note the MetricsType specifies the MetricsTypes which can be supported by this MetricsGroup.
     */
    private final MetricsType type;
    /**
     * Collection of <code>Metrics</code> objects of <code>Type</code>.
     * <p>Due to the Validation of MetricsType, this object will only ever support one MetricsType at a time.
     */
    private List metrics = new LinkedList();


    /**
     * Create a new MetricsGroup object of the specified type
     *
     * @param type MetricsType of this Grouping.
     */
    public MetricsGroup(MetricsType type) {
        this.type = type;
    }

    /**
     * Adds a validated <code>Metrics</code> object to the collection.
     * <p>
     * When the metric is added, it's <code>MetricsType</code> is validated at runtime
     * throws a MetricsTypeException if the <code>Metrics</code> is not the supported MetricsType
     * (specified by the MetricsType).
     *
     * @param metric <code>Metrics</code> to be added.
     * @throws MetricsTypeException throws a MetricsTypeException if the <code>Metrics</code> is not the supported MetricsType
     */
    protected void add(Metrics metric) throws MetricsTypeException {

        validateMetrics(metric);
        this.metrics.add(metric);


    }

    /**
     * Validates that the <code>Metrics</code> being added to the group is of the correct <code>MetricsType</code>.
     * <p>
     * 
     * @param metrics
     * @throws MetricsTypeException if the <code>Metrics</code> is not the correct type.
     */
    private void validateMetrics(Metrics metrics) throws MetricsTypeException {

        if (!MetricsType.PERSONAL_ASSIGNMENT.equals (metrics.getType())) {
            throw new MetricsTypeException("This MetricsGroup only supports metrics of type: " + metrics.getType().getID());
        } else {
            // do nothing
        }
    }

    /**
     * Returns the type of this MetricsGroup
     *
     * @return the type of this MetricsGroup
     */
    public MetricsType getType() {
        return this.type;
    }

    /**
     * Given an XML document, adds XML for all embedded SimpleMetric(s).
     *
     * @param xml The XML document to be completed.
     */
    protected void addXMLElements(XMLDocument xml) {
        Iterator metricsIterator = this.metrics.iterator();
        Metrics metrics;

        while (metricsIterator.hasNext()) {

            metrics = (Metrics) metricsIterator.next();
            xml.addElement(metrics.getXMLDocument());
        }
    }

    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */

    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("MetricsGroup");
        addXMLElements(xml);
        xml.endElement();

        return (xml);
    }

    /**
     * Returns a Strng with a well-formed XML Document represtation of this MetricsGroup
     *
     * @return XML string represtation of this MetricsGroup
     */
    public String getXML() {
        return (getXMLDocument().getXMLString());
    }

    /**
     * Returns a Strng with a the XML body represtation of this MetricsGroup
     *
     * @return XML string represtation of this MetricsGroup
     */
    public String getXMLBody() {
        return (getXMLDocument().getXMLBodyString());
    }


}
