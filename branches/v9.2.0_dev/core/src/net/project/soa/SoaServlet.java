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
package net.project.soa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;

import org.codehaus.xfire.transport.http.XFireConfigurableServlet;

public class SoaServlet extends XFireConfigurableServlet {

	/* (non-Javadoc)
	 * @see org.codehaus.xfire.transport.http.XFireServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		initSecuritySession(request, response);
		super.doGet(request, response);
	}

	/* (non-Javadoc)
	 * @see org.codehaus.xfire.transport.http.XFireServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		initSecuritySession(request, response);
		super.doPost(request, response);
	}

	protected void initSecuritySession(HttpServletRequest request, HttpServletResponse response){
        // Grab the session, creating one if necessary
        HttpSession session = ((HttpServletRequest) request).getSession();
        
        // Save the session in ThreadLocal
        // This allows "back-door" session access from SessionManager
        ((LocalSessionProvider) Compatibility.getSessionProvider()).setLocalSession(session);

        // Initialize the properties, if necessary
        initializeProperties((HttpServletRequest) request, session);
	}
	
    /**
     * Initializes the session by loading Properties, if necessary.
     * After calling, a <code>User</code> will be available in the session.
     * Properties will be loaded.
     * @param request the request required for determining the current configuration
     * @param session the session to initialize
     */
    private void initializeProperties(HttpServletRequest request, HttpSession session) {

        if (!net.project.base.property.PropertyProvider.isLoaded()) {

            // Create a user object and store it in the session
            // It is needed
            net.project.security.User user = new net.project.security.User();
            session.setAttribute("user", user);

            try {
                // Initialize the properties
                net.project.base.property.PropertyProvider.setContextFromRequest (request, session.getServletContext());
                if (!net.project.base.property.PropertyProvider.isLoaded()) {
                    // Can't load properties, we have to abort initialization
                    throw new RuntimeException("Error initializing application.  Properties could not be loaded.");
                }
            } catch (net.project.base.property.PropertyException e) {
                throw new RuntimeException("Error initializing application.  Error loading properties: " + e, e);

            }

        }

    }
}
