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
package net.project.taglibs;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Utility methods for taglibs.
 */
public class TaglibUtils {

    /**
     * Returns the appropriate scope integer based on the specified string.
     * @param scope one of <code>page, request, session, application</code>
     * @return the corresponding integer scope; defaults to PAGE_SCOPE if
     * passed scope is null
     * @throws javax.servlet.jsp.JspException if the scope is invalid
     * @see javax.servlet.jsp.PageContext
     */
    public static int getPageContextScope(String scope) throws JspException {

        int beanScope = PageContext.PAGE_SCOPE;

        if (scope != null) {
            
            if (scope.equals("page")) {
                beanScope = PageContext.PAGE_SCOPE;
            
            } else if (scope.equals("request")) {
                beanScope = PageContext.REQUEST_SCOPE;
            
            } else if (scope.equals("session")) {
                beanScope = PageContext.SESSION_SCOPE;
            
            } else if (scope.equals("application")) {
                beanScope = PageContext.APPLICATION_SCOPE;
            
            } else {
                throw new IllegalArgumentException("Invalid scope '" + scope + 
                        "'.  Must be one of application, session, request, page.");
            }
        }

        return beanScope;
    }

}
