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

import java.util.HashMap;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * This class represents a collection of Metric objects and contains a number
 * of convenience methods for looking up specific metrics and getting an XML
 * rendition of the metrics.
 * 
 * @author Phil Dixon
 */

public class MetricCollection extends HashMap implements IXMLPersistence {

    /** the name of the MetricCollection.  By default will simply be "MetricCollection" */
     protected String name = "MetricCollection";
   
    public MetricCollection() {
        super();
        registerMetrics();
    }

    protected void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Metric getMetric (String id) {
        return ((Metric) get (id));
    }

    public String getMetricName (String id) {

        Metric metric = getMetric (id);
        return (metric != null) ? metric.getName() : "NULL";
    }

    public String getMetricValue (String id) {

        Metric metric = getMetric (id);
        return (metric != null) ? metric.getValue() : "NULL";
    }


    protected void registerMetrics() {
        // empty implementation
    }

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
        Iterator iterator = this.values().iterator();
        Metric metric = null;

        try {

            xml.startElement ("MetricCollection");

            // the name of the collection.  By default will be MetricCollection unless overridden
            xml.addElement ("Name", getName());

            while (iterator.hasNext()) {
                
                metric = (Metric) iterator.next();
                xml.addElement (metric.getXMLDocument());
            }

            xml.endElement ();

        } catch (XMLDocumentException xde) {
            // simply return an empty XML structure
        }

        return xml;
    }
}

    
    



