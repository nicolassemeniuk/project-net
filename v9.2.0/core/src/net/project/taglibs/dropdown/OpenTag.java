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
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

/**
 * This class contains the HTML that will be displayed when the DropDown is
 * expanded.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class OpenTag extends SectionTag {
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspException {
        super.doInitBody();

        try {
            JspWriter out = getBodyContent().getEnclosingWriter();
            out.write("<div id=\""+ getDivId() +"Open\" class=\""+(isOpen()?"visible":"hidden")+"\">");
        } catch (IOException e) {
            throw new JspException(e.toString());
        }
    }

    public int doAfterBody() throws JspException {
        try {
            JspWriter out = getPreviousOut();

            //Write out the JSP that was processed with this tag.
            out.write(getBodyContent().getString());

            //Close the div tag we opened in doInitBody().
            out.write("</div>");
        } catch (IOException e) {
            throw new JspException(e.toString());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    boolean isOpen() {
        Tag parent = getParent();

        if (!(parent instanceof DropDownTag)) {
            throw new RuntimeException("OpenTag objects can only be embedded in DropDownTag objects");
        }

        return !((DropDownTag)parent).isClosed();
    }
}
