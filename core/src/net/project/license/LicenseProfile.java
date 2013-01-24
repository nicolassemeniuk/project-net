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
package net.project.license;

import net.project.persistence.PersistenceException;
import net.project.resource.Person;

/**
 * Represent's a person's licensing profile.  Provides view-level methods for
 * displaying license information.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class LicenseProfile implements net.project.persistence.IXMLPersistence {
    /** The current person. */
    private Person person = null;

    /** The person's current license. */
    private LicenseHolder licenseHolder = null;

    /**
     * Creates an empty LicenseProfile.
     */
    public LicenseProfile() {
        // Do nothing
    }

    /**
     * Sets the person that this profile represents.
     * @param person the current person
     * @throws LicenseException if there is a problem looking for the
     * person's license
     */
    public void setPerson(Person person) throws LicenseException {
        this.person = person;
        loadLicense();
    }

    /**
     * Indicates whether the current person has a license.
     * @return true if the current person has a license; false if they do not
     */
    public boolean hasLicense() {
        return this.licenseHolder.hasLicense();
    }

    /**
     * Returns the XML representation of this license profile including the
     * XML version tag
     * @return the xml representation
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns the XML representation of this license profile excluding the
     * XML version tag
     * @return the xml representation
     */
    public String getXMLBody() {
        License license = this.licenseHolder.getLicense();
        return license.getXMLBody();
    }


    /**
     * Loads the current person's license.
     * After calling, {@link #licenseHolder} will be set.
     * @throws LicenseException if there is a problem finding the user's license;
     * in this case the {@link #licenseHolder} is set to {@link #NO_LICENSE}
     * @throws IllegalStateException if this method is called when no person
     * is available.
     */
    private void loadLicense() throws LicenseException {

        if (this.person == null) {
            throw new IllegalStateException("No current person");
        }

        try {
            // Ensures the user's license has been loaded (or at least checked
            // to see if they have one)
            if (this.licenseHolder == null) {
                // Load the user's license
                PersonLicense personLicense = new PersonLicense(this.person);
                License license = null;

                try {
                    license = personLicense.getCurrentLicense();
                
                } catch (LicenseNotFoundException e) {
                    // Person has no license
                    // Do nothing; license remains null

                }

                if (license == null) {
                    // User had no license
                    this.licenseHolder = NO_LICENSE;

                } else {
                    // User has license
                    this.licenseHolder = new LicenseHolder(license);
                }
            }
        
        } catch (PersistenceException pe) {
            this.licenseHolder = NO_LICENSE;
            throw new LicenseException("License fetch operation failed: " + pe, pe);

        }
    
    }

    //
    // Nested top-level classes
    //

    /**
     * Simple store for user's license.
     */
    private static class LicenseHolder {
        private License license = null;

        /**
         * Creates a new LicenseHolder for the given license.
         * The license may be null.
         * @param license the license
         */
        LicenseHolder(License license) {
            this.license = license;
        }

        /**
         * Indicates whether this LicenseHolder has a license.
         * @return true if this LicenseHolder has a license; false otherwise
         */
        boolean hasLicense() {
            return (this.license != null);
        }

        /**
         * Returns the current license.
         * Only valid to call this if {@link #hasLicense} returns true.
         * @return the license
         * @throws IllegalStateException if this LicenseHolder has no license
         */
        License getLicense() throws IllegalStateException {
            if (this.license == null) {
                throw new IllegalStateException("No license");
            }
            return this.license;
        }

    }

    /**
     * Constant that represents fact that user has no license.
     */
    private static final LicenseHolder NO_LICENSE = new LicenseHolder(null);
    
}
