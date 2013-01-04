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


package net.project.taglibs.template;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;


public class GetDoctypeTag extends TagSupport {
    private static final String DOCTYPE_DECLARATION = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";

    /* -------------------------------  Constructors  ------------------------------- */

    public GetDoctypeTag() {
        super();
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {
        JspWriter out = pageContext.getOut();
        try {
            out.print(DOCTYPE_DECLARATION);
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        }
        return SKIP_BODY;
    }
}
