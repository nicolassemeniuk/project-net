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
/**
 * 
 */
package net.project.hibernate.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;
import net.project.security.SessionAccessFilter;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.spring.TapestrySpringFilter;

public class CustomTapestryFilter implements Filter{
	TapestrySpringFilter tapestryFilter = new TapestrySpringFilter();

    public void init(FilterConfig filterConfig) throws ServletException {
    	tapestryFilter.init(filterConfig);
    }

    public void doFilter(ServletRequest request, ServletResponse response,
			 FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getServletPath();        
        
        
        // intialized user in session for wiki module... TODO: Make some helper for such things later
        if (request instanceof HttpServletRequest && uri.startsWith("/wiki")) {
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
            SessionAccessFilter.initializeProperties((HttpServletRequest) request, session);

            // Add in current servlet path
            session.removeAttribute("servletPath");
            session.setAttribute("servletPath", filteredPage);
        }
        
        if(uri.contains(".htm")) {
        	chain.doFilter(request, response);
        } else {
        	try{
        		tapestryFilter.doFilter(request, response, chain);        		
        	}catch (Exception e) {
        		if(!(e instanceof NullPointerException)){
				// if tapestry mapping is not available, let the chain handle it (for any errors)
        			chain.doFilter(request, response);
        		}
			}
        }
    }
    
    //Added for ingoring dwr method calls
    public static void contributeIgnoredPathsFilter(Configuration<String> configuration)
    {
      configuration.add("/dwr/.*");
    }
    
    public void destroy() {
    	tapestryFilter.destroy();
    }  

} 
