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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/

package net.project.taglibs.session;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.SessionManager;

/**
 * Returns the JSPRootURL value.
 */
public class GetJSPRootURLTag extends TagSupport {

    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Constructor.
     */
    public GetJSPRootURLTag() {
	super();
    }


    /* -------------------------------  Implementing setters and getters  ------------------------------- */


    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {

	JspWriter out = pageContext.getOut();

	try {
	    out.print( SessionManager.getJSPRootURL() );
	}

	catch (IOException ioe) {
	    throw new JspTagException ("I/O exception: " + ioe);
	}

	return SKIP_BODY;

    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */

}          
