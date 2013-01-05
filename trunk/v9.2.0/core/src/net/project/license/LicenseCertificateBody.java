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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.license.cost.LicenseCost;
import net.project.license.model.LicenseModel;
import net.project.resource.Address;
import net.project.resource.Person;
import net.project.resource.PersonStatus;
import net.project.security.SessionManager;
import net.project.xml.XMLException;
import net.project.xml.XMLUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides access to the license models and costs supported by a license.
 * This object is always stored encrypted.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class LicenseCertificateBody implements java.io.Serializable {

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
     * Converts XML representation of certificate body into a LicenseCertificateBody
     * object.
     * @param certificateBodyXML the xml
     * @return the unmarshalled object
     */
    static LicenseCertificateBody unmarshal(String certificateBodyXML) 
            throws net.project.xml.XMLException {

        LicenseCertificateBody body = null;

        if (certificateBodyXML == null) {
            throw new NullPointerException("Error creating license certificate body; missing parameter");
        }

        try {
            // Build the document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new java.io.StringReader(certificateBodyXML));

            // Create the certificate body from the root element
            body = create(doc.getRootElement());
        
        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating license certificate from xml: " + e, e);
        
        }catch (IOException ioe) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating license certificate from xml: " + ioe, ioe);
        }

        return body;
    }

    /**
     * Creates a LicenseCertificateBody from a JDOM Element.
     *
     * @param licenseCertificateElement the JDOM Element for this CertificateBody
     * @return the body built from the element
     * @throws XMLException if any NullPointerException of LicensingErrors occur
     * while unmarshalling this XML into a LicenseCertificateBody.
     */
    static LicenseCertificateBody create(org.jdom.Element licenseCertificateElement) 
            throws XMLException {

        LicenseCertificateBody body = null;

        // Build Document using SAX and JDOM
        try {    
            // Get the version element
            // Note that we currently ignore it; that is, this method
            // handles all versions
            Element versionElement = licenseCertificateElement.getChild("Version");

            // Create associated license key; the AssociatedLicenseKey
            // element's first child is the license key
            Element associatedLicenseKeyElement = licenseCertificateElement.getChild("AssociatedLicenseKey");
            LicenseKey associatedKey = LicenseKey.create((Element) associatedLicenseKeyElement.getChildren().get(0));

            // Get LicenseModelCollection
            List licenseModelCollection = new ArrayList();
            for (Iterator it = licenseCertificateElement.getChild("LicenseModelCollection").getChildren().iterator();
                it.hasNext();) {

                // Add a new LicenseModel object built from the license model element
                licenseModelCollection.add(LicenseModel.create((Element) it.next()));
            }

            // Get LicenseCostCollection
            List licenseCostCollection = new ArrayList();
            for (Iterator it = licenseCertificateElement.getChild("LicenseCostCollection").getChildren().iterator();
                it.hasNext();) {

                // Add a new LicenseCost object built from the license cost element
                licenseCostCollection.add(LicenseCost.create((Element) it.next()));
            }

            Element personElement = licenseCertificateElement.getChild("Purchaser");
            Person person = null;
            if (personElement != null) {
                person = new Person();
                person.setAddress(new Address());
                person.setStatus(PersonStatus.UNREGISTERED);
                person.setLicensed(false);

                person.setFirstName(personElement.getChild("FirstName").getText());
                person.setLastName(personElement.getChild("LastName").getText());
                person.setDisplayName(personElement.getChild("DisplayName").getText());
                person.setEmail(personElement.getChild("Email").getText());
                person.setOfficePhone(personElement.getChild("PhoneNumber").getText());

                Element addressElement = personElement.getChild("Address");
                if (addressElement != null) {
                    //Set the current user as the creator of this address record.
                    person.getAddress().setUser(SessionManager.getUser());

                    person.setAddress1(addressElement.getChild("Address1").getText());
                    person.setAddress2(addressElement.getChild("Address2").getText());
                    person.setCity(addressElement.getChild("City").getText());
                    person.setState(addressElement.getChild("StateID").getText());
                    person.setZipcode(addressElement.getChild("Zip").getText());
                    person.setCountry(addressElement.getChild("CountryID").getText());
                }
            }


            // Now build the license certificate body
            body = new LicenseCertificateBody();
            body.setAssociatedLicenseKey(associatedKey);
            body.licenseModelCollection = licenseModelCollection;
            body.licenseCostCollection = licenseCostCollection;
            body.purchaser = person;

        } catch (NullPointerException e) {
            // JDOM returns null when methods like getChild("Name") do not find
            // matching elements
            // It becomes remarkably tedious coding to check every single possible structure
            // problem
            // I guess using the validating parser would help
            throw new net.project.xml.XMLException("Error creating license certificate from xml; invalid document structure");

        } catch (LicenseException e) {
            // Problem building a component of the license certificate body
            throw new net.project.xml.XMLException("Error creating license certificate: " + e, e);

        }

        return body;
    }

    //
    // Instance members
    //

    /** The license models in use by this license certificate. */
    private List licenseModelCollection = new ArrayList();

    /** The license costs in use by this license certificate. */
    private List licenseCostCollection = new ArrayList();

    /**
     * Person who purchased the license.  This is a person object which has
     * not been stored in the database.  (There were too many complications
     * about the Person having to be a user that we needed to avoid for now.)
     */
    private Person purchaser;

    /**
     * The license key associated with the license of which this certificate
     * is a part.  This is to combat "cut-and-paste" of certificates; each
     * certificate is bound to a key during storage.
     */
    private LicenseKey associatedLicenseKey = null;

    /**
     * Creates an empty LicenseCertificateBody.
     */
    LicenseCertificateBody() {
        // Nothing
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append(this.associatedLicenseKey).append("\n");
        result.append(this.licenseModelCollection).append("\n");
        result.append(this.licenseCostCollection).append("\n");
        return result.toString();
    }


    /**
     * Adds the specified license model to this license certificate.
     * @param model the license model to add
     */
    void addLicenseModel(LicenseModel model) {
        getLicenseModelCollection().add(model);
    }


    /**
     * Returns the current license models.
     * @return the collection of license models; each element is of type
     * <code>LicenseModel</code>
     * @see net.project.license.model.LicenseModel
     */
    Collection getLicenseModelCollection() {
        return this.licenseModelCollection;
    }


    /**
     * Adds the specified license cost to this license certificate.
     * @param cost the license cost to add
     */
    void addLicenseCost(LicenseCost cost) {
        getLicenseCostCollection().add(cost);
    }

    /**
     * Returns the current license costs.
     * @return the collection of license costs; each element is of type
     * <code>LicenseCost</code>
     * @see net.project.license.cost.LicenseCost
     */
    Collection getLicenseCostCollection() {
        return this.licenseCostCollection;
    }


    /**
     * Specifies the license key that this certificate is associated with.
     * This is to prevent "cut-and-paste" duplication of certificate data
     * in the database; It is now bound to a key.
     */
    void setAssociatedLicenseKey(LicenseKey key) {
        this.associatedLicenseKey = key;
    }

    /**
     * Returns the license key associated with the license that this certificate
     * is part of.
     * @return the key
     */
    LicenseKey getAssociatedLicenseKey() {
        return this.associatedLicenseKey;
    }

    /**
     * Returns the XML format of this certificate suitable for storage.
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
        Element rootElement = new Element("LicenseCertificateBody");

        // Add the version number for this XML schema
        rootElement.addContent(XMLUtils.getVersionElement(CURRENT_XML_SCHEMA_VERSION_MAJOR, CURRENT_XML_SCHEMA_VERSION_MINOR));

        // Add the associated license key xml
        if (getAssociatedLicenseKey() != null) {
            rootElement.addContent(new Element("AssociatedLicenseKey").addContent(getAssociatedLicenseKey().getXMLElement()));
        }

        // Add all license models in collection
        Element licenseModelCollectionElement = new Element("LicenseModelCollection");
        for (Iterator it = getLicenseModelCollection().iterator(); it.hasNext();) {
            licenseModelCollectionElement.addContent( ((LicenseModel) it.next()).getXMLElement() );
        }
        rootElement.addContent(licenseModelCollectionElement);

        // Add all license costs in collection
        Element licenseCostCollectionElement = new Element("LicenseCostCollection");
        for (Iterator it = getLicenseCostCollection().iterator(); it.hasNext();) {
            licenseCostCollectionElement.addContent( ((LicenseCost) it.next()).getXMLElement() );
        }
        rootElement.addContent(licenseCostCollectionElement);

        //If a purchaser was added, save them
        if (purchaser != null) {
            Element personElement = new Element("Purchaser");
            personElement.addContent(new Element("FirstName").addContent(getPurchaser().getFirstName()));
            personElement.addContent(new Element("LastName").addContent(getPurchaser().getLastName()));
            personElement.addContent(new Element("DisplayName").addContent(getPurchaser().getDisplayName()));
            personElement.addContent(new Element("Email").addContent(getPurchaser().getEmail()));
            personElement.addContent(new Element("PhoneNumber").addContent(getPurchaser().getOfficePhone()));

            Element addressElement = new Element("Address");
            addressElement.addContent(new Element("Address1").addContent(getPurchaser().getAddress1()));
            addressElement.addContent(new Element("Address2").addContent(getPurchaser().getAddress2()));
            addressElement.addContent(new Element("City").addContent(getPurchaser().getCity()));
            addressElement.addContent(new Element("StateID").addContent(getPurchaser().getState()));
            addressElement.addContent(new Element("Zip").addContent(getPurchaser().getZipcode()));
            addressElement.addContent(new Element("CountryID").addContent(getPurchaser().getCountry()));

            personElement.addContent(addressElement);
            rootElement.addContent(personElement);
        }


        return rootElement;
    }

    public void setPurchaser(Person purchaser) {
        this.purchaser = purchaser;
    }

    public Person getPurchaser() {
        return purchaser;
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
