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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.api.model.Stats;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.security.AuthorizationFailedException;

/**
 * A handler that displays server-side statistics.
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class StatisticsHandler extends Handler implements IGatewayHandler {

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    public StatisticsHandler(HttpServletRequest request, ServletContext servletContext) {
        super(request);
        this.context = servletContext;
    }

    public String getViewName() {
        return null;
    }

    public IView getView() {
        return new StatisticsView();
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
            throws AuthorizationFailedException, PnetException {

        // Always succeed

    }

    /**
     * @param request
     * @param response 
     * @return request
     * @throws IOException      
     * @throws ServletException 
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Map model = new HashMap();

        model.put("stats", Stats.get(this.context));

        return model;
    }

    /**
     * OK Response.
     */
    private static class StatisticsView implements IView {

        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException {

            Stats stats = (Stats) model.get("stats");

            String message = "Average Update: " + stats.getUpdateAverage() + "\n" +
                    "Average Select: " + stats.getSelectAverage() + " \n";

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setContentLength(message.length());

            Writer writer = new BufferedWriter(response.getWriter());
            writer.write(message);
            writer.flush();
        }
    }

}
