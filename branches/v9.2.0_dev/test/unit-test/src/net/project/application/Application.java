/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 13981 $
|       $Date: 2005-03-17 03:07:31 +0530 (Thu, 17 Mar 2005) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.application;

import net.project.admin.ApplicationSpace;
import net.project.base.PnetRuntimeException;
import net.project.security.User;
import net.project.security.SessionManager;
import net.project.security.SecurityProvider;
import net.project.mockobjects.MockHttpSession;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.test.TestSessionProvider;
import net.project.base.PnetException;

import java.util.TimeZone;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * The application object simulates an Application Server that we would use to
 * run our Application, along with some of the services that we normally run
 * "behind the scenes" such as setting up usings, Security Providers, etc.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class Application {
    /** The user that is currently logged into the application. */
    private static User user = new User();
    /** The current session that the user is involved in. */
    private static MockHttpSession session = new MockHttpSession();
    /** This is a list of servlets that the application server recognizes. */
    private static Set registeredServlets = new HashSet();

    /**
     * Set up internal objects that should be available through Session Manager,
     * such as the concept of a "currently logged in user" and a "session".
     */
    public static void login() {
        setupSession();
        setupSecurityProvider();
        setupLoggedInUser();
    }

    private static void setupSecurityProvider() {
        SecurityProvider security = new SecurityProvider();
        session.setAttribute("securityProvider", security);
    }

    private static void setupSession() {
        // The test TestSessionProvider actually uses a threadlocal
        ((TestSessionProvider) Compatibility.getSessionProvider()).setLocalSession(session);
    }

    private static void setupLoggedInUser() {
        //Set up the user login to be the app admin -- it should always exist
        //for cases where we need to save to databases.
        user.setID("1");
        user.setLogin("appadmin");
        try {
            user.setCurrentSpace(ApplicationSpace.DEFAULT_APPLICATION_SPACE);
        } catch (PnetException e) {
            throw new PnetRuntimeException("Unable to set user.setCurrentSpace: ", e);
        }
        updateUserSettingsDefault();
        SessionManager.setUser(user);
    }

    /**
     * Updates the currently logged-in user to give them default values.
     * <p>
     * Defaults are:
     * <li>TimeZone - PST
     * <li>Locale - US
     * </p>
     */
    public static void updateUserSettingsDefault() {
        updateUserSettings(TimeZone.getTimeZone("PST"), Locale.US);
    }

    /**
     * Updates the currently logged-in user to give them the specified
     * timeZone and locale.
     * @param timeZone their time zone; ignored when null
     * @param locale their locale; ignored when null
     */
    public static void updateUserSettings(TimeZone timeZone, Locale locale) {
        if (timeZone != null) {
            user.setTimeZone(timeZone);
        }
        if (locale != null) {
            user.setLocale(locale);
        }
    }

    public static void registerServlet(String servletName) {
        registeredServlets.add(servletName);
    }

    /**
     * This method simulates what occurs on the application server when a user
     * requests a page.  This method is largely a convenience method that makes
     * it quite a bit easier to set up Servlet or JSP-based test cases.
     *
     * The following items are dealt with as a side-effect of this method:
     *
     * <ol>
     * <li> Creation of an HttpServletRequest object suitable for passing to a
     * servlet or JSP page.</li>
     * <li> Parsing of all the parameters from the uri.  All of them will be set
     * as parameters in the request object.</li>
     * <li> Setting the "extra path info" that can be accessed from request.getPathInfo()</li>
     * <li> Setting the "query string" that can be accessed through request.getQueryString()</li>
     * <li> Setting up the "checked" action, module, and id in the security
     * provide object.</li>
     * <li> Setting the session in the request</li>
     * </ol>
     *
     * <b>Prerequisites</b>
     * You must have called Application.login() prior to calling this method.
     *
     * @param requestURI
     * @return
     */
    public static MockHttpServletRequest requestPage(String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        //First, figure out the path info
        int questionMarkIndex = requestURI.indexOf('?');
        if (questionMarkIndex < 0) {
            request.setRequestURI(requestURI);
        } else {
            request.setRequestURI(requestURI.substring(0, questionMarkIndex));

            String parameterString = requestURI.substring(questionMarkIndex+1);
            StringTokenizer st = new StringTokenizer(parameterString, "&");

            while (st.hasMoreTokens()) {
                String parameterPair = st.nextToken("&");
                int equalsPosition = parameterPair.indexOf("=");
                String name = parameterPair.substring(0, equalsPosition);
                String value = parameterPair.substring(equalsPosition+1);

                request.addParameter(name, value);
            }
        }

        //Figure out if there is extra path info to add
        String requestURIWithoutParams = request.getRequestURI();
        for (Iterator it = registeredServlets.iterator(); it.hasNext();) {
            String servletName = (String)it.next();
            if (requestURI.startsWith(servletName)) {
                int extraPathInfoIndex = servletName.length()-1;
                if (extraPathInfoIndex+1 < requestURIWithoutParams.length()) {
                    request.setPathInfo(requestURIWithoutParams.substring(extraPathInfoIndex+1));
                }

                break;
            }
        }

        //Set the security parameters in the SecurityProvider object
        int action = Integer.parseInt(request.getParameter("action"));
        int module = Integer.parseInt(request.getParameter("module"));
        String id = request.getParameter("id");
        setSecurityParameters(action, module, id);

        request.setSession(session);
        request.setQueryString(requestURI);

        return request;
    }

    public static void setSecurityParameters(int actionID, int moduleID, String objectID) {
        SecurityProvider sp = SessionManager.getSecurityProvider();
        sp.setCheckedActionID(actionID);
        sp.setCheckedModuleID(moduleID);
        sp.setCheckedObjectID(objectID);
    }
}
