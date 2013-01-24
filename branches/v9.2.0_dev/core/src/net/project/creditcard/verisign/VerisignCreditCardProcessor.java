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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.project.base.PnetException;
import net.project.base.money.Money;
import net.project.base.property.PropertyProvider;
import net.project.creditcard.CreditCard;
import net.project.creditcard.ICreditCardProcessingResults;
import net.project.creditcard.ICreditCardProcessor;
import net.project.creditcard.IMerchantAccount;
import net.project.creditcard.InvalidChargeException;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.license.system.MasterProperties;
import net.project.license.system.PropertyName;
import net.project.security.EncryptionException;
import net.project.security.EncryptionManager;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.crypto.SecretKeyType;
import net.project.util.Validator;

import com.Verisign.payment.PFProAPI;

/**
 * This is a wrapper class that surrounds the credit card processing engine of
 * Project.net.
 *
 * @author Matthew Flower
 * @since Version 8.0
 */
public class VerisignCreditCardProcessor implements ICreditCardProcessor {
    private static SecretKeyType merchantAccountSecretKey = new SecretKeyType("") {
        private SecretKey key = new SecretKeySpec(new byte[] { -128, 78, -41, 0, 120, 12, 91, -55}, "Blowfish");
        public SecretKey getKey() throws EncryptionException {
            return key;
        }
    };

    /**
     * This constant represents the amount of time the VerisignCreditCardProcessor will
     * wait for a response from the credit card server.  In most circumstances,
     * the credit card processing is
     */
    private static final int TRANSACTION_TIME_OUT = 30;
    /**
     * The VeriSign server that we are going to connect to do processing.
     */
    private String serverName;
    /**
     * The port on which VeriSign's credit card transaction processor is
     * running.
     */
    private String serverPort;
    /**
     * The location on the hard drive of VeriSign's public key.  We require this
     * to connect to their site with SSL.
     */
    private String publicKeyLocation;
    /** IP Address of a proxy in between this object and VeriSign. */
    private String proxyAddress;
    /** Port of a proxy in between this object and VeriSign. */
    private int proxyPort;
    /** Username to bypass a proxy in between this object and VeriSign. */
    private String proxyUsername;
    /** Password to bypass a proxy in between this object and VeriSign. */
    private String proxyPassword;
    /** The account which will be credited by a sale. */
    private IMerchantAccount merchantAccount;

    /**
     * Creates new instance of <code>VerisignCreditCardProcessor</code>.  This
     * will fetch the servername, portname, and publickeylocation from the
     * SaJava.ini too.
     */
    public VerisignCreditCardProcessor() {
        serverName = SessionManager.getVerisignServerName();
        serverPort = SessionManager.getVerisignPortNumber();
        publicKeyLocation = SessionManager.getVerisignPublicKeyPath();
    }

    /**
     * This method allows a proxy to be set up which will exist between the
     * server running this software and VeriSign's servers.
     *
     * @param address a <code>String</code> containing a dotted-quad IP address
     * which is the IP address of the proxy server.
     * @param port an <code>int</code> containing the port that the proxy is
     * running on.
     * @param userName a <code>String</code> value representing the username that
     * is required to log onto a proxy.  Leave this blank if a username is not
     * required.
     * @param password a <code>String</code> value representing the password that
     * is required to log onto the proxy.  Leave this blank if the proxy server
     * does not require a password.
     */
    public void setupProxy(String address, int port, String userName, String password) {
        this.proxyAddress = address;
        this.proxyPort = port;
        this.proxyUsername = userName;
        this.proxyPassword = password;
    }

    /**
     * Set the merchant account which will be the destination for any money
     * charged to a credit card.
     *
     * @param account a <code>IMerchantAccount</code> which will be receiving
     * money from a credit card.
     */
    public void setMerchantAccount(IMerchantAccount account) {
        this.merchantAccount = account;
    }

