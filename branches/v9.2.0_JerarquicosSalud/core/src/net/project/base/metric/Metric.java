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
package net.project.base.metric;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * This abstract class represents a metric which will be used to calculate and display
 * interesting metrics related information in the application.  This class must be extended
 * to support the particular "Metric" of interest (ie. ActiveUserMetric)
 */

public abstract class Metric implements IMetric, IXMLPersistence {

    /** the internal id of this metric -- used for looking up metrics from a collection */
    private String id = null;

    /** the display label of a metric (should come from a token) */
    private String name = null;

    /** the calculated value of this metric */
    private String value = null;

    /** indicates whether or not the value has already been calculated */
    protected boolean isCalculated = false;

    /** A special status for the "value" field if there was an error in the calculation method */
    protected static final String ERROR_VALUE = "Error Calculating Value";

    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Creates an empty metric.
     * Also calls the abstract initialize method which should initialize the 
     * ID of the metric (from a constant) and the Name of the metric (from a token)
     */
    public Metric() {
        initialize();
    }

    /* -------------------------------  Getters/Setters  ------------------------------- */

    /**
     * Return the internal ID of the Metric.
     * Will likely be statically set by the concrete implementation of the Metric object.
     * 
     * @return The ID of the metric
     * @since Gecko 3
     */
    public String getID() {
        return this.id;
    }

    /**
     * Set the internal ID of the metric.
     * This will generally be in the form of our token naming conventions.
     * For example:  prm.resource.activeuser
     * 
     * @param name   The ID of the metric
     */
    protected void setID (String id) {
        this.id = id;
    }

    /**
     * Return the display label of the Metric.
     * This value should ideally be set to a property (token) by the concrete implementor
     * 
     * @return The display label of the metric
     * @since Gecko 3
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name (label) of the metric.
     * This name should come from a token
     * 
     * @param name   The name of the metric
     */
    protected void setName (String name) {
        this.name = name;
    }

    /**
     * Return the value of the Metric (as a string)
     * The value returned will be calculated if the Metric has not yet been calculated.  
     * However, after the first time the metric is calculated, you must call recalculate() to refresh the value
     * 
     * @return The value of the metric
     * @since Gecko 3
     */
    public String getValue() {

        if (!this.isCalculated) {
            calculate();
        }

        return this.value;
    }

    /**
     * Set the value (typically calculated) of the metric.
     * 
     * @param name   The value of the metric
     */
    protected void setValue (String value) {
        this.value = value;
    }

    /**
     * Returns true if the metric has already been calculated
     * 
     * @return Returns true if the metric has already been calculated
     */
    public boolean isCalculated() {
        return this.isCalculated;
    }

    /* -------------------------------  Metrics Calculation Methods  ------------------------------- */
    
    /**
     * Method to calculate the "value" of this Metric. 
     * Since the Metric object can be used to represent anything, the implementation
     * is left up to the subclass.  
     * The implementation must ensure that the boolean isCalculate flag is set to true
     * after calculating the metric.
     * @since Gecko 3
     */
    public abstract void calculate();

    /**
     * Method to initialize the Metric.  
     * Should be implemented to at least set the name and the ID of the metric
     */
    protected abstract void initialize();

    /* -------------------------------  IXMLPersistence Methods  ------------------------------- */

    /**
     * Returns the XML representation of this search result.
     * Includes the xml Version tag.
     * Constructed by calling <code>getXMLDocument().getXMLString()</code>.
     * @return the xml representation
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML representation of this search result.
     * Does not include the xml Version tag.
     * Constructed by calling <code>getXMLDocument().getXMLBodyString()</code>.
     * @return the xml representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns an XMLDocument containing the Search Result elements
     * and any subclass specific elements.
     * @return the XMLDocument.
     */
    protected XMLDocument getXMLDocument() {

        XMLDocument xml = new XMLDocument();

        try {
            xml.startElement ("Metric");

            xml.addElement ("ID", getID());
            xml.addElement ("Name", getName());
            xml.addElement ("Value", getValue());

            xml.endElement ();

        } catch (XMLDocumentException xde) {
            // simply return an empty XML structure
        }

        return xml;
    }
}
