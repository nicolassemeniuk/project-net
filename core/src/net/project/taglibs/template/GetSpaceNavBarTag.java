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
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.User;
import net.project.space.ISpaceTypes;
import net.project.util.Conversion;


public class GetSpaceNavBarTag extends TagSupport {

    private static final String INCLUDE_PATH_SUFFIX = "/include/NavBar.jsp";
    private String _space;
    private boolean showVertical = false;
    
    /* -------------------------------  Constructors  ------------------------------- */

    public GetSpaceNavBarTag() {
    }

    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setSpace(String space) {
        _space = space;
    }

    public void setShowVertical(String showVertical) {
    	this.showVertical = Conversion.toBoolean(showVertical);
	}
    
    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

	public int doStartTag() throws JspTagException {
        final JspWriter out = pageContext.getOut();
        try {
        	if(showVertical) // if vertikal? then include menu rendering within leftframe div
        		out.println("<div id=\"leftframe\">");
            
            //final String path = _space == null ? getIncludePathPrefix() : getIncludePathUsingSuppliedSpace();
			//pageContext.include("/" + (path) + INCLUDE_PATH_SUFFIX);
        	out.println("<script language='javascript' type='text/javascript'>writeSpaceNavBarMenu();</script>");

			if(showVertical)
				out.println("</div>");
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        }
        return SKIP_BODY;
    }

    public void release() {
        _space = null;
        super.release();
    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */

    private String getIncludePathPrefix() throws JspTagException {
        final User user = (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
        if (user == null) {
            throw new JspTagException("Error in GetSpaceNavBarTag tag: Unable to locate user in session.");
        }
        final String spaceType = user.getCurrentSpace().getType();
        if (spaceType.equals(ISpaceTypes.PERSONAL_SPACE)) {
            return "personal";
        } else if (spaceType.equals(ISpaceTypes.PROJECT_SPACE)) {
            return "project";
        } else if (spaceType.equals(ISpaceTypes.BUSINESS_SPACE)) {
            return "business";
        } else if (spaceType.equals(ISpaceTypes.FINANCIAL_SPACE)) {
            return "financial";            
        } else if (spaceType.equals(ISpaceTypes.METHODOLOGY_SPACE)) {
            return "methodology";
        } else if (spaceType.equals(ISpaceTypes.APPLICATION_SPACE)) {
            return "admin";
        } else if (spaceType.equals(ISpaceTypes.CONFIGURATION_SPACE)) {
            return "configuration";
        } else if (spaceType.equals(ISpaceTypes.ENTERPRISE_SPACE)) {
            return "enterprise";
        } else {
            throw new JspTagException("Error in GetSpaceNavBarTag: Unhandled space type '" + spaceType + "'.");
        }
    }

    private String getIncludePathUsingSuppliedSpace() throws JspTagException {
        if ("personal".equals(_space)) {
            return "personal";
        } else if ("project".equals(_space)) {
            return "project";
        } else if ("business".equals(_space)) {
            return "business";
        } else if ("financial".equals(_space)) {
        	return "financial";            
        } else if ("methodology".equals(_space)) {
            return "methodology";
        } else if ("application".equals(_space)) {
            return "admin";
        } else if ("configuration".equals(_space)) {
            return "configuration";
        } else if ("enterprise".equals(_space)) {
            return "enterprise";
        } else {
            throw new JspTagException("Error in GetSpaceNavBarTag: Unhandled space type '" + _space + "'.");
        }
    }
}
