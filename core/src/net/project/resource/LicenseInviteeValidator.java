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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.util.Collection;
import java.util.Iterator;

import net.project.license.License;
import net.project.license.PersonLicense;

/**
 * Provides convenient methods for validating an invitee to a license.
 */
public class LicenseInviteeValidator
        extends InviteeValidator {

    private net.project.license.License license = null;

    /**
     * Creates an empty InviteeValidator.
     * All validation methods will return positive results.
     */
    public LicenseInviteeValidator() {
        // Do nothing
    }

    /**
     * Creates an InviteeValidator for a single invitee.
     * @param license the license to validate against
     * @param invitee the invitee to validate
     */
    public LicenseInviteeValidator(License license, Invitee invitee) {
        this.license = license;
        this.inviteeList.add(invitee);
    }

    /**
     * Creates an InviteeValidator for multiple invitees.
     * @param license the license to validate against
     * @param invitees the invitees to validate
     */
    public LicenseInviteeValidator(License license, Collection invitees) {
        this.license = license;
        this.inviteeList.addAll(invitees);
    }

    public boolean isAlreadyInvited() {

        this.invalidInviteeList.clear();
        this.isInvalid = false;

        // Iterate over all invitees
        // Check each for license to which they are associated;
        // if one is already associated to this license, add the
        // invitee to the error list
        // Note that we iterate over ALL invitees so we can provide
        // a full report
        net.project.license.License inviteeLicense = null;

        for (Iterator it = this.inviteeList.iterator(); it.hasNext();) {
            Invitee nextInvitee = (Invitee) it.next();

            boolean isAlreadyInvited = false;
            inviteeLicense = null;

            nextInvitee.resolveUserIDForInvitee();

            if (nextInvitee.isInviteeRegisteredUser()) {
                try {

                    Person person = new Person(nextInvitee.getID());
                    person.load();
                    net.project.license.PersonLicense personLicense = new PersonLicense(person);
                    inviteeLicense = personLicense.getCurrentLicense();
                } catch (net.project.license.LicenseNotFoundException lnfe) {
                    inviteeLicense = null;

                } catch (net.project.license.InvalidLicenseCertificateException e) {
                    inviteeLicense = null;

                } catch (net.project.license.LicenseKeyMismatchException e) {
                    inviteeLicense = null;

                } catch (net.project.persistence.PersistenceException pe) {
                    inviteeLicense = null;
                }

                if (inviteeLicense != null && (inviteeLicense.getID().equals(this.license.getID()))) {
                    isAlreadyInvited = true;
                }
            }

            if (isAlreadyInvited) {
                this.invalidInviteeList.add(nextInvitee);
            }
        }

        // If we have an erroneous invitee
        // then return that fact
        if (!this.invalidInviteeList.isEmpty()) {
            this.isInvalid = true;
        }

        return this.isInvalid;
    }

    /**
     * Always returns empty string.
     * @return the empty string
     */
    public String getInviteeStatus() {
        return "";
    }

}
