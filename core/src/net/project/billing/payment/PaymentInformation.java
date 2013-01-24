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
package net.project.billing.payment;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides payment information, used for paying for application licenses.
 */
public class PaymentInformation implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Creates a PaymentInformation from a JDOM Element.
     * @param paymentInformationElement the JDOM Element for this paymentInformation
     * @return the payment information built from the element
     */
    public static PaymentInformation create(org.jdom.Element paymentInformationElement) 
            throws net.project.xml.XMLException {

        PaymentInformation payment = new PaymentInformation();

        // Build Document using SAX and JDOM
        try {    
            // Get the version element
            // Note that we currently ignore it; that is, this method
            // handles all versions
            org.jdom.Element versionElement = paymentInformationElement.getChild("Version");
            String paymentID = paymentInformationElement.getChildTextTrim("PaymentID");
            if (paymentID != null && paymentID.length() > 0) {
                payment.setID(paymentID);
            }
            payment.setPaymentModel(PaymentModel.create((org.jdom.Element) paymentInformationElement.getChild("PaymentModel").getChildren().get(0)));
        
        } catch (NullPointerException e) {
            // JDOM returns null when methods like getChild("Name") do not find
            // matching elements
            // It becomes remarkably tedious coding to check every single possible structure
            // problem
            // I guess using the validating parser would help
            throw new net.project.xml.XMLException("Error creating payment information from xml; invalid document structure");

        } catch (PaymentException e) {
            throw new net.project.xml.XMLException("Error creating payment information from xml: " + e, e);
        }

        return payment;
    }

    /** The id of this payment information. */
    private String paymentID = null;

    /** The payment model for this payment information. */
    private PaymentModel paymentModel = null;

    /** The Party that owns this payment information. */
    private Party party = null;

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("paymentID: ").append(getID()).append("\n");
        result.append(getPaymentModel()).append("\n");
        return result.toString();
    }
    
    /**
     * Sets the id of this payment information.
     * @param id the id of this payment information
     */
    public void setID(String id) {
        this.paymentID = id;
    }


    /**
     * Returns the id of this payment information.
     * @return the id of this payment information
     */
    public String getID() {
        return this.paymentID;
    }


    /**
     * Sets the payment model for this payment information.
     * @param model the payment model
     */
    public void setPaymentModel(PaymentModel model) {
        this.paymentModel = model;
    }


    /**
     * Returns the payment model for this payment information.
     * @return the payment model
     */
    public PaymentModel getPaymentModel() {
        return this.paymentModel;
    }


    /**
     * Returns the party for this payment information.
     * @return the party
     */
    public Party getParty() {
        return this.party;
    }


    /**
     * Loads payment information.
     * Assumes the current id is set.
     * @throws PersistenceException if there is a problem loading
     * @see #setID
     */
    public void load() throws PersistenceException {
        
        // Build query to load payment info
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append(PaymentDAO.getQueryLoadPaymentInformation());
        loadQuery.append("where payment_id = ? ");

        // Load payment info
        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            if (db.result.next()) {
                // Populate this payment information from the result
                // This causes the model and party to be loaded
                PaymentDAO.populatePaymentInformation(db.result, this);
            
            } else {
                throw new PersistenceException("PaymentInformation load operation failed: No data for id");

            }
        
        } catch (SQLException sqle) {
            throw new PersistenceException("PaymentInformation load operation failed: " + sqle, sqle);

        } finally {
            db.release();
            
        }

    }

    /**
     * Loads the payment model with the specified id into this
     * payment information.
     * @param modelID the id of the payment model to load
     * @throws PersistenceException if there is a problem loading the model
     */
    protected void loadModel(String modelID) throws PersistenceException {
        PaymentModel model = null;

        try {
            // Create the payment model object of the appropriate class
            // for the specified model id
            // The id will be populated in it
            model = PaymentModel.newPaymentModel(modelID);
            // Load it
            model.load();
        
        } catch (PaymentException e) {
            throw new PersistenceException("Payment Model operation failed: " + e, e);

        }
        
        // Since everything was successful we can set the payment model
        setPaymentModel(model);
    }

    /**
     * Loads the party for the specified id into this payment information.
     * @param partyID the id of the party to load
     * @throws PersistenceException if there is a problem loading the pary
     */
    protected void loadParty(String partyID) throws PersistenceException {
        // TODO Haven't implemented Party yet
        // Quietly succeed
    }

    /**
     * Stores this payment information.
     * @param db the DBBean in which to perform the updates
     * @throws PersistenceException if there is a problem storing
     */
    public void store(DBBean db) throws PersistenceException {
        
        if (getID() != null) {
            update(db);
        } else {
            insert(db);
        }

    }

    /**
     * Inserts this payment information into the database.
     * @param db the DBBean in which to perform the insert
     * @throws PersistenceException if there is a problem in the database
     */
    private void insert(DBBean db) throws PersistenceException {
        String paymentID = null;
        String modelID = null;
        String partyID = null;

        // Generate a new payment id
        paymentID = new net.project.database.ObjectManager().getNewObjectID();

        // Store the payment model and get the new model id
        PaymentModel model = getPaymentModel();
        model.store(db);
        modelID = model.getID();

        // Store the payment party and get the new party id
        if (getParty() != null) {
            // TODO Implement store
            // partyID = some value
            // Meanwhile, always error if a party exists
            throw new PersistenceException("PaymentInformation.insert() - Party store not implemented");
        }

        // Build insert statement
        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_payment_information ");
        insertQuery.append("(payment_id, payment_model_id, party_id) ");
        insertQuery.append("values (?, ?, ?) ");
        
        try {
            // Execute statement
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, paymentID);
            db.pstmt.setString(++index, modelID);
            db.pstmt.setString(++index, partyID);
            db.executePrepared();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Payment information store operation failed: " + sqle, sqle);
        
        }

        // Now set the id
        // This MUST be the last line to ensure the ID is set only upon
        // successful completion of all other operations
        setID(paymentID);
    }

    /**
     * Updates this payment information.
     *
     * @param db the DBBean in which to perform the insert
     * @throws PersistenceException if there is a problem in the database
     */
    private void update(DBBean db) throws PersistenceException {

        // Store the payment model and get the new model id
        PaymentModel model = getPaymentModel();
        model.store(db);

        // Store the payment party and get the new party id
        if (getParty() != null) {
            // TODO Implement store
            // partyID = some value
            // Meanwhile, always error if a party exists
            throw new PersistenceException("PaymentInformation.insert() - Party store not implemented");
        }

        // Currently, no attributes in payment information need updated
        // in the pn_payment_information row

    }

   /**
     * Returns the xml Element for this PaymentInformation.
     * @return the element
     */
    public org.jdom.Element getXMLElement() {
        return getXMLDocument().getRootElement();
    }

    /**
     * Returns this payment information as xml
     * @return the xml for this payment information
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("PaymentInformation");
            doc.addElement("PaymentID", getID());
            doc.startElement("PaymentModel");
            doc.addElement(getPaymentModel().getXMLDocument());
            doc.endElement();
            if (getParty() != null) {
                doc.addElement(getParty().getXMLDocument());
            }
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }
  
    /**
     * Populates this payment information from the xml element.
     * @param element the xml element from which to populate this payment information
     */
  /*  protected void populate(Element element) {
        
        // Iterate over each child element of this PaymentInformation element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("PaymentID")) {
                
                this.setID(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("PaymentModel")) {
                
                this.setPaymentModel(PaymentModel.create(childElement));
            
            } 
	}

    }

    // Static Members

    /**
     * Creates a payment information from the specified payment information element.
     * @param paymentInformationElement the xml element from which to create the
     * paymentInformation
     * @return the payment information
     */
/*    static PaymentInformation create(org.jdom.Element paymentInformationElement) {
        PaymentInformation paymentInformation = new PaymentInformation();
        paymentInformation.populate(paymentInformationElement);
        return PaymentInformation;
    } */

}
