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

import java.io.File;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.IConfigurationProvider;
import net.project.base.compatibility.ISessionProvider;
import net.project.base.property.PropertyBundle;
import net.project.base.property.PropertyProvider;
import net.project.brand.BrandManager;
import net.project.document.DocumentManagerBean;
import net.project.schedule.Schedule;

import org.apache.commons.lang.StringUtils;

/**
 * Provides static methods to access configuration settings. 
 * The source of these settings is determined by current application server compatibility model.
 *
 * @since Version 1.0
 */
public final class SessionManager implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static String DEFAULT_APPLICATION_NAME = "pnet";
	private static String JSP_ROOT_URL = "";
    

    /**
     * Captures some information from the request, including hostname and protocol.
     */
    public static void initializeFromRequest(javax.servlet.http.HttpServletRequest request) {

        String hostname = net.project.util.HttpUtils.getRequestHostname(request);
        String siteScheme = net.project.util.HttpUtils.getRequestProtocol(request);
        JSP_ROOT_URL = request.getContextPath();        
        // Save the hostname and URL protocol the user used when entering the site.
        setSiteHost(hostname);
        setSiteScheme(siteScheme);
    }
    
    /**
     * Logs out specified user and invalidates the user's session.
     *
     * @param request the current request
     * @param user the user to logout
     */
    public static void logout(javax.servlet.http.HttpServletRequest request, net.project.security.User user) {
    	try {
    		user.logout();
    		request.getSession(false).invalidate();
    	 } catch (Exception e) {
     		// we are logging out, don't care about presenting errors to user.
     		// Possibly the session is already null when the user logs out after a session timeout?
             e.printStackTrace();
         }
    }

    public static void setSessionShortInactiveInterval(javax.servlet.http.HttpServletRequest request) {
    	request.getSession(false).setMaxInactiveInterval(1);
    }
    
    /**
     * Sets the site scheme to be returned by other methods for this session. Typically <code>{@link
     * SessionManager#initializeFromRequest}</code> sets this value; this method may be used to override that value. For
     * example, the notification scheduler must override the scheme since it use HTTP by default while the scheme must
     * be returned as HTTPS. The site scheme is stored in the session.
     *
     * @see SessionManager#getSiteURL
     * @see SessionManager#getAppURL
     */
    public static void setSiteScheme(String siteScheme) {
        Compatibility.getSessionProvider().setAttribute("SiteScheme", siteScheme);
    }

    /**
     * Sets the site host to be returned by other methods for this session. Typically <code>{@link
     * SessionManager#initializeFromRequest}</code> sets this value; this method may be used to override that value. For
     * example, the notification scheduler must override the site host since it uses the IP address of the web server
     * but this must return a domain name The site host is stored in the session.
     *
     * @see SessionManager#getSiteURL
     * @see SessionManager#getAppURL
     */
    public static void setSiteHost(String hostname) {
        Compatibility.getSessionProvider().setAttribute("SiteHost", hostname);
    }

    public static Schedule getSchedule() {
        return (Schedule) Compatibility.getSessionProvider().getAttribute("schedule");
    }

    public static DocumentManagerBean getDocumentManager() {
        return (DocumentManagerBean) Compatibility.getSessionProvider().getAttribute(net.project.document.DocumentManager.DOCUMENT_MANAGER_SESSION_OBJECT_NAME);
    }

    /**
     * Get the current User of this session.
     * 
     * @return the User object currently active in this session.
     */
    public static User getUser() {
        return (User) Compatibility.getSessionProvider().getAttribute("user");
    }

    /**
     * Replaces the user in session with the specified user. Ordinarily the
     *
     * @param user the user to put in the session
     */
    public static void setUser(User user) {
        Compatibility.getSessionProvider().setAttribute("user", user);
    }

    /**
     * Get the current SecurityProvider for this session.
     * 
     * @return the SecurityProvider object currently active in this session.
     */
    public static SecurityProvider getSecurityProvider() {
        return (SecurityProvider) Compatibility.getSessionProvider().getAttribute(SecurityProvider.SECURITY_PROVIDER_SESSION_OBJECT_NAME);
    }

    public static void setSecurityProvider(SecurityProvider securityProvider) {
        Compatibility.getSessionProvider().setAttribute(SecurityProvider.SECURITY_PROVIDER_SESSION_OBJECT_NAME, securityProvider);
    }

    public static PropertyBundle getPropertyBundle() {
        Object propertyBundle = Compatibility.getSessionProvider().getAttribute(PropertyBundle.PROPERTY_BUNDLE_SESSION_OBJECT_NAME);
        if (propertyBundle != null) {
            assert propertyBundle instanceof PropertyBundle : "propertyBundle should be of type PropertyBundle but is " + propertyBundle.getClass().toString();
        }
        return (PropertyBundle)propertyBundle;
    }

    public static void setPropertyBundle(PropertyBundle propertyBundle) {
        Compatibility.getSessionProvider().setAttribute(PropertyBundle.PROPERTY_BUNDLE_SESSION_OBJECT_NAME, propertyBundle);
    }

    public static BrandManager getBrandManager() {
        return (BrandManager) Compatibility.getSessionProvider().getAttribute(BrandManager.BRAND_MANAGER_SESSION_OBJECT_NAME);
    }

    //
    //  Settings from the configuration file
    //

    /**
     * Get the root URL of the application server.
     *
     * @return a <code>String</code> value containing the root url of the application server.
     */
    public static String getJSPRootURL() {
    	String jspUrl = Compatibility.getConfigurationProvider().getSetting("jspRootURL");
        if(StringUtils.isEmpty(jspUrl))
            return JSP_ROOT_URL;
        else if (jspUrl.equals("/"))
        	return "";
        else
            return jspUrl;
    }

    /**
     * Get the root URL of the application server, always in HTTPS format.
     *
     * @return a <code>String</code> value containing the root url of the application server in secure mode.
     */
    public static String getJSPRootURLHTTPS() {
        String rootURL = "https://" + getSiteHost();

        if (rootURL.endsWith(":80")) {
            rootURL = rootURL.substring(0, rootURL.length() - 3);
        }

        rootURL += getJSPRootURL();

        return rootURL;
    }

    /**
     * Get the SMTP mail host to use for email notificaitons.
     * 
     * @return smtp mail host machine (such as smtp.project.net)
     */
    public static String getSmtpHost() {
        return Compatibility.getConfigurationProvider().getSetting("smtpHost");
    }


    /**
     * Get the site hostname.  In production deployments with farm of application servers, 
     * this may be the hostname of the webserver front-end or hardware load balancer.
     *
     * @return the site hostname (such as www.project.net)
     */
    public static String getSiteHost() {
        ISessionProvider sessionProvider = Compatibility.getSessionProvider();
        String siteHost;

        if (sessionProvider != null) {
            siteHost = (String) sessionProvider.getAttribute("SiteHost");
            if (siteHost == null) {
                siteHost = Compatibility.getConfigurationProvider().getSetting("siteHost");    
            }
        } else {
            siteHost = Compatibility.getConfigurationProvider().getSetting("siteHost");
        }

        return siteHost;
    }

     /**
     * Get the site scheme (protocol).
     *
     * @return the site hostname (such as http:// or https://)
     */
    public static String getSiteScheme() {
        ISessionProvider sessionProvider = Compatibility.getSessionProvider();
        String siteScheme;

        if (sessionProvider != null && !PropertyProvider.getBoolean("prm.global.configuration.overwrite-httpscheme")) {
            siteScheme = (String) sessionProvider.getAttribute("SiteScheme");
            if (siteScheme == null) {
                siteScheme = Compatibility.getConfigurationProvider().getSetting("siteScheme");
            }
        } else {
            siteScheme = Compatibility.getConfigurationProvider().getSetting("siteScheme");
        }

        return siteScheme;
    }


    /**
     * Get the support Email address.  May be different for test and production envs.
     *
     * @return the support email address stored in sajava.ini file.
     */
    public static String getSupportEmail() {
        return Compatibility.getConfigurationProvider().getSetting("supportEmail");
    }


    /**
     * get the project.net site from sajava.ini
     * 
     * @return project.net site
     */
    public static String getWebexSite() {
        return Compatibility.getConfigurationProvider().getSetting("webexSite");
    }


    /**
     * Get site URL base.
     * 
     * @return the HTTP site base URL (such as https://www.project.net)
     */
    public static String getSiteURL() {
        return getSiteScheme() + getSiteHost();
    }


    /**
     * Get application URL. Provides the base url to the JSP top directory.
     *
     * @return application base URL of the form http://hostname/default.sph/Pnet.class.
     */
    public static String getAppURL() {
        return getSiteURL() + getJSPRootURL();
    }

    /**
     * Returns the character encoding which the client is expected to use. This value should be set in the client's
     * request and subsequently used to import or export bytes from and to the client.
     *
     * @return the character encoding
     */
    public static String getCharacterEncoding() {
        String characterEncoding = Compatibility.getConfigurationProvider().getSetting("characterEncoding");
        if (characterEncoding == null) {
            characterEncoding = System.getProperty("file.encoding", null);

            // Dump the stack trace to error stream to see how we got in this state
            try {
                throw new Exception("Unable to determine character encoding. Using " + characterEncoding + ".");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return characterEncoding;
    }

    /**
     * Returns the name of the current application, <code>pnet</code>.
     * 
     * @return the current application name
     */
    public static String getApplicationName() {
        return DEFAULT_APPLICATION_NAME;
    }
    
    /**
     * Get the name of the server that can process credit cards if we are using VeriSign as our transaction processor.
     *
     * @return a <code>String</code> containing the server name of the verisign processing server.
     * @see #getVerisignPortNumber
     */
    public static String getVerisignServerName() {
        return Compatibility.getConfigurationProvider().getSetting("verisignServerName");
    }

    /**
     * Returns the path to the directory containing the public key required for SSL connectivity with Verisign.
     *
     * @return the fully qualified path to the directory in which the key is located.
     */
    public static String getVerisignPublicKeyPath() {
        return Compatibility.getConfigurationProvider().getSetting("repositoryPath");
    }

    /**
     * Get the port number used to connect to a VeriSign credit card transaction server.
     *
     * @return a <code>String</code> value containing a port number for VeriSign.
     * @see #getVerisignServerName
     */
    public static String getVerisignPortNumber() {
        return Compatibility.getConfigurationProvider().getSetting("verisignPortNumber");
    }

    /**
     * Does this machine have an SSL certificate installed?
     *
     * @return a <code>boolean</code> value indicating whether this server supports SSL.  This is detected by setting a
     *         property, it isn't detected at runtime.
     */
    public static boolean isSSLSupported() {
        return Boolean.valueOf(Compatibility.getConfigurationProvider().getSetting("sslSupported")).booleanValue();
    }

    /**
     * Indicates whether we should operate in test mode. Test mode means that only test credit card numbers will be
     * accepted. Non-test mode means that only real credit card numbers will be accepted.
     *
     * @return true if test mode is enabled; false otherwise.
     */
    public static boolean isCreditCardTestMode() {
        return Boolean.valueOf(Compatibility.getConfigurationProvider().getSetting("ccTestMode")).booleanValue();
    }

    public static String getCreditCardMerchantAccountClassName() {
        return Compatibility.getConfigurationProvider().getSetting("creditCardMerchantAccountClass");
    }

    /**
     * Returns the upload temporary directory used when uploading MPD files during MPD import.
     *
     * @return the directory path or the empty string if none is found
     */
    public static String getXMLImportTempDirectory() {
        File file = new File(Compatibility.getConfigurationProvider().getSetting("xmlImportTempDirectory"));
        if( file.isDirectory() && file.canWrite() )
            return Compatibility.getConfigurationProvider().getSetting("xmlImportTempDirectory");
        else{
            String tmpDir = System.getProperty("java.io.tmpdir");
            if(StringUtils.isNotBlank(tmpDir) && tmpDir.length() > 1 && ( tmpDir.endsWith("/") || tmpDir.endsWith("\\"))){
                return tmpDir.substring(0, tmpDir.length()-1);
            } else 
                return tmpDir;        
        }
    }

    /**
     * Returns the default upload temporary directory used when uploading documents and files.
     *
     * @return the directory path value of the <code>[sapphire]/postDataTempDir</code> setting or null if none is found
     */
    public static String getDefaultUploadTempDirectory() {
        File file = new File(Compatibility.getConfigurationProvider().getDefaultUploadTempDirectory());
        if( file.isDirectory() && file.canWrite() )
            return Compatibility.getConfigurationProvider().getDefaultUploadTempDirectory();
        else {
            String tmpDir = System.getProperty("java.io.tmpdir");
            if(StringUtils.isNotBlank(tmpDir) && tmpDir.length() > 1 && ( tmpDir.endsWith("/") || tmpDir.endsWith("\\"))){
                return tmpDir.substring(0, tmpDir.length()-1);
            } else 
                return tmpDir;        
        }
    }

    /**
	 * Returns the email character encoding which will be used when sending all
	 * emails.
	 * 
	 * If this value is set incorrectly such that the text placed in the message
	 * contains characters not supported by the specified email character
	 * encoding, problems may occur with the resulting email. For example,
	 * Microsoft Outlook will display a message and attach the real email to
	 * that error message. The real email will not be displayed properly.
	 * 
	 * @return the email character encoding
	 */
    public static String getEmailCharacterEncoding() {
        return Compatibility.getConfigurationProvider().getSetting("emailCharacterEncoding");
    }

    /**
	 * Returns the startup page to be navigated to in absence of any other JSP
	 * page. Typically only used within the application class.
	 * 
	 * @return the startup page relative from the JSP root
	 */
    public static String getStartupPage() {
        return Compatibility.getConfigurationProvider().getSetting("startupPage");
    }

    /**
     * Returns the custom XSL root path.
     * 
     * @return the path to custom XSL files
     */
    public static String getCustomXSLRootPath() {
        return Compatibility.getConfigurationProvider().getSetting("customXSLRootPath");
    }

    public static String getDefaultLayoutTemplate() {
        return Compatibility.getConfigurationProvider().getSetting("defaultLayoutTemplate");
    }

    /**
     * The datatabase tablespace to be used to store tables and other database objects created during runtime.
     * @return the name of the database data tablespace.
     */
    public static String getDataTablespace() {
        return Compatibility.getConfigurationProvider().getSetting("dataTablespace");
    }

    /**
     * The datatabase tablespace to be used to store indexes created during runtime.
     * @return the name of the database index tablespace.
     */
    public static String getIndexTablespace() {
        return Compatibility.getConfigurationProvider().getSetting("indexTablespace");
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {
        // Do nothing
    }
}
