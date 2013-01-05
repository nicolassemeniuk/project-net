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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.billing.ledger.LedgerEntry;
import net.project.xml.XMLUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides access to the ledger entries in an invoice.
 * This object is always stored encrypted.
 */
public class InvoiceBody implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * The current XML schema major version number for this object structure.
     * This is written into all xml produced to help determine the structure
     */
    private static final int CURRENT_XML_SCHEMA_VERSION_MAJOR = 1;

    /**
     * The current XML schema minor version number for this object structure.
     * This is written into all xml produced to help determine the structure
     */
    private static final int CURRENT_XML_SCHEMA_VERSION_MINOR = 0;

    /**
     *  The ID list of entries contained in this invoice boody.
     */
    private ArrayList ledgerEntryIDList = new ArrayList();

    /**
     * Converts XML representation of invoice body into a InvoiceBody
     * object.
     * @param invoiceBodyXML the xml
     * @return the unmarshalled object
     */
    static InvoiceBody unmarshal(String invoiceBodyXML)
        throws net.project.xml.XMLException {

        InvoiceBody body = null;

        if (invoiceBodyXML == null) {
            throw new NullPointerException("Error creating invoice body; missing parameter");
        }

        try {
            // Build the document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new java.io.StringReader(invoiceBodyXML));

            // Create the invoice body from the root element
            body = create(doc.getRootElement());

        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating invoice from xml: " + e, e);

        } catch (IOException ioe) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating invoice from xml: " + ioe, ioe);
        }

        return body;
    }

    /**
     * Creates a InvoiceBody from a JDOM Element.
     * @param invoiceElement the JDOM Element for this InvoiceBody
     * @return the body built from the element
     */
    static InvoiceBody create(org.jdom.Element invoiceElement)
        throws net.project.xml.XMLException {

        InvoiceBody body = null;

        // Build Document using SAX and JDOM
        try {
            // Get the version element
            // Note that we currently ignore it; that is, this method
            // handles all versions
            Element versionElement = invoiceElement.getChild("Version");

            // Get LedgerEntryCollection
            List ledgerEntryCollection = new ArrayList();
            for (Iterator it = invoiceElement.getChild("LedgerEntryCollection").getChildren().iterator();
                 it.hasNext();) {

                // Add a new LedgerEntry object built from the ledger entry element
                ledgerEntryCollection.add(LedgerEntry.create((Element)it.next()));
            }

            // Now build the invoice body
            body = new InvoiceBody();
            body.ledgerEntryCollection = ledgerEntryCollection;

        } catch (NullPointerException e) {
            // JDOM returns null when methods like getChild("Name") do not find
            // matching elements
            // It becomes remarkably tedious coding to check every single possible structure
            // problem
            // I guess using the validating parser would help
            throw new net.project.xml.XMLException("Error creating invoice from xml; invalid document structure");

        }
        return body;
    }

    //
    // Instance members
    //

    /** The ledger entries contained in this invoice. */
    private List ledgerEntryCollection = new ArrayList();

    /**
     * Creates an empty InvoiceBody
     */
    InvoiceBody() {
        // Nothing
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append(this.ledgerEntryCollection).append("\n");
        return result.toString();
    }


    /**
     * Adds the specified license model to this license certificate.
     * @param ledgerEntry the license model to add
     */
    void addLedgerEntry(LedgerEntry ledgerEntry) {
        this.ledgerEntryIDList.add(ledgerEntry.getID());
        getLedgerEntryCollection().add(ledgerEntry);
    }

    /**
     * Gets a list of ledger entry ids contained in this invoice.
     * @return ledgerEntryIDList, the ledger entry id's list
     */
    public ArrayList getLedgerEntryIDList() {

        return this.ledgerEntryIDList;
    }

    /**
     * Returns the current ledger entries in this invoicebody.
     * @return the collection of ledger entries; each element is of type
     * <code>LedgerEntry</code>
     * @see net.project.billing.ledger.LedgerEntry
     */
    Collection getLedgerEntryCollection() {
        return this.ledgerEntryCollection;
    }

    /**
     * Returns the XML format of this invoice body suitable for storage.
     * Any changes to this MUST be handled during loading; it will be impossible
     * to perform data conversions during upgrades. <br>
     * <b>Note:</b> This is UNSECURED; it must not be displayed or otherwise
     * returned to the view layer for presentation (even with an XSL stylesheet)
     * @param xml the stringbuffer to which the XML representation will be
     * appended; this procedural-style of updating a parameter will make any
     * future methods that write to output streams more consistent
     */
    void marshal(StringBuffer xml) {
        Element rootElement = getXMLElement();
        // Build the document and add it to the xml string
        Document doc = new Document(rootElement);
        xml.append(XMLUtils.outputString(doc));

    }

    org.jdom.Element getXMLElement() {
        Element rootElement = new Element("InvoiceBody");

        // Add the version number for this XML schema
        rootElement.addContent(XMLUtils.getVersionElement(CURRENT_XML_SCHEMA_VERSION_MAJOR, CURRENT_XML_SCHEMA_VERSION_MINOR));

        // Add all ledger entries in collection
        Element ledgerEntryCollectionElement = new Element("LedgerEntryCollection");
        for (Iterator it = getLedgerEntryCollection().iterator(); it.hasNext();) {
            ledgerEntryCollectionElement.addContent(((LedgerEntry)it.next()).getXMLElement());
        }
        rootElement.addContent(ledgerEntryCollectionElement);

        return rootElement;
    }

    //
    // Unit Test
    //

//     public static void main(String[] args) {
//         try {
//             LicenseCertificateBody certBody = new LicenseCertificateBody();
//             certBody.setAssociatedLicenseKey(LicenseKey.createLicenseKey());
//             certBody.addLicenseModel(new net.project.license.model.UsageLimit(1));
//             certBody.addLicenseModel(new net.project.license.model.TimeLimit(30));
//             certBody.addLicenseCost(new net.project.license.cost.BaseCost(new net.project.base.money.Money("100")));
//             certBody.addLicenseCost(new net.project.license.cost.MaintenanceCost(new net.project.base.quantity.Percentage("17.5")));
//
//             StringBuffer serialized = new StringBuffer();
//             certBody.marshal(serialized);
//             System.out.println("New certificate body: 1 user; 30 day; base cost=100; maintenance cost=17.5%");
//             System.out.println("Marshalled object:");
//             System.out.println(serialized.toString());
//             System.out.println("And the reverse:");
//             System.out.println(LicenseCertificateBody.unmarshal(serialized.toString()));
//             System.out.println("Serialized again: ");
//             StringBuffer again = new StringBuffer();
//             LicenseCertificateBody.unmarshal(serialized.toString()).marshal(again);
//             System.out.println(again.toString());
//
//
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//
//     }

}

