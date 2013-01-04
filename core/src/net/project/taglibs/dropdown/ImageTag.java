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
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import net.project.security.SessionManager;

public class ImageTag extends BodyTagSupport {
    private String openImage = SessionManager.getJSPRootURL()+"/images/unexpand.gif";
    private String closedImage = SessionManager.getJSPRootURL()+"/images/expand.gif";

    private boolean isOpen() {
        Tag parent = getParent().getParent();

        if (!(parent instanceof DropDownTag)) {
            throw new RuntimeException("Invalid DropDownTag structure (Grandparent of an image must be a DropDownImage)");
        }

        return !((DropDownTag)parent).isClosed();
    }

    boolean isShowClosedBlockWhenOpen() {
        Tag parent = getParent().getParent();

        if (!(parent instanceof DropDownTag)) {
            throw new RuntimeException("Invalid DropDownTag structure (Grandparent of an image must be a DropDownImage)");
        }

        return ((DropDownTag)parent).isShowClosedContentWhenOpen();
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
        try {
            SectionTag parent = (SectionTag)getParent();
            String divId = parent.getDivId();
            //This is the writer that we need to use to write out HTML.
            JspWriter out = parent.getEnclosingWriter();
            out.write("<a href=\"javascript:toggleFilterExpansion('"+divId+"filterExpandImage', '"+divId+"Open', '"+divId+"Closed', "+String.valueOf(isShowClosedBlockWhenOpen())+");\">");
            out.write("<img src=\""+(isOpen() ? openImage : closedImage)+"\" id=\""+divId+"filterExpandImage\" height=\"11\" width=\"11\" border=\"0\">");
            out.write("</a>");
        } catch (IOException e) {
            throw new JspException(e.toString());
        }


        return EVAL_PAGE;
    }

    public String getOpenImage() {
        return openImage;
    }

    public void setOpenImage(String openImage) {
        this.openImage = openImage;
    }

    public String getClosedImage() {
        return closedImage;
    }

    public void setClosedImage(String closedImage) {
        this.closedImage = closedImage;
    }
}
