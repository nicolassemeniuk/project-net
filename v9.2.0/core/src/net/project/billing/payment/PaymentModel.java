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

import org.apache.log4j.Logger;

/**
 * A <code>PaymentModel</code> represents a way that a license may be
 * paid for.
 * This class provides methods for constructing a payment model of the appopriate
 * type for an id of an existing payment model.
 */
public abstract class PaymentModel {

    //
    // Static members
    //

    /**
     * Constructs an empty PaymentModel of the correct type for the specified id.
     * The is set in the PaymentModel.
     * @param paymentModelID the id of the payment model for which to construct
     * the appropriate class.
     * @return the PaymentModel of the appropriate class
     * @throws PaymentException if there is a problem creating the payment model
     */
    public static PaymentModel newPaymentModel(String paymentModelID) 
            throws PaymentException {
        
        PaymentModel paymentModel = null;

        try {
            // Construct a payment model object with the specified id by
            // lookup up its type and creating a payment model for that type
            paymentModel = newPaymentModel(PaymentModel.getPaymentModelTypeIDForPaymentModel(paymentModelID));

            // Populate the id
            paymentModel.setID(paymentModelID);
        
        } catch (PersistenceException pe) {
            throw new PaymentException("New payment model operation failed: " + pe, pe);

        }

        return paymentModel;
    }

    /**
     * Creates a payment model from the specified payment model element.
     * The payment model is fully populated from the element.
     * @param paymentModelElement the xml element from which to create the
     * payment model
     * @return the payment model of the appropriate type for the paymentModelElement
     * @throws PaymentException if there is a problem creating the payment model;
     * for example, the element has no name or the model class cannot be instantiated
     */
    public static PaymentModel create(org.jdom.Element paymentModelElement) 
            throws PaymentException {
        
        String elementName = paymentModelElement.getName();
        if (elementName == null) {
            throw new PaymentException("Error constructing payment model; no element name");
        }

        // get the payment model
        PaymentModelType modelType = PaymentModelTypes.getAll().getPaymentModelTypeForElementName(elementName);
        PaymentModel model = newPaymentModel(modelType);
        model.populate(paymentModelElement);

        return model;
    }


    /**
     * Determines the payment model type id for the payment model with the
     * specified id.  This can be used to determine the type of the payment model.
     * @param paymentModelID the id of the payment model
     * @return the type id of the payment model
     * @throws PersistenceException if there is a problem determining the
     * payment model type id for the payment model id
     */
    private static PaymentModelTypeID getPaymentModelTypeIDForPaymentModel(String paymentModelID) 
            throws PersistenceException {

        PaymentModelTypeID paymentModelTypeID = null;

        // Build query to select payment model type id from payment model
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select pm.model_type_id ");
        selectQuery.append("from pn_payment_model pm ");
        selectQuery.append("where pm.payment_model_id = ? ");

        // Fetch the type id
        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(selectQuery.toString());
            db.pstmt.setString(++index, paymentModelID);
            db.executePrepared();
            if (db.result.next()) {
                // Lookup object based on id
                paymentModelTypeID = PaymentModelTypeID.forID(db.result.getString("model_type_id"));
            
            } else {
                throw new PersistenceException("Payment model not found for id");

            }
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Payment model load operation failed: " + sqle, sqle);
        
        } finally {
            db.release();

        }