    /**
     * Charge money to a credit card.
     *
     * @param creditCard a <code>CreditCard</code> object to which we are going
     * to charge money.
     * @param amount a <code>Money</code> object which represents the amount
     * that we are going to charge to the credit card.
     * @param uniqueTransactionID a <code>String</code> containing a globally
     * unique id.  The processor code is going to use this string to ensure
     * that the same transaction isn't submitted twice.
     * @param lineItemDesc a <code>String</code> object describing what the user
     * is purchasing.  This will appear in the record of the sale at VeriSign
     * and provides us a way to recover in case the user's card is charged, but
     * their license isn't created.
     * @param purchasingUser a <code>User</code> object who is doing the
     * purchasing.  This may or may not be the same as the information that
     * appears on the credit card.
     * @return a <code>ICreditCardProcessingResult</code> which indicates
     * whether the transaction was successful, and if not, what happened.
     * @throws PnetException if any error occurs during the credit card
     * processing.
     */
    public ICreditCardProcessingResults makePurchase(CreditCard creditCard,
        Money amount, String uniqueTransactionID, String lineItemDesc,
        User purchasingUser) throws PnetException {

        //Get the id of this site, we need to send that
        MasterProperties props = MasterProperties.getInstance();

        VerisignCreditCardProcessingResults authResults =
            (VerisignCreditCardProcessingResults)performTransaction(creditCard,
            amount, VerisignTransactionType.AUTHORIZATION, null,
            uniqueTransactionID, lineItemDesc, purchasingUser,
            props.get(PropertyName.LICENSE_PRODUCT_INSTALLATION_ID).getValue());
        ICreditCardProcessingResults toReturn;

        if (authResults.getResultCode() == VerisignTransactionResultCode.APPROVED &&
            !authResults.isDuplicateTransaction()) {
            //We don't send the unique transaction id when we do the delayed
            //capture.  We avoid this because the auth and the delayed capture
            //must have different unique id's in order for the delayed capture
            //to be recognized.  Also, we should know if it is a duplicate
            //because of the authorization
            toReturn = performTransaction(creditCard, amount,
                VerisignTransactionType.DELAYED_CAPTURE,
                authResults.getVendorTransactionID(), null, lineItemDesc, purchasingUser,
                props.get(PropertyName.LICENSE_PRODUCT_INSTALLATION_ID).getValue());
        } else {
            toReturn = authResults;
        }

        return toReturn;
    }

    /**
     * Charge a credit card a certain amount of money, transferring it to the
     * merchant account.
     *
     * @param creditCard a <code>CreditCard</code> object which is about to have
     * money debited from it.
     * @param amount a <code>Money</code> object representing the amount of
     * money to be debited.
     * @param transactionType a <code>VerisignTransactionType</code> object
     * which indicates if we are doing a Sale, Credit, Void, etc.
     * @param originalID a <code>String</code> value which contains the
     * transaction ID of a transaction that already occurred.  This is used
     * for delayed capture transactions.  Pass null or a blank string otherwise.
     * @param uniqueTransitID a <code>String</code> value which is unique to the
     * current transaction.  Some credit card processors implement a way to
     * allow you to send an ID.  If this ID is sent twice, the processor will
     * @param lineItemDesc a <code>String</code> which describes what the user
     * is receiving for their money.
     * @param purchasingUser a <code>User</code> object who is doing the
     * purchasing.  This may or may not be the same as the information that
     * appears on the credit card.
     * @return a <code>CreditCardProcessingResults</code> object which indicates
     * what occurred when this charge was attempted.
     * @throws InvalidChargeException if the currency is unsupported, or if the
     * credit card is invalid.
     */
    private ICreditCardProcessingResults performTransaction(CreditCard creditCard,
        Money amount, VerisignTransactionType transactionType,
        String originalID, String uniqueTransitID, String lineItemDesc, 
        User purchasingUser, String siteID) throws InvalidChargeException {

        //Don't allow credit transactions for the Pnet merchant account

        if (merchantAccount.getClass().getName().equals("net.project.creditcard.verisign.PNETMerchantAccount") &&
            transactionType == VerisignTransactionType.CREDIT) {
            throw new InvalidChargeException("Invalid transaction");
        }

        //Validity checks
        verifyInputParameters(creditCard, amount);

        PFProAPI processor = getProcessor();
        try {
            StringBuffer parmListSB = new StringBuffer();

            //Add the information needed for the merchant account
            parmListSB.append(getMerchantAccountInfo());

            //Add the credit card information
            parmListSB.append(getCreditCardInfo(creditCard));

            //Add the amount that we are going to charge
            parmListSB.append(getAmountInfo(amount));

            if (!Validator.isBlankOrNull(lineItemDesc)) {
                parmListSB.append(createValidParameter("&COMMENT1", lineItemDesc));
            }

            if (!Validator.isBlankOrNull(creditCard.getName())) {
                parmListSB.append(createValidParameter("&COMMENT2", creditCard.getName()));
            }

            if (!Validator.isBlankOrNull(originalID)) {
                parmListSB.append(createValidParameter("&ORIGID", originalID));
            }

            if (!Validator.isBlankOrNull(uniqueTransitID)) {
                parmListSB.append(createValidParameter("&DSGUID", uniqueTransitID));
            }

            if (purchasingUser != null) {
                if (!Validator.isBlankOrNull(purchasingUser.getEmail())) {
                    parmListSB.append(createValidParameter("&EMAIL", purchasingUser.getEmail()));
                }

                String comment2 = "";
                if (!Validator.isBlankOrNull(siteID)) {
                    comment2 += siteID;
                }

                if (!Validator.isBlankOrNull(purchasingUser.getDisplayName())) {
                    if (comment2.length() > 0) {
                        comment2 += "/";
                    }
                    comment2 += purchasingUser.getDisplayName();
                }

                if (!Validator.isBlankOrNull(purchasingUser.getEcom_ShipTo_Telecom_Phone_Number())) {
                    if (comment2.length() > 0) {
                        comment2 += "/";
                    }
                    comment2 += purchasingUser.getEcom_ShipTo_Telecom_Phone_Number();
                }

                if (!comment2.equals("")) {
                    parmListSB.append(createValidParameter("&COMMENT2", comment2));
                }
            }

            //We are doing a sale, not a credit
            parmListSB.append(createValidParameter("&TRXTYPE", transactionType.getID()));


            String transactionID = ObjectManager.getNewObjectID();
            VerisignCreditCardProcessingResults results =
                new VerisignCreditCardProcessingResults(processor.SubmitTransaction(parmListSB.toString()),
                    transactionID, creditCard.getCountryCode().equals("US"));

            //Log the transaction
            DBBean db = new DBBean();
            try {
                db.prepareStatement(
                    "insert into pn_credit_card_transaction " +
                    "  (transaction_id, vendor_transaction_id, date_submitted, " +
                    "   transaction_type, transaction_amount, " +
                    "   transaction_amount_currency, authorization_code," +
                    "   is_duplicate) " +
                    "values" +
                    "   (?,?,?,?,?,?,?,?) "
                );
                db.pstmt.setString(1, transactionID);
                db.pstmt.setString(2, results.getVendorTransactionID());
                db.pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
                db.pstmt.setString(4, transactionType.getID());
                db.pstmt.setBigDecimal(5, amount.getValue());
                db.pstmt.setString(6, amount.getCurrency().getCurrencyCode());
                db.pstmt.setString(7, results.getAuthCode());
                db.pstmt.setBoolean(8, results.isDuplicateTransaction());

                db.executePrepared();
            } catch (SQLException sqle) {
                //Don't cause a general failure here.  It is better to not log
                //the info in the database than to cause a credit to have
                //occurred without issuing a license.
            } finally {
                db.release();
            }

            return results;
        } finally {
            processor.DestroyContext();
        }
    }

