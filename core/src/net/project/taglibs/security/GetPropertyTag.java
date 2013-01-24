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

package net.project.taglibs.security;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.util.Conversion;

public class GetPropertyTag extends TagSupport {

    private static String OBJECT_ID = "objectID";
    private static String ACTION = "action";
    private static String MODULE = "module";

    private String propertyName = null;

    /* -------------------------------  Constructors  ------------------------------- */

    public GetPropertyTag() {
	super();
    }


    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setName (String name) {

	this.propertyName = name;
    }


    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {

	JspWriter out = pageContext.getOut();

	try {
	    out.println( getPropertyValue() );
	}

	catch (IOException ioe) {
	    throw new JspTagException ("I/O exception: " + ioe);
	}
	
	return SKIP_BODY;

    }

    public void release() {

	this.propertyName = null;
	super.release();

    }


    /* -------------------------------  Implementing utility methods  ------------------------------- */

    private String getPropertyValue() {

	SecurityProvider securityProvider = SessionManager.getSecurityProvider();
	String value = null;

	if (this.propertyName.equals(OBJECT_ID))
	    value = securityProvider.getCheckedObjectID();

	else if (this.propertyName.equals(ACTION))
	    value = Conversion.intToString (securityProvider.getCheckedActionID() );

	else if (this.propertyName.equals(MODULE))
	    value = Conversion.intToString ( securityProvider.getCheckedModuleID() );

	return value;

    }

}
