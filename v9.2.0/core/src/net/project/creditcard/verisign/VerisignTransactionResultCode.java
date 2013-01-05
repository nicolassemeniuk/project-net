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

import net.project.base.property.PropertyProvider;
import net.project.creditcard.CreditCardResultType;
import net.project.creditcard.IProcessorResultCode;

/**
 * A typed enumeration of VerisignTransactionResultCode classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class VerisignTransactionResultCode implements IProcessorResultCode {
    /** This list contains all of the possible types for this typed enumeration. */
    private static Map types = new HashMap();

    /** Code for an approved transaction. */
    public static final VerisignTransactionResultCode APPROVED =
        new VerisignTransactionResultCode(0, "prm.global.creditcard.verisign.transactioncode.approved",
            CreditCardResultType.SUCCESS);

    /**
     * This result code isn't really offered by Verisign because credit card
     * processors don't reject credit cards based on bad address information.
     * It is the responsibility of merchants to do so.
     */
    public static final VerisignTransactionResultCode AVS_FAILED =
        new VerisignTransactionResultCode(-999, "prm.global.creditcard.verisign.transactioncode.avsfailed",
            CreditCardResultType.TRANSACTION_DECLINED_ERROR);

    //
    //Results for communications errors
    //

    /** Thrown if we couldn't reach the VeriSign server. */
    public static final VerisignTransactionResultCode FAILED_TO_CONNECT_TO_HOST =
        new VerisignTransactionResultCode(-1, "prm.global.creditcard.verisign.transactioncode.failedtoconnecttohost",
            CreditCardResultType.NETWORK_ERROR);
    /** Thrown if the host name cannot be found in DNS. */
    public static final VerisignTransactionResultCode FAILED_TO_RESOLVE_HOSTNAME =
        new VerisignTransactionResultCode(-2, "prm.global.creditcard.verisign.transactioncode.failedtoresolvehostname",
            CreditCardResultType.CONFIGURATION_ERROR);
    /** Thrown if we couldn't start SSL.  This usually indicates a missing jar. */
    public static final VerisignTransactionResultCode FAILED_TO_INITIALIZE_SSL_CONTEXT =
        new VerisignTransactionResultCode(-5, "prm.global.creditcard.verisign.transactioncode.failedtoinitializessl",
            CreditCardResultType.INTERNAL_ERROR);
    /** Indicates that the parameter list is malformed. */
    public static final VerisignTransactionResultCode PARAMETER_LIST_ERROR_AMPERSAND_IN_NAME =
        new VerisignTransactionResultCode(-6, "prm.global.creditcard.verisign.transactioncode.ampersandinparametername",
            CreditCardResultType.INTERNAL_ERROR);
    /** Indicates that the parameter list is malformed. */
    public static final VerisignTransactionResultCode PARAMETER_LIST_INVALID_NAME_CLAUSE =
        new VerisignTransactionResultCode(-7, "prm.global.creditcard.verisign.transactioncode.invalidnamebracketclause",
            CreditCardResultType.INTERNAL_ERROR);
    /** Internal SSL Error.  Could be the result of a setup problem. */
    public static final VerisignTransactionResultCode SSL_FAILED_TO_CONNECT =
        new VerisignTransactionResultCode(-8, "prm.global.creditcard.verisign.transactioncode.sslfailedtoconnect",
            CreditCardResultType.NETWORK_ERROR);
    /** Internal SSL Error. */
    public static final VerisignTransactionResultCode SSL_READ_FAILED =
        new VerisignTransactionResultCode(-9, "prm.global.creditcard.verisign.transactioncode.sslreadfailed",
            CreditCardResultType.NETWORK_ERROR);
    /** Internal SSL Error. */
    public static final VerisignTransactionResultCode SSL_WRITE_FAILED =
        new VerisignTransactionResultCode(-10, "prm.global.creditcard.verisign.transactioncode.sslwritefailed",
            CreditCardResultType.NETWORK_ERROR);
    /** The username or password provided for the proxy was wrong, check SaJava.ini. */
    public static final VerisignTransactionResultCode PROXY_AUTHORIZATION_FAILED =
        new VerisignTransactionResultCode(-11, "prm.global.creditcard.verisign.transactioncode.proxyauthenticationfailed",
            CreditCardResultType.CONFIGURATION_ERROR);
    /**
     * It took too long for Verisign to respond.  Either the timeout period in
     * our implementation needs to be increased, or something is wrong with
     * VeriSign's site.
     */
    public static final VerisignTransactionResultCode TIMEOUT_WAITING_FOR_RESPONSE =
        new VerisignTransactionResultCode(-12, "prm.global.creditcard.verisign.transactioncode.timeoutwaitingforresponse",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode SELECT_FAILURE =
        new VerisignTransactionResultCode(-13, "prm.global.creditcard.verisign.transactioncode.selectfailure",
            CreditCardResultType.NETWORK_ERROR);
    /** The VeriSign server is overloaded. */
    public static final VerisignTransactionResultCode TOO_MANY_CONNECTIONS =
        new VerisignTransactionResultCode(-14, "prm.global.creditcard.verisign.transactioncode.toomanyconnections",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode FAILED_TO_SET_SOCKET_OPTIONS =
        new VerisignTransactionResultCode(-15, "prm.global.creditcard.verisign.transactioncode.failedtosetsocketoptions",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode PROXY_READ_FAILED =
        new VerisignTransactionResultCode(-20, "prm.global.creditcard.verisign.transactioncode.proxyreadfailed",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode PROXY_WRITE_FAILED =
        new VerisignTransactionResultCode(-21, "prm.global.creditcard.verisign.transactioncode.proxywritefailed",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode FAILED_TO_INITIALIZE_SSL_CERTIFICATE =
        new VerisignTransactionResultCode(-22, "prm.global.creditcard.verisign.transactioncode.failedtoinitializesslcert",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode HOST_ADDRESS_NOT_SPECIFIED =
        new VerisignTransactionResultCode(-23, "prm.global.creditcard.verisign.transactioncode.hostaddressnotspecified",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode INVALID_TRANSACTION_TYPE_ERROR =
        new VerisignTransactionResultCode(-24, "prm.global.creditcard.verisign.transactioncode.invalidtransactiontype",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode FAILED_TO_CREATE_A_SOCKET =
        new VerisignTransactionResultCode(-25, "prm.global.creditcard.verisign.transactioncode.failedtocreateasocket",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode FAILED_TO_INITIALIZE_SOCKET_LAYER =
        new VerisignTransactionResultCode(-26, "prm.global.creditcard.verisign.transactioncode.failedtoinitializesocketlayer",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode PARAMETER_LIST_FORMAT_ERROR_INVALID_NAME_LENGTH_CLAUSE =
        new VerisignTransactionResultCode(-27, "prm.global.creditcard.verisign.transactioncode.invalidnamelengthclause",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode PARAMETER_LIST_FORMAT_ERROR_NAME =
        new VerisignTransactionResultCode(-28, "prm.global.creditcard.verisign.transactioncode.parameternameformat",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode FAILED_TO_INITIALIZE_SSL_CONNECTION =
        new VerisignTransactionResultCode(-29, "prm.global.creditcard.verisign.transactioncode.failedtoinitializessl",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode INVALID_TIMEOUT_VALUE =
        new VerisignTransactionResultCode(-30, "prm.global.creditcard.verisign.transactioncode.invalidtimeoutvalue",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode THE_CERTIFICATE_CHAIN_DID_NOT_VALIDATE_NO_LOCAL_CERTIFICATE_FOUND =
        new VerisignTransactionResultCode(-31, "prm.global.creditcard.verisign.transactioncode.nolocalcertfound",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode THE_CERTIFICATE_CHAIN_DID_NOT_VALIDATE_COMMON_NAME_DID_NOT_MATCH_URL =
        new VerisignTransactionResultCode(-32, "prm.global.creditcard.verisign.transactioncode.certificationcommonname",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode OUT_OF_MEMORY =
        new VerisignTransactionResultCode(-99, "prm.global.creditcard.verisign.transactioncode.outofmemory",
            CreditCardResultType.INTERNAL_ERROR);


    /** Information provided for the merchant account was incorrect. */
    public static final VerisignTransactionResultCode USER_AUTHENTICATION_FAILED =
        new VerisignTransactionResultCode(1, "prm.global.creditcard.verisign.transactioncode.userauthenticationfailed",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    /** The credit card processor doesn't support the credit card type that was submitted. */
    public static final VerisignTransactionResultCode INVALID_TENDER_TYPE =
        new VerisignTransactionResultCode(2, "prm.global.creditcard.verisign.transactioncode.invalidtendertype",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    /**
     * The transaction type is not appropriate for this transaction.  For
     * example, you cannot credit an authorization-only transaction.
     */
    public static final VerisignTransactionResultCode INVALID_TRANSACTION_TYPE =
        new VerisignTransactionResultCode(3, "prm.global.creditcard.verisign.transactioncode.invalidtransactiontype2",
            CreditCardResultType.INTERNAL_ERROR);
    /** Check the AMT parameter.  It is in an invalid format. */
    public static final VerisignTransactionResultCode INVALID_AMOUNT_FORMAT =
        new VerisignTransactionResultCode(4, "prm.global.creditcard.verisign.transactioncode.invalidamountformat",
            CreditCardResultType.INTERNAL_ERROR);
    /** Processor does not recognize your merchant account information. */
    public static final VerisignTransactionResultCode INVALID_MERCHANT_INFORMATION =
        new VerisignTransactionResultCode(5, "prm.global.creditcard.verisign.transactioncode.invalidmerchantinformation",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    /**
     * The request that was sent to VeriSign was incorrect.  One or more of the
     * fields was formatted incorrectly.
     */
    public static final VerisignTransactionResultCode FIELD_FORMAT_ERROR =
        new VerisignTransactionResultCode(7, "prm.global.creditcard.verisign.transactioncode.fieldformaterror",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode NOT_A_TRANSACTION_SERVER =
        new VerisignTransactionResultCode(8, "prm.global.creditcard.verisign.transactioncode.notatransactionserver",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode TOO_MANY_PARAMETERS_OR_INVALID_STREAM =
        new VerisignTransactionResultCode(9, "prm.global.creditcard.verisign.transactioncode.toomanyparameters",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode TOO_MANY_LINE_ITEMS =
        new VerisignTransactionResultCode(10, "prm.global.creditcard.verisign.transactioncode.toomanylineitems",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode CLIENT_TIME_OUT_WAITING_FOR_RESPONSE =
        new VerisignTransactionResultCode(11, "prm.global.creditcard.verisign.transactioncode.clienttimeout",
            CreditCardResultType.NETWORK_ERROR);
    /**
     * Either the user's account information was entered correctly, or the user
     * need to contact their credit card company.
     */
    public static final VerisignTransactionResultCode DECLINED =
        new VerisignTransactionResultCode(12, "prm.global.creditcard.verisign.transactioncode.declined",
            CreditCardResultType.TRANSACTION_DECLINED_ERROR);
    /**
     * The transaction was declined, but could be approved with verbal
     * authorization.  This is probably not really applicable to our
     * application.
     */
    public static final VerisignTransactionResultCode REFERRAL =
        new VerisignTransactionResultCode(13, "prm.global.creditcard.verisign.transactioncode.referral",
            CreditCardResultType.TRANSACTION_DECLINED_ERROR);
    public static final VerisignTransactionResultCode ORIGINAL_TRANSACTION_ID_NOT_FOUND =
        new VerisignTransactionResultCode(19, "prm.global.creditcard.verisign.transactioncode.originaltransactionidnotfound",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode CANNOT_FIND_CUSTOMER_REFERENCE_NUMBER =
        new VerisignTransactionResultCode(20, "prm.global.creditcard.verisign.transactioncode.cannotfindcustomerrefnumber",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode INVALID_ABA_NUMBER =
        new VerisignTransactionResultCode(22, "prm.global.creditcard.verisign.transactioncode.invalidabanumber",
            CreditCardResultType.INVALID_ACCOUNT_DATA);
    public static final VerisignTransactionResultCode INVALID_ACCOUNT_NUMBER =
        new VerisignTransactionResultCode(23, "prm.global.creditcard.verisign.transactioncode.invalidaccountnumber",
            CreditCardResultType.INVALID_ACCOUNT_DATA);
    public static final VerisignTransactionResultCode INVALID_EXPIRATION_DATE =
        new VerisignTransactionResultCode(24, "prm.global.creditcard.verisign.transactioncode.invalidexpirationdate",
            CreditCardResultType.INVALID_ACCOUNT_DATA);
    public static final VerisignTransactionResultCode INVALID_HOST_MAPPING =
        new VerisignTransactionResultCode(25, "prm.global.creditcard.verisign.transactioncode.invalidhostmapping",
            CreditCardResultType.CONFIGURATION_ERROR);
    public static final VerisignTransactionResultCode INVALID_VENDOR_ACCOUNT =
        new VerisignTransactionResultCode(26, "prm.global.creditcard.verisign.transactioncode.invalidvendoraccount",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    public static final VerisignTransactionResultCode INSUFFICIENT_PARTNER_PERMISSIONS =
        new VerisignTransactionResultCode(27, "prm.global.creditcard.verisign.transactioncode.insufficientpartnerpermission",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    public static final VerisignTransactionResultCode INSUFFICIENT_USER_PERMISSIONS =
        new VerisignTransactionResultCode(28, "prm.global.creditcard.verisign.transactioncode.insufficientuserpermission",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    public static final VerisignTransactionResultCode INVALID_XML_DOCUMENT =
        new VerisignTransactionResultCode(29, "prm.global.creditcard.verisign.transactioncode.invalidxmldocument",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode DUPLICATE_TRANSACTION =
        new VerisignTransactionResultCode(30, "prm.global.creditcard.verisign.transactioncode.duplicationtransaction",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode ERROR_IN_ADDING_THE_RECURRING_PROFILE =
        new VerisignTransactionResultCode(31, "prm.global.creditcard.verisign.transactioncode.erroraddingrecurringprofile",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode ERROR_IN_MODIFYING_THE_RECURRING_PROFILE =
        new VerisignTransactionResultCode(32, "prm.global.creditcard.verisign.transactioncode.errormodifyingrecurringprofile",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode ERROR_IN_CANCELLING_THE_RECURRING_PROFILE =
        new VerisignTransactionResultCode(33, "prm.global.creditcard.verisign.transactioncode.errorcancelingrecurringprofile",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode ERROR_IN_FORCING_THE_RECURRING_PROFILE =
        new VerisignTransactionResultCode(34, "prm.global.creditcard.verisign.transactioncode.errorforcingrecurringprofile",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode ERROR_IN_REACTIVATING_THE_RECURRING_PROFILE =
        new VerisignTransactionResultCode(35, "prm.global.creditcard.verisign.transactioncode.errorreactivatingrecurringprofile",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode OLTP_TRANSACTION_FAILED =
        new VerisignTransactionResultCode(36, "prm.global.creditcard.verisign.transactioncode.oltptransactionfailed",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode INSUFFICIENT_FUNDS_AVAILABLE_IN_ACCOUNT =
        new VerisignTransactionResultCode(50, "prm.global.creditcard.verisign.transactioncode.insufficientfunds",
            CreditCardResultType.TRANSACTION_DECLINED_ERROR);
    public static final VerisignTransactionResultCode GENERAL_ERROR =
        new VerisignTransactionResultCode(99, "prm.global.creditcard.verisign.transactioncode.generalerror",
            CreditCardResultType.UNRECOGNIZED_ERROR_TYPE);
    public static final VerisignTransactionResultCode TRANSACTION_TYPE_NOT_SUPPORTED_BY_HOST =
        new VerisignTransactionResultCode(100, "prm.global.creditcard.verisign.transactioncode.transactiontypenotsupported",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode TIME_OUT_VALUE_TOO_SMALL =
        new VerisignTransactionResultCode(101, "prm.global.creditcard.verisign.transactioncode.timeouttoosmall",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode PROCESSOR_NOT_AVAILABLE =
        new VerisignTransactionResultCode(102, "prm.global.creditcard.verisign.transactioncode.processornotavailable",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode ERROR_READING_RESPONSE_FROM_HOST =
        new VerisignTransactionResultCode(103, "prm.global.creditcard.verisign.transactioncode.errorreadingresponse",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode TIMEOUT_WAITING_FOR_PROCESSOR_RESPONSE =
        new VerisignTransactionResultCode(104, "prm.global.creditcard.verisign.transactioncode.timeoutwaitingforprocessor",
            CreditCardResultType.NETWORK_ERROR);
    /**
     * Make sure you have not already credited this transaction, or that this
     * transaction ID is for a creditable transaction. (For example, you cannot
     * credit an authorization.)
     */
    public static final VerisignTransactionResultCode CREDIT_ERROR =
        new VerisignTransactionResultCode(105, "prm.global.creditcard.verisign.transactioncode.crediterror",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode HOST_NOT_AVAILABLE =
        new VerisignTransactionResultCode(106, "prm.global.creditcard.verisign.transactioncode.hostnotavailable",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode DUPLICATE_SUPPRESSION_TIME_OUT =
        new VerisignTransactionResultCode(107, "prm.global.creditcard.verisign.transactioncode.duplicatesuppressiontimeout",
            CreditCardResultType.INTERNAL_ERROR);
    /**
     * See RESPMSG. Make sure the transaction ID entered has not already been
     * voided. If not, then look at the Transaction Detail screen for this
     * transaction to see if it has settled. (The Batch field is set to a number
     * greater than zero if the transaction has been settled). If the
     * transaction has already settled, your only recourse is a reversal (credit
     * a payment or submit a payment for a credit).
     */
    public static final VerisignTransactionResultCode VOID_ERROR =
        new VerisignTransactionResultCode(108, "prm.global.creditcard.verisign.transactioncode.voiderror",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode TIME_OUT_WAITING_FOR_HOST_RESPONSE =
        new VerisignTransactionResultCode(109, "prm.global.creditcard.verisign.transactioncode.timeoutwaitingforhost",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode CAPTURE_ERROR =
        new VerisignTransactionResultCode(111, "prm.global.creditcard.verisign.transactioncode.captureerror",
            CreditCardResultType.INTERNAL_ERROR);
    /**
     * Address and ZIP code do not match. An authorization may still exist on
     * the cardholder's account.  This error isn't thrown usually.  The merchant
     * needs to reject for AVS reasons.
     */
    public static final VerisignTransactionResultCode FAILED_AVS_CHECK =
        new VerisignTransactionResultCode(112, "prm.global.creditcard.verisign.transactioncode.failedavscheck",
            CreditCardResultType.INVALID_ACCOUNT_DATA);
    //public static final VerisignTransactionResultCode CANNOT_EXCEED_SALES_CAP =
    //    new VerisignTransactionResultCode(113, "Cannot exceed sales cap. For ACH transactions only. ");
    public static final VerisignTransactionResultCode MERCHANT_SALE_TOTAL_WILL_EXCEED_CAP =
        new VerisignTransactionResultCode(113, "prm.global.creditcard.verisign.transactioncode.salewillexceedcap",
            CreditCardResultType.MERCHANT_ACCOUNT_ERROR);
    /**
     * The CSC code that the user entered doesn't match what the credit card
     * company has on file.
     */
    public static final VerisignTransactionResultCode CARD_SECURITY_CODE_MISMATCH =
        new VerisignTransactionResultCode(114, "prm.global.creditcard.verisign.transactioncode.cscmismatch",
            CreditCardResultType.INVALID_ACCOUNT_DATA);
    public static final VerisignTransactionResultCode SYSTEM_BUSY =
        new VerisignTransactionResultCode(115, "prm.global.creditcard.verisign.transactioncode.systembusy",
            CreditCardResultType.NETWORK_ERROR);
    public static final VerisignTransactionResultCode VPS_INTERNAL_ERROR =
        new VerisignTransactionResultCode(116, "prm.global.creditcard.verisign.transactioncode.vpsinternalerror",
            CreditCardResultType.NETWORK_ERROR);
    /**
     * An attempt was made to submit a transaction that failed to meet the
     * security settings specified on the VeriSign Manager Security Settings
     * page. See Chapter 4; Configuring Account Security.
     */
    public static final VerisignTransactionResultCode FAILED_MERCHANT_RULE_CHECK =
        new VerisignTransactionResultCode(117, "prm.global.creditcard.verisign.transactioncode.failedmerchantrulecheck",
            CreditCardResultType.INTERNAL_ERROR);
    public static final VerisignTransactionResultCode INVALID_KEYWORDS_FOUND_IN_STRING_FIELDS =
        new VerisignTransactionResultCode(118, "prm.global.creditcard.verisign.transactioncode.invalidkeywordsfound",
            CreditCardResultType.INTERNAL_ERROR);
    /** An error which is unusual and hasn't been identified by VeriSign. */
    public static final VerisignTransactionResultCode GENERIC_HOST_ERROR =
        new VerisignTransactionResultCode(1000, "prm.global.creditcard.verisign.transactioncode.generichosterror",
            CreditCardResultType.UNRECOGNIZED_ERROR_TYPE);

    /**
     * The Result code that will be returned if getForID cannot find a matching
     * id in our result code objects.  Default is "GENERIC_HOST_ERROR".
     */
    public static final VerisignTransactionResultCode DEFAULT = GENERIC_HOST_ERROR;

    /**
     * Get the VerisignTransactionResultCode that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>VerisignTransactionResultCode</code> we want to find.
     * @return a <code>VerisignTransactionResultCode</code> corresponding to the supplied ID, or
     * the DEFAULT <code>VerisignTransactionResultCode</code> if one cannot be found.
     */
    public static VerisignTransactionResultCode getForID(String id) {
        VerisignTransactionResultCode toReturn = (VerisignTransactionResultCode)types.get(new Integer(id));

        if (toReturn == null) {
            toReturn = DEFAULT;
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this VerisignTransactionResultCode. */
    private int id;
    /** A token used to find a human-readable name for this VerisignTransactionResultCode. */
    private String displayToken;
    /** What category this message falls into. */
    private CreditCardResultType resultType;

    /**
     * Private constructor which creates a new VerisignTransactionResultCode instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     * @param resultType a <code>CreditCardResultType</code> object which
     * categorizes this result.
     */
    private VerisignTransactionResultCode(int id, String displayToken, CreditCardResultType resultType) {
        this.id = id;
        this.displayToken = displayToken;
        this.resultType = resultType;
        types.put(new Integer(id), this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return an <code>int</code> value containing the unique id for this
     * type.
     */
    public int getID() {
        return id;
    }

    /**
     * Indicates if the result code is successful, or something else.
     *
     * @return a <code>boolean</code> indicating whether the transaction was
     * successful.
     */
    public boolean isSuccessful() {
        return this.equals(APPROVED);
    }

    /**
     * A message describing the error.
     *
     * @return a <code>String</code> containing a message which describes this
     * error.
     */
    public String getMessage() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Get the general "result type" for this result code.  This is a category
     * for the error.
     *
     * @return a <code>CreditCardResultType</code> which categorizes this error.
     */
    public CreditCardResultType getResultType() {
        return resultType;
    }

    /**
     * Return a human-readable display name for this VerisignTransactionResultCode.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * VerisignTransactionResultCode.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
