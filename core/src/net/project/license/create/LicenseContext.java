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
package net.project.license.create;

import net.project.base.property.PropertyProvider;
import net.project.gui.error.IErrorProvider;
import net.project.license.InvalidLicenseCertificateException;
import net.project.license.InvalidLicenseKeyException;
import net.project.license.LicenseException;
import net.project.license.LicenseKeyMismatchException;
import net.project.license.LicenseManager;
import net.project.license.LicenseNotFoundException;
import net.project.license.LicenseResult;
import net.project.license.LicenseResultCode;
import net.project.license.LicenseStatus;
import net.project.license.LicenseStatusCode;
import net.project.persistence.PersistenceException;

/**
 * Provides a structure for storing the registration context for creating a
 * license.  Currently when a user registers, they may specify a charge code,
 * license key or request a trial license.  If those options are expanded,
 * this class should be modified to incorporate them.
 */
public class LicenseContext implements java.io.Serializable, IErrorProvider {

    /**
     * The system default charge code property name, currently
     * <code>prm.global.license.chargecode.defaultvalue</code>.
     */
    private static final String DEFAULT_CHARGE_CODE_PROPERTY = "prm.global.license.chargecode.defaultvalue";

    /**
     * The system default license key property name, currently
     * <code>prm.global.license.licensekey.defaultvalue</code>.
     */
    private static final String DEFAULT_LICENSE_KEY_PROPERTY = "prm.global.license.licensekey.defaultvalue";

    /** The current registration license selection. */
    private LicenseSelectionType selectionType = null;

    /** The current charge code. */
    private String chargeCode = null;

    /** The current entered license key. */
    private String enteredLicenseKey = null;

    /** The id of the person for whom a license is being associated. */
    private String currentPersonID = null;

    /**
     * A string which shows the last four digits of the credit card number in
     * the context of how the card is formatted.  That is, for card number
     * 4111-1111-1111-1111, this string should hold XXXX-XXXX-XXXX-1111.
     */
    private String creditCardNumber = null;

    /**
     * An int which contains the number of licenses that a user paid for on a
     * credit card.
     */
    private int creditCardLicenseCount = 1;

    /**
     * Int value specifying which month the credit card expires in.
     */
    private int creditCardExpirationMonth;
    /**
     * Int value specifying which year the credit card expires in.
     */
    private int creditCardExpirationYear;

    /**
     * Creates a new LicenseContext, defaulting to Charge Code selected.
     */
    public LicenseContext() {
        setSelectionType(LicenseSelectionType.ENTERED_LICENSE_KEY);
    }

    /**
     * Clears this license context.
     */
    public void clear() {
        setSelectionType(LicenseSelectionType.ENTERED_LICENSE_KEY);
        this.chargeCode = null;
        this.enteredLicenseKey = null;
        this.currentPersonID = null;
    }

    /**
     * Sets the selection type that the user has selected based on the sepcified
     * id.
     * @param typeID the id of the type selected by the user
     * @see LicenseSelectionType#getID
     * @see #getSelectionType
     */
    public void setSelectionTypeID(String typeID) {
        setSelectionType(LicenseSelectionType.forID(typeID));
    }

    /**
     * Returns the id of the type that has been selected by the user.
     * @return the selection type id, or <code>null</code> if a valid one
     * has not yet been specified
     * @see #setSelectionTypeID
     */
    public String getSelectionTypeID() {
        String typeID = null;

        if (getSelectionType() != null) {
            typeID = getSelectionType().getID();
        }

        return typeID;
    }


    /**
     * Sets the selection type.
     * @param type the selection type
     * @see #getSelectionType
     */
    public void setSelectionType(LicenseSelectionType type) {
        this.selectionType = type;
        clearValues();
    }


    /**
     * Returns the selection type.
     * @return the seleciton type
     * @see #setSelectionType
     */
    public LicenseSelectionType getSelectionType() {
        return this.selectionType;
    }


