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
 * Provides the credit card payment model.
 */
public class CreditCard extends PaymentModel implements java.io.Serializable {
    private String cardNumber = null;
    private Integer expiryMonth = null;
    private Integer expiryYear = null;
    private String vendorTransactionID = null;

    /**
     * Creates an empty CreditCard.
     * Available for PaymentModel only to instantiate.
     */
    public CreditCard() {
        super();
    }

    /**
     * Creates a new CreditCard with the specified card number and expiry date.
     * @param cardNumber the credit card number
     * @param expiryMonth the expiry month
     * @param expiryYear the expiry year
     */
    public CreditCard(String cardNumber, int expiryMonth, int expiryYear) {
        this.cardNumber = cardNumber;
        this.expiryMonth = new Integer(expiryMonth);
        this.expiryYear = new Integer(expiryYear);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("id: ").append(getID()).append("\n");
        result.append("cardNumber: ").append(getCardNumber()).append("\n");
        result.append("expiryMonth: ").append(getExpiryMonth()).append("\n");
        result.append("expiryYear: ").append(getExpiryYear()).append("\n");
        result.append("vendorTransactionID: ").append(getVendorTransactionID()).append("\n");
        return result.toString();
    }

    /**
     * Returns this payment model's type id.
     * @return <code>{@link PaymentModelTypeID#CREDIT_CARD}</code>
     */
    public PaymentModelTypeID getPaymentModelTypeID() {
        return PaymentModelTypeID.CREDIT_CARD;
    }

    /**
     * Sets the credit card number.
     * @param cardNumber the card number
     * @see #getCardNumber
     */
    private void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Returns the credit card number.
     * @return the card number
     * @see #setCardNumber
     */
    private String getCardNumber() {
        return this.cardNumber;
    }

    /**
     * Sets the card expiry month.  No validation is performed.
     *
     * @param expiryMonth the card expiry month
     * @see #getExpiryMonth
     */
    private void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    /**
     * Returns the expiry month.
     * @return the card expiry month
     * @see #setExpiryMonth
     */
    private Integer getExpiryMonth() {
        return this.expiryMonth;
    }

    /**
     * Sets the card expiry year.
     * No validation is performed.
     * @param expiryYear
     * @see #getExpiryYear
     */
    private void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    /**
     * Returns the card expiry year.
     * @return the expiry year
     * @see #setExpiryYear
     */
    private Integer getExpiryYear() {
        return this.expiryYear;
    }

    private String getVendorTransactionID() {
        return vendorTransactionID;
    }

    private void setVendorTransactionID(String vendorTransactionID) {
        this.vendorTransactionID = vendorTransactionID;
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

            } else if (childElement.getName().equals("CardNumber")) {
                setCardNumber(childElement.getTextTrim());

            } else if (childElement.getName().equals("ExpiryMonth")) {
                setExpiryMonth(Integer.valueOf(childElement.getTextTrim()));

            } else if (childElement.getName().equals("ExpiryYear")) {
                setExpiryYear(Integer.valueOf(childElement.getTextTrim()));

            }
        }

    }

    /**
     * Inserts this credit card's data.
     * Does not commit/rollback/release
     * @param db the DBBean in which to perform the transaction
     * @param paymentModelID the newly created payment model id of this
     * credit card payment model
     * @throws PersistenceException if there is a problem storing
     */
    protected void insertCustom(DBBean db, String paymentModelID) throws PersistenceException {

        try {
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("insert into pn_payment_model_creditcard ");
            insertQuery.append("(payment_model_id, card_number, card_expiry_month, card_expiry_year) ");
            insertQuery.append("values (?, ?, ?, ?) ");

            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(1, paymentModelID);

            db.pstmt.setString(2, getCardNumber());
            db.pstmt.setInt(3, getExpiryMonth().intValue());
            db.pstmt.setInt(4, getExpiryYear().intValue());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Credit card insert operation failed: " + sqle, sqle);

        }

    }

    /**
     * Updates this credit card's data.
     * Does not commit/rollback/release.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     * @see #update
     */
    protected void updateCustom(DBBean db) throws PersistenceException {

        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("update pn_payment_model_creditcard ");
            updateQuery.append("set card_number = ?, card_expiry_month = ?, card_expiry_year = ? ");
            updateQuery.append("where payment_model_id = ? ");

            int index = 0;
            db.prepareStatement(updateQuery.toString());
            db.pstmt.setString(++index, getCardNumber());
            db.pstmt.setInt(++index, getExpiryMonth().intValue());
            db.pstmt.setInt(++index, getExpiryYear().intValue());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Credit card update operation failed: " + sqle, sqle);

        }

    }

    /**
     * Loads this payment model from persistent store.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        // Build query to load credit card payment model
        // Joins with base table and credit card table
        // Technically we don't need anything from the base table
        // But this may well change in the future
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select ");
        loadQuery.append("    pm.payment_model_id, pm.model_type_id, ");
        loadQuery.append("    cc.card_number, cc.card_expiry_month, ");
        loadQuery.append("    cc.card_expiry_year, t.vendor_transaction_id ");
        loadQuery.append("from ");
        loadQuery.append("    pn_payment_information pi, ");
        loadQuery.append("    pn_payment_model pm, pn_payment_model_creditcard cc, ");
        loadQuery.append("    pn_cc_transaction_payment ctp, pn_credit_card_transaction t ");
        loadQuery.append("where ");
        loadQuery.append("    pi.payment_model_id = pm.payment_model_id ");
        loadQuery.append("    and pm.payment_model_id = cc.payment_model_id ");
        loadQuery.append("    and pi.payment_id = ctp.payment_id(+) ");
        loadQuery.append("    and ctp.transaction_id = t.transaction_id(+) ");
        loadQuery.append("    and pm.payment_model_id = ? ");

        // Load it
        DBBean db = new DBBean();
        try {
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            if (db.result.next()) {
                // Populate all attributes in this credit card
                setCardNumber(db.result.getString("card_number"));
                setExpiryMonth(new Integer(db.result.getInt("card_expiry_month")));
                setExpiryYear(new Integer(db.result.getInt("card_expiry_year")));
                setVendorTransactionID(db.result.getString("vendor_transaction_id"));

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
     *
     * @return a <code>String</code> value containing the credit card number in
     * the form XXXX-XXXX-XXXX-1234.  We don't store the whole credit card, so
     * there isn't a way to display the whole number.  (This would be a security
     * problem anyhow.)
     */
    public String getIdentifyingValue() {
        return getCardNumber();
    }


    /**
     * Returns this payment model as xml
     * @return the xml for this payment model
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("CreditCard");
            doc.addElement("PaymentModelID", getID());
            // Haven't decided how to handle card number yet
            // Should we never return it, or obfuscate it?
            // Perhaps we should be storing it encrypted anyhow
            doc.addElement("CardNumber", getCardNumber());
            doc.addElement("ExpiryMonth", getExpiryMonth());
            doc.addElement("ExpiryYear", getExpiryYear());
            doc.addElement("TransactionID", getVendorTransactionID());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

   /**
     * Returns the xml Element for this CreditCard.
     * @return the element
     */
    public org.jdom.Element getXMLElement() {
        return getXMLDocument().getRootElement();
    }

}
