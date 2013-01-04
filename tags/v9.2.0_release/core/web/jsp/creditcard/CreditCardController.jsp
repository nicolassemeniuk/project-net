<%--
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
--%>

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Collect credit card information for registration"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.util.Validator,
            net.project.util.ErrorDescription,
            net.project.creditcard.verisign.VerisignCreditCardProcessor,
            net.project.creditcard.CreditCard,
            java.util.Date,
            java.util.List,
            java.util.GregorianCalendar,
            net.project.calendar.PnCalendar,
            java.math.BigDecimal,
            net.project.base.PnetException,
            net.project.creditcard.ICreditCardProcessingResults,
            net.project.creditcard.CreditCardResultType,
            java.util.StringTokenizer,
            net.project.creditcard.CreditCardPageProcessor,
            net.project.base.IUniqueTransaction,
            net.project.license.system.LicenseProperties,
            net.project.admin.RegistrationBean,
            net.project.database.DBBean,
            net.project.util.UniqueTransaction,
            net.project.license.LicenseNotification,
            net.project.license.PersonLicense,
            net.project.license.License,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="registration" class="net.project.admin.RegistrationBean" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<jsp:useBean id="creditCardProcessor" class="net.project.creditcard.CreditCardPageProcessor" scope="session"/>
<%
    String action = request.getParameter("theAction");
    boolean blankOrNull = Validator.isBlankOrNull(action);
    errorReporter.populateFromRequest(request);

    //Check to make sure that the process hasn't already been finished.  If so,
    //the user cannot go back and make changes now!
    if (!blankOrNull && !action.equalsIgnoreCase("initialize") &&
        !action.equalsIgnoreCase("finish") &&
        creditCardProcessor.isCreditCardAlreadyCharged()) {
        //Indicate that we shouldn't do any more processing, that instead we
        //need to tell the user that they were already done.
        action = "alreadyCompleted";
    }


    if (!blankOrNull && action.equalsIgnoreCase("initialize")) {
        creditCardProcessor.initialize(request, errorReporter);
        if (SessionManager.isSSLSupported()) {
            response.sendRedirect(SessionManager.getJSPRootURLHTTPS()+"/creditcard/CollectCreditCardInfo.jsp");
        } else {
            response.sendRedirect(SessionManager.getJSPRootURL()+"/creditcard/CollectCreditCardInfo.jsp");
        }
    } else if (!blankOrNull && action.equalsIgnoreCase("collectCreditCardInfo")) {
        pageContext.forward("CollectCreditCardInfo.jsp");
    } else if (!blankOrNull && action.equalsIgnoreCase("finish")) {
        response.sendRedirect(creditCardProcessor.getNextPage());
    } else if (!blankOrNull && action.equalsIgnoreCase("completePurchase")) {
        creditCardProcessor.completeRegistration(errorReporter, licenseContext,
            licenseUpdater, registration, request, session);

        if (errorReporter.errorsFound()) {
            pageContext.forward("CollectCreditCardInfo.jsp");
        } else {
            pageContext.forward("PurchaseComplete.jsp");
        }
    } else if (!blankOrNull && action.equalsIgnoreCase("purchaseSummary")) {
        //Store the parameters from this page in case we need to repopulate the
        //page to show it to the user.
        creditCardProcessor.populateCCInfoParameters(request);

        //Make sure that all of our local validation for the credit card
        //passes before we do any more work on the card
        creditCardProcessor.parseAndValidate(errorReporter);

        if (errorReporter.errorsFound()) {
            pageContext.forward("CollectCreditCardInfo.jsp");
        } else {
            pageContext.forward("PurchaseSummary.jsp");
        }

    } else if (!blankOrNull && action.equalsIgnoreCase("previous")) {
        response.sendRedirect(creditCardProcessor.getPreviousPage());
    } else if (!blankOrNull && action.equalsIgnoreCase("cancel")) {
        response.sendRedirect(creditCardProcessor.getCancelPage());
    } else if (!blankOrNull && action.equalsIgnoreCase("alreadyCompleted")) {
        pageContext.forward("PurchaseComplete.jsp?duplicateSubmission=true");
    } else {
        // All other actions are implied to be a "cancel"
        response.sendRedirect(net.project.security.SessionManager.getJSPRootURL() + "/Login.jsp");
    }
%>