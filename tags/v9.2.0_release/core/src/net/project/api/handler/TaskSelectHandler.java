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
package net.project.api.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.XMLView;
import net.project.api.model.Stats;
import net.project.base.mvc.IView;
import net.project.persistence.PersistenceException;
import net.project.schedule.TaskFinder;
import net.project.schedule.filters.UserIDFilter;

import org.apache.log4j.Logger;

/**
 * Return all of the tasks for a given user id.
 *
 * @author Matthew Flower
 * @since Version 7.6.4
 */
public class TaskSelectHandler implements IGatewayHandler {

    private static final Logger logger = Logger.getLogger(TaskSelectHandler.class);

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    public TaskSelectHandler(HttpServletRequest request, ServletContext servletContext) {
        this.context = servletContext;
    }

    public IView getView() {
        return new TaskSelectView();
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map model = new HashMap();

        String userID = request.getParameter("userID");

        logger.info("Begin");
        long start = System.currentTimeMillis();

        try {
            TaskFinder finder = new TaskFinder();
            finder.setMaximumRowsToFetch(200);
            finder.addFinderFilter(new UserIDFilter("1", true, userID));
            model.put("foundTasksList", finder.findAll());
        } catch (PersistenceException e) {
            logger.debug("Unable to findAll()", e);

        } finally {
            long end = System.currentTimeMillis() - start;

            // Log the elapsed time with the statistics
            Stats.get(this.context).logSelect(end);

            logger.info("End. Total Execution time: " + end);

        }

        return model;
    }

    private class TaskSelectView extends XMLView {
        /**
         * Returns the XML content as a string.
         *
         * @return the XML content
         */
        protected String getContent(Map model) {
            return TaskApiXML.createXML((List) model.get("foundTasksList"));
        }
    }
}
