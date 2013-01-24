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
package net.project.taglibs.dropdown;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.security.SessionManager;

public class DropDownTag extends BodyTagSupport {
    private boolean closed = true;
    private boolean showClosedContentWhenOpen = true;
    private String divId = "dd";

    /**
     * Called prior to evaluating the body of this tag.
     * @return an <code>int</code> value
     * @exception javax.servlet.jsp.JspTagException if an error occurs
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspException {
        super.doInitBody();

        try {
            //This is the writer that we need to use to write out HTML.
            JspWriter out = getBodyContent().getEnclosingWriter();
            out.write("<script language=\"javascript\" src=\""+SessionManager.getJSPRootURL()+"/src/dropdown.js\"></script>");
            out.write("<style>");
            out.write(".visible { visibility: visible; }");
            out.write(".hidden { display: none; }");
            out.write("</style>");

        } catch (IOException e) {
            throw new JspTagException(e.toString());
        }
    }

    public int doAfterBody() throws JspException {
        try {
            getPreviousOut().write(getBodyContent().getString());
        } catch (IOException e) {
            throw new JspException("Unable to write body content of closed tag to output.  "+e.toString());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isShowClosedContentWhenOpen() {
        return showClosedContentWhenOpen;
    }

    public void setShowClosedContentWhenOpen(boolean showClosedContentWhenOpen) {
        this.showClosedContentWhenOpen = showClosedContentWhenOpen;
    }

    public String getDivId() {
        return divId;
    }

    public void setDivId(String divId) {
        this.divId = divId;
    }
    
    
}
