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

 package net.project.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.project.admin.ApplicationSpace;
import net.project.base.Module;
import net.project.base.ObjectFactory;
import net.project.base.PnetException;
import net.project.base.SessionNotInitializedException;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpaceBean;
import net.project.configuration.ConfigurationSpaceManager;
import net.project.gui.history.History;
import net.project.gui.history.HistoryLevel;
import net.project.methodology.MethodologySpaceBean;
import net.project.project.ProjectSpaceBean;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.util.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Provides servlet security functionality.
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ServletSecurityProvider {


    // This is the list of servlets that we do not need to check access on.
    private static final Map IGNORED_SERVLET_NAMES = new HashMap();
    private static final Map IGNORE_AUTHENTICATION_JSP = new HashMap();
    private static final Map IGNORE_INITIALIZATION_JSP = new HashMap();
    
    // This is the list of directories to ingore the access check
    private static final Map IGNORE_AUTHENTICATION_DIRECTORY = new HashMap();
    private static final Map IGNORED_DIRECTORY_NAMES = new HashMap();

    static final String HISTORY_OBJECT_VARIABLE = "historyTagHistoryObject";
    
    // Initialize the lookup tables with pages that should have special
    // security consideration
    
    static {

        /*
         * Add pages not requiring session initialization.
         * 
         * Most pages will require initialization.  Only system pages or 
         * testing pages should be ignored here. Additionally pages accessed 
         * in a manner which does not recognize cookies should be ignored 
         * (clearly these are special cases).  
         * Note: Only pages that do not require authentication should be added
         * here (since authentication requires a session).
         */
    	
        // Initialization pages don't require initialization.
    	// This is absolutely necessary to avoid runtime errors.
        addIgnoreInitializationJSP("Initialize.jsp");
        addIgnoreInitializationJSP("errors.jsp");

        // Error pages should not require initialization to avoid masking
        // error by another potential error.
        addIgnoreInitializationJSP("AccessDenied.jsp");
        addIgnoreInitializationJSP("BadBrowser.jsp");
        addIgnoreInitializationJSP("comingsoon.jsp");
        addIgnoreInitializationJSP("CookieRequired.jsp");

        // Miscellaneous system pages
        addIgnoreInitializationJSP("default.jsp");
        addIgnoreInitializationJSP("SnoopServlet.jsp");
        addIgnoreInitializationJSP("WebExError.jsp");
        
        //pages for SSO authentication processing
        addIgnoreInitializationJSP("AdminLogin.jsp");
        addIgnoreInitializationJSP("sso/SSOLogin.jsp");
        addIgnoreInitializationJSP("sso/NoHeader.jsp");
        addIgnoreInitializationJSP("sso/NoAccount.jsp");
        addIgnoreInitializationJSP("sso/LoggedOut.jsp");
        
        // Gantt chart pages
        addIgnoreInitializationJSP("schedule/gantt/Toolbar.jsp");
        addIgnoreInitializationJSP("schedule/gantt/TaskFrame.jsp");
        addIgnoreInitializationJSP("schedule/gantt/GanttFrame.jsp");
        
        /*
         * Add pages not requiring authentication.
         * 
         * This includes any page available prior to logging in,
         * for example, the Login page itself, registration pages,
         * certain help pages etc.
         */

        // External authentication pages
        addIgnoreAuthenticationJSP("external/HTTPLoginProcessing.jsp");

        // Login processing & error pages don't need authentication, 
        // since they are performing authentication.
        addIgnoreAuthenticationJSP("j_security_check");
        addIgnoreAuthenticationJSP("Login.jsp");
        addIgnoreAuthenticationJSP("login.jsp");
        addIgnoreAuthenticationJSP("LoginProcessing.jsp");
		addIgnoreAuthenticationJSP("shadowLoginProcessing.jsp");
		addIgnoreAuthenticationJSP("shadowLogin.jsp");

        addIgnoreAuthenticationJSP("Logout.jsp");
        addIgnoreAuthenticationJSP("AccessDenied.jsp");
        addIgnoreAuthenticationJSP("BadBrowser.jsp");
        addIgnoreAuthenticationJSP("CookieRequired.jsp");
        addIgnoreAuthenticationJSP("default.jsp");
        addIgnoreAuthenticationJSP("comingsoon.jsp");
        addIgnoreAuthenticationDirectory("login");

        // All Registration pages are available prior to login
        // THis includes forgotten login and password wizard
        addIgnoreAuthenticationDirectory("registration");
        addIgnoreAuthenticationDirectory("registration/nativedir");
        addIgnoreAuthenticationDirectory("registration/ldap");

        //Information regarding the collection of credit cards can occur before
        //or after login.  Ignore those pages which can be displayed before
        //logging in.
        addIgnoreAuthenticationJSP("creditcard/CollectCreditCardInfo.jsp");
        addIgnoreAuthenticationJSP("creditcard/CreditCardController.jsp");
        addIgnoreAuthenticationJSP("creditcard/PurchaseComplete.jsp");
        addIgnoreAuthenticationJSP("creditcard/PurchaseSummary.jsp");

        // Ignore the Help Directory that is displayed outside of the login. Everyone should be able to access this
        addIgnoreAuthenticationDirectory("help/include_outside");
        addIgnoreAuthenticationJSP("help/HelpDesk.jsp");
        addIgnoreAuthenticationJSP("help/TC.jsp");

        // Ignore the Help Directory that is displayed after logging in. 
        // Everyone should be able to access these help and support pages.
        addIgnoredDirectoryName("help/include");
        addIgnoredServlet("help/Help.jsp");
        addIgnoreAuthenticationJSP("SnoopServlet.jsp");

        // 01/18/2002 - Tim
        // These pages are used by the Notification Scheduler
        // It cannot currently log into the application automatically
        // Hence, authentication must be ignored
        addIgnoreAuthenticationJSP("notification/NotificationScheduler.jsp");
        addIgnoreAuthenticationJSP("notification/PostmanAgent.jsp");
        addIgnoreAuthenticationJSP("notification/DeleteOrphanedNotificationClobs.jsp");

        // Although this page gets called from other places in the application it is also
        // being used in the registration process and hence has to be ignored from authentication.
        addIgnoreAuthenticationJSP("admin/license/include/LicenseSelect.jsp");

		// 11/25/2006 - Avinash Bhamare -- added for iCal integration 
        addIgnoreAuthenticationJSP("ical/icalendar.jsp");

        // 11/25/2003 - Tim - API Gateway prototype
        // This is for performance test; remove eventually
        //addIgnoreAuthenticationJSP("servlet/api/GatewayController");


        /*
         * Add pages to be ignored from security checks.
         * 
         * Most pages require security checks. Exceptions are listed here.
         * Each set of pages should be documented as to why they are ignored.
         */

        // Layout templates and include files.
        // These pages are used for layout of content provided by other pages.
        // They provide no useful content themselves.
        addIgnoreAuthenticationJSP("ReportError.jsp");
        addIgnoreAuthenticationJSP("ReportErrorProcessing.jsp");
        addIgnoreAuthenticationJSP("template/PreAuthentication.jsp");
        addIgnoredServlet("template/PreAuthentication.jsp");
        addIgnoredServlet("template/Default.jsp");
        addIgnoredServlet("template/Popup.jsp");
        addIgnoredServlet("template/IFrame.jsp");


        // Application's NavigationFrameset, toolbars, static pages, etc. don't need access checks.
        // These pages provide no information directly.
        addIgnoreAuthenticationJSP("NavigationFrameset.jsp");
        addIgnoreAuthenticationJSP("toolbar/Main.jsp");
        addIgnoreAuthenticationJSP("toolbar/include/Main.jsp");
        addIgnoreAuthenticationJSP("toolbar/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("project/NavBar.jsp");
        addIgnoreAuthenticationJSP("project/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("personal/NavBar.jsp");
        addIgnoreAuthenticationJSP("personal/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("business/NavBar.jsp");
        addIgnoreAuthenticationJSP("admin/NavBar.jsp");
        addIgnoreAuthenticationJSP("business/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("admin/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("configuration/NavBar.jsp");
        addIgnoreAuthenticationJSP("configuration/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("methodology/NavBar.jsp");
        addIgnoreAuthenticationJSP("methodology/include/NavBar.jsp");
        addIgnoreAuthenticationJSP("enterprise/Main.jsp");
        addIgnoreAuthenticationJSP("enterprise/NavBar.jsp");
        addIgnoreAuthenticationJSP("enterprise/include/NavBar.jsp");


        // Personal Space will only present info from the User bean
        // Access security is enforced via Authentication.  
        // User bean is only set if authenticated successfully.
        addIgnoredServlet("personal/Main.jsp");
        addIgnoredServlet("portfolio/PersonalPortfolio.jsp");   // displays projects user is member of
        addIgnoredServlet("business/BusinessPortfolio.jsp");    // displays businesses a user is member of
        addIgnoredServlet("portfolio/ConfigurationPortfolio.jsp"); // displays configurations a user is member of
        addIgnoredServlet("project/RemoveProjectProcessing.jsp"); // user can only remove himself, no need for check
        addIgnoredServlet("project/ProjectRegister.jsp");
        addIgnoredServlet("project/ProjectRegisterProcessing.jsp");
        addIgnoredServlet("project/BusinessRegister.jsp");
        addIgnoredServlet("project/BusinessRegisterProcessing.jsp");
        addIgnoredServlet("admin/ApplicationRegister.jsp");
        addIgnoredServlet("configuration/ConfigurationRegister.jsp");
        addIgnoredServlet("methodology/MethodologyList.jsp");           // Security based on user's current space
        addIgnoredServlet("methodology/MethodologyBrowser.jsp");        // Security based on current user

        // Month popup does not need security
        addIgnoreAuthenticationJSP("calendar/MiniMonthPopup.jsp");
        addIgnoreAuthenticationJSP("calendar/MiniMonthPopupProcessing.jsp");
        addIgnoredServlet("calendar/MiniMonthPopup.jsp");
        addIgnoredServlet("calendar/MiniMonthPopupProcessing.jsp");

        // Channel management only affects users personal settings
        // Security enforced via Authentication
        addIgnoredServlet("channel/ChannelProcessing.jsp");
        addIgnoredServlet("channel/CustomizeChannels.jsp");
        addIgnoredServlet("channel/EnableChannelsProcessing.jsp");

        // SPECIAL CASE:
        // Space Main.jsp pages must perform special security checks within the
        // page because normal security check is performed on current space (usually PersonalSpace)
        // and these require changing space.
        addIgnoredServlet("project/Main.jsp");
        addIgnoredServlet("project/Dashboard");
        addIgnoredServlet("business/Main.jsp");
        addIgnoredServlet("admin/Main.jsp");
        addIgnoredServlet("configuration/Main.jsp");
        addIgnoredServlet("configuration/include/Properties.jsp");
        addIgnoredServlet("methodology/Main.jsp");
        addIgnoredServlet("enterprise/Main.jsp");
        addIgnoredServlet("resources/Main.jsp");
        
        //For Resources Tab : This needs to be revisited once 
        //we decide final list of reports in 8.4.0
        addIgnoredServlet("resource/management/manageresourceslist.form");  
        addIgnoredServlet("resource/management/manageresourceslist");
        addIgnoredServlet("resource/management/select.viewform");
        addIgnoredServlet("resource/management/selectresource_v8_5.viewform");
        addIgnoredServlet("resource/management/selectresource_v8_5");
        addIgnoredServlet("resource/management/viewdetails_v8_5.displayform");
        addIgnoredServlet("resource/management/viewdetails_v8_5.viewform");
        addIgnoredServlet("resource/management/viewdetails_v8_5");
        addIgnoredServlet("resource/management/allocateby.resourceallocationform");
        addIgnoredServlet("resource/management/allocateby");
        addIgnoredServlet("resource/management/allocatebyproject.projectallocationform");
        addIgnoredServlet("resource/management/allocatebyproject");
        
        // Blog created action from Tapestry form need to be ignored        
        addIgnoredServlet("blog/addweblogentry.form");
        addIgnoredServlet("blog/addweblogentry");
        addIgnoredServlet("blog/view.form");
        addIgnoredServlet("blog/view");
        addIgnoredServlet("blog/addweblogentrycomment.form");
        addIgnoredServlet("blog/addweblogentrycomment");        
        addIgnoredServlet("blog/view.myprofile");
        addIgnoredServlet("personal/imageupload.form");
        addIgnoredServlet("personal/Imageupload");
        addIgnoredServlet("personal/profile");
        addIgnoredServlet("personal/profile.bloglink");
        addIgnoredServlet("blog/entries");
        addIgnoredServlet("personal/uploaddocument.form");
        addIgnoredServlet("personal/uploaddocument");
        addIgnoredServlet("blog/entry");
        addIgnoredServlet("blog/view/get_moduleid_from_space");
        addIgnoredServlet("blog/view/get_last_blogit_date_link");
        
        // Wiki created action from Tapestry form need to be ignored        
        /*addIgnoredServlet("wiki/editwikipage.form");
        addIgnoredServlet("wiki/editwikipage");
        addIgnoredServlet("wiki/welcome.form");
        addIgnoredServlet("wiki/welcome");
        addIgnoredServlet("wiki/upload.form");
        addIgnoredServlet("wiki/welcome.imgfrm");
        addIgnoredServlet("wiki/Controller");
        addIgnoredServlet("wiki/index");
        addIgnoredServlet("wiki/upload");
        addIgnoredServlet("wiki/history");*/
        addIgnoreAuthenticationDirectory("wiki");
        addIgnoredServlet("/wiki.create");
        addIgnoredServlet("/wiki.form");
        addIgnoredServlet("pwiki/upload");
        addIgnoredServlet("pwiki/upload.form");
        addIgnoredServlet("pwiki/WikiAjaxHandler");
        
        addIgnoreAuthenticationDirectory("directory");
        
        // Project Portfolio module
        addIgnoreAuthenticationDirectory("portfolio");
        
        //Charge-code
        addIgnoreAuthenticationDirectory("chargecode");
        
        // Timesheet module 
        addIgnoreAuthenticationDirectory("timesheet");
        
        addIgnoreAuthenticationDirectory("workplan");
        addIgnoreAuthenticationDirectory("assignments");
        
        // Used by document applet, security check is done inside jsp
        addIgnoredServlet("document/AppletCreateDocumentProcessing.jsp");
        addIgnoreAuthenticationJSP("document/NewSession.jsp");
        addIgnoreAuthenticationJSP("document/KillSession.jsp");
        // Both these servlets perform their own security checks
        addIgnoredServlet("servlet/DownloadDocument");
        addIgnoredServlet("servlet/DownloadVersion");

        addIgnoredServlet("document/PropertyFrameset.jsp");
        addIgnoredServlet("document/ContainerSortProcessing.jsp");
        addIgnoredServlet("document/DocumentErrorHandler.jsp");
        addIgnoredServlet("document/LaunchDocumentApplet.jsp");
        addIgnoredServlet("document/LaunchDocumentAppletGetCookies.jsp");

        // Unknown reason for presence of this JSP
        addIgnoredServlet("document/DocumentProvider.jsp");


        // Workflow Security

        // CloseWindow is a generic program that simply closes the window in which
        // it appears.
        addIgnoredServlet("workflow/CloseWindow.jsp");

        // WorkflowMenu is designed to be included by other modules.  It therefore
        // cannot implement security.  It provides no update capability.
        addIgnoredServlet("workflow/include/WorkflowMenu.jsp");

        // WorkflowErrors must avoid the need for authentication, much like /errors.jsp
        // AuthenticationErrors occurring within Workflow will be routed to this
        // jsp before being forwarded to /errors.jsp */
        addIgnoreAuthenticationJSP("workflow/WorkflowErrors.jsp");

        // These perform their own security check : ensuring user has permission
        // to view the envelope
        addIgnoredServlet("workflow/envelope/EnvelopeProperties.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopePropertiesDetail.jsp");


        // Methodology Security

        // Ability to edit methodology implied with access to methodology space
        // These pages only permit edit on current space (i.e. methodology space)
        addIgnoredServlet("methodology/PropertiesEdit.jsp");
        addIgnoredServlet("methodology/PropertiesEditProcessing.jsp");

        addIgnoredServlet("servlet/HeaderServlet");
        
        // Activity Log directory
        addIgnoreAuthenticationDirectory("activity");

        //------------------------------------------------------------
        // TESTING SECTION
        //

        // PAGES LISTED HERE ARE FOR TESTING ONLY
        // IF ANY PAGES ARE REQUIRED TO BE IGNORED, MOVE THEM TO THE
        // UPPER SECTION

        // Schedule exclusions -- Phil Dixon 9/9/00
        addIgnoreAuthenticationJSP("schedule/Gantt.jsp");
        addIgnoreAuthenticationJSP("schedule/GanttTester.jsp");
        addIgnoreAuthenticationJSP("schedule/gantt/Navigation.jsp");

        // brand
        addIgnoredServlet("configuration/brand/AddToken.jsp");
        addIgnoredServlet("configuration/brand/AddTokenProcessing.jsp");
        addIgnoredServlet("configuration/brand/Back.jsp");
        addIgnoredServlet("configuration/brand/CreateBrand.jsp");
        addIgnoredServlet("configuration/brand/CreateBrandProcessing.jsp");
        addIgnoredServlet("configuration/brand/BrandTokenEdit.jsp");
        addIgnoredServlet("configuration/brand/BrandTokenEditProcessing.jsp");

        // Notifcation exclusions -- Phil Dixon 9/9/00
        addIgnoredServlet("notification/Main.jsp");
        //addIgnoredServlet("notification/CreateTypeSubscription1.jsp");
        addIgnoredServlet("notification/CreateTypeSubscription1Processing.jsp");
        addIgnoredServlet("notification/CreateSubscription1.jsp");
        addIgnoredServlet("notification/CreateSubscription1Processing.jsp");
        addIgnoredServlet("notification/CreateSubscription2.jsp");
        addIgnoredServlet("notification/CreateSubscription2Processing.jsp");
        addIgnoredServlet("notification/CreateSubscription3.jsp");
        addIgnoredServlet("notification/CreateSubscription3Processing.jsp");
        addIgnoredServlet("notification/ViewSubscriptions.jsp");
        addIgnoredServlet("notification/ManageSubscriptions.jsp");
        addIgnoredServlet("notification/ManageSubscriptionProcessing.jsp");
        addIgnoredServlet("notification/EditSubscription.jsp");
        addIgnoredServlet("notification/EditSubscriptionProcessing.jsp");
        addIgnoredServlet("notification/RemoveSubscriptionProcessing.jsp");
        addIgnoredServlet("notification/ViewSubscriptionProperties.jsp");
        addIgnoredServlet("notification/ViewSubscriptionPropertiesProcessing.jsp");
        addIgnoredServlet("notification/ClosePopup.jsp");

        //
        // Forms Security
        //
        /* Not implemented on Form Instance Editing yet */
        addIgnoredServlet("form/TextAreaPopup.jsp");
        addIgnoreAuthenticationJSP("form/TextAreaPopup.jsp");
        addIgnoredServlet("form/ChangeHistory.jsp");
        addIgnoredServlet("form/AccessHistory.jsp");
        addIgnoredServlet("form/XmlDump.jsp");


        // CSV JSPs start
        // For time being ... Need to bypass security for CSV Importer
        addIgnoredServlet("base/Busy.jsp");
        addIgnoredServlet("admin/portfolio/RemoveProjectProcessing.jsp");
        addIgnoreAuthenticationJSP("admin/portfolio/RemoveProjectProcessing.jsp");
        addIgnoredServlet("admin/CreateBusinessAccount.jsp");
        addIgnoreAuthenticationJSP("admin/CreateBusinessAccount.jsp");
        addIgnoredServlet("admin/BusinessAccountEdit.jsp");
        addIgnoreAuthenticationJSP("admin/BusinessAccountEdit.jsp");
        addIgnoredServlet("business/BusinessAccountList.jsp");
        addIgnoreAuthenticationJSP("business/BusinessAccountView.jsp");
        addIgnoredServlet("business/BusinessAccountView.jsp");
        addIgnoreAuthenticationJSP("business/BusinessAccountList.jsp");
        addIgnoredServlet("admin/CreateBusinessAccountProcessing.jsp");
        addIgnoreAuthenticationJSP("admin/CreateBusinessAccountProcessing.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/main.jsp");
        addIgnoredServlet("form/CSVImport.jsp");
        addIgnoreAuthenticationJSP("form/CSVImport.jsp");
        addIgnoredServlet("datatransform/csv/main.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/ColumnMap.jsp");
        addIgnoredServlet("datatransform/csv/ColumnMap.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/DataResolution.jsp");
        addIgnoredServlet("datatransform/csv/DataResolution.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/FileUpload.jsp");
        addIgnoredServlet("datatransform/csv/FileUpload.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/FileUploadProcessing.jsp");
        addIgnoredServlet("datatransform/csv/FileUploadProcessing.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/ValidationResults.jsp");
        addIgnoredServlet("datatransform/csv/ValidationResults.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/ErrorResults.jsp");
        addIgnoredServlet("datatransform/csv/ErrorResults.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/ImportResults.jsp");
        addIgnoredServlet("datatransform/csv/ImportResults.jsp");
        addIgnoreAuthenticationJSP("datatransform/csv/ErrorenousRowWriter.jsp");
        addIgnoredServlet("datatransform/csv/ErrorenousRowWriter.jsp");
        addIgnoredServlet("servlet/Download");
        addIgnoreAuthenticationJSP("servlet/Download");
        addIgnoreAuthenticationJSP("datatransform/csv/ValidationResultsStatus.jsp");
        addIgnoredServlet("datatransform/csv/ValidationResultsStatus.jsp");
        // Deepak's CSV JSPs end

        // TEMPORARY -- NO SECURITY FOR Envelopes
        // Tim Morrow 11/13/00
        addIgnoredServlet("workflow/envelope/EnvelopePropertiesProcessing.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardStart.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardPage1.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardPage1Processing.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardPage2.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardPage2Processing.jsp");
        addIgnoredServlet("workflow/envelope/EnvelopeWizardEnd.jsp");
        addIgnoredServlet("workflow/envelope/TransitionPerform.jsp");
        addIgnoredServlet("workflow/envelope/TransitionProblem.jsp");
        addIgnoredServlet("workflow/envelope/TransitionProblemProcessing.jsp");
        addIgnoredServlet("workflow/envelope/include/EnvelopeProperties.jsp");
        addIgnoredServlet("workflow/envelope/include/EnvelopeVersionList.jsp");
        addIgnoredServlet("workflow/envelope/include/EnvelopeContent.jsp");
        addIgnoredServlet("workflow/envelope/include/EnvelopeList.jsp");
        addIgnoredServlet("workflow/envelope/include/EnvelopeListProcessing.jsp");
        addIgnoredServlet("workflow/envelope/include/HistoryList.jsp");
        addIgnoredServlet("workflow/envelope/include/MyEnvelopeList.jsp");
        addIgnoredServlet("workflow/envelope/include/TransitionSelect.jsp");
        addIgnoredServlet("workflow/envelope/include/StepIndicator.jsp");
        
        //For Personal Working time calendar(T5 version)
        
        addIgnoredServlet("personal/workcalendar/workweekmodify.workweekmodifyform");
        addIgnoredServlet("personal/workcalendar/workweekmodify");
        addIgnoredServlet("personal/workcalendar/workday");
        addIgnoredServlet("personal/workcalendar/workday.workdayform");
        addIgnoredServlet("personal/workcalendar/workdate.workdateform");
        addIgnoredServlet("personal/workcalendar/workdate");
        addIgnoredServlet("personal/workcalendar/workdatemodify.workdatemodifyform");
        addIgnoredServlet("personal/workcalendar/workdatemodify");
        
        addIgnoredServlet("documents/upload.form");
        addIgnoredServlet("documents/upload");
        
        //For business time submittal report 
        addIgnoredServlet("business/report/timesummary");
        addIgnoredServlet("business/report/timesummaryview");
    }

    /**
     * Don't check access on the specified Servlet (JSP Page). Note: For pages that are ignored, no security is checked,
     * and hence the module, action and id values are not stored in {@link SecurityProvider}.
     * @param servletName the JSP or servlet name to ignore security on
     */
    protected static void addIgnoredServlet(String servletName) {
        IGNORED_SERVLET_NAMES.remove(servletName);
        IGNORED_SERVLET_NAMES.put(servletName, servletName);
    }

    /**
     * Specifies that Authentication is not required for accessing specified JSP page.  For example, Help pages, Login
     * page etc.
     * @param jspName the JSP for which authentication is not required
     */
    private static void addIgnoreAuthenticationJSP(String jspName) {
        IGNORE_AUTHENTICATION_JSP.remove(jspName);
        IGNORE_AUTHENTICATION_JSP.put(jspName, jspName);
    }

    /**
     * Specifies that all JSPs in specified directory do not require security checks.
     * @param dirName the directory
     */
    private static void addIgnoredDirectoryName(String dirName) {
        IGNORED_DIRECTORY_NAMES.remove(dirName);
        IGNORED_DIRECTORY_NAMES.put(dirName, dirName);
    }

    /**
     * Specifies that all JSPs in the specified directory do not require authentication
     * @param dirName the directory name
     */
    private static void addIgnoreAuthenticationDirectory(String dirName) {
        IGNORE_AUTHENTICATION_DIRECTORY.remove(dirName);
        IGNORE_AUTHENTICATION_DIRECTORY.put(dirName, dirName);
    }

    /**
     * Specifies that session initialization is not required in order to access the specified JSP page.  Few pages are
     * exempt from Initialization; the Initialize page itself.
     * @param jspName the JSP page name
     */
    private static void addIgnoreInitializationJSP(String jspName) {
        IGNORE_INITIALIZATION_JSP.remove(jspName);
        IGNORE_INITIALIZATION_JSP.put(jspName, jspName);
    }

    /**
     * This section of code will check on the click of an ACL to ensure that the user is in the State onject and also
     * that the user has properly authenticated
     * @param page the current JSP page
     * @param user the current user
     * @throws AuthenticationRequiredException if the user is not authenticated
     */
    private static void checkAuthentication(String page, User user) throws AuthenticationRequiredException {

        // Active User in session AND sucessfully authenticated?
        if (user == null || !user.getAuthenticated()) {
            throw new AuthenticationRequiredException(page);
        }
    }

    /**
     * Checks to see whether session has been initialized. A session has been initialized if a loaded ProperyProvider is
     * available.
     * @throws net.project.base.SessionNotInitializedException if session is not initialized
     */
    private static void checkSessionInitialized() throws SessionNotInitializedException {
        if (!PropertyProvider.isLoaded()) {
            throw new SessionNotInitializedException("Session has not been initialized. This exception is expected when a new request is serviced.");
        }
    }

    //
    // Instance Members
    //

    /**
     * Access check for a request.
     * This method is called for every user request.
     * Access is allowed if this method returns; an exception is always thrown when disallowed.
     * @param request the name the servlet being requested; of the form <code>/Login.jsp</code> or
     * <code>/document/Main.jsp</code>.
     * @throws SessionNotInitializedException if the requested page requires initialization
     * and the session has not been initialized
     * @throws AuthenticationRequiredException if the requested page requires authentication
     * and the user is not authenticated
     * @throws AuthorizationFailedException if the requested page requires authorization and
     * the user has no permission
     */
    public void allowAccess(HttpServletRequest request) throws SessionNotInitializedException, AuthenticationRequiredException, AuthorizationFailedException {

        String servletName = request.getServletPath();
        ISecurityParameters params = new SecurityParameters(request);

        // Drop leading "/".
        // For legacy reasons, we store ignore paths without preceding "/"
        // characters
        if (servletName.startsWith("/")) {
            servletName = servletName.substring(1);
        }

        allowAccess(servletName, params);
        
    }

	public void manageSpace(HttpServletRequest request){
		ISecurityParameters params = new SecurityParameters(request);
        Space space = null;
        User theUser = SessionManager.getUser();
        try{
	        if (  ObjectFactory.isSpaceType(params.getSID()) && theUser != null && theUser.getCurrentSpace() != null && !params.getSID().equals(theUser.getCurrentSpace().getID()) ){
	        	History history = (History)request.getSession().getAttribute(HISTORY_OBJECT_VARIABLE);
	        	HistoryLevel businessHistoryLevel = history != null ? history.getLevel(HistoryLevel.BUSINESS) : null;
	        	HistoryLevel projectHistoryLevel = history != null ? history.getLevel(HistoryLevel.PROJECT) : null;
	        	
	            if (params.getSpaceType().equals(Space.PERSONAL_SPACE)) {
	                space = new PersonalSpaceBean();
		            space.setID(params.getSID());
		            space.load();
		            request.getSession().setAttribute("personalSpace", (PersonalSpaceBean)space);
		            if(projectHistoryLevel != null){
		            	projectHistoryLevel.setDisplay(space.getName());
		            }
		            
	            } else if (params.getSpaceType().equals(Space.PROJECT_SPACE)) {
	            	space = new ProjectSpaceBean();
		            space.setID(params.getSID());
		            space.load();
		            request.getSession().setAttribute("projectSpace", (ProjectSpaceBean)space);
		            if(businessHistoryLevel != null){
		            	businessHistoryLevel.setDisplay(((ProjectSpaceBean)space).getParentBusinessName());
		            	businessHistoryLevel.setQueryString("module="+net.project.base.Module.BUSINESS_SPACE + "&id=" + ((ProjectSpaceBean)space).getParentBusinessID() );
		            }
		            if(projectHistoryLevel != null){
		            	projectHistoryLevel.setDisplay(space.getName());
		            	projectHistoryLevel.setJspPage(SessionManager.getJSPRootURL()+"/project/Dashboard");
		            	projectHistoryLevel.setQueryString("module="+net.project.base.Module.PROJECT_SPACE + "&id=" + space.getID());
		            }
	            } else if (params.getSpaceType().equals(Space.BUSINESS_SPACE)) {
	            	space = new BusinessSpaceBean();
		            space.setID(params.getSID());
		            space.load();
		            request.getSession().setAttribute("businessSpace", (BusinessSpaceBean)space);
		            if(businessHistoryLevel != null){
		            	businessHistoryLevel.setDisplay(space.getName());
		            	businessHistoryLevel.setJspPage(SessionManager.getJSPRootURL() + "/business/Main.jsp");
		            	businessHistoryLevel.setQueryString("module="+net.project.base.Module.BUSINESS_SPACE + "&id=" + space.getID());
		            	if(projectHistoryLevel != null){
		            		projectHistoryLevel.setShow(false);
		            	}
		            }
	            } else if (params.getSpaceType().equals(Space.METHODOLOGY_SPACE)) {
	            	space = new MethodologySpaceBean();
		            space.setID(params.getSID());
		            space.load();
		            if(projectHistoryLevel != null){
		            	projectHistoryLevel.setDisplay(space.getName());
		            }		            
	
	            } else if (params.getSpaceType().equals(Space.APPLICATION_SPACE) && ApplicationSpace.DEFAULT_APPLICATION_SPACE.isUserSpaceMember(theUser)) {
	            	space = new ApplicationSpace();
		            space.setID(params.getSID());
		            space.load();
		            request.getSession().setAttribute("applicationSpace", (ApplicationSpace)space);
		            if(projectHistoryLevel != null){
		            	projectHistoryLevel.setDisplay(space.getName());
		            }		            
	
	            } else if (params.getSpaceType().equals(Space.CONFIGURATION_SPACE)) {
	            	space = new ConfigurationSpaceManager();
		            space.setID(params.getSID());
		            space.load();
		            request.getSession().setAttribute("configurationSpace", (ConfigurationSpaceManager)space);
		            if(projectHistoryLevel != null){
		            	projectHistoryLevel.setDisplay(space.getName());
		            }		            
	
	            }
	            if(space != null) {
		            if(theUser != null){
		            	theUser.setCurrentSpace(space);
		            	SecurityProvider.getInstance().setSpace(space);
		            	request.getSession().setAttribute("user", theUser);
		            }
	            }
	        }
	        
        } catch (PnetException pe) {
        	Logger.getLogger(ServletSecurityProvider.class).error("ServletSecurityProvider.allowAccess() threw an SQL exception: " + pe);
        }
    }

    /**
     * Checks for access based on the servlet (jsp) name and parameters.
     * @param servletName
     * @param params
     * @throws SessionNotInitializedException if the requested page requires initialization
     * and the session has not been initialized
     * @throws AuthenticationRequiredException if the requested page requires authentication
     * and the user is not authenticated
     * @throws AuthorizationFailedException if the requested page requires authorization and
     * the user has no permission
     */
    static void allowAccess(String servletName, ISecurityParameters params) throws SessionNotInitializedException, AuthenticationRequiredException, AuthorizationFailedException {

        // Get the page Name. Here, the servletName name indicates a top level page which
        // JSP includes may have been called from
        //  The page will be null when we are NOT processing an include
        String dirName = null;
        String page = servletName;
        String moduleName = null;

        // strip off the jsp file and save the directory name
        int pos = page.lastIndexOf("/");
        if (pos > 0) {
            dirName = page.substring(0, pos);
        }
        pos = page.indexOf("/");
        if (pos > 0) {
        	moduleName = page.substring(0, pos);
        }

        // Check to see if acess to page should ALWAYS be allowed, even without
        // application initialization. (E.g. "Initialize.jsp" itself)
        if (IGNORE_INITIALIZATION_JSP.get(page) != null) {
            return;
        }

        // Check session is initialized
        // This throws an exception if session not initialized
        checkSessionInitialized();

        // Check to see if access to the page should be allowed regardless of authentication
        // If Authentication is not required, we are done
        if (IGNORE_AUTHENTICATION_JSP.get(page) != null || (dirName != null && IGNORE_AUTHENTICATION_DIRECTORY.get(dirName) != null) || (moduleName != null && IGNORE_AUTHENTICATION_DIRECTORY.get(moduleName) != null)) {
            return;
        }

        // At this point, Session is initialized and Authentication is required
        // Make sure the user is authenticated, if not they will be directed to the login screen.

        User theUser = SessionManager.getUser();
        checkAuthentication(page, theUser);

        // Check to see if security check for the page should be ignored
        if (IGNORED_SERVLET_NAMES.get(page) != null || (dirName != null && IGNORED_DIRECTORY_NAMES.get(dirName) != null)) {

            // XXX Temporary - populate the checked ids with values that would be checked had
            // we actually performed a security checked

            // 11/02/2001 - Tim.  What this code is doing is checking access anyway
            // even if the JSP is ignored;  it traps the Exception if access is denied
            // This is an attempt to set up the checked module, action and id
            // even when ignoring a page.
            // I don't think it works though
            try {
                checkAccess(page, params);

            } catch (Exception ex) {
                // do nothing
            }
            // END: XXX

            return;
        }


        // Page is not being ignored
        // Perform the actual security check
        try {
            checkAccess(page, params);

        } catch (AuthorizationFailedException afe) {

            SecurityProvider securityProvider = SessionManager.getSecurityProvider();
            // Special cases, should be very few of these
            if (theUser.getCurrentSpace().getType().equals(Space.PERSONAL_SPACE) && securityProvider.getCheckedModuleID() == Module.DOCUMENT) {
                // Per Tom K. users will be denied access to their document module until they have
                // created a project.
                throw new AuthorizationFailedException(PropertyProvider.get("prm.security.servletsecurity.accessdocumentmodule.noproject.message"));
            } else {
                throw afe;
            }
        }

        // All security denials are done via throws.  No throws implies access granted.
        return;
    }

    /**
     * Check to see if the User is allowed to perform the requested action on the requested resource.
     * <p>
     * Access is granted if this method succeeds without throwing an exception.
     * </p>
     * @param page the current JSP page
     * @param params
     * @throws AuthorizationFailedException
     */
    private static void checkAccess(String page, ISecurityParameters params) throws AuthorizationFailedException {

        SecurityProvider securityProvider;


        // Locate the session scoped security provider
        securityProvider = SessionManager.getSecurityProvider();

        // Sets the Module, Action, Object to the Security Provider via set Methods.
        // This done, in order that every page must check the values the security
        // was done against to ensure that the application was not entered by
        // circumventing the Security
        if (params.hasModule()) {
            try {
                securityProvider.setCheckedModuleID(Integer.parseInt(params.getModule()));

            } catch (NumberFormatException nex) {
                throw new AuthorizationFailedException(PropertyProvider.get("prm.security.servletsecurity.accessdenied.invalidmodule.message"));

            }
        } else {
            securityProvider.setCheckedModuleID(0);
        }

        if (params.hasID()) {
            securityProvider.setCheckedObjectID(params.getID());
        } else {
            securityProvider.setCheckedObjectID("");
        }

        // Business Rule - If there is no Action attribute then set to View
        int actionValue = params.getActionValue();

        securityProvider.setCheckedActionID(actionValue);

        //Security check call to the security Provider class
        // Throws an Exception if security not permitted
        securityProvider.securityCheck(params.getID(), params.getModule(), actionValue);

        // At this point, permissions are ok
    }

    //
    // Nested top-level classes
    //

    /**
     * Implementing classes provide request values for <code>module</code>, <code>action</code> and <code>id</code>.
     * <p/>
     * It is expected that each implementor first looks in the request scope attributes
     * then at the request parameters to determine each value.
     */
    interface ISecurityParameters {

        /**
         * Indicates whether a module value was found in the request.
         * @return true if there is a non-blank module value
         */
        boolean hasModule();

        /**
         * Returns the module value or null if no non-blank value was found.
         * @return the (numeric) value for the module
         */
        String getModule();

        /**
         * Indicates whether an action value was found in the request.
         * @return true if there is a non-blank action value
         */
        boolean hasAction();

        /**
         * Returns the action value or null if no non-blank value was found.
         * @return the (numeric) value for the action
         */
        String getAction();

        /**
         * Returns the action value as an int or the default action of <code>VIEW</code>.
         * @return the int action value or {@link Action#VIEW} if there is no action
         * @throws AuthorizationFailedException if there is a problem converting the action to
         * an int value
         */
        int getActionValue() throws AuthorizationFailedException;

        /**
         * Indicates whether an id value was found in the request.
         * @return true if there is a non-blank id value
         */
        boolean hasID();

        /**
         * Returns the id value or null if no non-blank value was found.
         * @return the (numeric) value for the id
         */
        String getID();

        /**
         * Indicates whether an id value was found in the request.
         * @return true if there is a non-blank id value
         */
        boolean hasSID();

        /**
         * Returns the id value or null if no non-blank value was found.
         * @return the (numeric) value for the id
         */
        String getSID();
        
        String getSpaceType();        
    }

    /**
     * Provides common functionality for implementors of ISecurityParameters.
     */
    static abstract class AbstractSecurityParameters implements ISecurityParameters {

        public boolean hasModule() {
            return !Validator.isBlankOrNull(getModule());
        }

        public boolean hasAction() {
            return !Validator.isBlankOrNull(getAction());
        }

        public int getActionValue() throws AuthorizationFailedException {
            int actionValue;

            if (!hasAction()) {
                actionValue = Action.VIEW;

            } else {

                try {
                    actionValue = Integer.parseInt(getAction());
                } catch (NumberFormatException nex) {
                    throw new AuthorizationFailedException(PropertyProvider.get("prm.security.servletsecurity.accessdenied.invalidaction.message"));
                }
            }

            return actionValue;
        }

        public boolean hasID() {
            return !Validator.isBlankOrNull(getID());
        }

        public boolean hasSID() {
            return !Validator.isBlankOrNull(getSID());
        }
    }

    /**
     * Provides security parameters from a servlet request.
     */
    private static class SecurityParameters extends AbstractSecurityParameters implements ISecurityParameters {

        private final String module;
        private final String action;
        private final String id;
        private String sid;
        private String spaceType;

        private SecurityParameters(javax.servlet.http.HttpServletRequest request) {
        	
            String module = (String) request.getAttribute("module");
            String action = (String) request.getAttribute("action");
            String id = (String) request.getAttribute("id");

            if (module == null) {
                module = request.getParameter("module");
            }

            if (action == null) {
                action = request.getParameter("action");
            }

            if (id == null) {
                id = request.getParameter("id");
                boolean isIdSpaceType = false;
                
                if(StringUtils.isNotEmpty(id) )
                	isIdSpaceType = ObjectFactory.isSpaceType(id);
                
                if(!isIdSpaceType && request.getParameter("s") == null && request.getParameter("redirectedFromSpace") == null){
                	this.sid = getCookie(request, "_sid");
                	this.spaceType = getCookie(request, "_styp");
                }
                if(isIdSpaceType){
                	this.sid = id;
                	this.spaceType = ObjectFactory.getObjectType(id);
                }
            }

            // Ensure empty value is really null
            if (id != null && ((id.trim().length() == 0)||(id.equals("null")))) {
                id= null;
            }

            this.module = module;
            this.action = action;
            this.id = id;
        }

        public String getModule() {
            return module;
        }

        public String getAction() {
            return action;
        }

        public String getID() {
            return id;
        }
        
        public String getSID() {
            return sid;
        }
        
        public String getSpaceType() {
            return spaceType;
        }        
    }
    
    /*
     * This method is used to set explicitly values of id , module and action
     * in net.project.security.SecurityProvider session object for security verification 
     * of the requested resource
     */
    public static final void setAndCheckValues(ServletRequest request) throws java.io.IOException, javax.servlet.ServletException{
        if (request instanceof HttpServletRequest) {
            ServletSecurityProvider servletSecurityProvider = new ServletSecurityProvider();
            servletSecurityProvider.allowAccess((HttpServletRequest) request);
        }
    }

    /** Find a cookie by name; return first found */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] allCookies;

        if ( name == null ) {
            return null;
        }

        allCookies = request.getCookies();
        if (allCookies != null) {
            for (int i=0; i < allCookies.length; i++) {
                Cookie candidate = allCookies[i];
                if (name.equals(candidate.getName()) ) {
                    return candidate.getValue();
                }
            }
        }
        return null;
    }
}