        return paymentModelTypeID;
    }


    /**
     * Constructs a new PayemntModel object of the appropriate class for the
     * specified model type id.
     * @param modelTypeID the id of the payment model type for which to
     * construct the appopriate payment model class
     * @return a new, empty payment model of the appropriate class for
     * the model type
     * @throws PaymentException if there is a problem constructing the
     * payment model for the model type id; for example, the class could
     * not be found or instantiated
     */
    private static PaymentModel newPaymentModel(PaymentModelTypeID modelTypeID) 
                throws PaymentException {

        // Get all the types
        PaymentModelTypes modelTypes = PaymentModelTypes.getAll();
        return newPaymentModel(modelTypes.getPaymentModelType(modelTypeID));
    }

    private static PaymentModel newPaymentModel(PaymentModelType modelType) 
            throws PaymentException {

        PaymentModel paymentModel = null;
        
        // Create an instance for the appropriate model type
        try {
            Class modelClass = Class.forName(modelType.getClassName());
            paymentModel = (PaymentModel) modelClass.newInstance();
        
        } catch (ClassNotFoundException cnfe) {
        	Logger.getLogger(PaymentModel.class).error("PaymentModel.newPaymentModel threw a ClassNotFoundException when trying to create class " + 
                    modelType.getClassName() + ": " + cnfe);
            throw new PaymentException("Unable to create a PaymentModel: " + cnfe, cnfe);

        } catch (InstantiationException ie) {
        	Logger.getLogger(PaymentModel.class).error("PaymentModel.newPaymentModel threw a InstantiationException when trying to create class " + 
                    modelType.getClassName() + ": " + ie);
            throw new PaymentException("Unable to create a PaymentModel: " + ie, ie);
        
        } catch (IllegalAccessException iae) {
        	Logger.getLogger(PaymentModel.class).error("PaymentModel.newPaymentModel trhew a IllegalAccessException when trying to create class " + 
                    modelType.getClassName() + ": " + iae);
            throw new PaymentException("Unable to create a PaymentModel: " + iae, iae);
        
        }

        return paymentModel;
    }

    //
    // Instance members
    //

    /** This payment model's id. */
    private String id = null;

    /**
     * Sets this payment model's id.
     * @param id the payment model id
     */
    public void setID(String id) {
        this.id = id;
    }

    /** 
     * Returns this payment model's id.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the payment model type id for this payment model.
     * @return the payment model type id
     */
    public abstract PaymentModelTypeID getPaymentModelTypeID();

    /**
     * Returns a value for this payment model.
     * The value should attempt to be unique with respect to other payment models
     * of the same type.  For example, if the payment model was a credit card
     * the identifying value might be a credit card number (or some obfuscated
     * derivation thereof).
     * <b>Note:</b> The value need not be display-oriented; in fact it should
     * never change.
     * @return the identifying value for this payment model
     */
    public abstract String getIdentifyingValue();

    /**
     * Populates this payment model from the xml element.
     * The element can be assumed to be of the correct type for the payment model.
     * @param element the xml element from which to populate this payment model
     * @throws PaymentException if there is a problem populating this payment model
     */
    protected abstract void populate(org.jdom.Element element) throws PaymentException;

    /**
     * Loads this payment model from persistent store.
     * @throws PersistenceException if there is a problem loading
     */
    public abstract void load() throws PersistenceException;
    
    /**
     * Stores this PaymentModel to the database.
     * Does not commit/rollback/release.
     * @param db the DBBean in which to perform the transaction.
     * @throws PersistenceException if there is a problem storing
     * @see #update
     * @see #insert
     */
    public void store(DBBean db) throws PersistenceException {
        if ((getID() != null) && (getID().trim().length() > 0)) {
            update(db);
        } else {
            insert(db);
        }
    }


    /**
     * Inserts the base payment model information.
     * Does not commit/rollback/release.
     * This is a template method that calls <code>{@link #insertCustom}</code>
     * that allows sub-classes to insert their own data in the same transaction.
     * This method is typically not overridden by a sub-class.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem storing
     * @see #insertCustom
     */
    protected void insert(DBBean db) throws PersistenceException {
        String paymentModelID = null;

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_payment_model ");
        insertQuery.append("(payment_model_id, model_type_id) ");
        insertQuery.append("values (?, ?) ");

        // Generate a new id
        paymentModelID = new net.project.database.ObjectManager().getNewObjectID();

        try {
            // Insert the header record
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, paymentModelID);
            db.pstmt.setString(++index, getPaymentModelTypeID().getID());
            db.executePrepared();

            // Now insert the custom payment model info
            insertCustom(db, paymentModelID);

        } catch (SQLException sqle) {
            throw new PersistenceException("Payment model insert operation " +
                "failed", sqle);

        }

        // This must be at end to ensure id is only set if success
        // Set the newly generated id
        setID(paymentModelID);
    }


    /**
     * Inserts custom payment model information into the database.
     * Called during <code>{@link #insert}</code>.
     * @param db the DBBean in which to perform the transaction
     * @param paymentModelID the newly generated payment model id that
     * has been insert (but not commited).
     * @throws PersistenceException if there is a problem inserting
     * @see #insert
     */
    protected abstract void insertCustom(DBBean db, String paymentModelID) throws PersistenceException;


    /**
     * Updates this payment model.
     * No commit/rollback/release is performed.
     * Calls the custom update method after updating 
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     */
    protected void update(DBBean db) throws PersistenceException {
        // Currently this payment model has no attributes that are
        // required to be stored in pn_payment_model

        // Now update the custom payment model info
        updateCustom(db);

    }

    /**
     * Updates this custom payment model information in the database.
     * Called during <code>{@link #update}</code>.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     * @see #update
     */
    protected abstract void updateCustom(DBBean db) throws PersistenceException;

    /**
     * Returns this payment model as xml.
     * @return the xml for this payment model
     */
    public abstract net.project.xml.document.XMLDocument getXMLDocument();

    /**
     * Returns this payment model as an Element.
     * @return the element for this payment model
     */
    public abstract org.jdom.Element getXMLElement();

}
