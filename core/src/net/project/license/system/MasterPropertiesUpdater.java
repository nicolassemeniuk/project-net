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
package net.project.license.system;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

/**
 * Provides view-layer mechanism to update master properties from encrypted
 * string of properties.
 */
public class MasterPropertiesUpdater implements net.project.persistence.IXMLPersistence {

    private String enteredProperties = null;
    private MasterProperties masterProperties = null;

    public MasterPropertiesUpdater() {
        // Do nothing
    }

    /**
     * Sets the encrypted master properties string entered by a user.
     * @param enteredProperties the entered properties
     */
    public void setEnteredProperties(String enteredProperties) {
        this.enteredProperties = enteredProperties;
    }

    /**
     * Indicates whether the current product installation id is different from
     * the one in the current properties.
     * This is significant as every existing license is tied to the current
     * product installation id; all existing licenses will be invalidated as
     * a result of changing it
     * @return true if the product installation id is different; false if the
     * product installation id is present in the current properties and the
     * new properties and they are equal
     */
    public boolean isInconsistentProductInstallationID() throws MasterPropertiesNotFoundException, LicenseException {
        boolean isInconsistent = true;
        
        try {
            buildMasterProperties();
	    Property newProductInstallationIDProperty = this.masterProperties.get(PropertyName.LICENSE_PRODUCT_INSTALLATION_ID);
	    retrieveMasterProperties();
            Property currentProductInstallationIDProperty = MasterProperties.getInstance().get(PropertyName.LICENSE_PRODUCT_INSTALLATION_ID);
            
            if (currentProductInstallationIDProperty != null && newProductInstallationIDProperty != null) {
                String currentProductInstallationID = currentProductInstallationIDProperty.getValue();
                String newProductInstallationID = newProductInstallationIDProperty.getValue();
                if (currentProductInstallationID != null && newProductInstallationID != null) {
                    if (currentProductInstallationID.equals(newProductInstallationID)) {
                        isInconsistent = false;
                    }

                }

            }
        
        } catch (net.project.persistence.PersistenceException pe) {
            // Remains inconsistent

	} catch (net.project.license.system.MasterPropertiesNotFoundException e) {
            // License Master Properties are not installed on this system.
	    throw new MasterPropertiesNotFoundException("No License Master Properties were found installed on the system. " + e, e);
        
        } catch (java.lang.IllegalArgumentException e) {
	    throw new LicenseException("Unable to read the properties. Please enter valid, encrypted license properties : " + e, e);
	}

        return isInconsistent;
    }

    /**
     * Indicates whether the system has any existing licenses. 
     * This is significant as every existing license is tied to a
     * product installation id; all existing licenses will be invalidated as
     * a result of changing it.
     * @return true if previously created licenses exist in the system; false if 
     * no licenses are found.
     */
    public boolean checkSystemHasExistingLicenses() 
	throws PersistenceException {

	String query = "select count(license_id) from pn_license";
	int licenseCount = 0;
        DBBean db = new DBBean();

	try {
            db.setQuery(query);
	    db.executeQuery();
	    while (db.result.next()) {
		licenseCount = db.result.getInt("count(license_id)");
	    }

	} catch (SQLException sqle){
	    throw new PersistenceException("Problem checking existing licenses in " +
            "the system." + sqle, sqle);

	} finally {
            db.release();
        }


	if(licenseCount > 0){
	    return true;
	}
	return false;
    }


    /**
     * Stores the currently set entered properties.
     * Assumes entered properties have been set
     * @throws LicenseException if there is a problem reading the entered properties
     * @throws PersistenceException if there is a problem loading the current
     * master properties or storing them
     * @throws IllegalStateException if no entered proeprties have been specified
     */
    public void store() throws LicenseException, PersistenceException {
        buildMasterProperties();
        this.masterProperties.store();
    }

    /**
     * Builds the master properties from the entered properties.
     * @throws LicenseException if there is a problem reading the entered properties
     * @throws PersistenceException if there is a problem loading the current
     * master properties
     * @throws IllegalStateException if no entered properties have been specified
     */
    private void buildMasterProperties() throws LicenseException, PersistenceException {

        if (this.enteredProperties == null) {
            throw new IllegalStateException("No entered properties");
        }
        this.enteredProperties = this.enteredProperties.trim();
        //this.masterProperties = MasterProperties.getInstance();
	this.masterProperties = new MasterProperties();

        try {
            this.masterProperties.update(
                    PropertyCollection.unmarshal(
                        net.project.security.EncryptionManager.decryptBlowfish(
                            this.enteredProperties, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE
                            )
                        )
                    );

        } catch (net.project.xml.XMLException e) {
            throw new LicenseException("Unable to read entered properties: " + e, e);

        } catch (net.project.security.EncryptionException e) {
            throw new LicenseException("Unable to read entered properties: " + e, e);
        
        }

    }

    /**
     * Retrieves the master properties from the database.
     * @throws LicenseException if there is a problem reading the properties
     * @throws PersistenceException if there is a problem loading the master properties
     * @throws IllegalStateException if no entered properties have been specified 
     */
    private void retrieveMasterProperties() throws LicenseException, PersistenceException {
	//try {
	    this.masterProperties = MasterProperties.getInstance();
        
	/*} catch (net.project.xml.XMLException e) {
            throw new LicenseException("Unable to read entered properties: " + e, e);

        } catch (net.project.security.EncryptionException e) {
            throw new LicenseException("Unable to read entered properties: " + e, e);
        
        } */

    }

    public String getXML() {
        return getXMLDocument().getXMLBodyString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    net.project.xml.document.XMLDocument getXMLDocument(){
        
	try {
	    buildMasterProperties();
	} catch (LicenseException le){
	    // NO XML
	    System.out.println(" MasterPropertiesUpdater.java : LicenseException thrown :" + le.getMessage());

	} catch (PersistenceException pe){
            //NO XML
	    System.out.println(" MasterPropertiesUpdater.java : PersistenceException thrown :" + pe.getMessage());
	}
        
        return this.masterProperties.getXMLDocument();
	
    }

}
