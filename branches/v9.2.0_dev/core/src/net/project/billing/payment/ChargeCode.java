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
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides the charge code payment model.
 */
public class ChargeCode 
        extends PaymentModel 
        implements java.io.Serializable {

    /** The charge code for this payment model. */
    private String chargeCode = null;

    /**
     * Creates an empty charge code.
     * Available for PaymentModel only to instantiate.
     */
    ChargeCode() {
        super();
    }

    /**
     * Creates a new charge code payment model based on the specified charge code.
     * @param chargeCode the charge code to associate with this payment model
     */
    public ChargeCode(String chargeCode) {
        super();
        setChargeCode(chargeCode);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("id: ").append(getID()).append("\n");
        result.append("chargeCode: ").append(getChargeCode()).append("\n");
        return result.toString();
    }

    /**
     * Sets the charge code associated with this payment model.
     * @param chargeCode the charge code
     * @see #getChargeCode
     */
    private void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }


    /**
     * Returns the actual charge code value.
     * @return the charge code
     * @see #setChargeCode
     */
    private String getChargeCode() {
        return this.chargeCode;
    }

    /**
     * Returns this payment model's type id.
     * @returns <code>{@link PaymentModelTypeID#CHARGE_CODE}</code>
     */
    public PaymentModelTypeID getPaymentModelTypeID() {
        return PaymentModelTypeID.CHARGE_CODE;
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
                String paymentModelID = childElement.getTextTrim();
                if (paymentModelID != null && paymentModelID.length() > 0) {
                    setID(paymentModelID);
                }
            
            } else if (childElement.getName().equals("Value")) {
                String value = childElement.getTextTrim();
                if (value != null && value.length() > 0) {
                    setChargeCode(value);
                }

            }
        }

    }

    /**
     * Inserts this charge code's data.
     * Does not commit/rollback/release
     * @param db the DBBean in which to perform the transaction
     * @param paymentModelID the newly created payment model id of this
     * charge code payment model
     * @throws PersistenceException if there is a problem storing
     */
    protected void insertCustom(DBBean db, String paymentModelID) throws PersistenceException {

        try {
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("insert into pn_payment_model_charge ");
            insertQuery.append("(payment_model_id, charge_code) ");
            insertQuery.append("values (?, ?) ");
            
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, paymentModelID);
            db.pstmt.setString(++index, getChargeCode());
            db.executePrepared();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Charge code update operation failed: " + sqle, sqle);

        }

    }

    /**
     * Updates this charge code's data.
     * Does not commit/rollback/release.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     * @see #update
     */
    protected void updateCustom(DBBean db) throws PersistenceException {
        
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("update pn_payment_model_charge ");
            updateQuery.append("set charge_code = ? ");
            updateQuery.append("where payment_model_id = ? ");

            int index = 0;
            db.prepareStatement(updateQuery.toString());
            db.pstmt.setString(++index, getChargeCode());
            db.pstmt.setString(++index, getID());
            db.executePrepared();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Charge code update operation failed: " + sqle, sqle);

        }

    }

    /**
     * Loads this payment model from persistent store.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {

        // Build query to load charge code payment model
        // Joins with base table and charge code table
        // Technically we don't need anything from the base table
        // But this may well change in the future
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select pm.payment_model_id, pm.model_type_id, ");
        loadQuery.append("mc.charge_code ");
        loadQuery.append("from pn_payment_model pm, pn_payment_model_charge mc ");
        loadQuery.append("where mc.payment_model_id = pm.payment_model_id ");
        loadQuery.append("and pm.payment_model_id = ? ");
        
        // Load it
        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            if (db.result.next()) {
                // Populate all attributes in this charge code
                setChargeCode(db.result.getString("charge_code"));

            } else {
                throw new PersistenceException("Payment Model not found for id");

            }
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Payment model load operation failed: " + sqle, sqle);

        } finally {
            db.release();
            
        }

    }

    /**
     * Returns a value for this payment model.
     * @return the charge code value
     */
    public String getIdentifyingValue() {
        return getChargeCode();
    }    
    

    /**
     * Returns this payment model as xml.
     * @return the xml for this payment model
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("ChargeCode");
            doc.addElement("PaymentModelID", getID());
            doc.addElement("Value", getChargeCode());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

   /**
     * Returns the xml Element for this ChargeCode.
     * @return the element
     */
    public org.jdom.Element getXMLElement() {
        return getXMLDocument().getRootElement();
    }

}