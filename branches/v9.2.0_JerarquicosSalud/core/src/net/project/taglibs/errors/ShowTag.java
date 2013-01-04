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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.util.ErrorReporter;
import net.project.xml.XMLFormatException;
import net.project.xml.XMLFormatter;

/**
 * Taglib to show any errors that have been
 */
public class ShowTag extends TagSupport {
    private String stylesheet = "/base/xsl/error-report.xsl";
    private boolean clearAfterDisplay = true;
    private String beanName = "errorReporter";
    private String scope = "session";

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isClearAfterDisplay() {
        return clearAfterDisplay;
    }

    public void setClearAfterDisplay(boolean clearAfterDisplay) {
        this.clearAfterDisplay = clearAfterDisplay;
    }

    public int doEndTag() throws JspException {
        //Get the ErrorReporter object
        ErrorReporter errors = getErrorReporter();

        if (errors != null && (errors.errorsFound() || errors.warningsFound())) {
            XMLFormatter formatter = new XMLFormatter();
            formatter.setStylesheet(stylesheet);
            formatter.setXML(errors.getXML());

            try {
                pageContext.getOut().write(formatter.getPresentation());
            } catch (IOException e) {
                throw new JspException("Unexpected IOException in <errors:show>.  "+e);
            } catch (XMLFormatException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }

            if (clearAfterDisplay) {
                errors.clear();
            }
        }

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
