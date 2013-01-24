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
package net.project.license;

import java.io.IOException;

import net.project.billing.payment.PaymentInformation;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLException;

import org.jdom.Element;

/**
 * A <code>DistributableLicense</code> is a License that may be passed to
 * a remote system.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class DistributableLicense extends License {
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

    //
    // Instance members
    //

    /**
     * Converts XML representation of a license into a License object. A new
     * license key is generated; any license id values are ignored
     *
     * @param licenseXML the xml
     * @return the unmarshalled object
     * @throws XMLException if there was an error decoding the license xml into
     * a license.
     * @throws PersistenceException if there is an error accessing the database
     * to see if the license is unique, or to set the person responsible for this
     * license.
     * @throws LicenseException
     */
    public static License unmarshal(String licenseXML) throws XMLException, LicenseException, PersistenceException {
        License license = new License();

        if (licenseXML == null) {
            throw new NullPointerException("Error creating license; missing parameter");
        }

        // Build Document using SAX and JDOM
        try {
            // Build the document
            org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
            org.jdom.Document doc = builder.build(new java.io.StringReader(licenseXML));

            // Get the root element
            Element licenseElement = doc.getRootElement();

            // Get the version element
            // Note that we currently ignore it; that is, this method
            // handles all versions
            org.jdom.Element versionElement = licenseElement.getChild("Version");
            String licenseKey = licenseElement.getChild("LicenseKey").getText().trim();
            license.ensureLicenseKeyIsUnique(licenseKey);
            license.setKey(new LicenseKey(licenseKey));
            license.setTrial(net.project.util.Conversion.toBoolean(licenseElement.getChild("IsTrial").getText().trim()));
            license.setLicenseCertificate(new LicenseCertificate(LicenseCertificateBody.create((org.jdom.Element)licenseElement.getChild("LicenseCertificate").getChild("LicenseCertificateBody"))));
            license.setPaymentInformation(PaymentInformation.create((org.jdom.Element)licenseElement.getChild("PaymentInformation")));
            license.setResponsiblePersonID(net.project.security.SessionManager.getUser().getID());

        } catch (NullPointerException e) {
            // JDOM returns null when methods like getChild("Name") do not find
            // matching elements
            // It becomes remarkably tedious coding to check every single possible structure
            // problem
            // I guess using the validating parser would help
            throw new net.project.xml.XMLException("Error creating license from xml; invalid document structure");

        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating license from xml: " + e, e);

        } catch (IOException ioe) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating license from xml: " + ioe, ioe);
        }

        return license;
    }

    /**
     * Constructs the XML format of this license for distribution.
     * <b>Note:</b> This is UNSECURED.
     * A Distributable license has a LicenseKey.
     * @param xml the stringbuffer to which the XML representation will be
     * appended; this procedural-style of updating a parameter will make any
     * future methods that write to output streams more consistent
     * Technically i should specify a java.io.Reader here
     */
    void marshal(StringBuffer xml) {
        org.jdom.Element rootElement = new org.jdom.Element("License");

        // Add the version number for this XML schema
        rootElement.addContent(net.project.xml.XMLUtils.getVersionElement(CURRENT_XML_SCHEMA_VERSION_MAJOR, CURRENT_XML_SCHEMA_VERSION_MINOR));

        rootElement.addContent(new Element("IsTrial").addContent(net.project.util.Conversion.booleanToString(isTrial())));
        rootElement.addContent(new Element("LicenseKey").addContent(this.getKey().getValue()));
        rootElement.addContent(getPaymentInformation().getXMLElement());
        rootElement.addContent(getLicenseCertificate().getXMLElement());

        // Build the document and add it to the xml string
        org.jdom.Document doc = new org.jdom.Document(rootElement);
        xml.append(net.project.xml.XMLUtils.outputString(doc));
    }
}
