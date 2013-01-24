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
|   $Revision: 20773 $
|       $Date: 2010-04-29 12:22:39 -0300 (jue, 29 abr 2010) $
|     $Author: nilesh $
|
+----------------------------------------------------------------------*/
package net.project.license.create;

import java.util.Iterator;

import net.project.license.DistributableLicense;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.model.LicenseModel;
import net.project.license.model.NodeID;
import net.project.license.model.NodeLocked;
import net.project.license.system.LicenseProperties;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * Provides view-layer mechanism to create a license from an external
 * encrypted license source.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class ExternalLicenseCreator implements net.project.persistence.IXMLPersistence {
    private String enteredLicense = null;
    private License license = null;

    public ExternalLicenseCreator() {
        // Do nothing
    }

    /**
     * Sets the encrypted license string entered by a user.
     * @param enteredLicense the license entered by the user
     */
    public void setEnteredLicense(String enteredLicense) {
        this.enteredLicense = removeSpecialCharacters(enteredLicense);
    }

    /**
     * Indicates whether the license's NodeLocked nodeID is different from
     * the current system node id (that is, product installation id).
     * This is significant; the license will be unusable if stored and assigned
     * to a user.
     * @return true if the node id is different; false if the
     * node id in the license is the same as the current system id or the license
     * is not node locked
     * @throws LicenseException
     */
    public boolean isInconsistentNodeID()
        throws LicenseException, PersistenceException {
        boolean isInconsistent = true;

        // Not Inconsistent if system node id equals license node id
        // If license has no node id (that is, it is not node locked)
        // then it will not be deemed inconsistent
        if (getLicenseNodeID() == null || getSystemNodeID().equals(getLicenseNodeID())) {
            isInconsistent = false;
        }

        return isInconsistent;
    }

    public String getLicenseNodeIDDisplayString() {
        if (getLicenseNodeID() != null) {
            return getLicenseNodeID().toDisplayString();
        }
        return "Not Node Locked";
    }

    private NodeID getLicenseNodeID() {
        if (this.license == null) {
            throw new IllegalStateException("No License");
        }

        NodeID nodeID = null;

        // Loop over license models, looking for a node locked model
        for (Iterator it = this.license.getModelCollection().iterator(); it.hasNext();) {
            LicenseModel nextModel = (LicenseModel)it.next();
            if (nextModel instanceof NodeLocked) {
                nodeID = ((NodeLocked)nextModel).getNodeID();
            }

        }

        return nodeID;
    }

    public String getSystemNodeIDDisplayString()
        throws LicenseException, PersistenceException {

        return getSystemNodeID().toDisplayString();
    }

    private NodeID getSystemNodeID()
        throws LicenseException, PersistenceException {

        return LicenseProperties.getInstance().getCurrentNodeID();
    }

    /**
     * Stores the currently set entered license.
     * @throws IllegalStateException if no entered license has been specified
     */
    public void store() throws LicenseException, PersistenceException {
        //Check to see if a responsible user has been specified, if not use the
        //current user.
        if (license.getResponsiblePerson() == null) {
            license.setResponsiblePerson(SessionManager.getUser());
        }

        license.store();
    }

    /**
     * Returns the display formatted license key of the stored license.
     * Assumes license has already been stored.
     */
    public String getGeneratedLicenseKeyDisplay() {
        return this.license.getKey().toDisplayString();
    }

    /**
     * Builds the license from the entered license.
     * @throws IllegalStateException if no entered license has been specified
     */
    public void buildLicense() throws LicenseException, PersistenceException {

        if (this.enteredLicense == null) {
            throw new IllegalStateException("No entered license");
        }
        this.enteredLicense = this.enteredLicense.trim();
        try {
            License license = DistributableLicense.unmarshal(
                net.project.security.EncryptionManager.decryptBlowfish(
                    this.enteredLicense, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE
                )
            );

            this.license = license;

        } catch (net.project.xml.XMLException e) {
            throw new LicenseException("License entered by you is incorrect. Please enter a valid, encrypted license " , e);

        } catch (net.project.security.EncryptionException e) {
            throw new LicenseException("License entered by you is incorrect. Please enter a valid, encrypted license " , e);

        } catch (java.lang.IllegalArgumentException e) {
            throw new LicenseException("License entered by you is incorrect. Please enter a valid, encrypted license " , e);
        }
    }

    public String getXML() {
        return getXMLDocument().getXMLBodyString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    net.project.xml.document.XMLDocument getXMLDocument() {
        return this.license.getXMLDocument();
    }
    
    /**
     * Builds the valid license key avoiding special characters from the entered license.
     * @param entredKey is the license key entered by user.
     * @return String updated license key after removing special characters.
     */
    private String removeSpecialCharacters(String entredKey){
    	String updatedKey = "";
    	
    	 for(int index = 0 ; index < entredKey.length(); index++){ 
             if((entredKey.charAt(index) >=65 && entredKey.charAt(index) <= 70) ||
                (entredKey.charAt(index) >=48 && entredKey.charAt(index) <= 57) ||
            	(entredKey.charAt(index) >=97 && entredKey.charAt(index) <= 102)){ 
            	 updatedKey += entredKey.charAt(index); 
             } 
         }  
    	     	
    	return updatedKey;
    }
  
}

