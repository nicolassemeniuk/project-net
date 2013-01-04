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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.billing.bill;

import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Address;
import net.project.resource.Person;
import net.project.xml.XMLUtils;


/**
 * A ResponsibleParty is the party which is responsible for payment of this bill.
 * It would be included in a billing report and also in an invoice.
 * Currently we are assuming that the responsible party is the responsible user
 * for a license for which this bill has been created .
 * If there is no responsible  user
 * then the creator of the license would be considered as the responsible party.
 * In future we will need to keep track of the responsible party (not user) separately
 * and create an instance of this class from the corresponding responsible party information.
 */

public class ResponsibleParty
    implements IXMLPersistence {


    /**
     * The id of this responsible party.
     */
    private String id = null;

    /**
     * The name of the responsible party.
     */
    private String name = null;

    /**
     * The address for this party.
     */
    private Address address = null;

    /**
     * The email address for this bill.
     */
    private String email = null;

    /**
     * The bill id of the bill for which this party is responsible.
     */
    private String billID = null;

    /**
     * Creates an empty responsible party.
     */
    public ResponsibleParty() {
        // Do nothing
    }

    /**
     * Creates a responsible party for a given bill id.
     */
    public ResponsibleParty(String billID)
        throws PersistenceException {
        setBillID(billID);
        this.load();
    }

    /**
     * Creates a responsible party for a given bill id.
     * @param billID the id of the bill to initialize
     * @param db the database bean in which to perform the transaction
     * We must pass the db bean in order to ensure that the data that is written out
     * to the database in the same transaction is available to us.
     */
    public ResponsibleParty(String billID, DBBean db)
        throws PersistenceException {
        setBillID(billID);
        this.load(db);
    }

    /**
     * Sets the id for this responsible party.
     * @param id the bill's id
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     *	Gets the  id for this party.
     * @return id, the party's id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the bill id for this responsible party.
     * @param billID the bill's id
     */
    public void setBillID(String billID) {
        this.billID = billID;
    }

    /**
     *	Gets the bill id for this party.
     * @return billID, the party's bill id
     */
    public String getBillID() {
        return this.billID;
    }

    /**
     * Sets the name for this responsible party.
     * @param name the party's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *	Gets the party's name.
     * @return name, the party's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the address for this responsible party.
     * @param address the party's address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     *	Gets the address.
     * @return address, the party's address
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * Sets the email address for this responsible party.
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *	Gets the email address of the party.
     * @return email, the email address of the party
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Loads the responsible party information from the bill's id.
     * First we try to load the responsible person of the license otherwise we load the
     * original creator of the license.
     */
    public void load()
        throws PersistenceException {

        DBBean db = new DBBean();
        try {
            load(db);

        } finally {
            db.release();

        }
    }

    /**
     * Loads the responsible party information from the bill's id.
     * First we try to load the responsible person of the license otherwise we load the
     * original creator of the license.
     * @param db the DBBean in which to perform the transaction
     */
    public void load(DBBean db)
        throws PersistenceException {

        int index = 0;
        String personID = this.getID();

        String query = "select pl.responsible_user_id from pn_license pl where pl.license_id = " +
            " (select originating_license_id from pn_bill where bill_id = ?)";

        String alternateQuery = "select pb.originating_person_id from pn_bill pb where pb.bill_id = ?";

        try {
            if (personID == null) {

                db.prepareStatement(query);
                db.pstmt.setString(++index, this.billID);
                db.executePrepared();

                index = 0;

                while (db.result.next()) {
                    personID = db.result.getString("responsible_user_id");
                }

                if (personID == null) {
                    db.prepareStatement(alternateQuery);
                    db.pstmt.setString(++index, this.billID);
                    db.executePrepared();

                    while (db.result.next()) {
                        personID = db.result.getString("originating_person_id");
                    }
                }

                // Check if the person id is still null, in this case we assume that the user in session is the
                //responsible person
                if (personID == null) {
                    personID = net.project.security.SessionManager.getUser().getID();
                }
                // Set the person's ID
                this.setID(personID);
                // Load the responsible person's information
            }
            Person person = new Person(personID);
            person.load();

            this.name = person.getDisplayName();
            this.address = person.getAddress();
            this.email = person.getEmail();


        } catch (SQLException sqle) {
            throw new PersistenceException("ResponsibleParty.java: Error creating responsible party ." + sqle, sqle);
        }
    }

    /**
     Converts the object to XML representation.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     Converts the object to XML representation without the XML version tag.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<ResponsibleParty>\n");
        xml.append(getXMLProperties());
        xml.append("</ResponsibleParty>\n");

        return xml.toString();
    }

    /**
     Converts the object to XML representation without the XML version tag.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXMLProperties() {

        StringBuffer xml = new StringBuffer();
        net.project.security.User currentUser = net.project.security.SessionManager.getUser();

        xml.append("<PartyId>" + XMLUtils.escape(this.id) + "</PartyId>\n");
        xml.append("<PartyName>" + XMLUtils.escape(this.name) + "</PartyName>\n");
        xml.append("<EmailAddress>" + XMLUtils.escape(this.email) + "</EmailAddress>\n");
        if (this.address != null)
            xml.append(this.address.getXMLBody());

        return xml.toString();
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("ResponsibleParty");
            doc.addElement("PartyID", this.getID());
            doc.addElement("PartyName", this.getName());
            doc.addElement("Address", this.getAddress());
            doc.addElement("Email", this.getEmail());
            doc.addElement("BillID", this.getBillID());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this responsible party from the xml element.
     * @param element the xml element from which to populate this responsible party
     */
    protected void populate(org.jdom.Element element) {

        // Iterate over each child element of this ResponsibleParty element
        // and handle each one
        org.jdom.Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            childElement = (org.jdom.Element)it.next();

            if (childElement.getName().equals("PartyID")) {
                // ID contains a string
                this.setID(childElement.getTextTrim());

            } else if (childElement.getName().equals("PartyName")) {
                this.setName(childElement.getTextTrim());

            } else if (childElement.getName().equals("Address")) {
                // Need to fix this
                //this.setAddress(Address.create(childElement));
                this.setAddress(new Address());

            } else if (childElement.getName().equals("Email")) {
                this.setEmail(childElement.getTextTrim());

            } else if (childElement.getName().equals("BillID")) {
                this.setBillID(childElement.getTextTrim());

            }
        }

    }

    // Static Members

    /**
     * Creates a responsible party from the specified responsible party element.
     * @param responsiblePartyElement the xml element from which to create the
     * responsibleParty
     * @return the responsible party
     */
    public static ResponsibleParty create(org.jdom.Element responsiblePartyElement) {
        ResponsibleParty responsibleParty = new ResponsibleParty();
        responsibleParty.populate(responsiblePartyElement);
        return responsibleParty;
    }


}
