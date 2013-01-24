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

import net.project.security.ServletSecurityProvider;
import net.project.security.SessionManager;

/**
 * Renders a view of a model using a JSP page by forwarding to the JSP page.
 * <p>
 * The variables in the model will be passed to the JSP page as request attributes.
 * </p>
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class JSPView extends AbstractJSPView {

    /**
     * Create a new JSP view that forwards to the JSP page.
     * @param pathToJSP the path to the JSP page; it should start with a <code>/</code>
     * and contain a jsp page name; for example <code>/schedule/Main.jsp</code>
     */
    public JSPView(String pathToJSP) {
        super(pathToJSP);
    }

    public JSPView(String pathToJSP, boolean isRedirect) {
        super(pathToJSP);
        setRedirect(isRedirect);
    }
    /**
     * Renders this view by either including or forwarding to the JSP page.
     * <p>
     * Copies all the entires in <code>model</code> to the request as attributes. <br>
     * After calling, an include or forward will have been performed using the RequestDispatcher.
     * </p>
     * <p>
     * An include is performed if the response is already committed.  A forward is performed
     * if the response is not already committed.
     * </p>
     *
     * @param model a <code>Map</code> containing the request attributes; each
     * key must be a <code>String<code> representing a request attribute name
     * and each value is the request attribute value.
     * @param request a <code>HttpServletRequest</code> object which contains
     * the information passed to the controller; the model entries are added
     * as attributes
     * @param response a <code>HttpServletResponse</code> object which is the
     * context for our response.
     * @throws Exception if any error occurs.
     */
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        addModelToRequest(model, request);
        response.setContentType("text/html; charset=UTF-8");
        //This is our attempt to see if we are including a page or forwarding to
        //it.  If there has already been data sent to the page (that is, data
        //has been committed), there must already be a page in process.
        if (response.isCommitted()) {
            request.getRequestDispatcher(getJSPPath()).include(request, response);
        } else {
/*Avinash:-------setting request values to SecurityProvider and checking 
        	for violations if any --------*/
        	ServletSecurityProvider.setAndCheckValues(request);
//Avinash:------------------------------------------------------------------------        	
        	if(isRedirect())
        		response.sendRedirect(SessionManager.getJSPRootURL() + getJSPPath());
        	else
        		request.getRequestDispatcher(getJSPPath()).forward(request, response);
        }
    }


}
