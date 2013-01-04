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
package net.project.license.model;

/**
 * Provides a type of license model.
 * Used to determine the appropriate class for a license model.
 */
public class LicenseModelType implements java.io.Serializable {

    /** The id of this license model type. */
    private LicenseModelTypeID id = null;

    /** The class for the license model that this type represents. */
    private String className = null;

    /** The xml element name of this license model type. */
    private String xmlElementName = null;

    /**
     * Creates a new license model type.
     */
    protected LicenseModelType() {
        // Nothing
    }

    /**
     * Sets the id of this license model type.
     * @param id the license model type id
     * @see #getID
     */
    protected void setID(LicenseModelTypeID id) {
        this.id = id;
    }

    /**
     * Returns this type's id.
     * @return the id of this type
     * @see #setID
     */
    public LicenseModelTypeID getID() {
        return this.id;
    }
   
    /**
     * Sets this license model type's class name.
     * @param className the class name
     * @see #getClassName
     */
    protected void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the java class name that represents a license of this type.
     * @return the class name
     * @see #setClassName
     */
    protected String getClassName() {
        return this.className;
    }

    /**
     * Sets the xml element name used by license models of this type.
     * Used when constructing license models from xml stream.
     * @param xmlElementName the xml element name for license models of this
     * type
     */
    protected void setXMLElementName(String xmlElementName) {
        this.xmlElementName = xmlElementName;
    }

    /**
     * Returns the xml element name used by license models of this type.
     * @return the xml element name
     */
    protected String getXMLElementName() {
        return this.xmlElementName;
    }

}
