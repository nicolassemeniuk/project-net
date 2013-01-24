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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.creditcard.verisign;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.project.base.property.PropertyProvider;
import net.project.creditcard.CreditCardResultType;
import net.project.creditcard.ICreditCardProcessingResults;
import net.project.util.Validator;

/**
 * Class which decodes results that VeriSign emits from a credit card
 * transaction.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class VerisignCreditCardProcessingResults implements ICreditCardProcessingResults {
    /** The Project.Net transaction ID. */
    private String transactionID;
    /** PNREF variable which is a unique id for this transaction. */
    private String vendorTransactionID;
    /** The "Result" variable which contains a VeriSign-specific result code. */
    private VerisignTransactionResultCode resultCode;  //Result
    /**
     * Message returned from the vendor.  Generally, we ignore this so we can
     * supply our own message based on the result code.
     */
    private String responseMessage;
    /** This authorization code for the transaction. */
    private String authCode;
    /**
     * Describes whether the user-supplied address matched what was on file
     * at the credit card company as the user's billing address.
     */
    private AVSResponse avsAddressResponse;
    /**
     * Describes whether the user-supplied zip code matched what was on file
     * at the credit card company as the user's billing zip code.
     */
    private AVSResponse avsPostalCodeResponse;
    /**
     * Indicates whether the user has already submitted this transaction.
     */
    private boolean isDuplicateTransaction;

    /**
     * Initialize a <code>VerisignCreditCardProcessingResults</code> object
     * based on a <code>String</code> returned from VeriSign.
     *
     * @param resultString a <code>String</code> containing zero or more
     * parameters from VeriSign.
     * @param transactionID a <code>String</code> containing the Project.Net
     * identifier for this transaction.
     * @param isUSAddress a <code>String</code> indicating if the address
     * that the user entered as a billing address for the credit card was
     * located in the United States.
     */
    public VerisignCreditCardProcessingResults(String resultString, String transactionID, boolean isUSAddress) {
        Map parameterMap = new HashMap();

        StringTokenizer tok = new StringTokenizer(resultString, "&");
        while (tok.hasMoreTokens()) {
            String parameter = tok.nextToken();
            String name = parameter.substring(0, parameter.indexOf("="));
            String value = parameter.substring(parameter.indexOf("=")+1);
            parameterMap.put(name, value);
        }

        this.transactionID = transactionID;
        vendorTransactionID = (String)parameterMap.get("PNREF");
        resultCode = VerisignTransactionResultCode.getForID((String)parameterMap.get("RESULT"));
        responseMessage = (String)parameterMap.get("RESPMSG");
        authCode = (String)parameterMap.get("AUTHCODE");
        avsAddressResponse = AVSResponse.getForID((String)parameterMap.get("AVSADDR"));
        avsPostalCodeResponse = AVSResponse.getForID((String)parameterMap.get("AVSZIP"));

        //If AVS failed and result code is success, change the result code
        boolean verifyAddresses = (PropertyProvider.isDefined("prm.global.creditcard.addressverification") ?
                PropertyProvider.getBoolean("prm.creditcard.addressverification") : true);
        verifyAddresses = verifyAddresses && isUSAddress;

        if (verifyAddresses) {
            if (resultCode == VerisignTransactionResultCode.APPROVED &&
               (avsAddressResponse == AVSResponse.NO || avsPostalCodeResponse == AVSResponse.NO)) {
                resultCode = VerisignTransactionResultCode.AVS_FAILED;
                responseMessage = "";
            }
        }

        //Check to see if this is a duplicate response
        String duplication = (String)parameterMap.get("DUPLICATE");
        isDuplicateTransaction = (!Validator.isBlankOrNull(duplication) &&
            duplication.trim().equals("1"));
    }

    /**
     * Get the result code returned in the "RESULT" parameter.
     *
     * @return a <code>VerisignTransactionResultCode</code> object.
     */
    public VerisignTransactionResultCode getResultCode() {
        return resultCode;
    }

    /**
     * Get the project.net unique database identifier for this transaction.
     *
     * @return a <code>String</code> containing the project.net identifier for
     * this transaction.
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Set the project.net unique database identifier for this transaction.
     *
     * @param transactionID a <code>String</code> containing the unique
     * identifier for this transaction.
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    /**
     * Get the Verisign "PNREF" parameter, which is an id for this transaction.
     *
     * @return a <code>String</code> containing the PNREF parameter.
     */
    public String getVendorTransactionID() {
        return vendorTransactionID;
    }


    /**
     * Get the response message returned directly from VeriSign.  In general,
     * this string shouldn't be used because it isn't internationalized.
     * Instead, use {#getResultCode().getMessage()}.
     *
     * @return a <code>String</code> containing the message that VeriSign
     * returned with this transaction.
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Get the authorization code returned by VeriSign.
     *
     * @return a <code>String</code> containing the authorization code.
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * Get an <code>AVSResponse</code> object which indicates whether the
     * address provided for the credit card matches what is on file for the
     * user at their credit card company.
     *
     * @return a <code>AVSResponse</code> object indicating if the user's
     * address is correct, incorrect, or if address checking isn't supported
     * by the vendor for this address.
     */
    public AVSResponse getAvsAddressResponse() {
        return avsAddressResponse;
    }

    /**
     * Get an <code>AVSResponse</code> object which indicates whether the
     * zip code provided for the credit card matches what is on file for the
     * user at their credit card company.
     *
     * @return a <code>AVSResponse</code> object indicating if the user's
     * zip code is correct, incorrect, or if address checking isn't supported
     * by the vendor for this zip code.
     */
    public AVSResponse getAvsPostalCodeResponse() {
        return avsPostalCodeResponse;
    }

    /**
     * Get a {@link net.project.creditcard.CreditCardResultType} object which
     * gives the general category of a credit card response.  There are few of
     * these and they include things like "NETWORK_ERROR" and "SUCCESS".
     *
     * @return a <code>CreditCardResultType</code> object which indicates in
     * general terms if the transaction was successful, and if not why.
     */
    public CreditCardResultType getResultType() {
        return getResultCode().getResultType();
    }

    /**
     * Get any specific messages that the credit card processor returned.  It is
     * anticipated that anyone who implements this message will do what is
     * necessary to make these messages appropriate for a user.
     *
     * @return a <code>String</code> containing a user-centric message
     * explaining an error that might have occurred.
     */
    public String getMessage() {
        StringBuffer messageToReturn = new StringBuffer();
        String resultCodeString = getResultCode().getMessage();
        String responseMessage = getResponseMessage();

        messageToReturn.append(getResultType().toString()).append("  ");
        messageToReturn.append(resultCodeString);

        //Make sure the response message isn't the same as the result code string.
        //We don't want ugly duplication.
        if (responseMessage.indexOf(resultCodeString) == -1) {
            messageToReturn.append("  ").append(getResponseMessage());
        }

        return messageToReturn.toString();
    }

    /**
     * Indicates if the user is attempting to submit the same credit card
     * transaction twice.
     *
     * @return a <code>boolean</code> value indicating if the same transaction
     * is being submitted twice.
     */
    public boolean isDuplicateTransaction() {
        return isDuplicateTransaction;
    }
}
