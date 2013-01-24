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

 package net.project.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.project.security.SessionManager;

public class HttpUtils extends javax.servlet.http.HttpUtils {
    /** The request parameter indicating the pnet session id */
    public static final String PNET_SESSION_PARAMETER_NAME = "$sid$";
    /** The session attribute indicating the session id */
    public static final String PNET_SESSION_ATTRIBUTE_NAME = "pnetSessionID";

    /**
     * Returns the prototol of the request such as http:// or https://.
     *
     * @param request the servlet request.
     * @return the prototol of the request such as http:// or https://.
     */
    static public String getRequestProtocol(javax.servlet.http.HttpServletRequest request) {
        String url = getRequestURL(request).toString();
        return url.substring(0, url.indexOf("://") + 3);
    }


    /**
     * Returns the hostname of the request URL.
     *
     * @param request the servlet request.
     * @return the hostname of the request URL..
     */
    static public String getRequestHostname(javax.servlet.http.HttpServletRequest request) {
        String url = getRequestURL(request).toString();
        String path = url.substring(url.indexOf("://") + 3);
        return path.substring(0, path.indexOf("/"));
    }

    /**
     * Encodes a url for redirections
     *
     * @see net.project.util.HttpUtils#encodeURL
     */
    public static String encodeRedirectURL(javax.servlet.http.HttpSession session, String url) {
        return encodeURL(session, url);
    }

    /**
     * Encodes the url to add the pnet session id into the request
     * For example, if the url is <code>http://hippo/Login.jsp?name=tim</code> then
     * it may return <code>http://hippo/Login.jsp?$sid$=123456&name=tim</code><br>
     * The session parameter will always be placed first.
     *
     * @param session the session from which to get the pnet session id
     * @param url the url to encode
     * @return the encoded url or the same url if the session id is not found
     * @see net.project.util.HttpUtils#PNET_SESSION_PARAMETER_NAME
     */
    public static String encodeURL(javax.servlet.http.HttpSession session, String url) {
        // Grab the current pnet session id
        String sessionID = (String)session.getAttribute(PNET_SESSION_ATTRIBUTE_NAME);
        if (url == null || sessionID == null) {
            return url;
        }

        String path = null;
        String query = null;
        int question = url.indexOf("?");
        if (question < 0)
            path = url;
        else {
            path = url.substring(0, question);
            // Query does NOT include the '?'
            query = url.substring(question + 1);
        }

        // Now construct new query from path + session id + existing query
        StringBuffer sb = new StringBuffer(path);
        sb.append("?" + PNET_SESSION_PARAMETER_NAME + "=");
        sb.append(sessionID);
        if (query != null) {
            sb.append("&" + query);
        }

        return sb.toString();
    }

    /**
     * Puts the pnet session id into session by getting it from the request parameters
     * If there is no session id in the request parameters then any existing
     * session id is removed from the session.  This is to prevent bogus url encoding
     * for new session attachments.
     *
     * @param request the request to get the pnet session id from
     * @param session the session to put pnet session id into under the pnet session attribute name
     * @see net.project.util.HttpUtils#PNET_SESSION_PARAMETER_NAME
     * @see net.project.util.HttpUtils#PNET_SESSION_ATTRIBUTE_NAME
     */
    public static void putPnetSessionID(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpSession session) {
        String sessionID = request.getParameter(PNET_SESSION_PARAMETER_NAME);
        if (sessionID != null) {
            session.setAttribute(PNET_SESSION_ATTRIBUTE_NAME, sessionID);
        } else {
            session.removeAttribute(PNET_SESSION_ATTRIBUTE_NAME);
        }
    }

    /**
     * Returns the pnet session id.
     *
     * @return the pnet session id
     */
    public static String getPnetSessionID(javax.servlet.http.HttpSession session) {
        return (String)session.getAttribute(PNET_SESSION_ATTRIBUTE_NAME);
    }

    /**
     * Generates a pnet session id and stores it in the session.  This method should
     * only be called by the first JSP that is executed - if called subsequently
     * the existing session cookies will be lost since the lookup key will have
     * changed.
     *
     * Note: The session in which this id is put will be lost if the client
     * does not support cookies.  However, it is placed in the session to
     * faciliate the use of encodeRedirectURL on the Login page.
     *
     * @param request the request (used to make the id, may not in future)
     * @param session the session to store the id in under the pnet session attribute name
     * @see net.project.util.HttpUtils#PNET_SESSION_ATTRIBUTE_NAME
     */
    public static void generatePnetSessionID(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpSession session) {
        // For now we are using Bluestone's generated session id as a unique id
        // This may not be a good idea since the session that this id represents
        // will get thrown way... this id may get generated again?
        // Also, it is almost certainly not unique when dealing with multiple JVMs
        String sessionID = request.getRequestedSessionId();
        session.setAttribute(PNET_SESSION_ATTRIBUTE_NAME, sessionID);
    }