    /**
     * Get the PFProAPI object that does the actual processing and hooks into
     * VeriSign's API's for charging credit cards.
     *
     * @return a <code>PFProAPI</code> object.
     */
    private PFProAPI getProcessor() {
        PFProAPI processor = new PFProAPI();

        processor.SetCertPath(publicKeyLocation);
        processor.CreateContext(serverName, Integer.parseInt(serverPort),
            TRANSACTION_TIME_OUT, proxyAddress, proxyPort, proxyUsername,
            proxyPassword);
        return processor;
    }

    /**
     * Checks the credit card and amout parameters to make sure they are valid
     * values to be sent to VeriSign.
     *
     * @param creditCard a <code>CreditCard</code> object which contains
     * information that is going to be sent to VeriSign.  We will check the
     * expiration date, and we will check to make sure it is a valid credit
     * card number.
     * @param amount a <code>Money</code> object representing the amount we
     * are going to charge to the credit card.  Currently, we only support
     * charging US Dollars, so if the currency is something else, we will
     * throw an error.
     * @throws InvalidChargeException if any of the input parameters have errors
     * which would cause the VeriSign transaction to fail.
     */
    private void verifyInputParameters(CreditCard creditCard, Money amount) throws InvalidChargeException {
        if (!creditCard.isValidExpirationDate()) {
            throw new InvalidChargeException(PropertyProvider.get("prm.global.creditcard.validation.invalidcreditcardexpdate.message"));
        }
        if (!creditCard.isValidNumber()) {
            throw new InvalidChargeException(PropertyProvider.get("prm.global.creditcard.validation.invalidcreditcard.message"));
        }
        if (amount.getValue().compareTo(new BigDecimal(0)) < 0) {
            throw new InvalidChargeException(PropertyProvider.get("prm.global.creditcard.validation.invalidamount.message"));
        }
        if (!amount.getCurrency().getCurrencyCode().equals("USD")) {
            throw new InvalidChargeException(PropertyProvider.get("prm.global.creditcard.validation.invalidcurrency.message"));
        }
    }

