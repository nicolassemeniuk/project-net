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

import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides the trial payment model.
 */
public class Trial 
        extends PaymentModel 
        implements java.io.Serializable {

    /**
     * Creates an empty Trial.
     * Avaiable for PaymentModel only to instantiate.
     */
    public Trial() {
        super();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        return result.toString();
    }

    /**
     * Returns this payment model's type id.
     * @returns <code>{@link PaymentModelTypeID#TRIAL}</code>
     */
    public PaymentModelTypeID getPaymentModelTypeID() {
        return PaymentModelTypeID.TRIAL;
    }

    /**
     * Returns a value for this payment model.
     * For trial models, this is an empty string
     * @return the identifying value for this payment model
     */
    public String getIdentifyingValue() {
        return "";
    }
    
    /**
     * Populates this payment model from the xml element.
     * The element can be assumed to be of the correct type for the payment model.
     * @param element the xml element from which to populate this payment model
     */
    protected void populate(org.jdom.Element element) {
        
        // Iterate over each child element of this ChargeCode element
        // and handle each one
        org.jdom.Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (org.jdom.Element) it.next();

            if (childElement.getName().equals("PaymentModelID")) {
                setID(childElement.getTextTrim());
            
            }
        }

    }


    /**
     * Inserts this trial data.
     * Does not commit/rollback/release
     * @param db the DBBean in which to perform the transaction
     * @param paymentModelID the newly created payment model id of this
     * credit card payment model
     * @throws PersistenceException if there is a problem updating
     */
    protected void insertCustom(DBBean db, String paymentModelID) throws PersistenceException {
        // There is no additional data for trial payment models yet
        // Quietly do nothing
    }

    /**
     * Updates this trial's data.
     * Does not commit/rollback/release.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     * @see #update
     */
    protected void updateCustom(DBBean db) throws PersistenceException {
        
        // Currently there are no attributes that require updating in
        // the database

    }

   /**
     * Loads this payment model from persistent store.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        // No additional attributes to load for Trial payment model
    }
    
    /**
     * Returns this payment model as xml.
     * @return the xml for this payment model
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Trial");
            doc.addElement("PaymentModelID", getID());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Returns the xml Element for this Trial.
     * @return the element
     */
    public org.jdom.Element getXMLElement() {
        return getXMLDocument().getRootElement();
    }

}
