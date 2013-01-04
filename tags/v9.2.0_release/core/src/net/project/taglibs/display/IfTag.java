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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.display;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.base.property.PropertyProvider;


public class IfTag extends BodyTagSupport {

    /** Name of property to evaluate as boolean. */
    private String name = null;
    /** Name of boolean condition to evaluate. */
    private boolean condition = true;

    /* -------------------------------  Constructors  ------------------------------- */

    public IfTag() {
	    super();
    }


    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setName (String name) {
	    this.name = name;
    }

    public void setCondition (boolean condition) {
        this.condition = condition;
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    /**
     * Determines whether to process body.
     * @return <code>EVAL_BODY_TAG</code> if the property specified in the
     * <code>name</code> attribute is defined and is set to true;
     * <code>SKIP_BODY</code> otherwise
     */
    public int doStartTag() throws JspTagException {
        if (isPropertyTrue()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    /**
      * After body content
      * Prints the body content
      */
    public int doAfterBody() throws JspTagException {
        try {
            
            JspWriter out = getBodyContent().getEnclosingWriter();
            out.print(getBodyContent().getString());
        
        } catch (IOException ioe) {
            throw new JspTagException("Error in display If tag: " + ioe);
        
        }
	return (SKIP_BODY);
    }
     

    public int doEndTag() throws JspTagException {
        clear();
	return EVAL_PAGE;
    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */


    private boolean isPropertyTrue() {
        if (name != null) {
            return PropertyProvider.getBoolean (name);
        } else {
            return condition;
        }
    }

    private void clear() {
        name = null;
        condition = true;
    }

}

