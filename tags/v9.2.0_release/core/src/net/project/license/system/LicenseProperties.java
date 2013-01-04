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

 package net.project.license.system;

import javax.servlet.http.HttpServletRequest;

import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

/**
 * Provides system properties for licensing.
 * These are encapsulated to provide a convenient place to access all properties
 * relating to licensing.
 */
public class LicenseProperties implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns this session's instance of license properties.
     * The master properties are loaded from persistence store.
     * @throws LicenseException if there is a problem getting the license properties
     */
    public static LicenseProperties getInstance(HttpServletRequest request)
        throws LicenseException {

        return new LicenseProperties(request);
    }

    public static LicenseProperties getInstance() {
        return new LicenseProperties();
    }

    //
    // Instance members
    //

    /**
     * Creates a new LicenseProperties.
     * Requiring the request was a stupid idea; we'll let the properties worry
     * about how to get the current properties from the session
     */
    private LicenseProperties() {
        // Do nothing
    }

    /**
     * Creates a new LicenseProperties for the specified request.
     * The request is needed to access the session to get the property provider
     * (future mod:  currently backdoor session access used)
     * @param request the request
     */
    private LicenseProperties(HttpServletRequest request) {
        // Do nothing
    }


    /**
     * Indicates whether a license must be acquired during registration.
     * @return true if a license is required to be acquired during registration;
     * false otherwise
     */
    public boolean isLicenseRequiredAtRegistration() throws LicenseException,
        PersistenceException {

        return isLicenseEnabled();
    }

    /**
     * Indicates whether a valid license is required for a user during login.
     * @return true if a license is required to log in; false otherwise
     */
    public boolean isLicenseRequiredAtLogin() throws LicenseException,
        PersistenceException {

        return isLicenseEnabled();
    }


    /**
     * Indicates whether licensing is enabled globally.
     * @return true if licensing is enabled; false otherwise.  Returns false
     * if and only if there is a master property set to 0; returns true if the
     * property does not exist or is set to true.
     */
    private boolean isLicenseEnabled() throws LicenseException, PersistenceException {
        boolean isLicenseEnabled = true;

        Property licenseEnabledProperty = getMasterProperties().get(PropertyName.LICENSE_IS_ENABLED);

        if (licenseEnabledProperty != null) {
            isLicenseEnabled &= net.project.util.Conversion.toBoolean(licenseEnabledProperty.getValue());
        }

        //
        // NOTE
        // This value MUST NOT be cached.  By not caching, we ensure that it
        // it is possible to turn licensing on and off without requiring a
        // restart of the application server
        return isLicenseEnabled;
    }


    /**
     * Returns the default trial license period, in days.
     * @return the number of days
     */
    public int getDefaultTrialLicensePeriodDays()
        throws LicenseException, PersistenceException {
        Property trial = getMasterProperties().get(PropertyName.LICENSE_MODEL_TRIAL_PERIOD);
        return new Integer(trial.getValue()).intValue();
    }


    /**
     * Returns the current master properties.
     * Note that missing master properties returns a structure with empty
     * properties.
     * @return the Master properties
     */
    private MasterProperties getMasterProperties()
        throws LicenseException, PersistenceException {

        // Do not cache to ensure the master properties may be updated dynamically
        MasterProperties masterProperties = null;

        // Get the master properties
        masterProperties = MasterProperties.getInstance();

        return masterProperties;
    }

    /**
     * Returns the node id of the current installation.
     * This is actually based on the product installation id.
     */
    public net.project.license.model.NodeID getCurrentNodeID() throws
        LicenseException, PersistenceException {

        Property productInstallationIDProperty = getMasterProperties().get(PropertyName.LICENSE_PRODUCT_INSTALLATION_ID);
        String piid = productInstallationIDProperty.getValue();
        // Create the NodeID from the product installation id
        return net.project.license.model.NodeID.createNodeID(piid);
    }
}
