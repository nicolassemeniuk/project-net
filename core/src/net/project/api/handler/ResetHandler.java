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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.StatusView;
import net.project.api.model.ScheduleCache;
import net.project.api.model.ScheduleEntryCache;
import net.project.api.model.Stats;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.persistence.PersistenceException;
import net.project.security.AuthorizationFailedException;

import org.apache.log4j.Logger;

/**
 * A handler that resets the test.
 * <p/>
 * All checked-out person IDs are returned to the pool.
 * </p>
 * 
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class ResetHandler extends Handler implements IGatewayHandler {

    private static final Logger logger = Logger.getLogger(ResetHandler.class);

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    public ResetHandler(HttpServletRequest request, ServletContext servletContext) {
        super(request);
        this.context = servletContext;
    }

    public String getViewName() {
        return null;
    }

    public IView getView() {
        return StatusView.OK;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
            throws AuthorizationFailedException, PnetException {

        // Always succeed

    }

    /**
     * Resets the person ID pool.
     * 
     * @param request  
     * @param response 
     * @return request
     * @throws IOException      
     * @throws ServletException 
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            // Reset the person ID cache
            logger.info("Resetting PersonProvider");
            PersonProvider.getPersonProvider(this.context).reset();

            // Clear out cached Schedule objects
            logger.info("Resetting ScheduleCache");
            ScheduleCache.get(this.context).clear();

            // Clear our cached ScheduleEntry objects
            logger.info("Resetting ScheduleEntryCache");
            ScheduleEntryCache.get(this.context).clear();

            // Clear out average times
            logger.info("Resetting Statistics");
            Stats.get(this.context).clear();

        } catch (PersistenceException e) {
            throw new ServletException("Error resetting");

        }

        return Collections.EMPTY_MAP;
    }

}
