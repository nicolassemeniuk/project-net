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

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.persistence.PersistenceException;
import net.project.security.AuthorizationFailedException;

import org.apache.log4j.Logger;

/**
 * A handler that returns a selection of 100 tasks for a particular person.
 * 
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class TaskSelectInit extends Handler implements IGatewayHandler {

    private static final Logger logger = Logger.getLogger(TaskSelectInit.class);

    /** The current servlet context used for storing cached data. */
    private final ServletContext context;

    private IView view = new MyAssignmentsView();

    public TaskSelectInit(HttpServletRequest request, ServletContext servletContext) {
        super(request);
        this.context = servletContext;
    }

    public String getViewName() {
        return null;
    }

    public IView getView() {
        return this.view;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
            throws AuthorizationFailedException, PnetException {
        // Always succeed
    }

    /**
     * @param request  
     * @param response 
     * @return a <code>content</code> element containing XML content
     * @throws IOException      
     * @throws ServletException 
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Map model = new HashMap();

        try {
            // Determine a personID
            String personID = PersonProvider.getPersonProvider(this.context).checkOutPersonID();
            model.put("personID", personID);

            if (logger.isDebugEnabled()) {
                logger.debug("Checked out Person ID: " + personID);
            }
        } catch (PersistenceException e) {
            throw new ServletException("Error getting person ID pool.");

        }

        return model;
    }

    /**
     * Provides an XML view of assignments.
     */
    private static class MyAssignmentsView implements IView {

        /**
         * Renders the XML content.
         *
         * @param model
         * @param request
         * @param response
         * @throws java.io.IOException if there is a problem writing the content
         */
        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException {
            String content = (String)model.get("personID");
            response.setContentType("text/plain");
            response.setContentLength(content.length());

            // Now write the XML to the response
            Writer writer = new BufferedWriter(response.getWriter());
            writer.write(content);
            writer.flush();
        }
    }
}
