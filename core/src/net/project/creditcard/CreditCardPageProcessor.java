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
package net.project.creditcard;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.admin.RegistrationBean;
import net.project.base.IUniqueTransaction;
import net.project.base.PnetException;
import net.project.base.money.InvalidCurrencyException;
import net.project.base.money.Money;
import net.project.base.property.PropertyProvider;
import net.project.creditcard.verisign.VerisignCreditCardProcessor;
import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.LicenseNotification;
import net.project.license.cost.LicenseCostManager;
import net.project.license.create.LicenseContext;
import net.project.license.create.LicenseCreator;
import net.project.license.create.LicenseUpdater;
import net.project.license.system.LicenseProperties;
import net.project.notification.NotificationException;
import net.project.notification.NotificationUtils;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.UniqueTransaction;
import net.project.util.Validator;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * This page stores information and operations needed to walk a user through the
 * steps of credit card processing.  It is an attempt to keep that code out of
 * the CreditCardController.jsp page until we can start using a framework that
 * will make it a bit more normal to keep that code out of the page.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class CreditCardPageProcessor {
    /**
     * This is the page that the credit card processing pages will redirect to
     * after we have completed charging the credit card.
     */
    private String nextPage = null;
    /**
     * This is the page that we will go to if the user presses the "previous"
     * button on the first page of credit card processing.
     */
    private String previousPage = null;
    /**
     * Whether or not we are going to show the previous button on the first page
     * of credit card processing.
     */
    private boolean previousPageEnabled = false;
    /**
     * What page we should direct the user to if they press the "cancel" button.
     */
    private String cancelPage = null;
    /**
     * Whether or not we are going to show a cancel button.  We aren't going to
     * show one after the credit card has already been processed.
     */
    private boolean cancelButtonEnabled = false;
    /**
     * Show special page header for registration.  This is a horrible hack, but
     * is unfortunately necessary for now.
     */
    private boolean showRegistrationHeader = false;
    /**
     * Show the checkbox that allows users to fill in the address and full name
     * info based on the information that the user has already entered in the
     * profile information screen.  (For registration only.)
     */
    private boolean showUseProfileInfoCheckbox = true;
    /**
     * Whether or not we are going to generate a license for this user when we
     * are completed.  This is done automatically if the
     * {@link #completeRegistration} flag is turned on.
     */
    private boolean updateLicense = false;
    /**
     * Whether we are going to create a license of which the current user will
     * be the responsible use, but will not be associated with the license.
     */
    private boolean createLicense = false;
    /**
     * When this flag is turned out, we need to complete a user registration
     * when we are done processing the credit card.  This method relies
     * (unfortunately) on some session variables (such as registrationBean and
     * licenseContext) being present.
     */
    private boolean completeRegistration = false;
    /**
     * This is the credit card that we are going to charge.  We will store
     * information in this object as we collect it from html forms.
     */
    private CreditCard creditCard = new CreditCard();
    /**
     * Indicates how many licenses the user is going to purchase.
     */
    private int numberOfLicenses = 1;
    /**
     * Indicates whether or not the user should remain in HTTPS mode after the
     * user clicks on the finish button on the PurchaseComplete.jsp page.  This
     * exists so we don't try to bail out of an https session if the user was
     * already in one to start with.
     */
    private boolean exitInHTTPSMode = false;
    /**
     * The user who is purchasing a license.
     */
    private User user;
    /**
     * These are the exact parameters that the user entered on the license
     * information page.
     */
    private Map ccInfoParameters = new HashMap();
    /**
     * Keep track of whether the credit card has already been charged.
     */
    private boolean creditCardAlreadyCharged = false;
    /**
     * The license created by this process.  This will only be available
     * after a license has been successfully created.
     */
    private License license = null;

    /**
     * Set all private member variables to their default state so this object
     * can be reused.
     */
    public void clear() {
        nextPage = null;
        previousPage = null;
        previousPageEnabled = false;
        cancelPage = null;
        cancelButtonEnabled = false;
        showRegistrationHeader = false;
        showUseProfileInfoCheckbox = true;
        createLicense = false;
        updateLicense = false;
        completeRegistration = false;
        creditCard = new CreditCard();
        numberOfLicenses = 1;
        exitInHTTPSMode = false;
        user = null;
        ccInfoParameters.clear();
        creditCardAlreadyCharged = false;
        license = null;
    }

    /**
     * Get the credit card that we are going to charge for the licenses that the
     * user has requested.
     *
     * @return a <code>CreditCard</code> object which we are going to charge for
     * the licenses.
     */
    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * The number of users that the license will contain.
     *
     * @return a <code>int</code> value indicating how many licenses we are
     * going to purchase.
     */
    public int getNumberOfLicenses() {
        return numberOfLicenses;
    }

    /**
     * Set the number of licenses that the user is going to purchase.
     *
     * @param numberOfLicenses a <code>int</code> value indicating the number of
     * licenses the user is going to purchase.
     */
    public void setNumberOfLicenses(int numberOfLicenses) {
        this.numberOfLicenses = numberOfLicenses;
    }

    /**
     * Get the price for an individual license of the software.
     *
     * @return a <code>Money</code> object representing the cost for a single
     * license.
     * @throws PersistenceException if there is an error fetching the license
     * information from the database.
     * @throws LicenseException never.
     */
    public Money getUnitPrice() throws PersistenceException, LicenseException {
        //Determine how much to charge
        LicenseCostManager costManager = LicenseCostManager.getInstance();
        return costManager.getBaseCostUnitPrice();
    }

    /**
     * Get the total amount of maintenance fee that will be applied for the
     * licenses purchased.
     *
     * @return a <code>Money</code> object which indicates the total maintenance
     * fee.
     * @throws PersistenceException if there is an error loading the master
     * properties from the database.
     * @throws LicenseException never
     */
    public Money getMaintenanceFee() throws PersistenceException, LicenseException {
        LicenseCostManager costManager = LicenseCostManager.getInstance();
        return getLicenseCosts().multiply(costManager.getMaintenanceCostPercentage());
    }

    public Money getMaintenanceFeeUnitPrice() throws LicenseException, PersistenceException {
        LicenseCostManager costManager = LicenseCostManager.getInstance();
        return getUnitPrice().multiply(costManager.getMaintenanceCostPercentage());
    }

    /**
     * Get the total cost of licenses for this purchase.  (Number of users *
     * unit price.)
     *
     * @return a <code>Money</code> object which indicates the total amount that
     * will be spent on licenses.
     * @throws LicenseException never
     * @throws PersistenceException if an error occurs fetching the master
     * properties.
     */
    public Money getLicenseCosts() throws LicenseException, PersistenceException {
        return getUnitPrice().multiply(new BigDecimal(getNumberOfLicenses()));
    }

    /**
     * Get the total amount of money charged to the user for maintenance fees
     * and user licenses.
     *
     * @return a <code>Money</code> object which contains the total costs which
     * can be attributed to license fees and maintenance costs.
     * @throws LicenseException never
     * @throws PersistenceException if an error occurs while loading the master
     * properties.
     * @throws InvalidCurrencyException if the maintenance fees and license fees
     * are not in the same currency.
     */
    public Money getSubtotal() throws LicenseException, PersistenceException, InvalidCurrencyException {
        return getLicenseCosts().add(getMaintenanceFee());
    }

    /**
     * Get the total cost that is going to be charged to the user for purchasing
     * with a credit card.
     *
     * @return a <code>Money</code> object which indicates the amount the user
     * is going to be charged for using a credit card.
     * @throws LicenseException never
     * @throws PersistenceException if an error occurs while loading the master
     * properties.
     * @throws InvalidCurrencyException if the maintenance fees and license fees
     * are not in the same currency.
     */
    public Money getCreditCardSurcharge() throws LicenseException, InvalidCurrencyException, PersistenceException {
        LicenseCostManager costManager = LicenseCostManager.getInstance();
        return costManager.getCreditCardTransactionFee(getSubtotal());
    }

    /**
     * Get the grand total that is going to be charged to the user's credit card.
     *
     * @return a <code>Money</code> object which indicates the total amount that
     * is to be charged to the user's credit card.
     * @throws LicenseException never
     * @throws PersistenceException if an error occurs while loading the master
     * properties.
     * @throws InvalidCurrencyException if the maintenance fees and license fees
     * are not in the same currency.
     */
    public Money getTotal() throws PersistenceException, LicenseException, InvalidCurrencyException {
        return getSubtotal().add(getCreditCardSurcharge());
    }

    /**
     * Should the previous page be shown on the first page of the credit card
     * processing.
     *
     * @return <code>true</code> if the previous button should be shown on the
     * first page.
     */
    public boolean isPreviousPageEnabled() {
        return previousPageEnabled;
    }

    /**
     * Indicates whether the cancel button be enabled before the user completes
     * the credit card process.
     *
     * @return a <code>boolean</code> value.  This will contain true if the
     * button should be shown.
     */
    public boolean isCancelButtonEnabled() {
        return cancelButtonEnabled;
    }

    /**
     * Indicates whether we should be showing the big gray ugly registration
     * header on the credit card pages.
     *
     * @return a <code>boolean</code> value indicating whether we are showing
     * the registration header.
     */
    public boolean isShowRegistrationHeader() {
        return showRegistrationHeader;
    }

    /**
     * Are we going to show the "Use information from user profile?" checkbox
     * at the top of the billing information section.
     *
     * @return a <code>boolean</code> value which indicates if we are going to
     * show the checkbox.
     */
    public boolean isShowUseProfileInfoCheckbox() {
        return showUseProfileInfoCheckbox;
    }

    /**
     * Make sure that if the user entered credit card processing in https mode
     * that they will exit in that mode.  If not, make sure that we transition
     * out of it.
     *
     * @param exitPage a <code>String</code> page that the user has requested
     * to go to that is outside of credit card processing.
     * @return a <code>String</code> containing a page that a user should
     * redirect to.
     */
    private String fixExitPageForHTTPS(String exitPage) {
        String finishPrefix = "";

        if (isExitInHTTPSMode()) {
            String rootURL = "https://" + SessionManager.getSiteHost();

            if (rootURL.endsWith(":80")) {
                rootURL = rootURL.substring(0, rootURL.length()-3);
            }
        } else {
            finishPrefix = SessionManager.getSiteURL();
            if (finishPrefix.startsWith("https:")) {
                finishPrefix = "http" + finishPrefix.substring(5);
            }
        }

        return finishPrefix+exitPage;
    }


    /**
     * Get the page that we should go to upon completing this wizard.  This
     * value exists so that we can plug the credit card transaction pages into
     * a variety of locations more easily.
     *
     * @return a <code>String</code> value which indicates which page we should
     * forward to at the end of credit card collection process.
     */
    public String getNextPage() {
        try {
            return fixExitPageForHTTPS(URLDecoder.decode(nextPage, SessionManager.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            return fixExitPageForHTTPS(nextPage);
        }
    }

    /**
     * Get the page that we should go to if the user presses the "previous"
     * button on the first page of the credit card wizard.
     *
     * @return a <code>String</code> containing the URL that we should go to if
     * the user presses "previous" on the first page of the credit card wizard.
     */
    public String getPreviousPage() {
        return fixExitPageForHTTPS(previousPage);
    }

    /**
     * Get the page where we should direct the user if they press the "cancel"
     * button on any page in the wizard.
     *
     * @return a <code>String</code> containing the location we should direct
     * the user to if they click the cancel button.
     */
    public String getCancelPage() {
        return fixExitPageForHTTPS(cancelPage);
    }

    /**
     * Whether or not we are going to update a user's license after we complete
     * the credit card transaction.
     *
     * @return a <code>boolean</code> value indicating whether we are going to
     * update the current user's license using the HttpSession object
     * "licenseUpdater" once we have completed the credit card transaction.
     */
    public boolean isUpdateLicense() {
        return updateLicense;
    }

    /**
     * Whether or not we are going to complete the user registration currently
     * in process in the <code>registration</code> object that is currently
     * stored in the HttpSession.
     *
     * @return a <code>boolean</code> indicating whether we are going to
     * register the user after we complete the credit card transaction.
     */
    public boolean isCompleteRegistration() {
        return completeRegistration;
    }

    /**
     * Whether or not we are going to create a license.  The current user will
     * be the responsible user for the license, but they won't be associated
     * to the license.
     *
     * @return a <code>boolean</code> value indicating whether we are going to
     * create a license which is not associated to any users.
     */
    public boolean isCreateLicense() {
        return createLicense;
    }

    /**
     * Get the user that is purchasing the licenses.  This user will be the
     * responsible user for this license after it is purchased.
     *
     * @return a <code>User</code> who is currently using their credit card to
     * purchase license(s).
     */
    public User getUser() {
        return user;
    }

    /**
     * Get a map which contains all of the parameters which were submitted on
     * the CollectCreditCardInfo.jsp page.
     *
     * @return a <code>Map</code> which contains all of the parameters submitted
     * on the CollectCreditCardInfo.jsp page.
     */
    public Map getCcInfoParameters() {
        return ccInfoParameters;
    }

    /**
     * Indicate whether the user should remain in HTTPS mode after they leave
     * the last page of credit card processing.
     *
     * @return a <code>boolean</code> value indicating whether the client should
     * continue to be in SSL mode after leaving the last page.
     */
    public boolean isExitInHTTPSMode() {
        return exitInHTTPSMode;
    }

    /**
     * Indicates whether the credit card has already been charged.
     *
     * @return a <code>boolean</code> indicating whether the credit card has
     * already been charged.
     */
    public boolean isCreditCardAlreadyCharged() {
        return creditCardAlreadyCharged;
    }

    /**
     * Get the license which was created when the {@link #completeRegistration}
     * method was called.
     *
     * @return a <code>License</code> object which was created when
     * {@link #completeRegistration} was called, or null if one hasn't been
     * created yet.
     */
    public License getLicense() {
        return license;
    }

    /**
     * Strip some requests parameters out of the request object that is fed to
     * the first page of credit card processing.  This sets up this object to
     * know what options the calling module has requested.
     *
     * @param request a <code>HttpServletRequest</code> object which we are
     * going to fetch parameters from.
     * @param errorReporter a <code>ErrorReporter</code> object to which we will
     * add errors, if we find any.
     * @throws PnetException if the calling page has failed to indicate the
     * "nextPage" parameter.
     */
    public void initialize(HttpServletRequest request, ErrorReporter errorReporter) throws PnetException {
        clear();

        previousPageEnabled = !Validator.isBlankOrNull(request.getParameter("previousPage"));
        cancelButtonEnabled = !Validator.isBlankOrNull(request.getParameter("cancelPage"));

        String registrationHeader = request.getParameter("registrationHeader");
        if (!Validator.isBlankOrNull(registrationHeader)) {
            showRegistrationHeader = Boolean.valueOf(registrationHeader).booleanValue();
        } else {
            showRegistrationHeader = false;
        }

        String userProfileAddress = request.getParameter("useUserProfileAddress");
        if (!Validator.isBlankOrNull(request.getParameter("useUserProfileAddress"))) {
            showUseProfileInfoCheckbox = Boolean.valueOf(userProfileAddress).booleanValue();
        } else {
            showUseProfileInfoCheckbox = true;
        }

        nextPage = request.getParameter("nextPage");
        if (Validator.isBlankOrNull(nextPage)) {
            throw new PnetException("Programmer error, this page requires the nextPage parameter");
        }

        previousPage = request.getParameter("previousPage");
        cancelPage = request.getParameter("cancelPage");

        String reqCompleteRegistration = request.getParameter("completeRegistration");
        if (!Validator.isBlankOrNull(reqCompleteRegistration)) {
            user = ((RegistrationBean)request.getSession().getAttribute("registration"));
            completeRegistration = Boolean.valueOf(reqCompleteRegistration).booleanValue();
        } else {
            completeRegistration = false;
        }

        String reqUpdateLicense = request.getParameter("updateLicense");
        if (!Validator.isBlankOrNull(reqUpdateLicense)) {
            user = ((LicenseUpdater)request.getSession().getAttribute("licenseUpdater")).getUser();
            updateLicense = Boolean.valueOf(reqUpdateLicense).booleanValue();
        } else {
            updateLicense = false;
        }

        String reqCreateLicense = request.getParameter("createLicense");
        if (!Validator.isBlankOrNull(reqCreateLicense)) {
            createLicense = Boolean.valueOf(reqCreateLicense).booleanValue();
        } else {
            createLicense = false;
        }

        String useUserFromSession = request.getParameter("useUserFromSession");
        if (!Validator.isBlankOrNull(useUserFromSession)) {
            if (Boolean.valueOf(useUserFromSession).booleanValue()) {
                user = (User)request.getSession().getAttribute("user");
            }
        }

        //If we are currently in https mode, we should exit in https mode.
        //Otherwise we need to explicitly exit from it.
        exitInHTTPSMode = request.isSecure();
    }

    /**
     * This method stores the parameters from the CollectCreditCardInfo.jsp page
     * so they can be repopulated if necessary.
     *
     * @param request a <code>HttpServletRequest</code> object which contains
     * the parameters which were submitted from the CollectCreditCardInfo.jsp
     * page.
     */
    public void populateCCInfoParameters(HttpServletRequest request) {
        //Clear out the parameter list to make sure we aren't putting in stuff
        //that shouldn't be there.
        ccInfoParameters.clear();

        //Add all of the parameters to the internal data structure
        Enumeration paramEnum = request.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            String paramName = (String)paramEnum.nextElement();
            String paramValue = request.getParameter(paramName);
            ccInfoParameters.put(paramName, paramValue);
        }

        //Add all of the attributes to the internal data structure.
        Enumeration attrEnum = request.getAttributeNames();
        while (attrEnum.hasMoreElements()) {
            String attributeName = (String)attrEnum.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            ccInfoParameters.put(attributeName, attributeValue);
        }
    }

    /**
     * Attempt the credit card transaction, and if necessary, complete registration
     * or update licenses.
     *
     * @param errorReporter
     * @param licenseContext
     * @param licenseUpdater
     * @param registration
     * @param request
     * @param session
     * @throws PnetException
     * @throws SQLException
     */
    public void completeRegistration(ErrorReporter errorReporter, LicenseContext licenseContext,
        LicenseUpdater licenseUpdater, RegistrationBean registration, HttpServletRequest request,
        HttpSession session) throws PnetException, SQLException {
        //If work failed, we shouldn't create a license
        if (!errorReporter.errorsFound()) {
            DBBean db = new DBBean();
            db.setAutoCommit(false);
            db.openConnection();
            try {
                //Add information about this purchase to the license context
                licenseContext.setCreditCardNumber(getCreditCard().getProtectedCreditCardNumber());
                licenseContext.setCreditCardLicenseCount(getNumberOfLicenses());
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(getCreditCard().getExpirationDate());
                licenseContext.setCreditCardExpirationMonth(cal.get(GregorianCalendar.MONTH)+1);
                licenseContext.setCreditCardExpirationYear(cal.get(GregorianCalendar.YEAR));

                //Create the person object for the person who created the license
                Person purchaser = getUser();

                //Which user are we sending a notification to.  This will be
                //based on whether we are updating or registering.
                if (isUpdateLicense()) {
                    licenseUpdater.setLicenseContext(licenseContext);
                    licenseUpdater.setPurchaser(purchaser);
                    licenseUpdater.updateLicense(db, false);
                    license = licenseUpdater.getLicense();
                }

                if (isCreateLicense()) {
                    LicenseCreator licenseCreator = LicenseCreator.makeCreator(licenseContext, LicenseProperties.getInstance(request), purchaser);
                    licenseCreator.commitLicense(db, user, false, false);
                    license = licenseCreator.getLicense();
                }

                if (isCompleteRegistration()) {
                    // Now try completing registration
                    net.project.admin.RegistrationBean.RegistrationResult result = null;
                    LicenseProperties licenseProps = LicenseProperties.getInstance();
                    result = registration.completeRegistration(licenseProps, db, false);

                    if (!result.isSuccess()) {
                        errorReporter.addError(result.getErrorMessagesFormatted());
                    }

                    //Find the license that was created for this user
                    license = result.getLicense();
                }

                //Get the unique transaction id for this credit card
                IUniqueTransaction uniqueTransaction =
                    (IUniqueTransaction)session.getAttribute("uniqueTransaction");
                if (uniqueTransaction == null) {
                    throw new PnetException("Implementation error.  Every process which " +
                        "initiates a credit card transaction must have set the uniqueTransactionID " +
                        "session variable with an object which implements IUniqueTransaction");
                }

                //As long as we haven't found any errors, charge the credit card
                ICreditCardProcessingResults results = null;
                if (!errorReporter.errorsFound()) {
                    results = processCreditCard(uniqueTransaction.getUniqueTransaction().getRawString(),
                            errorReporter, license.getPaymentInformation().getID(), db, license);
                }

                //If successful commit, otherwise rollback
                if (errorReporter.errorsFound()) {
                    //We need to generate a new "Unique ID".  If we resubmit
                    //with the same one, we'll get the same error.
                    uniqueTransaction = new UniqueTransaction(request.getRemoteAddr());
                    session.setAttribute("uniqueTransaction", uniqueTransaction);

                    registration.setUserStored(false);
                    db.rollback();
                } else if (results == null || results.isDuplicateTransaction()) {
                    db.rollback();
                } else {
                    db.commit();

                    sendReceipt(user);
                    creditCardAlreadyCharged = true;


                    if (isCompleteRegistration() || isUpdateLicense()) {
                        //Now that we have committed, notify the user of the registration
                        if (isCompleteRegistration() && registration.getUserDomain().isVerificationRequired()) {
                            registration.sendVerificationEmail();
                        }

                        //Notify the user of their license registration
                        new LicenseNotification().notifyResponsibleUserOfAssociation(license, getUser());
                        new LicenseNotification().notifyUserOfAssociation(license, getUser());
                    }
                }
            } catch (Exception e) {
                registration.setUserStored(false);
                db.rollback();
                throw new PnetException(e);
            } finally {
                db.release();
            }
        }
    }

    /**
     * Charge the credit card for the license information that has been set up
     * in this object.
     *
     * @param uniqueTransactionID a <code>String</code> whose value is unique to
     * this submission.  We uniquely identify this transaction in case the user
     * @param errorReporter an <code>ErrorReporter</code> object in which we will
     * record any failures that occur.
     * @param paymentID a <code>String</code> which contains the unique database
     * key of the Payment record we've generated.
     * @param db a <code>DBBean</code> object which is already in a transaction.
     * @param license a <code>License</code> object which contains the license
     * key we've generated.
     * @return a <code>ICreditCardProcessingResults</code> object which indicates
     * whether the transaction was successful and which contains any messages
     * generated by the transaction.
     */
    public ICreditCardProcessingResults processCreditCard(String uniqueTransactionID,
        ErrorReporter errorReporter, String paymentID, DBBean db, License license) throws SQLException {

        ICreditCardProcessingResults results = null;

        try {
            //Try to process the credit card
            if (!errorReporter.errorsFound()) {
                //Get the processor which is going to do the work of submitting
                //the credit card information to the credit card companies.
                VerisignCreditCardProcessor processor = new VerisignCreditCardProcessor();

                //Set the account which is going to receive the proceeds
                String merchantAccountClass = SessionManager.getCreditCardMerchantAccountClassName();
                try {
                    processor.setMerchantAccount((IMerchantAccount)(Class.forName(merchantAccountClass).newInstance()));
                } catch (InstantiationException e) {
                    throw new PnetException("Internal Error: Unable to instantiate Merchant Account class: "+merchantAccountClass, e);
                } catch (IllegalAccessException e) {
                    throw new PnetException("Internal Error: No public constructor found on Merchant Account Class: "+merchantAccountClass, e);
                } catch (ClassNotFoundException e) {
                    throw new PnetException("Internal Error: Unable to find Merchant Account class: "+merchantAccountClass, e);
                }

                //Determine how much to charge
                Money chargeAmount = getTotal();

                //Make the purchase.  We aren't tokenizing this because it is
                //what appears in VeriSign.  This isn't displayed to the user.
                String lineItemDesc = getNumberOfLicenses() + " user licenses @ "+
                    getUnitPrice().format(SessionManager.getUser()) + " + " +
                    getMaintenanceFee().format(SessionManager.getUser())+" maintenance + " +
                    getCreditCardSurcharge().format(SessionManager.getUser()) +
                    " surcharge.  Key: "+license.getKey().toDisplayString();
                results = processor.makePurchase(creditCard, chargeAmount,
                    uniqueTransactionID, lineItemDesc, user);

                //Link this transaction id to the payment id of the license
                db.prepareStatement(
                    "insert into pn_cc_transaction_payment "+
                    "  (transaction_id, payment_id) " +
                    "values " +
                    "  (?,?)"
                );
                db.pstmt.setString(1, results.getTransactionID());
                db.pstmt.setString(2, paymentID);
                db.executePrepared();

                if (results.getResultType() != CreditCardResultType.SUCCESS) {
                    errorReporter.addError(new ErrorDescription(
                        results.getMessage()
                    ));
                }
            }
        } catch (PnetException e) {
            errorReporter.addError(new ErrorDescription(
                e.getMessage()
            ));
        }

        return results;
    }

    /**
     * Parse the information from the collect credit card info page to make sure
     * that all of it is valid.
     *
     * @param errorReporter
     */
    public void parseAndValidate(ErrorReporter errorReporter) {
        //Parse and validate credit card number
        boolean ccOkay = true;
        String creditCardNumber = (String)ccInfoParameters.get("creditCardNumber");
        if (!Validator.isCreditCard((String)ccInfoParameters.get("creditCardNumber"))) {
            errorReporter.addError(new ErrorDescription(
                PropertyProvider.get("prm.global.creditcard.validation.numberisinvalid.message")
            ));
            ccOkay = false;
        } else {
            creditCard.setCreditCardNumber(creditCardNumber);
        }

        //Parse and validate credit card type
        String creditCardType = (String)ccInfoParameters.get("creditCardType");
        if (Validator.isBlankOrNull((String)ccInfoParameters.get("creditCardType"))) {
            errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.pleaseselectcctype.message"));
            ccOkay = false;
        } else {
            creditCard.setType(CreditCardType.getForID(creditCardType));
        }

        //Check to make sure credit card number matches credit card type
        if (ccOkay) {
            if (!creditCard.getType().numberMatchesType(creditCard.getCreditCardNumberWithoutPunctuation())) {
                errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.typenumbermismatch.message"));
            }
        }

        //Parse and validate expiration date
        String ccMonth = (String)ccInfoParameters.get("expirationMonth");
        String ccYear = (String)ccInfoParameters.get("expirationYear");
        int iMonth = -1, iYear = -1;
        boolean dateOK = true;

        if (!Validator.isNumeric(ccMonth)) {
            errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.expirationmonthinvalid.message"));
            dateOK = false;
        } else {
            iMonth = Integer.parseInt(ccMonth);
            if (!Validator.isInRange(iMonth, 0, 11)) {
                errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.expirationmonthinvalid.message"));
            }
        }

        if (!Validator.isNumeric(ccYear)) {
            errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.expirationyearinvalid.message"));
            dateOK = false;
        } else {
            iYear = Integer.parseInt(ccYear);

            if (!Validator.isInRange(iYear, 0, 99) && !Validator.isInRange(iYear, 2000, 2099)) {
                errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.expirationyearinvalid.message"));
                dateOK = false;
            } else if (Validator.isInRange(iYear, 0, 99)) {
                iYear += 2000;
            }
        }

        if (dateOK) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(iYear, iMonth, 1);
            cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
            cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
            cal.set(GregorianCalendar.MINUTE, 59);
            cal.set(GregorianCalendar.SECOND, 59);
            cal.set(GregorianCalendar.MILLISECOND, 999);

            creditCard.setExpirationDate(cal.getTime());
        }

        String billingAddress1 = (String)ccInfoParameters.get("billingAddress1");
        if (Validator.isBlankOrNull(billingAddress1)) {
            errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.address1required.message"));
        } else {
            creditCard.setStreetAddress(billingAddress1);
        }

        String billingCity = (String)ccInfoParameters.get("billingCity");
        if (!Validator.isBlankOrNull(billingCity)) {
            creditCard.setCity(billingCity);
        }

        String billingState = (String)ccInfoParameters.get("billingState");
        if (!Validator.isBlankOrNull(billingState)) {
            creditCard.setState(billingState);
        }

        String billingZip = (String)ccInfoParameters.get("billingZip");
        if (Validator.isBlankOrNull(billingZip)) {
            errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.zipcoderequired.message"));
        } else {
            creditCard.setZip(billingZip);
        }

        String billingCountry = (String)ccInfoParameters.get("billingCountry");
        if (Validator.isBlankOrNull(billingCountry)) {
            creditCard.setCountryCode("US");
        } else {
            creditCard.setCountryCode(billingCountry);
        }

        String billingName = (String)ccInfoParameters.get("billingDisplayName");
        if (!Validator.isBlankOrNull(billingName)) {
            creditCard.setName(billingName);
        }

        //Get the number of licenses were are going to purchase.
        if (((String)ccInfoParameters.get("licenseType")).equalsIgnoreCase("single")) {
            setNumberOfLicenses(1);
        } else {
            int numberOfLicenses;
            try {
                numberOfLicenses = Integer.parseInt((String)ccInfoParameters.get("numberOfLicenses"));

                if (numberOfLicenses > 0) {
                    setNumberOfLicenses(numberOfLicenses);
                } else {
                    errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.numberoflicenseserror.message"));
                }
            } catch (NumberFormatException e) {
                errorReporter.addError(PropertyProvider.get("prm.global.creditcard.validation.invalidnumberoflicenses.message"));
            }
        }

        if (errorReporter.errorsFound()) {
            errorReporter.setOverallError(PropertyProvider.get("prm.global.creditcard.validation.errorsfound.message"));
        }
    }

    /**
     * Send the user a receipt of their purchase.  This method assumes that the
     * purchase has already occurred.
     *
     * @param user a <code>User</code> object to which the receipt will be sent
     * by email.
     * @throws XMLDocumentException if internally we have violated the way an
     * XML page can be put together.
     * @throws PersistenceException
     * @throws LicenseException
     * @throws InvalidCurrencyException
     * @throws NotificationException
     */
    public void sendReceipt(User user) throws XMLDocumentException, PersistenceException, LicenseException, InvalidCurrencyException, NotificationException {
        XMLDocument doc = new XMLDocument();
        doc.startElement("CreditCardReceipt");

        //
        // Line Items
        //
        doc.startElement("LineItems");
        doc.startElement("LineItem");
        doc.addElement("Quantity", NumberFormat.getInstance().formatNumber(getNumberOfLicenses()));
        doc.addElement("Description",
            PropertyProvider.get("prm.global.creditcard.purchasesummary.lineitem.licensefees.description"));
        doc.addElement("UnitPrice", getUnitPrice().format(user));
        doc.addElement("LineSubtotal", getLicenseCosts().format(user));
        doc.endElement();  //Line Item

        doc.startElement("LineItem");
        doc.addElement("Quantity", NumberFormat.getInstance().formatNumber(getNumberOfLicenses()));
        doc.addElement("Description",
            PropertyProvider.get("prm.global.creditcard.purchasesummary.lineitem.maintenancefees.description"));
        doc.addElement("UnitPrice", getMaintenanceFeeUnitPrice().format(user));
        doc.addElement("LineSubtotal", getMaintenanceFee().format(user));
        doc.endElement();  //LineItem

        doc.endElement();  //LineItems

        //Totals and Surcharges
        doc.startElement("Summary");
        doc.addElement("Subtotal", getSubtotal().format(user));

        if (!(getCreditCardSurcharge().getValue().floatValue() == 0)) {
            doc.addElement("CreditCardServiceCharge", getCreditCardSurcharge().format(user));
        }
        doc.addElement("Total", getTotal().format(user));
        doc.endElement();

        doc.endElement();

        //Send the message
        NotificationUtils.sendImmediateEmailNotification(user, doc.getXMLString(),
            "/creditcard/xsl/emailreceipt.xsl");
    }

}