    /**
     * Clears the values entered based on the current selection type.
     * For example, if the current selection is <code>LicenseSelectionType.CHARGE_CODE</code>
     * then any entered license key is cleared out.
     */
    public void clearValues() {
        if (getSelectionType() != null) {
            if (getSelectionType().equals(LicenseSelectionType.CHARGE_CODE)) {
                setEnteredLicenseKey(null);

            } else if (getSelectionType().equals(LicenseSelectionType.ENTERED_LICENSE_KEY)) {
                setChargeCode(null);

            } else if (getSelectionType().equals(LicenseSelectionType.TRIAL)) {
                setEnteredLicenseKey(null);
                setChargeCode(null);

            } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_CHARGE_CODE)) {
                setEnteredLicenseKey(null);
                setChargeCode(null);

            } else if (getSelectionType().equals(LicenseSelectionType.CREDIT_CARD)) {
                setEnteredLicenseKey(null);
                setCreditCardNumber(null);

            } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_LICENSE_KEY)) {
                setEnteredLicenseKey(null);
                setChargeCode(null);

            }
        }

    }

    /**
     * Sets the charge code entered for registration.
     * The charge code is simply a text string
     * @param chargeCode the charge code; whitespace is trimmed
     * from the start and end of the charge code
     * @see #getChargeCode
     */
    public void setChargeCode(String chargeCode) {
        if (chargeCode != null) {
            this.chargeCode = chargeCode.trim();
        } else {
            this.chargeCode = null;
        }
    }

    /**
     * Returns the charge code previously set.
     * @return the charge code
     * @see #setChargeCode
     */
    public String getChargeCode() {
        return this.chargeCode;
    }

    /**
     * Sets the license key manually entered for registration.
     * @param licenseKey the license key entered; whitespace is
     * trimmed from the start and end of the key
     * @see #getEnteredLicenseKey
     */
    public void setEnteredLicenseKey(String licenseKey) {
        if (licenseKey != null) {
            this.enteredLicenseKey = licenseKey.trim();
        } else {
            this.enteredLicenseKey = null;
        }
    }

    /**
     * Returns the enetered license key previously set.
     * @return the entered license key
     * @see #setEnteredLicenseKey
     */
    public String getEnteredLicenseKey() {
        return this.enteredLicenseKey;
    }


    /**
     * Returns the system default charge code.
     * This is currently specified by the property <code>prm.global.license.chargecode.defaultvalue</code>.
     * @return the default charge code
     */
    public String getDefaultChargeCode() {
        return net.project.base.property.PropertyProvider.get(DEFAULT_CHARGE_CODE_PROPERTY);
    }

    /**
     * Returns the system default license key.
     * This is currently specified by the property <code>prm.global.license.licensekey.defaultvalue</code>.
     * @return the default license key
     */
    public String getDefaultLicenseKey() {
        return net.project.base.property.PropertyProvider.get(DEFAULT_LICENSE_KEY_PROPERTY);
    }

    /**
     * Get a displayable version of the credit card.  For the credit card number
     * 4111-1111-1111-1111, this would display XXXX-XXXX-XXXX-1111.
     *
     * @return a <code>String</code> value containing a formatted and safe
     * credit card number.
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Set a version of the credit card number that will be displayed to users.
     * This should *NOT* contain the complete credit card number.  If it does,
     * it will be converted to the proper format.
     *
     * @param creditCardNumber a <code>String</code> containing a credit card
     * number, either in protected or unprotected format.
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Get the month in which the credit card expires.
     *
     * @return a <code>int</code> value containing the month in which the credit
     * card expires.  This should be an int value between 1 and 12.
     */
    public int getCreditCardExpirationMonth() {
        return creditCardExpirationMonth;
    }

    /**
     * Set an integer specifying which month the credit card expires in.
     *
     * @param creditCardExpirationMonth a <code>int</code> value specifying
     * which month the credit card expires in.  This should be a value between
     * 1 and 12.
     */
    public void setCreditCardExpirationMonth(int creditCardExpirationMonth) {
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    /**
     * Get an int containing the four digit year in which the credit card
     * expires.
     *
     * @return a <code>int</code> value containing the four digit year in which
     * the credit card expires.
     */
    public int getCreditCardExpirationYear() {
        return creditCardExpirationYear;
    }

    /**
     * Set the year in which the credit card used to purchase this license
     * expires.
     *
     * @param creditCardExpirationYear a <code>int</code> value containing a
     * four digit year in which the credit card expires.
     */
    public void setCreditCardExpirationYear(int creditCardExpirationYear) {
        this.creditCardExpirationYear = creditCardExpirationYear;
    }

    /**
     * Sets the current person id, used for validating a license.
     * This is necessary to check a person's history of licenses.
     * @param personID the id of the person for whom a license is being
     * acquired
     * @see #getCurrentPersonID
     */
    public void setCurrentPersonID(String personID) {
        this.currentPersonID = personID;
    }

    /**
     * Returns the current person id.
     * @return the current person id
     * @see #setCurrentPersonID
     */
    private String getCurrentPersonID() {
        return this.currentPersonID;
    }

    //
    // Validation Routines
    //


    /**
     * Checks that the entered information is valid and that the user may
     * preliminarliy register with a license.
     */
    public void validate() throws LicenseException, PersistenceException {

        // Check values are filled in
        if (getSelectionType() == null) {

            this.validationErrors.put("selectionTypeID", PropertyProvider.get("prm.license.context.validate.error.selectoption.message"));

        } else if (getSelectionType().equals(LicenseSelectionType.CHARGE_CODE) &&
                (getChargeCode() == null || getChargeCode().length() == 0)) {

            this.validationErrors.put("chargeCode", net.project.base.property.PropertyProvider.get("prm.global.license.create.chargecode.message"));

        } else if (getSelectionType().equals(LicenseSelectionType.ENTERED_LICENSE_KEY) &&
                (getEnteredLicenseKey() == null || getEnteredLicenseKey().length() == 0)) {

            this.validationErrors.put("enteredLicenseKey", PropertyProvider.get("prm.license.context.validate.error.enterkey.message"));

        } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_CHARGE_CODE) &&
                getDefaultChargeCode() == null) {

            this.validationErrors.put("defaultChargeCode", PropertyProvider.get("prm.license.context.validate.error.defaultcodeavailable.message"));
        } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_LICENSE_KEY) &&
                getDefaultLicenseKey() == null || getDefaultLicenseKey().length() == 0) {

            this.validationErrors.put("defaultLicenseKey", PropertyProvider.get("prm.license.context.validate.error.defaultlicensekeyavailable.message"));

        } else {

            // All values are filled in
            // Now check that the user may proceed
            if (getSelectionType().equals(LicenseSelectionType.CHARGE_CODE)) {
                // Currently no validation is done on the charge code
                // Do nothing

            } else if (getSelectionType().equals(LicenseSelectionType.ENTERED_LICENSE_KEY)) {
                // Check the license key
                validateAvailableKey("enteredLicenseKey", getEnteredLicenseKey());

            } else if (getSelectionType().equals(LicenseSelectionType.TRIAL)) {
                // Check that the user may select a trial license
                validateTrialAvailable();

            } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_CHARGE_CODE)) {
                // No validation to do

            } else if (getSelectionType().equals(LicenseSelectionType.CREDIT_CARD)) {
                // No validation to do

            } else if (getSelectionType().equals(LicenseSelectionType.DEFAULT_LICENSE_KEY)) {
                // Check the default license key
                validateAvailableKey("defaultLicenseKey", getDefaultLicenseKey());

            } else {
                // Unhandled selection type
                throw new LicenseException("An error occurred validating the license information: Unhandled selection type '" + getSelectionType() + "'");
            }

        }

    }


    /**
     * Validates that the specified display license key is available for use.
     * Any error messages are placed in the errors table.
     * @param viewInputField the input field on the view to which the
     * validation is applicable
     * @param licenseKey the display license key to validate
     * @throws PersistenceException if some database error occurs loading a
     * license
     * @throws LicenseException if a license is invalid or corrupt or cannot
     * be parsed
     */
    private void validateAvailableKey(String viewInputField, String licenseKey)
            throws PersistenceException, LicenseKeyMismatchException, InvalidLicenseCertificateException, LicenseException {

        LicenseManager manager = new LicenseManager();

        try {

            LicenseStatus status = manager.checkLicenseStatusForDisplayKey(licenseKey);

            if (status.getCode().equals(LicenseStatusCode.CANCELED)) {
                // The license is cancelled
                this.validationErrors.put(viewInputField, status.getMessage());

            } else if (status.getCode().equals(LicenseStatusCode.DISABLED)) {
                // The license is disabled
                this.validationErrors.put(viewInputField, status.getMessage());

            } else {

                // Not cancelled or disabled
                // Continue to check whether the license models allow it to be
                // used

                LicenseResult licenseResult = manager.checkLicenseAvailableForUseForDisplayKey(licenseKey);

                if (licenseResult.getCode().equals(LicenseResultCode.MISSING)) {
                    this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.keydoesnotexist.message"));

                } else if (licenseResult.getCode().equals(LicenseResultCode.CONSTRAINT_EXCEEDED)) {
                    String message = (licenseResult.hasMessage() ? licenseResult.getMessage() : PropertyProvider.get("prm.license.context.validate.error.keyexpired.message"));
                    this.validationErrors.put(viewInputField, message);

                } else if (licenseResult.getCode().equals(LicenseResultCode.FAILURE)) {
                    this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.checkingproblem.message"));

                } else if (licenseResult.getCode().equals(LicenseResultCode.VALID)) {
                    // Good, the license is valid
                    // Say nothing

                } else if (licenseResult.getCode().equals(LicenseResultCode.CERTIFICATE_KEY_MISMATCH)) {
                    // Key in license doesn't match key in license cert
                    // Some has messed with cert
                    this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.keymodified.message"));

                } else {
                    // Should never occur.  This implies a license code was returned
                    // that we did not know about
                    // Throw up an error anyway
                    this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.resultunknown.message"));

                }

            }

        } catch (LicenseNotFoundException e) {
            // License was not found for the key
            this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.keynotfound.message"));

        } catch (InvalidLicenseKeyException e) {
            // The entered key is garbage; it cannot be parsed
            this.validationErrors.put(viewInputField, PropertyProvider.get("prm.license.context.validate.error.keyinvalid.message"));

        }

    }


    /**
     * Validates that a trial license is available to the user.
     * A trial license may not be available if they have already used one.
     */
    private void validateTrialAvailable() {

        try {
            if (getCurrentPersonID() == null || getCurrentPersonID().length() == 0) {
                // There is no current person context
                // Mostly occurs when a user is registering
                // Do nothing

            } else {

                // Check if user has had a trial license
                // and add error if so
                if (net.project.license.LicenseHistory.hasHadTrialLicense(getCurrentPersonID())) {
                    this.validationErrors.put(null, PropertyProvider.get("prm.license.context.validate.error.alreadyhadtrial.message"));

                }

            }

        } catch (LicenseException e) {
            // Problem (likely persistence) checking license availability
            // Prevent ability to use trial license
            this.validationErrors.put(null, PropertyProvider.get("prm.license.context.validate.error.problemcheckingtrial.message"));

        }

    }


    //
    // Implementing IErrorProvider
    //

    private net.project.gui.error.ValidationErrors validationErrors = new net.project.gui.error.ValidationErrors();

    /**
     * Clears all errors.
     */
    public void clearErrors() {
        validationErrors.clearErrors();
    }

    /**
     * Indicates whether there are any errors
     * @return true if there are errors; false otherwise
     */
    public boolean hasErrors() {
        return validationErrors.hasErrors();
    }

    /**
     * Gets the Error Flag for the Field.  This method is used for
     * flagging a field label as having an error.  If an error is present
     * for the field with the specified id, the specified label is returned
     * but formatted to indicate there is an error.  Currently this uses
     * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
     * for the field with the specified id, the label is returned untouched.
     * @param fieldID the id of the field which may have the error
     * @param label the label to modify to indicate there is an error
     * @return the HTML formatted label
     */
    public String getFlagError(String fieldID, String label) {
        return validationErrors.getFlagErrorHTML(fieldID, label);
    }

    /**
     * Gets the Error Message for the Field.
     * @param fieldID  the id of the field for which to get the error message
     * @return the HTML formatted error message
     */
    public String getErrorMessage(String fieldID) {
        return validationErrors.getErrorMessageHTML(fieldID);
    }

    /**
     * Gets the Error Message for the Field
     * @return HTML formatted error messages
     */
    public String getAllErrorMessages() {
        return validationErrors.getAllErrorMessagesHTML();
    }

    /**
     * Get the number of licenses a user paid for on a credit card.
     *
     * @return a <code>int</code> containing the number of users licenses a user
     * paid for when purchasing this license.
     */
    public int getCreditCardLicenseCount() {
        return creditCardLicenseCount;
    }

    /**
     * Set the number of licenses a user paid for on a credit card.
     *
     * @param creditCardLicenseCount a <code>int</code> value indicating the
     * number of licenses paid for on the credit card.
     */
    public void setCreditCardLicenseCount(int creditCardLicenseCount) {
        this.creditCardLicenseCount = creditCardLicenseCount;
    }

    //
    // End of IErrorProvider
    //

}