    /**
     * Returns the path to a servlet based on an entire path.  For example:
     * Given<br>
     * <code>/pnet/document/Main.jsp</code><br>
     * this method will return<br>
     * <code>/document/Main.jsp</code><br>
     * The servlet name is the part of the URI following the application name,
     * in this case "pnet" is the application name.  Note that this would work
     * even if "pnet.class" was the application name.
     *
     * @param requestURI the request URI to get servlet name from
     * @return the servlet name or the original URI if the servlet name could
     * not be determined.  If the servlet name is found but empty, the default
     * startup page is returned.
     * @see net.project.security.SessionManager#getStartupPage
     */
    public static String getServletNameFromRequestURI(String requestURI) {
        //
        //First, try the easy way, using SessionManager.getJSPRootURL().  If
        //that string exists in the request URI, just return what is after it.
        //
        String rootURL = SessionManager.getJSPRootURL();
        int rootURLIndex = requestURI.indexOf(rootURL);
        if (rootURLIndex != -1) {
            String redirectTo = requestURI.substring(rootURLIndex + rootURL.length());
            if ((redirectTo.equals("")) || (redirectTo.equals("/"))) {
                redirectTo = SessionManager.getStartupPage();
            }

            return redirectTo;
        } else {
            //
            //Unfortunately, we couldn't find the root URL, try the old way
            //
            String appName = SessionManager.getApplicationName();
            String appNameClass = appName + ".class";
            String currentToken = null;
            StringBuffer servletName = new StringBuffer();
            boolean isServletNameFound = false;

            if (requestURI == null) {
                return null;
            }
            // Split the URI based on "/" and look for first occurrence of
            // application name (either variance of that name)
            java.util.StringTokenizer tok = new java.util.StringTokenizer(requestURI, "/");
            while (tok.hasMoreTokens()) {
                currentToken = tok.nextToken();

                if (currentToken.equals(appName) || currentToken.equals(appNameClass)) {
                    isServletNameFound = true;
                    // Build servlet name from remaining tokens
                    while (tok.hasMoreTokens()) {
                        servletName.append("/" + tok.nextToken());
                    }
                }

            }

            // Return appropriate servlet name
            if (isServletNameFound) {

                if (servletName.length() == 0) {
                    // We found an empty servlet name; must be wanting the startup
                    // page
                    return SessionManager.getStartupPage();
                } else {
                    // We found an actual servlet name
                    return servletName.toString();
                }

            } else {
                // No servlet name found, return original request
                return requestURI;
            }
        }
    }

    /**
     * This method constructs a parameter string based on all the parameters that
     * were received as part of the original request.  This method returns all parameters
     * set by an HTTP GET or POST, whether in the request URL, form input fields, or
     * hidden input parameters.
     *
     * @param request a <code>HttpServletRequest</code> object that we are going
     * to fetch parameters from.
     * @return a <code>String</code> value which contains all the parameters and
     * values delimited by ampersands.
     */
    public static String getRedirectParameterString(HttpServletRequest request) {
        StringBuffer parameterString = new StringBuffer();

        // For each parameter name
        for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
            String parameterName = (String)enumeration.nextElement();
            String[] parameterValues = request.getParameterValues(parameterName);

            // For each parameter value
            // Iterate through all the parameter values, just in case a single
            // parameter has more than one value.
            for (int i = 0; i < parameterValues.length; i++) {
                parameterString.append(parameterName);
                parameterString.append("=");
                try {
                    // try to URL encode the parameter value
                    parameterString.append(URLEncoder.encode(parameterValues[i],
                        SessionManager.getCharacterEncoding()));
                } catch (UnsupportedEncodingException e) {
                    parameterString.append(parameterValues[i]);
                }
                // If there is another parameter value, add an ampersand
                if (i + 1 < parameterValues.length) {
                    parameterString.append("&");
                }
            }

            if (enumeration.hasMoreElements()) {
                parameterString.append("&");
            }
        }

        return parameterString.toString();
    }

    /**
     * Returns the value for the specified cookie name.
     * @param request the request from which to get the cookie
     * @param name the name of the cookie who's value to get
     * @return the value of the cookie or null if no cookie was found with that name
     * @throws NullPointerException if request or name are null
     */
    public static String getCookieValue(HttpServletRequest request, String name) {

        if (request == null || name == null) {
            throw new NullPointerException("request and name are required");
        }

        String value = null;

        Cookie[] cookies = request.getCookies();
        //In BEA, this can be null, so we have to test for it.
        cookies = (cookies == null ? new Cookie[] {} : cookies);

        for (Iterator it = Arrays.asList(cookies).iterator(); it.hasNext();) {
            Cookie nextCookie = (Cookie) it.next();
            if (nextCookie.getName().equals(name)) {
                value = nextCookie.getValue();
                break;
            }
        }

        return value;
    }

}
