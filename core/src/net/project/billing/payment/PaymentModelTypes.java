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
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides the collection of available payment model types.
 */
public class PaymentModelTypes 
    extends ArrayList
    implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of payment model types.
     * @return the loaded payment model types
     */
    public static PaymentModelTypes getAll() {
        PaymentModelTypes types = new PaymentModelTypes();
        
        try {
            types.load();

            // XXX
            // MAJOR HACK
            // 02/27/2002 @ 20:20 - Tim
            // Need to add a column for XML Element name and load it
            // Since we're code-complete in less than 24 hours, I'm not
            // about to do that
            // For now, I'm going to hard code this stuff
            for (Iterator it = types.iterator(); it.hasNext(); ) {
                PaymentModelType nextType = (PaymentModelType) it.next();
                if (nextType.getID().equals(PaymentModelTypeID.CHARGE_CODE)) {
                    nextType.setXMLElementName("ChargeCode");
                } else if (nextType.getID().equals(PaymentModelTypeID.CREDIT_CARD)) {
                    nextType.setXMLElementName("CreditCard");
                } else if (nextType.getID().equals(PaymentModelTypeID.TRIAL)) {
                    nextType.setXMLElementName("Trial");
                } else {
                    nextType.setXMLElementName("Unspecified");
                }
            }
            // END MAJOR HACK
        
        } catch (PersistenceException pe) {
            // No types
        }
        
        return types;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty payment model types list
     */
    private PaymentModelTypes() {
        super();
    }

    /**
     * Loads the avaialable payment types.
     * @throws PersistenceException if there is a problem loading
     */
    private void load() throws PersistenceException {
        PaymentModelType type = null;
        DBBean db = new DBBean();
        
        // Build query to load all payment model types
        StringBuffer query = new StringBuffer();
        query.append("select mt.model_type_id, mt.class_name, mt.description ");
        query.append("from pn_payment_model_type mt ");

        // Clear any current entries
        clear();
        
        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                type = new PaymentModelType();
                type.setID(PaymentModelTypeID.forID(db.result.getString("model_type_id")));
                type.setClassName(db.result.getString("class_name"));
                // Now set the description; this might be a token
                String description = PropertyProvider.get(db.result.getString("description"));
                if (PropertyProvider.isToken(description)) {
                    description = PropertyProvider.get(description);
                }
                type.setDescription(description);
                
                // Add the type to this collection
                add(type);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(PaymentModelTypes.class).error("PaymentModelTypes.load() threw an SQLException: " + sqle);
            throw new PersistenceException("Payment model types load operation failed: " + sqle, sqle);
        
        } finally {
            db.release();

        }

    }
    

    /**
     * Returns the payment model type for the given id.
     * @param PaymentModelTypeID the id of the payment model type to get
     * @return the payment model type, or null if there is no payment model type for
     * the specified id
     */
    public PaymentModelType getPaymentModelType(PaymentModelTypeID paymentModelTypeID) {
        PaymentModelType paymentModelType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            paymentModelType = (PaymentModelType) it.next();
            if (paymentModelType.getID().equals(paymentModelTypeID)) {
                isFound = true;
            }
        }

        return paymentModelType;
    }

    /**
     * Returns the payment model type for the given xml element name.
     * @param xmlElementName the xml element name
     * @return the model type for that element name, or null if there is no model
     * type for the specified element name
     */
    protected PaymentModelType getPaymentModelTypeForElementName(String xmlElementName) {
        PaymentModelType paymentModelType = null;
        boolean isFound = false;

        // Iteratate over the types in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            paymentModelType = (PaymentModelType) it.next();
            if (paymentModelType.getXMLElementName().equals(xmlElementName)) {
                isFound = true;
            }
        }

        return paymentModelType;
    }

}
