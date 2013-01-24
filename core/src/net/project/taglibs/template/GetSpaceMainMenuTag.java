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

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;


public class GetSpaceMainMenuTag extends TagSupport {

    private static final String INCLUDE_PATH = "/toolbar/include/Main.jsp";
    private static final String INCLUDE_PATH_CUSTOM_HTML = "/custom/page.html";
    private static final String INCLUDE_PATH_CUSTOM_JSP = "/custom/page.jsp";

    /* -------------------------------  Constructors  ------------------------------- */

    public GetSpaceMainMenuTag() {
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {
        JspWriter out = pageContext.getOut();
        try {
            //check for custom menu
            String custom = PropertyProvider.get("prm.custom.topmenu.content");
            if ((custom != null)&&(custom.equalsIgnoreCase("jsp"))) {
            	//custom jsp
            	out.println("<div id=\"topframe\">\n");
            	pageContext.include(INCLUDE_PATH_CUSTOM_JSP);
            	out.println("</div>");
            } else if ((custom != null)&&(custom.equalsIgnoreCase("html"))) {
            	//custom html
            	out.println("<div id=\"topframe\">\n");
            	pageContext.include(INCLUDE_PATH_CUSTOM_HTML);
            	out.println("</div>");
            } else {
            	//use default menu
            	//pageContext.include(INCLUDE_PATH);
            	out.println("<script language='javascript' type='text/javascript'>writeSpaceMenu();</script>");
            }
            
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        } catch (ServletException se) {
            throw new JspTagException("Error including content in GetSpaceMainMenuTag: " + se);
        }
        return SKIP_BODY;
    }
}
