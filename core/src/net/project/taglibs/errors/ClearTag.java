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
package net.project.taglibs.errors;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.util.ErrorReporter;

/**
 * Clear all of the errors and parameters out of the error reporter.
 *
 * @author Matthew Flower
 * @since Version 7.7.1
 */
public class ClearTag extends TagSupport {
    private String beanName = "errorReporter";
    private String scope = "session";

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int doEndTag() throws JspException {
        ErrorReporter errorReporter = getErrorReporter();
        errorReporter.clear();
        return EVAL_PAGE;
    }

    private ErrorReporter getErrorReporter() throws JspTagException {
        int errorReporterObjectScope = PageContext.SESSION_SCOPE;

        if (scope != null) {
            if (scope.equals("page")) {
                errorReporterObjectScope = PageContext.PAGE_SCOPE;
            } else if (scope.equals("request")) {
                errorReporterObjectScope = PageContext.REQUEST_SCOPE;
            } else if (scope.equals("session")) {
                errorReporterObjectScope = PageContext.SESSION_SCOPE;
            } else if (scope.equals("application")) {
                errorReporterObjectScope = PageContext.APPLICATION_SCOPE;
            } else {
                throw new JspTagException("Invalid scope in <errors;show> tag.");
            }
        }

        Object errorReporter = pageContext.getAttribute(beanName, errorReporterObjectScope);
        return (ErrorReporter)errorReporter;
    }
}
