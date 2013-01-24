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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory;

/**
 * Provides a presentation-level object servicing a request
 * for directory-specific presentation.
 * <p>
 * A subclass must implement the "service" operations
 * </p>
 */
public abstract class DirectoryConfigurator {

    /**
     * Creates an empty <code>DirectoryConfigurator</code>.
     */
    public DirectoryConfigurator() {
        // DO nothing
    }

    /**
     * Provides a hook for subclasses to implement a mechanism for
     * capturing the configuration parameters for a directory service.
     * A directory is configured for a particular domain.
     * <p>
     * Part of this process must pass a subclass of {@link net.project.base.directory.DirectoryConfiguration}
     * to the specified domain.
     * </p>
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param domain the Domain for which directory configuration is
     * occurring
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceDirectoryConfiguration(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.security.domain.UserDomain domain) 
            throws javax.servlet.ServletException, java.io.IOException;


    /**
     * Provides a hook for subclasses to implement a mechanism for
     * capturing registration authorization information.
     * <p>
     * This process must ensure that a username and email address
     * is provided to the specified registration bean.
     * </p>
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param registration the RegistrationBean created during the
     * registration process
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceRegistrationAuthorization(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration)
            throws javax.servlet.ServletException, java.io.IOException;


    /**
     * Provides a hook for subclasses to implement a mechanism for
     * capturing profile login information.
     * <p>
     * This process must ensure that any directory-specific information
     * is populated in the registration bean as an <code>IDirectoryEntry</code>.
     * </p>
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param registration the RegistrationBean that is the profile
     * being updated
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceProfileUpdate(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration)
            throws javax.servlet.ServletException, java.io.IOException;


    /**
     * Provides a hook for subclasses to implement a mechanism for
     * capturing profile login information in the Administration
     * space by an Application Administrator.
     * <p>
     * This process must ensure that any directory-specific information
     * is populated in the registration bean as an <code>IDirectoryEntry</code>.
     * </p>
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param registration the RegistrationBean that is the profile
     * being updated
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceProfileUpdateAdmin(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration)
            throws javax.servlet.ServletException, java.io.IOException;


    /**
     * Provides a hook for subclasses to implement a mechanism for
     * capturing domain migration authorization information.
     * <p>
     * This process must ensure that a username and email address
     * is provided to the specified registration bean.
     * </p>
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param registration the RegistrationBean created during the
     * migration process
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceMigrationAuthorization(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext, net.project.admin.RegistrationBean registration)
           throws javax.servlet.ServletException, java.io.IOException;


    /**
     * Provides a hook for subclasses to implement a mechanism for
     * providing a Forgotten Login or Password information wizard.
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param pageContext the current page context
     * @param registration the RegistrationBean that is the profile
     * being updated
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException if there is an IOException servicing
     * the configuration
     */
    public abstract void serviceForgottenInfo(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext)
            throws javax.servlet.ServletException, java.io.IOException;

}
