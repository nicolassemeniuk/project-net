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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.api;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.handler.IGatewayHandler;
import net.project.base.mvc.Controller;
import net.project.base.mvc.ControllerException;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * API GatewayController servlet.
 * 
 * @author Tim Morrow
 * @since 7.6.4
 */
public class GatewayController extends Controller {

    /**
     * Process the request and serve up the appropriate page.
     * 
     * @param request  a <code>HttpServletRequest</code> object which contains
     *                 the parameters submitted to this page.
     * @param response a <code>HttpServletResponse</code> object which we will
     *                 use to send the response to the user.
     * @throws ControllerException if any error occurs while processing the request.
     */
    public void controlRequest(HttpServletRequest request, HttpServletResponse response)
            throws ControllerException {

        try {
            initializeUser();

            IGatewayHandler handler = GatewayHandlerMapping.getHandler(request, getServletContext());
            Map model = handler.handleRequest(request, response);

            // Return the view of the results
            handler.getView().render(model, request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ControllerException("Error in GatewayController Controller: " + e, e);
        }
    }

    private void initializeUser() {
        User user = new User();
        user.setID("1");
        user.setLogin("appadmin");
        user.setTimeZone(TimeZone.getTimeZone("PST"));
        user.setLocale(Locale.US);
        SessionManager.setUser(user);
    }

}