    /**
     * Translate a Credit Card object into a series of parameters that Verisign
     * expects to see for a credit card.
     *
     * @param creditCard a <code>CreditCard</code> object which contains the
     * information that VeriSign expects to see for a credit card.
     * @return a <code>String</code> containing parameters and values for all
     * the required parameters.  The parameters will all have an ampersand
     * prepended to them for ease of constructing the parameter string.
     */
    private String getCreditCardInfo(CreditCard creditCard) {
        StringBuffer parmListSB = new StringBuffer();
        parmListSB.append(createValidParameter("&ACCT", creditCard.getCreditCardNumberWithoutPunctuation()));
        parmListSB.append(createValidParameter("&EXPDATE", creditCard.getExpirationDateMMYY()));
        parmListSB.append(createValidParameter("&TENDER", "C"));

        //Add some parameters that are used solely for reporting purposes
        if (!Validator.isBlankOrNull(creditCard.getName())) {
            parmListSB.append(createValidParameter("&NAME", creditCard.getName()));
        }

        if (!Validator.isBlankOrNull(creditCard.getStreetAddress()) &&
            !Validator.isBlankOrNull(creditCard.getZip())) {
            parmListSB.append(createValidParameter("&STREET", creditCard.getStreetAddress()));
            parmListSB.append(createValidParameter("&ZIP", creditCard.getZip()));
        }

        if (creditCard.getName() != null) {
            StringTokenizer tok = new StringTokenizer(creditCard.getName(), " ");
            if (tok.countTokens() > 0) {
                String firstName = tok.nextToken();
                parmListSB.append(createValidParameter("&FIRSTNAME", firstName));

                if (tok.hasMoreTokens()) {
                    String lastName;
                    do {
                        lastName = tok.nextToken();
                    } while (tok.hasMoreTokens());
                    parmListSB.append(createValidParameter("&LASTNAME", lastName));
                }
            }
        }

        if (!Validator.isBlankOrNull(creditCard.getCountryCode())) {
            parmListSB.append(createValidParameter("&COUNTRY", creditCard.getCountryCode()));
        }

        if (!Validator.isBlankOrNull(creditCard.getCity())) {
            parmListSB.append(createValidParameter("&CITY", creditCard.getCity()));
        }

        if (!Validator.isBlankOrNull(creditCard.getState())) {
            parmListSB.append(createValidParameter("&STATE", creditCard.getState()));
        }

        return parmListSB.toString();
    }

    /**
     * Translate the merchantAccount field into the parameters that need to be
     * passed to VeriSign.
     *
     * @return a <code>String</code> containing the merchant account to credit
     * from the user's credit card.
     */
    private String getMerchantAccountInfo() {
        StringBuffer merchantAccountInfo = new StringBuffer();
        Map merchantParameters = merchantAccount.getParameters();
        String user, vendor, partner, pwd;

        user = (String)merchantParameters.get("USER");
        vendor = (String)merchantParameters.get("VENDOR");
        partner = (String)merchantParameters.get("PARTNER");
        pwd = (String)merchantParameters.get("PWD");

        if (merchantAccount.getClass().getName().equals("net.project.creditcard.verisign.PNETMerchantAccount")) {
            try {
                pwd = EncryptionManager.decryptBlowfish(pwd, merchantAccountSecretKey);
            } catch (Exception e) {
                throw new RuntimeException("Could not decrypt merchant account information");
            }
        }


        merchantAccountInfo.append("USER=").append(user);
        merchantAccountInfo.append("&VENDOR=").append(vendor);
        merchantAccountInfo.append("&PARTNER=").append(partner);
        merchantAccountInfo.append("&PWD=").append(pwd);

        return merchantAccountInfo.toString();
    }

    /**
     * Translate a money object into an amount parameter that VeriSign would
     * understand.
     *
     * @param amount a <code>Money</code> object containing the amount to be
     * charged and the currency.
     * @return a <code>String</code> in VeriSign-centric format which represents
     * the amount to be debited or credited to a user's credit card.
     */
    private String getAmountInfo(Money amount) {
        DecimalFormat df = new DecimalFormat("##0.00;-##0.00");
        return createValidParameter("&AMT", df.format(amount.getValue()));
    }

    /**
     * This method will take a string that needs to be appended to a VeriSign
     * parameter list and will remove any characters that VeriSign would
     * otherwise reject.
     *
     * @param parameterName a <code>String</code> containing the parameter name.
     * (That is, the part before the equal sign.)
     * @param parameterValue a <code>String</code> containing the parameter
     * value.  (That is, the part after the equal sign.)
     * @return a <code>String</code> which will be properly transformed to a
     * valid String.  This will match the source parameter if it didn't have any
     * invalid characters.
     */
    String createValidParameter(String parameterName, String parameterValue) {
        parameterValue = parameterValue.replaceAll("\n", " ");
        parameterValue = parameterValue.replaceAll("\"", "'");

        boolean hasEqualsOrAmpersand = false;
        if (parameterValue.indexOf("=") > -1) {
            hasEqualsOrAmpersand = true;
        }
        if (parameterValue.indexOf("&") > -1) {
            hasEqualsOrAmpersand = true;
        }

        if (hasEqualsOrAmpersand) {
            parameterName = parameterName + "[" + parameterValue.length() + "]";
        }

        return parameterName + "=" + parameterValue;
    }

}
