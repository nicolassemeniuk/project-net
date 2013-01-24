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

 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
  +----------------------------------------------------------------------*/
package net.project.base.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.security.ServletSecurityProvider;


/**
 * Provides logic to initialize the session, check for authentication and
 * invoke security.
 * <p>
 * These services are necessary since Bluestone does not invoke <code>ServletSecurity</code>
 * for servlets.  This will not be necessary if a ServletFilter is used for
 * security checks.
 * </p>
 * <p><b>Note</b>: The security check READS FROM THE REQUEST.  This servlet
 * cannot be the superclass of any servlet that processes multipart/form-data inputstreams.
 * </p>
 * @since Gecko 4
 * @author Tim
 */
public class BaseSecurityServlet extends HttpServlet {

    /**
     * Invokes the security provider.
     * <p>
     * If a security exception occurs, forwards to the standard error page for 
     * handling; in that case, the superclass <code>service()</code> method
     * is <b>not</b> invoked.  The security exception may be a <code>SessionNotInitializedException</code>,
     * an <code>AuthenticationRequiredException</code> or an <code>AuthorizationFailedException</code>.
     * The exception is saved in the request by the name given by <code>javax.servlet.jsp.PageContext.EXCEPTION</code>.
     * <br>
     * If no security exception occurs, the superclass <code>service()</code>
     * method is invoked.  No changes are made to the request or response.
     * <br>
     * This method handles any exceptions or throwables by forwarding to the
     * appropriate resource.  See {@link #handleException} and {@link #handleThrowable} for details.
     * </p>
     * <p>
     * <b>Note</b>: The request is read; thus the inputstream cannot be processed
     * successfully after this method executes.
     * </p>
     * @param request the request
     * @param response the response
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws java.io.IOException, javax.servlet.ServletException {


        try {
            boolean isSecurityError = false;

            try {
                ServletSecurityProvider servletSecurityProvider = new ServletSecurityProvider();
                servletSecurityProvider.allowAccess(request);

            } catch (SecurityException e) {
                // Bluestone's security exceptions are subclasses of java.lang.SecurityException
                isSecurityError = true;

                // Setup the forward for the exception
                handleException(request, response, e);
            }

            // Only precede with the servlet if there was no security error
            // If there was a security error, the errors page will handle it
            if (!isSecurityError) {
                super.service(request, response);
            }

        } catch (Exception e) {
            handleException(request, response, e);

        } catch (Throwable t) {
            handleThrowable(request, response, t);

        }

    }

    /**
     * Returns the default error resource to forward to when an exception occurs.
     * <p>
     * Sub-classes may override this to force errors to be handled by a different
     * resource.
     * </p>
     * @return the default error resource, currently <code>/errors.jsp</code>
     */
    protected String getErrorResource() {
        return "/errors.jsp";
    }

    /**
     * Handles an exception in the same way as a JSP page.
     * <p>
     * Forwards to the resource specified by {@link #getErrorResource}.
     * Sub-classes should call this from within <code>doGet</code>, <code>doPost</code>,
     * or <code>service</code> to handle exceptions.
     * </p>
     * @param request the request
     * @param response the response
     * @param e the exception that occurred
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws java.io.IOException, javax.servlet.ServletException {

        request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
        request.getRequestDispatcher(getErrorResource()).forward(request, response);

    }

    /**
     * Handles a throwable by converting to an exception and forwarding to
     * the errors page.
     * <p>
     * Equivalent to calling <code>handleException(request, response, new Exception(t.getMessage(), t);</code>.
     * Typically sub-classes do not need to call this method since throwables will
     * be handled by the {@link #service} method automatically.
     * </p>
     * @param request the request
     * @param response the response
     * @param t the throwable that occurred
     * @see #handleException
     */
    protected void handleThrowable(HttpServletRequest request, HttpServletResponse response, Throwable t)
            throws java.io.IOException, javax.servlet.ServletException {

        handleException(request, response, new Exception(t.getMessage(), t));
    }

}
