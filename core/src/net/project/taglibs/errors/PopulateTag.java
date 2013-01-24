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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.base.PnetRuntimeException;
import net.project.security.SessionManager;
import net.project.util.ErrorReporter;

/**
 * Populates all request items in the error reporter back into the form.
 *
 * @author Matthew Flower
 * @since Version 7.7.1
 */
public class PopulateTag extends BodyTagSupport {
    private boolean populateOnErrorOnly = true;
    private String beanName = "errorReporter";
    private String scope = "session";

    public boolean isPopulateOnErrorOnly() {
        return populateOnErrorOnly;
    }

    public void setPopulateOnErrorOnly(boolean populateOnErrorOnly) {
        this.populateOnErrorOnly = populateOnErrorOnly;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Called prior to evaluating the body of this tag.
     * @return an <code>int</code> value
     * @exception JspTagException if an error occurs
     */
    public int doStartTag() throws JspTagException {
         return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        ErrorReporter errorReporter = getErrorReporter();

        //Determine if we should be populating the fields.
        boolean showParameters = true;
        if (populateOnErrorOnly) {
            showParameters = errorReporter.errorsFound();
        }

        //Write the javascript that will populate the fields.
        if (showParameters) {
            //This is the writer that we need to use to write out HTML.
            JspWriter out = getBodyContent().getEnclosingWriter();
            try {
                //Import the library we will use to populate the field
                out.println("<script language=\"javascript\" src=\""+SessionManager.getJSPRootURL()+"/src/errorHandler.js\"></script>");
                out.println("<script language=\"javascript\">");

                Map params = errorReporter.getParameters();
                for (Iterator it = params.keySet().iterator(); it.hasNext();) {
                    String paramName = (String) it.next();
                    String paramValue = (String) params.get(paramName);

                    out.println("populateField('"+paramName+"', '"+paramValue+"');");
                }

                out.println("</script>");
            } catch (IOException e) {
                throw new PnetRuntimeException(e);
            }
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        //Prepare for reuse
        clear();
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

    private void clear() {
        populateOnErrorOnly = true;
    }
}
