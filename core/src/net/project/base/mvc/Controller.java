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

 package net.project.base.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.security.ServletSecurityProvider;

import org.apache.log4j.Logger;

/**
 * A <code>Controller</code> is a servlet that checks security then
 * passes control to <code>controlRequest</code>.
 *
 * @author Mattew Flower
 * @since Version 7.6
 */
public abstract class Controller extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Controller.class);

    /**
     * Returns the default path to the error page.
     * @return the path to the error page, e.g. <code>/errors.jsp</code>
     */
    protected String getErrorPage() {
        return "/errors.jsp";
    }

    /**
     * Handles the specified exception by forwarding to the error page.
     * @param request the request
     * @param response the response
     * @param e the exception to handle
     * @throws ServletException
     * @throws IOException
     * @see #getErrorPage
     */
    protected void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {

        if (LOGGER.isDebugEnabled() && e instanceof RuntimeException) {
            LOGGER.debug("Error in controller", e);            
        }

        // Save the exception for the errors page to get
        request.setAttribute(javax.servlet.jsp.PageContext.EXCEPTION, e);
        request.getRequestDispatcher(getErrorPage()).forward(request, response);
    }

    /**
     * This method emulates the functionality found in the
     * {@link net.project.base.servlet.BaseSecurityServlet} servlet.  It is
     * duplicated here because the BaseSecurityServlet calls super(req, res)
     * which for some reason writes to the output.  When it does this, it
     * prevents us from forwarding.
     *
     * @param request
     * @param response
     */
    protected void checkSecurity(HttpServletRequest request, HttpServletResponse response) {
        ServletSecurityProvider servletSecurityProvider = new ServletSecurityProvider();
        servletSecurityProvider.allowAccess(request);
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            checkSecurity(request, response);
            controlRequest(request, response);
        } catch (Exception e) {
            handleError(request, response, e);                                                  
        } catch (Throwable t) {
            handleError(request, response, (Exception) new Exception(t.getMessage()).initCause(t));
        }
    }

    /**
     * Sub-classes should implement this method to handle the request and
     * provide the appropriate view.
     * @param request
     * @param response
     * @throws Exception
     */
    public abstract void controlRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
