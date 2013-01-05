/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 15505 $
|       $Date: 2006-10-16 10:48:43 +0530 (Mon, 16 Oct 2006) $
|     $Author: anarancio $
|
+-----------------------------------------------------------------------------*/
package net.project.mockobjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

public class MockHttpServletRequest implements HttpServletRequest {
    private HashMap attributes = new HashMap();

    /**
     * Map of request parameter name to value.
     * Each key is a <code>String</code> name and each value an <code>Object</code>
     * representing that value.
     */
    private HashMap parameters = new HashMap();
    private HttpSession session = null;
    private String pathInfo = "";
    private List createdDispatchers = new ArrayList();
    private String requestURI = "";
    private String servletPath = "";
    private String queryString = "";

    public String getAuthType() {
        throw new RuntimeException("Method getAuthType() not implemented");
    }

    public String getContextPath() {
        throw new RuntimeException("Method getContextPath() not implemented");
    }

    public Cookie[] getCookies() {
        throw new RuntimeException("Method ] getCookies() not implemented");
    }

    public long getDateHeader(String postID) {
        throw new RuntimeException("Method getDateHeader(String postID) not implemented");
    }

    public String getHeader(String postID) {
        throw new RuntimeException("Method getHeader(String postID) not implemented");
    }

    public Enumeration getHeaderNames() {
        throw new RuntimeException("Method getHeaderNames() not implemented");
    }

    public Enumeration getHeaders(String postID) {
        throw new RuntimeException("Method getHeaders(String postID) not implemented");
    }

    public int getIntHeader(String postID) {
        throw new RuntimeException("Method getIntHeader(String postID) not implemented");
    }

    public String getMethod() {
        throw new RuntimeException("Method getMethod() not implemented");
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        throw new RuntimeException("Method getPathTranslated() not implemented");
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        throw new RuntimeException("Method getRemoteUser() not implemented");
    }

    public String getRequestURI() {
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        throw new RuntimeException("Method getRequestURL() not implemented");
    }

    public String getRequestedSessionId() {
        throw new RuntimeException("Method getRequestedSessionId() not implemented");
    }

    public String getServletPath() {
        return servletPath;
    }

    public HttpSession getSession() {
        return session;
    }

    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = new MockHttpSession();
        }

        return session;
    }

    public Principal getUserPrincipal() {
        throw new RuntimeException("Method getUserPrincipal() not implemented");
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new RuntimeException("Method isRequestedSessionIdFromCookie() not implemented");
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new RuntimeException("Method isRequestedSessionIdFromURL() not implemented");
    }

    /**
     * @deprecated
     */
    public boolean isRequestedSessionIdFromUrl() {
        throw new RuntimeException("Method boolean isRequestedSessionIdFromUrl() not implemented");
    }

    public boolean isRequestedSessionIdValid() {
        throw new RuntimeException("Method isRequestedSessionIdValid() not implemented");
    }

    public boolean isUserInRole(String postID) {
        throw new RuntimeException("Method isUserInRole(String postID) not implemented");
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public String getCharacterEncoding() {
        throw new RuntimeException("Method getCharacterEncoding() not implemented");
    }

    public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
        throw new RuntimeException("Method setCharacterEncoding(String) not implemented");
    }

    public int getContentLength() {
        throw new RuntimeException("Method getContentLength() not implemented");
    }

    public String getContentType() {
        throw new RuntimeException("Method getContentType() not implemented");
    }

    public ServletInputStream getInputStream() throws IOException {
        throw new RuntimeException("Method getInputStream() throws IOException not implemented");
    }

    public Locale getLocale() {
        throw new RuntimeException("Method getLocale() not implemented");
    }

    public Enumeration getLocales() {
        throw new RuntimeException("Method getLocales() not implemented");
    }

    public String getParameter(String name) {
        return (String) parameters.get(name);
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        return new String[] { (String)parameters.get(name) };
    }

    public Map getParameterMap() {
        throw new RuntimeException("Method getParameterMap() not implemented");
    }

    public String getProtocol() {
        throw new RuntimeException("Method getProtocol() not implemented");
    }

    public BufferedReader getReader() throws IOException {
        throw new RuntimeException("Method getReader() throws IOException not implemented");
    }

    /**
     * @deprecated
     */
    public String getRealPath(String postID) {
        throw new RuntimeException("Method String getRealPath(String postID) not implemented");
    }

    public String getRemoteAddr() {
        throw new RuntimeException("Method getRemoteAddr() not implemented");
    }

    public String getRemoteHost() {
        throw new RuntimeException("Method getRemoteHost() not implemented");
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        //Create a dispatcher and add it to the list of created dispatchers so
        //we can look at it after we are done processing the request.
        RequestDispatcher dispatcher = new MockRequestDispatcher(path);
        createdDispatchers.add(dispatcher);

        return dispatcher;
    }

    public String getScheme() {
        throw new RuntimeException("Method getScheme() not implemented");
    }

    public String getServerName() {
        throw new RuntimeException("Method getServerName() not implemented");
    }

    public int getServerPort() {
        throw new RuntimeException("Method getServerPort() not implemented");
    }

    public boolean isSecure() {
        throw new RuntimeException("Method isSecure() not implemented");
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }


    /*--------------------------------------------------------------------------
     *--------------------------------------------------------------------------
     * Methods below this long do not normally belong to the HttpServletRequest
     * interface and are included for testing purposes.
     *--------------------------------------------------------------------------
     *--------------------------------------------------------------------------
     */
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void addParameter(String parameterName, String parameterValue) {
        this.parameters.put(parameterName, parameterValue);
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * Return a list of dispatcher objects created by the {@link #getRequestDispatcher}
     * method.
     *
     * @return a <code>List</code> containing zero or more
     * {@link net.project.mockobjects.MockRequestDispatcher} objects.
     */
    public List getCreatedDispatchers() {
        return createdDispatchers;
    }

    public Map getAttributes() {
        return attributes;
    }

    public Map getParameters() {
        return parameters;
    }

    public String getLocalAddr() {
	// TODO Auto-generated method stub
	return null;
    }

    public String getLocalName() {
	// TODO Auto-generated method stub
	return null;
    }

    public int getLocalPort() {
	// TODO Auto-generated method stub
	return 0;
    }

    public int getRemotePort() {
	// TODO Auto-generated method stub
	return 0;
    }
}
