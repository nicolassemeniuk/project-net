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
package net.project.admin.mvc;

import javax.servlet.http.HttpServletRequest;

import net.project.admin.mvc.handler.DocumentAdminHandler;
import net.project.admin.mvc.handler.DocumentRootUpdateHandler;
import net.project.admin.mvc.handler.SettingsHandler;
import net.project.admin.mvc.handler.SettingsUpdateHandler;
import net.project.base.mvc.Handler;
import net.project.util.Validator;

public class AdminHandlerMapping {
    /**
     * Get a handler that corresponds to the request that the user has made.
     *
     * @param request a <code>HttpServletRequest</code> which contains the
     * information passed to the ScheduleController.
     * @return a <code>Handler</code> object which can handle the request that
     * the user has made.
     * @throws java.lang.RuntimeException if there isn't a handler that corresponds to the
     * requested path info.
     */
    public static Handler getHandler(HttpServletRequest request) throws RuntimeException {
        //Get the path information minus the first slash.
        String pathInfo = request.getPathInfo();
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("handlerName");
        }
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("theAction");
        }

        if (pathInfo.equalsIgnoreCase("/DocumentAdmin")) {
            return new DocumentAdminHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/DocumentRootUpdate")) {
            return new DocumentRootUpdateHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/Settings")) {
            return new SettingsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/SettingsUpdate")) {
            return new SettingsUpdateHandler(request);
        } else {
            throw new RuntimeException("Unrecognized schedule module: " +pathInfo);
        }
    }
}
