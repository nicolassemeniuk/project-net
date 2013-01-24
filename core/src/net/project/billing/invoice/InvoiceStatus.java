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
package net.project.billing.invoice;

import java.util.Iterator;

import org.jdom.Element;

/**
 * Each ledger entry has a invoice status that indicates
 *
 * @author Vishwajeet
 */
public class InvoiceStatus {

    private String id = null;

    /**
     * Creates an empty InvoiceStatus.
     */
    public InvoiceStatus() {
        // Do nothing
    }

    /**
     * Creates an InvoiceStatus.
     */
    public InvoiceStatus(String id) {
        this.id = id;
    }

    /**
     * Sets this invoice status's id.
     * @param id the invoice status id
     * @see #getID
     */
    protected void setID(String id) {
        this.id = id;
    }

    /**
     * Returns this Invoice Status's id.
     * @return the invoice status id
     * @see #setID
     */
    public String getID() {
        return this.id;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("InvoiceStatus");
            doc.addElement("StatusID", this.getID());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    // Static members
    /**
     * Populates this invoice status from the xml element.
     * @param element the xml element from which to populate this invoice status
     */
    protected void populate(Element element) {

        // Iterate over each child element of this InvoiceStatus element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            childElement = (Element)it.next();

            if (childElement.getName().equals("ID")) {
                // ID contains a string
                this.setID(childElement.getTextTrim());

            }
        }

    }

    // Static Members

    /**
     * Creates a invoice status from the specified invoice status element.
     * @param invoiceStatusElement the xml element from which to create the
     * invoiceStatus
     * @return the invoice status
     */
    public static InvoiceStatus create(org.jdom.Element invoiceStatusElement) {
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.populate(invoiceStatusElement);
        return invoiceStatus;
    }

    public static final InvoiceStatus NOT_INVOICED = new InvoiceStatus("100");
    public static final InvoiceStatus INVOICED = new InvoiceStatus("200");


}
