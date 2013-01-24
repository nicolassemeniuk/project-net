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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.project.api.handler.IGatewayHandler;
import net.project.api.handler.ResetHandler;
import net.project.api.handler.ScheduleEntryUpdateHandler;
import net.project.api.handler.ScheduleEntryUpdateInitHandler;
import net.project.api.handler.StatisticsHandler;
import net.project.api.handler.TaskSelectHandler;
import net.project.api.handler.TaskSelectInit;
import net.project.api.handler.ThreadSynchronizer;
import net.project.base.PnetRuntimeException;

/**
 * Provides a mapping from request actions to handlers.
 */
class GatewayHandlerMapping {

    /**
     * Get a handler that corresponds to the request that the user has made.
     * 
     * @param request        a <code>HttpServletRequest</code> which contains the
     *                       information passed to the Controller.
     * @param servletContext the current servlet context
     * @return a <code>Handler</code> object which can handle the request that
     *         the user has made.
     * @throws net.project.base.PnetRuntimeException
     *          if there isn't a handler that corresponds to the
     *          requested path info.
     */
    public static IGatewayHandler getHandler(HttpServletRequest request, ServletContext servletContext) {

        //Get the path information minus the first slash.
        String pathInfo = request.getPathInfo();

        if (pathInfo.equalsIgnoreCase("/taskupdate")) {
            return new ScheduleEntryUpdateHandler(request, servletContext);

        } else if (pathInfo.equalsIgnoreCase("/taskupdateinit")) {
            return new ScheduleEntryUpdateInitHandler(request, servletContext);

        } else if (pathInfo.equalsIgnoreCase("/reset")) {
            return new ResetHandler(request, servletContext);

        } else if (pathInfo.equalsIgnoreCase("/taskselectinit")) {
            return new TaskSelectInit(request, servletContext);

        } else if (pathInfo.equalsIgnoreCase("/taskselect")) {
            return new TaskSelectHandler(request, servletContext);

        } else if (pathInfo.equalsIgnoreCase("/synchronize")) {
            return new ThreadSynchronizer();

        } else if (pathInfo.equalsIgnoreCase("/stats")) {
            return new StatisticsHandler(request, servletContext);

        } else {
            throw new PnetRuntimeException("Unrecognized gateway handler: " + pathInfo);
        }
    }
}
