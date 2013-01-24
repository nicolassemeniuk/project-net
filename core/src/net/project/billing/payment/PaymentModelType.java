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

/**
 * Provides a type of payment model.
 * Used to determine the appropriate class for a payment model.
 */
public class PaymentModelType implements java.io.Serializable {

    /** The id of this payment model type. */
    private PaymentModelTypeID id = null;

    /** The class for the payment model that this type represents. */
    private String className = null;

    /** The description of this PaymentModelType. */
    private String description = null;
    
    /** The xml element name of this payment model type. */
    private String xmlElementName = null;

    /**
     * Creates a new payment type.
     */
    protected PaymentModelType() {
        // Nothing
    }

    /**
     * Sets the id of this payment type.
     * @param id the payment type id
     * @see #getID
     */
    protected void setID(PaymentModelTypeID id) {
        this.id = id;
    }

    /**
     * Returns this type's id.
     * @return the id of this type
     * @see #setID
     */
    public PaymentModelTypeID getID() {
        return this.id;
    }
   
    /**
     * Sets this payment type's class name.
     * @param className the class name
     * @see #getClassName
     */
    protected void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the java class name that represents a payment of this type.
     * @return the class name
     * @see #setClassName
     */
    protected String getClassName() {
        return this.className;
    }

    /**
     * Sets the description for this payment model type.
     * @param description the description
     */
    protected void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the description for this payment model type.
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Sets the XML Element name for this payment model type.
     * @param xmlElementName the XML element name
     * @see #getXMLElementName
     */
    protected void setXMLElementName(String xmlElementName) {
        this.xmlElementName = xmlElementName;
    }

    /**
     * Returns the XML Element name for this PaymentModelType.
     * This allows an instance for a type to be constructed from an XML element.
     * @see #setXMLElementName
     */
    public String getXMLElementName() {
        return this.xmlElementName;
    }
}
