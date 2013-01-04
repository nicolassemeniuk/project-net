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


import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * The License Model embodies the limits and constraints on using a license.
 */
public abstract class LicenseModel {

    //
    // Static members
    //

    /**
     * Creates a license model from the specified license model element.
     * The license model is fully populated from the element.
     * @param licenseModelElement the xml element from which to create the
     * license model
     * @return the license model of the appropriate type for the licenseModelElement
     * @throws LicenseException if there is a problem creating the license model;
     * for example, the element has no name or the model class cannot be instantiated
     */
    public static LicenseModel create(org.jdom.Element licenseModelElement) 
            throws LicenseException {
        
        String elementName = licenseModelElement.getName();
        if (elementName == null) {
            throw new LicenseException("Error constructing license model; no element name");
        }

        // get the license model
        LicenseModelType modelType = LicenseModelTypes.getAll().getLicenseModelTypeForElementName(elementName);
        LicenseModel model = newLicenseModel(modelType);
        model.populate(licenseModelElement);

        return model;
    }

    /**
     * Constructs a new LicenseModel object of the appropriate class for the
     * specified model type.
     * @param modelType the type of license model for which to
     * construct the appopriate license model class
     * @return a new, empty license model of the appropriate class for
     * the model type
     * @throws LicenseException if there is a problem constructing the
     * license model for the model type; for example, the class could
     * not be found or instantiated
     */
    private static LicenseModel newLicenseModel(LicenseModelType modelType) 
                throws LicenseException {

        LicenseModel licenseModel = null;

        // Create an instance for the appropriate model type
        try {
            Class modelClass = Class.forName(modelType.getClassName());
            licenseModel = (LicenseModel) modelClass.newInstance();
        
        } catch (ClassNotFoundException cnfe) {
        	Logger.getLogger(LicenseModel.class).error("LicenseModel.newLicenseModel threw a ClassNotFoundException when trying to create class " + 
                    modelType.getClassName() + ": " + cnfe);
            throw new LicenseException("Unable to create a LicenseModel: " + cnfe, cnfe);

        } catch (InstantiationException ie) {
        	Logger.getLogger(LicenseModel.class).error("LicenseModel.newLicenseModel threw a InstantiationException when trying to create class " + 
                    modelType.getClassName() + ": " + ie);
            throw new LicenseException("Unable to create a LicenseModel: " + ie, ie);
        
        } catch (IllegalAccessException iae) {
        	Logger.getLogger(LicenseModel.class).error("LicenseModel.newLicenseModel threw a IllegalAccessException when trying to create class " + 
                    modelType.getClassName() + ": " + iae);
            throw new LicenseException("Unable to create a LicenseModel: " + iae, iae);
        
        }

        return licenseModel;
    }


    //
    // Instance members
    //

    LicenseModel() {
        // Do nothing
    }

    /**
     * Returns the license model type id for this license model.
     * @return the license model type id
     */
    public abstract LicenseModelTypeID getLicenseModelTypeID();
    
    /**
     * Returns the xml Element for this LicenseModel.
     * @return the element
     */
    public abstract org.jdom.Element getXMLElement();


    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     * @throws LicenseException if there is a problem populating this license model
     */
    protected abstract void populate(org.jdom.Element element) throws LicenseException;

    /**
     * Indicates if this license model's constraints have already been met.
     * @return the status of checking the constraints; returns a status value
     * of <code>true</code> if this constraint has been met; <code>false</code>
     * if the constraint is below its limits.
     * Typically a license may only be acquired if none of its models have
     * has their constraints been met; if at least one model constraint has been met, 
     * the license may not be acquired by additional persons.
     */
    public abstract net.project.license.CheckStatus checkConstraintMet() throws LicenseException, PersistenceException ;

    /**
     * Indicates if this license model's constraints have already been exceeded.
     * @return the status of checking the constraints; returns a status value
     * of <code>true</code> if this constraint has been exceeded; <code>false</code>
     * if the constraint has simply been met or not met
     * Typically a person may not continue to use a license if any of its
     * models have been exceeded.
     */
    public abstract net.project.license.CheckStatus checkConstraintExceeded() throws LicenseException, PersistenceException ;
    
    /**
     * Performs any tasks required when a license to which this model belongs
     * is being acquired.
     * This method is expected to succeed if {@link #checkConstraintMet} returns
     * a value of <code>false</code>.
     * @throws LicenseModelAcquisitionException if there is a problem performing
     * the tasks; for example, some limit has been met.
     * @throws LicenseException if there is a problem loading the license master properties
     */
    public abstract void acquisitionEvent() throws LicenseModelAcquisitionException, LicenseException, PersistenceException;

    /**
     * Performs any tasks required when a license to which this model belongs
     * is being relinquished.
     * @throws LicenseModelRelinquishException if there is a problem performing
     * the tasks.
     */
    public abstract void relinquishEvent() throws LicenseModelRelinquishException;

    /**
     * Returns the XMLDocument representation of this LicenseModel.
     * @return the XMLDocument representation
     */
    public abstract net.project.xml.document.XMLDocument getXMLDocument();

}
