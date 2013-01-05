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

 package net.project.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;

/**
 * Ensures that any initialization has been performed prior to accessing any resource.
 * <p>
 * Initialzes the system settings and the session by loading properties and creating a user object.
 * </p>
 * <p>
 * This Filter is only required for resources that use system settings or require access to the session.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class SessionAccessFilter implements Filter {

    public void init(FilterConfig filterConfig) throws javax.servlet.ServletException {
        // Do nothing; we might save the filterConfig we we needed servlet context
    }
    
    public void destroy() {
        // Do nothing
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, javax.servlet.ServletException {

        if (request instanceof HttpServletRequest) {
            //Get the page we are filtering.  If the page is an include page,
            //it will be in the javax.servlet.include.request_uri attribute
            //instead of the getServletPath().
            String filteredPage = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (filteredPage == null) {
                filteredPage = ((HttpServletRequest)request).getServletPath();
            }
            
            Logger.getLogger(SessionAccessFilter.class).debug("checking session action " + filteredPage);

            // Grab the session, creating one if necessary
            HttpSession session = ((HttpServletRequest) request).getSession();
            
            // Save the session in ThreadLocal
            // This allows "back-door" session access from SessionManager
            ((LocalSessionProvider) Compatibility.getSessionProvider()).setLocalSession(session);

            // Initialize the properties, if necessary
            initializeProperties((HttpServletRequest) request, session);

            // Add in current servlet path
            session.removeAttribute("servletPath");
            session.setAttribute("servletPath", filteredPage);
        }

        // Execute next item in chain
        chain.doFilter(request, response);

    }

    /**
     * Initializes the session by loading Properties, if necessary.
     * After calling, a <code>User</code> will be available in the session.
     * Properties will be loaded.
     * @param request the request required for determining the current configuration
     * @param session the session to initialize
     */
    public static void initializeProperties(HttpServletRequest request, HttpSession session) {

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
