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
|   $Revision: 19757 $
|       $Date: 2009-08-16 06:23:23 -0300 (dom, 16 ago 2009) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.security.SessionManager;

/**
 * Renders a view of a model using a JSP page by redirecting to the page.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class RedirectingJSPView extends AbstractJSPView {

    /** Query sting to add to URL when redirecting. */
    private final String queryString;

    /**
     * Create a new JSP view that redirects to the JSP page.
     * @param pathToJSP the path to the JSP page; it should start with a <code>/</code>
     * and contain a jsp page name; for example <code>/schedule/Main.jsp</code>
     * @param queryString the query string to add to the URL
     */
    public RedirectingJSPView(String pathToJSP, String queryString) {
        super(pathToJSP);
        this.queryString = queryString;
    }

    /**
     * Renders this view by redirecting to the JSP page.
     * <p>
     * <b>Note:</b> Model is ignored since we are redirecting; the request
     * parameters are set by the query string specified in the constructor.
     * </p>
     * @param model ignored
     * @param request a <code>HttpServletRequest</code> object which contains
     * the information passed to the controller
     * @param response a <code>HttpServletResponse</code> object which is the
     * context for our response.
     * @throws Exception if any error occurs.
     */
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=UTF-8");

        response.sendRedirect(SessionManager.getJSPRootURL() + getJSPPath() + "?" + queryString);
    }

}
