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

import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * ParamTag captures a parameter value which is passed to the enclosing GetTag.
 * These passed to the property provider and are combined into the message.
 *
 * @author  Tim Morrow
 */
public class ParamTag extends TagSupport {

    /** Value attribute */
    private String value;
    
    /** Enclosing GetTag */
    private GetTag parent = null;

    /**
     * Creates a new ParamTag
     */    
    public ParamTag() {
        super();
    }

    //
    // Tag Attribute Setters
    //

    /**
     * Sets the value attribute.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** Returns the value of the "value" attribute
     * @return the value attribute
     */    
    public String getValue() {
        return this.value;
    }
    
    //
    // Override TagSupport methods
    //
    
    /**
     * Ensures tag is within GetTag
     * @throws JspTagException if the param tag is not inside a get tag
     * @return <code>javax.servlet.jsp.tagext.TagSupport.SKIP_BODY</code> always
     */
    public int doStartTag() throws JspTagException { 
        // Locate the enclosing GetTag
        parent = (GetTag) findAncestorWithClass(this, GetTag.class); 
        if (parent == null) {
            throw new JspTagException("Error in ParamTag: Not inside GetTag.");
        }

        return SKIP_BODY;
    }

    /**
     * Adds the attribute 'value' to the parameter values maintained by parent.
     * @throws JspTagException if value attribute is null
     * @return <code>javax.servlet.jsp.tagext.TagSupport.EVAL_PAGE</code> always
     */
    public int doEndTag() throws JspTagException {
        List paramValues = null;
        if (value == null) {
            throw new JspTagException("Error in ParamTag: Required attribute 'value' missing.");
        }
        
        parent.getParamValues().add(this.value);
        clear();
        
        return EVAL_PAGE;
    }

    
    //
    // Utility methods
    //
    
    /**
     * Clears all attributes.
     */
    private void clear() {
        this.value = null;
    }
    
}
