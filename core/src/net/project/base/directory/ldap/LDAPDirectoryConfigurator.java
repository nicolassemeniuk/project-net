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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|     $Author: andrewr $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import net.project.admin.RegistrationBean;
import net.project.security.domain.UserDomain;

/**
 * Launches configuration screens for LDAP Directory Configuration.
 */
public class LDAPDirectoryConfigurator extends net.project.base.directory.DirectoryConfigurator {


    public void serviceDirectoryConfiguration(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, UserDomain domain) 
            throws ServletException, IOException {

        JspWriter out = pageContext.getOut();

        try {
            // Ensure current domain is in session
            pageContext.setAttribute("domain", domain, PageContext.SESSION_SCOPE);

            // Forward to appropriate configuration page
            pageContext.forward("/admin/domain/LDAPDirectoryConfigurationEdit.jsp");

        } finally {
            out.flush();
        
        }


    }


    public void serviceRegistrationAuthorization(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, RegistrationBean registration)
            throws ServletException, IOException {

        JspWriter out = pageContext.getOut();

        try {
            // Ensure current registration bean is in session
            pageContext.setAttribute("registration", registration, PageContext.SESSION_SCOPE);

            // Forward to appropriate configuration page
            pageContext.forward("/registration/LDAPDirectoryAuthorizationController.jsp");

        } finally {
            out.flush();
        
        }


    }

    
    public void serviceProfileUpdate(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, RegistrationBean registration)
            throws ServletException, IOException {

        JspWriter out = pageContext.getOut();

        try {
            // Ensure current registration bean is in session
            pageContext.setAttribute("registration", registration, PageContext.SESSION_SCOPE);

            // Forward to appropriate profile page
            pageContext.forward("/personal/LDAPProfileLoginController.jsp");

        } finally {
            out.flush();
        
        }


    }


    public void serviceProfileUpdateAdmin(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, RegistrationBean registration)
            throws ServletException, IOException {

        JspWriter out = pageContext.getOut();

        try {
            // Ensure current registration bean is in session
            pageContext.setAttribute("registration", registration, PageContext.SESSION_SCOPE);

            // Forward to appropriate profile page
            pageContext.forward("/admin/profile/LDAPProfileLoginController.jsp");

        } finally {
            out.flush();
        
        }


    }


    public void serviceMigrationAuthorization(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, RegistrationBean registration)
        throws ServletException, IOException {

        JspWriter out = pageContext.getOut();

        try {
            // Ensure current registration bean is in session
            pageContext.setAttribute("registration", registration, PageContext.SESSION_SCOPE);

            // Forward to appropriate configuration page
            pageContext.forward("/domain/LDAPDirectoryAuthorizationController.jsp?module="+net.project.base.Module.PERSONAL_SPACE);

        } finally {
            out.flush();

        }

    }    


    public void serviceForgottenInfo(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.jsp.PageContext pageContext)
            throws javax.servlet.ServletException, java.io.IOException {

        JspWriter out = pageContext.getOut();

        try {

            // Forward to appropriate configuration page
            pageContext.forward("/registration/ldap/ForgottenInfoController.jsp");

        } finally {
            out.flush();

        }

    }


}
